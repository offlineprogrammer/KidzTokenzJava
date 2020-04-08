package com.offlineprogrammer.kidztokenz.taskTokenz;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.kidztokenz.R;

public class TaskTokenzViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView taskTokenzImageView;
    OnTaskTokenzListener onTaskTokenzListener;
    public TaskTokenzViewHolder(@NonNull View itemView, OnTaskTokenzListener OnTaskTokenzListener) {
        super(itemView);
        taskTokenzImageView = itemView.findViewById(R.id.taskTokenzImage);
        this.onTaskTokenzListener = OnTaskTokenzListener;
        itemView.setOnClickListener(this);
    }

    public void bindData(final TaskTokenz viewModel) {
        taskTokenzImageView.setImageResource(viewModel.getTaskTokenzImage());
    }

    @Override
    public void onClick(View v) {
        onTaskTokenzListener.onTaskTokenzClick(getAdapterPosition());

    }
}

