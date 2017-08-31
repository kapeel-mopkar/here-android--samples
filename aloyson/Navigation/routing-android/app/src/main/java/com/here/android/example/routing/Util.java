package com.here.android.example.routing;

/**
 * Created by aloyson_decosta on 18-08-2017.
 */
import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;


public class Util {
      static Properties properties = null;

    public Util(Context context) throws IOException{
        properties=new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
    }

    public static String getProperty(String key) {

        return properties.getProperty(key);

    }

    public static Set<Object> getKeys(){
        return properties.keySet();
    }

}
