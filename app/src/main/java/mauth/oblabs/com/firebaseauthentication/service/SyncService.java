package raven.oblabs.com.raven.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mauth.oblabs.com.firebaseauthentication.activity.MainActivity;
import mauth.oblabs.com.firebaseauthentication.adapter.ListContactInviteAdapter;
import mauth.oblabs.com.firebaseauthentication.database.DatabaseHandler;
import mauth.oblabs.com.firebaseauthentication.pojo.ContactData;
import mauth.oblabs.com.firebaseauthentication.utils.Constants;
import mauth.oblabs.com.firebaseauthentication.utils.Helper;
import mauth.oblabs.com.firebaseauthentication.utils.SharedPreference;

public class SyncService extends Service {


   ContentResolver cr;



   @SuppressLint("InlinedApi")
   private static final String[] PROJECTION =
           {
                   ContactsContract.Contacts._ID,
                   ContactsContract.Contacts.LOOKUP_KEY,

                   ContactsContract.Contacts.DISPLAY_NAME,
                   ContactsContract.Contacts.HAS_PHONE_NUMBER,



           };


   @SuppressLint("InlinedApi")
   private static final String[] PROJECTION_INSIDE =
           {
                   ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                   ContactsContract.CommonDataKinds.Phone.NUMBER,





           };



   List<ContactData> data;

   Map<String , String> contacts;
   String owner;
   
   /** indicates how to behave if the service is killed */
   int mStartMode;
   
   /** interface for clients that bind */
   IBinder mBinder;     
   
   /** indicates whether onRebind should be used */
   boolean mAllowRebind;

   /** Called when the service is being created. */
   @Override
   public void onCreate() {
     
   }

   /** The service is starting, due to a call to startService() */
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {

      data = new ArrayList<>();

      contacts = new HashMap<>();

      owner = new SharedPreference().getValueWithKey(this , Constants.KEY_MOBILE);

      cr = getContentResolver();

      Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
              PROJECTION, null, null, null);

      if (cur.getCount() > 0) {

         while (cur.moveToNext()) {
            String id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));

            if (cur.getInt(cur.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
               Cursor pCur = cr.query(
                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                       PROJECTION_INSIDE,
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



                     if(!contacts.containsKey(phoneNo)){
                        ContactData contactData = new ContactData(phoneNo, name , id);
                        data.add(contactData);
                     }



                     contacts.put(phoneNo , name);
                  }
               }
               pCur.close();
            }
         }

         updateData();

      }else{

      }



      return mStartMode;
   }

   /** A client is binding to the service with bindService() */
   @Override
   public IBinder onBind(Intent intent) {
      return mBinder;
   }

   /** Called when all clients have unbound with unbindService() */
   @Override
   public boolean onUnbind(Intent intent) {
      return mAllowRebind;
   }

   /** Called when a client is binding to the service with bindService()*/
   @Override
   public void onRebind(Intent intent) {

   }

   /** Called when The service is no longer used and is being destroyed */
   @Override
   public void onDestroy() {

   }



   private void updateData() {


      final FirebaseFirestore db = FirebaseFirestore.getInstance();


      db.collection("data").document(owner).collection("contact").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
         @Override
         public void onComplete(@NonNull Task<QuerySnapshot> task) {

            if(task.isSuccessful()){
               for (DocumentSnapshot snapshot : task.getResult().getDocuments()){

                  db.collection("data").document(owner).collection("contact").document(snapshot.getId()).delete();
//
//
               }


               upload();









            }

         }
      });






//      final Map<String, List<ContactData>> contacts = new HashMap<>();
//      contacts.put("list",data );




//         data.add(new ContactData(entry.getValue().toString() , entry.getKey().toString()));
      }

   private void upload() {
      final FirebaseFirestore db = FirebaseFirestore.getInstance();

//
//      List list = new LinkedList(contacts.entrySet());
//      // Defined Custom Comparator here
//      Collections.sort(data, new Comparator() {
//         public int compare(Object o1, Object o2) {
//            return ((Comparable) ((Map.Entry) (o1)).getValue())
//                    .compareTo(((Map.Entry) (o2)).getValue());
//         }
//      });



//      // Here I am copying the sorted list in HashMap
//      // using LinkedHashMap to preserve the insertion order
      final HashMap sortedHashMap = new LinkedHashMap();

      Map<String, Object> contact = new HashMap<>();

      for (ContactData contactObj : data){
         contact.put("name" , contactObj.getName());
         contact.put("conId" , contactObj.getContactId());
         contact.put("mobile" , contactObj.getMobile());
         contact.put("owner" , owner);
                  db.collection("data").document(owner).collection("contact").add(contact);
         contact.clear();
      }
//      for (Iterator it = list.iterator(); it.hasNext();) {
//         Map.Entry entry = (Map.Entry) it.next();
////         sortedHashMap.put(entry.getKey(), entry.getValue());
//
////         sortedHashMap.put(entry.getKey(), entry.getValue());
//
//         contact.put("name" , entry.getValue());
//         contact.put("mobile" , entry.getKey());
//         contact.put("owner" , owner);
//
//
////     String id =     db.collection("data").document(owner).collection("contact").getId();
//
//
//
//
//
//         db.collection("data").document(owner).collection("contact").add(contact);
//         contact.clear();
//   }


//



      Log.d("Service : " , String.valueOf(sortedHashMap.size()));

      Gson gson = new Gson();
      String json = gson.toJson(sortedHashMap);

      Log.d("Service : " , json);

                                new SharedPreference().saveSession(SyncService.this , true);

      stopSelf();






      Handler handler = new Handler();
      handler.post(new Runnable() {
         @Override
         public void run() {
//
//            db.collection("contact").document(owner)
//                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//               @Override
//               public void onComplete(@NonNull Task<Void> task) {
//
//                  db.collection("contact").document(owner)
//                          .set(contacts)
//                          .addOnCompleteListener(new OnCompleteListener<Void>() {
//                             @Override
//                             public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful())
//
//                                stopSelf();
//                             }
//                          });
//
//               }
//            });

//            db.collection("contacts").document(owner).set(sortedHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//               @Override
//               public void onComplete(@NonNull Task<Void> task) {
//                  Helper.showToast(SyncService.this , "Complete");
//                  stopSelf();
//               }
//            });




         }
      });














   }

}