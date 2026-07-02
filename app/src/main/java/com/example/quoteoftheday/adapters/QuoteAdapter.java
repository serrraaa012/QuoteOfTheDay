package com.example.quoteoftheday.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quoteoftheday.R;
import com.example.quoteoftheday.models.Quote;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {
    private List<Quote> quotes;
    private OnRemoveFavoriteListener listener;

    public interface OnRemoveFavoriteListener {
        void onRemove(Quote quote);
    }

    public QuoteAdapter(List<Quote> quotes, OnRemoveFavoriteListener listener) {
        this.quotes = quotes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.quoteText.setText("\"" + quote.getText() + "\"");
        holder.quoteAuthor.setText("- " + quote.getAuthor());

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(quote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotes != null ? quotes.size() : 0;
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView quoteText, quoteAuthor;
        Button removeButton;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteText = itemView.findViewById(R.id.quoteText);
            quoteAuthor = itemView.findViewById(R.id.quoteAuthor);
            removeButton = itemView.findViewById(R.id.removeFavoriteButton);
        }
    }
}