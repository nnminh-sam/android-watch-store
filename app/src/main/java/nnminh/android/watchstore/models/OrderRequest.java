package nnminh.android.watchstore.models;

import java.util.List;

public class OrderRequest {
    private List<String> productIds;
    private List<Integer> quantities;
    private String deliveryAddress;
    private String paymentMethod;

    public OrderRequest(List<String> productIds, List<Integer> quantities, String deliveryAddress, String paymentMethod) {
        this.productIds = productIds;
        this.quantities = quantities;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
    }
}