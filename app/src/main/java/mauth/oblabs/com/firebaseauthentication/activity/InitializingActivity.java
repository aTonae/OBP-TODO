package mauth.oblabs.com.firebaseauthentication.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.adapter.ListContactInviteAdapter;
import mauth.oblabs.com.firebaseauthentication.database.DatabaseHandler;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.service.SyncService;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

public class InitializingActivity extends AppCompatActivity {


    public String TAG = "INIT_ACTIVITY";


    String owner;
    EditText etUsername;

    boolean flagActiveUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initializing);
        etUsername = findViewById(R.id.etUsername);



        owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);


        updateUsername();



        if(checkPermission()){
            readContacts();
        }else{
            requestPermission();
        }


    }

    private void updateUsername() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        db.collection("users").document(owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if(task.isSuccessful()){
//                  String username = (String) task.getResult().get("username");
//                  Helper.showLongToast(InitializingActivity.this , username);
//                }
//            }
//        });


        db.collection("users")
                .whereEqualTo("mobile", owner).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG,"size : "+task.getResult().size());
                            Log.d(TAG,"data : "+task.getResult());
                            if(task.getResult().size()>0) {
                                flagActiveUser = true;
                                QuerySnapshot query = task.getResult();
                                List<DocumentSnapshot> data = query.getDocuments();
                                etUsername.setText((CharSequence) data.get(0).get("username"));
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void readContacts(){


        startService(new Intent(this , SyncService.class));


    }



    private boolean checkPermission(){


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "permission not given");
            return false;
        }else{
            Log.i(TAG, "permission given");
            return true;
        }

    }
    private void requestPermission(){


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                Log.i(TAG, "request permission already given");
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 10);

            } else {
                Log.i(TAG, "requesting permission");
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 10);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    readContacts();
                } else {
                    //not granted

                    Helper.showLongToast(this , "We required contact read permission !");
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private void updateUgcStatus(List<ContactData> contactDatas) {


        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        Set<ContactData> hs = new HashSet<>();
        hs.addAll(contactDatas);
        contactDatas.clear();
        contactDatas.addAll(hs);

        final Map<String, List<ContactData>> contacts = new HashMap<>();
        contacts.put("list",contactDatas );

















                Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                db.collection("contact").document(owner)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        db.collection("contact").document(owner)
                                .set(contacts)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        updateView();;
                                    }
                                });

                    }
                });

            }
        });














    }

    private void updateView() {


        Helper.hideLoading();

        Helper.showToast(this , "Value Updated");

        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("contact").document(owner)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()) {


                        Map<String, Object> data = task.getResult().getData();




                        List<ContactData> myConatct = (List<ContactData>) data.get("list");
                        Helper.showToast(InitializingActivity.this, "Length : " + myConatct.size());
                    }else {
                    }
                }

            }
        });






    }

    public void getStarted(View view) {

        if(etUsername.getText().toString().isEmpty()){
            etUsername.setError("Please choose an username");
            return;
        }

        Helper.showLoading(this , "Initializing...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(flagActiveUser){
            Map<String , Object> username = new HashMap<>();
            username.put("username" , etUsername.getText().toString());
            db.collection("users").document(owner).update(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Helper.hideLoading();
                    if(task.isSuccessful()) {
                        startActivity(new Intent(InitializingActivity.this, ShoppingActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Helper.hideLoading();
                }
            });
        }else {
            Map<String, Object> user = new HashMap<>();

            user.put("mobile", owner);
            user.put("username", etUsername.getText().toString());

            db.collection("users").document(owner).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Helper.hideLoading();
                    if(task.isSuccessful()) {
                        startActivity(new Intent(InitializingActivity.this, ShoppingActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Helper.hideLoading();
                }
            });


        }

    }
}
