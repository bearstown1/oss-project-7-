package kr.sprite.cola;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);


        if(isEntering) {
            Toast.makeText(context, "위험지역에 접근중입니다..", Toast.LENGTH_LONG).show();
            NotificationManager notificationManager= (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(context.getApplicationContext(),MainActivity.class); //인텐트 생성.


            Notification.Builder builder = new Notification.Builder(context.getApplicationContext());
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);


            PendingIntent pendingNotificationIntent = PendingIntent.getActivity( context,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setTicker("성범죄자주의지역").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_launcher_background)
                    .setNumber(1).setContentTitle("성범죄자 주거지역입니다").setContentText("성범죄자 주거지역")
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);

            notificationManager.notify(1, builder.build()); // Notification send
        }
        else
            Toast.makeText(context, "위험지역에서 벗어납니다..", Toast.LENGTH_LONG).show();
    }
}