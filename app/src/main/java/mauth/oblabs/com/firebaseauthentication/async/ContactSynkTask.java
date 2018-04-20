package mauth.oblabs.com.firebaseauthentication.async;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mauth.oblabs.com.firebaseauthentication.database.DatabaseHandler;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;


class ContactSynkTask extends AsyncTask<Void, Void,  List<ContactData>> {

        // Parsing the data in non-ui thread


    ContentResolver cr;
    List<ContactData> contactDatas;
    String TAG = "contact synk adpater";
    ProgressDialog progressDialog;
    DatabaseHandler databaseHandler;
    String userId;
    Context context;

    public ContactSynkTask(Context context, ContentResolver cr, ProgressDialog progressDialog, DatabaseHandler databaseHandler, String userId) {
        this.cr = cr;
        this.progressDialog = progressDialog;
        this.databaseHandler = databaseHandler;
        this.userId = userId;
        this.context = context;


    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Initializing Contacts..");
        progressDialog.setCancelable(false);
    progressDialog.show();
    }

    @Override
    protected List<ContactData> doInBackground(Void... params) {
        contactDatas = new ArrayList<>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            Log.i(TAG, "cursor not null");
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = phoneNo.replaceAll("[^0-9]", "");
                        if(!name.equals("Identified As Spam")){
                            phoneNo = phoneNo.replace(" ", "");
                            if(phoneNo.length()>10){

                                switch (phoneNo.length()){
                                    case 11:
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        break;
                                    case 12:
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        break;
                                    case 13:
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        phoneNo = phoneNo.replaceFirst("[+|9|1|0]","");
                                        break;
                                    case 14:

                                        break;
                                }


                            }

                            ContactData contactData = new ContactData(phoneNo, name);
                            contactDatas.add(contactData);



                        }



                    }
                    pCur.close();
                }
            }


        }else{
            Log.i(TAG, "cursor is null");
        }
        return contactDatas;
    }

    @Override
    protected void onPostExecute(final List<ContactData> contactDatas) {




        List<String> contact = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for(ContactData contactData : contactDatas){
            contact.add(contactData.getMobile());
            names.add(contactData.getName());
        }

//        new ApiRestAdapter().checkContactStatus(contact ,userId , names).enqueue(new Callback<List<Integer>>() {
//            @Override
//            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
//                if(response!=null && response.body().size()!=0){
//                    updateUgcStatus(response.body(), contactDatas);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Integer>> call, Throwable t) {
//
//            }
//        });


    }



    private void updateUgcStatus(List<Integer> body, List<ContactData> contactDatas) {

        Log.d("database" , "retrun size id "+body.size());
        Log.d("database" , "updated size id "+contactDatas.size());
        for(int i=0;i<contactDatas.size();i++){

            if(databaseHandler.checkContactIsPresent(contactDatas.get(i).getMobile())){
                databaseHandler.updateUgcStatus(contactDatas.get(i).getMobile() , body.get(i));
            }else {
                databaseHandler.addContact(contactDatas.get(i));
            }
        }
        progressDialog.dismiss();
        new SharedPreference().saveValueWithKey(context , "is-synk" , "true");

    }
}

