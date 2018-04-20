package mauth.oblabs.com.firebaseauthentication.adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.pojo.TodoData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.SelectSingleItemCallback;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


/**
 * Created by android on 8/3/17.
 */

public class NewItemsAdapter extends RecyclerView.Adapter<NewItemsAdapter.Holder> {
    public List list;
    Context context;
    String KEY_ENTITY;
    int type;


    ContentResolver cr;

    String TAG = "Item_adapter";
    SharedPreference preference;




    public NewItemsAdapter(List list, String keyEntity, int i , ContentResolver cr) {
        this.list = list;
        this.KEY_ENTITY = keyEntity;
        this.type = i;
        this.cr = cr;
        preference = new SharedPreference();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_new, parent, false);
        context = parent.getContext();
        view.setOnClickListener(itemCliked());
        return new Holder(view);
    }

    private View.OnClickListener itemCliked(  ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        };
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {

        TodoData data = (TodoData) list.get(position);

        holder.tvTitle.setText(data.getInfo());

        if(type==0) {
            if(data.getOwner().equals(preference.getValueWithKey(context , Constants.KEY_MOBILE)))
            holder.tvOwner.setText("Created By You".concat(String.valueOf(Character.toChars(0x1F46E))));
            else {
                holder.tvOwner.setText(data.getOwner());
                            getName(new SelectSingleItemCallback() {
                @Override
                public void selectedItem(String item) {


                    holder.tvOwner.setText("Created By ".concat(item).concat(String.valueOf(Character.toChars(0x1F46E))));

                }
            } , data.getOwner());
            }

        }
        else {
            if(data.getCompleted().equals(preference.getValueWithKey(context , Constants.KEY_MOBILE)))
                holder.tvOwner.setText("Completed By You".concat(String.valueOf(Character.toChars(0x1F44D))));
            else {
                holder.tvOwner.setText(data.getCompleted());
                getName(new SelectSingleItemCallback() {
                    @Override
                    public void selectedItem(String item) {


                        holder.tvOwner.setText("Completed By ".concat(item).concat(String.valueOf(Character.toChars(0x1F44D))));

                    }
                } , data.getCompleted());
            }

//            holder.tvOwner.setText(data.getCompleted());
        }

        holder.tvQty.setVisibility(View.GONE);

        long now = System.currentTimeMillis();
        String time = (String) DateUtils.getRelativeTimeSpanString(data.getTimestamp(), now, DateUtils.DAY_IN_MILLIS);


        holder.tvTimestamp.setText(time);

        if(type==1){
            holder.cbComplete.setVisibility(View.GONE);
        }else {
            holder.cbComplete.setChecked(false);
            holder.cbComplete.setOnClickListener(checkBoxClicked(position));
        }






    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {



        TextView tvTitle , tvQty , tvOwner , tvTimestamp;
        CheckBox cbComplete;

        public Holder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);



            cbComplete = itemView.findViewById(R.id.cbComplete);








        }
    }

    private View.OnClickListener checkBoxClicked(final int adapterPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TodoData data = (TodoData) list.get(adapterPosition);



                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final String owner = new SharedPreference().getValueWithKey(context , Constants.KEY_MOBILE);

                final Map<String , Object> entity = new HashMap<>();

                entity.put("completed" , owner);




                entity.put("updated" , System.currentTimeMillis());
                entity.put("status" , true);

                db.collection("entity").document(KEY_ENTITY).collection("todos").document(data.getId()).update(entity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            entity.clear();
                            entity.put("completed" , true);
                            entity.put("updated" , System.currentTimeMillis());
                            entity.put("completedBy", owner);
                            entity.put("lastDo" , data.getInfo());

                            db.collection("entity").document(KEY_ENTITY).update(entity);
                            list.remove(adapterPosition);
                            notifyDataSetChanged();
                        }
                    }
                });









            }
        };
    }

    public void getName(final SelectSingleItemCallback callback , final String partner) {


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


                Log.d(TAG , "Name : "+ contactName + " ~"+System.currentTimeMillis());


            }
        });



    }



}
