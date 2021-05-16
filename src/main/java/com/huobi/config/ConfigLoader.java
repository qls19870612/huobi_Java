package com.huobi.config;

import com.huobi.utils.FileOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * 创建人  liangsong
 * 创建时间 2021/05/04 17:32
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static Properties p;




    private static void init(){
        if (p==null) {
            p = new Properties();
            try {
                String s = FileOperator.getConfig("config.properties");

                InputStream in = new BufferedInputStream(new ByteArrayInputStream(s.getBytes()));

                p.load(in);

            }
            catch (Exception e)
            {
                logger.error("init {}", e.getMessage());
            }
        }
    }
    private static Properties getP(){
         if (p == null)init();
         return p;
    }
    public static String getString(String key)
    {
        return getP().getProperty(key);
    }
    public static int getInt(String key)
    {
        return Integer.parseInt(getP().getProperty(key));
    }


}
