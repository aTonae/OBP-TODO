package mauth.oblabs.com.firebaseauthentication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.ItemClicked;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


/**
 * Created by android on 8/3/17.
 */

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.Holder> {
    public List list;
    Context context;

    public List getList() {


        return list;
    }

    public SelectedContactAdapter(List list ) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_select_contact, parent, false);
        context = parent.getContext();
        view.setOnClickListener(itemCliked());
        return new Holder(view);
    }

    private View.OnClickListener itemCliked(  ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String position = view.getTag().toString();

//                createToDoEntity(position);


            }
        };
    }

    private void createToDoEntity(String position) {


        ContactData contactData = (ContactData) list.get(Integer.parseInt(position));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String owner = new SharedPreference().getValueWithKey(context , Constants.KEY_MOBILE);

        Map<String , Object> entity = new HashMap<>();
        entity.put("owner" , owner);
        entity.put("name" , (CharSequence) contactData.getName());
        String key = (String) contactData.getMobile();
        Map<String , Object> partners = new HashMap<>();
        partners.put(owner , true);
        partners.put(key , true);
        entity.put("partner" , partners);





        db.collection("entity").add(entity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){

                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {



        ContactData contactData = (ContactData) list.get(position);





        String name = (String) contactData.getName();
        String[] splitName =  name.split(" ");
        if(splitName.length>2){
            holder.name.setText(splitName[0]+" "+splitName[1]);
        }else{
            holder.name.setText(splitName[0]);
        }



        holder.cbAction.setSelected(true);

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


        TextView name ;
        CheckBox cbAction;
        ImageView img;

        public Holder(View row) {
            super(row);

            name = (TextView)row.findViewById(R.id.tvName);
            cbAction =row.findViewById(R.id.cbAction);
            img = (ImageView)row.findViewById(R.id.img);



        }
    }


}
