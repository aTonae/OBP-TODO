package mauth.oblabs.com.firebaseauthentication.fragment;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.activity.ContactActivity;
import mauth.oblabs.com.firebaseauthentication.adapter.EntityAdapter;
import mauth.oblabs.com.firebaseauthentication.adapter.NewItemsAdapter;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.pojo.EntityData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


public class EntityListFragment extends Fragment implements ItemClicked  {






    RecyclerView recycleItems;



    EntityAdapter adapter;

    public String TAG = "ENTITY_FRAG";

    ContentResolver cr;


    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {


                    ContactsContract.Contacts.DISPLAY_NAME,



            };




    @SuppressLint("InlinedApi")
    private static final String SELECTION =

            ContactsContract.Contacts._ID + "  =?";
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {"1695"};





    public static EntityListFragment createInstance() {
        EntityListFragment fragment = new EntityListFragment();







        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cr = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        recycleItems= view.findViewById(R.id.rvItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL ,false);
        recycleItems.setLayoutManager(linearLayoutManager);

        cr = getActivity().getContentResolver();








        fetchEntities();











        return view;
    }

    private void fetchEntities() {

        Helper.showLoading(getContext() ,"Loading...");


            String owner = new SharedPreference().getValueWithKey(getContext() , Constants.KEY_MOBILE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

//
//
//                db.collection("entity")
//                        .whereEqualTo("partner."+owner ,true).whereEqualTo("isActive" , true)
//
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        Helper.hideLoading();
//                        if (task.isSuccessful()) {
//
//                            Log.d(TAG,"size : "+task.getResult().size());
//
////                            QuerySnapshot query = task.getResult();
////                            List<DocumentSnapshot> data = query.getDocuments();
////                            data.get(0).get("name");
//
//                            List<EntityData> listEntity = new ArrayList<>();
//
//
//
//                            for (DocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                EntityData data = new EntityData();
//                                data.setId(document.getId());
//                                data.setName(String.valueOf(document.getData().get("name")));
//                                data.setUpdated((Long) document.getData().get("updated"));
//                                data.setOwner(String.valueOf(document.getData().get("owner")));
//                                data.setpName(String.valueOf(document.getData().get("pName")));
//                                data.setoName(String.valueOf(document.getData().get("oName")));
//
//                                data.setType(String.valueOf(document.getData().get("type")));
//                                data.setLastDo(String.valueOf(document.getData().get("lastDo")));
//
//
//                                Log.d(TAG ,document.getData().get("partner").getClass().getName() );
//
//                                listEntity.add(data);
//
//
//
//                            }
//
//
//
//
//
//                            renderView(listEntity);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Helper.hideLoading();
//                        Helper.showLongToast(getContext() , "Failed");
//                    }
//                });


        db.collection("entity")
                .whereEqualTo("partner."+owner ,true).whereEqualTo("isActive" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                Helper.hideLoading();
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(snapshots != null){

                    List<EntityData> listEntity = new ArrayList<>();



                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        EntityData data = new EntityData();
                        data.setId(document.getId());
                        data.setName(String.valueOf(document.getData().get("name")));
                        data.setUpdated((Long) document.getData().get("updated"));
                        data.setOwner(String.valueOf(document.getData().get("owner")));
                        data.setpName(String.valueOf(document.getData().get("pName")));
                        data.setoName(String.valueOf(document.getData().get("oName")));

                        data.setType(String.valueOf(document.getData().get("type")));
                        data.setLastDo(String.valueOf(document.getData().get("lastDo")));
                        data.setCompletedBy(String.valueOf(document.getData().get("completedBy")));
                        data.setCompleted(document.getData().get("completed")!=null?(Boolean)document.getData().get("completed"):false);

                        Log.d(TAG ,document.getData().get("partner").getClass().getName() );

                        if(Boolean.parseBoolean(document.getData().get("isActive").toString()))
                        listEntity.add(data);



                    }





                    renderView(listEntity);

                }
            }
        });
    }

    private void renderView(List<EntityData> listEntity) {

        //        newItemsAdapter = new NewItemsAdapter(null);
        adapter = new EntityAdapter(listEntity , this  ,cr );
//
        recycleItems.setAdapter(adapter);
    }


    @Override
    public void clicked(int code) {
//
//        AddItemDialog dialog = AddItemDialog.createInstance();
//        dialog.show(getFragmentManager() , "add item");

        startActivityForResult(new Intent(getContext() , ContactActivity.class) , Constants.CODE_SINGLE_ENTITY);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.CODE_SINGLE_ENTITY)
        {

            if(data!=null) {
                boolean status = data.getBooleanExtra("status", false);

                if (status) {
                    updateEntities();
                } else {
                    Helper.showToast(getContext(), "Failed");
                }
            }

        }
    }

    private void updateEntities() {


        final String owner = new SharedPreference().getValueWithKey(getContext() , Constants.KEY_MOBILE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("entity")
                .whereEqualTo("partner."+owner ,true)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG,"size : "+task.getResult().size());

//                            QuerySnapshot query = task.getResult();
//                            List<DocumentSnapshot> data = query.getDocuments();
//                            data.get(0).get("name");

                            List<EntityData> listEntity = new ArrayList<>();



                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                EntityData data = new EntityData();
                                data.setId(document.getId());
                                data.setName(String.valueOf(document.getData().get("name")));
                                data.setUpdated((Long) document.getData().get("updated"));
                                data.setOwner(String.valueOf(document.getData().get("owner")));
                                data.setpName(String.valueOf(document.getData().get("pName")));
                                data.setoName(String.valueOf(document.getData().get("oName")));
                                data.setLastDo(String.valueOf(document.getData().get("lastDo")));
                                data.setType(String.valueOf(document.getData().get("type")));
                                data.setCompletedBy(String.valueOf(document.getData().get("completedBy")));
                                data.setCompleted((boolean) document.getData().get("completed"));



                                Log.d(TAG ,document.getData().get("partner").getClass().getName() );

                                listEntity.add(data);



                            }





                            updateView(listEntity);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });


    }

    private void updateView(List<EntityData> listEntity) {


        adapter.setList(listEntity);
        adapter.notifyDataSetChanged();

    }



    public void getName() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String name = "blank";
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        PROJECTION, SELECTION, mSelectionArgs, null);



                Log.d(TAG , cur.getCount()+" : "+cur.getColumnCount());
                if (cur.getCount() > 0) {
                    Log.i(TAG, "cursor not null");
                    while (cur.moveToNext()) {

                        name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));


                    }




                }else{
                    Log.i(TAG, "cursor is null");
                }


                Helper.showLongToast(getContext() , name);


            }
        });



    }

}
