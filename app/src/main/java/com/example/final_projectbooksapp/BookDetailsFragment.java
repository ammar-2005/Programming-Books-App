package com.example.final_projectbooksapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class BookDetailsFragment extends Fragment {

    private ImageView imgCover;
    private TextView txtTitle, txtAuthor, txtDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);
        imgCover = view.findViewById(R.id.imgDetailCover);
        txtTitle = view.findViewById(R.id.txtDetailTitle);
        txtAuthor = view.findViewById(R.id.txtDetailAuthor);
        txtDescription = view.findViewById(R.id.txtDetailDescription);
        return view;
    }

    public void displayBookDetails(Book book) {
        if (book != null && txtTitle != null) {
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthors());
            txtDescription.setText(book.getDescription());

            Glide.with(this)
                    .load(book.getThumbnailUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgCover);
        }
    }
}