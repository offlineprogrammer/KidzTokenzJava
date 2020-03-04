package com.offlineprogrammer.kidztokenz;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KidViewHolder extends RecyclerView.ViewHolder {
    private TextView kidNameTextView;
    private ImageView kidMonsterImageView;
    public KidViewHolder(@NonNull View itemView) {
        super(itemView);
        kidNameTextView = itemView.findViewById(R.id.kid_name);
        kidMonsterImageView = itemView.findViewById(R.id.kid_monster_name);
    }

    public void bindData(final Kid viewModel) {
        kidNameTextView.setText(viewModel.getKidName());
        kidMonsterImageView.setImageResource(viewModel.getMonsterImage());
    }
}
