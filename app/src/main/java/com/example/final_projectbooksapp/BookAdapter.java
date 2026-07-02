package com.example.final_projectbooksapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.txtTitle.setText(book.getTitle());
        holder.txtAuthor.setText(book.getAuthors());

        Glide.with(holder.itemView.getContext())
                .load(book.getThumbnailUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        holder.btnDetails.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() { return bookList.size(); }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtAuthor, btnDetails;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgBookCover);
            txtTitle = itemView.findViewById(R.id.txtBookTitle);
            txtAuthor = itemView.findViewById(R.id.txtBookAuthor);
            btnDetails = itemView.findViewById(R.id.btnDetailsAction);
        }
    }
}