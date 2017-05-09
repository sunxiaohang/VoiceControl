package com.voice.view;

import java.util.HashMap;

/**
 * Created by root on 2017/5/8.
 */
public class FaceNames {
    private HashMap<String,String> peoples;
    private static FaceNames faceNames=new FaceNames();
    private FaceNames() {
        peoples=new HashMap<>();
        addPeople("/home/pi/VoiceControl/picture/result.png","孙航");
        addPeople("/home/pi/VoiceControl/picture/confuse.png","杨红竟");
//        addPeople("/home/pi/VoiceControl/picture/xhf.png","许海峰");
    }
    public static FaceNames getInstance(){
        if(faceNames!=null)return faceNames;
        else return new FaceNames();
    }
    public void addPeople(String path,String name){
        peoples.put(path,name);
    }
    public String getPeopleName(String path){
        return peoples.get(path);
    }
}
