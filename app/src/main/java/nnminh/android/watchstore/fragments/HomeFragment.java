package nnminh.android.watchstore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.ProductAdapter;
import nnminh.android.watchstore.models.ProductResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private TextView textError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar);
        textError = view.findViewById(R.id.textError);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), null);
        recyclerView.setAdapter(productAdapter);

        fetchProducts();
        return view;
    }

    private void fetchProducts() {
        progressBar.setVisibility(View.VISIBLE);
        textError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().getProducts().isEmpty()) {
                    productAdapter.setProductList(response.body().getProducts());
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    textError.setText("No products found");
                    textError.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                textError.setText("Failed to load products");
                textError.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}