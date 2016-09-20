package com.tejeswar.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import com.tejeswar.android.stockhawk.R;
import com.tejeswar.android.stockhawk.data.QuoteColumns;
import com.tejeswar.android.stockhawk.data.QuoteProvider;

/**
 * Created by tejeswar on 9/20/2016.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context context = null;
    private int appWidgetId;
    private ArrayList<WidgetQuote> list = new ArrayList<WidgetQuote>();

    @Override
    public void onDestroy() {}

    @Override
    public void onDataSetChanged() {
        mCursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        list.clear();


        for (mCursor.moveToFirst(); ! mCursor.isAfterLast(); mCursor.moveToNext()) {
            list.add(new WidgetQuote(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)),
                    mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)),
                    mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE))));
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {

    }

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_item_quote);
        remoteView.setTextViewText(R.id.stock_symbol, list.get(i).symbol);
        remoteView.setTextViewText(R.id.bid_price, list.get(i).bid+"$");
        remoteView.setTextViewText(R.id.change, list.get(i).change+"$");
        remoteView.setTextColor(R.id.stock_symbol, context.getResources().getColor(R.color.black));
        remoteView.setTextColor(R.id.bid_price, context.getResources().getColor(R.color.black));
        remoteView.setTextColor(R.id.change, context.getResources().getColor(R.color.black));
        return remoteView;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
