package mauth.oblabs.com.firebaseauthentication.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.activity.EntityActivity;
import mauth.oblabs.com.firebaseauthentication.activity.GroupContactActivity;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.pojo.EntityData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


/**
 * Created by android on 8/3/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {
    public List list;
    Context context;
    ItemClicked callback;
    String owner;

    public ContactAdapter(List list , ItemClicked callback) {
        this.list = list;
        this.callback = callback;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_contact, parent, false);
        context = parent.getContext();
        view.setOnClickListener(itemCliked());
        return new Holder(view);
    }

    private View.OnClickListener itemCliked(  ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String position = view.getTag().toString();

                if(Integer.parseInt(position)!=0) {
                    Helper.showLoading(context, "Creating...");

                    checkEntity(position);
                }
                else {
                    callback.clicked(1);

                }



            }
        };
    }

    private void checkEntity(final String position) {

        ContactData contactData = (ContactData) list.get(Integer.parseInt(position));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
       owner = new SharedPreference().getValueWithKey(context , Constants.KEY_MOBILE);

        db.collection("entity").whereEqualTo("partner."+owner , true).whereEqualTo("partner."+contactData.getMobile(),true).whereEqualTo("type" , "single").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
//                        Helper.showToast(context , task.getResult().getDocuments().get(0).getId());
                      showAlreadyExistEntity(  task.getResult().getDocuments().get(0));
                    }else{
                        createToDoEntity(position);
                    }
                }else{
                    Helper.showToast(context , "Failed");
                    Helper.hideLoading();
                }

            }
        });

    }

    private void showAlreadyExistEntity(DocumentSnapshot document) {

        EntityData entityData = new EntityData();
        entityData.setId(document.getId());
        entityData.setName(String.valueOf(document.getData().get("name")));
        entityData.setUpdated((Long) document.getData().get("updated"));
        entityData.setOwner(String.valueOf(document.getData().get("owner")));
        entityData.setpName(String.valueOf(document.getData().get("pName")));
        entityData.setoName(String.valueOf(document.getData().get("oName")));

        entityData.setType(String.valueOf(document.getData().get("type")));
        entityData.setLastDo(String.valueOf(document.getData().get("lastDo")));

        String entityName = "";
        if(entityData.getType().equals("single")) {
            if (owner.equals(entityData.getOwner())) {
                entityName = entityData.getoName();
            } else {

                entityName = entityData.getpName();
            }
        }else{

            entityName =  entityData.getName();
        }


        showEntityActivity(entityData.getId() , entityName);

    }


    private void createToDoEntity(String position) {

        final ContactData contactData = (ContactData) list.get(Integer.parseInt(position));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String owner = new SharedPreference().getValueWithKey(context , Constants.KEY_MOBILE);

        Map<String , Object> entity = new HashMap<>();
        entity.put("owner" , owner);
        entity.put("oName" , (CharSequence) contactData.getName());
        final String key = (String) contactData.getMobile();
        Map<String , Object> partners = new HashMap<>();
        partners.put(owner , true);
        partners.put(key , true);
        entity.put("partner" , partners);
        entity.put("timestamp" , System.currentTimeMillis());
        entity.put("updated" , System.currentTimeMillis());
        entity.put("type" , "single");
        entity.put("pName" , "");
        entity.put("lastDo" , null);
        entity.put("isActive" , false);





        db.collection("entity").add(entity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
//                    callback.clicked(0);

                    updatePartnerName(task.getResult().getId() , owner , key ,contactData.getName());
                }else{
                    Helper.hideLoading();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.hideLoading();
            }
        });
    }

    private void updatePartnerName(final String entityId, final String owner, final String partner, final String name) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document(partner).collection("contact").whereEqualTo("mobile" , owner).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String pName = "";
                if(task.isSuccessful() && task.getResult().size()>0){

                    pName =   task.getResult().getDocuments().get(0).get("name").toString();
                }else {
                    pName = owner;
                }


                updateEntity(entityId , pName , name);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.hideLoading();
            }
        });

    }

    private void updateEntity(final String entityId, String pName, final String name) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String , Object> data = new HashMap<>();
        data.put("pName" , pName);
        db.collection("entity").document(entityId).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                showEntityActivity(entityId , name);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Helper.hideLoading();
            }
        });
    }

    private void showEntityActivity(String entityId, String name) {


//        EntityData contactData = (EntityData) list.get(Integer.parseInt(position));
        Helper.hideLoading();
        Bundle bundle = new Bundle();
        bundle.putString("key" , entityId);
        bundle.putString("name" , name);
        bundle.putString("type" , "single");

        Intent intent = new Intent(context , EntityActivity.class);
        intent.putExtras(bundle);

        context.startActivity(intent);
        callback.clicked(2);
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        ContactData contactData = (ContactData) list.get(position);




        holder.name.setText((CharSequence) contactData.getName());
        holder.detail.setText((CharSequence) contactData.getMobile());

        if(position==0) {
            holder.name.setTextSize(16);
            holder.detail.setText("");
        }

        holder.itemView.setTag(position);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(holder.name.getText().charAt(0)), color1);
        holder.img.setImageDrawable(drawable);





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView name , detail;
        ImageView img;

        public Holder(View row) {
            super(row);

            name = (TextView)row.findViewById(R.id.tv_name);
            detail = (TextView)row.findViewById(R.id.tv_detail);
            img = (ImageView)row.findViewById(R.id.img);



        }
    }


}
