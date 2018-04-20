package mauth.oblabs.com.firebaseauthentication.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import android.widget.TextView;


import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.HashMap;
import java.util.List;

import mauth.oblabs.com.firebaseauthentication.R;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;

/**
 * Created by Akash on 6/13/2016.
 */
public class ListContactInviteAdapter extends ArrayAdapter {




    public ListContactInviteAdapter(Context context, int element_contact) {
        super(context , element_contact);

        this.context = context;
        checkBoxStatus = new HashMap<>();
    }

    public void setListItemCount(List listItemCount) {
        this.listItemCount = listItemCount;
    }
    Context context;
    List<ContactData> listItemCount ;
    View row;

    public List<ContactData> getListItemCount() {
        return listItemCount;
    }

    boolean isButtonVisible = true;
    HashMap<Integer , Boolean> checkBoxStatus;

    public void setButtonVisible(boolean buttonVisible) {
        isButtonVisible = buttonVisible;
    }



    @Override
    public int getCount() {
        if(listItemCount!=null) {
            return listItemCount.size();
        }else{
            return 10;
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService((Context.LAYOUT_INFLATER_SERVICE));
                row = layoutInflater.inflate(R.layout.element_contact, parent, false);

        TextView name = (TextView)row.findViewById(R.id.tv_name);
        TextView detail = (TextView)row.findViewById(R.id.tv_detail);
        ImageView img = (ImageView)row.findViewById(R.id.img);
        ContactData contactData = listItemCount.get(position);
        CheckBox checkBox = (CheckBox) row.findViewById(R.id.cb_action);
        checkBox.setOnClickListener(checkBoxClickListener(position));

        Button button = (Button)row.findViewById(R.id.bttnRequest);
        button.setText("Action");
        Button buttonView = (Button)row.findViewById(R.id.bttnView);
        button.setOnClickListener(buttonClickListener(position));


        updateCheckBoxStatus(checkBox , position);
        if(isButtonVisible){
            checkBox.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);

            updatViewButtonVisibility(position , buttonView);

        }else{
            button.setVisibility(View.GONE);
            checkBox.setVisibility(View.VISIBLE);
        }

        name.setText(contactData.getName());
        detail.setText(contactData.getMobile());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(contactData.getName().charAt(0)), color1);
        img.setImageDrawable(drawable);


        return row ;
    }

    private void updatViewButtonVisibility(int position, final Button buttonView) {

    }




    private void updateCheckBoxStatus(CheckBox checkBox, int position) {


        boolean status = checkBoxStatus.containsKey(position);
        if(status){
            checkBox.setChecked(checkBoxStatus.get(position));
        }else{
            checkBox.setChecked(false);
        }
    }

    private View.OnClickListener buttonClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMethodChooseDialog(position);

            }
        };
    }

    public void makeRequest(final int position){


    }

    private View.OnClickListener checkBoxClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                if(cb.isChecked()){
                    checkBoxStatus.put(position , true);
                    Log.d("adapter contact" , "checkbox is checked : "+position);
                }else{
                    checkBoxStatus.put(position , false);
                    Log.d("adapter contact" , "checkbox is not checked : "+position);
                }
            }
        };
    }
    private void showMethodChooseDialog(final int position) {

    }

}


