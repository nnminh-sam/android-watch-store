package nnminh.android.watchstore.models;

import java.util.List;

public class OrderRequest {
    private String full_name;
    private String phone_number;
    private String city;
    private String district;
    private String street;
    private String specific_address;
    private List<String> cart_detail_ids;
    private String payment_method;

    public OrderRequest(String full_name, String phone_number, String city, String district, String street, String specific_address, List<String> cart_detail_ids, String payment_method) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.city = city;
        this.district = district;
        this.street = street;
        this.specific_address = specific_address;
        this.cart_detail_ids = cart_detail_ids;
        this.payment_method = payment_method;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSpecific_address() {
        return specific_address;
    }

    public void setSpecific_address(String specific_address) {
        this.specific_address = specific_address;
    }

    public List<String> getCart_detail_ids() {
        return cart_detail_ids;
    }

    public void setCart_detail_ids(List<String> cart_detail_ids) {
        this.cart_detail_ids = cart_detail_ids;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}