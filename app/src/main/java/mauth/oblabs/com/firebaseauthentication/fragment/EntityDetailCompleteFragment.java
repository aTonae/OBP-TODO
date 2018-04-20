package mauth.oblabs.com.firebaseauthentication.fragment;


import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.adapter.NewItemsAdapter;
import mauth.oblabs.com.firebaseauthentication.dialog.AddItemDialog;
import mauth.oblabs.com.firebaseauthentication.pojo.TodoData;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.ObjectCallback;


public class EntityDetailCompleteFragment extends Fragment implements ItemClicked {






    RecyclerView recycleItems;
    NewItemsAdapter newItemsAdapter;
    ContentResolver contentResolver;




    public String TAG = "ENTITY_DETAIL_FRAG";

    public static String KEY_ENTITY ;







    public static EntityDetailCompleteFragment createInstance(String key) {
        EntityDetailCompleteFragment fragment = new EntityDetailCompleteFragment();

        fragment.KEY_ENTITY = key;






        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);

        recycleItems= view.findViewById(R.id.rvItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL ,false);
        recycleItems.setLayoutManager(linearLayoutManager);







//        fetchEntities();

        attachLocalListener();











        return view;
    }

    private void attachLocalListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("entity").document(KEY_ENTITY).collection("todos").whereEqualTo("status" , true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }


                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";


//                Helper.showToast(getContext() , source);

                List<TodoData> listEntity = new ArrayList<>();

                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    TodoData data = new TodoData();


                    data.setId(document.getId());
                    data.setInfo((String) document.getData().get("info"));
                    data.setOwner((String) document.getData().get("owner"));
                    data.setTimestamp((Long) document.getData().get("timestamp"));
                    data.setCompleted((String) document.getData().get("completed"));






                    listEntity.add(data);



                }

                renderView(listEntity);


            }
        });

    }

    private void fetchEntities() {






            FirebaseFirestore db = FirebaseFirestore.getInstance();



                db.collection("entity").document(KEY_ENTITY).collection("todos")
                        .whereEqualTo("status" , true)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            Log.d(TAG,"size : "+task.getResult().size());


                            List<TodoData> listEntity = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                TodoData data = new TodoData();


                                data.setId(document.getId());
                                data.setInfo((String) document.getData().get("info"));
                                data.setOwner((String) document.getData().get("owner"));
                                data.setTimestamp((Long) document.getData().get("timestamp"));





                                listEntity.add(data);



                            }

                            renderView(listEntity);
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

    private void renderView(List<TodoData> listEntityDetail) {

                newItemsAdapter = new NewItemsAdapter(listEntityDetail , KEY_ENTITY , 1 , contentResolver);

//
        recycleItems.setAdapter(newItemsAdapter);
    }


    @Override
    public void clicked(int code) {

        AddItemDialog dialog = AddItemDialog.createInstance(KEY_ENTITY, "single", new ObjectCallback() {
            @Override
            public void itemCreated(Object item) {

                Map<String , Object> hash = (Map<String, Object>) item;
                TodoData data = new TodoData();
                data.setId((String) hash.get("id"));
                data.setInfo((String) hash.get("info"));
                data.setOwner((String) hash.get("owner"));
                data.setTimestamp((Long) hash.get("timestamp"));
                data.setStatus((Boolean) hash.get("status"));


               newItemsAdapter.list.add(0 , data);
               newItemsAdapter.notifyDataSetChanged();


            }
        });
        dialog.show(getFragmentManager() , "add todo");



    }

    private void updateEntities() {



        FirebaseFirestore db = FirebaseFirestore.getInstance();



        db.collection("entity").document(KEY_ENTITY).collection("todos")
                .whereEqualTo("status" , true)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG,"size : "+task.getResult().size());

//                            QuerySnapshot query = task.getResult();
//                            List<DocumentSnapshot> data = query.getDocuments();
//                            data.get(0).get("name");

                            List<TodoData> listEntity = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                TodoData data = new TodoData();


                                data.setId(document.getId());
                                data.setInfo((String) document.getData().get("info"));
                                data.setOwner((String) document.getData().get("owner"));
                                data.setTimestamp((Long) document.getData().get("timestamp"));





                                listEntity.add(data);



                            }

                            updateView(listEntity);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void updateView(List<TodoData> listEntity) {
        Helper.showLongToast(getContext() , "Updated");
        newItemsAdapter.list = listEntity;
        newItemsAdapter.notifyDataSetChanged();

    }
}
