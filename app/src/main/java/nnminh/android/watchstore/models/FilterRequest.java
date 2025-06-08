package nnminh.android.watchstore.models;

import java.util.HashMap;
import java.util.Map;

public class FilterRequest {
    private String name;
    private String brandName;
    private Double min_price;
    private Double max_price;
    private String categoryName;

    public FilterRequest() {
    }

    public FilterRequest(String name, String brandName, Double min_price, Double max_price, String categoryName) {
        this.name = name;
        this.brandName = brandName;
        this.min_price = min_price;
        this.max_price = max_price;
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return categoryName;
    }

    public void setBrandName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double min_price) {
        this.min_price = min_price;
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double max_price) {
        this.max_price = max_price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static Map<String, Object> filterRequestToMap(FilterRequest filter) {
        Map<String, Object> map = new HashMap<>();
        if (filter.getName() != null) map.put("name", filter.getName());
        if (filter.getBrandName() != null) map.put("brand", filter.getBrandName());
        if (filter.getMin_price() != null) map.put("min_price", filter.getMin_price());
        if (filter.getMax_price() != null) map.put("max_price", filter.getMax_price());
        if (filter.getCategoryName() != null) map.put("category", filter.getCategoryName());
        return map;
    }
}