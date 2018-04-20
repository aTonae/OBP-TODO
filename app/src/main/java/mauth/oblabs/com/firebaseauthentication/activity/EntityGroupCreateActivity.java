package mauth.oblabs.com.firebaseauthentication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.adapter.ContactAdapter;
import mauth.oblabs.com.firebaseauthentication.adapter.SelectedContactAdapter;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.service.SyncService;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

public class EntityGroupCreateActivity extends AppCompatActivity {


    public String TAG = "INIT_ACTIVITY";


    String owner;
    EditText etGroupName;

    RecyclerView rvParticipant;

    TextView tvCount;
    List<ContactData> partners;

    SelectedContactAdapter contactAdapter;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Group");

        etGroupName = findViewById(R.id.etUsername);

        rvParticipant = findViewById(R.id.rvPartner);
        tvCount = findViewById(R.id.tvPartnerCount);






        partners = (List) getIntent().getSerializableExtra("list");

        tvCount.setText("Total Participants : "+partners.size());


        contactAdapter = new SelectedContactAdapter(partners);






        owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);


        StaggeredGridLayoutManager gridLayoutManager  = new StaggeredGridLayoutManager(4 , StaggeredGridLayoutManager.VERTICAL);
        rvParticipant.setLayoutManager(gridLayoutManager);

        rvParticipant.setAdapter(contactAdapter);




        updateUsername();




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


//        db.collection("users")
//                .whereEqualTo("mobile", owner).limit(1)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            Log.d(TAG,"size : "+task.getResult().size());
//                            Log.d(TAG,"data : "+task.getResult());
//                            if(task.getResult().size()>0) {
//                                flagActiveUser = true;
//                                QuerySnapshot query = task.getResult();
//                                List<DocumentSnapshot> data = query.getDocuments();
//                                etUsername.setText((CharSequence) data.get(0).get("username"));
//                            }
//
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
    }



    public void fabClicked(View view) {

        if(etGroupName.getText().toString().isEmpty()){
            etGroupName.setError("Not a valid name !");
            return;
        }


        createGroup();

    }

    private void createGroup() {

        Helper.showLoading(this , "Creating Group...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String , Object> entity = new HashMap<>();
        entity.put("owner" , owner);
        entity.put("name" , etGroupName.getText().toString());
        entity.put("type" , "group");


        Map<String , Object> entityPartner = new HashMap<>();
        entityPartner.put(owner , true);

        for (ContactData data : partners){

//            Map<String , Object> partnerData = (Map<String, Object>) data;

//            String key = (String) partnerData.get("mobile");
            entityPartner.put(data.getMobile() , true);

        }


        entity.put("partner" , entityPartner);
        entity.put("timestamp" ,System.currentTimeMillis());
        entity.put("updated" , System.currentTimeMillis());
        entity.put("lastDo" , "Group Created");
        entity.put("isActive" , true);
















        db.collection("entity").add(entity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Helper.hideLoading();
                if(task.isSuccessful()){

                    Bundle bundle = new Bundle();
                    bundle.putString("key" , task.getResult().getId());
                    bundle.putString("name" , etGroupName.getText().toString());
                    bundle.putString("type" , "group");

                    Intent intent = new Intent(EntityGroupCreateActivity.this , EntityActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);



                    Intent intentFinish = new Intent();
                    intentFinish.putExtra("status", true);

                    setResult(Constants.CODE_FINISH_PRE_ACTIVITY, intentFinish);
                    finish();//finishing activity


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
