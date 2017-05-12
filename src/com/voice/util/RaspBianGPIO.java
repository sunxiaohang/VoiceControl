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
