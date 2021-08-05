package edu.nju.ics.alex.inputgenerator;

import java.text.SimpleDateFormat;

//这里是将logcat中的时间输出转换为毫秒的输出，便于进行比较分析
public class Time {
    public static long toMill(){
        long time=0;
        String date="2017-06-27 15-20-00";
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        try{
            time=sd.parse(date).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }

        return time;
    }

    public static void main(String[] args){

        System.out.println(toMill());
        KUtils kUtils=new KUtils();
        kUtils.test();
    }
}
