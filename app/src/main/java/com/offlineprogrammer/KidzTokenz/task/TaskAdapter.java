package com.offlineprogrammer.KidzTokenz.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;

import java.util.ArrayList;

import timber.log.Timber;

public class TaskAdapter extends RecyclerView.Adapter {
    private ArrayList<KidTask> models = new ArrayList<>();
    private OnTaskListener onTaskListener;
    private static final String TAG = "TaskAdapter";

    public TaskAdapter(@NonNull final ArrayList<KidTask> viewModels, OnTaskListener OnTaskListener) {
        this.models.addAll(viewModels);
        this.onTaskListener = OnTaskListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TaskViewHolder) holder).bindData(models.get(position));

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public ArrayList<KidTask> getAllItems() {
        return models;
    }

    public void updateData(ArrayList<KidTask> viewModels){
        models.clear();
        models.addAll(viewModels);
        notifyDataSetChanged();

    }

    public void delete(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void add(KidTask item, int position){
        models.add(position, item);
        Timber.i("add: " + item.toString());
        notifyItemInserted(position);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.task_itemview;
    }
}
