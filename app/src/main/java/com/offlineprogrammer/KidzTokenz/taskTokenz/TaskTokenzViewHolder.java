package com.offlineprogrammer.KidzTokenz.taskTokenz;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;

public class TaskTokenzViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context mContext;

    private ImageView taskTokenzImageView;
    OnTaskTokenzListener onTaskTokenzListener;
    public TaskTokenzViewHolder(@NonNull View itemView, OnTaskTokenzListener OnTaskTokenzListener) {
        super(itemView);
        mContext = itemView.getContext();
        taskTokenzImageView = itemView.findViewById(R.id.taskTokenzImage);
        this.onTaskTokenzListener = OnTaskTokenzListener;
        itemView.setOnClickListener(this);
    }

    public void bindData(final TaskTokenz viewModel) {
        final ColorMatrix grayscaleMatrix = new ColorMatrix();
        TypedValue typedValue = new TypedValue();
        grayscaleMatrix.setSaturation(0);
        taskTokenzImageView.setImageResource(viewModel.getTaskTokenzImage());
        if(!viewModel.getRewarded()) {
            taskTokenzImageView.setColorFilter(new ColorMatrixColorFilter(grayscaleMatrix));
            taskTokenzImageView.setBackgroundColor(mContext.getColor(R.color.colorPrimaryDisabled));
        } else {
            taskTokenzImageView.setBackgroundColor(mContext.getColor(R.color.colorPrimaryDisabled));
            mContext.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
            if (typedValue.resourceId != 0) {
                taskTokenzImageView.setBackgroundResource(typedValue.resourceId);
            } else {
                taskTokenzImageView.setBackgroundColor(typedValue.data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        TypedValue typedValue = new TypedValue();
        if (taskTokenzImageView.getColorFilter() == null){
            final ColorMatrix grayscaleMatrix = new ColorMatrix();
            grayscaleMatrix.setSaturation(0);
            taskTokenzImageView.setColorFilter(new ColorMatrixColorFilter(grayscaleMatrix));
            taskTokenzImageView.setBackgroundColor(mContext.getColor(R.color.colorPrimaryDisabled));
        } else {
            taskTokenzImageView.setColorFilter(null);
            mContext.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
            if (typedValue.resourceId != 0) {
                taskTokenzImageView.setBackgroundResource(typedValue.resourceId);
            } else {
                taskTokenzImageView.setBackgroundColor(typedValue.data);
            }
        }

        onTaskTokenzListener.onTaskTokenzClick(getAdapterPosition());

    }
}

