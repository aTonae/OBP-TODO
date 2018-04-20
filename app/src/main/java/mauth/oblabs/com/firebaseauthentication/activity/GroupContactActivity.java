package mauth.oblabs.com.firebaseauthentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.adapter.ContactAdapter;
import mauth.oblabs.com.firebaseauthentication.adapter.GroupContactAdapter;
import mauth.oblabs.com.firebaseauthentication.adapter.SelectedContactAdapter;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SelectSingleItemCallback;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

public class GroupContactActivity extends AppCompatActivity implements SelectSingleItemCallback {

    RecyclerView recycleContacts , rvGroupContact;
    GroupContactAdapter adapter;
    SelectedContactAdapter selectAdapter;

    List selectList;

    String TAG = "GROUP_CONTACT_ACT";

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Participants");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabClicked());

        recycleContacts= findViewById(R.id.rvContact);
        rvGroupContact= findViewById(R.id.rvGroupContact);


        LinearLayoutManager llmGroupRecycle = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL ,false);
        rvGroupContact.setLayoutManager(llmGroupRecycle);


        selectList = new ArrayList();
        selectAdapter = new SelectedContactAdapter(selectList);
        rvGroupContact.setAdapter(selectAdapter);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false);
        recycleContacts.setLayoutManager(linearLayoutManager);




        firebaseListener();

    }

    public void  firebaseListener() {

        String owner = new SharedPreference().getValueWithKey(this, Constants.KEY_MOBILE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("data").document(owner).collection("contact").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshots != null) {

                    List<ContactData> data = new ArrayList<>();

                    for (DocumentSnapshot snapshot : snapshots.getDocuments()) {


                        data.add(new ContactData(snapshot.getData().get("name").toString(), snapshot.getData().get("mobile").toString()));


                    }
                    Collections.sort(data, new Comparator<ContactData>() {
                        public int compare(ContactData o1, ContactData o2) {
                            return ((Comparable) o1.getName())
                                    .compareTo(o2.getName());
                        }
                    });


                    adapter = new GroupContactAdapter(data, GroupContactActivity.this);
                    recycleContacts.setAdapter(adapter);

                }

            }
        });

    }

    private View.OnClickListener fabClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapter.list.size()==0){
                    Helper.showToast(GroupContactActivity.this , "Participant Count Cannot be 0");
                    return;
                }

                Intent intent = new Intent(GroupContactActivity.this , EntityGroupCreateActivity.class);
                intent.putExtra("list", (Serializable) selectAdapter.getList());

                startActivityForResult(intent , Constants.CODE_GROUP_ENTITY_CREATE);



            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.CODE_GROUP_ENTITY_CREATE)
        {
            if(data!=null) {
                boolean status = data.getBooleanExtra("status", false);

                if (status) {
                    Intent intent = new Intent();
                    intent.putExtra("status", true);

                    setResult(Constants.CODE_GROUP_ENTITY, intent);
                    finish();//finishing activity
                } else {
                    Helper.showToast(this, "Failed");
                }
            }else
                finish();

        }

        if(requestCode==Constants.CODE_FINISH_PRE_ACTIVITY)
        {
            if(data!=null) {
                boolean status = data.getBooleanExtra("status", false);

                if (status) {
                    Intent intent = new Intent();
                    intent.putExtra("status", true);

                    setResult(Constants.CODE_FINISH_PRE_ACTIVITY, intent);
                    finish();//finishing activity
                } else {
                    Helper.showToast(this, "Failed");
                }
            }else
                finish();

        }
    }

    private void updateFirebaseContact() {

        Helper.showLoading(this , "Please wait..");
        String owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("contact").document(owner);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Helper.hideLoading();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                       List<Object> data = (List<Object>) document.get("list");
                       Log.d(TAG , "size is : "+data.size());



//                       Log.d(TAG ,data.get(0).getClass().getName() );
                       adapter = new GroupContactAdapter(data , GroupContactActivity.this);
                        recycleContacts.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.hideLoading();
            }
        });






    }




    @Override
    public void selectedItem(String position) {

        rvGroupContact.setVisibility(View.VISIBLE);




        ContactData contactData = (ContactData) adapter.list.get(Integer.parseInt(position));

        selectAdapter.list.add(contactData);

        selectAdapter.notifyDataSetChanged();


    }
}
