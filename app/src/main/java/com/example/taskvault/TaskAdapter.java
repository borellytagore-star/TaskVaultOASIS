package com.example.taskvault;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final OnTaskActionListener listener;

    public interface OnTaskActionListener {

        void onTaskCompleted(
                Task task,
                boolean completed
        );

        void onTaskDeleted(Task task);
    }

    public TaskAdapter(
            List<Task> taskList,
            OnTaskActionListener listener) {

        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_task,
                parent,
                false
        );

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder,
            int position) {

        Task task = taskList.get(position);

        holder.tvTaskName.setText(
                task.getTaskName()
        );

        holder.tvTaskNotes.setText(
                task.getNotes()
        );

        // Prevent old listener from firing
        holder.checkTask.setOnCheckedChangeListener(
                null
        );

        holder.checkTask.setChecked(
                task.isCompleted()
        );

        updateStrikeThrough(
                holder.tvTaskName,
                task.isCompleted()
        );

        // Checkbox
        holder.checkTask.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    task.setCompleted(isChecked);

                    updateStrikeThrough(
                            holder.tvTaskName,
                            isChecked
                    );

                    if (listener != null) {
                        listener.onTaskCompleted(
                                task,
                                isChecked
                        );
                    }
                }
        );

        // Delete
        holder.btnDeleteTask.setOnClickListener(
                v -> {

                    if (listener != null) {
                        listener.onTaskDeleted(task);
                    }
                }
        );
    }

    private void updateStrikeThrough(
            TextView textView,
            boolean completed) {

        if (completed) {

            textView.setPaintFlags(
                    textView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            textView.setPaintFlags(
                    textView.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkTask;
        TextView tvTaskName;
        TextView tvTaskNotes;
        Button btnDeleteTask;

        public TaskViewHolder(
                @NonNull View itemView) {

            super(itemView);

            checkTask =
                    itemView.findViewById(
                            R.id.checkTask
                    );

            tvTaskName =
                    itemView.findViewById(
                            R.id.tvTaskName
                    );

            tvTaskNotes =
                    itemView.findViewById(
                            R.id.tvTaskNotes
                    );

            btnDeleteTask =
                    itemView.findViewById(
                            R.id.btnDeleteTask
                    );
        }
    }
}