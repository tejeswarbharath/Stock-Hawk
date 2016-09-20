package com.tejeswar.android.stockhawk.rest;

import static android.os.Looper.getMainLooper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.tejeswar.android.stockhawk.R;
import com.tejeswar.android.stockhawk.data.QuoteColumns;
import com.tejeswar.android.stockhawk.data.QuoteProvider;

/**
 * Created by tejeswar on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();
  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(Context context,String JSON){
    final Context mContext;
    mContext=context;
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){

        jsonObject = jsonObject.getJSONObject("query");

        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");

          if(!jsonObject.getString("Ask").equals("null"))
          {
            batchOperations.add(buildBatchOperation(jsonObject));
          }
          else
          {
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(mContext, mContext.getString(R.string.invalid_stock), Toast.LENGTH_SHORT).show();
              }
            });
          }
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
    if(bidPrice != null){
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    }
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    if(change != null) {
      String weight = change.substring(0, 1);
      String ampersand = "";
      if (isPercentChange) {
        ampersand = change.substring(change.length() - 1, change.length());
        change = change.substring(0, change.length() - 1);
      }
      change = change.substring(1, change.length());
      double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
      change = String.format("%.2f", round);
      StringBuffer changeBuffer = new StringBuffer(change);
      changeBuffer.insert(0, weight);
      changeBuffer.append(ampersand);
      change = changeBuffer.toString();
    }
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
              jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }
    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }


  /**
   * Returns true if network suddenly becomes available
   */

  /**static public boolean isNetworkAvailable(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  }**/

  public static ArrayList historyJsonToGraphValues(String JSON){
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    ArrayList<String> formattedResults = new ArrayList<>();
    try {
      jsonObject = new JSONObject(JSON);
      jsonObject = jsonObject.getJSONObject("query");
      jsonObject = jsonObject.getJSONObject("results");
      jsonArray = jsonObject.getJSONArray("quote");
      for(int i=0; i<jsonArray.length(); i++){
        jsonObject = jsonArray.getJSONObject(i);
        formattedResults.add(jsonObject.getString("Date"));
        formattedResults.add(jsonObject.getString("Close"));
        formattedResults.add(jsonObject.getString("Volume"));
      }
    }
    catch (JSONException e){
      e.printStackTrace();
    }

    return formattedResults;
  }

  public static StockInfo infoJsonToValues(String JSON) throws JSONException {
    JSONObject jsonObject = new JSONObject(JSON);
    jsonObject = jsonObject.getJSONObject("query");
    jsonObject = jsonObject.getJSONObject("results");
    jsonObject = jsonObject.getJSONObject("quote");
    String symbol = jsonObject.getString("symbol");
    String Ask = jsonObject.getString("Ask");
    String Bid = jsonObject.getString("Bid");
    String Change = jsonObject.getString("Change");
    String Currency = jsonObject.getString("Currency");
    String LastTradeDate = jsonObject.getString("LastTradeDate");
    String YearLow = jsonObject.getString("YearLow");
    String YearHigh = jsonObject.getString("YearHigh");
    String MarketCapitalization = jsonObject.getString("MarketCapitalization");
    String DaysRange = jsonObject.getString("DaysRange");
    String Name = jsonObject.getString("Name");
    String StockExchange = jsonObject.getString("StockExchange");
    StockInfo object = new StockInfo(symbol,Ask,Bid,Change,Currency,LastTradeDate,
            YearLow, YearHigh, MarketCapitalization, DaysRange,
            Name, StockExchange);
    return object;
  }
}