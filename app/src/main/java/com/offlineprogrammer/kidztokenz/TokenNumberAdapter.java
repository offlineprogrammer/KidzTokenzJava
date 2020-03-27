package com.offlineprogrammer.kidztokenz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TokenNumberAdapter extends RecyclerView.Adapter<TokenNumberViewHolder> {

    private Context mContext;
    private List<TokenNumberData> mTokenNumberList;

    public TokenNumberAdapter(Context mContext, List< TokenNumberData > mTokenNumberList) {
        this.mContext = mContext;
        this.mTokenNumberList = mTokenNumberList;
    }

    @NonNull
    @Override
    public TokenNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.token_itemview, parent, false);
        return new TokenNumberViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TokenNumberViewHolder holder, int position) {

        holder.mImage.setImageResource(mTokenNumberList.get(position).getTokenNumberImage());
        holder.mTitle.setText(mTokenNumberList.get(position).getTokenNumberName());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, KidActivity.class);
                mIntent.putExtra("Image", mTokenNumberList.get(holder.getAdapterPosition()).getTokenNumberImage());
                ((Activity)mContext).setResult(Activity.RESULT_OK,mIntent);
                ((Activity)mContext).finish();
                mContext.startActivity(mIntent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mTokenNumberList.size();
    }
}

class TokenNumberViewHolder extends RecyclerView.ViewHolder {
    ImageView mImage;
    TextView mTitle;
    CardView mCardView;

    TokenNumberViewHolder(View itemView) {
        super(itemView);
         mImage = itemView.findViewById(R.id.ivImage);
         mTitle = itemView.findViewById(R.id.tvTitle);
         mCardView = itemView.findViewById(R.id.tokenCardView);


    }
}
