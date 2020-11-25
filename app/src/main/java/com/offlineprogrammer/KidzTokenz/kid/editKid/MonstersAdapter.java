package com.offlineprogrammer.KidzTokenz.kid.editKid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.KidActivity;
import com.offlineprogrammer.KidzTokenz.R;

import java.util.List;

public class MonstersAdapter extends RecyclerView.Adapter<MonstersViewHolder> {

    private final Context mContext;
    private final List<Monster> monsterList;

    public MonstersAdapter(Context mContext, List<Monster> monsterList) {
        this.mContext = mContext;
        this.monsterList = monsterList;
    }

    @NonNull
    @Override
    public MonstersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monster_itemview, parent, false);
        return new MonstersViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MonstersViewHolder holder, int position) {

        holder.mImage.setImageResource(mContext.getResources().getIdentifier(monsterList.get(position).getMonsterImageResourceName(), "drawable",
                mContext.getPackageName()));


        holder.mCardView.setOnClickListener(view -> {

            ((KidActivity) mContext).setMonsterImage(monsterList.get(holder.getAdapterPosition()).getMonsterImageResourceName());
            ((KidActivity) mContext).getSupportFragmentManager().popBackStack();

        });

    }

    @Override
    public int getItemCount() {
        return monsterList.size();
    }
}
