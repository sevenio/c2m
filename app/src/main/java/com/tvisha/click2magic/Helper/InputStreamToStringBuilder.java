package com.tvisha.click2magic.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by koti on 22/3/16.
 */
public class InputStreamToStringBuilder {

   public static StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder data = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((rLine = rd.readLine()) != null) {
                data.append(rLine);
            }
        } catch (IOException e) {
        }
        return data;
    }
}
