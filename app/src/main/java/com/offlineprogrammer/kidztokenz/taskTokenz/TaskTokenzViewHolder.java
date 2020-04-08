package com.offlineprogrammer.kidztokenz.taskTokenz;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
        final ColorMatrix grayscaleMatrix = new ColorMatrix();
        grayscaleMatrix.setSaturation(0);
        taskTokenzImageView.setImageResource(viewModel.getTaskTokenzImage());
        if(!viewModel.getIsRewarded()) {
            taskTokenzImageView.setColorFilter(new ColorMatrixColorFilter(grayscaleMatrix));
        }
    }

    @Override
    public void onClick(View v) {
        if (taskTokenzImageView.getColorFilter() == null){
            final ColorMatrix grayscaleMatrix = new ColorMatrix();
            grayscaleMatrix.setSaturation(0);
            taskTokenzImageView.setColorFilter(new ColorMatrixColorFilter(grayscaleMatrix));

        } else {
            taskTokenzImageView.setColorFilter(null);
        }

        onTaskTokenzListener.onTaskTokenzClick(getAdapterPosition());

    }
}

