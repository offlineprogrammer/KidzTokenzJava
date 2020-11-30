package com.offlineprogrammer.KidzTokenz.kid;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.OnKidListener;
import com.offlineprogrammer.KidzTokenz.R;

public class KidViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView kidNameTextView;
    private final ImageView kidMonsterImageView;
    OnKidListener onKidListener;
    private final Context mContext;

    public KidViewHolder(@NonNull View itemView, OnKidListener onKidListener) {
        super(itemView);
        mContext = itemView.getContext();
        kidNameTextView = itemView.findViewById(R.id.kid_name);
        kidMonsterImageView = itemView.findViewById(R.id.kid_monster_name);
        this.onKidListener = onKidListener;
        itemView.setOnClickListener(this);
        kidMonsterImageView.setOnClickListener(this);
    }

    public void bindData(final Kid viewModel) {
        kidNameTextView.setText(viewModel.getKidName());
        kidMonsterImageView.setImageResource(viewModel.getMonsterImage());

        kidMonsterImageView.setImageResource(mContext.getResources().getIdentifier(viewModel.getMonsterImageResourceName(), "drawable",
                mContext.getPackageName()));

    }

    @Override
    public void onClick(View v) {
        onKidListener.onKidClick(getAdapterPosition());

    }
}

