package mauth.oblabs.com.firebaseauthentication.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.network.ApiRestAdapter;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ObjectCallback;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemDialog extends DialogFragment {





    EditText etItemName;

    public String KEY_ENTITY , TYPE;

    ObjectCallback callback;
    String owner;

    public static AddItemDialog createInstance(String keyEntity, String type, ObjectCallback callback) {
        AddItemDialog fragment = new AddItemDialog();
        fragment.KEY_ENTITY = keyEntity;
        fragment.callback = callback;
        fragment.TYPE = type;

        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            /* Use the Builder class for convenient dialog construction */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        /* Get the layout inflater */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_meal, null);

        etItemName = rootView.findViewById(R.id.edit_text_meal_name);

        owner = new SharedPreference().getValueWithKey(getContext() , Constants.KEY_MOBILE);

        /**
         * Call addMeal() when user taps "Done" keyboard action
         */
        etItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addMeal();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout */
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addMeal();
                    }
                });



        return builder.create();
    }

    private void addMeal() {


        if(etItemName.getText().toString().isEmpty()){
            etItemName.setError("Not a valid name.");
            return;
        }





        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String owner = new SharedPreference().getValueWithKey(getContext() , Constants.KEY_MOBILE);

        final Map<String , Object> entity = new HashMap<>();
        entity.put("owner" , owner);
        entity.put("info" , etItemName.getText().toString());
        entity.put("timestamp" , System.currentTimeMillis());
        entity.put("status" , false);

        db.collection("entity").document(KEY_ENTITY).collection("todos").add(entity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){


                        entity.put("id" , task.getResult().getId());

                    entity.clear();
                    entity.put("updated" , System.currentTimeMillis());
                    entity.put("lastDo" , etItemName.getText().toString());
                    entity.put("isActive" , true);
                    entity.put("completed" , false);
                    db.collection("entity").document(KEY_ENTITY).update(entity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                sendPush();

                            }
                        }
                    });

                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });














    }

    private void sendPush() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(TYPE.equals("single")){



            db.collection("entity").document(KEY_ENTITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                      Map<String , Object> partners = (Map<String, Object>) task.getResult().getData().get("partner");

                        Log.d("Add Item" , String.valueOf(partners.size()));

                        String to = "";
                        for(String partner : partners.keySet()){
                            if(!owner.equals(partner))
                            to = partner;
                        }

                        new ApiRestAdapter().sendSingleTodo(owner , to , etItemName.getText().toString()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                dismiss();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });


                    }

                }
            });

        }else{

            db.collection("entity").document(KEY_ENTITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        Map<String , Object> partners = (Map<String, Object>) task.getResult().getData().get("partner");

                        Log.d("Add Item" , String.valueOf(partners.size()));

                        List<String> to = new ArrayList<>();
                        for(String partner : partners.keySet()){
                            if(!owner.equals(partner))
                                to.add(partner);
                        }

                        new ApiRestAdapter().sendGroupTodo(owner , to , etItemName.getText().toString()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                dismiss();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {

                            }
                        });


                    }

                }
            });

        }


    }


}
