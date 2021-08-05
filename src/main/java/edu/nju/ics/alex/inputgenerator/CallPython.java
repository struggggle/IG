package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.LayoutTree;
import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeSimilar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import edu.nju.ics.alex.inputgenerator.MainKt;

public class CallPython {
    public static void main(String[] args) {
        String a=MainKt.getDirectoryPath()+"/testOutput/wordPress/layoutsExplore/layout0.xml";
        String b=MainKt.getDirectoryPath()+"/testOutput/wordPress/layoutsExplore/layout1.xml";
        String string=callPython(a,b);
        System.out.println("the result of callPython():   "+string);//这里是对返回结果进行输出


//        //editBlog.shLayout8.xml
//        String str1="$directoryPath/testOutput/wordPressMutation/layout/";
//        String str2="$directoryPath/testOutput/wordPressMutation/layout/eventLayout1.xml";
//        String str3="eventLayout1.xml";
//        String result=compare(str1,str2,str3);
//        System.out.println("result: "+result);

//        Process proc;
//        try {
//            String pythonPath="/Users/wenjieli/My-floder/InputGenerator/python/demo1.py";
//            proc = Runtime.getRuntime().exec("python "+pythonPath);// 执行py文件
//            //用输入输出流来截取结果
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//            in.close();
//            proc.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 这里是之前的版本，对原始的所有的layout进行比较，有一个相似就认为是正确的，明显是有问题的。
     * @param path
     * @param layout
     * @return
     */
    public static String compareExplore(String path, String layout){
        String result="False";
        String temp="False";
        File file=new File(path);
        File[] fs=file.listFiles();
        for(File f:fs){
            String layoutFile=f.getAbsolutePath();
            temp=callPython(layoutFile,layout);
            if(temp.equals("True")){
                result="True";
            }
        }

        return result;
    }

    public static String compare(String path, String layout,String fileName){
        String result="False";
        String temp="False";
        File file=new File(path);
        File[] fs=file.listFiles();
        for(File f:fs){
            String layoutFile=f.getAbsolutePath();
            System.out.println("filename: "+fileName+"------"+"layoutpath: "+layoutFile);
            if(layoutFile.contains(fileName)){
                System.out.println("compare two layoutFile: "+layout+"----"+layoutFile);
                temp=callPython(layoutFile,layout);
                if(temp.equals("True")){
                    result="True";
                }
            }

        }
        return result;
    }

    public static String callPython1(String a,String b){
        String result="";
        String pythonPath=MainKt.getDirectoryPath()+"/python/demo2.py"; //demo2.py";
        try {
            String[] args = new String[] { "python3", pythonPath, String.valueOf(a), String.valueOf(b) };
            Process proc = Runtime.getRuntime().exec(args);// 执行py文件

            //下面的操作是获得返回值？
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result=line;
                //System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String callPython(String layoutPath1,String layoutPath2){
        boolean result=false;
        //首先获得两个layout的所有控件
        LayoutTreeSimilar layoutTree1 = new LayoutTreeSimilar(layoutPath1);
        LayoutTreeSimilar layoutTree2 = new LayoutTreeSimilar(layoutPath2);
        Set<String> set1=uniqueWidget(layoutTree1);
        Set<String> set2=uniqueWidget(layoutTree2);

        if(set1.size()==set2.size()){
            result=isSame(set1,set2);
        }else {
            return "false";
        }
        if(result){
            return "True";
        }else {
            return "False";
        }
    }

    public static Set<String> uniqueWidget(LayoutTreeSimilar layoutTree){
        Set<String> result=new HashSet<String>();
        for(int i=0;i<layoutTree.eventPosition.size();i++){
            String item=layoutTree.eventPosition.get(i);
            result.add(item);
        }
        System.out.println("ArrayList size: "+layoutTree.eventPosition.size());
        System.out.println("SetList size: "+result.size());
        return result;
    }

    public static boolean isSame(Set<String> set1,Set<String> set2) {
        for (String str1 : set1) {
            boolean result_temp = false;
            for (String str2 : set2) {
                System.out.println("isSame str1:"+str1);
                System.out.println("isSame str2:"+str2);
                if (str1.equals(str2)) {
                    result_temp = true;
                }
            }

            if (!result_temp)
                return false;
        }
        return true;
    }
}
