package raven.oblabs.com.raven.service;

import android.content.BroadcastReceiver;
import android.content.Context;  
import android.content.Intent;  
import android.media.MediaPlayer;  
import android.widget.Toast;

import mauth.oblabs.com.firebaseauthentication.R;

public class MyBroadcastReceiver extends BroadcastReceiver {  
    @Override
    public void onReceive(Context context, Intent intent) {

        intent.getFlags();



        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();  
    }  
} 