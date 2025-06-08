package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.RegisterRequest;
import nnminh.android.watchstore.models.AuthResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, confirmPasswordInput, firstNameInput, lastNameInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmPasswordInput = findViewById(R.id.editTextConfirmPassword);
        firstNameInput = findViewById(R.id.editTextFirstName);
        lastNameInput = findViewById(R.id.editTextLastName);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            RegisterRequest registerRequest = new RegisterRequest(email, password, firstName, lastName);
            Call<AuthResponse> call = apiService.register(registerRequest);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        TokenManager.getInstance(getApplicationContext()).saveToken(response.body().getData().getAccess_token());
                        TokenManager.getInstance(getApplicationContext()).saveUser(response.body().getData().getUser());
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}