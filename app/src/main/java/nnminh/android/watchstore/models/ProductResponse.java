package nnminh.android.watchstore.models;

import java.util.List;

public class ProductResponse extends BaseResponse {
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
