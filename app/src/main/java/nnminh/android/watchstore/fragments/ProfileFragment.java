package nnminh.android.watchstore.fragments;

import android.net.Uri;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.activities.ChangePasswordActivity;
import nnminh.android.watchstore.activities.OrderDetailActivity;
import nnminh.android.watchstore.adapters.OrderAdapter;
import nnminh.android.watchstore.models.*;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProfileFragment extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 1001;
    private Uri selectedImageUri;

    private ImageView imageAvatar;
    private Button buttonChangePassword, buttonSave;
    private TextView textEmail, textError, textOrdersTitle;
    private EditText editFirstName, editLastName, editPhone, editDob;
    private Spinner spinnerGender;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewOrders;
    private ImageButton buttonEditAvatar, buttonLogout;

    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();

    private Calendar dobCalendar = Calendar.getInstance();
    private UserProfile user;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageAvatar = view.findViewById(R.id.imageAvatar);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonSave = view.findViewById(R.id.buttonSave);
        textEmail = view.findViewById(R.id.textEmail);
        editFirstName = view.findViewById(R.id.editFirstName);
        editLastName = view.findViewById(R.id.editLastName);
        editPhone = view.findViewById(R.id.editPhone);
        editDob = view.findViewById(R.id.editDob);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        progressBar = view.findViewById(R.id.progressBar);
        textError = view.findViewById(R.id.textError);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        textOrdersTitle = view.findViewById(R.id.textOrdersTitle);

        buttonEditAvatar = view.findViewById(R.id.buttonEditAvatar);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Avatar"), REQUEST_PICK_IMAGE);
        });

        buttonLogout.setOnClickListener(v -> {
            // Remove tokens and user info
            TokenManager.getInstance(requireContext()).deleteToken();
            TokenManager.getInstance(requireContext()).deleteUser();
            Toast.makeText(getContext(), "Logged out.", Toast.LENGTH_SHORT).show();
            // Return user to login screen
            requireActivity().finishAffinity();
            startActivity(new Intent(getContext(), nnminh.android.watchstore.activities.LoginActivity.class));
        });

        // Gender spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Male", "Female", "Other"}
        );
        spinnerGender.setAdapter(genderAdapter);

        // Orders RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
//        orderAdapter = new OrderAdapter(orderList);
//        recyclerViewOrders.setAdapter(orderAdapter);
        orderAdapter = new OrderAdapter(orderList, order -> {
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
        });
        recyclerViewOrders.setAdapter(orderAdapter);

        buttonChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
        });

        editDob.setOnClickListener(v -> showDatePicker());

        buttonSave.setOnClickListener(v -> saveProfile());

        loadProfileAndOrders();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageAvatar.setImageURI(selectedImageUri);
            uploadAvatar(selectedImageUri);
        }
    }

    private void uploadAvatar(Uri imageUri) {
        if (imageUri == null) return;
        showLoading(true);
        File file = FileUtils.getFileFromUri(requireContext(), imageUri);
        if (file == null) {
            showLoading(false);
            Toast.makeText(getContext(), "Could not read image file.", Toast.LENGTH_SHORT).show();
            return;
        }
        String mimeType = requireContext().getContentResolver().getType(imageUri);
        if (mimeType == null) mimeType = "image/png";
        RequestBody reqFile = RequestBody.create(file, MediaType.parse(mimeType));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        String token = TokenManager.getInstance(requireContext()).getToken();
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        apiService.updateAvatar(token, body).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Success: Now fetch the updated user profile
                    fetchUserProfileAfterAvatar();
                    Toast.makeText(getContext(), "Avatar updated!", Toast.LENGTH_SHORT).show();
                } else {
                    showLoading(false);
                    Toast.makeText(getContext(), "Failed to update avatar.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // New helper method to fetch profile
    private void fetchUserProfileAfterAvatar() {
        String token = TokenManager.getInstance(requireContext()).getToken();
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.getUserProfile(token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    bindUserProfile(response.body().getUser());
                } else {
                    Toast.makeText(getContext(), "Failed to reload profile.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileAndOrders() {
        showLoading(true);
        textError.setVisibility(View.GONE);
        String token = TokenManager.getInstance(getContext()).getToken();
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        // Fetch user profile
        apiService.getUserProfile(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body().getUser();
                    bindUserProfile(user);
                    loadOrders();
                } else {
                    showLoading(false);
                    showError("Failed to load profile.");
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showLoading(false);
                showError("Failed to load profile.");
            }
        });
    }

    private void loadOrders() {
        String token = TokenManager.getInstance(getContext()).getToken();
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.getOrders(token).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getOrders() != null) {
                    orderList = response.body().getOrders();
                    orderAdapter.setOrders(orderList);
                    textOrdersTitle.setVisibility(orderList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    textOrdersTitle.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showLoading(false);
                textOrdersTitle.setVisibility(View.GONE);
            }
        });
    }

    private void bindUserProfile(UserProfile user) {
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(imageAvatar);
        } else {
            imageAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
        textEmail.setText("Email: " + user.getEmail());
        editFirstName.setText(user.getfirst_name());
        editLastName.setText(user.getlast_name());
        editPhone.setText(user.getphone_number());
        // * Gender
        if ("male".equalsIgnoreCase(user.getGender())) spinnerGender.setSelection(0);
        else if ("female".equalsIgnoreCase(user.getGender())) spinnerGender.setSelection(1);
        else spinnerGender.setSelection(2);
        // * Date of birth
        if (!TextUtils.isEmpty(user.getDate_of_birth().toString())) {
            editDob.setText(user.getDate_of_birth().toString());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = sdf.parse(user.getDate_of_birth().toString());
                if (date != null) dobCalendar.setTime(date);
            } catch (Exception ignore) {}
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    dobCalendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    editDob.setText(sdf.format(dobCalendar.getTime()));
                },
                dobCalendar.get(Calendar.YEAR),
                dobCalendar.get(Calendar.MONTH),
                dobCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        textError.setVisibility(View.VISIBLE);
        textError.setText(msg);
    }

    private void saveProfile() {
        if (user == null) return;
        showLoading(true);

        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().toUpperCase();
        UpdateProfileRequest requestBody = new UpdateProfileRequest(
                firstName, lastName, gender, phone, dob);

        String token = TokenManager.getInstance(getContext()).getToken();
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        apiService.updateProfile(token, requestBody).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    bindUserProfile(response.body().getUser());
                } else {
                    showError("Failed to save profile.");
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showLoading(false);
                showError("Failed to save profile.");
            }
        });
    }
}