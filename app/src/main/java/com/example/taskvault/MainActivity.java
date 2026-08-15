package com.example.taskvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button btnAddTask;

    private RecyclerView recyclerTasks;
    private LinearLayout emptyState;

    private DatabaseHelper databaseHelper;

    private List<Task> taskList;
    private TaskAdapter taskAdapter;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // IMPORTANT: Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Connect XML views
        btnLogout =
                findViewById(R.id.btnLogout);

        btnAddTask =
                findViewById(R.id.btnAddTask);

        recyclerTasks =
                findViewById(R.id.recyclerTasks);

        emptyState =
                findViewById(R.id.emptyState);
        // Add Task
        btnLogout.setOnClickListener(v -> {

            Toast.makeText(
                    MainActivity.this,
                    "Logout button clicked",
                    Toast.LENGTH_SHORT
            ).show();

            logoutUser();
        });
        // Get logged-in user
        SharedPreferences preferences =
                getSharedPreferences(
                        "TaskVaultSession",
                        MODE_PRIVATE
                );

        userId = preferences.getInt(
                "user_id",
                -1
        );

        // No session
        if (userId == -1) {

            goToLogin();

            return;
        }

        // RecyclerView
        recyclerTasks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        taskList = new ArrayList<>();

        taskAdapter = new TaskAdapter(
                taskList,
                new TaskAdapter.OnTaskActionListener() {

                    @Override
                    public void onTaskCompleted(
                            Task task,
                            boolean completed) {

                        databaseHelper.updateTaskStatus(
                                task.getId(),
                                completed
                        );
                    }

                    @Override
                    public void onTaskDeleted(
                            Task task) {

                        showDeleteConfirmation(task);
                    }
                }
        );

        recyclerTasks.setAdapter(
                taskAdapter
        );

        // Load existing tasks
        loadTasks();

        // Add Task
        btnAddTask.setOnClickListener(
                v -> showAddTaskDialog()
        );

        // Logout
        btnLogout.setOnClickListener(
                v -> logoutUser()
        );
    }

    // Load tasks from SQLite
    private void loadTasks() {

        taskList.clear();

        List<Task> tasks =
                databaseHelper.getTasksForUser(
                        userId
                );

        taskList.addAll(tasks);

        taskAdapter.notifyDataSetChanged();

        // Empty state
        if (taskList.isEmpty()) {

            emptyState.setVisibility(
                    View.VISIBLE
            );

            recyclerTasks.setVisibility(
                    View.GONE
            );

        } else {

            emptyState.setVisibility(
                    View.GONE
            );

            recyclerTasks.setVisibility(
                    View.VISIBLE
            );
        }
    }

    // Add Task Dialog
    private void showAddTaskDialog() {

        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(
                Window.FEATURE_NO_TITLE
        );

        dialog.setContentView(
                R.layout.dialog_add_task
        );

        EditText etTaskName =
                dialog.findViewById(
                        R.id.etTaskName
                );

        EditText etTaskNotes =
                dialog.findViewById(
                        R.id.etTaskNotes
                );

        Button btnCancel =
                dialog.findViewById(
                        R.id.btnCancel
                );

        Button btnAdd =
                dialog.findViewById(
                        R.id.btnAdd
                );

        // Cancel
        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        // Add
        btnAdd.setOnClickListener(
                v -> {

                    String taskName =
                            etTaskName
                                    .getText()
                                    .toString()
                                    .trim();

                    String notes =
                            etTaskNotes
                                    .getText()
                                    .toString()
                                    .trim();

                    // Task name required
                    if (taskName.isEmpty()) {

                        etTaskName.setError(
                                "Enter a task name"
                        );

                        etTaskName.requestFocus();

                        return;
                    }

                    long result =
                            databaseHelper.addTask(
                                    userId,
                                    taskName,
                                    notes
                            );

                    if (result != -1) {

                        Toast.makeText(
                                MainActivity.this,
                                "Task added successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();

                        loadTasks();

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Failed to add task",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        dialog.show();
    }

    // Delete confirmation
    private void showDeleteConfirmation(
            Task task) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage(
                        "Are you sure you want to delete this task?"
                )
                .setNegativeButton(
                        "CANCEL",
                        null
                )
                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> {

                            int result =
                                    databaseHelper.deleteTask(
                                            task.getId()
                                    );

                            if (result > 0) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Task deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadTasks();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Failed to delete task",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // Logout
    private void logoutUser() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "TaskVaultSession",
                        MODE_PRIVATE
                );

        preferences.edit()
                .clear()
                .apply();

        Intent intent =
                new Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    private void goToLogin() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}