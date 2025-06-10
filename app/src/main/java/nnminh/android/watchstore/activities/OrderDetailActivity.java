package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.OrderItemAdapter;
import nnminh.android.watchstore.models.Order;
import nnminh.android.watchstore.models.SingleOrderResponse;
import nnminh.android.watchstore.models.OrderItem;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import nnminh.android.watchstore.auth.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView textOrderId, textOrderDate, textOrderTotal, textOrderStatus, textOrderAddress, textError;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private OrderItemAdapter itemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        textOrderId = findViewById(R.id.textOrderId);
        textOrderDate = findViewById(R.id.textOrderDate);
        textOrderTotal = findViewById(R.id.textOrderTotal);
        textOrderStatus = findViewById(R.id.textOrderStatus);
        textOrderAddress = findViewById(R.id.textOrderAddress);
        textError = findViewById(R.id.textError);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        progressBar = findViewById(R.id.progressBar);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new OrderItemAdapter(new ArrayList<>());
        recyclerViewItems.setAdapter(itemAdapter);

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            textError.setVisibility(View.VISIBLE);
            textError.setText("No order ID found!");
            return;
        }

        loadOrderDetail(orderId);
    }

    private void loadOrderDetail(String orderId) {
        showLoading(true);
        String token = "Bearer " + TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getOrderById(token, orderId).enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getOrder() != null) {
                    bindOrderDetail(response.body().getOrder());
                } else {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Failed to load order detail.");
                }
            }

            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {
                showLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText("Network error.");
            }
        });
    }

    private void bindOrderDetail(Order order) {
        textOrderId.setText("Order #" + order.getId());

        // Format date
        String dateStr = order.getCreated_at().toString();
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date date = iso.parse(order.getCreated_at().toString());
            if (date != null) {
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                dateStr = out.format(date);
            }
        } catch (Exception ignored) {}
        textOrderDate.setText(dateStr);

        // Format total
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        textOrderTotal.setText("Total: " + nf.format(order.getTotal()) + " ₫");

        textOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Unknown");

        if (order.getDelivery_information().getSpecific_address() != null)
            textOrderAddress.setText(order.getDelivery_information().getSpecific_address());
        else
            textOrderAddress.setText("No address info");

        if (order.getDetails() != null)
            itemAdapter.setItems(order.getDetails());
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}