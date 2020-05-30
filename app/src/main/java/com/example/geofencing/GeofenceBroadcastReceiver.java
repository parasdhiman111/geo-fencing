package com.example.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationHelper notificationHelper=new NotificationHelper(context);



        Toast.makeText(context,"Geofence triggered...",Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent= GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError())
        {
            Log.d(TAG, "onReceive: Error receiving geofencing event ");
            return;
        }
         List<Geofence> geofenceList =geofencingEvent.getTriggeringGeofences();
        for(Geofence geofence: geofenceList)
        {
            Log.d(TAG, "onReceive: "+geofence.getRequestId());
        }
       // Location location=geofencingEvent.getTriggeringLocation();
          int transistionType=geofencingEvent.getGeofenceTransition();

          switch (transistionType)
          {
              case Geofence.GEOFENCE_TRANSITION_ENTER:
                  Toast.makeText(context,"GEOFENCE_TRANSITION_ENTER",Toast.LENGTH_SHORT).show();
                  notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER","",MapsActivity.class);

              case Geofence.GEOFENCE_TRANSITION_DWELL:
                  Toast.makeText(context,"GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                  notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL","",MapsActivity.class);
              case Geofence.GEOFENCE_TRANSITION_EXIT:
                  Toast.makeText(context,"GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                  notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT","",MapsActivity.class);
          }


    }
}
