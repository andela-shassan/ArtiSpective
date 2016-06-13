package artispective.blogspot.com.ng.artispective.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import artispective.blogspot.com.ng.artispective.models.Users;
import artispective.blogspot.com.ng.artispective.models.events.Events;
import artispective.blogspot.com.ng.artispective.models.model.BigEvent;
import artispective.blogspot.com.ng.artispective.models.model.DeleteEvent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ArtiSpectiveEndpoint {

    @FormUrlEncoded
    @POST(Constants.REGISTER_URL)
    Call<Users> register(@Field("firstName") String firstName, @Field("lastName") String lastName,
                         @Field("password") String password, @Field("emailAddress") String email,
                         @Field("phoneNumber") String phoneNumber, @Field("craft") String craft);

    @FormUrlEncoded
    @POST(Constants.LOGIN_URL)
    Call<Users> login(@Field("emailAddress") String emailAddress, @Field("password") String pass);

    @GET(Constants.USER_PROFILE)
    Call<Users> getUserProfile(@Header("x-access-token") String token, @Path("id") String id);

    @FormUrlEncoded
    @PUT(Constants.UPDATE_ACCOUNT)
    Call<Users> updateAccount(@Field("userId") String userId, @Field("token") String token,
                              @Field("firstName") String _1stName, @Field("lastName") String lName,
                              @Field("password") String pass, @Field("emailAddress") String email,
                              @Field("phoneNumber") String phoneNo, @Field("craft") String craft);

    @FormUrlEncoded
    @POST(Constants.LOGOUT)
    Call<Users> logout(@Field("id") String userId, @Field("token") String token);

    @FormUrlEncoded
    @POST(Constants.DELETE_ACCOUNT)
    Call<Users> deleteAccount(@Field("id") String id);

    @Multipart
    @POST(Constants.ADD_EVENT_URL)
    Call<BigEvent> addEvent(@Part("userId") RequestBody userId, @Part("token") RequestBody token,
                            @Part("title") RequestBody title, @Part("details") RequestBody details,
                            @Part("address") RequestBody address, @Part("date") RequestBody date,
                            @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @Multipart
    @POST(Constants.UPDATE_EVENT)
    Call<BigEvent> updateEvent(@Part("eventId") RequestBody eveId, @Part("userId") RequestBody usId,
                               @Part("token") RequestBody token, @Part("title") RequestBody title,
                               @Part("details") RequestBody dtls, @Part("address") RequestBody adrs,
                               @Part("date") RequestBody date,
                               @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @Multipart
    @POST(Constants.UPDATE_EVENT)
    Call<BigEvent> updateEvent(@Part("eventId") RequestBody eveId, @Part("userId") RequestBody usId,
                               @Part("token") RequestBody token, @Part("title") RequestBody title,
                               @Part("details") RequestBody dtls, @Part("address") RequestBody adrs,
                               @Part("date") RequestBody date);


    @FormUrlEncoded
    @POST(Constants.REMOVE_EVENT)
    Call<DeleteEvent> deleteEvent(@Field("token") String token, @Field("userId") String userId,
                                   @Field("eventId") String eventId);

    @GET(Constants.GET_ALL_EVENTS)
    Call<Events> getAllEvents();

    class Factory {
        private static ArtiSpectiveEndpoint artiSpectiveEndpoint;

        public static ArtiSpectiveEndpoint getArtiSpectiveEndpoint(String url) {
            if (artiSpectiveEndpoint == null) {
                long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
                Cache c = new Cache(new File(ContextProvider.getContext().getCacheDir(), "http")
                        , SIZE_OF_CACHE);
                OkHttpClient.Builder client = new OkHttpClient().newBuilder();
                client.cache(c);
                client.networkInterceptors().add(new CacheControlInterceptor());
                Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();

                Retrofit retrofit = new Retrofit.Builder()
                        .client(client.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl(url)
                        .build();
                artiSpectiveEndpoint = retrofit.create(ArtiSpectiveEndpoint.class);
                return artiSpectiveEndpoint;
            } else {
                return artiSpectiveEndpoint;
            }
        }
    }

}
