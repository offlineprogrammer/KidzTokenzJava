package com.offlineprogrammer.kidztokenz.taskTokenz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.kidztokenz.R;

import java.util.ArrayList;

public class TaskTokenzAdapter extends RecyclerView.Adapter {
    private ArrayList<TaskTokenz> models = new ArrayList<>();
    private OnTaskTokenzListener onTaskTokenzListener;
    private static final String TAG = "TaskTokenzAdapter";

    public TaskTokenzAdapter(@NonNull final ArrayList<TaskTokenz> viewModels, OnTaskTokenzListener OnTaskTokenzListener) {
        this.models.addAll(viewModels);
        this.onTaskTokenzListener =OnTaskTokenzListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TaskTokenzViewHolder(view, onTaskTokenzListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TaskTokenzViewHolder) holder).bindData(models.get(position));

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public ArrayList<TaskTokenz> getAllItems() {
        return models;
    }

    public void delete(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void add(TaskTokenz item, int position){
        models.add(position, item);
        Log.i(TAG, "add: " + item.toString());
        notifyItemInserted(position);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.tasktokenz_itemview;
    }
}
