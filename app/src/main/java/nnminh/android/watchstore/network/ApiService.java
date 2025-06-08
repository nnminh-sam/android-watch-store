package nnminh.android.watchstore.network;

import java.util.List;
import java.util.Map;

import nnminh.android.watchstore.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ──────── Auth ────────

    @POST("auth/sign-up")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/sign-in")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("users/me")
    Call<UserProfile> getUserProfile(@Header("Authorization") String token);

    @PUT("users/me")
    Call<UserProfile> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);


    // ──────── Products ────────

    @GET("products")
    Call<ProductResponse> getProducts(@QueryMap Map<String, Object> request);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String productId);


    // ──────── Cart ────────

    @GET("cart")
    Call<CartResponse> getCart(@Header("Authorization") String token);

    @POST("cart")
    Call<ApiMessage> addToCart(@Header("Authorization") String token, @Body CartItemRequest request);

    @PUT("cart")
    Call<ApiMessage> updateCart(@Header("Authorization") String token, @Body CartItemRequest request);

    @DELETE("cart/{productId}")
    Call<ApiMessage> removeFromCart(@Header("Authorization") String token, @Path("productId") String productId);


    // ──────── Orders ────────

    @GET("orders")
    Call<List<Order>> getOrders(@Header("Authorization") String token);

    @GET("orders/{id}")
    Call<Order> getOrderById(@Header("Authorization") String token, @Path("id") String orderId);

    @POST("orders")
    Call<OrderResponse> placeOrder(@Header("Authorization") String token, @Body OrderRequest request);


    // ──────── Favorites ────────

    @GET("favorites")
    Call<List<Product>> getFavorites(@Header("Authorization") String token);

    @POST("favorites")
    Call<ApiMessage> addToFavorites(@Header("Authorization") String token, @Body FavoriteRequest request);

    @DELETE("favorites/{productId}")
    Call<ApiMessage> removeFromFavorites(@Header("Authorization") String token, @Path("productId") String productId);


    // ──────── Categories & Brands ────────

    @GET("categories")
    Call<CategoryResponse> getCategories();

    @GET("brands")
    Call<BrandResponse> getBrands();
}