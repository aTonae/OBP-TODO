package mauth.oblabs.com.firebaseauthentication.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.adapter.ContactAdapter;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

public class ContactActivity extends AppCompatActivity implements ItemClicked {

    RecyclerView recycleContacts;
    ContactAdapter adapter;

    List<ContactData> contacts;

    String TAG = "CONTACT_ACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Contact");

        findViewById(R.id.fab).setVisibility(View.GONE);
        recycleContacts= findViewById(R.id.rvContact);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false);
        recycleContacts.setLayoutManager(linearLayoutManager);



//        updateFirebaseContact();
//        attachLocalListener();

        firebaseListener();
    }

    private void attachLocalListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);
        db.collection("contact").document(owner).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }


                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";


                Helper.showToast(ContactActivity.this , source);


//                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    List<Object> data = (List<Object>) documentSnapshot.get("list");

                    Log.d(TAG , "size is : "+data.size());

                    Map<String , Object> newGroupEntity = new HashMap<>();
                    newGroupEntity.put("name" , "New Group");
                    newGroupEntity.put("mobile" , "0000000000");
                    data.add(0 , newGroupEntity);

//                       Log.d(TAG ,data.get(0).getClass().getName() );
                    adapter = new ContactAdapter(data , ContactActivity.this);


                    recycleContacts.setAdapter(adapter);

        }});





    }


    public void  firebaseListener(){

        String owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("data").document(owner).collection("contact").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(snapshots != null){

                    List<ContactData> data = new ArrayList<>();

                    for (DocumentSnapshot snapshot : snapshots.getDocuments()){


                        data.add(new ContactData(snapshot.getData().get("name").toString() , snapshot.getData().get("mobile").toString()));



                    }
                    Collections.sort(data, new Comparator<ContactData>() {
                        public int compare(ContactData o1, ContactData o2) {
                            return ((Comparable) o1.getName())
                                    .compareTo( o2.getName());
                        }
                    });

                    data.add(0,new ContactData("New Group" , "0000000"));

                    adapter = new ContactAdapter(data , ContactActivity.this);
                    contacts = data;
                    recycleContacts.setAdapter(adapter);

                }

            }
        });


//        db.collection("data").document(owner).collection("contact").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                if(task.isSuccessful()){
//                    List<ContactData> data = new ArrayList<>();
//                    data.add(new ContactData("New Group" , "0000000"));
//                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()){
//
//
//                        data.add(new ContactData(snapshot.getData().get("name").toString() , snapshot.getData().get("mobile").toString()));
//
//
//
//                    }
//                    Collections.sort(data, new Comparator<ContactData>() {
//                        public int compare(ContactData o1, ContactData o2) {
//                            return ((Comparable) o1.getName())
//                                    .compareTo( o2.getName());
//                        }
//                    });
//
//                    adapter = new ContactAdapter(data , ContactActivity.this);
//                    recycleContacts.setAdapter(adapter);
//
//
//                }
//
//
//            }
//        });

//        db.collection("contacts").document(owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                if(task.isSuccessful()){
//
//
//
//
//                    Map<String , Object> hashmap = task.getResult().getData();
//
//
//                    List list = new LinkedList(hashmap.entrySet());
//                    // Defined Custom Comparator here
//                    Collections.sort(list, new Comparator() {
//                        public int compare(Object o1, Object o2) {
//                            return ((Comparable) ((Map.Entry) (o1)).getValue())
//                                    .compareTo(((Map.Entry) (o2)).getValue());
//                        }
//                    });
//
//
//
//
//
//
//                    List<ContactData> data = new ArrayList<>();
//                    data.add(new ContactData("New Group" , "0000000"));
//                    final Map<String , Object> sortedHashMap = new LinkedHashMap();
//                    for (Iterator it = list.iterator(); it.hasNext();) {
//                        Map.Entry entry = (Map.Entry) it.next();
//
////                        sortedHashMap.put((String) entry.getValue(), entry.getKey());
//                        data.add(new ContactData(entry.getValue().toString() , entry.getKey().toString()));
//
//                    }
//
//
//
//
//
//                    adapter = new ContactAdapter(data , ContactActivity.this);
//                    recycleContacts.setAdapter(adapter);
//
//
//                }
//
//            }
//        });
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

                       Map<String , Object> newGroupEntity = new HashMap<>();
                       newGroupEntity.put("name" , "New Group");
                       newGroupEntity.put("mobile" , "0000000000");
                       data.add(0 , newGroupEntity);

//                       Log.d(TAG ,data.get(0).getClass().getName() );
                       adapter = new ContactAdapter(data , ContactActivity.this);
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
    public void clicked(int code) {

        if(code==0) {

            Intent intent = new Intent();
            intent.putExtra("status", true);

            setResult(Constants.CODE_SINGLE_ENTITY, intent);
            finish();//finishing activity

        }else if(code==1){
            startActivityForResult(new Intent(this, GroupContactActivity.class) , Constants.CODE_GROUP_ENTITY);
        }
        // entity created or updated and entity detail screen opened
        else if(code==2){
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.CODE_GROUP_ENTITY)
        {
            if(data!=null) {
                boolean status = data.getBooleanExtra("status", false);

                if (status) {
                    Intent intent = new Intent();
                    intent.putExtra("status", true);

                    setResult(Constants.CODE_SINGLE_ENTITY, intent);
                    finish();//finishing activity
                } else {
                    Helper.showToast(this, "Failed");
                }
            }

        }

        if(requestCode==Constants.CODE_FINISH_PRE_ACTIVITY)
        {
            finish();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_contact, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));


        if (mSearchView != null )
        {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener()
            {
                public boolean onQueryTextChange(String newText)
                {

                    //doFilterAsync(mSearchString);
                    updateContactList(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query)
                {


                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }

    private void updateContactList(String newText) {

        List<ContactData> updatedContact = new ArrayList();
        updatedContact.add(0,new ContactData("New Group" , "0000000"));
        if(contacts!=null && contacts.size()!=0){
            for (ContactData contact : contacts){

                if(contact.getName().contains(newText) || contact.getMobile().contains(newText) ){
                    updatedContact.add(contact);
                }

            }

            adapter.list = updatedContact;
            adapter.notifyDataSetChanged();

        }else{

        }


    }
}
