package nnminh.android.watchstore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.Brand;
import nnminh.android.watchstore.models.Category;
import nnminh.android.watchstore.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textName.setText(product.getName());

        // Brand
        Brand brand = product.getBrand();
        holder.textBrand.setText(brand != null ? brand.getName() : "Unknown Brand");

        // Categories
        String categoriesText = "";
        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            List<String> categoryNames = new ArrayList<>();
            for (Category c : product.getCategories()) {
                if (c != null && c.getName() != null)
                    categoryNames.add(c.getName());
            }
            categoriesText = android.text.TextUtils.join(", ", categoryNames);
        }
        holder.textCategories.setText(categoriesText.isEmpty() ? "No category" : categoriesText);

        // Price
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        String priceStr = formatter.format(product.getPrice()) + " ₫";
        holder.textPrice.setText(priceStr);

        // Sold
        holder.textSold.setText(product.getSold() + " sold");

        // Availability
        if (product.getStock() > 0) {
            holder.textAvailability.setText("In stock");
            holder.textAvailability.setBackgroundResource(R.drawable.bg_availability_badge);
        } else {
            holder.textAvailability.setText("Out of stock");
            holder.textAvailability.setBackgroundResource(R.drawable.bg_availability_badge); // You may create a red version if you prefer
            holder.textAvailability.setBackgroundColor(Color.RED); // Or set background tint for out of stock
        }

        // Product image (first from assets)
        String imageUrl = null;
        if (product.getAssets() != null && !product.getAssets().isEmpty()) {
            imageUrl = product.getAssets().get(0);
        }
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_watch_placeholder)
                .into(holder.imageProduct);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void setProductList(List<Product> products) {
        this.productList = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName, textBrand, textCategories, textPrice, textSold, textAvailability;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textName);
            textBrand = itemView.findViewById(R.id.textBrand);
            textCategories = itemView.findViewById(R.id.textCategories);
            textPrice = itemView.findViewById(R.id.textPrice);
            textSold = itemView.findViewById(R.id.textSold);
            textAvailability = itemView.findViewById(R.id.textAvailability);
        }
    }
}