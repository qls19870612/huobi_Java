package com.huobi;

import com.huobi.config.ConfigLoader;

public class Constants {
  public static String API_KEY = "";
  public static String SECRET_KEY = "";
  static{
    API_KEY = ConfigLoader.getString("your_api_key");
    SECRET_KEY = ConfigLoader.getString("your_secret");
  }

}
