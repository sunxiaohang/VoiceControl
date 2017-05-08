package com.voice.view;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.voice.util.HttpRequest;
import com.voice.util.JsonParser;
import com.voice.util.ProcessImage;

import java.io.File;
import java.io.IOException;

public class SpeechToText {
    private SpeechRecognizer mIat;
    private volatile String result;
    private boolean isInto = true;
    private HttpRequest request;
    private String controlCode;
    private ProcessImage processImage;
    private static SpeechToText speechToText=new SpeechToText();

    public static SpeechToText getInstance(){
        if(speechToText!=null)return speechToText;
        else return new SpeechToText();
    }
    private SpeechToText() {
        request = new HttpRequest();
        processImage=new ProcessImage();
        //1.创建语音对象
        mIat = SpeechRecognizer.createRecognizer();
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    public void translate() {
        mIat.startListening(mRecoListener);//3.开始听写
    }

    //监听对象
    private RecognizerListener mRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = results.getResultString();
            if (text == null || text == "") {
                System.out.print("请输入语音");
            } else {
                result = JsonParser.parseIatResult(text);
                System.out.println(result);
                if (result.contains("前")) {
                    try {
                        moveForward();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.contains("后")) {
                    try {
                        moveBack();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.contains("左")) {
                    try {
                        moveLeft();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.contains("右")) {
                    try {
                        moveRight();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.contains("停")) {
                    try {
                        stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.contains("直")) {
                    try {
                        straight();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isInto) {
                        TextToSpeech textToSpeech =TextToSpeech.getInstance();
                        String temp = request.request(result);
                        localProcess(result);
                        if (temp != null) {
                            textToSpeech.translate(temp);
                        }
                        isInto = false;
                    }
                }
            }
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getErrorDescription(true);//获取错误码描述
            if (null != error) {
                System.out.print("onError Code：" + error.getErrorCode());
            }
        }
        //开始录音
        public void onBeginOfSpeech() {
            System.out.print("开始录音：");
        }  //音量值0~30
        public void onVolumeChanged(int volume) {

        }
        //结束录音
        public void onEndOfSpeech() {

        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, String msg) {

        }
    };

    private void localProcess(String result) {
        if(result.contains("朋友")){
            TextToSpeech.getInstance().translate("那当然很开心呢，我叫小西，你叫什么名字呢");
        }else if(result.contains("名字")){
            String imagePath=processImage.scropImage();
            if(FaceDetect.geInstance().isFace(imagePath))
                FaceNames.getInstance().addPeople(imagePath,result.substring(result.indexOf("叫")));
            else TextToSpeech.getInstance().translate("换个角度试试，我看不清楚嘛");
        }else if(result.contains("认识我")){
            String conformImage=processImage.scropImage();
            TextToSpeech.getInstance().translate("让我想想");
            conformFace(conformImage);
        }
    }

    public void conformFace(String imagePath) {
        File file=new File("./picture");
        String name="";
        FaceNames.getInstance().addPeople("./picture/test.png","杨红竟");
        FaceNames.getInstance().addPeople("./picture/confuse.png","杨红竟");
        if(file.exists()){
            File files[]=file.listFiles();
            for (File f:files){
                if(FaceDetect.geInstance().detectFace(imagePath,f.getPath())) {
                    name = FaceNames.getInstance().getPeopleName(f.getPath());
                    System.out.println("名字"+name);
                    TextToSpeech.getInstance().translate(TalkMessages.SUCCESS+name);
                    break;
                }
                else TextToSpeech.getInstance().translate("我还不认识你呢");
            }
        }
    }

    private void stop() throws IOException {
        System.out.println("停止");
        controlCode = "echo -n " + "A" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
        controlCode = "echo -n " + "B" + " >/dev/ttyUSB0";
        String[] sendCode2 = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode2);
    }

    private void moveRight() throws IOException {
        System.out.println("右转");
        controlCode = "echo -n " + "d" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
    }

    private void moveLeft() throws IOException {
        System.out.println("左转");
        controlCode = "echo -n " + "c" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
    }

    private void moveBack() throws IOException {
        System.out.println("后退");
        controlCode = "echo -n " + "b" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
    }

    private void straight() throws IOException {
        System.out.println("直行");
        controlCode = "echo -n " + "C" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
        controlCode = "echo -n " + "D" + " >/dev/ttyUSB0";
        String[] sendCode2 = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode2);
    }
    private void moveForward() throws IOException {
        System.out.println("前进");
        controlCode = "echo -n " + "a" + " >/dev/ttyUSB0";
        String[] sendCode = {"/bin/bash", "-c", controlCode};
        Runtime.getRuntime().exec(sendCode);
    }
}
