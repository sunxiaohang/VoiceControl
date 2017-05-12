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
