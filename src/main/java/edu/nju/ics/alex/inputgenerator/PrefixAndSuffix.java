package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeContent1;

import java.io.*;
import java.util.ArrayList;

import edu.nju.ics.alex.inputgenerator.ExplorationSetterKt;

import static edu.nju.ics.alex.inputgenerator.ExplorationSetterKt.*;
import static edu.nju.ics.alex.inputgenerator.InputMutation09.appNameJava;

public class PrefixAndSuffix {
    public static void main(String[] args){
        //$directoryPath/testOutput/temp/hprof
        String pidFile="/Users/wenjieli/Desktop/result/pid.txt";
        String temp_hprofFile="/Users/wenjieli/Desktop/result/heap0.hprof";
        String hprofFile="/Users/wenjieli/Desktop/result/heap.hprof";
//        String pidFile="$directoryPath/testOutput/QKSMS-v3.8.1browseContent.sh/hprof/pid.txt";
//        String temp_hprofFile="$directoryPath/testOutput/QKSMS-v3.8.1browseContent.sh/hprof/heap0.hprof";
//        String hprofFile="$directoryPath/testOutput/QKSMS-v3.8.1browseContent.sh/hprof/heap1.hprof";
//
        String packageName="com.moez.QKSMS";

        obtainHprof(pidFile,temp_hprofFile,hprofFile,packageName);
        System.out.println(">>>>>>>>>>>>>finished");
//        String appName="今日头条";
//        String target_fileName="1.sh";
//        System.out.println("Start!");
//        executePrefix(appName,target_fileName);
//        executeSuffix(appName);
    }

    public static void obtainHprof(String pidFile,String temp_hprofFile,String hprofFile,String packageName){
        //fileIsExist(pidFile);
        //GC first
        Runtime.getRuntime().gc();

        System.out.println(">>>>>>>>>>>>>>>Begin to obtain .hprof");
        KUtils kUtils=new KUtils();
        //obtain pid
        //String adb="adb shell ps >   "+pidFile;
        String adb="adb shell ps";
        System.out.println("execution adb: "+adb);
        //kUtils.executeLogEvent(adb);
        String result=executeAdb(adb);
        String savePath="$directoryPath/testOutput/"+appNameJava+"/hprof/";
        String name="pid";
        Oracles.saveToFile(savePath,name,result);
        delay(2000);
        String pid=obtainPid1(pidFile,packageName);
        System.out.println(">>>>>>>>>>>pid: "+pid);

//        //obtain .hprof file
        String tempFile="/data/local/tmp/heap.hprof";
        adb="adb shell am dumpheap "+pid+"  "+tempFile;
        System.out.println("execution adb: "+adb);
        kUtils.executeDumpEvent(adb);
        //MainKt.executeDumpCmd(adb);
        delay(500);

        //pull .hprof file
        adb="adb pull "+tempFile+"  "+temp_hprofFile;
        System.out.println("execution adb: "+adb);
        kUtils.executeDumpEvent(adb);
        //MainKt.executeDumpCmd(adb);
        delay(500);

        adb="/Users/wenjieli/Library/Android/sdk/platform-tools/hprof-conv "+temp_hprofFile+"  "+hprofFile;
        System.out.println("execution adb: "+adb);
        kUtils.executeDumpEvent(adb);
        //MainKt.executeDumpCmd(adb);
        delay(500);
    }

    public static void delay(int time){
        try {
            Thread.sleep(time);//delay 0.5s
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String obtainPid(String str,String packageName){
        Oracles oracles=new Oracles();
        String result="null";
        String[] strings=str.split("\\s+");
        System.out.println("size: "+strings.length);
        for(int i=0;i<strings.length;i++){
            if(strings[i].equals(packageName)){
                result=strings[i-7];
            }
        }
        return result;
    }


    public static String obtainPid1(String savePath,String packageName){
        Oracles oracles=new Oracles();
        String result="null";
        try {
            ArrayList<String> strs=new ArrayList<>();
            strs=oracles.readFile(savePath);
            for(int i=0;i<strs.size();i++){
                if(strs.get(i).contains(packageName)){
                    String str=strs.get(i);
                    str = str.trim();
                    System.out.println("obtainPid: "+str);
                    String[] strings=str.split("\\s+");
                    System.out.println(strings[1]);
                    result=strings[1];
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void fileIsExist(String fileName){
        try {
            File file = new File(fileName);
            if (file.exists()) {
            }else {
                file.createNewFile();;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void unstallAPK(String packageName){
        System.out.println("unstall apk's packageName: "+packageName);
        KUtils kUtils=new KUtils();
        //install apk
        String adb="adb uninstall "+packageName;//adb uninstall org.wordpress.android
        kUtils.executeLogEvent(adb);
    }

    public static void obtainRoot(){
        KUtils kUtils=new KUtils();
        String adb="adb root";
        kUtils.executeLogEvent(adb);
    }

    public static void installAPK(String apkName,String apkPath){
        KUtils kUtils=new KUtils();
        //install apk
        String adb="adb install "+apkPath+apkName+".apk";
        System.out.println("Install apk: "+adb);
        kUtils.executeLogEvent(adb);
    }
//    public static void executePrefix(String appName,String target_fileName){
//        System.out.println("Execute prefix:");
//        KUtils kUtils=new KUtils();
//        //swipe
//        String adb="adb shell input swipe 700 1400  700 400";
//        kUtils.executeLogEvent(adb);
//
//        //click
//        adb=getCommond(appName);
//        kUtils.executeLogEvent(adb);
//
//        waitForStableMT(0);
//        //TODO 在执行的最后一步，获得layout，并得到所有的widget，供实际的分析使用--其实这里仅仅是试用，后续会修改，提供启动app的首页页面信息
//        //界面稳定后，开始保存layout.xml文件，并得到event对应的widget，没有包含坐标
//        getCurrentLayoutStableNoWidget(appName,adb,0,target_fileName);//这里EN就应该是0,对应app启动事件。目前fileName未知
//    }

    //这里的查找涉及到一个循环，第一个页面没有找到，再执行一次滑动，然后继续找
//    public static String getCommond(String text){
//        String result="null";
//        //obtain stable layout snapshot
//        while (true) {
//            KUtils kUtils=new KUtils();
//            kUtils.getCurrentLayoutStable();
//            String layoutPath = "$directoryPath/testOutput/wordPress/layoutsExplore/layout999.xml";
//            result=findTargetCommond(text,layoutPath);
//            if(!(result.contains("null"))){
//                break;
//            }else {
//                //swipe
//                String adb="adb shell input swipe 700 1400  700 400";
//                kUtils.executeLogEvent(adb);
//            }
//        }
//        return result;
//    }

    public static String findTargetCommond(String text,String layoutPath){
        System.out.println("call findTargetCommond()");
        String result="null";
        //获得所有的widgets
        ArrayList<String> eventPositionContent=new ArrayList<>();
        LayoutTreeContent1 layoutTree = new LayoutTreeContent1(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
        eventPositionContent=layoutTree.eventPositionContent;//获得所有包含text的widgets

        //对widgets进行遍历，找到目标
        String widgetText="";
        for(int i=0;i<eventPositionContent.size();i++){//对文本widget进行遍历
            String widgetTemp=eventPositionContent.get(i);
            int index=widgetTemp.indexOf("%");
            widgetText=widgetTemp.substring(index+1);
            if(widgetText.contains(text)){
                //解析坐标，生成点击命令
                result=getTargetPosition(widgetTemp);
                break;
            }
        }
        return result;
    }

    //(ln.indexXpath+"@"+eventLocation(ln)+"%"+ln.text)
    public static String getTargetPosition(String widget){
        String result="null";
        System.out.println("getTargetPosition: "+widget);
        String[] widgetListStrs = widget.split("@");//这里是空格区分
        String widgetSuffix=widgetListStrs[1];
        String[] boundStrs=widgetSuffix.split("%");
        String boundStr=boundStrs[0];
                String[] bounds = boundStr.split(",");//这里是空格区分
                int x1=Integer.parseInt(bounds[0]);//这里是变成int
                int x2=Integer.parseInt(bounds[1]);
                int y1=Integer.parseInt(bounds[2]);
                int y2=Integer.parseInt(bounds[3]);
                int[] result_temp={0,0};
                result_temp[0]=(x1+x2)/2;
                result_temp[1]=(y1+y2)/2;
                System.out.println("position:  "+result_temp[0]+"    "+result_temp[1]);
        result="adb shell input tap "+result_temp[0]+"  "+result_temp[1];
        return result;
    }


//    public static void executeSuffix(String appName){
//        System.out.println("Execute prefix:");
//        KUtils kUtils=new KUtils();
//        String[] adbs={
//                "adb shell input keyevent 4",//返回主界面
//                "adb shell input tap 540 2112",//打开菜单
//                "adb shell input tap 94 1094",//打开应用管理
//                appName,                      //通过app的名字进行查找，点击中间位置，得到一个菜单
//                "卸载",                            //点击菜单中的“卸载”选项
//                "adb shell input tap 905 1300",  //点击“确定”来删除app
//                "adb shell input keyevent 4",    //返回设置页面
//                "adb shell input tap 111 274",   //点击添加应用
//                appName,                       //对页面上的widget进行查找，找到对应的app
//                "adb shell input tap 540 2215",  //接下来点击“安装”--固定点
//                "adb shell input tap 674 1339",  //选择“VIR...“安装
//                "完成"   //等待安装结束，会比较久的等待，点击完成------返回到了主页面
//        };
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[0]);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[1]);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[2]);
//
//        kUtils.getCurrentLayoutStable();
//        String adb=getCommond(adbs[3]);
//        kUtils.executeLogEvent(adb);
//
//        kUtils.getCurrentLayoutStable();
//        adb=getCommond(adbs[4]);
//        kUtils.executeLogEvent(adb);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[5]);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[6]);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[7]);
//
//        kUtils.getCurrentLayoutStable();
//        adb=getCommond(adbs[8]);
//        kUtils.executeLogEvent(adb);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[9]);
//
//        kUtils.getCurrentLayoutStable();
//        kUtils.executeLogEvent(adbs[10]);
//
//        kUtils.getCurrentLayoutStable();
//        adb=getCommond(adbs[11]);
//        kUtils.executeLogEvent(adb);
//    }


    public static String executeAdb(String cmd) {
        //String adbHome="/Users/wenjieli/Library/Android/sdk/platform-tools/";
        Process process;
        String result="";
        try {
            process=Runtime.getRuntime().exec(cmd);
            result=InputStream2String(process.getInputStream());
            System.out.println("result: "+result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String InputStream2String(InputStream inputStream) {
        String result = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String temp = "";
            while ((temp = br.readLine()) != null) {
                result += temp + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
