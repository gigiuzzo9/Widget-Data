package com.datawidget.datamobile;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.app.usage.NetworkStatsManager;
import android.app.usage.NetworkStats;
import android.widget.RemoteViews;

import java.util.Calendar;

public class TrafficoWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        final Context ctx = context;
        final AppWidgetManager mgr = appWidgetManager;
        final int id = appWidgetId;

        new Thread(() -> {
            double gb = leggiGbMobile(ctx);

            RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_traffico);
            views.setTextViewText(
                    R.id.txt_gb,
                    gb < 0 ? "-- GB" : String.format("%.2f GB", gb)
            );

            mgr.updateAppWidget(id, views);
        }).start();
    }

    private double leggiGbMobile(Context context) {
        try {
            NetworkStatsManager nsm =
                    (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);

            SharedPreferences prefs = context.getSharedPreferences("dati", Context.MODE_PRIVATE);
            int giornoCiclo = prefs.getInt("giorno_ciclo", 1);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            int giornoOggi = cal.get(Calendar.DAY_OF_MONTH);

            if (giornoOggi >= giornoCiclo) {
                cal.set(Calendar.DAY_OF_MONTH, giornoCiclo);
            } else {
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, giornoCiclo);
            }

            long inizio = cal.getTimeInMillis();
            long adesso = System.currentTimeMillis();

            NetworkStats.Bucket bucket = nsm.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    inizio,
                    adesso
            );

            if (bucket == null) return -1.0;
            long byteTotali = bucket.getRxBytes() + bucket.getTxBytes();
            return byteTotali / 1024.0 / 1024.0 / 1024.0;
        } catch (Exception e) {
            return -1.0;
        }
    }
}
