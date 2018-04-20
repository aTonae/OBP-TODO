package mauth.oblabs.com.firebaseauthentication.adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.activity.EntityActivity;
import mauth.oblabs.com.firebaseauthentication.activity.ShoppingActivity;
import mauth.oblabs.com.firebaseauthentication.pojo.EntityData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SelectSingleItemCallback;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


/**
 * Created by android on 8/3/17.
 */

public class EntityAdapter extends RecyclerView.Adapter<EntityAdapter.Holder>  {
    List list;
    Context context;
    ItemClicked callback;
    String owner;
    SharedPreference preference;

    ContentResolver cr;

    String TAG = "ENT_ADAPTER";


    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {


                    ContactsContract.Contacts.DISPLAY_NAME,



            };




    @SuppressLint("InlinedApi")
    private static final String SELECTION =

            ContactsContract.Contacts._ID + "  =?";
    // Defines a variable for the search string
    private String mSearchString = "1695";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};



    public void setList(List list) {
        this.list = list;
    }

    public EntityAdapter(List list , ItemClicked callback , ContentResolver contentResolver) {
        this.list = list;
        this.callback = callback;
        preference = new SharedPreference();
        this.cr = contentResolver;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_contact, parent, false);
        context = parent.getContext();
        view.setOnClickListener(itemCliked());
        view.setOnLongClickListener(itemLongClicked());
        return new Holder(view);
    }

    private View.OnLongClickListener itemLongClicked() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String position = v.getTag().toString();
                EntityData contactData = (EntityData) list.get(Integer.parseInt(position));

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("entity").document(contactData.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        list.remove(Integer.parseInt(position));

                        notifyDataSetChanged();
                    }
                });



                return false;
            }
        };
    }

    private View.OnClickListener itemCliked(  ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String position = view.getTag().toString();
                EntityData entityData = (EntityData) list.get(Integer.parseInt(position));

                Bundle bundle = new Bundle();


                bundle.putString("key" , entityData.getId());
                bundle.putString("type" , entityData.getType());



                owner = preference.getValueWithKey(context , Constants.KEY_MOBILE);


                if(entityData.getType().equals("single")) {
                    if (owner.equals(entityData.getOwner())) {
                        bundle.putString("name" , entityData.getoName());
                    } else {

                        bundle.putString("name" , entityData.getpName());
                    }
                }else{

                    bundle.putString("name" , entityData.getName());
                }



                Intent intent = new Intent(context , EntityActivity.class);
                intent.putExtras(bundle);

                context.startActivity(intent);







            }
        };
    }



    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        final EntityData entityData = (EntityData) list.get(position);


        owner = preference.getValueWithKey(context , Constants.KEY_MOBILE);


        if(entityData.getType().equals("single")) {
            if (owner.equals(entityData.getOwner())) {
                holder.name.setText(entityData.getoName());
            } else {


                holder.name.setText(entityData.getpName());
            }

//            holder.name.setText(entityData.getPartner());
//
//
//
//
//            getName(new SelectSingleItemCallback() {
//                @Override
//                public void selectedItem(String item) {
//
//
//                    holder.name.setText(item);
//
//                }
//            } , entityData.getPartner());
        }else{
            holder.name.setText(entityData.getName());
        }



//        holder.name.setText(contactData.getName());
        //code to generate emoji
//        String.valueOf(Character.toChars(0x1F60A))


        //check if the task is completed

//        Helper.showToast(context , String.valueOf(entityData.isCompleted()).concat(entityData.getLastDo()));
        if(entityData.isCompleted()){

            if(owner.equals(entityData.getCompletedBy())){
                holder.detail.setText(String.valueOf(Character.toChars(0x2705)).concat(" ").concat(entityData.getLastDo())
                        .concat("\n")
                        .concat(" Completed by : You ")
                        );
            }else {

                getName(new SelectSingleItemCallback() {
                    @Override
                    public void selectedItem(String item) {
                        holder.detail.setText(String.valueOf(Character.toChars(0x2705)).concat(" ").concat(entityData.getLastDo())
                                .concat("\n")
                                .concat(" Completed by : ")
                                .concat(item));
                    }
                }, entityData.getCompletedBy());
            }


        }else
            holder.detail.setText(String.valueOf(Character.toChars(0x23F3)).concat(" ").concat(entityData.getLastDo()));


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


    public void getName(final SelectSingleItemCallback callback , final String partner) {

        mSelectionArgs[0] = partner;

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(partner));

                String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                String contactName="";
                Cursor cursor=cr.query(uri,projection,null,null,null);

                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        contactName=cursor.getString(0);
                    }
                    cursor.close();
                }




                callback.selectedItem(contactName);


            }
        });



    }





}
