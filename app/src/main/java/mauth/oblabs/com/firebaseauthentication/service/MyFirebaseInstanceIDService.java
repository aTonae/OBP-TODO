package raven.oblabs.com.raven.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mauth.oblabs.com.firebaseauthentication.network.ApiRestAdapter;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by android on 18/4/17.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "firebase token";




    @Override
    public void onTokenRefresh() {

        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

      sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.


        SharedPreference preference = new SharedPreference();
        preference.saveValueWithKey(this ,Constants.KEY_TOKEN , token );


        if(preference.getSession(this)) {


            new ApiRestAdapter().updateToken(preference.getValueWithKey(this, Constants.KEY_MOBILE), token).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }


    }
}
