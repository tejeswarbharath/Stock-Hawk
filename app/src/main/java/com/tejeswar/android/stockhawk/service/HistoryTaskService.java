package com.tejeswar.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.tejeswar.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by tejeswar on 16/6/16.
 */
public class HistoryTaskService extends GcmTaskService {

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    String symbol;
    String startDate,endDate;

    public HistoryTaskService(){

    }

    public HistoryTaskService(Context context) {
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
        startDate = bundle.getString("startDate");
        endDate = bundle.getString("endDate");

        StringBuilder urlStringBuilder = new StringBuilder();
        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select Date,Close,Volume from yahoo.finance.historicaldata where symbol = \"", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode(symbol + "\"", "iso-8859-1"));
            urlStringBuilder.append(URLEncoder.encode(" and startDate = \"" + startDate + "\"", "iso-8859-1"));
            urlStringBuilder.append(URLEncoder.encode(" and endDate = \"" + endDate + "\"", "iso-8859-1"));
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
        if (getResponse != null) {
            ArrayList<String> resultsList = Utils.historyJsonToGraphValues(getResponse);

            Intent intent = new Intent("historyResults");
            intent.putStringArrayListExtra("list", resultsList);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        return 0;
    }
}