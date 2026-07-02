package com.example.final_projectbooksapp;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String description;
    private String thumbnailUrl;

    public Book(String id, String title, String authors, String description, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
}