package com.offlineprogrammer.KidzTokenz.kid;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.OnKidListener;
import com.offlineprogrammer.KidzTokenz.R;

public class KidViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView kidNameTextView;
    private ImageView kidMonsterImageView;
    OnKidListener onKidListener;
    public KidViewHolder(@NonNull View itemView, OnKidListener onKidListener) {
        super(itemView);
        kidNameTextView = itemView.findViewById(R.id.kid_name);
        kidMonsterImageView = itemView.findViewById(R.id.kid_monster_name);
        this.onKidListener = onKidListener;
        itemView.setOnClickListener(this);
    }

    public void bindData(final Kid viewModel) {
        kidNameTextView.setText(viewModel.getKidName());
        kidMonsterImageView.setImageResource(viewModel.getMonsterImage());
    }

    @Override
    public void onClick(View v) {
        onKidListener.onKidClick(getAdapterPosition());

    }
}

