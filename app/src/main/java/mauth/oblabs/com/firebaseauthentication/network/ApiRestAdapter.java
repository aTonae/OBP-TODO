/**
 * Created by ajaythakur on 6/16/15.
 */
package raven.oblabs.com.raven.network;

import android.content.Context;
import android.util.Log;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiRestAdapter {

    protected final String TAG = getClass().getSimpleName();
    protected Retrofit mRestAdapter;
    protected RestApi mApi;

    static String BASE_URL ="http://obleamlabs.com/todos/index.php/";


    public ApiRestAdapter() {













        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();






        mRestAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)

               .addConverterFactory(GsonConverterFactory.create())
                .client(client)

                .build();
        mApi = mRestAdapter.create(RestApi.class); // create the interface

        Log.d(TAG, "BASE -- created");
    }



    public Call<String> updateToken(String contact , String token){
       return mApi.updateToken(contact , token);
    }


    public Call<String> sendSingleTodo(String from , String to , String message){
        return mApi.sendSingleTodo(from , to, message);
    }


    public Call<String> sendGroupTodo(String from , List<String> to , String message){
        return mApi.sendGroupTodo(from , to, message);
    }

    public Call<String> createGroupTodo(String from , List<String> to , String message){
        return mApi.createGroupTodo(from , to, message);
    }








}


