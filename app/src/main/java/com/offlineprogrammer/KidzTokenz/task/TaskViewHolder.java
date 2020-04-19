package com.offlineprogrammer.KidzTokenz.task;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;
import com.squareup.picasso.Picasso;

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
        Uri taskTokenImageUri = null;
        taskNameTextView.setText(viewModel.getTaskName());
        if (viewModel.getFirestoreImageUri() == null) {
            taskImageView.setImageResource(viewModel.getTaskImage());
        }
        else {
            taskTokenImageUri = Uri.parse(viewModel.getFirestoreImageUri());
            Picasso.get().load(taskTokenImageUri)
                    .placeholder(R.drawable.bekind)
                    .into(taskImageView);
        }

    }

    @Override
    public void onClick(View v) {
        onTaskListener.onTaskClick(getAdapterPosition());

    }
}

