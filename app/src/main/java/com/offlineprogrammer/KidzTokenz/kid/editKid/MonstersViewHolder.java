package com.offlineprogrammer.KidzTokenz.kid.editKid;

import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;

public class MonstersViewHolder extends RecyclerView.ViewHolder {
    ImageView mImage;
    CardView mCardView;

    MonstersViewHolder(View itemView) {
        super(itemView);
        mImage = itemView.findViewById(R.id.monsterImage);
        mCardView = itemView.findViewById(R.id.monsterCardView);


    }
}
