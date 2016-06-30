package artispective.blogspot.com.ng.artispective.utils;

import android.util.Log;
import android.widget.Toast;

import artispective.blogspot.com.ng.artispective.interfaces.LoginAuthentication;
import artispective.blogspot.com.ng.artispective.interfaces.LogoutAuthentication;
import artispective.blogspot.com.ng.artispective.interfaces.RegisterAuthentication;
import artispective.blogspot.com.ng.artispective.models.User;
import artispective.blogspot.com.ng.artispective.models.Users;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserAuthentication {

    private static Users aUser;

    public static void register(final RegisterAuthentication authentication, User user) {
        Observable<Users> observable = Endpoint.RxFactory.getEndpoint().rxRegister(
                user.getFirstName(), user.getLastName(), user.getPassword(),
                user.getEmailAddress(), user.getPhoneNumber(), user.getCraft());

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>() {
                    @Override
                    public void onCompleted() {
                        authentication.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        authentication.onFailure();
                    }

                    @Override
                    public void onNext(Users users) {

                    }
                });
    }

    public static void loginUser(final LoginAuthentication auth, String email, String password) {
        Observable<Users> observable = Endpoint.RxFactory.getEndpoint().rxLogin(email, password);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>(){
                    @Override
                    public void onCompleted() {
                        auth.onSuccess();
                        saveUserData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        auth.onFailure();
                    }

                    @Override
                    public void onNext(Users users) {
                        aUser = users;
                    }
                });
    }

    private static void saveUserData() {
        User user = aUser.getUser();
        Helper.setUserData("user_id", user.get_id());
        Helper.setUserData("user_token", aUser.getToken());
        Helper.setUserAdminStatus(user.isAdmin());
    }

    public static void updateUserProfile(final RegisterAuthentication auth, User user) {
        String token = Helper.getUserData("user_token");
        String userId = Helper.getUserData("user_id");
        Observable<Users> observable = Endpoint.RxFactory.getEndpoint().rxUpdateAccount(userId,
                token, user.getFirstName(), user.getLastName(), user.getPassword(),
                user.getEmailAddress(), user.getPhoneNumber(), user.getCraft());
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>(){
                    @Override
                    public void onNext(Users users) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("Failed to update the user profile");
                        Log.e("semiu profile update", e.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        showToast("Profile Updated Successfully");
                        auth.onUpdateSuccess();
                    }
                });
    }

    public static void logoutUser(final LogoutAuthentication log, String id, String token) {
        Observable<Users> observable = Endpoint.RxFactory.getEndpoint().rxLogout(id, token);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>() {
                    @Override
                    public void onCompleted() {
                        showToast("You are logged out");
                        Helper.setUserData("user_id", "");
                        Helper.setUserData("user_token", "");
                        Helper.setUserAdminStatus(false);
                        log.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("Something went wrong. try again");
                        log.onFailure();
                    }

                    @Override
                    public void onNext(Users users) {

                    }
                });
    }

    private static void showToast(String message) {
        Toast.makeText(ContextProvider.getContext(), message, Toast.LENGTH_LONG).show();
    }


}


