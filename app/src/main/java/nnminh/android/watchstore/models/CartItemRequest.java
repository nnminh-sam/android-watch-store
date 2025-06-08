package nnminh.android.watchstore.models;

public class CartItemRequest {
    private String productId;
    private int quantity;

    public CartItemRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}