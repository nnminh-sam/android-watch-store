package nnminh.android.watchstore.models;

import java.util.List;

public class ProductDetail extends Product {
    private String description;
    private List<String> images;

    public String getDescription() {
        return description;
    }

    public List<String> getImages() {
        return images;
    }
}