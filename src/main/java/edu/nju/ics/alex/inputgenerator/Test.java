package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        //System.out.println("line1: " + new Throwable().getStackTrace()[0].getLineNumber());
        //System.out.println("line2: " + getLineInfo());
        //System.out.println("line3: " + getTraceInfo());

        StackTraceElement ste1 = null;
        StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        int steArrayLength = steArray.length;
        String s = null;
        // output all related info of the existing stack traces
//        if(steArrayLength==0) {
//            System.err.println("No Stack Trace.");
//        } else {
//            for (int i=0; i<steArrayLength; i++) {
//                System.out.println("Stack Trace-" + i);
//                ste1 = steArray[i];
//                s = ste1.getFileName() + ": Line " + ste1.getLineNumber();
//                System.out.println(s);
//            }
//        }
        foo1();

    }

    public static void foo1(){
        foo();
    }
    public static void foo(){
        getTraceInfo();//我会在app中执行这个调用，我需要得到foo在哪里被调用，foo1方法的31行
        //栈区信息是倒着的，我们需要获得第3个就ok了
    }

    /*也就是说，*/
    public static String getTraceInfo(){
        StringBuffer sb0 = new StringBuffer();
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();

        StackTraceElement[] stacks = new Throwable().getStackTrace();
        int stacksLen = stacks.length;
        sb0.append("class: " ).append(stacks[2].getClassName())
                .append("; method: ").append(stacks[2].getMethodName())
                .append("; number: ").append(stacks[2].getLineNumber());

        sb.append("class: " ).append(stacks[1].getClassName())
                .append("; method: ").append(stacks[1].getMethodName())
                .append("; number: ").append(stacks[1].getLineNumber());

        sb1.append("class: " ).append(stacks[0].getClassName())
                .append("; method: ").append(stacks[0].getMethodName())
                .append("; number: ").append(stacks[0].getLineNumber());
        System.out.println("sb0:  "+sb0.toString());
        System.out.println("sb:  "+sb.toString());
        System.out.println("sb1:  "+sb1.toString());
        return sb.toString();
    }

    public static String getLineInfo() {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return ste.getFileName() + ": Line " + ste.getLineNumber();
    }

//    public static void main(String[] args){
//        //1084:  3:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.RelativeLayout[1]/android.view.ViewGroup[0]/android.view.ViewGroup[0]/android.support.v7.widget.RecyclerView[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[3]/android.widget.LinearLayout[0]/android.widget.LinearLayout[1]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@533,624,1057,1114    layout:  $directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml
//        String event_temp1="3:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.RelativeLayout[1]/android.view.ViewGroup[0]/android.view.ViewGroup[0]/android.support.v7.widget.RecyclerView[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[3]/android.widget.LinearLayout[0]/android.widget.LinearLayout[1]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@533,624,1057,1114";
//        String currentLayout1="$directoryPath/testOutput/wordPress/layoutsExplore/layout1.xml";
//        System.out.println("1084:  "+event_temp1+"    layout:  "+currentLayout1);
//        ArrayList<Widget> similarAndContentEqualEvents1 = SimilarEvents.getSimilarAndContentEqualEvents(currentLayout1,event_temp1);
//        System.out.println("size1: "+similarAndContentEqualEvents1.size());
//
//        //1164:  3:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.RelativeLayout[1]/android.view.ViewGroup[0]/android.view.ViewGroup[0]/android.support.v7.widget.RecyclerView[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[3]/android.widget.LinearLayout[0]/android.widget.LinearLayout[1]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@533,624,1057,1114    layout:  $directoryPath/testOutput/wordPress/layoutsExplore/layout1.xml
//
//        String event_temp="3:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.RelativeLayout[1]/android.view.ViewGroup[0]/android.view.ViewGroup[0]/android.support.v7.widget.RecyclerView[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[3]/android.widget.LinearLayout[0]/android.widget.LinearLayout[1]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@533,624,1057,1114";
//        String currentLayout="$directoryPath/testOutput/wordPress/layoutsExplore/layout0.xml";
//        System.out.println("1164:  "+event_temp+"    layout:  "+currentLayout);
//        ArrayList<Widget> similarAndContentEqualEvents = SimilarEvents.getSimilarAndContentEqualEvents(currentLayout,event_temp);
//        System.out.println("size2  :"+similarAndContentEqualEvents.size());
//    }
}
