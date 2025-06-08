package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.Product;
import nnminh.android.watchstore.models.ProductDetailResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageButton buttonBack, buttonAddToCart;
    private ProgressBar progressBar;
    private TextView textName, textBrand, textPrice, textStock, textSold, textCategories, textDesc, textError;
    private RecyclerView recyclerViewImages;
    private ImagesAdapter imagesAdapter;

    private String productId;
    private Product product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            finish();
            return;
        }

        buttonBack = findViewById(R.id.buttonBack);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        progressBar = findViewById(R.id.progressBar);
        textName = findViewById(R.id.textName);
        textBrand = findViewById(R.id.textBrand);
        textPrice = findViewById(R.id.textPrice);
        textStock = findViewById(R.id.textStock);
        textSold = findViewById(R.id.textSold);
        textCategories = findViewById(R.id.textCategories);
        textDesc = findViewById(R.id.textDesc);
        textError = findViewById(R.id.textError);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);

        imagesAdapter = new ImagesAdapter(new ArrayList<>());
        recyclerViewImages.setAdapter(imagesAdapter);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        buttonBack.setOnClickListener(v -> finish());

        buttonAddToCart.setOnClickListener(v -> {
            if (product != null) {
                // TODO: Implement add-to-cart API
                Toast.makeText(this, "Added to cart (stub)", Toast.LENGTH_SHORT).show();
            }
        });

        loadProductDetail();
    }

    private void loadProductDetail() {
        showLoading(true);
        textError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getProductById(productId).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body().getProduct();
                    bindProduct(product);
                } else {
                    showError("Failed to load product.");
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error.");
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        textError.setVisibility(View.VISIBLE);
        textError.setText(msg);
    }

    private void bindProduct(Product p) {
        textName.setText(p.getName());
        textBrand.setText(p.getBrand() != null ? p.getBrand().getName() : "");
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        textPrice.setText("Price: " + formatter.format(p.getPrice()) + " ₫");
        textStock.setText(p.getStock() > 0 ? "In stock: " + p.getStock() : "Out of stock");
        textSold.setText("Sold: " + p.getSold());

        if (p.getCategories() != null && !p.getCategories().isEmpty()) {
            List<String> catNames = new ArrayList<>();
            for (var cat : p.getCategories()) catNames.add(cat.getName());
            textCategories.setText("Categories: " + TextUtils.join(", ", catNames));
        } else {
            textCategories.setText("Categories: None");
        }

        textDesc.setText(p.getDescription() != null ? p.getDescription() : "No description.");

        imagesAdapter.setImages(p.getAssets() != null ? p.getAssets() : new ArrayList<>());
    }

    // Simple inner RecyclerView Adapter for image carousel
    static class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageVH> {
        private List<String> images;
        ImagesAdapter(List<String> images) { this.images = images; }
        void setImages(List<String> imgs) { this.images = imgs; notifyDataSetChanged(); }

        @NonNull
        @Override
        public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new RecyclerView.LayoutParams(600, ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(8, 8, 8, 8);
            return new ImageVH(iv);
        }
        @Override
        public void onBindViewHolder(@NonNull ImageVH holder, int pos) {
            Glide.with(holder.itemView.getContext())
                    .load(images.get(pos))
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into((ImageView) holder.itemView);
        }
        @Override public int getItemCount() { return images != null ? images.size() : 0; }
        static class ImageVH extends RecyclerView.ViewHolder {
            ImageVH(@NonNull View itemView) { super(itemView); }
        }
    }
}