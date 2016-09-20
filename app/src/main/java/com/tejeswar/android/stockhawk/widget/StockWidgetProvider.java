package com.tejeswar.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.tejeswar.android.stockhawk.R;
import com.tejeswar.android.stockhawk.service.StockTaskService;
import com.tejeswar.android.stockhawk.ui.MyStocksActivity;
/**
 * create MyWidgetProvider class to be a sub-class of AppWidgetProvider.
 * The AppWidgetProvider is a broadcast receiver class.
 * <p>
 * <p>
 * This is governing body of App Widget. Meaning everything of App Widget is controlled from here. By control means
 * <p>
 * Widget Update
 * Widget Delete
 * Widget enabled/disabled
 */

public class StockWidgetProvider extends AppWidgetProvider {

    Context mcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())){
            ComponentName name = new ComponentName(context, StockWidgetProvider.class);
            int [] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mcontext = context;

        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, svcIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }
}
