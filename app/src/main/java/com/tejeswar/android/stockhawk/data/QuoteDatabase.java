package com.tejeswar.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


/**
 * Created by tejeswar on 10/5/15.
 */
@Database(version = QuoteDatabase.VERSION)

public class QuoteDatabase {
  private QuoteDatabase(){}

  public static final int VERSION = 13;

  @Table(QuoteColumns.class) public static final String QUOTES = "quotes";

}
