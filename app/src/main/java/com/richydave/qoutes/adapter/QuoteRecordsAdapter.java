package com.richydave.qoutes.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.richydave.qoutes.Constant;
import com.richydave.qoutes.R;
import com.richydave.qoutes.fragments.ViewQuoteFragment;
import com.richydave.qoutes.model.Quote;
import com.richydave.qoutes.util.FragmentUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class QuoteRecordsAdapter extends RecyclerView.Adapter<QuoteRecordsAdapter.QuoteRecordViewHolder> {

    private List<Quote> quoteRecords;

    private ViewQuoteClickListener viewQuoteClickListener;

    public QuoteRecordsAdapter(List<Quote> quoteRecords) {
        this.quoteRecords = quoteRecords;
    }

    @NonNull
    @Override
    public QuoteRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote_record, parent, false);
        ButterKnife.bind(this, view);
        return new QuoteRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteRecordViewHolder holder, int position) {

        final Quote quoteRecord = quoteRecords.get(position);
        Glide.with(holder.itemView.getContext())
                .load(quoteRecord.getPhotoUrl())
                .into(holder.avatar);

        holder.authorName.setText(quoteRecord.getAuthor());
        holder.viewQuote.setOnClickListener(click -> {
            Bundle args = passDataToFragment(quoteRecord);
            viewQuoteClickListener.onViewQuoteClick(args);
        });
    }

    public void setViewQuoteClickListener(ViewQuoteClickListener viewQuoteClickListener) {
        this.viewQuoteClickListener = viewQuoteClickListener;
    }

    private Bundle passDataToFragment(Quote quoteRecord) {

        Bundle args = new Bundle();
        args.putString(Constant.PHOTO_URI, quoteRecord.getPhotoUrl());
        args.putString(Constant.AUTHOR, quoteRecord.getAuthor());
        args.putString(Constant.STATEMENT, quoteRecord.getStatement());
        args.putString(Constant.BIRTH_PLACE, quoteRecord.getBirthPlace());

        return args;
    }

    @Override
    public int getItemCount() {
        return quoteRecords != null ? quoteRecords.size() : 0;
    }

    public class QuoteRecordViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_avatar)
        CircleImageView avatar;

        @BindView(R.id.author_name)
        TextView authorName;

        @BindView(R.id.view_quote)
        AppCompatButton viewQuote;

        public QuoteRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ViewQuoteClickListener {

        void onViewQuoteClick(Bundle quoteDetial);
    }
}
