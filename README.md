## IntelligentCar

**IntelligentCar (VoiceControl.jar)**
语音控制小车的树莓派端，主要功能包括语音识别，语音聊天，人脸身份识别以及socket通信服务器

##### 库文件
- face_sdk-1.3.4.jar
- json-20160810.jar
- json-jean-1.0.jar
- Msc.jar
- pi4j-core.jar
- pi4j-device.jar
- pi4j-gpio.extension.jar
- pi4j-service.jar

其中face_sdk是人脸识别jar包

pi4j是树莓派GPIO口库（用作音量触发）

Msc是语音识别库（科大讯飞）
##### 语音识别部分
语音转文字
```
package com.voice.view;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.voice.util.HttpRequest;
import com.voice.util.JsonParser;
import com.voice.util.ProcessImage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SpeechToText {
    private SpeechRecognizer mIat;
    private volatile String result;
    private boolean isInto = true;
    private HttpRequest request;
    private String controlCode;
    private ProcessImage processImage;
    private static SpeechToText speechToText = new SpeechToText();

    public static SpeechToText getInstance() {
        if (speechToText != null) return speechToText;
        else return new SpeechToText();
    }

    private SpeechToText() {
        request = new HttpRequest();
        processImage = new ProcessImage();
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
            System.out.println(text);
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
//                        TextToSpeech textToSpeech =TextToSpeech.getInstance();
                        localProcess(result);
//                        String temp = request.request(result);
//                        if (temp != null) {
//                            textToSpeech.translate(temp);
//                        }
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
        System.out.println("执行到了");
        System.out.println(result);
        if (result.contains("朋友")) {
            TextToSpeech.getInstance().translate("那当然很开心呢，我叫小西，你叫什么名字呢");
        } else if (result.contains("名字")) {
            String imagePath = processImage.scropImage();
            if (FaceDetect.geInstance().isFace(imagePath))
                FaceNames.getInstance().addPeople(imagePath, result.substring(result.indexOf("叫")));
            else TextToSpeech.getInstance().translate("换个角度试试，我看不清楚嘛");
        } else if (result.contains("认识我")) {
            String conformImage = processImage.scropImage();
            TextToSpeech.getInstance().translate("让我想想");
            conformFace(conformImage);
        }
    }

    public void conformFace(String imagePath) {
        File file = new File("/home/pi/VoiceControl/picture/");
        String name = "";
        if (file.exists()) {
            File files[] = file.listFiles();
            for (File f : files) {
                if (FaceDetect.geInstance().detectFace(imagePath, f.getPath())) {
                    System.out.println(f.getPath());
                    System.out.println(f.getName());
                    name = FaceNames.getInstance().getPeopleName(f.getPath());
                    System.out.println("名字" + name);
                    TextToSpeech.getInstance().translate(TalkMessages.SUCCESS + name);
                    return;
                }
            }
            TextToSpeech.getInstance().translate("我还不认识你呢");
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
```
文字转语音
```
package com.voice.view;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;
public class TextToSpeech {
	private  SpeechSynthesizer mTts;
	private static TextToSpeech textToSpeech=new TextToSpeech();
	private TextToSpeech() {
		//创建合成对象
		mTts=SpeechSynthesizer.createSynthesizer();
		mTts.setParameter(SpeechConstant.VOICE_NAME, "小燕");//发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
		//设置合成音频保存位置（可自定义保存位置），保存在“./tts_test.pcm” //如果不需要保存合成音频，注释该行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./test.pcm");
	}
	public static TextToSpeech getInstance(){
		if(textToSpeech!=null)return textToSpeech;
		else return new TextToSpeech();
	}
	public void translate(String text){
		//3.开始合成
		 mTts.startSpeaking(text, mSynListener);
	}
	private  SynthesizerListener mSynListener= new SynthesizerListener(){
		//percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在 文本中结束位置，info为附加信息。
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onCompleted(SpeechError arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, int arg3,
				Object arg4, Object arg5) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakBegin() {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakPaused() {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void onSpeakResumed() {
			// TODO Auto-generated method stub			
		} 
	};
}

```
图灵机器人
```
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


```
##### 人脸识别
```
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
    public static final String APP_ID = "XXXXXXX";
    public static final String API_KEY = "XXXXXXXXXXXXXXXXXXXX";
    public static final String SECRET_KEY = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx";
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
```
###### 树莓派GPIO触发监听器
```
package com.voice.util;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * Created by root on 2017/5/12.
 */
public class RaspBianGPIO {
    private boolean signal=false;
    private int conut=0;
    private static RaspBianGPIO Instance=new RaspBianGPIO();
    private RaspBianGPIO(){}
    public static RaspBianGPIO getInstance(){
        if(Instance!=null)return Instance;
        else return new RaspBianGPIO();
    }
    public boolean getGPIOStatus() throws InterruptedException {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput toggle = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
        toggle.setShutdownOptions(true);
        toggle.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                conut++;
                System.out.println(toggle.getState());
                if(conut%2==1)signal=true;
                else signal=false;
            }
        });
        return true;
    }
}

```
##### Socket通信
```
package com.voice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sunxiaohang on 17-5-8.
 */
public class SocketServer extends Thread {
    private Socket client;
    private String controlcode = "";

    public SocketServer(Socket c) {
        this.client = c;
    }
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            while (true) {
                String str = in.readLine();
                if (str == null) break;
                System.out.println(str);
                if (str.equals("110")) {
                    startStream();
                } else if (str.equals("119")) {
                    String[] command1 = {"/bin/bash", "-c", "killall -9 mjpg_streamer"};
                    String[] command2 = {"/bin/bash", "-c", "killall -9 video.sh"};
                    java.lang.Process process1 = Runtime.getRuntime().exec(command1);
                    java.lang.Process process2 = Runtime.getRuntime().exec(command2);
                } else {
                    controlcode = "echo -n " + str + " >/dev/ttyUSB0";
                    String[] sendcode = {"/bin/bash", "-c", controlcode};
                    java.lang.Process process = Runtime.getRuntime().exec(sendcode);
                    out.println("has receive....");
                    out.flush();
                }
            }
            client.close();
        } catch (IOException ex) {
        } finally {
        }
    }

    private void startStream() {
        String[] loadvideo = {"/bin/bash", "-c", "/home/pi/VoiceControl/VideoStream.sh  >/dev/null 2>&1 &"};
        try {
            Process process = Runtime.getRuntime().exec(loadvideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startServer() throws IOException {
        ServerSocket server = new ServerSocket(6789);
        while (true) {
            SocketServer mc = new SocketServer(server.accept());
            mc.start();
        }
    }
}
```