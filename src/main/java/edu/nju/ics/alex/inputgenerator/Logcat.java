package edu.nju.ics.alex.inputgenerator;

import java.io.*;

import static edu.nju.ics.alex.inputgenerator.Utils.getGcTime;

/**
 *
 * 处理机制：
 * 每次event执行之前，需要进行一次分析，然后将分析结果存储在一个全局的对象当中，读取GC数据的只有一个地方，可以设计一个全局变量
 *
 * 这里仅仅分析一个event是否触发了GC，触发了什么类型的GC
 *
 * 现在首先获得每个事件触发时的时间戳格式，得到decoding 的时间戳，然后进行统一分析
 *
 *
 * */
public class Logcat {

    public static int fileCount=2;

    public static void main(String[] args){
        String filePath=MainKt.getDirectoryPath()+"/testOutput/wordPress/logcat/3.txt";
        getLogcat(filePath);
        GcResult gcResult=checkGcInFile(1,filePath);
        isDecodingEvent();


    }

    public static void getLogcat(String filePath){

        //保存日志到当前目录指定文件，这个命令执行后，将持续不断的将产生的日志写入到文件
        String adb="adb logcat > "+filePath;



    }



    public static void copyFile(String srcPathStr, String desPathStr) {
        try {
            FileInputStream fis = new FileInputStream(srcPathStr);//创建输入流对象
            FileOutputStream fos = new FileOutputStream(desPathStr); //创建输出流对象
            byte datas[] = new byte[1024 * 8];//创建搬运工具
            int len = 0;//创建长度
            while ((len = fis.read(datas)) != -1)//循环读取数据
            {
                fos.write(datas, 0, len);
            }
            fis.close();//释放资源
            fis.close();//释放资源
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static GcResult checkGcInFile(int loopIndex,String path) {
        GcResult gcResult = new GcResult();
        try {
            String tem = "";
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(isr, 5 * 1024 * 1024);
            /**遍历所有语句*/
            while ((tem = reader.readLine()) != null) {
                //TODO 首先要解析分析到哪一块，获得时间信息
                if (tem.contains("GC freed")) {//判断是否为GC信息的输出
                    //继续进行gc分类存储
                    GC gc=new GC();
                    if (tem.contains("Background partial concurrent mark sweep")) {
                        gc.eventCount=1;
                        gc.loopIndex=loopIndex;
                        gc.timeStamp=getGcTime(tem);
                        gc.GcInfo=tem;
                        gcResult.backgroundPartialGc.add(gc);

                    } else if (tem.contains("Background sticky concurrent mark sweep")) {
                        gc.timeStamp=getGcTime(tem);
                        gc.GcInfo=tem;
                        gcResult.backgroundStickyGc.add(gc);

                    } else if (tem.contains("Alloc partial concurrent mark sweep")) {
                        gc.timeStamp=getGcTime(tem);
                        gc.GcInfo=tem;
                        gcResult.allocPartialGc.add(gc);

                    } else {
                        gc.timeStamp=getGcTime(tem);
                        gc.GcInfo=tem;
                        gcResult.otherGc.add(gc);

                    }
                }
            }
            reader.close();
            isr.close();
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return gcResult;
    }

    /**
     * 现在先得到一个文件的所有信息看看
     * 感觉我们需要从文件的最末开始遍历才最节省时间。
     * */
    public static GcResult checkGcInFileEnd(long stopTime,int loopIndex,String filename) {
        GcResult gcResult = new GcResult();

        String charset="GBK";

        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(filename, "r");
            long len = rf.length();
            long start = rf.getFilePointer();
            long nextend = start + len - 1;
            String line;
            rf.seek(nextend);
            int c = -1;
            while (nextend > start) {//反方向遍历
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                        String tem=new String(line.getBytes("ISO-8859-1"), charset);
                        //开始进行分析处理
                        GC gc=new GC();
                        if (tem.contains("GC freed")) {//判断是否为GC信息的输出
                            //继续进行gc分类存储
                            if (tem.contains("Background partial concurrent mark sweep")) {
                                gc.eventCount=1;
                                gc.loopIndex=loopIndex;
                                gc.timeStamp=getGcTime(tem);
                                if(gc.timeStamp<=stopTime){
                                    return gcResult;
                                }
                                gc.GcInfo=tem;
                                gcResult.backgroundPartialGc.add(gc);

                            } else if (tem.contains("Background sticky concurrent mark sweep")) {
                                gc.eventCount=1;
                                gc.loopIndex=loopIndex;
                                gc.timeStamp=getGcTime(tem);
                                if(gc.timeStamp<=stopTime){
                                    return gcResult;
                                }
                                gc.GcInfo=tem;
                                gcResult.backgroundStickyGc.add(gc);

                            } else if (tem.contains("Alloc partial concurrent mark sweep")) {
                                gc.eventCount=1;
                                gc.loopIndex=loopIndex;
                                gc.timeStamp=getGcTime(tem);
                                if(gc.timeStamp<=stopTime){
                                    return gcResult;
                                }
                                gc.GcInfo=tem;
                                gcResult.allocPartialGc.add(gc);

                            } else {
                                gc.eventCount=1;
                                gc.loopIndex=loopIndex;
                                gc.timeStamp=getGcTime(tem);
                                if(gc.timeStamp<=stopTime){
                                    return gcResult;
                                }
                                gc.GcInfo=tem;
                                gcResult.otherGc.add(gc);
                            }
                        }
                    }else {
                        //System.out.println(line);// 输出为null，可以注释掉
                    }
                    nextend--;
                }
                nextend--;
                rf.seek(nextend);
                if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
                    String str=new String(rf.readLine().getBytes("ISO-8859-1"), charset);
                    System.out.println("第一行：  "+str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return gcResult;
    }



    /**
     * 分析一个event是否造成image decoding
     * 这里分为三个文件：一个记录event，一个记录decoding，一个记录GC
     * 然后比较时间戳来得到具体信息。
     * 这里有个问题：GC的时间不是秒表时间，但是也没有问题，我们能得到是哪些event触发来GC，给GC标记上event的编号和时间。
     *
     * //TODO 关于event的执行，需要分为三个阶段，存储在三个文件，decoding的时间戳也类似。
     * //TODO 可以这样，将存储的文件作为一个日志文件，然后我们仅仅需要记住每个阶段的时间戳就可以了，然后再进行分开存储。
     * //TODO 现在开始实现吧，还有需要处理好loop的问题，需要一次得到多个loop，然后对于多个loop，如何进行一个处理呢？有没有先后顺序？
     *
     * */

    public static void isDecodingEvent(){
        String timeStamp="1234567890";
        String time_temp=timeStamp.substring(4);
        int timeEvent=Utils.toInt(time_temp);
        System.out.println("timeEvent:  "+timeEvent);

    }


}
