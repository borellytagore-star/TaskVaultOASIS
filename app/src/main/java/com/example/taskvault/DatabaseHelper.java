package com.example.taskvault;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TaskVault.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Users table
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "password_hash TEXT NOT NULL" +
                        ")"
        );

        // Tasks table
        db.execSQL(
                "CREATE TABLE tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_id INTEGER NOT NULL, " +
                        "task_name TEXT NOT NULL, " +
                        "notes TEXT, " +
                        "is_completed INTEGER DEFAULT 0, " +
                        "FOREIGN KEY(user_id) REFERENCES users(id)" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }

    // Add a new task
    public long addTask(
            int userId,
            String taskName,
            String notes) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("user_id", userId);
        values.put("task_name", taskName);
        values.put("notes", notes);
        values.put("is_completed", 0);

        return db.insert(
                "tasks",
                null,
                values
        );
    }

    // Get tasks belonging to one user
    public List<Task> getTasksForUser(int userId) {

        List<Task> taskList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "tasks",
                new String[]{
                        "id",
                        "user_id",
                        "task_name",
                        "notes",
                        "is_completed"
                },
                "user_id = ?",
                new String[]{
                        String.valueOf(userId)
                },
                null,
                null,
                "is_completed ASC, id DESC"
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            );

            int taskUserId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("user_id")
            );

            String taskName = cursor.getString(
                    cursor.getColumnIndexOrThrow("task_name")
            );

            String notes = cursor.getString(
                    cursor.getColumnIndexOrThrow("notes")
            );

            boolean completed =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "is_completed"
                            )
                    ) == 1;

            Task task = new Task(
                    id,
                    taskUserId,
                    taskName,
                    notes,
                    completed
            );

            taskList.add(task);
        }

        cursor.close();

        return taskList;
    }

    // Mark task complete/incomplete
    public int updateTaskStatus(
            int taskId,
            boolean completed) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                "is_completed",
                completed ? 1 : 0
        );

        return db.update(
                "tasks",
                values,
                "id = ?",
                new String[]{
                        String.valueOf(taskId)
                }
        );
    }

    // Delete task
    public int deleteTask(int taskId) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "tasks",
                "id = ?",
                new String[]{
                        String.valueOf(taskId)
                }
        );
    }
}