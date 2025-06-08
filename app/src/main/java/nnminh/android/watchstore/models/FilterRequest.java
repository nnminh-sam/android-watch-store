package nnminh.android.watchstore.models;

import java.util.List;

public class FilterRequest {
    private List<String> brandIds;
    private Double minPrice;
    private Double maxPrice;
    private String categoryId;

    public FilterRequest(List<String> brandIds, Double minPrice, Double maxPrice, String categoryId) {
        this.brandIds = brandIds;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.categoryId = categoryId;
    }

    public List<String> getBrandIds() {
        return brandIds;
    }

    public void setBrandIds(List<String> brandIds) {
        this.brandIds = brandIds;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}