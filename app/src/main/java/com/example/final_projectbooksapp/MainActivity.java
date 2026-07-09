package com.example.final_projectbooksapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private BookListFragment bookListFragment;
    private BookDetailsFragment bookDetailsFragment;
    private static final String CHANNEL_ID = "book_api_channel";
    private FetchBooksTask currentTask = null;

    private static final String GOOGLE_BOOKS_API_KEY = "AIzaSy BwLj8mGGLyWSlcxohWOwNTmxcCWoGplF8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bookListFragment = new BookListFragment();
        bookDetailsFragment = new BookDetailsFragment();

        Fragment[] fragments = new Fragment[]{bookListFragment, bookDetailsFragment};

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Book List" : "Details");
        }).attach();

        createNotificationChannel();
        checkNotificationPermission();
        viewPager.post(() -> refreshData("Android Development"));
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    public void refreshData() {
        refreshData("Android Development");
    }

    public void refreshData(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) return;
        if (currentTask != null) {
            currentTask.cancel(true);
        }

        try {
            String encodedQuery = URLEncoder.encode(searchQuery.trim(), "UTF-8");
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + encodedQuery + "&key=" + GOOGLE_BOOKS_API_KEY;

            currentTask = new FetchBooksTask();
            currentTask.execute(url);
        } catch (Exception e) {
            Log.e("SEARCH_ERROR", "Encoding error");
        }
    }

    @Override
    public void onBookClick(Book book) {
        if (bookDetailsFragment != null) {
            bookDetailsFragment.displayBookDetails(book);
            viewPager.setCurrentItem(1, true);
        }
    }

    // --- بداية كلاس جلب البيانات ---
    private class FetchBooksTask extends AsyncTask<String, Void, List<Book>> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            if (bookListFragment != null) bookListFragment.showLoading(true);
        }

        @Override
        protected List<Book> doInBackground(String... urls) {
            List<Book> resultList = new ArrayList<>();
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    errorMsg = "Server Error: " + responseCode;
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                JSONObject jsonResponse = new JSONObject(sb.toString());
                if (jsonResponse.has("items")) {
                    JSONArray items = jsonResponse.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject itemJson = items.getJSONObject(i);
                        JSONObject volumeInfo = itemJson.getJSONObject("volumeInfo");

                        String id = itemJson.optString("id", "");
                        String title = volumeInfo.optString("title", "No Title");

                        String authors = "Unknown Author";
                        if (volumeInfo.has("authors")) {
                            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                            if (authorsArray.length() > 0) authors = authorsArray.getString(0);
                        }

                        String desc = volumeInfo.optString("description", "No description available.");

                        String thumb = "";
                        if (volumeInfo.has("imageLinks")) {
                            thumb = volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "")
                                    .replace("http://", "https://");
                        }
                        resultList.add(new Book(id, title, authors, desc, thumb));
                    }
                }
            } catch (Exception e) {
                errorMsg = "Please check internet connection";
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            currentTask = null;
            if (bookListFragment != null) bookListFragment.showLoading(false);

            if (books != null) {
                if (books.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No books found", Toast.LENGTH_SHORT).show();
                } else {
                    if (bookListFragment != null) bookListFragment.updateBooks(books);
                    if (bookDetailsFragment != null) bookDetailsFragment.displayBookDetails(books.get(0));
                    sendNotification("Update Successful", "Found " + books.size() + " books.");
                }
            } else {
                if (!isCancelled()) {
                    Toast.makeText(MainActivity.this, errorMsg != null ? errorMsg : "Fetch Failed", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            currentTask = null;
            if (bookListFragment != null) bookListFragment.showLoading(false);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "API Updates", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                manager.notify(1, builder.build());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Update Data")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(0, 2, 1, "About Books App")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();
            refreshData();
            return true;
        } else if (item.getItemId() == 2) {
            showProjectIdeaDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProjectIdeaDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About Books App");
        builder.setMessage("Books App is a specialized digital library designed for programmers and tech enthusiasts.\n\n" +
                "The app provides instant access to thousands of technical books via Google Books API, allowing you to search, view details, and stay updated with the latest in software development.");
        builder.setPositiveButton("Got it!", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}