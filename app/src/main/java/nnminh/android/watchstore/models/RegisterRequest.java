package nnminh.android.watchstore.models;

public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public RegisterRequest(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}