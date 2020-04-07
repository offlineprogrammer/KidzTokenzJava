package com.offlineprogrammer.kidztokenz.taskTokenz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.kidztokenz.R;

public class TaskTokenzViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView taskNameTextView;
    private ImageView taskImageView;
    OnTaskTokenzListener onTaskTokenzListener;
    public TaskTokenzViewHolder(@NonNull View itemView, OnTaskTokenzListener OnTaskTokenzListener) {
        super(itemView);
        taskNameTextView = itemView.findViewById(R.id.taskName);
        taskImageView = itemView.findViewById(R.id.taskImage);
        this.onTaskTokenzListener = OnTaskTokenzListener;
        itemView.setOnClickListener(this);
    }

    public void bindData(final TaskTokenz viewModel) {
        taskNameTextView.setText(viewModel.getTaskName());
        taskImageView.setImageResource(viewModel.getTaskImage());
    }

    @Override
    public void onClick(View v) {
        onTaskTokenzListener.onTaskTokenzClick(getAdapterPosition());

    }
}

