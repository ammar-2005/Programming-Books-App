package com.example.final_projectbooksapp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText etSearch; // إضافة تعريف حقل البحث
    private List<Book> pendingBooks;
    private boolean isLoadingPending = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch = view.findViewById(R.id.etSearch); // ربط حقل البحث من الواجهة

        // تفعيل البحث عند الضغط على زر "البحث" في لوحة المفاتيح
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    String query = etSearch.getText().toString().trim();
                    if (!query.isEmpty() && getActivity() instanceof MainActivity) {
                        // استدعاء دالة البحث في MainActivity
                        ((MainActivity) getActivity()).refreshData(query);
                        etSearch.clearFocus(); // إغلاق التركيز عن الحقل بعد البحث
                    }
                    return true;
                }
                return false;
            }
        });

        if (isLoadingPending) showLoading(true);
        if (pendingBooks != null) updateBooks(pendingBooks);

        return view;
    }

    public void showLoading(boolean show) {
        this.isLoadingPending = show;
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void updateBooks(List<Book> books) {
        this.pendingBooks = books;
        this.isLoadingPending = false;
        if (progressBar != null) progressBar.setVisibility(View.GONE);

        if (recyclerView != null && getActivity() instanceof BookAdapter.OnBookClickListener) {
            BookAdapter adapter = new BookAdapter(books, (BookAdapter.OnBookClickListener) getActivity());
            recyclerView.setAdapter(adapter);
        }
    }
}