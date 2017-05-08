package com.voice.util;

import com.voice.view.SpeechToText;

/**
 * Created by sunxiaohang on 17-5-8.
 */
public class VoiceAwake {
    private boolean isAwake=false;
    private SpeechToText speechToText;
    private static VoiceAwake Instance=new VoiceAwake();
    private VoiceAwake() {
        speechToText =SpeechToText.getInstance();
    }
    public static VoiceAwake getInstance(){
        if(Instance!=null)return Instance;
        else return new VoiceAwake();
    }
    public boolean isAwake() {
        return isAwake;
    }
    public void setAwake(boolean awake) {
        isAwake = awake;
    }
    public void running() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(isAwake)speechToText.translate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
