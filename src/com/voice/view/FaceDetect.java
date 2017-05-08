package com.voice.view;

import com.baidu.aip.face.AipFace;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 2017/5/7.
 */
public class FaceDetect {
    private AipFace client;
    public static final String APP_ID = "9613410";
    public static final String API_KEY = "MzF1rA3bxFUWcSEV1Anl5Ayt";
    public static final String SECRET_KEY = "sD3D0e93EC8r4dbcUBGSsbZ7qKY9S1pg";
    private static FaceDetect faceDetect=new FaceDetect();
    public static FaceDetect geInstance(){
        if(faceDetect!=null)return faceDetect;
        else return new FaceDetect();
    }
    private FaceDetect() {
        client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }
    public boolean detectFace(String imageRequest,String imageResult){
        // 调用API
        double score=0;
        boolean conform=false;
        ArrayList<String> pathArray = new ArrayList<>();
        pathArray.add(imageRequest);
        pathArray.add(imageResult);
        JSONObject response = client.match(pathArray, new HashMap<String, String>());
        System.out.println(response.toString());
        try {
            JSONArray jsonArray=response.getJSONArray("result");
            JSONObject jsonObjectChild=jsonArray.getJSONObject(0);
            score=jsonObjectChild.getDouble("score");
            if(score>90)conform=true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conform;
    }
    public boolean isFace(String path){
        JSONObject response=client.detect(path,new HashMap<String, String>());
        JSONObject jsonObject = null;
        JSONArray array=null;
        double result=0;
        System.out.println(response.toString());
        try {
            jsonObject = new JSONObject(response.toString());
            array=jsonObject.getJSONArray("result");
            JSONObject jsonObject1 = array.getJSONObject(0);
            result=jsonObject1.getDouble("face_probability");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(result+"");
        if(result>0.6)return true;
        else return false;
    }
}