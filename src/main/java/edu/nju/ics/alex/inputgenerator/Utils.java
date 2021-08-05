package edu.nju.ics.alex.inputgenerator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static int[] string2int(String str){
        int[] result={0,0,0,0};
        String temp[]=str.split("]");
        String temp1[]=temp[0].split(",");
        String temp2[]=temp[1].split(",");
        result[0]=toInt(temp1[0]);
        result[1]=toInt(temp1[1]);
        result[2]=toInt(temp2[0]);
        result[3]=toInt(temp2[1]);
        return result;
    }

    public static int toInt(String str){
        String regEX="[^0-9]";
        Pattern p= Pattern.compile(regEX);
        Matcher m=p.matcher(str);
        String str1=((Matcher) m).replaceAll("").trim();
        //System.out.println("str1:"+str1);
        return Integer.parseInt(str1);
    }

    public static long toLong(String str){
        String regEX="[^0-9]";
        Pattern p= Pattern.compile(regEX);
        Matcher m=p.matcher(str);
        String str1=((Matcher) m).replaceAll("").trim();
        //System.out.println("str1:"+str1);
        return Long.parseLong(str1);
    }

    public static boolean compareBounds(int location[], int bounds[]){
        boolean result=false;
        int x=location[0];
        int y=location[1];
        if(x>=bounds[0]&&x<=bounds[1]&&y>=bounds[2]&&y<=bounds[3]){
            result=true;
        }
        return result;
    }

    public static int[] getLocation(String event){
        int[] result={0,0,0,0};
        String temp[]=event.split(" ");
        System.out.println("length:"+temp.length);
        if(event.contains("tap")){
            result[0]=toInt(temp[4]);
            result[1]=toInt(temp[5]);
            System.out.println(result[0]+"  "+result[1]);
        }
        return result;

    }

    //接下来的分析，遍历所有的节点进行分析
    //从根节点进行分析，得到所有的节点元素，确定对应的widget，然后分析出所有可能的节点。


    /**没有decoding的就为null，有的就非null*/
    public static ArrayList<ArrayList<ImageInfo>> getDecodingEvents(ArrayList<EventTimeStamp> eventTimeStamps,String decodingImagesPath){
        ArrayList<ArrayList<ImageInfo>> result=new ArrayList<>();
        //bitmapUtils.saveDrawable(nameCount+"-"+timeStamp+"-"+"Drawable"+"-"+"createFromPath (pathName)",bmp);
        //bitmapUtils.saveUrl(nameCount+"-"+timeStamp+"-"+"url"+"-"+"setImageViewUri",param.args[1].toString());
        //首先读取文件中所有的文件，获得文件名中的时间戳还有api

        ArrayList<ImageInfo> images=getImageInfo(decodingImagesPath);
        //开始进行时间比较
        long startTime=0;
        long endTime=0;
        //遍历所有的事件的时间戳
        for(int i=0;i<eventTimeStamps.size();i++){
            if(i<(eventTimeStamps.size()-1)){//不是最后一个event
                //时间1606403790645  java中long是可以满足的
                startTime=eventTimeStamps.get(i).time;
                endTime=eventTimeStamps.get(i+1).time;
                ArrayList<ImageInfo> imageInfo_temp=new ArrayList<>();
                for(int j=0;j<images.size();j++){
                    long imageTime=toLong(images.get(j).timeStamp);
                    if((startTime<imageTime)&&(imageTime<endTime)){
                        imageInfo_temp.add(images.get(j));
                    }
                }
                result.add(imageInfo_temp);
            }else {//最后一个event，则不再需要endTime了
                startTime=eventTimeStamps.get(i).time;
                //endTime=eventTimeStamps.get(i+1).time;
                ArrayList<ImageInfo> imageInfo_temp=new ArrayList<>();
                for(int j=0;j<images.size();j++){
                    long imageTime=toLong(images.get(j).timeStamp);
                    if(startTime<imageTime){
                        imageInfo_temp.add(images.get(j));
                    }
                }
                result.add(imageInfo_temp);
            }
        }
        return result;
    }


    public static ArrayList<ImageInfo> getImageInfo(String path) {
        ArrayList<ImageInfo> imageInfos=new ArrayList<ImageInfo>();

        int fileNum = 0, folderNum = 0;
        File file = new File(path);
        LinkedList<File> list = new LinkedList<>();

        if (file.exists()) {
            if (null == file.listFiles()) {
                return null;
            }
            list.addAll(Arrays.asList(file.listFiles()));
            while (!list.isEmpty()) {
                File[] files = list.removeFirst().listFiles();
                if (null == files) {
                    continue;
                }
                for (File f : files) {
                    if (f.isDirectory()) {
                        System.out.println("文件夹:" + f.getAbsolutePath());
                        list.add(f);
                        folderNum++;
                    } else {
                        System.out.println("文件:" + f.getAbsolutePath());
                        /**进行处理*/
                        String fileName=f.getName();
                        //开始处理，对文件名进行处理
                        String temp[]=fileName.split("-");
                        ImageInfo imageInfo=new ImageInfo();
                        imageInfo.nameCount=temp[0];
                        imageInfo.timeStamp=temp[1];
                        imageInfo.imageType=temp[2];
                        imageInfo.apiName=temp[3];

                        imageInfos.add(imageInfo);
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        System.out.println("文件夹数量:" + folderNum + ",文件数量:" + fileNum);
        return imageInfos;
    }


    public static int[] splitFileName(String event){
        int[] result={0,0,0,0};
        String temp[]=event.split("-");
        System.out.println("length:"+temp.length+"   "+temp.toString());

        return result;

    }

    //日期转换为时间戳 (精确到毫秒)
    public static long timeToStamp(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return timeStemp;
    }

    public static long getGcTime(String str){
        int index=str.indexOf(".");
        System.out.println(index);
        long time1=timeToStamp(str.substring(0,index));
        long time2=toInt(str.substring(index,index+4));
        System.out.println("Time to stamp:   "+time1+"    "+time2+"    ");
        return time1+time2;
    }

    // 判断文件是否存在
    public static void judeFileExists(File file) {
      if (file.exists()) {
            System.out.println("file exists");
            } else {
            System.out.println("file not exists, create it ...");
            try {
                file.createNewFile();
                } catch (IOException e) {
                e.printStackTrace();
                }
            }
    }


    public static void main(String[] args){
        String strl="1606403790645";
        long l=toLong(strl);
        System.out.println("long: "+l);

        String str="[234,650][332,564]";
        int[] a=string2int(str);

        String event="adb shell input tap 699 1484";
        getLocation(event);//目前确实仅仅需要对tap坐标进行分析，swipe这个仅仅需要repeat就可以了，并不需要重新生成

        System.out.println(a[0]);

        String str1="abc-678-dkf-aaa";
        splitFileName(str1);

        //2020-06-08 12:17:42.001
        String dataTime="2020-06-08 12:17:42.001  abc.abc";
        int index=dataTime.indexOf(".");
        System.out.println(index);
        long time1=timeToStamp(dataTime.substring(0,index));
        long time2=toInt(dataTime.substring(index,index+4));
        System.out.println("Time to stamp:   "+time1+"    "+time2+"    ");


    }
}
