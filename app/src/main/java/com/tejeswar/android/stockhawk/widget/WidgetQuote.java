package com.tejeswar.android.stockhawk.widget;

/**
 * Created by tejeswar on 9/20/2016.
 */

public class WidgetQuote {

    public String symbol;
    public String bid;
    public String change;

    public WidgetQuote(String symbol, String bid, String change) {
        this.symbol = symbol;
        this.bid = bid;
        this.change = change;
    }

}
