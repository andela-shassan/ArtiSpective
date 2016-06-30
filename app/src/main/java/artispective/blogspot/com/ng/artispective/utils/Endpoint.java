package artispective.blogspot.com.ng.artispective.utils;

import java.io.File;

import artispective.blogspot.com.ng.artispective.models.Users;
import artispective.blogspot.com.ng.artispective.models.article.ArticleResponse;
import artispective.blogspot.com.ng.artispective.models.article.GetArticles;
import artispective.blogspot.com.ng.artispective.models.events.Events;
import artispective.blogspot.com.ng.artispective.models.model.BigEvent;
import artispective.blogspot.com.ng.artispective.models.model.DeleteEvent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
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
import rx.Observable;
import rx.schedulers.Schedulers;


public interface Endpoint {

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
    Call<BigEvent> addEvent(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                            @Part("title") RequestBody title, @Part("details") RequestBody details,
                            @Part("address") RequestBody address, @Part("date") RequestBody date,
                            @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @Multipart
    @POST(Constants.ADD_EVENT_URL)
    Call<BigEvent> addEvent2(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                            @Part("title") RequestBody title, @Part("details") RequestBody details,
                            @Part("address") RequestBody address, @Part("date") RequestBody date,
                            @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image,
                            @Part("eventImage\"; filename=\"pp2.png\" ") RequestBody image2);

    @Multipart
    @POST(Constants.ADD_EVENT_URL)
    Call<BigEvent> addEvent3(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                            @Part("title") RequestBody title, @Part("details") RequestBody details,
                            @Part("address") RequestBody address, @Part("date") RequestBody date,
                            @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image,
                            @Part("eventImage\"; filename=\"pp2.png\" ") RequestBody image2,
                             @Part("eventImage\"; filename=\"pp3.png\" ") RequestBody image3);

    @Multipart
    @POST(Constants.UPDATE_EVENT)
    Call<BigEvent> updateEvent(@Part("eventId") RequestBody eveId, @Part("userId") RequestBody usId,
                               @Header("x-access-token") RequestBody token, @Part("title") RequestBody title,
                               @Part("details") RequestBody dtls, @Part("address") RequestBody adrs,
                               @Part("date") RequestBody date,
                               @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @FormUrlEncoded
    @POST(Constants.UPDATE_EVENT)
    Call<BigEvent> updateEvent(@Header("x-access-token") String token,
                               @Field("eventId") String eveId, @Field("userId") String usId,
                               @Field("title") String title, @Field("details") String dtls,
                               @Field("address") String adrs, @Field("date") String date);


    @FormUrlEncoded
    @POST(Constants.REMOVE_EVENT)
    Call<DeleteEvent> deleteEvent(@Field("token") String token, @Field("userId") String userId,
                                   @Field("eventId") String eventId);

    @GET(Constants.GET_ALL_EVENTS)
    Call<Events> getAllEvents();

    @Multipart
    @POST(Constants.ADD_POST_URL)
    Call<ArticleResponse> addArticle(@Header("x-access-token") String token,
                                     @Part("userId") RequestBody userId,
                                     @Part("heading") RequestBody heading,
                                     @Part("body") RequestBody body,
                                     @Part("postImage\"; " +
                                             "filename=\"postImage.png\" ") RequestBody image);

    @Multipart
    @PUT(Constants.UPDATE_POST_URL)
    Call<ArticleResponse> updateArticle(@Header("x-access-token") String token,
                                 @Part("userId") RequestBody userId,
                                 @Part("postId") RequestBody postId,
                                 @Part("heading") RequestBody heading,
                                 @Part("body") RequestBody body,
                                 @Part("postImage\"; " +
                                         "filename=\"postImage.png\" ") RequestBody image);

    @FormUrlEncoded
    @PUT(Constants.UPDATE_POST_URL)
    Call<ArticleResponse> updateArticle(@Header("x-access-token") String token,
                                 @Field("userId") String userId,
                                 @Field("postId") String postId,
                                 @Field("heading") String heading,
                                 @Field("body") String body);

    @GET(Constants.GET_ALL_POST_URL)
    Call<GetArticles> getAllArticles();

    @FormUrlEncoded
    @POST(Constants.ADD_COMMENT_URL)
    Call<ArticleResponse> addComment(@Field("token") String token, @Field("userId") String userId,
                                  @Field("postId") String eventId, @Field("comment") String coment);

    @FormUrlEncoded
    @POST(Constants.REMOVE_POST_URL)
    Call<ArticleResponse> removePost(@Field("token") String token, @Field("userId") String userId,
                                     @Field("postId") String postId);


    class Factory {
        private static Endpoint endpoint;

        public static Endpoint getEndpoint(String url) {
            if (endpoint == null) {
                long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
                Cache c = new Cache(new File(ContextProvider.getContext().getCacheDir(), "http")
                        , SIZE_OF_CACHE);
                OkHttpClient.Builder client = new OkHttpClient().newBuilder();
                client.cache(c);
                client.networkInterceptors().add(new CacheControlInterceptor());
                RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(
                        Schedulers.io()
                );

                Retrofit retrofit = new Retrofit.Builder()
                        .client(client.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(url)
                        .build();
                endpoint = retrofit.create(Endpoint.class);
                return endpoint;
            } else {
                return endpoint;
            }
        }
    }




////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                //
//                                                                                                //
//                          Reactive Android (rx) Implementation                                  //
//                                                                                                //
//                                                                                                //
////////////////////////////////////////////////////////////////////////////////////////////////////




    @FormUrlEncoded
    @POST("register")
    Observable<Users> rxRegister(@Field("firstName") String firstName, @Field("lastName") String lastName,
                               @Field("password") String password, @Field("emailAddress") String email,
                               @Field("phoneNumber") String phoneNumber, @Field("craft") String craft);

    @FormUrlEncoded
    @POST("login")
    Observable<Users> rxLogin(@Field("emailAddress") String emailAddress, @Field("password") String pass);

    @GET("getUserProfile/{id}")
    Observable<Users> rxGetUserProfile(@Header("x-access-token") String token, @Path("id") String id);

    @FormUrlEncoded
    @PUT("updateUserProfile")
    Observable<Users> rxUpdateAccount(@Field("userId") String userId, @Field("token") String token,
                              @Field("firstName") String _1stName, @Field("lastName") String lName,
                              @Field("password") String pass, @Field("emailAddress") String email,
                              @Field("phoneNumber") String phoneNo, @Field("craft") String craft);

    @FormUrlEncoded
    @POST("logout")
    Observable<Users> rxLogout(@Field("id") String userId, @Field("token") String token);

    @FormUrlEncoded
    @POST("deleteUserAccount")
    Observable<Users> rxDeleteAccount(@Field("id") String id);

    @Multipart
    @POST("addEvent")
    Observable<BigEvent> rxAddEvent(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                            @Part("title") RequestBody title, @Part("details") RequestBody details,
                            @Part("address") RequestBody address, @Part("date") RequestBody date,
                            @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @Multipart
    @POST("addEvent")
    Observable<BigEvent> rxAddEvent2(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                             @Part("title") RequestBody title, @Part("details") RequestBody details,
                             @Part("address") RequestBody address, @Part("date") RequestBody date,
                             @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image,
                             @Part("eventImage\"; filename=\"pp2.png\" ") RequestBody image2);

    @Multipart
    @POST("addEvent")
    Observable<BigEvent> rxAddEvent3(@Header("x-access-token") String token, @Part("userId") RequestBody userId,
                             @Part("title") RequestBody title, @Part("details") RequestBody details,
                             @Part("address") RequestBody address, @Part("date") RequestBody date,
                             @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image,
                             @Part("eventImage\"; filename=\"pp2.png\" ") RequestBody image2,
                             @Part("eventImage\"; filename=\"pp3.png\" ") RequestBody image3);

    @Multipart
    @POST("updateEvent")
    Observable<BigEvent> rxUpdateEvent(@Part("eventId") RequestBody eveId, @Part("userId") RequestBody usId,
                               @Header("x-access-token") RequestBody token, @Part("title") RequestBody title,
                               @Part("details") RequestBody dtls, @Part("address") RequestBody adrs,
                               @Part("date") RequestBody date,
                               @Part("eventImage\"; filename=\"pp.png\" ") RequestBody image);

    @FormUrlEncoded
    @POST("updateEvent")
    Observable<BigEvent> rxUpdateEvent(@Header("x-access-token") String token,
                               @Field("eventId") String eveId, @Field("userId") String usId,
                               @Field("title") String title, @Field("details") String dtls,
                               @Field("address") String adrs, @Field("date") String date);


    @FormUrlEncoded
    @POST("removeEvent")
    Observable<DeleteEvent> rxDeleteEvent(@Field("token") String token, @Field("userId") String userId,
                                  @Field("eventId") String eventId);

    @GET("getAllEvents")
    Observable<Events> rxGetAllEvents();

    @Multipart
    @POST("addPost")
    Observable<ArticleResponse> rxAddArticle(@Header("x-access-token") String token,
                                     @Part("userId") RequestBody userId,
                                     @Part("heading") RequestBody heading,
                                     @Part("body") RequestBody body,
                                     @Part("postImage\"; " +
                                             "filename=\"postImage.png\" ") RequestBody image);

    @Multipart
    @PUT("updatePost")
    Observable<ArticleResponse> rxUpdateArticle(@Header("x-access-token") String token,
                                        @Part("userId") RequestBody userId,
                                        @Part("postId") RequestBody postId,
                                        @Part("heading") RequestBody heading,
                                        @Part("body") RequestBody body,
                                        @Part("postImage\"; " +
                                                "filename=\"postImage.png\" ") RequestBody image);

    @FormUrlEncoded
    @PUT("updatePost")
    Observable<ArticleResponse> rxUpdateArticle(@Header("x-access-token") String token,
                                        @Field("userId") String userId,
                                        @Field("postId") String postId,
                                        @Field("heading") String heading,
                                        @Field("body") String body);

    @GET("getAllPosts")
    Observable<GetArticles> rxGetAllArticles();

    @FormUrlEncoded
    @POST("addComment")
    Observable<ArticleResponse> rxAddComment(@Field("token") String token, @Field("userId") String userId,
                                     @Field("postId") String eventId, @Field("comment") String coment);

    @FormUrlEncoded
    @POST("removePost")
    Observable<ArticleResponse> rxRemovePost(@Field("token") String token, @Field("userId") String userId,
                                     @Field("postId") String postId);


    class RxFactory {
        private static Endpoint endpoint;
        private static String BASE_URL ="http://artispective.herokuapp.com/api/v1/";

        public static Endpoint getEndpoint() {
            if (endpoint == null) {
                long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
                Cache c = new Cache(new File(ContextProvider.getContext().getCacheDir(), "http")
                        , SIZE_OF_CACHE);
                OkHttpClient.Builder client = new OkHttpClient().newBuilder();
                client.cache(c);
                client.networkInterceptors().add(new CacheControlInterceptor());
                RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory
                        .createWithScheduler(Schedulers.newThread());

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(rxAdapter)
                        .build();
                endpoint = retrofit.create(Endpoint.class);
                return endpoint;
            } else {
                return endpoint;
            }
        }
    }

}
