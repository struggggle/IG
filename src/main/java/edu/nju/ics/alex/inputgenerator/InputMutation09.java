package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.nju.ics.alex.inputgenerator.ExplorationSetterKt.getCurrentLayoutExplore;
import static edu.nju.ics.alex.inputgenerator.MainKt.*;
import static edu.nju.ics.alex.inputgenerator.PrefixAndSuffix.obtainHprof;
import static edu.nju.ics.alex.inputgenerator.UZipFile.createfolder;

/**2020-12-23*/

public class InputMutation09 {
    //loop is wrong?
    public static boolean wrongLoop=false;
    public static ArrayList<Integer> executionHistory=new ArrayList<Integer>();
    public static ArrayList<String> generatedTestCase=new ArrayList<>();
    public static ArrayList<Boolean> isEnd=new ArrayList<>();
    public static ArrayList<ArrayList<EventInfo>> currentLoopEvent=new ArrayList<>();
    public static ArrayList<Integer> swipeTag=new ArrayList<>();
    public static int currentExecutionIndex=-1;
    public static String appNameJava="";
    public static String packageNameJava="";
    public static String apptarget_fileName="";
    public static boolean beginToObtainLoopInfo=false;

//    public static void main(String[] args){
//        String widgetCasePath=MainKt.getDirectoryPath()+"/testOutput/wordPress/caseWidget/browseBlog.sh.txt";
//        String layoutsPath=MainKt.getDirectoryPath()+"/testOutput/wordPress/layout/";
//        String caseName="browseBlog.sh";
//        String generateTestCaseFile=MainKt.getDirectoryPath()+"/testOutput/wordPress/generateTestCase/generateTestCasetxt";
//        int[] loopPoints={2,5};
//        appNameJava="wordPress";
//        //generation(appNameJava,widgetCasePath,layoutsPath,caseName,generateTestCaseFile,loopPoints);
//    }

    public static void goToMutate(String target_fileName,String apkName,String appName,String widgetCasePath,String layoutsPath,String caseName,String generateTestCaseFile,ArrayList<Loop> loops,ArrayList<ArrayList<ImageInfo>> event_decoding,String packageName,String apkPath,String activityFile){
        appNameJava=appName;
        apptarget_fileName=target_fileName;
        packageNameJava=packageName;

//        ArrayList<String> activityNames = new ArrayList<>();
//        try{
//            activityNames = readFile(activityFile);
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        //ArrayList<Loop> loopsReal=new ArrayList<Loop>();
        //loopsReal=loops;

//        for(int i=0;i<loops.size();i++) {
//            Loop loop=loops.get(i);
//            int index1=activityNames.get(loop.start-1).indexOf(":");
//            int index2=activityNames.get(loop.end-1).indexOf(":");
//            System.out.println("activity start: " +loop.start+"  "+activityNames.get(loop.start-1));
//            System.out.println("activity end: " +loop.start+"  "+activityNames.get(loop.end-1));
//            String name1=activityNames.get(loop.start-1).substring(index1+1);
//            String name2=activityNames.get(loop.end-1).substring(index2+1);
//            if(name1.equals(name2)){
//                loopsReal.add(loops.get(i));
//            }
//        }

        ArrayList<Loop> loopsMin=new ArrayList<Loop>();
        for(int i=0;i<loops.size();i++) {
            if((loops.get(i).end-loops.get(i).start)>1) {
                loopsMin.add(loops.get(i));
            }
        }

//        for(int i=0;i<loopsMin.size();i++) {
//            int[] loopPoints1 = {loopsMin.get(i).start, loopsMin.get(i).end};
//            for(int j=0;j<loops.size();j++){
//                int[] loopPoints2 = {loops.get(i).start, loops.get(i).end};
//                if(((loopPoints1[0]>loopPoints2[0])&&(loopPoints1[1]<loopPoints2[1]))
//                ||((loopPoints1[0]>loopPoints2[0])&&(loopPoints1[1]<=loopPoints2[1]))
//                ||((loopPoints1[0]>=loopPoints2[0])&&(loopPoints1[1]<loopPoints2[1]))){
//                    loopsMin.remove(i);
//                }
//            }
//        }

        System.out.println("loopsMin:  ");
        for(int i=0;i<loopsMin.size();i++){
            System.out.println("loop:  "+loopsMin.get(i).start+"  "+loopsMin.get(i).end);
        }

        for(int i=loopsMin.size()-1;i>=0;i--) {
        //for(int i=0;i<loopsMin.size();i++) {
//            int start=loopsMin.get(i).start+1;
//            int end=loopsMin.get(i).end+1;
            int start=loopsMin.get(i).start;
            int end=loopsMin.get(i).end;
//            start=4;
//            end=6;
            int[] loopPoints={start,end};
            System.out.println("Begin to try loop:  "+start+"  "+end);

            String zipPath=MainKt.getDirectoryPath()+"/testOutput/temp.zip";
            String parentPath=MainKt.getDirectoryPath()+"/testOutput/";
            String zipFolderName=MainKt.getDirectoryPath()+"/testOutput/temp";
            String newName=MainKt.getDirectoryPath()+"/testOutput/"+appName+"_"+start+"_"+end;

            createfolder(zipPath, zipFolderName,parentPath,  newName);
            appNameJava=appName+"_"+start+"_"+end;

            PrefixAndSuffix.unstallAPK(packageName);
            PrefixAndSuffix.installAPK(apkName,apkPath);

            try {
                generation(appNameJava, widgetCasePath, layoutsPath, caseName, generateTestCaseFile, loopPoints, packageName, apkPath, apkName);
            }catch (Exception e){
                System.out.println("event loop explore and exploit fail");
                e.printStackTrace();
            }
        }
    }

    public static void generation(String appName,String widgetCasePath,String layoutsPath,String caseName,String generateTestCaseFile,int[] loopPoints,String packageName,String apkPath,String apkName){
        appNameJava=appName;
        ArrayList<Integer> mutateTag=preprocessPhase(widgetCasePath,layoutsPath,caseName,loopPoints);
        System.out.println("Pre-process result:");
        for(int i=0;i<mutateTag.size();i++){
            System.out.println(mutateTag.get(i));
        }

        exploreAndExploit(widgetCasePath,layoutsPath,caseName,loopPoints,mutateTag,appName,packageName,apkPath,apkName);
        wrongLoop=false;
        System.out.println("The length of generated test case:   "+generatedTestCase.size());

        try{
            writeArrayList2File(generatedTestCase,generateTestCaseFile);
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static ArrayList<Integer> preprocessPhase(String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints){
        ArrayList<Integer> mutateTag=new ArrayList<>();
        ArrayList<EventInfo> loopEvents=new ArrayList<EventInfo>();
        try{
            List<String> eventLines= Files.readAllLines(Paths.get(widgetCasePath));

            System.out.println("Events in the loop: "+loopPoints[0]+"  "+loopPoints[1]);
            for(int i=0;i<=eventLines.size();i++){
                //TODO change >= and < to > and <=
                if((i>(loopPoints[0]))&&(i<=(loopPoints[1]))){
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.index=i;
                    eventInfo.event=eventLines.get(i);
                    loopEvents.add(eventInfo);
                    System.out.println("index: "+i+"    event: "+eventLines.get(i));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        for(int i=0;i<loopEvents.size();i++) {
            String widgetCase=loopEvents.get(i).event;
            System.out.println("widgetCase:"  +widgetCase);
            if(widgetCase.contains("adb shell")){
                if (widgetCase.contains("adb shell input swipe")) {
                    mutateTag.add(1);
                }else {
                    //do nothing
                    mutateTag.add(0);
                }
            } else {
                //String layoutPath="$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout2.xml";
                System.out.println("index: " + (loopEvents.get(i).index));
                String layoutPath = layoutsPath + caseName + "Layout" + (loopEvents.get(i).index) + ".xml";
                System.out.println("layoutPath:  " + layoutPath);
                ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(layoutPath, widgetCase);
                System.out.println("similarEvents:  " + similarEvents.size());//ok
                ArrayList<String> swipeWidgets = getSwipeWidgets(layoutPath, widgetCase);
                System.out.println("swipeWidgets:  " + swipeWidgets.size());

                if (swipeWidgets.size() > 0 && (similarEvents.size() > 1)) {
                    mutateTag.add(4);//can change widget and swipe
                } else if (similarEvents.size() > 1) {
                    mutateTag.add(3);//can change widget
                } else if (swipeWidgets.size() > 0) {
                    mutateTag.add(2);//can swipe
                } else {
                    mutateTag.add(0);//can not change
                }
            }
        }
        return mutateTag;
    }

    public static ArrayList<String> getSwipeWidgets(String layoutPath, String event){
        System.out.println("Start getSwipeWidgets--------------"+event);
        ArrayList<String> result=new ArrayList<>();
        ArrayList<String> result_temp=new ArrayList<>();
        ArrayList<ArrayList<Integer>> boundResults=new ArrayList<>();
        ArrayList<Integer> bound1=getNumber(event);
        ArrayList<Integer> bound11=new ArrayList<>();
        bound11.add(bound1.get(bound1.size()-4));
        bound11.add(bound1.get(bound1.size()-3));
        bound11.add(bound1.get(bound1.size()-2));
        bound11.add(bound1.get(bound1.size()-1));
        for(int i=0;i<bound11.size();i++){
            System.out.println(bound11.get(i));
        }

        ArrayList<String> widgets=getSwipeWidgets(layoutPath);
        System.out.println("Swipe event number:  "+widgets.size());
        if(widgets.size()>0) {
            String widget;
            for (int i = 0; i < widgets.size(); i++) {
                widget = widgets.get(i);
                System.out.println("Swipe event: " + widget);
                int index = widget.indexOf("#");
                String scrollable = widget.substring(index + 1);
                System.out.println("scrollable: " + scrollable);
                if (scrollable.equals("True")) {
                    ArrayList<Integer> bound2 = getNumber(widget);
                    ArrayList<Integer> bound22 = new ArrayList<>();
                    bound22.add(bound2.get(bound2.size() - 4));
                    bound22.add(bound2.get(bound2.size() - 3));
                    bound22.add(bound2.get(bound2.size() - 2));
                    bound22.add(bound2.get(bound2.size() - 1));
                    for (int k = 0; k < bound22.size(); k++) {
                        System.out.println("bound22: " + bound22.get(k));
                    }
                    if (boundChecking(bound11, bound22)) {
                        result_temp.add(widget);
                        boundResults.add(bound22);
                    }
                }
            }
            System.out.println("result_temp size: " + result_temp.size());
            if(result_temp.size()>0) {
                int index = 0;
                int x = boundResults.get(0).get(1) - boundResults.get(0).get(0);
                int y = boundResults.get(0).get(3) - boundResults.get(0).get(2);
                ;
                for (int i = 0; i < boundResults.size(); i++) {
                    int x1 = boundResults.get(i).get(1) - boundResults.get(i).get(0);
                    int y1 = boundResults.get(i).get(3) - boundResults.get(i).get(2);
                    if ((x >= x1) && (y >= y1)) {
                        index = i;
                    }
                }
                result.add(result_temp.get(index));
            }
        }
        return result;
    }

    public static ArrayList<String> getSwipeWidgets(String layoutPath){
        LayoutTreeSwipe layoutTreeSwipe = new LayoutTreeSwipe(layoutPath);
        ArrayList<String> eventSwipe = new ArrayList<String>();
        Set<String> set = new HashSet<>();

        set=layoutTreeSwipe.eventPositionSwipe;
        eventSwipe.addAll(set);
        return eventSwipe;
    }


    //bound1 is clickk, bound2 is swipe
    public static boolean boundChecking(ArrayList<Integer> bound1, ArrayList<Integer> bound2){//@850,900,200,2100@846,954,126,238 x1,x2,y1,y2
        boolean result;
        if((bound1.get(0)>=bound2.get(0))
                &&(bound1.get(1)<=bound2.get(1))
                &&(bound1.get(2)>=bound2.get(2))
                &&bound1.get(3)<=bound2.get(3)){
            result=true;
        }else {
            result=false;
        }
        return result;
    }


    public static ArrayList<ExploreResult> exploreResults;
    public static int screenCount=0;

    public static boolean exploreAndExploit(String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints,ArrayList<Integer> mutageTag,String appName,String packageName,String apkPath,String apkName){
        exploreResults=new ArrayList<>();

        System.out.println("Explore phase!!!-----------------------");
        ArrayList<EventInfo> prefix=new ArrayList<EventInfo>();
        ArrayList<EventInfo> suffix=new ArrayList<EventInfo>();
        ArrayList<EventInfo> loopEvents=new ArrayList<EventInfo>();
        try{
            List<String> eventLines= Files.readAllLines(Paths.get(widgetCasePath));
            for(int i=0;i<eventLines.size();i++){
                if(i<=loopPoints[0]){
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.index = i;
                    eventInfo.event = eventLines.get(i);
                    eventInfo.targetLayoutIndex=i;
                    prefix.add(eventInfo);
                }else if(i>loopPoints[1]){
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.index=i;
                    eventInfo.event=eventLines.get(i);
                    eventInfo.targetLayoutIndex=i;
                    suffix.add(eventInfo);
                }else {
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.index=i;
                    eventInfo.event=eventLines.get(i);
                    eventInfo.targetLayoutIndex=i;
                    loopEvents.add(eventInfo);

                    ArrayList<EventInfo> eventInfos=new ArrayList<>();
                    eventInfos.add(eventInfo);
                    currentLoopEvent.add(eventInfos);
                }
            }
            System.out.println("prefix: "+prefix.size()+"   loop: "+loopEvents.size()+"  suffix: "+suffix.size());


            String layoutPathExplorePrefix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExplorePrefix/";
            String layoutPathExploreLoop=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreLoop";
            String layoutPathExploreSuffix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreSuffix/";

            String savePath999 = layoutPathExploreLoop + 999 + "/";
            boolean isRight=false;

            String savedImagePath="/data/user/0/"+packageName+"/images/";  ///data/user/0/org.wordpress.android/images/
            String savedDisplayedImagePath="/data/user/0/"+packageName+"/displayedImages/";
            clearImages(savedImagePath, savedDisplayedImagePath);

            /**execute loop prefix*/
            long explore_timeStamp_start=System.currentTimeMillis();
            String timeStampFile=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/Explore_timeStamp_start.txt";
            saveTimeStamp(timeStampFile,String.valueOf(explore_timeStamp_start));
            ArrayList<EventExecutionResult> executionResultsPrefix1 = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);

            String imageFloderPrefix=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/imagesPrefix/";
            String displayedImageFloderPrefix=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/displayedImagesPrefix/";
            pullImages(savedImagePath, savedDisplayedImagePath,imageFloderPrefix,displayedImageFloderPrefix);

            clearImages(savedImagePath, savedDisplayedImagePath);

            /**execute loop*/
            System.out.println("the first run");
            //clear event set
            getEventTimeStampReplay().clear();
            //compare(appName: String,event: String,packageName: String, EN:Int,target_fileName:String)
            //ArrayList<EventInfo> loopEvents,String appName,String event,String packageName, String target_fileName
            ArrayList<EventExecutionResult> executionResultsLoop1 = executeEvents(layoutsPath, caseName, loopEvents, savePath999);
            String imageFloderLoop1=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/imagesLoop1/";
            String displayedImageFloderLoop1=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/displayedImagesLoop1/";
            pullImages(savedImagePath, savedDisplayedImagePath,imageFloderLoop1,displayedImageFloderLoop1);

            /**write event loop to file*/
            StringBuffer content1 = new StringBuffer();
            String loop_events="event_loop1";
            String savePath1=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/loop/";
            for(int j=0;j<getEventTimeStampReplay().size();j++){
                content1.append(getEventTimeStampReplay().get(j).time)
                        .append("-")
                        .append(getEventTimeStampReplay().get(j).index)
                        .append("-")
                        .append(getEventTimeStampReplay().get(j).event)
                        .append("\n");
            }
            String result1=content1.toString();
            Oracles.saveToFile(savePath1, loop_events,result1);

            clearImages(savedImagePath, savedDisplayedImagePath);
            System.out.println("the second run");
            //clear event set
            getEventTimeStampReplay().clear();
            ArrayList<EventExecutionResult> executionResultsLoop2 = executeEvents(layoutsPath, caseName, loopEvents, savePath999);

            String imageFloderLoop2=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/imagesLoop2/";
            String displayedImageFloderLoop2=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/displayedImagesLoop2/";
            pullImages(savedImagePath, savedDisplayedImagePath,imageFloderLoop2,displayedImageFloderLoop2);

            /**write event loop to file*/
            StringBuffer content2 = new StringBuffer();
            String loop_event2="event_loop2";
            String savePath2=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/loop/";
            for(int j=0;j<getEventTimeStampReplay().size();j++){
                content2.append(getEventTimeStampReplay().get(j).time)
                        .append("-")
                        .append(getEventTimeStampReplay().get(j).index)
                        .append("-")
                        .append(getEventTimeStampReplay().get(j).event)
                        .append("\n");
            }
            String result2=content2.toString();
            Oracles.saveToFile(savePath2, loop_event2,result2);

            ArrayList<EventExecutionResult> executionResultsSuffix2 = executeEvents(layoutsPath, caseName, suffix, layoutPathExploreSuffix);

            System.out.println("loop repeated execution result: "+checkChangeResult(executionResultsPrefix1)
                            +"--"+checkChangeResult(executionResultsLoop1)
                            +"--"+checkChangeResult(executionResultsLoop2)
                            +"--"+checkChangeResult(executionResultsSuffix2));
            if (checkChangeResult(executionResultsPrefix1)
                            && checkChangeResult(executionResultsLoop1)
                            && checkChangeResult(executionResultsLoop2)
                            && checkChangeResult(executionResultsSuffix2)
            ) {
                isRight=true;
            }
            System.out.println("loop is right: "+isRight);

            if(isRight) {
                System.out.println("The loop is right!");
                int index = 0;
                recursionMutageTagOne(index, widgetCasePath, layoutsPath, caseName, loopPoints, mutageTag, prefix, loopEvents, suffix, appName, packageName, apkPath, apkName);
                //save explore time consumption
            }
            long explore_timeStamp_end=System.currentTimeMillis();
            String timeStampFile_end=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/Explore_timeStamp_end.txt";
            saveTimeStamp(timeStampFile_end,String.valueOf(explore_timeStamp_end));
        }catch(Exception e){
            e.printStackTrace();
        }

        if(wrongLoop){
            return false;
        }

        System.out.println("Explore results:  "+exploreResults.size());
        for(int i=0;i<exploreResults.size();i++){
            System.out.println("Index:  "+exploreResults.get(i).index);
            System.out.println("swipeDiff:   "+exploreResults.get(i).swipeDiff);
            System.out.println("tag:    "+exploreResults.get(i).tag);
            System.out.println("swipeWidget:    "+exploreResults.get(i).swipeWidget);
            System.out.println("metric:    "+exploreResults.get(i).metric);
            System.out.println();
        }

        boolean canMutate=false;
        for(int i=0;i<exploreResults.size();i++){
            if(exploreResults.get(i).tag>1){//0: can not mutate, change, and swipe，1：swipe
                canMutate=true;
            }
        }

        if(!canMutate){
            System.out.println("canMutate is false!");
            return false;
        }


        /**begin exploit phase*/
        ArrayList<ArrayList<EventInfo>> loopItems=new ArrayList<>();
        for(int m=0;m<loopEvents.size();m++){
            ArrayList<EventInfo> item=new ArrayList<>();
            item.add(loopEvents.get(m));
            loopItems.add(item);
            swipeTag.add(0);//has not begin the first travel
            isEnd.add(false);//false means not swipe to bottom
        }

        System.out.println("Exploit phase!!!-----------------------");
        System.out.println("call exploitPhase: "+apkName+"  loopPoints: "+loopPoints[0]+"  "+loopPoints[1]);
        long exploit_timeStamp_start=System.currentTimeMillis();
        ArrayList<String> performanceTestCase=new ArrayList<>();
        try {
            performanceTestCase=exploitPhase(widgetCasePath, layoutsPath, caseName, prefix, loopItems, suffix, exploreResults, appName, packageName, apkPath, apkName);
        }catch (Exception e){
            e.printStackTrace();
        }
        long exploit_timeStamp_end=System.currentTimeMillis();
        long exploitTimeConsumption=exploit_timeStamp_end-exploit_timeStamp_start;
        String timeStampFile=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/Exploit_timeConsumption.txt";
        saveTimeStamp(timeStampFile,String.valueOf(exploitTimeConsumption));

        /**write event loop start times to file*/
        StringBuffer content = new StringBuffer();
        String event_loop_start_time="event_loop_start_time";
        String savePath=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/loop/";
        for(int j=0;j<loopTimes.size();j++){
            content.append("loop"+j+"-").append(loopTimes.get(j)).append("\n");
        }
        String result=content.toString();
        Oracles.saveToFile(savePath, event_loop_start_time,result);

        /**write event loop to file*/
        StringBuffer content1 = new StringBuffer();
        String loop_events="event_loops";
        String savePath1=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/loop/";
        for(int j=0;j<getEventTimeStampReplay().size();j++){
            content1.append(getEventTimeStampReplay().get(j).time)
                    .append("-")
                    .append(getEventTimeStampReplay().get(j).index)
                    .append("-")
                    .append(getEventTimeStampReplay().get(j).event)
                    .append("\n");
        }
        String result1=content1.toString();
        Oracles.saveToFile(savePath1, loop_events,result1);
        return true;
    }

    public static int imagePullCount=0;
    public static void recursionMutageTagOne(int index,String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints,ArrayList<Integer> mutageTag,ArrayList<EventInfo> prefix,ArrayList<EventInfo> loopEvents,ArrayList<EventInfo> suffix,String appName,String packageName,String apkPath,String apkName) {
        String savedImagePath="/data/user/0/"+packageName+"/images/";  ///data/user/0/org.wordpress.android/images/
        String savedDisplayedImagePath="/data/user/0/"+packageName+"/displayedImages/";

        String imageFloder=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/imagesLoopMutation"+imagePullCount+"/";
        String displayedImageFloder=MainKt.getDirectoryPath()+"/testOutput/"+appName+"/images/imagesLoopMutation"+imagePullCount+"/";
        pullImages(savedImagePath, savedDisplayedImagePath,imageFloder,displayedImageFloder);
        clearImages(savedImagePath, savedDisplayedImagePath);

        System.out.println("----》Run recursionMutageTag:    index="+index);
        if((index)<mutageTag.size()){
            EventInfo eventInfo=loopEvents.get(index);
            String swipeEvent="null";

            if((mutageTag.get(index)==0)||(mutageTag.get(index)==1)){
                System.out.println("(mutageTag.get(index)==0)||(mutageTag.get(index)==1)");
                //继续对下一个index+1的event进行explore
                ExploreResult exploreResult=new ExploreResult();
                exploreResult.index=index;
                exploreResult.tag=(mutageTag.get(index));
                System.out.println("mutageTag.get(index)--add exploreResults");
                exploreResults.add(exploreResult);

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                recursionMutageTagOne(index+1,widgetCasePath,layoutsPath,caseName,loopPoints,mutageTag,prefix,loopEvents,suffix,appName,packageName,apkPath,apkName);//开始下一个

            }else if(mutageTag.get(index)==2){
                System.out.println("mutageTag.get(index)==2");
                boolean canSwipe=false;
                boolean swipeDiff=false;

                String layoutPathExplorePrefix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExplorePrefix/";
                String layoutPathExploreLoop=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreLoop";
                String layoutPathExploreSuffix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreSuffix/";

                String widget_temp="null";
                ArrayList<String> swipeWidgets=new ArrayList<>();
                if(true) {
                    String layoutPathSwipe = layoutsPath + caseName + "Layout" + eventInfo.targetLayoutIndex + ".xml";
                    System.out.println("Explore swipe:  : " + layoutPathSwipe);
                    swipeWidgets = getSwipeWidgets(layoutPathSwipe, loopEvents.get(index).event);
                    System.out.println("Explore phase:  swipeWidgets number: " + swipeWidgets.size());
                    for (int j = 0; j < swipeWidgets.size(); j++) {
                        widget_temp = swipeWidgets.get(j);
                        int[] swipeBounds = getSwipeBounds(widget_temp);
                        swipeEvent = "adb shell input swipe " + swipeBounds[2] + " " + swipeBounds[3] + "  " + swipeBounds[0] + " " + swipeBounds[1] + "  " + 2000;//滑动时间500ms
                        ArrayList<EventExecutionResult> executionResultsPrefix1 = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);//要比较的layout保存在prefix中

                        String savePath1 = layoutPathExploreLoop + 0 + "/";
                        boolean loopExecutionResultSwipe = executeSwipeExplore(layoutsPath, caseName, loopEvents, index, swipeEvent, savePath1);
                        System.out.println("executionResultsLoop1: " +index+"  "+ loopExecutionResultSwipe);
                        if (loopExecutionResultSwipe) {
                            canSwipe = true;
                        } else {
                            canSwipe = false;
                        }
                    }
                }

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);

                /**just explore swipeDiff*/
                if(canSwipe){
                    String layoutPathSwipe=layoutsPath+caseName+"Layout"+eventInfo.targetLayoutIndex+".xml";
                    System.out.println("Explore swipeDiff:  "+layoutPathSwipe);
                    System.out.println("Explore phase:  swipeWidgets number: "+swipeWidgets.size());
                    for(int j=0;j<swipeWidgets.size();j++) {
                        String layout1 = "";
                        String layout2 = "";
                        widget_temp = swipeWidgets.get(j);
                        System.out.println("swipe widget  : " + widget_temp);
                        int[] swipeBounds = getSwipeBounds(widget_temp);
                        swipeEvent = "adb shell input swipe " + swipeBounds[2] + " " + swipeBounds[3] + "  " + swipeBounds[0] + " " + swipeBounds[1] + "  " + 2000;
                        System.out.println("swipe event  : " + swipeEvent);

                        ArrayList<EventExecutionResult> executionResultsPrefix1 = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);
                        String testLayout1 = MainKt.getDirectoryPath()+"/testOutput/" + appNameJava + "/layoutExplorePrefix/" + caseName + "Layout" + prefix.size()+".xml";

                        String savePath1 = layoutPathExploreLoop + 0 + "/";
                        ArrayList<EventExecutionResult> executionResultsLoop1 = executeSwipeExploreDiff(layoutsPath, caseName, prefix,loopEvents, index, swipeEvent, savePath1);
                        if (index > 0) {
                            layout1 = executionResultsLoop1.get(index - 1).layoutPath;
                            System.out.println("lwjlayout1: "+layout1);
                        }
                        String testLayout2 = MainKt.getDirectoryPath()+"/testOutput/" + appNameJava + "/layoutExploreLoop0/" + caseName + "Layout" + (prefix.size() + loopEvents.size()) + ".xml";
                        System.out.println("testLayout2: "+testLayout2);

                        getCurrentLayoutExplore(1);
                        String savePath2 = layoutPathExploreLoop + 1 + "/";//the second loop' layout file save path
                        System.out.println("the second loop path: "+savePath2);
                        ArrayList<EventExecutionResult> executionResultsLoop2 = executeExploreLoop(layoutsPath, caseName, prefix,loopEvents, index, swipeEvent, savePath2);

                        if (index > 0) {
                            layout2 = executionResultsLoop2.get(index - 1).layoutPath;
                            System.out.println("lwjlayout2: "+layout2+"  "+executionResultsLoop2.get(index - 1).isSimilar);
                        }
                        ArrayList<EventExecutionResult> executionResultsSuffix2 = executeEvents(layoutsPath, caseName, suffix, layoutPathExploreSuffix);

                        System.out.println("loop explore result: "+checkChangeResult(executionResultsPrefix1)
                                +"--"+checkChangeResult(executionResultsLoop1)
                                +"--"+checkChangeResult(executionResultsLoop2)
                                +"--"+checkChangeResult(executionResultsSuffix2));
                        if (checkChangeResult(executionResultsPrefix1)
                                && checkChangeResult(executionResultsLoop1)
                                && checkChangeResult(executionResultsLoop2)
                                && checkChangeResult(executionResultsSuffix2)
                        ) {
                            //compare layout
                            if (index == 0) {
                                double percentage=samePercentage(testLayout1, testLayout2);
                                if (percentage<0.9) {
                                    swipeDiff = true;
                                } else {
                                    swipeDiff = false;
                                }
                            } else {
                                double percentage=samePercentage(layout1, layout2);
                                if (percentage<0.9) {
                                    swipeDiff = true;
                                } else {
                                    swipeDiff = false;
                                }
                            }
                        }else {
                            wrongLoop=true;
                            System.out.println("found out a wrong loop: "+loopPoints[0]+"  "+loopPoints[1]);
                        }
                    }
                }else {
                    swipeDiff=false;
                }

                System.out.println("indexExploreResult: "+index+"  "+"canSwipe: "+canSwipe+"     "+"swipeDiff: "+swipeDiff);
                ExploreResult exploreResult=new ExploreResult();
                if(canSwipe){
                    exploreResult.index=index;
                    exploreResult.swipeWidget.add(widget_temp);
                    exploreResult.swipeEvent.add(swipeEvent);
                    exploreResult.tag=2;
                    exploreResult.swipeDiff=swipeDiff;
                }else {
                    exploreResult.index=index;
                    exploreResult.tag=0;
                }
                System.out.println("mutageTag.get(index)--add exploreResults");
                exploreResults.add(exploreResult);
                System.out.println("tag 2: "+exploreResult.tag+"---"+exploreResult.index+"---"+exploreResult.swipeDiff+"---"+exploreResult.metric);
                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                recursionMutageTagOne(index+1,widgetCasePath,layoutsPath,caseName,loopPoints,mutageTag,prefix,loopEvents,suffix,appName,packageName,apkPath,apkName);//开始下一个

            }else if(mutageTag.get(index)==3){//change widget
                boolean canChangeContent=false;
                boolean canChangeStructure=false;

                /**explore widget changing*/

                String layoutPathExplorePrefix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExplorePrefix/";
                String layoutPathExploreLoop=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreLoop";
                String layoutPathExploreSuffix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreSuffix/";

                String layoutPathChange = layoutsPath + caseName + "Layout" + eventInfo.targetLayoutIndex + ".xml";
                System.out.println("layoutPathChange  : " + layoutPathChange);

                String targetWidget=loopEvents.get(index).event;
                ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(layoutPathChange,targetWidget);
                ArrayList<Widget> similarAndContenEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(layoutPathChange, targetWidget);

                System.out.println("Similar widgets:   " + similarEvents.size());
                System.out.println("Similar and content equal widgets:   " + similarAndContenEqualEvents.size());
                boolean considerContent=true;
                if((similarEvents.size()==similarAndContenEqualEvents.size())||similarAndContenEqualEvents.size()<2){
                    considerContent=false;
                }
                System.out.println("considerContent:  "+considerContent);

                ArrayList<Widget> similar=new ArrayList<>();
                ArrayList<Widget> similarAndContent=new ArrayList<>();

                if(considerContent){
                    for(int i=0;i<similarAndContenEqualEvents.size();i++){
                        Widget temp=similarAndContenEqualEvents.get(i);
                        System.out.println("temp: "+temp.widgetPath);
                        String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                        String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
                        if(!(temp2.contains(temp1))){
                            similarAndContent.add(temp);
                        }
                    }
                }

                for(int i=0;i<similarEvents.size();i++){
                    Widget temp=similarEvents.get(i);
                    String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                    String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
                    if(!(temp2.contains(temp1))){
                        System.out.println("temp1 is not temp2");
                        boolean inContent=false;
                        for(int j=0;j<similarAndContent.size();j++){
                            Widget inTemp=similarAndContent.get(j);
                            String temp11=inTemp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                            if(temp11.contains(temp1)){
                                inContent=true;
                            }
                        }
                        if(!inContent){
                            similar.add(temp);
                        }
                    }
                }

                System.out.println("result--similar:"+similar.size()+"     "+"similarAndContent:"+similarAndContent.size());

                /**explore content*/
                if((similarAndContent.size()>0)&&considerContent){
                    System.out.println("explore content");
                    String mutateWidget = similarAndContent.get(0).widgetPath;
                    System.out.println("MutateWidget:   " + mutateWidget);
                    System.out.println("Execution prefix------------");
                    ArrayList<EventExecutionResult> executionResultsPrefix = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);
                    String savePath = layoutPathExploreLoop + 0 + "/";
                    System.out.println("Execution loop------------");
                    boolean loopExecutionResult = executeChangeExplore(layoutsPath, caseName, loopEvents, index, mutateWidget, savePath);

                    ArrayList<EventExecutionResult> executionResultsSuffix=null;
                    if(loopExecutionResult){
                        canChangeContent = true;
                    } else {
                        canChangeContent = false;
                    }
                }else {
                    canChangeContent = false;
                }

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                /**explore similar*/
                if(similar.size()>0){
                    System.out.println("explore similar");
                    String mutateWidgetSimilar = similar.get(0).widgetPath;
                    ArrayList<EventExecutionResult> executionResultsPrefixSimilar = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);//要比较的layout保存在prefix中

                    String savePath = layoutPathExploreLoop + 0 + "/";
                    boolean loopExecutionResultSimilar = executeChangeExplore(layoutsPath, caseName, loopEvents, index, mutateWidgetSimilar, savePath);
                    System.out.println("loopExecutionResultSimilar3:  "+loopExecutionResultSimilar);
                    if(loopExecutionResultSimilar){
                        canChangeStructure = true;
                    } else {
                        canChangeStructure = false;
                    }
                    PrefixAndSuffix.unstallAPK(packageName);
                    PrefixAndSuffix.installAPK(apkName,apkPath);
                }else {
                    canChangeStructure = false;
                }

                //final analysis
                ExploreResult exploreResult=new ExploreResult();
                if(canChangeStructure){
                    exploreResult.index=index;
                    exploreResult.tag=3;
                    exploreResult.metric="Structure";
                }else if(canChangeContent) {
                    exploreResult.index=index;
                    exploreResult.tag=3;
                    exploreResult.metric="Content";
                }else {
                    exploreResult.index=index;
                    exploreResult.tag=0;
                }
                System.out.println("mutageTag.get(index)--add exploreResults");
                exploreResults.add(exploreResult);

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                recursionMutageTagOne(index+1,widgetCasePath,layoutsPath,caseName,loopPoints,mutageTag,prefix,loopEvents,suffix,appName,packageName,apkPath,apkName);//开始下一个

            }else if(mutageTag.get(index)==4){
                boolean canChangeContent=false;
                boolean canChangeStructure=false;
                boolean canSwipe=false;
                boolean swipeDiff=false;

                /**explore wiget changing*/
                String layoutPathExplorePrefix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExplorePrefix/";
                String layoutPathExploreLoop=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreLoop";
                String layoutPathExploreSuffix=MainKt.getDirectoryPath()+"/testOutput/"+appNameJava+"/layoutExploreSuffix/";

                String layoutPathChange = layoutsPath + caseName + "Layout" + eventInfo.targetLayoutIndex + ".xml";
                System.out.println("layoutPathChange  : " + layoutPathChange);

                String targetWidget=loopEvents.get(index).event;
                ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(layoutPathChange,targetWidget);
                ArrayList<Widget> similarAndContenEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(layoutPathChange, targetWidget);

                boolean considerContent=true;
                if((similarEvents.size()==similarAndContenEqualEvents.size())||similarAndContenEqualEvents.size()<2){
                    considerContent=false;
                }
                System.out.println("considerContent:  "+considerContent);

                ArrayList<Widget> similar=new ArrayList<>();
                ArrayList<Widget> similarAndContent=new ArrayList<>();

                if(considerContent){
                    for(int i=0;i<similarAndContenEqualEvents.size();i++){
                        Widget temp=similarAndContenEqualEvents.get(i);
                        System.out.println("temp: "+temp.widgetPath);
                        String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                        String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
                        if(!(temp2.contains(temp1))){
                            similarAndContent.add(temp);
                        }
                    }
                }

                for(int i=0;i<similarEvents.size();i++){
                    Widget temp=similarEvents.get(i);
                    String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                    String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
                    if(!(temp2.contains(temp1))){
                        System.out.println("temp1 is not temp2");
                        boolean inContent=false;
                        for(int j=0;j<similarAndContent.size();j++){
                            Widget inTemp=similarAndContent.get(j);
                            String temp11=inTemp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                            if(temp11.contains(temp1)){
                                inContent=true;
                            }
                        }
                        if(!inContent){
                            similar.add(temp);
                        }
                    }
                }
                System.out.println("result--similar:"+similar.size()+"     "+"similarAndContent:"+similarAndContent.size());

                /**explore content*/
                if((similarAndContent.size()>0)&&considerContent){
                    System.out.println("explore content");
                    String mutateWidget = similarAndContent.get(0).widgetPath;
                    System.out.println("MutateWidget:   " + mutateWidget);
                    System.out.println("Execution prefix------------");
                    ArrayList<EventExecutionResult> executionResultsPrefix = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);//要比较的layout保存在prefix中
                    String savePath = layoutPathExploreLoop + 0 + "/";
                    System.out.println("Execution loop------------");
                    boolean loopExecutionResult = executeChangeExplore(layoutsPath, caseName, loopEvents, index, mutateWidget, savePath);

                    ArrayList<EventExecutionResult> executionResultsSuffix=null;
                   if(loopExecutionResult){
                        canChangeContent = true;
                    } else {
                        canChangeContent = false;
                    }
                }else {
                    canChangeContent = false;
                }

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);

                /**explore similar*/
                if(similar.size()>0){
                    System.out.println("explore similar");
                    String mutateWidgetSimilar = similar.get(0).widgetPath;
                    System.out.println("MutateWidgetSimilar:   " + mutateWidgetSimilar);
                    System.out.println("Execution prefix------------");
                    ArrayList<EventExecutionResult> executionResultsPrefixSimilar = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);//要比较的layout保存在prefix中
                    String savePath = layoutPathExploreLoop + 0 + "/";
                    System.out.println("Execution loop------------");
                    boolean loopExecutionResultSimilar = executeChangeExplore(layoutsPath, caseName, loopEvents, index, mutateWidgetSimilar, savePath);
                    System.out.println("loopExecutionResultSimilar:  "+loopExecutionResultSimilar);

                    if(loopExecutionResultSimilar){
                        canChangeStructure = true;
                    } else {
                        canChangeStructure = false;
                    }
                }else {
                    canChangeStructure = false;
                }

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                /**explore swipe*/
                String widget_temp="null";
                ArrayList<String> swipeWidgets=new ArrayList<>();
                if(canChangeStructure||canChangeContent) {
                    String layoutPathSwipe = layoutsPath + caseName + "Layout" + eventInfo.targetLayoutIndex + ".xml";
                    System.out.println("Explore swipe:  : " + layoutPathSwipe);
                    swipeWidgets = getSwipeWidgets(layoutPathSwipe, loopEvents.get(index).event);
                    System.out.println("Explore phase:  swipeWidgets number: " + swipeWidgets.size());

                    for (int j = 0; j < swipeWidgets.size(); j++) {
                        widget_temp = swipeWidgets.get(j);
                        System.out.println("swipe widget  : " + widget_temp);
                        int[] swipeBounds = getSwipeBounds(widget_temp);
                        swipeEvent = "adb shell input swipe " + swipeBounds[2] + " " + swipeBounds[3] + "  " + swipeBounds[0] + " " + swipeBounds[1] + "  " + 2000;//滑动时间500ms
                        System.out.println("swipe event  : " + swipeEvent);

                        ArrayList<EventExecutionResult> executionResultsPrefix1 = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);//要比较的layout保存在prefix中

                        String savePath1 = layoutPathExploreLoop + 0 + "/";
                        boolean loopExecutionResultSwipe = executeSwipeExplore(layoutsPath, caseName, loopEvents, index, swipeEvent, savePath1);
                        System.out.println("executionResultsLoop1: " +index+"  "+ loopExecutionResultSwipe);
                        if (loopExecutionResultSwipe) {
                            canSwipe = true;
                        } else {
                            canSwipe = false;
                        }
                    }
                }

                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);

                if(canSwipe){
                    String layoutPathSwipe=layoutsPath+caseName+"Layout"+eventInfo.targetLayoutIndex+".xml";
                    System.out.println("Explore swipeDiff:  "+layoutPathSwipe);
                    System.out.println("Explore phase:  swipeWidgets number: "+swipeWidgets.size());
                    for(int j=0;j<swipeWidgets.size();j++) {
                        String layout1 = "";
                        String layout2 = "";
                        widget_temp = swipeWidgets.get(j);

                        System.out.println("swipe widget  : " + widget_temp);
                        int[] swipeBounds = getSwipeBounds(widget_temp);
                        swipeEvent = "adb shell input swipe " + swipeBounds[2] + " " + swipeBounds[3] + "  " + swipeBounds[0] + " " + swipeBounds[1] + "  " + 2000;
                        System.out.println("swipe event  : " + swipeEvent);

                        ArrayList<EventExecutionResult> executionResultsPrefix1 = executeEvents(layoutsPath, caseName, prefix, layoutPathExplorePrefix);
                        String testLayout1 = "$directoryPath/testOutput/" + appNameJava + "/layoutExplorePrefix/" + caseName + "Layout" + prefix.size()+".xml";
                        System.out.println("testLayout1: "+testLayout1);

                        String savePath1 = layoutPathExploreLoop + 0 + "/";
                        System.out.println("the first loop path: "+savePath1);
                        ArrayList<EventExecutionResult> executionResultsLoop1 = executeSwipeExploreDiff(layoutsPath, caseName, prefix,loopEvents, index, swipeEvent, savePath1);
                        if (index > 0) {
                            layout1 = executionResultsLoop1.get(index - 1).layoutPath;
                            System.out.println("lwjlayout1: "+layout1);
                        }
                        String testLayout2 = "$directoryPath/testOutput/" + appNameJava + "/layoutExploreLoop0/" + caseName + "Layout" + (prefix.size() + loopEvents.size()) + ".xml";
                        System.out.println("testLayout2: "+testLayout2);

                        getCurrentLayoutExplore(1);

                        String savePath2 = layoutPathExploreLoop + 1 + "/";
                        ArrayList<EventExecutionResult> executionResultsLoop2 = executeExploreLoop(layoutsPath, caseName, prefix,loopEvents, index, swipeEvent, savePath2);

                        if (index > 0) {
                            layout2 = executionResultsLoop2.get(index - 1).layoutPath;
                            System.out.println("lwjlayout2: "+layout2+"  "+executionResultsLoop2.get(index - 1).isSimilar);
                        }

                        ArrayList<EventExecutionResult> executionResultsSuffix2 = executeEvents(layoutsPath, caseName, suffix, layoutPathExploreSuffix);

                        System.out.println("loop explore result: "+checkChangeResult(executionResultsPrefix1)
                                +"--"+checkChangeResult(executionResultsLoop1)
                                +"--"+checkChangeResult(executionResultsLoop2)
                                +"--"+checkChangeResult(executionResultsSuffix2));
                        if (checkChangeResult(executionResultsPrefix1)
                                && checkChangeResult(executionResultsLoop1)
                                && checkChangeResult(executionResultsLoop2)
                                && checkChangeResult(executionResultsSuffix2)
                        ) {

                            if (index == 0) {
                                double percentage=samePercentage(testLayout1, testLayout2);
                                if (percentage<0.9) {
                                    swipeDiff = true;
                                } else {
                                    swipeDiff = false;
                                }
                            } else {
                                double percentage=samePercentage(layout1, layout2);
                                if (percentage<0.9) {
                                    swipeDiff = true;
                                } else {
                                    swipeDiff = false;
                                }
                            }
                        }else {
                            wrongLoop=true;
                            System.out.println("found out a wrong loop: "+loopPoints[0]+"  "+loopPoints[1]);
                        }
                    }
                }else {
                    swipeDiff=false;
                }

                    System.out.println("indexExploreResult: "+index+"  canChangeContent:  "+canChangeContent+"   canChangeSimilar:  "+canChangeStructure+"     "+"canSwipe: "+canSwipe+"     "+"swipeDiff: "+swipeDiff);
                    ExploreResult exploreResult=new ExploreResult();
                    if(canChangeContent&&canSwipe&&canChangeStructure){
                        exploreResult.index=index;
                        exploreResult.swipeWidget.add(widget_temp);
                        exploreResult.swipeEvent.add(swipeEvent);
                        exploreResult.tag=4;
                        exploreResult.swipeDiff=swipeDiff;
                        exploreResult.metric="Structure";
                    }else if(canChangeContent&&canSwipe) {
                        exploreResult.index = index;
                        exploreResult.swipeWidget.add(widget_temp);
                        exploreResult.swipeEvent.add(swipeEvent);
                        exploreResult.tag = 4;
                        exploreResult.swipeDiff = swipeDiff;
                        exploreResult.metric = "Content";
                    }else if(canChangeStructure&&canSwipe) {
                        exploreResult.index = index;
                        exploreResult.swipeWidget.add(widget_temp);
                        exploreResult.swipeEvent.add(swipeEvent);
                        exploreResult.tag = 4;
                        exploreResult.swipeDiff = swipeDiff;
                        exploreResult.metric = "Structure";
                    }else if(canChangeStructure){
                        exploreResult.index=index;
                        exploreResult.tag=3;
                        exploreResult.metric="Structure";
                    }else if(canChangeContent){
                        exploreResult.index=index;
                        exploreResult.tag=3;
                        exploreResult.metric="Content";
                    }else if(canSwipe){
                        exploreResult.index=index;
                        exploreResult.swipeWidget.add(widget_temp);
                        exploreResult.swipeEvent.add(swipeEvent);
                        exploreResult.tag=2;
                        exploreResult.swipeDiff=swipeDiff;
                    }else {
                        exploreResult.index=index;
                        exploreResult.tag=0;
                    }

                    exploreResults.add(exploreResult);
                    System.out.println("tag 4: "+exploreResult.tag+"---"+exploreResult.index+"---"+exploreResult.swipeDiff+"---"+exploreResult.metric);
                PrefixAndSuffix.unstallAPK(packageName);
                PrefixAndSuffix.installAPK(apkName,apkPath);
                if(!wrongLoop) {
                    recursionMutageTagOne(index + 1, widgetCasePath, layoutsPath, caseName, loopPoints, mutageTag, prefix, loopEvents, suffix, appName, packageName, apkPath,apkName);
                }
            }
        }
    }

    public static ArrayList<ArrayList<Widget>> getContentAndSimilarEventDiff(String targetWidget,String layoutPathChange){
        ArrayList<ArrayList<Widget>> contentAndSimilarEventsDiff=new ArrayList<>();

        ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(layoutPathChange,targetWidget);
        ArrayList<Widget> similarAndContenEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(layoutPathChange, targetWidget);

        boolean considerContent=true;
        if((similarEvents.size()==similarAndContenEqualEvents.size())||similarAndContenEqualEvents.size()<2){
            considerContent=false;
        }
        ArrayList<Widget> similar=new ArrayList<>();
        ArrayList<Widget> similarAndContent=new ArrayList<>();

        if(considerContent){
            for(int i=0;i<similarAndContenEqualEvents.size();i++){
                Widget temp=similarAndContenEqualEvents.get(i);
                System.out.println("temp: "+temp.widgetPath);
                String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
                if(!(temp2.contains(temp1))){
                    similarAndContent.add(temp);
                }
            }
        }

        for(int i=0;i<similarEvents.size();i++){
            Widget temp=similarEvents.get(i);
            String temp1=temp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
            String temp2=targetWidget.substring(0,targetWidget.indexOf("@"));
            if(!(temp2.contains(temp1))){
                System.out.println("temp1 is not temp2");
                boolean inContent=false;
                for(int j=0;j<similarAndContent.size();j++){
                    Widget inTemp=similarAndContent.get(j);
                    String temp11=inTemp.widgetPath.substring(0,temp.widgetPath.indexOf("@"));
                    if(temp11.contains(temp1)){
                        inContent=true;
                    }
                }
                if(!inContent){
                    similar.add(temp);
                }
            }
        }

        contentAndSimilarEventsDiff.add(similarAndContent);
        contentAndSimilarEventsDiff.add(similar);

        return contentAndSimilarEventsDiff;
    }



    public static boolean checkChangeResult(ArrayList<EventExecutionResult> eventExecutionResults){
        for(int i=0;i<eventExecutionResults.size();i++){
            if(eventExecutionResults.get(i).isSimilar.equals("False")){
                return false;
            }
        }
        return true;
    }

    public static int[] getSwipeBounds(String widget_temp){
        int[] result=new int[4];
        //@850,900,200,2100 @846,954,126,238 x1,x2,y1,y2
        //adb shell input swipe 700 1400  700 400    x,y的点

        ArrayList<Integer> bound=getNumber(widget_temp);
        System.out.println("Bound size:  "+bound.size());
        int x=(bound.get(bound.size()-4)+bound.get(bound.size()-3))/2;
        int y1=bound.get(bound.size()-1)-5;//y2
        int y2=bound.get(bound.size()-2)-5;//y1

        result[0]=x;
        result[1]=y2;
        result[2]=x;
        result[3]=y1;

        return result;
    }



    /**
     * DFS travel
     */
    public static ArrayList<String> exploitPhase(String widgetCasePath,String layoutsPath,String caseName,ArrayList<EventInfo> prefix,ArrayList<ArrayList<EventInfo>> loopItems,ArrayList<EventInfo> suffix,ArrayList<ExploreResult> exploreResults,String appName,String packageName,String apkPath,String apkName){

        generatedTestCase=new ArrayList<>();
        boolean stopFlag=false;

        String layoutPathExplorePrefix="$directoryPath/testOutput/"+appNameJava+"/layoutExplorePrefix/";
        String layoutPathExploreLoop="$directoryPath/testOutput/"+appNameJava+"/layoutExploreLoop1";
        String layoutPathExploreSuffix="$directoryPath/testOutput/"+appNameJava+"/layoutExploreSuffix/";

        PrefixAndSuffix.unstallAPK(packageName);
        PrefixAndSuffix.installAPK(apkName,apkPath);
        String savedImagePath="/data/user/0/"+packageName+"/images/";  ///data/user/0/org.wordpress.android/images/
        String savedDisplayedImagePath="/data/user/0/"+packageName+"/displayedImages/";
        clearImages(savedImagePath, savedDisplayedImagePath);

        //clear event set
        getEventTimeStampReplay().clear();
        ArrayList<EventExecutionResult> executionResultsPrefix=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中

        /**execute loop change*/
        int eventIndex=0;
        travelSwipes(stopFlag,eventIndex,layoutsPath,caseName, loopItems,layoutPathExploreLoop,exploreResults,packageName);

        ArrayList<EventExecutionResult> executionResultsSuffix=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
        return generatedTestCase;
    }


    public static long stopTime;
    public static int heapCount=1;
    public static int loopCount=0;
    public static ArrayList<Long> loopTimes=new ArrayList<>();

    public static void travelSwipes(boolean stopFlag, int eventIndex, String layoutsPath,String caseName,ArrayList<ArrayList<EventInfo>> loopItems,String layoutPathExploreLoop,ArrayList<ExploreResult> exploreResults,String packageName) {
        beginToObtainLoopInfo=true;

        currentExecutionIndex=eventIndex-1;//这里说的是执行到loop中的第几个event，那么第一个赋值是-1，表示还没有开始执行loop
        executionHistory.add(currentExecutionIndex);
        System.out.println("ExecutionHistory:    "+executionHistory);
        stopTime=System.currentTimeMillis();

        if (stopFlag) {
            return;
        }

        ExploreResult exploreResult=exploreResults.get(eventIndex);
        /**1：directed swipe and can change widget*/
        if(((exploreResult.tag==4))&&(exploreResult.swipeDiff)){
            if((swipeTag.get(eventIndex))==0){
                getCurrentLayoutExplore(0,appNameJava);
                String currentLayout="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout0.xml";
                String event_temp=loopItems.get(eventIndex).get(0).event;

                if(exploreResult.metric.equals("Structure")){

                    ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout,event_temp);
                    System.out.println("similarEvents:  " + similarEvents.size());

                    for(int i=0;i<similarEvents.size();i++){
                        ArrayList<EventInfo> currentEvent_temp=new ArrayList<>();
                        EventInfo eventInfo=new EventInfo();

                        eventInfo.index=eventIndex;
                        eventInfo.event=similarEvents.get(i).widgetPath;

                        currentEvent_temp.add(eventInfo);
                        currentLoopEvent.set(eventIndex,currentEvent_temp);

                        if(eventIndex==(loopItems.size()-1)){
                            System.out.println("This is the end of the loop, eventIndex="+eventIndex);

                            ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                            for(int k1=currentExecutionIndex+1;k1<loopItems.size();k1++){
                                partLoops.add(currentLoopEvent.get(k1));
                            }

                            String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";

                            ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                            currentExecutionIndex=-1;
                        }else {
                            ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                            for(int k1=currentExecutionIndex+1;k1<=eventIndex;k1++){
                                partLoops.add(currentLoopEvent.get(k1));
                            }
                            String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                            ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                            travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                        }
                        if((i+1)==similarEvents.size()){
                            swipeTag.set(eventIndex,1);
                        }
                    }

                }

                /**if 如果是Content相似*/
                if(exploreResult.metric.equals("Content")){
                    ArrayList<Widget> SimilarAndContentEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(currentLayout,event_temp);
                    for(int i=0;i<SimilarAndContentEqualEvents.size();i++){

                        ArrayList<EventInfo> currentEvent_temp=new ArrayList<>();
                        EventInfo eventInfo=new EventInfo();

                        eventInfo.index=eventIndex;
                        eventInfo.event=SimilarAndContentEqualEvents.get(i).widgetPath;

                        currentEvent_temp.add(eventInfo);
                        currentLoopEvent.set(eventIndex,currentEvent_temp);

                        /**判断是否为loop的最后一个event，如果是，则停止迭代，如果不是，则添加迭代*/
                        if(eventIndex==(loopItems.size()-1)){
                            System.out.println("This is the end of the loop, eventIndex="+eventIndex);

                            /**分析此刻需要被执行的events并执行，从目前执行到的后一个开始遍历*/
                            ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                            for(int k1=currentExecutionIndex+1;k1<loopItems.size();k1++){
                                partLoops.add(currentLoopEvent.get(k1));
                            }

                            String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                            ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                            currentExecutionIndex=-1;
                        }else {
                            ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                            for(int k1=currentExecutionIndex+1;k1<=eventIndex;k1++){
                                partLoops.add(currentLoopEvent.get(k1));
                            }
                            String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                            ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                            travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                        }
                        if((i+1)==SimilarAndContentEqualEvents.size()){
                            swipeTag.set(eventIndex,1);
                        }
                    }
                }
            }

            /**如果初始界面已经被遍历过，加swipe，对当前对layout进行遍历*/
            if((swipeTag.get(eventIndex))==1){
                while(!isEnd.get(eventIndex)){
                    getCurrentLayoutExplore(0,appNameJava);
                    String currentLayout0="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+0+".xml";
                    /**add swipe, before adding swipe, execute all event before swipe*/
                    ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                    for(int k1=currentExecutionIndex+1;k1<eventIndex;k1++){
                        partLoops.add(currentLoopEvent.get(k1));
                    }
                    String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                    String swipeEvent=exploreResult.swipeEvent.get(0);//这里没有问题，如果能滑动，widget path一般都不会发生变化
                    executeSwipeEvent(swipeEvent);

                    getCurrentLayoutExplore(1,appNameJava);//这里1是给的一个编号
                    String currentLayout1="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+1+".xml";

                    boolean isChange=isSameFile(currentLayout0,currentLayout1);
                    if(!(isChange)) {
                        String event_temp1 = loopItems.get(eventIndex).get(0).event;
                        System.out.println("1164:  " + event_temp1 + "    layout:  " + currentLayout1);
                        ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout1, event_temp1);
                        ArrayList<Widget> similarAndContentEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(currentLayout1, event_temp1);
                        System.out.println("Similar Equal Events::  " + similarEvents.size());
                        System.out.println("Similar And Content Equal Events::  " + similarAndContentEqualEvents.size());

                        if(exploreResult.metric.equals("Content")) {
                            for (int i = 0; i < similarAndContentEqualEvents.size(); i++) {
                                ArrayList<EventInfo> currentEvent = new ArrayList<>();
                                EventInfo eventInfo = new EventInfo();
                                eventInfo.index = eventIndex;
                                eventInfo.event = similarAndContentEqualEvents.get(i).widgetPath;
                                currentEvent.add(eventInfo);
                                currentLoopEvent.set(eventIndex, currentEvent);

                                if (eventIndex == (loopItems.size() - 1)) {
                                    System.out.println("This is the end of the loop, eventIndex=" + eventIndex);
                                    ArrayList<ArrayList<EventInfo>> partLoops1 = new ArrayList<>();
                                    for (int k1 = currentExecutionIndex + 1; k1 < loopItems.size(); k1++) {
                                        partLoops1.add(currentLoopEvent.get(k1));
                                    }

                                    String savePath1 = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";
                                    ArrayList<EventExecutionResult> executionResultsLoop21 = executeExploit(layoutsPath, caseName, partLoops1, savePath1);

                                    currentExecutionIndex = -1;
                                } else {
                                    ArrayList<ArrayList<EventInfo>> partLoops2 = new ArrayList<>();
                                    for (int k1 = currentExecutionIndex + 1; k1 <= eventIndex; k1++) {
                                        partLoops2.add(currentLoopEvent.get(k1));
                                    }
                                    String savePath2 = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";
                                    ArrayList<EventExecutionResult> executionResultsLoop22 = executeExploit(layoutsPath, caseName, partLoops2, savePath2);

                                    travelSwipes(stopFlag, eventIndex + 1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                                }
                            }
                        }

                        if(exploreResult.metric.equals("Structure")){
                            for (int i = 0; i < similarEvents.size(); i++) {//遍历替换widget，并修改event
                                ArrayList<EventInfo> currentEvent = new ArrayList<>();
                                EventInfo eventInfo = new EventInfo();
                                eventInfo.index = eventIndex;
                                eventInfo.event = similarEvents.get(i).widgetPath;
                                currentEvent.add(eventInfo);
                                currentLoopEvent.set(eventIndex, currentEvent);

                                if (eventIndex == (loopItems.size() - 1)) {
                                    System.out.println("This is the end of the loop, eventIndex=" + eventIndex);

                                    ArrayList<ArrayList<EventInfo>> partLoops1 = new ArrayList<>();
                                    for (int k1 = currentExecutionIndex + 1; k1 < loopItems.size(); k1++) {
                                        partLoops1.add(currentLoopEvent.get(k1));
                                    }

                                    String savePath1 = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";
                                    ArrayList<EventExecutionResult> executionResultsLoop21 = executeExploit(layoutsPath, caseName, partLoops1, savePath1);

                                    currentExecutionIndex = -1;
                                } else {
                                    ArrayList<ArrayList<EventInfo>> partLoops2 = new ArrayList<>();
                                    for (int k1 = currentExecutionIndex + 1; k1 <= eventIndex; k1++) {
                                        partLoops2.add(currentLoopEvent.get(k1));
                                    }
                                    String savePath2 = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";
                                    ArrayList<EventExecutionResult> executionResultsLoop22 = executeExploit(layoutsPath, caseName, partLoops2, savePath2);

                                    travelSwipes(stopFlag, eventIndex + 1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                                }
                            }
                        }


                    }else {
                        isEnd.set(eventIndex,true);
                        swipeTag.set(eventIndex,0);
                    }
                }
                isEnd.set(eventIndex,false);
            }
        }

        if(((exploreResult.tag==4))&&!(exploreResult.swipeDiff)){
            System.out.println("EventIndex: "+eventIndex+"   "+"((exploreResult.tag==4)||(exploreResult.tag==2))&&(exploreResult.swipeDiff==\"False\"))");
            if((swipeTag.get(eventIndex))==0){
                getCurrentLayoutExplore(0,appNameJava);
                String currentLayout="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout0.xml";
                String event_temp=loopItems.get(eventIndex).get(0).event;
                ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout,event_temp);
                System.out.println("similarEvents:  " + similarEvents.size());

                for(int i=0;i<similarEvents.size();i++){
                    ArrayList<EventInfo> currentEvent_temp=new ArrayList<>();
                    EventInfo eventInfo=new EventInfo();
                    eventInfo.index=eventIndex;
                    eventInfo.event=similarEvents.get(i).widgetPath;
                    currentEvent_temp.add(eventInfo);
                    currentLoopEvent.set(eventIndex,currentEvent_temp);
                    if(eventIndex==(loopItems.size()-1)){
                        System.out.println("This is the end of the loop, eventIndex="+eventIndex);
                        ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                        for(int k1=currentExecutionIndex+1;k1<loopItems.size();k1++){
                            partLoops.add(currentLoopEvent.get(k1));
                        }

                        String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                        ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                        currentExecutionIndex=-1;//还没有执行任何一个loop中的event

                    }else {
                        ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                        for(int k1=currentExecutionIndex+1;k1<=eventIndex;k1++){
                            partLoops.add(currentLoopEvent.get(k1));
                        }
                        String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                        ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                        travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                    }
                    if((i+1)==similarEvents.size()){
                        swipeTag.set(eventIndex,1);
                    }
                }
            }else {
                    System.out.println("EventIndex: "+eventIndex+"   "+"((exploreResult.tag==4)||(exploreResult.tag==2))&&(exploreResult.swipeDiff==\"True\"))");

                    getCurrentLayoutExplore(0,appNameJava);//这里0是给的一个编号
                    String currentLayout0="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+0+".xml";
                    ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                    for(int k1=currentExecutionIndex+1;k1<eventIndex;k1++){
                        partLoops.add(currentLoopEvent.get(k1));
                    }
                    String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                    String swipeEvent=exploreResult.swipeEvent.get(0);//这里没有问题，如果能滑动，widget path一般都不会发生变化
                    for(int k=0;k<swipeTag.get(eventIndex);k++){
                        executeSwipeEvent(swipeEvent);
                    }

                    getCurrentLayoutExplore(1,appNameJava);//这里1是给的一个编号
                    String currentLayout1="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+1+".xml";

                    boolean isSameFile=isSameFile(currentLayout0,currentLayout1);
                    System.out.println("isSameFile: "+isSameFile);
                    if(!isSameFile){
                        int temp=swipeTag.get(eventIndex);
                        swipeTag.set(eventIndex,temp+1);

                        ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout1, exploreResult.widget);
                        System.out.println("similarEvents:  " + similarEvents.size());

                        for(int i=0;i<similarEvents.size();i++){//遍历替换widget，并修改event
                            ArrayList<EventInfo> currentEvent=new ArrayList<>();
                            EventInfo eventInfo=new EventInfo();
                            eventInfo.index=eventIndex;
                            eventInfo.event=similarEvents.get(i).widgetPath;
                            currentEvent.add(eventInfo);
                            currentLoopEvent.set(eventIndex,currentEvent);
                            if(eventIndex==(loopItems.size()-1)){
                                ArrayList<ArrayList<EventInfo>> partLoops1=new ArrayList<>();
                                for(int k1=currentExecutionIndex+1;k1<loopItems.size();k1++){
                                    partLoops.add(currentLoopEvent.get(k1));
                                }

                                String savePath1="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                                ArrayList<EventExecutionResult> executionResultsLoop21=executeExploit(layoutsPath,caseName,partLoops,savePath1);
                                currentExecutionIndex=-1;
                            }else {
                                ArrayList<ArrayList<EventInfo>> partLoops2=new ArrayList<>();
                                for(int k1=currentExecutionIndex+1;k1<=eventIndex;k1++){
                                    partLoops.add(currentLoopEvent.get(k1));
                                }
                                String savePath2="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                                ArrayList<EventExecutionResult> executionResultsLoop22=executeExploit(layoutsPath,caseName,partLoops,savePath2);
                                travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                            }
                        }
                    }else {
                        swipeTag.set(eventIndex,0);
                    }
            }
        }
        if(exploreResult.tag==1){
            System.out.println("EventIndex: "+eventIndex+"   "+"exploreResult.tag==1");
            String swipeEvent=loopItems.get(eventIndex).get(0).event;
            executeSwipeEvent(swipeEvent);//执行滑动
            if(eventIndex==(loopItems.size()-1)){
                boolean checkBottom=false;
                while(!checkBottom){
                    getCurrentLayoutExplore(0,appNameJava);
                    String currentLayout0="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+0+".xml";
                    ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                    for(int k1=currentExecutionIndex+1;k1<eventIndex;k1++){
                        partLoops.add(currentLoopEvent.get(k1));
                    }
                    System.out.println("PartLoops size:  "+partLoops.size());
                    String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);
                    String swipeEvent_temp=exploreResult.swipeEvent.get(0);
                    executeSwipeEvent(swipeEvent_temp);

                    getCurrentLayoutExplore(1,appNameJava);
                    String currentLayout1="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+1+".xml";
                    boolean isSame=isSameFile(currentLayout0,currentLayout1);
                    if(isSame){//如果界面相同
                        checkBottom=true;
                    }else {
                        checkBottom=false;
                    }
                }
                currentExecutionIndex=-1;

            }else {
                boolean checkBottom=false;
                while(!checkBottom){//如果不是底部
                    getCurrentLayoutExplore(0,appNameJava);//这里0是给的一个编号
                    String currentLayout0="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+0+".xml";
                    ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                    for(int k1=currentExecutionIndex+1;k1<eventIndex;k1++){
                        partLoops.add(currentLoopEvent.get(k1));
                    }
                    System.out.println("PartLoops size:  "+partLoops.size());
                    String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                    executeSwipeEvent(swipeEvent);

                    getCurrentLayoutExplore(1,appNameJava);//这里1是给的一个编号
                    String currentLayout1="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore/layout"+1+".xml";
                    boolean isSame=isSameFile(currentLayout0,currentLayout1);
                    if(isSame){//如果界面相同
                        checkBottom=true;
                    }else {
                        checkBottom=false;
                    }
                }
                travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
            }
        }
        if((exploreResult.tag==3)) {
            System.out.println("EventIndex: " + eventIndex + "   " + "(exploreResult.tag==3)");
            getCurrentLayoutExplore(0, appNameJava);
            String currentLayout = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore/layout0.xml";
            String event_temp = loopItems.get(eventIndex).get(0).event;
            if (exploreResult.metric.equals("Structure")) {

                ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout, event_temp);
                System.out.println("similarEvents:  " + similarEvents.size());
                for (int i = 0; i < similarEvents.size(); i++) {
                    ArrayList<EventInfo> currentEvent_temp = new ArrayList<>();
                    EventInfo eventInfo = new EventInfo();

                    eventInfo.index = eventIndex;
                    eventInfo.event = similarEvents.get(i).widgetPath;

                    currentEvent_temp.add(eventInfo);
                    currentLoopEvent.set(eventIndex, currentEvent_temp);
                    if (eventIndex == (loopItems.size() - 1)) {
                        System.out.println("This is the end of the loop, eventIndex=" + eventIndex);
                        ArrayList<ArrayList<EventInfo>> partLoops = new ArrayList<>();
                        for (int k1 = currentExecutionIndex + 1; k1 < loopItems.size(); k1++) {
                            partLoops.add(currentLoopEvent.get(k1));
                        }

                        String savePath = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";

                        ArrayList<EventExecutionResult> executionResultsLoop2 = executeExploit(layoutsPath, caseName, partLoops, savePath);

                        currentExecutionIndex = -1;//还没有执行任何一个loop中的event
                    } else {
                        ArrayList<ArrayList<EventInfo>> partLoops = new ArrayList<>();
                        for (int k1 = currentExecutionIndex + 1; k1 <= eventIndex; k1++) {
                            partLoops.add(currentLoopEvent.get(k1));
                        }
                        String savePath = "$directoryPath/testOutput/" + appNameJava + "/layoutsExplore";
                        ArrayList<EventExecutionResult> executionResultsLoop2 = executeExploit(layoutsPath, caseName, partLoops, savePath);

                        travelSwipes(stopFlag, eventIndex + 1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                    }
                    if ((i + 1) == similarEvents.size()) {
                        swipeTag.set(eventIndex, 1);
                    }
                }

            }
            if(exploreResult.metric.equals("Content")){
                ArrayList<Widget> SimilarAndContentEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(currentLayout,event_temp);
                for(int i=0;i<SimilarAndContentEqualEvents.size();i++){

                    ArrayList<EventInfo> currentEvent_temp=new ArrayList<>();
                    EventInfo eventInfo=new EventInfo();

                    eventInfo.index=eventIndex;
                    eventInfo.event=SimilarAndContentEqualEvents.get(i).widgetPath;

                    currentEvent_temp.add(eventInfo);
                    currentLoopEvent.set(eventIndex,currentEvent_temp);

                    if(eventIndex==(loopItems.size()-1)){
                        ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                        for(int k1=currentExecutionIndex+1;k1<loopItems.size();k1++){
                            partLoops.add(currentLoopEvent.get(k1));
                        }

                        String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                        ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                        /**back to the start point of the loop*/
                        currentExecutionIndex=-1;
                    }else {
                        ArrayList<ArrayList<EventInfo>> partLoops=new ArrayList<>();
                        for(int k1=currentExecutionIndex+1;k1<=eventIndex;k1++){
                            partLoops.add(currentLoopEvent.get(k1));
                        }
                        String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
                        ArrayList<EventExecutionResult> executionResultsLoop2=executeExploit(layoutsPath,caseName,partLoops,savePath);

                        travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
                    }
                    if((i+1)==SimilarAndContentEqualEvents.size()){
                        swipeTag.set(eventIndex,1);
                    }
                }
            }
        }

        if(exploreResult.tag==0){
            EventInfo eventInfo=loopItems.get(eventIndex).get(0);
            String savePath="$directoryPath/testOutput/"+appNameJava+"/layoutsExplore";
            String[] rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);

            if(eventIndex==(loopItems.size()-1)){
                currentExecutionIndex=-1;
                //do nothing
            }else {
                travelSwipes(stopFlag, eventIndex+1, layoutsPath, caseName, loopItems, layoutPathExploreLoop, exploreResults,packageName);
            }
        }
    }



    public static boolean executeChangeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
    {
        boolean result=true;
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
        String[] rightExecution;
        for(int i=0;i<loopEvents.size();i++){
            EventInfo eventInfo=loopEvents.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();

            if(i==index) {
                rightExecution = executeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                System.out.println("rightExecution------------: "+index+"  "+rightExecution[1]);
                if(rightExecution[1].contains("False")){
                    return false;
                }else {
                    return true;
                }
            }else {
                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                if(rightExecution[1].contains("False")){
                    return false;
                }
                executionResults.add(eventExecutionResult);
            }
        }
        return result;
    }


    public static ArrayList<EventExecutionResult> executeExploreLoop(String layoutsPath,String caseName,ArrayList<EventInfo> prefix,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
    {
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
        String[] rightExecution;
        for(int i=0;i<loopEvents.size();i++){//逐个执行event
            EventInfo eventInfo=loopEvents.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();
                rightExecution = executeLoopEventSimilar(prefix.size(),eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                executionResults.add(eventExecutionResult);

        }
        return executionResults;
    }


    public static ArrayList<EventExecutionResult> executeExploit(String layoutsPath,String caseName,ArrayList<ArrayList<EventInfo>> partLoopEvents,String savePath)
    {
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();

        String[] rightExecution;
        for(int i=0;i<partLoopEvents.size();i++){
            ArrayList<EventInfo> eventInfoList=partLoopEvents.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();

            if(eventInfoList.size()==1){
                EventInfo eventInfo=eventInfoList.get(0);
                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                executionResults.add(eventExecutionResult);

            }else {
                for(int j=0;j<eventInfoList.size();j++){
                    EventInfo eventInfo=eventInfoList.get(j);
                    //eventInfo.index就是event在case中的编号
                    rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
                    eventExecutionResult.layoutPath = rightExecution[0];
                    eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                    executionResults.add(eventExecutionResult);
                }
            }
        }
        return executionResults;
    }


    public static boolean executeSwipeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
    {
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
        String[] rightExecution;
        for(int i=0;i<loopEvents.size();i++){
            EventInfo eventInfo=loopEvents.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();

            if(i==index) {
                rightExecution=executeSwipeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                System.out.println("swipe result: "+index+"  "+eventExecutionResult.isSimilar);
                if(rightExecution[1].equals("True")){
                    return true;
                }else {
                    return false;
                }
            }else {
                //eventInfo.index就是event在case中的编号
                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
                eventExecutionResult.layoutPath = rightExecution[0];
                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
                executionResults.add(eventExecutionResult);
            }
        }
        return false;
    }

    public static ArrayList<EventExecutionResult> executeSwipeExploreDiff(String layoutsPath,String caseName,ArrayList<EventInfo> prefix,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
    {
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
        String[] rightExecution;
        for(int i=0;i<loopEvents.size();i++){
            EventInfo eventInfo=loopEvents.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();
            if(i==index) {
                executeSwipeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
            }
            rightExecution = executeLoopEvent(prefix.size(),eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
            eventExecutionResult.layoutPath = rightExecution[0];
            eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
            executionResults.add(eventExecutionResult);
        }
        return executionResults;
    }



    public static ArrayList<EventExecutionResult> executeEvents( String layoutsPath,String caseName,ArrayList<EventInfo> events,String savePath){
        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
        String[] rightExecution;
        for(int i=0;i<events.size();i++){
            EventInfo eventInfo=events.get(i);
            EventExecutionResult eventExecutionResult=new EventExecutionResult();
            System.out.println("eventInfo.index: "+eventInfo.index);
            rightExecution=executeEvent(eventInfo.event,eventInfo.index,caseName,layoutsPath,savePath);
            eventExecutionResult.layoutPath=rightExecution[0];
            eventExecutionResult.isSimilar=rightExecution[1];
        }
        return executionResults;
    }

    public static String[] executeEvent(String event, int index,String caseName,String layoutsPath,String savePath){
        String[] result={"null","null"};
        KUtils kUtils=new KUtils();
        if(event.contains(":")){
            int index_temp=event.indexOf(":");
            System.out.println("index_temp: "+index_temp);
            event = event.substring(index_temp+1);
            System.out.println("event: "+event);
        }else {
            //do nothing
        }
        result=kUtils.executeEvent(event,index,caseName,layoutsPath,savePath);
        generatedTestCase.add(event);
        return result;
    }

    public static String[] executeLoopEvent(int prefixSize,String event, int index,String caseName,String layoutsPath,String savePath){
        String[] result={"null","null"};
        KUtils kUtils=new KUtils();
        if(event.contains("adb shell")){
            int index_temp=event.indexOf(":");
            event = event.substring(index_temp+1);
            System.out.println("event: "+event);
        }else {
            int index_temp=event.indexOf(":");
            event = event.substring(index_temp+1);
            System.out.println("widget event: "+event);

            getCurrentLayoutExplore(1);
            String currentLayout="$directoryPath/testOutput/wordPress/layoutsExplore/layout1.xml";
            ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(currentLayout,event);
            System.out.println("similarEvents: "+index+"  "+similarEvents.size());
            event=similarEvents.get(0).widgetPath;
        }
        System.out.println("liwenjie executeLoopEvent: "+event);
        result=kUtils.executeEventLoop(prefixSize,event,index,caseName,layoutsPath,savePath);
        generatedTestCase.add(event);
        return result;
    }

    public static String[] executeLoopEventSimilar(int prefixSize,String event, int index,String caseName,String layoutsPath,String savePath){
        String[] result={"null","null"};
        KUtils kUtils=new KUtils();
        System.out.println("executeLoopEventSimilar: "+event);
        if(event.contains("adb shell")){
            int index_temp=event.indexOf(":");
            System.out.println("index_temp: "+index_temp);
            event = event.substring(index_temp+1);
            System.out.println("adb event: "+event);
        }else {
            int index_temp=event.indexOf(":");
            System.out.println("index_temp: "+index_temp);
            event = event.substring(index_temp+1);
            System.out.println("widget event: "+event);

            getCurrentLayoutExplore(1);
            String currentLayout="$directoryPath/testOutput/wordPress/layoutsExplore/layout1.xml";
            ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(currentLayout,event);
            System.out.println("loop2: "+index+"  "+similarEvents.size());
            event=similarEvents.get(0).widgetPath;
        }
        System.out.println("liwenjie executeLoopEventSimilar: "+event);
        result=kUtils.executeEventLoop(prefixSize,event,index,caseName,layoutsPath,savePath);
        generatedTestCase.add(event);
        return result;
    }


    public static String[] executeSwipeEvent(String event, int index,String caseName,String layoutsPath,String savePath){
        String[] result={"null","null"};
        KUtils kUtils=new KUtils();
        if(event.contains(":")){
            int index_temp=event.indexOf(":");
            System.out.println("index_temp: "+index_temp);
            event = event.substring(index_temp+1);
            System.out.println("event: "+event);
        }else {
            //do nothing
        }
        result=kUtils.executeSwipeEvent(event,index,caseName,layoutsPath,savePath);
        generatedTestCase.add(event);
        return result;
    }

    public static void executeSwipeEvent(String event){
        KUtils kUtils=new KUtils();
        if(event.contains(":")){
            int index=event.indexOf(":");
            event = event.substring(index+1);
        }else {
        }
        kUtils.executeSwipeEvent(event);
        generatedTestCase.add(event);
    }

    public static boolean positionCompare(int x, int y, ArrayList<Integer> bounds){
        boolean result=false;
        int number=bounds.size();
        System.out.println("bounds:   "+bounds.get(number-4));
        if((x>=bounds.get(number-4))
                &&(x<=bounds.get(number-3))
                &&(y>=bounds.get(number-2))
                &&(y<=bounds.get(number-1))
        ) {
            result = true;
        }
        return result;
    }


    public static ArrayList<Integer> getNumber(String str) {
        ArrayList<Integer> result=new ArrayList<Integer>();
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group());
            result.add(x);
        }
        return result;
    }


    public static void writeArrayList2File(ArrayList<String> eventPosition,String path) throws IOException {
        File fout = new File(path);
        FileOutputStream fos= new FileOutputStream(fout);
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
        for(int i=0;i<eventPosition.size();i++) {
            System.out.println("event position:  "+eventPosition.get(i));
            bw.write(eventPosition.get(i));//写入文件，并加断行
            bw.newLine();
        }
        bw.close();
    }

    public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException{
        ArrayList<String> result=new ArrayList<String>();
        String tem="";
        FileInputStream fileInputStream = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader reader = new BufferedReader(isr, 5*1024*1024);
        while ((tem = reader.readLine()) != null) {
            result.add(tem);
        }
        reader.close();
        isr.close();
        fileInputStream.close();
        return result;
    }

    public static double samePercentage(String layoutPath1,String layoutPath2){
        double result=0.0;
        ArrayList<String> pathElements1=new ArrayList<String>();
        LayoutTreeContentNoIndex layoutTree1 = new LayoutTreeContentNoIndex(layoutPath1);//得到所有节点的path
        pathElements1=layoutTree1.eventPositionContentNoIndex;

        ArrayList<String> pathElements2=new ArrayList<String>();
        LayoutTreeContentNoIndex layoutTree2 = new LayoutTreeContentNoIndex(layoutPath2);//得到所有节点的path
        pathElements2=layoutTree2.eventPositionContentNoIndex;

        System.out.println("call samePercentage(): "+"size1: "+pathElements1.size()+"  size2: "+pathElements2.size());
        int sameNumber=0;
        for(int i=0;i<pathElements1.size();i++){
            for(int j=0;j<pathElements2.size();j++){
                if(pathElements1.get(i).equals(pathElements2.get(j))){
                    System.out.println("same1: "+pathElements1.get(i));
                    System.out.println("same2: "+pathElements2.get(j));
                    sameNumber++;
                }
            }
        }
        double sameNumber1=sameNumber*1.0;
        result=sameNumber1/pathElements1.size();
        return result;
    }

    public static boolean isSameFile(String fileName1,String fileName2){
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;
        try {
            fis1 = new FileInputStream(fileName1);
            fis2 = new FileInputStream(fileName2);

            int len1 = fis1.available();
            int len2 = fis2.available();

            if (len1 == len2) {
                byte[] data1 = new byte[len1];
                byte[] data2 = new byte[len2];
                fis1.read(data1);
                fis2.read(data2);
                boolean eq=Arrays.equals(data1,data2);
                if(eq){
                    return true;
                }else {
                    return false;
                }

            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis1 != null) {
                try {
                    fis1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis2 != null) {
                try {
                    fis2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static void obtainLoopInfo(String packageName){
        String basePath = "$directoryPath/testOutput/" + appNameJava;
        String pidFile = basePath + "/hprof/pid.txt";
        String temp_hprofFile = basePath + "/hprof/heap0.hprof";
        String hprofFile = basePath + "/hprof/heap_" + heapCount + "_.hprof";
        obtainHprof(pidFile, temp_hprofFile, hprofFile, packageName);
        heapCount++;

        long loopStartTime = System.currentTimeMillis();
        loopTimes.add(loopStartTime);

        String savedImagePath = "/data/user/0/" + packageName + "/images/";  ///data/user/0/org.wordpress.android/images/
        String savedDisplayedImagePath = "/data/user/0/" + packageName + "/displayedImages/";
        //pull image to pc
        String imageFloderLoop1 = "$directoryPath/testOutput/" + appNameJava + "/images/Loop_" + loopCount + "/";
        String displayedImageFloderLoop1 = "$directoryPath/testOutput/" + appNameJava + "/images/displayedLoop_" + loopCount + "/";
        loopCount++;
        pullImages(savedImagePath, savedDisplayedImagePath, imageFloderLoop1, displayedImageFloderLoop1);
        //clear images
        clearImages(savedImagePath, savedDisplayedImagePath);
    }


}