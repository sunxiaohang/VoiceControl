package com.voice.view;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechUtility;

public class Main {
    private final static String APPID = "= 58f3097d";
    public static void main(String[] args) {
        SpeechUtility.createUtility(SpeechConstant.APPID + APPID);//初始化语音识别
//        TextToSpeech.getInstance().translate(TalkMessages.HELLO);//打招呼
//        VoiceAwake.getInstance().running();//开启语音监听
        SpeechToText.getInstance().conformFace("./picture/result.png");
    }
}
