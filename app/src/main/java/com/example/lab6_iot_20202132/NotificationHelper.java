package com.example.lab6_iot_20202132;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CANAL_ID = "canal_sesiones";
    private static final int NOTIF_ID = 1001;

    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CANAL_ID, "Sesiones de estudio",
                    NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription("Notificaciones de sesiones de estudio");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }

    public static void notificar(Context context, String titulo, String mensaje) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.baseline_assessment_24)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIF_ID, builder.build());
    }
}