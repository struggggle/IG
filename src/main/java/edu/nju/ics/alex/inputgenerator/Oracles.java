package edu.nju.ics.alex.inputgenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.nju.ics.alex.inputgenerator.CompareImages.*;

/**
 * only analyze the file with the name end with .txt
 * detect what：
 * 1)block UI decoding
 * 2)repeated decoding between two event loops
 * 3)ill-resizing
 * 4)continue heap growing
 * */
public class Oracles {
    public static float signalImageSize=839*839;//这个值还可以继续调节，不可能一个event只有一张图片
    public static String prefixPath=MainKt.getDirectoryPath()+"/testOutput/";
    public static boolean detectLoop=false;

    public static void main(String args[]){
        /**这里需要写好分析的参数*/
//        String funcCase="net.nurik.roman.muzei_340300myPhotos-repeat.sh";
//        String loopCase="net.nurik.roman.muzei_340300myPhotos-repeat.sh_4_7";
//        String target_fileName="myPhotos-repeat.sh";

        String funcCase="NewsBlur_v10.2.0Browse_SAVED_STORIES.sh";
        //String loopCase="net.nurik.roman.muzei_340300myPhotos-repeat.sh_4_7";
        String target_fileName="Browse_SAVED_STORIES.sh";

        //just checking function test case
        iidFuncDetection(funcCase,target_fileName);
        //iidDetection(funcCase,loopCase,target_fileName);   //这是最初始的版本
        //这里是要对所有的loop进行遍历
        //iidLoopDetection(funcCase,loopCase,target_fileName);
    }


    public static void iidFuncDetection(String funcCase,String target_fileName){
        String savePath=prefixPath+funcCase+"/result/";

        String uiDecodingFileName="UIDecoding";
        String decodingResizingFileName="DecodingResizing";
        String displayingResizingFileName="DisplayingResizing";
        String repeatedDecodingInAnEventSameLoopName="RepeatedDecodingInAnEventSameLoop";
        String repeatedDecodingInAnEventDifferentLoopName="RepeatedDecodingInAnEventDifferentLoop";

        String eventPath=prefixPath+funcCase+"/timeStamp/"+target_fileName+"_eventRecord.txt";
        String recordImagesPath=prefixPath+funcCase+"/images/recordImages/images/";
        String recordDisplayedImagesPath=prefixPath+funcCase+"/images/recordDisplayedImages/displayedImages/";

        String loopPrefix=prefixPath+funcCase+"/images/recordImages/images/";

        detectUIDecoding(uiDecodingFileName,eventPath,recordImagesPath,savePath);//ok
        System.out.println(">>>>>>>>>>>>>>Finish UI decoding detection!");

        try {
            //TODO 这里仅仅分析检测功能性测试用例的完整执行就可以了，因为mutation部分涉及的代码高概率是相同的代码。
            //detectDecodingResizing(recordImagesPath,savePath,decodingResizingFileName);//ok
            String api="true";
            detectDecodingResizing("true",recordImagesPath,savePath,decodingResizingFileName);//ok--这里仅仅考虑api
            //detectDisplayingResizing(recordDisplayedImagesPath,savePath,displayingResizingFileName);//ok
            System.out.println(">>>>>>>>>>>>>>Finish ill-resizing detection!");

            //TODO 分为两类：同一个loop中存在重复，两个loop之间存在重复：执行相同的loop，重复执行不同的loop，对前后loop的image进行存储，然后进行统计分析。
            /**loop级别的分析*/
            //这里是比较重复执行两个相同的loop
            //detectRepeateDecodingLoop(loopPrefix,imagesLoop2,savePath);
            //这里是比较重复执行两个不同的loop：这里说明如果user在后续进行相似的loop，会频繁地进行repeated decoding
            //detectRepeateDecodingLoop(imagesPrefix,imagesMutateLoop2,savePath);
            /**event级别的分析：得到一个前缀，然后拿每个event进行比较，这里无法就是根据event的时间戳来进行细分，
             * 这里可以记录loop执行开始和结束的时间戳（首先需要对文件夹中的文件进行排序）
             * //TODO 这里的前缀将变为：loopPrefix+preEvents---需要给event添加时间戳
             * */
            //detectRepeateDecodingEvent(loopPrefix,imagesLoop2,savePath);
            //detectRepeateDecodingEvent(imagesPrefix,imagesMutateLoop2,savePath);

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        String name="24-1606289765988-Bitmap-(960,718)-2-(960,718)-decodeStream(is, outPadding, opts)-com.facebook.imagepipeline.platform.DefaultDecoder;decodeBitmap;83951617";
        nameAnalysis(name);
    }


    public static void iidLoopDetection(String funcCase,String loopCase,String target_fileName){
        String savePath=prefixPath+loopCase+"/result/";

        String uiDecodingFileName="UIDecoding";
        String decodingResizingFileName="DecodingResizing";
        String displayingResizingFileName="DisplayingResizing";
        String repeatedDecodingInAnEventSameLoopName="RepeatedDecodingInAnEventSameLoop";
        String repeatedDecodingInAnEventDifferentLoopName="RepeatedDecodingInAnEventDifferentLoop";

        String imagesLoop1=prefixPath+loopCase+"/images/imagesLoop1/images/";
        String imagesLoop2=prefixPath+loopCase+"/images/imagesLoop2/images/";
        String imagesMutateLoop1=prefixPath+loopCase+"/images/Loop_1/images/";
        String imagesMutateLoop2=prefixPath+loopCase+"/images/Loop_2/images/";
        String timeStampLoop1=prefixPath+loopCase+"/loop/event_loop1.txt";
        String timeStampLoop2=prefixPath+loopCase+"/loop/event_loop2.txt";
        String timeStampMutateLoopStartTime=prefixPath+loopCase+"/loop/event_loop_start_time.txt";
        String timeStampMutateLoopsTime=prefixPath+loopCase+"/loop/event_loops.txt";

        String loopPrefix=prefixPath+funcCase+"/images/recordImages/images/";

        //detectUIDecoding(uiDecodingFileName,eventPath,recordImagesPath,savePath);//ok
        //System.out.println(">>>>>>>>>>>>>>Finish UI decoding detection!");

        /**这里分析相同loop存在的重复decoding*/
        detectRepeatedDecodingInAnEventInSameLoop(repeatedDecodingInAnEventSameLoopName,imagesLoop1,imagesLoop2,timeStampLoop1,timeStampLoop2,savePath);//ok
        System.out.println(">>>>>>>>>>>>>>Finish repeated decoding detection in same loop!");

        /**这里分析不同loop存在的重复decoding---这里分析第一个loop和第二个loop*/
        detectRepeatedDecodingInAnEventInDifferentLoop(repeatedDecodingInAnEventDifferentLoopName,imagesMutateLoop1,imagesMutateLoop2,timeStampMutateLoopStartTime,timeStampMutateLoopsTime,savePath);
        System.out.println(">>>>>>>>>>>>>>Finish repeated decoding detection in different loop!");

//        String hprofPath="$directoryPath/testOutput/"+appName+"/hprof/";
//        detectDump(hprofPath);

        try {
            //TODO 这里仅仅分析检测功能性测试用例的完整执行就可以了，因为mutation部分涉及的代码高概率是相同的代码。
            //detectDecodingResizing(recordImagesPath,savePath,decodingResizingFileName);//ok
            //detectDisplayingResizing(recordDisplayedImagesPath,savePath,displayingResizingFileName);//ok
            //System.out.println(">>>>>>>>>>>>>>Finish ill-resizing detection!");

            //TODO 分为两类：同一个loop中存在重复，两个loop之间存在重复：执行相同的loop，重复执行不同的loop，对前后loop的image进行存储，然后进行统计分析。
            /**loop级别的分析*/
            //这里是比较重复执行两个相同的loop
            detectRepeateDecodingLoop(loopPrefix,imagesLoop2,savePath);
            //这里是比较重复执行两个不同的loop：这里说明如果user在后续进行相似的loop，会频繁地进行repeated decoding
            //detectRepeateDecodingLoop(imagesPrefix,imagesMutateLoop2,savePath);
            /**event级别的分析：得到一个前缀，然后拿每个event进行比较，这里无法就是根据event的时间戳来进行细分，
             * 这里可以记录loop执行开始和结束的时间戳（首先需要对文件夹中的文件进行排序）
             * //TODO 这里的前缀将变为：loopPrefix+preEvents---需要给event添加时间戳
             * */
            detectRepeateDecodingEvent(loopPrefix,imagesLoop2,savePath);
            //detectRepeateDecodingEvent(imagesPrefix,imagesMutateLoop2,savePath);

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        String name="24-1606289765988-Bitmap-(960,718)-2-(960,718)-decodeStream(is, outPadding, opts)-com.facebook.imagepipeline.platform.DefaultDecoder;decodeBitmap;83951617";
        nameAnalysis(name);
    }


    public static void iidDetection(String funcCase,String loopCase,String target_fileName){
        String savePath=prefixPath+loopCase+"/result/";

        String uiDecodingFileName="UIDecoding";
        String decodingResizingFileName="DecodingResizing";
        String displayingResizingFileName="DisplayingResizing";
        String repeatedDecodingInAnEventSameLoopName="RepeatedDecodingInAnEventSameLoop";
        String repeatedDecodingInAnEventDifferentLoopName="RepeatedDecodingInAnEventDifferentLoop";

        String eventPath=prefixPath+funcCase+"/timeStamp/"+target_fileName+"_eventRecord.txt";
        String recordImagesPath=prefixPath+funcCase+"/images/recordImages/images/";
        String recordDisplayedImagesPath=prefixPath+funcCase+"/images/recordDisplayedImages/displayedImages/";

        String loopPrefix=prefixPath+funcCase+"/images/recordImages/images/";
        String imagesLoop1=prefixPath+loopCase+"/images/imagesLoop1/images/";
        String imagesLoop2=prefixPath+loopCase+"/images/imagesLoop2/images/";
        String imagesMutateLoop1=prefixPath+loopCase+"/images/Loop_1/images/";
        String imagesMutateLoop2=prefixPath+loopCase+"/images/Loop_2/images/";
        String timeStampLoop1=prefixPath+loopCase+"/loop/event_loop1.txt";
        String timeStampLoop2=prefixPath+loopCase+"/loop/event_loop2.txt";
        String timeStampMutateLoopStartTime=prefixPath+loopCase+"/loop/event_loop_start_time.txt";
        String timeStampMutateLoopsTime=prefixPath+loopCase+"/loop/event_loops.txt";

        detectUIDecoding(uiDecodingFileName,eventPath,recordImagesPath,savePath);//ok
        System.out.println(">>>>>>>>>>>>>>Finish UI decoding detection!");

        /**这里分析相同loop存在的重复decoding*/
        detectRepeatedDecodingInAnEventInSameLoop(repeatedDecodingInAnEventSameLoopName,imagesLoop1,imagesLoop2,timeStampLoop1,timeStampLoop2,savePath);//ok
        System.out.println(">>>>>>>>>>>>>>Finish repeated decoding detection in same loop!");

        /**这里分析不同loop存在的重复decoding---这里分析第一个loop和第二个loop*/
        detectRepeatedDecodingInAnEventInDifferentLoop(repeatedDecodingInAnEventDifferentLoopName,imagesMutateLoop1,imagesMutateLoop2,timeStampMutateLoopStartTime,timeStampMutateLoopsTime,savePath);
        System.out.println(">>>>>>>>>>>>>>Finish repeated decoding detection in different loop!");

//        String hprofPath="$directoryPath/testOutput/"+appName+"/hprof/";
//        detectDump(hprofPath);

        try {
            //TODO 这里仅仅分析检测功能性测试用例的完整执行就可以了，因为mutation部分涉及的代码高概率是相同的代码。
            detectDecodingResizing(recordImagesPath,savePath,decodingResizingFileName);//ok
            detectDisplayingResizing(recordDisplayedImagesPath,savePath,displayingResizingFileName);//ok
            System.out.println(">>>>>>>>>>>>>>Finish ill-resizing detection!");

            //TODO 分为两类：同一个loop中存在重复，两个loop之间存在重复：执行相同的loop，重复执行不同的loop，对前后loop的image进行存储，然后进行统计分析。
            /**loop级别的分析*/
            //这里是比较重复执行两个相同的loop
            //detectRepeateDecodingLoop(loopPrefix,imagesLoop2,savePath);
            //这里是比较重复执行两个不同的loop：这里说明如果user在后续进行相似的loop，会频繁地进行repeated decoding
            //detectRepeateDecodingLoop(imagesPrefix,imagesMutateLoop2,savePath);
            /**event级别的分析：得到一个前缀，然后拿每个event进行比较，这里无法就是根据event的时间戳来进行细分，
             * 这里可以记录loop执行开始和结束的时间戳（首先需要对文件夹中的文件进行排序）
             * //TODO 这里的前缀将变为：loopPrefix+preEvents---需要给event添加时间戳
             * */
            //detectRepeateDecodingEvent(loopPrefix,imagesLoop2,savePath);
            //detectRepeateDecodingEvent(imagesPrefix,imagesMutateLoop2,savePath);

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        String name="24-1606289765988-Bitmap-(960,718)-2-(960,718)-decodeStream(is, outPadding, opts)-com.facebook.imagepipeline.platform.DefaultDecoder;decodeBitmap;83951617";
        nameAnalysis(name);
    }

    public static void detectDump(String hprofsPath){
        Map<Integer,String> hprofs=travelHprof(hprofsPath);
        System.out.println("Hprof map size: "+hprofs.size());
        for(int i=0;i<hprofs.size();i++){
            String hprof=hprofs.get(i+1);
            //TODO 开始进行解析

            //TODO 存储结果到文件   number:i+1, result,
        }
    }

    /**
     * 检测resizing的问题
     * 1）没有resizing
     * 2)resizing的option是一定固定的值
     * 基本思路：
     * 1)首先分析是否包含了特定的api，如果包含，报错。保存文件名，后期再核对是哪个event触发的decoding
     * 2)获得类，方法，行号，sample的值，看看值是不是永远只有一个，如果是，报错，如果不是，则正确。
     * */
    public static void detectDecodingResizing(String api,String filepath,String savePath,String fileName) throws FileNotFoundException, IOException {
        System.out.println("call detectDecodingResizing()");
        ArrayList<String> noResizing=new ArrayList<>();//没有option参数
        ArrayList<String> badResizing=new ArrayList<>();//inSimpleSize没有变化，不存在或为1
        ArrayList<BitmapInfo> bitmapInfos=new ArrayList<>();
        //下面的api是只要调用，就要报告错误的，存储到文件是写入location+image name
        //TODO the api name should be checked
        String[] decodingAPIs={
                "decodeFile(pathName)",
                "decodeFileDescriptor(fd)",
                "decodeByteArray(data, offset,length)",
                "decodeStream(is)",
                "setImageURI",
                "setImageViewUri"
        };

        String[] decodingDrawableAPIs={
                "decodeResource"
        };

        try {
            File file = new File(filepath);
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + filelist[i]);
                    String name=readfile.getName();
                    String signalfile=readfile.getAbsolutePath();
                    System.out.println("Analyze image: "+signalfile);
                    //检查是否存在没有进行resizing的
                    boolean isDoResizing=true;
                    if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))) {
                        for (int j = 0; j < decodingAPIs.length; j++) {
                            if (name.contains(decodingAPIs[j])) {
                                noResizing.add(name);
                                isDoResizing = false;
                            }
                        }
                    }

                    if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))) {
                        for (int j = 0; j < decodingDrawableAPIs.length; j++) {
                            if (searchKeyWordInFile(decodingDrawableAPIs[j], signalfile)) {
                                System.out.println("drawable decoding: "+signalfile);
                                //obtain image size
                                String[] items=name.split("-");
                                String imageSizeStr=items[5];
                                String[] imageSizeStrs = imageSizeStr.split(",");
                                Float imageSize= Float.parseFloat(imageSizeStrs[0]) * Float.parseFloat(imageSizeStrs[1]);
                                System.out.println("image size: "+imageSize);
                                if(imageSize>signalImageSize){
                                    //if image size is large
                                    noResizing.add(name);
                                    isDoResizing = false;
                                }
                            }
                        }
                    }
                    //如果不是
                    /**
                     * 3-1610077917635-Bitmap-(720,720)-4-(168,168-decodeStream(is, outPadding, opts)-Background-android.graphics.BitmapFactory;decodeStream;-1
                     * 3-
                     * 1610077917635-
                     * Bitmap-
                     * (720,720)-
                     * 4-
                     * 168,168-
                     * decodeStream(is, outPadding, opts)-
                     * Background-
                     * android.graphics.BitmapFactory;decodeStream;-1
                     * */
                    //only analyze .txt file, each corresponds to an image
                    if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))){
                        //检查是否存在不合理的resizing，进行信息存储
                        String content = getFileContents(signalfile);
                        System.out.println("do resizing: "+signalfile);
                        String[] items=name.split("-");
                        boolean tag=true;
                        //添加第一个item
                        if(bitmapInfos.size()==0){
                            BitmapInfo bitmapInfo=new BitmapInfo();
                            bitmapInfo.sampleSize.add(items[4]);
                            bitmapInfo.nameSet.add(name);
                            bitmapInfo.stackInfo = items[items.length - 1];
                            bitmapInfo.stackTrace = content;
                            bitmapInfos.add(bitmapInfo);
                        }else {
                            for (int k = 0; k < bitmapInfos.size(); k++) {
                                if (bitmapInfos.get(k).stackInfo.contains(items[items.length - 1])) {//如果标识一致
                                //if (bitmapInfos.get(k).contains(content)) {//if stack traces are same
                                    bitmapInfos.get(k).sampleSize.add(items[4]);
                                    bitmapInfos.get(k).nameSet.add(name);
                                    tag = false;
                                }
                            }
                            if (tag) {
                                BitmapInfo bitmapInfo = new BitmapInfo();
                                bitmapInfo.sampleSize.add(items[4]);
                                bitmapInfo.stackInfo = items[items.length - 1];
                                bitmapInfo.stackTrace = content;
                                bitmapInfo.nameSet.add(name);
                                bitmapInfos.add(bitmapInfo);
                            }
                        }
                    }
                }
            //遍历集合，判断是否存在IID，判断数量小于等于1
            for(int n=0;n<bitmapInfos.size();n++){
                if(!(bitmapInfos.get(n).sampleSize.size()<=1)){
                    badResizing.add(bitmapInfos.get(n).stackInfo);
                }
            }
            //TODO 将检测结果写入文件
            System.out.println("Resizing related IID issue number: " + "NoResizing: "+noResizing.size()+"----"+"BadResizing: "+badResizing.size());
            StringBuffer content = new StringBuffer();
            content.append("NoResizing number:  "+noResizing.size()).append("\n");
            for(int i=0;i<noResizing.size();i++){
                content.append(noResizing.get(i)).append("\n");
            }
            content.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append("\n");
            content.append("BadResizing number:  "+badResizing.size()).append("\n");
            //遍历集合，判断是否存在IID，并写入文件
            for(int n=0;n<bitmapInfos.size();n++){
                if(!(bitmapInfos.get(n).sampleSize.size()>1)){
                    content.append(bitmapInfos.get(n).stackInfo).append("\n");
                    for(int j=0;j<bitmapInfos.get(n).nameSet.size();j++){
                        content.append(bitmapInfos.get(n).nameSet.get(j)).append("\n");
                    }
                }
            }
            //将结果保存到文件
            String result=content.toString();
            saveToFile(savePath,fileName,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void detectDecodingResizing(String filepath,String savePath,String fileName) throws FileNotFoundException, IOException {
        System.out.println("call detectDecodingResizing()");
        ArrayList<String> noResizing=new ArrayList<>();//没有option参数
        ArrayList<String> badResizing=new ArrayList<>();//inSimpleSize没有变化，不存在或为1
        ArrayList<BitmapInfo> bitmapInfos=new ArrayList<>();
        //下面的api是只要调用，就要报告错误的，存储到文件是写入location+image name
        //TODO the api name should be checked
        String[] decodingAPIs={
                "decodeFile(pathName)",
                "decodeFileDescriptor(fd)",
                "decodeByteArray(data, offset,length)",
                "decodeStream(is)",
                "setImageURI",
                "setImageViewUri"
        };

        String[] decodingDrawableAPIs={
                "decodeResource"
        };

        try {
            File file = new File(filepath);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + filelist[i]);
                String name=readfile.getName();
                String signalfile=readfile.getAbsolutePath();
                System.out.println("Analyze image: "+signalfile);
                //检查是否存在没有进行resizing的
                boolean isDoResizing=true;
                if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))) {
                    for (int j = 0; j < decodingAPIs.length; j++) {
                        if (name.contains(decodingAPIs[j])) {
                            noResizing.add(name);
                            isDoResizing = false;
                        }
                    }
                }

                if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))) {
                    for (int j = 0; j < decodingDrawableAPIs.length; j++) {
                        if (searchKeyWordInFile(decodingDrawableAPIs[j], signalfile)) {
                            System.out.println("drawable decoding: "+signalfile);
                            //obtain image size
                            String[] items=name.split("-");
                            String imageSizeStr=items[5];
                            String[] imageSizeStrs = imageSizeStr.split(",");
                            Float imageSize= Float.parseFloat(imageSizeStrs[0]) * Float.parseFloat(imageSizeStrs[1]);
                            System.out.println("image size: "+imageSize);
                            if(imageSize>signalImageSize){
                                //if image size is large
                                noResizing.add(name);
                                isDoResizing = false;
                            }
                        }
                    }
                }
                //如果不是
                /**
                 * 3-1610077917635-Bitmap-(720,720)-4-(168,168-decodeStream(is, outPadding, opts)-Background-android.graphics.BitmapFactory;decodeStream;-1
                 * 3-
                 * 1610077917635-
                 * Bitmap-
                 * (720,720)-
                 * 4-
                 * 168,168-
                 * decodeStream(is, outPadding, opts)-
                 * Background-
                 * android.graphics.BitmapFactory;decodeStream;-1
                 * */
                //only analyze .txt file, each corresponds to an image
                if(isDoResizing&&(!name.contains(".DS_Store"))&&(name.contains(".txt")&&(!name.contains("displaying")))){
                    //检查是否存在不合理的resizing，进行信息存储
                    String content = getFileContents(signalfile);
                    System.out.println("do resizing: "+signalfile);
                    String[] items=name.split("-");
                    boolean tag=true;
                    //添加第一个item
                    if(bitmapInfos.size()==0){
                        BitmapInfo bitmapInfo=new BitmapInfo();
                        bitmapInfo.sampleSize.add(items[4]);
                        bitmapInfo.nameSet.add(name);
                        bitmapInfo.stackInfo = items[items.length - 1];
                        bitmapInfo.stackTrace = content;
                        bitmapInfos.add(bitmapInfo);
                    }else {
                        for (int k = 0; k < bitmapInfos.size(); k++) {
                            //if (bitmapInfos.get(k).stackInfo.contains(items[items.length - 1])) {//如果标识一致
                            if (bitmapInfos.get(k).stackTrace.contains(content)) {//if stack traces are same
                                bitmapInfos.get(k).sampleSize.add(items[4]);
                                bitmapInfos.get(k).nameSet.add(name);
                                tag = false;
                            }
                        }
                        if (tag) {
                            BitmapInfo bitmapInfo = new BitmapInfo();
                            bitmapInfo.sampleSize.add(items[4]);
                            bitmapInfo.stackInfo = items[items.length - 1];
                            bitmapInfo.stackTrace = content;
                            bitmapInfo.nameSet.add(name);
                            bitmapInfos.add(bitmapInfo);
                        }
                    }
                }
            }
            //遍历集合，判断是否存在IID
            for(int n=0;n<bitmapInfos.size();n++){
                if(!(bitmapInfos.get(n).sampleSize.size()>1)){
                    badResizing.add(bitmapInfos.get(n).stackInfo);
                }
            }
            //TODO 将检测结果写入文件
            System.out.println("Resizing related IID issue number: " + "NoResizing: "+noResizing.size()+"----"+"BadResizing: "+badResizing.size());
            StringBuffer content = new StringBuffer();
            content.append("NoResizing number:  "+noResizing.size()).append("\n");
            for(int i=0;i<noResizing.size();i++){
                content.append(noResizing.get(i)).append("\n");
            }
            content.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>").append("\n");
            content.append("BadResizing number:  "+badResizing.size()).append("\n");
            //遍历集合，判断是否存在IID，并写入文件
            for(int n=0;n<bitmapInfos.size();n++){
                if(!(bitmapInfos.get(n).sampleSize.size()>1)){
                    content.append(bitmapInfos.get(n).stackInfo).append("\n");
                    for(int j=0;j<bitmapInfos.get(n).nameSet.size();j++){
                        content.append(bitmapInfos.get(n).nameSet.get(j)).append("\n");
                    }
                }
            }
            //将结果保存到文件
            String result=content.toString();
            saveToFile(savePath,fileName,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean searchKeyWordInFile(String word,String file){
        try {
            String content = getFileContents(file);
            if(content.contains(word)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getFileContents(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String ans = "", line = null;
        while((line = reader.readLine()) != null){
            ans += line + "\r\n";
        }
        reader.close();
        return ans;
    }

    public static void detectDisplayingResizing(String filepath,String savePath,String fileName){
        System.out.println("call detectDisplayingResizing");
        try {
            File file = new File(filepath);
            String[] filelist = file.list();
            StringBuffer content = new StringBuffer();
            content.append("DisplayingResizing:  ").append("\n");
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + filelist[i]);
                String name = readfile.getName();
                /**
                 * 11-
                 * 1610284664543-
                 * displayingDrawable-
                 * 175.0,175.0-
                 * 98,175-
                 * createFromStream(is,srcName)-UI-androidx.appcompat.widget.AppCompatImageView;setImageDrawable;104
                 * */
                if (!name.contains(".DS_Store")&&(name.contains(".jpg"))) {
                    String[] items = name.split("-");
                    String item1 = items[3];
                    String item2 = items[4];
                    String[] item1s = item1.split(",");
                    float w1 = Float.parseFloat(item1s[0]);
                    float w2 = Float.parseFloat(item1s[1]);
                    String[] item2s = item2.split(",");
                    float d1 = Float.parseFloat(item2s[0]);
                    float d2 = Float.parseFloat(item2s[1]);

                    //check
                    if ((w1 > 0) && (w2 > 0)) {
                        if ((w1 * w2) / (d1 * d2) < 0.6) {//控件的大小小于图片的0.6
                            content.append(name).append("\n");
                        }
                    }
                }
            }
            //write to file
            String result=content.toString();
            saveToFile(savePath,fileName,result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
/**
 * 这里直接检查是否存在相同大小的image（不涉及图片的替换）
 * 在得到存在相同大小的图片后，接着比较图片的像素是否一致
 *
 * 这里其实是将相同大小的图片进行分组吗？
 *
 * 应该是这样的，给定一个image path，分析出这个path中有多少张different的图片，然后对每个event做分析，得到每个event中涉及到多少image
 * 的repeated decoding
 * 这里要求对每个event都打时间戳。
 *
 * 这里其实需要分析一个event涉及的image decoding和这个event之前的所有image decoding.
 *
 * //TODO 因为是离线的分析，所以不需要关心时间开销，且loop仅仅分析两类。
 * */
    public static void detectRepeateDecodingLoop(String imagesPrefix,String imagesLoop2,String savePath) throws FileNotFoundException, IOException {
        System.out.println("call detectRepeateDecodingLoop");
        StringBuffer content = new StringBuffer();
        //ArrayList<String> repeatedImages=new ArrayList<>();
        String uniqueImagesFileName="uniqueImages";
        double sumPixel=0.0;
        double repeatedPixel=0.0;
        ArrayList<String> uniqueImages=getUniqueImages(uniqueImagesFileName,imagesPrefix,savePath);

        ArrayList<String> repeatedDecoding=new ArrayList<>();
        try {
            File file = new File(imagesLoop2);
            String[] filelist = file.list();
            //遍历每一张图片
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(imagesLoop2 + filelist[i]);
                String imageFullPath=readfile.getAbsolutePath();
                String imageName=readfile.getName();
                /**读取像素信息*/
                /**
                 * 2-1610516814718-Bitmap-720,720-4-168,168-decodeStream(is, outPadding, opts)-Background-com.bumptech.glide.load.resource.bitmap.Downsampler;decodeStream;583
                 * */
                String[] strings=imageName.split("-");
                String decodedResult=strings[5];
                String[] decodeResultStrs=decodedResult.split(",");
                double pixel=Integer.parseInt(decodeResultStrs[0])*Integer.parseInt(decodeResultStrs[1]);
                sumPixel=sumPixel+pixel;

                /**检查是否在prefix中存在*/
                //比较图片的像素是否完全一致
                for(int j=0;j<uniqueImages.size();j++){
                    String uniqueImage=uniqueImages.get(j);
                    boolean isSame=compareTwoImages(imageFullPath,uniqueImage);
                    if(isSame){
                        //repeated decoding
                        repeatedPixel=repeatedPixel+pixel;
                        repeatedDecoding.add(imageName);
                    }
                }
            }

            //TODO 将检测结果写入文件
            //write to file
            float ratio=(float)repeatedPixel/(float)sumPixel;
            System.out.println("Repeated decoding loop: ratio--" +ratio+"  image number: "+repeatedDecoding.size());
            //if(ratio>0.5) {
            content.append("detectRepeateDecoding_Loop:  "+ratio).append("\n");
            for(int i=0;i<repeatedDecoding.size();i++){
                content.append(repeatedDecoding.get(i)).append("\n");
            }
                String result = content.toString();
                String fileName = "repeatedDecodingLoop";
                saveToFile(savePath, fileName, result);
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ArrayList<String> getUniqueImagesPrefix
    public static void detectRepeateDecodingEvent(String imagesPrefix,String imagesLoop2,String savePath) throws FileNotFoundException, IOException {
        StringBuffer content = new StringBuffer();
        content.append("detectRepeateDecoding_Loop:  ").append("\n");
        //ArrayList<String> repeatedImages=new ArrayList<>();
        String uniqueImagesFileName="uniqueImages";
        double sumPixel=0.0;
        double repeatedPixel=0.0;
        ArrayList<String> uniqueImages=getUniqueImages(uniqueImagesFileName,imagesPrefix,savePath);
        //ArrayList<String> uniqueImagesPrefix=getUniqueImagesPrefix(uniqueImagesFileName,imagesPrefix,savePath);

        ArrayList<String> repeatedDecoding=new ArrayList<>();
        try {
            File file = new File(imagesLoop2);
            String[] filelist = file.list();
            //遍历每一张图片
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(imagesLoop2 + filelist[i]);
                String imageFullPath=readfile.getAbsolutePath();
                String imageName=readfile.getName();
                /**读取像素信息*/
                /**
                 * 2-1610516814718-Bitmap-720,720-4-168,168-decodeStream(is, outPadding, opts)-Background-com.bumptech.glide.load.resource.bitmap.Downsampler;decodeStream;583
                 * */
                String[] strings=imageName.split("-");
                String decodedResult=strings[5];
                String[] decodeResultStrs=decodedResult.split(",");
                double pixel=Integer.parseInt(decodeResultStrs[0])*Integer.parseInt(decodeResultStrs[1]);
                sumPixel=sumPixel+pixel;

                /**检查是否在prefix中存在*/
                //比较图片的像素是否完全一致
                for(int j=0;j<uniqueImages.size();j++){
                    String uniqueImage=uniqueImages.get(j);
                    boolean isSame=compareTwoImages(imageFullPath,uniqueImage);
                    if(isSame){
                        //repeated decoding
                        repeatedPixel=repeatedPixel+pixel;
                        repeatedDecoding.add(imageName);
                        content.append(imageName).append("\n");
                    }
                }
            }

            //TODO 将检测结果写入文件
            System.out.println("Repeated decoding images number: " +repeatedDecoding.size());
            //write to file
            String result=content.toString();
            String fileName="repeatedDecodingLoop";
            saveToFile(savePath,fileName,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //对文件名进行拆解
    //24-1606289765988-Bitmap-(960,718)-2-(960,718)-decodeStream(is, outPadding, opts)-com.facebook.imagepipeline.platform.DefaultDecoder;decodeBitmap;83951617
    public static ArrayList<BitmapInfo> nameAnalysis(String name){
        ArrayList<BitmapInfo> result=new ArrayList<>();
        String[] items=name.split("-");
        System.out.println("item number: "+items.length);
        System.out.println("items[4]: "+items[4]+"--"+items[7]);
        boolean tag=true;
        for(int i=0;i<result.size();i++){
            if(result.get(i).stackInfo.contains(items[7])){
                result.get(i).sampleSize.add(items[4]);
                tag=false;
            }
        }
        if (tag){
            BitmapInfo bitmapInfo=new BitmapInfo();
            bitmapInfo.sampleSize.add(items[4]);
            bitmapInfo.stackInfo=items[7];
            bitmapInfo.nameSet.add(name);
            result.add(bitmapInfo);
        }
        return result;
    }

    /**
     * 这里输出UI decoding发生在功能性测试用例的第几个event中
     * (1)首先遍历图片，看看哪些时间戳的图片是在UI线程上decoding
     * (2)接着遍历event，判断哪些event与UI decoding有关
     * 这里需要得出一个IID报告，得到将结果写入文本文件，说明每个event下面，在UI decoding的数量和图片大小，直接输出image文件的name就可以了。
     * */
    public static void detectUIDecoding(String fileName,String eventPath,String imagesPath,String savePath){
        System.out.println("call detectUIDecoding");
        System.out.println("image path: "+imagesPath);
        StringBuffer content = new StringBuffer();
        float sumPixel=0;
        try {
            //读取event的时间戳
            ArrayList<String> eventStamps = readFile(eventPath);
            System.out.println("eventStamps size: "+eventStamps.size());
            for (int i = 0; i < eventStamps.size(); i++) {
                //开始查找第i个event和第i+1个event之间是否存在UI decoding
                double eventStart;
                double eventEnd;
                if (i < eventStamps.size() - 1) {
                    //将字符串按照"-"进行分割
                    //String[] strs=name.split("\\:");
                    //double imageStamp=Double.parseDouble(strs[1].split("\\:")[1]);
                    eventStart = Double.parseDouble(eventStamps.get(i).split("\\:")[1]);
                    eventEnd = Double.parseDouble(eventStamps.get(i + 1).split("\\:")[1]);
                    System.out.println("event Start:  "+eventStart+"------"+"eventEnd:  "+eventEnd);
                } else {
                    eventStart = Double.parseDouble(eventStamps.get(i).split("\\:")[1]);
                    eventEnd = 9610001526017.0;
                }
                ArrayList<String> images = travelUIImages(eventStart, eventEnd, imagesPath);
                content.append("event" + i +"  timeStamp"+ eventStart+ ":").append("\n");
                for (int j = 0; j < images.size(); j++) {
                    String imageName = images.get(j);
                    String[] imageNameStrs = imageName.split("-");
                    String imageSize = imageNameStrs[5];
                    //System.out.println("lwjimageSize: "+imageSize+"---"+imageName);
                    if (imageSize.contains("decode")) {
                        imageSize = imageNameStrs[3];
                    }
                    String[] imageSizeStrs = imageSize.split(",");
                    sumPixel = sumPixel + Float.parseFloat(imageSizeStrs[0]) * Float.parseFloat(imageSizeStrs[1]);
                    content.append(images.get(j)).append("\n");
                }
                content.append("sumPixel: " + sumPixel + "      839*839: " + 703921).append("\n");
                //clear data
                sumPixel = 0;
            }
        }catch (Exception e) {
        e.printStackTrace();
    }
        String result=content.toString();
        saveToFile(savePath,fileName,result);

    }

    /**
     * 首先获得第二个loop的每个event所涉及到的image set--读取文件，获得时间，然后获得images
     * 然后，将每个event的image set和前一个image set的 unique images进行比较，得到repeated decoding的image set，这里也包括了一个event中的重复的图片
     * 最后，进行结果输出。
     */
    public static void detectRepeatedDecodingInAnEventInSameLoop(String fileName,String imagesLoop1,String imagesLoop2,String timeStampLoop1,String timeStampLoop2,String savePath){
        System.out.println("call detectRepeatedDecodingInAEventInSameLoop");
        StringBuffer content = new StringBuffer();
        float sumPixel=0;//感觉不需要了
        try {
            //读取event的时间戳
            ArrayList<String> eventStamps = readFile(timeStampLoop2);
            System.out.println("eventStamps size: "+eventStamps.size());
            for (int i = 0; i < eventStamps.size(); i++) {
                //开始查找第i个event和第i+1个event之间的image set
                double eventStart;
                double eventEnd;
                if(i<eventStamps.size()-1) {
                    //将字符串按照"-"进行分割
                    eventStart = Double.parseDouble(eventStamps.get(i).split("-")[0]);
                    eventEnd = Double.parseDouble(eventStamps.get(i+1).split("-")[0]);
                }else {
                    eventStart = Double.parseDouble(eventStamps.get(i).split("-")[0]);
                    eventEnd = 9610001526017.0;
                }
                ArrayList<String> images=travelImagesForEvent(eventStart,eventEnd,imagesLoop2);
                //为什么下面的输出为0呢？
                System.out.println("travelImagesForEvent"+i+": "+images.size());
                //TODO 上面是获得了一个event涉及到的image，下面计算repeated时，统计event之前的+loop1中的。
                //得到event之前的image set
                double eventBeforeStart=Double.parseDouble(eventStamps.get(0).split("-")[0]);
                double eventBeforeEnd=Double.parseDouble(eventStamps.get(i).split("-")[0]);
                ArrayList<String> imagesEventBefore=travelImagesForEvent(eventBeforeStart,eventBeforeEnd,imagesLoop2);
                content.append("event"+i+":").append("\n");

                /**接下来计算event之前的+loop1中的unique images*/
                ArrayList<String> uniqueImagesPrefix=getUniqueImagesPrefix(fileName,imagesLoop1,imagesEventBefore,savePath);

                System.out.println("images: "+images.size());
                for(int jj=0;jj<images.size();jj++){
                    System.out.println(images.get(jj));
                }
                System.out.println("uniqueImagesPrefix: "+uniqueImagesPrefix.size());
                for(int jj=0;jj<uniqueImagesPrefix.size();jj++){
                    System.out.println(uniqueImagesPrefix.get(jj));
                }
                /**接下来为每个event计算冗余的decoding*/
                ArrayList<String> repeatedImages=getRepeatedImages(images,uniqueImagesPrefix);
                System.out.println("repeatedImages: "+repeatedImages.size());

                /**统计像素，存储数据*/
                for(int j=0;j<repeatedImages.size();j++){
                    String imageFullName=images.get(j);
                    content.append(imageFullName).append("\n");

                    /**下面开始统计repeated的pixel数量*/
                    System.out.println("lwjimageFullName: "+imageFullName);
                    String[] imageNameStrs=imageFullName.split("-");
                    String imageSize=imageNameStrs[5];
                    //System.out.println("lwjimageSize: "+imageSize+"---"+imageName);
                    if(imageSize.equals("UI")||imageSize.equals("Background")){//对没有opts参数的处理
                        imageSize=imageNameStrs[3];
                    }
                    String[] imageSizeStrs=imageSize.split(",");
                    sumPixel=sumPixel+Float.parseFloat(imageSizeStrs[0])*Float.parseFloat(imageSizeStrs[1]);
                }
                content.append("sumPixel: "+sumPixel+"      400*400: "+160000).append("\n");
                //clear data
                sumPixel=0;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        String result=content.toString();
        saveToFile(savePath,fileName,result);

    }

    public static void detectRepeatedDecodingInAnEventInDifferentLoop(String fileName,String imagesLoop1,String imagesLoop2,String timeStampMutateLoopStartTime,String timeStampMutateLoopsTime,String savePath){
        System.out.println("call detectRepeatedDecodingInAnEventInDifferentLoop");
        StringBuffer content = new StringBuffer();
        float sumPixel=0;
        try {
            ArrayList<String> eventStampsLoopStarts = readFile(timeStampMutateLoopStartTime);
            /**
             * 首先，获得两个loop的start和end
             * 然后，获得event list
             * 然后，根据event list得到对应的图片
             * */
            double loop2Start = Double.parseDouble(eventStampsLoopStarts.get(1).split("-")[1]);
            double loop2End = Double.parseDouble(eventStampsLoopStarts.get(2).split("-")[1]);

            /**获得loop2的event list*/
            ArrayList<String> eventStamps=travelEvents(loop2Start,loop2End,timeStampMutateLoopsTime);
            for(int i = 0; i < eventStamps.size(); i++){
                System.out.println("diff eventStamps: "+eventStamps.get(i));
            }
            /**获得loop2中每个event所对应的images*/
            for (int i = 0; i < eventStamps.size(); i++) {
                //开始查找第i个event和第i+1个event之间的image set
                double eventStart;
                double eventEnd;
                //将字符串按照"-"进行分割，获得时间戳
                if(i<eventStamps.size()-1) {
                    //将字符串按照"-"进行分割
                    eventStart = Double.parseDouble(eventStamps.get(i).split("-")[0]);
                    eventEnd = Double.parseDouble(eventStamps.get(i+1).split("-")[0]);
                }else {
                    eventStart = Double.parseDouble(eventStamps.get(i).split("-")[0]);
                    eventEnd = 9610001526017.0;
                }

                ArrayList<String> images=travelImagesForEvent(eventStart,eventEnd,imagesLoop2);
                System.out.println("travelImagesForEvent"+i+": "+images.size());
                //下面计算repeated时，统计event之前的+loop1中的。
                //得到event之前的image set
                double eventBeforeStart=Double.parseDouble(eventStamps.get(0).split("-")[0]);
                double eventBeforeEnd=Double.parseDouble(eventStamps.get(i).split("-")[0]);
                ArrayList<String> imagesEventBefore=travelImagesForEvent(eventBeforeStart,eventBeforeEnd,imagesLoop2);
                System.out.println("imagesEventBefore"+i+": "+imagesEventBefore.size());
                content.append("event"+i+":").append("\n");

                /**接下来计算event之前的+loop1中的unique images*/
                ArrayList<String> uniqueImagesPrefix=getUniqueImagesPrefix(fileName,imagesLoop1,imagesEventBefore,savePath);
                System.out.println("uniqueImagesPrefix"+i+": "+uniqueImagesPrefix.size());

                /**接下来为每个event计算冗余的decoding*/
                ArrayList<String> repeatedImages=getRepeatedImages(images,uniqueImagesPrefix);
                System.out.println("repeatedImages"+i+": "+repeatedImages.size());

                /**统计像素，存储数据*/
                for(int j=0;j<repeatedImages.size();j++){
                    String imageFullName=repeatedImages.get(j);
                    content.append(imageFullName).append("\n");

                    /**下面开始统计repeated的pixel数量*/
                    System.out.println("lwjimageFullName: "+imageFullName);
                    String[] imageNameStrs=imageFullName.split("-");
                    String imageSize=imageNameStrs[5];
                    //System.out.println("lwjimageSize: "+imageSize+"---"+imageName);
                    if(imageSize.equals("UI")||imageSize.equals("Background")){//对没有opts参数的处理
                        imageSize=imageNameStrs[3];
                    }
                    String[] imageSizeStrs=imageSize.split(",");
                    sumPixel=sumPixel+Float.parseFloat(imageSizeStrs[0])*Float.parseFloat(imageSizeStrs[1]);
                }
                content.append("sumPixel: "+sumPixel+"      400*400: "+160000).append("\n");
                //clear data
                sumPixel=0;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        String result=content.toString();
        saveToFile(savePath,fileName,result);
    }

    public static ArrayList<String> getRepeatedImages(ArrayList<String> images,ArrayList<String> uniqueImagesPrefix){
        ArrayList<String> repeatedImages = new ArrayList<String>();
        for (int i = 0; i < images.size(); i++) {
            boolean isRepeated=isRepeatedImage(images.get(i),uniqueImagesPrefix);
            if(isRepeated){
                System.out.println("getRepeatedImage:  "+images.get(i));
                repeatedImages.add(images.get(i));
            }
        }
        return repeatedImages;
    }

    //计算一个image set中的重复图片
    public static ArrayList<String> getRepeatedImages(ArrayList<String> images){
        ArrayList<String> repeatedImages = new ArrayList<String>();
        ArrayList<String> differentImages = new ArrayList<String>();
        for (int i = 0; i < images.size(); i++) {
            if(differentImages.size()==0){
                System.out.println("lwjfullName:  "+images.get(i));
                differentImages.add(images.get(i));
            }else {
                System.out.println("lwjfullName:  "+images.get(i));
                boolean isDifferent=isDifferentImage(images.get(i),differentImages);
                System.out.println("lwjisDifferentImage:  "+isDifferent);
                if(!isDifferent){
                    repeatedImages.add(images.get(i));
                }else {
                    differentImages.add(images.get(i));
                }
            }
        }
        return repeatedImages;
    }

    /**
     * 遍历图片，看看每个event涉及到哪些图片的decoding
     * */
    public static void groupingImages(String fileName,String eventPath,String imagesPath,String savePath){
        StringBuffer content = new StringBuffer();
        try {
            //读取event的时间戳
            ArrayList<String> eventStamps = readFile(eventPath);
            System.out.println("eventStamps size: "+eventStamps.size());
            for (int i = 0; i < eventStamps.size(); i++) {
                //开始查找第i个event和第i+1个event之间是否存在image decoding
                double eventStart;
                double eventEnd;
                if(i<eventStamps.size()-1) {
                    //将字符串按照"-"进行分割
                    //String[] strs=name.split("\\:");
                    //double imageStamp=Double.parseDouble(strs[1].split("\\:")[1]);
                    eventStart = Double.parseDouble(eventStamps.get(i).split("\\:")[1]);
                    eventEnd = Double.parseDouble(eventStamps.get(i+1).split("\\:")[1]);
                }else {
                    eventStart = Double.parseDouble(eventStamps.get(i).split("\\:")[1]);
                    eventEnd = 9610001526017.0;
                }
                ArrayList<String> images=travelImages(eventStart,eventEnd,imagesPath);
                content.append("event"+i+":").append("\n");
                for(int j=0;j<images.size();j++){
                    content.append(images.get(j)).append("\n");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        String result=content.toString();
        saveToFile(savePath,fileName,result);

    }

    /**
     * 遍历图片，获得path中unique的图片
     * */
    public static ArrayList<String> getUniqueImages(String fileName,String imagesPath,String savePath){
        StringBuffer content = new StringBuffer();
        content.append("Unique images:").append("\n");
        ArrayList<String> uniqueImages=CompareImages.getUniqueImages(imagesPath);
        for(int j=0;j<uniqueImages.size();j++){
            content.append(uniqueImages.get(j)).append("\n");
        }
        String result=content.toString();
        saveToFile(savePath,fileName,result);
        return uniqueImages;
    }

    public static ArrayList<String> getUniqueImagesPrefix(String fileName,String imagesLoop1Path,ArrayList<String> eventBeforeImages,String savePath){
        StringBuffer content = new StringBuffer();
        content.append("Unique images:").append("\n");
        ArrayList<String> uniqueImages=CompareImages.getUniqueImagesPrefix(imagesLoop1Path,eventBeforeImages);
        for(int j=0;j<uniqueImages.size();j++){
            content.append(uniqueImages.get(j)).append("\n");
        }
        String result=content.toString();
        saveToFile(savePath,fileName,result);
        return uniqueImages;
    }

    public static ArrayList<String> travelImages(double eventStart,double eventEnd,String imagesPath){
        ArrayList<String> result=new ArrayList<>();
        try {
            File file = new File(imagesPath);
            String[] filelist = file.list();
            //遍历每一张图片
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(imagesPath + filelist[i]);
                String name=readfile.getName();
                System.out.println("name: "+name);
                if(!name.contains("DS_Store")&&(name.contains(".jpg"))) {
                    //将字符串按照"-"进行分割
                    String[] strs = name.split("\\-");
                    double imageStamp = Double.parseDouble(strs[1]);
                    if (imageStamp > eventStart && (imageStamp <= eventEnd)) {
                        result.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static ArrayList<String> travelUIImages(double eventStart,double eventEnd,String imagesPath){
        ArrayList<String> result=new ArrayList<>();
        try {
            File file = new File(imagesPath);
            String[] filelist = file.list();
            //遍历每一张图片
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(imagesPath + filelist[i]);
                String name=readfile.getName();
                if((!name.contains("DS_Store"))&&(!name.contains("-displaying"))&&(name.contains("-UI-"))&&(name.contains(".jpg"))) {
                    //将字符串按照"-"进行分割
                    System.out.println("image name: "+name);
                    String[] strs = name.split("\\-");
                    double imageStamp = Double.parseDouble(strs[1]);
                    if (imageStamp > eventStart && (imageStamp <= eventEnd)) {
                        System.out.println("imageStamp: "+imageStamp);
                        result.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> travelImagesForEvent(double eventStart,double eventEnd,String imagesPath){
        System.out.println("eventStart: "+eventStart);
        System.out.println("eventEnd: "+eventEnd);
        ArrayList<String> result=new ArrayList<>();
        try {
            File file = new File(imagesPath);
            String[] filelist = file.list();
            //遍历每一张图片
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(imagesPath + filelist[i]);
                String name=readfile.getAbsolutePath();
                System.out.println("name: "+name);
                if((!name.contains("DS_Store"))&&(name.contains(".jpg"))) {
                    //将字符串按照"-"进行分割
                    String[] strs = name.split("-");
                    System.out.println(">>>>lwj: "+strs[1]);
                    double imageStamp = Double.parseDouble(strs[1]);
                    System.out.println("imageStamp: "+imageStamp);
                    if (imageStamp > eventStart && (imageStamp <= eventEnd)) {
                        System.out.println("adding name "+name);
                        result.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> travelEvents(double eventStart,double eventEnd,String eventsPath){
        System.out.println("eventStart: "+eventStart);
        System.out.println("eventEnd: "+eventEnd);
        ArrayList<String> result=new ArrayList<>();
        try {
            ArrayList<String> events = readFile(eventsPath);
            //遍历每一张图片
            for (int i = 0; i < events.size(); i++) {
                String name=events.get(i);
                System.out.println("event name: "+name);
                if((!name.contains("DS_Store"))&&(name.contains(".jpg"))) {
                    //将字符串按照"-"进行分割
                    String[] strs = name.split("-");
                    System.out.println(">>>>lwj: "+strs[1]);
                    double imageStamp = Double.parseDouble(strs[0]);
                    if (imageStamp > eventStart && (imageStamp <= eventEnd)) {
                        System.out.println("adding name "+name);
                        result.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<Integer,String> travelHprof(String hprofPath){
        Map<Integer,String> result = new HashMap<Integer,String>();
        try {
            File file = new File(hprofPath);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(hprofPath + filelist[i]);
                String name=readfile.getName();
                String fullName=readfile.getAbsolutePath();
                System.out.println("name: "+name);
                if((!name.contains("DS_Store"))&&(name.contains("heap"))) {
                    //将字符串按照"-"进行分割
                    String[] strs = name.split("\\-");
                    int heapNumber = Integer.parseInt(strs[1]);
                    result.put(heapNumber,fullName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public static void saveToFile(String savePath,String name,String content){
        if(!fileIsExist(savePath)){
            System.out.println("TargetPath isn't exist");
        }else{
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String saveFileName=savePath + "/"+name+".txt";
            //clear file
            clearInfoForFile(saveFileName);
            File checkFile = new File(saveFileName);
            FileWriter writer = null;
            try {
                //if (!checkFile.exists()) {
                    checkFile.createNewFile();
                //}
                writer = new FileWriter(checkFile, true);
                //clear file

                writer.write(content);;//写入内容
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{
                    if (null != writer)
                        writer.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileIsExist(String fileName){
        File file=new File(fileName);
        if(file.exists())
            return true;
        else{
            return file.mkdirs();
        }
    }

    public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException{
        System.out.println("call readFile()");
        ArrayList<String> result=new ArrayList<String>();
        String tem="";
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(isr, 5*1024*1024);
        while ((tem = reader.readLine()) != null) {
            //System.out.println("tem:   "+tem);
            result.add(tem);
        }
        reader.close();
        isr.close();
        fileInputStream.close();
        return result;
    }
}
