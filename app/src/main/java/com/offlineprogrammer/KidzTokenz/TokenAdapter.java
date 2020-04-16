package com.offlineprogrammer.KidzTokenz;

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

public class TokenAdapter extends RecyclerView.Adapter<TokenViewHolder> {

    private Context mContext;
    private List<TokenData> mTokenList;

    public TokenAdapter(Context mContext, List< TokenData > mTokenList) {
        this.mContext = mContext;
        this.mTokenList = mTokenList;
    }

    @NonNull
    @Override
    public TokenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.token_itemview, parent, false);
        return new TokenViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TokenViewHolder holder, int position) {

        holder.mImage.setImageResource(mTokenList.get(position).getTokenImage());
        holder.mTitle.setText(mTokenList.get(position).getTokenName());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, KidActivity.class);
                mIntent.putExtra("Image", mTokenList.get(holder.getAdapterPosition()).getTokenImage());
                ((Activity)mContext).setResult(Activity.RESULT_OK,mIntent);
                ((Activity)mContext).finish();
                mContext.startActivity(mIntent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mTokenList.size();
    }
}

class TokenViewHolder extends RecyclerView.ViewHolder {
    ImageView mImage;
    TextView mTitle;
    CardView mCardView;

    TokenViewHolder(View itemView) {
        super(itemView);
         mImage = itemView.findViewById(R.id.ivImage);
         mTitle = itemView.findViewById(R.id.tvTitle);
         mCardView = itemView.findViewById(R.id.tokenCardView);


    }
}
