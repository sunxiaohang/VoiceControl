package com.voice.view;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechUtility;
import com.voice.util.RaspBianGPIO;
import com.voice.util.SocketServer;

import java.io.IOException;

public class Main {
    private final static String APPID = "= 58f3097d";
    public static void main(String[] args) throws IOException, InterruptedException {
        SpeechUtility.createUtility(SpeechConstant.APPID + APPID);//初始化语音识别
        TextToSpeech.getInstance().translate(TalkMessages.HELLO);//打招呼
        SocketServer.startServer();//开启命令控制socket服务器
        startStream();//开启视频流服务器
        while (true) {
            Thread.sleep(500);//检测周期为0.5s
            if(RaspBianGPIO.getInstance().getGPIOStatus()) {
                SpeechToText.getInstance().translate();
            }
        }
    }
    private static void startStream() {
        String[] loadvideo = {"/bin/bash", "-c", "/home/pi/VoiceControl/VideoStream.sh  >/dev/null 2>&1 &"};
        try {
            Process process = Runtime.getRuntime().exec(loadvideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
