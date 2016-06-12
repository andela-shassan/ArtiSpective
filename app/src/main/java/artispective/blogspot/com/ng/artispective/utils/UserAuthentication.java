package artispective.blogspot.com.ng.artispective.utils;

import android.util.Log;
import android.widget.Toast;

import artispective.blogspot.com.ng.artispective.interfaces.LoginAuthentication;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.interfaces.RegisterAuthentication;
import artispective.blogspot.com.ng.artispective.models.User;
import artispective.blogspot.com.ng.artispective.models.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAuthentication {

    public static void register(final RegisterAuthentication registerAuthentication, User u) {

        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.REGISTER_URL).register(
                u.getFirstName(), u.getLastName(), u.getPassword(), u.getEmailAddress(),
                u.getPhoneNumber(), u.getCraft()).enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                int code = response.code();
                if (code == 200) {
                    registerAuthentication.onSuccess();
                } else {
                    registerAuthentication.onFailure();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                registerAuthentication.onFailure();
            }
        });

    }

    public static void loginUser(final LoginAuthentication auth, String email, String password) {
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.LOGIN_URL)
                .login(email, password).enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.code() == 200) {
                    Users body = response.body();
                    User user = body.getUser();
                    Helper.setUserData("user_id", user.get_id());
                    Helper.setUserData("user_token", body.getToken());
                    Helper.setUserAdminStatus(user.isAdmin());
                    auth.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                auth.onFailure();
            }
        });

    }

    public static void updateUserProfile(final RegisterAuthentication updateAuth, User user) {

        String token = Helper.getUserData("user_token");
        String userId = Helper.getUserData("user_id");
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.UPDATE_ACCOUNT)
                .updateAccount(userId, token, user.getFirstName(), user.getLastName(),
                user.getPassword(), user.getEmailAddress(), user.getPhoneNumber(),
                user.getCraft()).enqueue(new Callback<Users>() {

                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        int code = response.code();
                        if (code == 200) {
                            showToast("Profile Updated Successfully");
                            updateAuth.onUpdateSuccess();
                        }
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        showToast("Failed to update the user profile");
                    }
                });

    }

    public static void logoutUser(final LogoutAuthentication log, String id, String token) {
        ArtiSpectiveEndpoint.Factory.getArtiSpectiveEndpoint(Constants.LOGOUT).logout(id, token)
                .enqueue(new Callback<Users>() {

            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.code() == 200) {
                    showToast("You are logged out");
                    Helper.setUserData("user_id", "");
                    Helper.setUserData("user_token", "");
                    Helper.setUserAdminStatus(false);
                    log.onSuccess();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                showToast("Something went wrong. try again");
                log.onFailure();
            }
        });
    }

    private static void showToast(String message) {
        Toast.makeText(ContextProvider.getContext(), message, Toast.LENGTH_LONG).show();
    }


}


