package com.offlineprogrammer.KidzTokenz.task;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView taskNameTextView;
    private ImageView taskImageView;
    OnTaskListener onTaskListener;
    public TaskViewHolder(@NonNull View itemView, OnTaskListener OnTaskListener) {
        super(itemView);
        taskNameTextView = itemView.findViewById(R.id.taskName);
        taskImageView = itemView.findViewById(R.id.taskImage);
        this.onTaskListener = OnTaskListener;
        itemView.setOnClickListener(this);
    }

    public void bindData(final KidTask viewModel) {
        taskNameTextView.setText(viewModel.getTaskName());
        taskImageView.setImageResource(viewModel.getTaskImage());
    }

    @Override
    public void onClick(View v) {
        onTaskListener.onTaskClick(getAdapterPosition());

    }
}

