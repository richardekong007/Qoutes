package com.richydave.quotes.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.richydave.quotes.R;
import com.richydave.quotes.UpdateProfileListener;
import com.richydave.quotes.model.database.LocalQuote;
import com.richydave.quotes.model.database.UserCredential;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class LocalQuoteAdapter extends RecyclerView.Adapter<LocalQuoteAdapter.LocalQuotesViewHolder> {

    private List<LocalQuote> quotes;

    private ViewQuoteClickListener viewQuoteClickListener;

    public LocalQuoteAdapter(List<LocalQuote> quotes) {
        this.quotes = quotes;
    }

    @NonNull
    @Override
    public LocalQuotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote_record, parent, false);
        return new LocalQuotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalQuotesViewHolder holder, int position) {

        final LocalQuote localQuote = quotes.get(position);
        setAvatar(holder.itemView.getContext(), holder.avatar, localQuote.getPhotoUrl());
        holder.authorName.setText(localQuote.getAuthorName());
        holder.viewQuote.setOnClickListener(view -> {
            int id = position + 1;
            viewQuoteClickListener.onViewQuoteClick(id, localQuote);
        });
    }

    public void setViewQuoteClickListener(ViewQuoteClickListener viewQuoteClickListener) {
        this.viewQuoteClickListener = viewQuoteClickListener;
    }

    private void setAvatar(Context context, ImageView image, String imagePath) {
        Glide.with(context)
                .load(imagePath)
                .into(image);
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public class LocalQuotesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author_avatar)
        CircleImageView avatar;

        @BindView(R.id.author_name)
        TextView authorName;

        @BindView(R.id.view_quote)
        AppCompatButton viewQuote;

        private LocalQuotesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ViewQuoteClickListener {

        void onViewQuoteClick(int id, LocalQuote quotes);
    }

}