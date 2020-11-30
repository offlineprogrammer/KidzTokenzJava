package com.offlineprogrammer.KidzTokenz.task;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.GlideApp;
import com.offlineprogrammer.KidzTokenz.R;


public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Context mContext;
    private final TextView taskNameTextView;
    private final ImageView taskImageView;
    OnTaskListener onTaskListener;

    public TaskViewHolder(@NonNull View itemView, OnTaskListener OnTaskListener) {
        super(itemView);
        mContext = itemView.getContext();
        taskNameTextView = itemView.findViewById(R.id.taskName);
        taskImageView = itemView.findViewById(R.id.taskImage);
        this.onTaskListener = OnTaskListener;
        itemView.setOnClickListener(this);
        taskImageView.setOnClickListener(this);

    }

    public void bindData(final KidTask viewModel) {
        Uri taskTokenImageUri;
        taskNameTextView.setText(viewModel.getTaskName());
        if (viewModel.getFirestoreImageUri() == null) {
            taskImageView.setImageResource(viewModel.getTaskImage());
        }
        else {
            taskTokenImageUri = Uri.parse(viewModel.getFirestoreImageUri());
            GlideApp.with(mContext)
                    .load(taskTokenImageUri)
                    .placeholder(R.drawable.bekind)
                    .into(taskImageView);
        }

    }

    @Override
    public void onClick(View v) {
        onTaskListener.onTaskClick(getAdapterPosition());

    }
}

