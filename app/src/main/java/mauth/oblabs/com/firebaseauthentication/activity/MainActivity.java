package mauth.oblabs.com.firebaseauthentication.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import mauth.oblabs.com.firebaseauthentication.BuildConfig;
import mauth.oblabs.com.firebaseauthentication.R;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String verificationId;
    EditText etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMobile = findViewById(R.id.etMobile);
        etMobile.addTextChangedListener(textWatcher());

//        if(new SharedPreference().getSession(this)){
//            startActivity(new Intent(this , ShoppingActivity.class));
//            finish();
//        }

        mAuth = new FirebaseAuth(FirebaseApp.initializeApp(this));

        SharedPreference preference = new SharedPreference();

        int buildCode = preference.getIntWithKey(this , Constants.KEY_BUILD_CODE);


        if(BuildConfig.VERSION_CODE!=buildCode){
            preference.clearSharedPreference(this);
            mAuth.signOut();
        }

        if(mAuth.getCurrentUser()!=null) {
            String mobile = mAuth.getCurrentUser().getPhoneNumber();

            if (mobile != null && !mobile.isEmpty() && preference.getSession(this)) {



                startActivity(new Intent(MainActivity.this, ShoppingActivity.class));
                finish();

            }

        }





    }

    private TextWatcher textWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==10){
                    sendMessage(s.toString());
                }

            }
        };
    }

    private void sendMessage(String mobile) {
        Helper.showLoading(this , "Verifying Phone Number...");
        String mobileNumber = "+91"+mobile;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Helper.hideLoading();

//                        Toast.makeText(MainActivity.this , "Verified : "+phoneAuthCredential.getSmsCode()  , Toast.LENGTH_LONG).show();

//                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, phoneAuthCredential.getSmsCode());
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Helper.hideLoading();
                        Toast.makeText(MainActivity.this , "Failed : "+e.getMessage() , Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

//                        Toast.makeText(MainActivity.this , "Code Sent : "+s , Toast.LENGTH_LONG).show();
                        verificationId = s;


                    }
                });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Helper.showLoading(this , "Creating User...");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Helper.hideLoading();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information




                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(MainActivity.this , "Success" , Toast.LENGTH_LONG).show();

                            SharedPreference preference = new SharedPreference();
                            preference.saveValueWithKey(MainActivity.this , Constants.KEY_MOBILE ,etMobile.getText().toString().trim() );
                            preference.saveIntWithKey(MainActivity.this , Constants.KEY_BUILD_CODE , BuildConfig.VERSION_CODE);
                            updateToken();
                            startActivity(new Intent(MainActivity.this , InitializingActivity.class));
                            finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            Toast.makeText(MainActivity.this , "Failed : "+task.getException() , Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.hideLoading();
            }
        });
    }

    private void updateToken() {



        SharedPreference preference = new SharedPreference();
        new ApiRestAdapter().updateToken(preference.getValueWithKey(this, Constants.KEY_MOBILE), FirebaseInstanceId.getInstance().getToken()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
