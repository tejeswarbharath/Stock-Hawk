package com.tejeswar.android.stockhawk.rest;

import java.io.Serializable;

/**
 * Created by tejeswar on 9/20/2016.
 */
public class StockInfo implements Serializable
{
    public String symbol;
    public String Ask;
    public String Bid;
    public String Change;
    public String Currency;
    public String LastTradeDate;
    public String YearLow;
    public String YearHigh;
    public String MarketCapitalization;
    public String DaysRange;
    public String Name;
    public String StockExchange;

    public StockInfo(String symbol, String Ask, String Bid,
                     String Change, String Currency, String LastTradeDate,
                     String YearLow, String YearHigh, String MarketCapitalization,
                     String DaysRange, String Name, String StockExchange){

        this.symbol = symbol;
        this.Ask = Ask;
        this.Bid = Bid;
        this.Change = Change;
        this.Currency = Currency;
        this.LastTradeDate = LastTradeDate;
        this.YearLow = YearLow;
        this.YearHigh = YearHigh;
        this.MarketCapitalization = MarketCapitalization;
        this.DaysRange = DaysRange;
        this.Name = Name;
        this.StockExchange = StockExchange;
    }

}
