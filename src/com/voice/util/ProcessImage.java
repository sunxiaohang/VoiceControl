package com.voice.util;

import java.io.*;

/**
 * Created by sunxiaohang on 17-5-8.
 */
public class ProcessImage {
    private ProcessBuilder builder;
    private final String pathHead = "./picture/people";
    private final String pathTail = ".jpg";
    private int index = 0;

    public ProcessImage() {
        builder = new ProcessBuilder();
    }

    public String getPath() {
        String path = pathHead + index + pathTail;
        index++;
        return path;
    }
    public String scropImage() {
        String imagePath = getPath();
        while(new File(imagePath).exists())imagePath = getPath();
        builder.command("ffmpeg", "-i", "http://localhost:8080/?action=stream", "-f", "image2", "-ss",
                "0", "-vframes", "1",imagePath);
        builder.redirectErrorStream(false);
        try {
            Process process = builder.start();
            InputStream in = process.getInputStream();
            System.out.println("正在截图，请稍候，");
            convertStreamToString(in);
            InputStream errorStream=process.getErrorStream();
            if(errorStream!=null&&errorStream.read()>0){
                System.out.println("错误:");
                convertStreamToString(errorStream);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}