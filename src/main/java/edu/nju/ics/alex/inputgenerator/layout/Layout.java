package edu.nju.ics.alex.inputgenerator.layout;

public class Layout {
    public static void main(String[] args){
        //下面来对一个xml文件进行基本的分析
        //String file="$directoryPath/xml/layout33.xml";//33
        String file="/Users/wenjieli/My-floder/InputGenerator/layoutsStable/layout5.xml";
        LayoutTree layoutTree=new LayoutTree(file);
        int size=layoutTree.getTreeSize();
        int eventSize=layoutTree.eventList.size();
        System.out.println("size: "+size);
        System.out.println("event size: "+eventSize);
        System.out.println(layoutTree.eventList.toString());
    }
}
