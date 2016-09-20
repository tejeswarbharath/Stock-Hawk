package com.tejeswar.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.tejeswar.android.stockhawk.rest.StockInfo;
import com.tejeswar.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class InfoTaskService extends GcmTaskService {

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    String symbol;

    public InfoTaskService(){}

    public InfoTaskService(Context context) {
        mContext = context;
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        if (mContext == null) {
            mContext = this;
        }

        Bundle bundle = taskParams.getExtras();
        symbol = bundle.getString("symbol");

        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select symbol,Ask,Bid,Currency,Change,LastTradeDate,DaysRange,YearLow,YearHigh,Name,StockExchange,MarketCapitalization from yahoo.finance.quotes where symbol = \"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(symbol + "\"", "iso-8859-1"));
            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlString;
        String getResponse = "";
        int result = GcmNetworkManager.RESULT_FAILURE;

        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();
            try {
                getResponse = fetchData(urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent("infoResults");

        if (getResponse != null) {
            try {
                StockInfo object = Utils.infoJsonToValues(getResponse);
                intent.putExtra("object", object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
        return 0;
    }
}