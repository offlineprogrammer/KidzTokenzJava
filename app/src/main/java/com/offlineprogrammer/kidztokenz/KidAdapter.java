package com.offlineprogrammer.kidztokenz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class KidAdapter extends RecyclerView.Adapter {
    private ArrayList<Kid> models = new ArrayList<>();

    public KidAdapter(@NonNull final ArrayList<Kid> viewModels) {
        this.models.addAll(viewModels);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new KidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((KidViewHolder) holder).bindData(models.get(position));

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void delete(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Kid item, int position){
        models.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.kid_itemview;
    }
}
