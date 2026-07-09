# Books App 

**Books App** is a specialized digital library application designed specifically for programmers, developers, and computer science students. It serves as a comprehensive hub for technical knowledge, allowing users to instantly search and access a vast collection of programming books, software engineering references, and tech magazines.

---

## 🚀 About the Project
The application connects directly to the official **Google Books REST API** to fetch real-time data. It provides an organized and seamless user experience for exploring global programming resources, tutorials, and technical guides anytime, anywhere.

---

## ✨ Features
* **Dynamic Search:** Instantly search for any programming or technical book topic.
* **Modern Navigation:** Implements `ViewPager2` and `TabLayout` for fluid swiping between the book list and detail views.
* **Rich Details:** Displays high-quality book covers, comprehensive descriptions, and author names.
* **Background Networking:** Utilizes asynchronous tasks (`AsyncTask`) to handle API requests without freezing the UI.
* **Real-time Notifications:** Sends official Android notifications upon successful data retrieval.
* **Data Refresh:** Features an options menu to update and refresh server data on demand.

---

## 🛠️ How it Works & Architecture
1. **API Integration:** The app constructs a secure HTTP request using a Google Books API key.
2. **JSON Parsing:** Server responses are intercepted in the background, parsed from JSON format, and transformed into structured Java objects (`Book`).
3. **Fragment Communication:** When an item is clicked in the `BookListFragment`, data is passed dynamically via an interface (`OnBookClickListener`) to update the `BookDetailsFragment` and automatically switch tabs.

---

## 💻 Tech Stack
* **Language:** Java
* **Platform:** Android SDK
* **UI Components:** Fragment, RecyclerView, ViewPager2, TabLayout, CoordinatorLayout, AppBarLayout, Material Components
* **Network & Data:** HttpURLConnection, JSON (JSONArray & JSONObject)

---
