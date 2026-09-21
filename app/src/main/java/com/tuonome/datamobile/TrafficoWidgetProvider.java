
package com.tuonome.datamobile;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.net.TrafficStats;
import android.widget.RemoteViews;

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
            String valore = leggiGbMobile();

            RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_traffico);
            views.setTextViewText(R.id.txt_gb, valore);
            mgr.updateAppWidget(id, views);
        }).start();
    }

    private String leggiGbMobile() {
        try {
            long rx = TrafficStats.getMobileRxBytes();
            long tx = TrafficStats.getMobileTxBytes();

            if (rx == TrafficStats.UNSUPPORTED || tx == TrafficStats.UNSUPPORTED) {
                return "N/D";
            }

            long byteTotali = rx + tx;
            double gb = byteTotali / 1024.0 / 1024.0 / 1024.0;
            return String.format("%.2f GB", gb);
        } catch (Exception e) {
            return "ERR";
        }
    }
}
