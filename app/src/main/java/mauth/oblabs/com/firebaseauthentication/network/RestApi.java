package mauth.oblabs.com.firebaseauthentication.network;


import java.util.List;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kmagrawal on 03/03/18.
 */

public interface RestApi {



    //update FCM token
    @FormUrlEncoded
    @POST("notification/updatetoken")
    Call<String> updateToken(@Field("contact") String contact, @Field("token") String token);



    //send single todos
    @FormUrlEncoded
    @POST("notification/createSingleTodo")
    Call<String> sendSingleTodo(@Field("from") String from, @Field("to") String to,@Field("message") String message);

    //send single todos
    @FormUrlEncoded
    @POST("notification/createGroupTodo")
    Call<String> createGroupTodo(@Field("from") String from, @Field("to[]") List<String> to,@Field("message") String message);

//    sendGroupTodo

    //send single todos
    @FormUrlEncoded
    @POST("notification/sendGroupTodo")
    Call<String> sendGroupTodo(@Field("from") String from, @Field("to[]") List<String> to,@Field("message") String message);

}


