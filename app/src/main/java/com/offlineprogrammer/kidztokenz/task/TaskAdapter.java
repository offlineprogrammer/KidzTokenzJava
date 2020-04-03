package com.offlineprogrammer.kidztokenz.task;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.kidztokenz.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter {
    private ArrayList<Task> models = new ArrayList<>();
    private OnTaskListener onTaskListener;
    private static final String TAG = "TaskAdapter";

    public TaskAdapter(@NonNull final ArrayList<Task> viewModels, OnTaskListener OnTaskListener) {
        this.models.addAll(viewModels);
        this.onTaskListener =OnTaskListener;
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

    public ArrayList<Task> getAllItems() {
        return models;
    }

    public void delete(int position) {
        models.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Task item, int position){
        models.add(position, item);
        Log.i(TAG, "add: " + item.toString());
        notifyItemInserted(position);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.task_itemview;
    }
}
