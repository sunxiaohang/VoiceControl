package com.voice.view;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechUtility;
import com.voice.util.SocketServer;
import com.voice.util.VoiceAwake;

import java.io.IOException;

public class Main {
    private final static String APPID = "= 58f3097d";
    public static void main(String[] args) throws IOException {
        SpeechUtility.createUtility(SpeechConstant.APPID + APPID);//初始化语音识别
        TextToSpeech.getInstance().translate(TalkMessages.HELLO);//打招呼
        VoiceAwake.getInstance().running();//开启语音监听
        SocketServer.startServer();//开启socket服务器
        SpeechToText.getInstance().conformFace("./picture/result.png");
    }
}
