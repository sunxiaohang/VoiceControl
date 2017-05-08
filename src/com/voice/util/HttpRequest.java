package com.voice.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dell on 2017/4/16.
 */
public class HttpRequest {
    private String result;
    private String requestAddress = "http://op.juhe.cn/robot/index?info=";
    private String appKey = "&key=12efe116cb1528ac08e5d4b36c8d503d";

    public String request(String text) {
        try {
            URL url = new URL(requestAddress + text + appKey);
            System.out.println(requestAddress + text + appKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String temp = "";
            StringBuffer buffer = new StringBuffer();
            while ((temp = reader.readLine()) != null) {
                buffer.append(temp);
            }
            System.out.println("开始转换"+buffer.toString());
            JSONObject jsonObject = new JSONObject(buffer.toString()).getJSONObject("result");
            result = jsonObject.getString("text");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
