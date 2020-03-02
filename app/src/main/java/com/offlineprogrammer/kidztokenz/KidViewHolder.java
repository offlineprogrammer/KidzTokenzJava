package com.offlineprogrammer.kidztokenz;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KidViewHolder extends RecyclerView.ViewHolder {
    private TextView kidNameTextView;
    public KidViewHolder(@NonNull View itemView) {
        super(itemView);
        kidNameTextView = (TextView) itemView.findViewById(R.id.kid_name);
    }

    public void bindData(final Kid viewModel) {
        kidNameTextView.setText(viewModel.getKidName());
    }
}
