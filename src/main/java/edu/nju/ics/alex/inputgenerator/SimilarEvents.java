package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.LayoutTree;
import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeContent1;

import java.util.ArrayList;

public class SimilarEvents {
    public static void main(String[] args){
        //第3个event产生第3个layout，需要在第2个layout中寻找相似第event
        String layoutPath=MainKt.getDirectoryPath()+"/testOutput/wordPress/layout/browseBlog.shLayout2.xml";
        String widgetPath="3:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.RelativeLayout[1]/android.view.ViewGroup[0]/android.view.ViewGroup[0]/android.support.v7.widget.RecyclerView[0]/android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[3]/android.widget.LinearLayout[0]/android.widget.LinearLayout[1]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@533,624,1057,1114";


        //String layoutPath="$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout1.xml";
        //String widgetPath="2:android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.RelativeLayout[0]/android.support.v4.view.ViewPager[1]/android.widget.FrameLayout[0]/android.widget.ScrollView[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[4]/android.widget.LinearLayout[0]/android.widget.TextView[1]/@196,521,1179,1339";


        ArrayList<Widget> similarEvents=getSimilarEvents(layoutPath,widgetPath);
        System.out.println("similarEvents size: "+similarEvents.size());

        for(int i=0;i<similarEvents.size();i++){
            System.out.println(similarEvents.get(i).widgetPath);

        }
        //对widget进行分类或排序

    }



    //这里就是分析前缀和后缀，寻找相似性的event
    public static ArrayList<Widget> getSimilarEvents(String layoutPath, String widgetPath){
        System.out.println("widgetPath: "+widgetPath);
        int index=widgetPath.indexOf(":");
        widgetPath=widgetPath.substring(index+1);//取第二个字符之后的字符串
        Widget widget1=getWidget(widgetPath);//对每个widget path进行拆解，存储widget type和index number

        ArrayList<Widget> widgets=new ArrayList<Widget>();
        widgets=getWidgets(layoutPath);//获得一个layoutPath的所有widget
        System.out.println("widgets in the layout: "+widgets.size());

        //下面开始寻找相似的widget
        ArrayList<Widget> similarWidgets=new ArrayList<Widget>();
        for(int i=0;i<widgets.size();i++){
            Widget widget2=widgets.get(i);
            boolean compareResult=compareWidget(widget1,widget2);
            if(compareResult){
                similarWidgets.add(widget2);
            }
        }

        return similarWidgets;
    }

    public static ArrayList<Widget> getSimilarAndContentEqualEvents(String layoutPath, String widgetPath){
        int index_temp=widgetPath.indexOf(":");
        widgetPath=widgetPath.substring(index_temp+1);//取第二个字符之后的字符串
        System.out.println("widgetPath: "+widgetPath);
        Widget widget1=getWidget(widgetPath);//对每个widget path进行拆解，存储widget type和index number

        ArrayList<Widget> widgets=new ArrayList<Widget>();
        widgets=getWidgets(layoutPath);//获得一个layoutPath的所有widget    这里不包含content
        System.out.println("widgets in the layout: "+widgets.size());

        //下面开始寻找相似的widget
        ArrayList<Widget> similarWidgets=new ArrayList<Widget>();
        for(int i=0;i<widgets.size();i++){
            Widget widget2=widgets.get(i);
            boolean compareResult=compareWidget(widget1,widget2);
            if(compareResult){
                similarWidgets.add(widget2);
            }
        }
        System.out.println("Similar widgets: "+similarWidgets.size());

        //下面开始从相似的widget中寻找content相同的widget
        ArrayList<Widget> similarAndContentEqualWidgets=new ArrayList<Widget>();
        ArrayList<String> eventPositionContent=new ArrayList<>();
        LayoutTreeContent1 layoutTree = new LayoutTreeContent1(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。

        eventPositionContent=layoutTree.eventPositionContent;//获得所有包含content的widgets


        String widgetPath_short="null";
        String content="null";
//        for(int i=0;i<eventPositionContent.size();i++){//对文本widget进行遍历
//            String temp=eventPositionContent.get(i);
//            System.out.println("EventPositionContent:  "+temp);
//            int end=temp.indexOf("@");
//            widgetPath_short=widgetPath.substring(0,end);//不包含坐标和content的前面部分
//            System.out.println("WidgetPathShort:  "+widgetPath_short);
//            if(temp.contains(widgetPath_short)){
//                int indexOf1=temp.indexOf("%");
//                content=temp.substring(indexOf1+1);
//            }
//        }
        for(int i=0;i<eventPositionContent.size();i++){//对文本widget进行遍历
            String temp=eventPositionContent.get(i);
            //System.out.println("EventPositionContent:  "+temp);
            int end=widgetPath.indexOf("@");
            widgetPath_short=widgetPath.substring(0,end);//不包含坐标和content的前面部分
            //System.out.println("WidgetPathShort:  "+widgetPath_short);
            if(temp.contains(widgetPath_short)){
                int indexOf1=temp.indexOf("%");
                content=temp.substring(indexOf1+1);
            }
        }
        System.out.println("Content:  "+content);

        for(int j=0;j<similarWidgets.size();j++){
            String tempSimilar=similarWidgets.get(j).widgetPath;

            for(int i=0;i<eventPositionContent.size();i++){
                String temp=eventPositionContent.get(i);
                if(temp.contains(tempSimilar)){
                    int indexOf1=temp.indexOf("%");
                    String content_temp=temp.substring(indexOf1+1);
                    if(content.equals(content_temp)){
                        similarAndContentEqualWidgets.add(similarWidgets.get(j));
                    }
                }
            }
        }

        return similarAndContentEqualWidgets;
    }

    //首先获得一个layout的所有widgets
    public static ArrayList<Widget> getWidgets(String layoutPath){
        ArrayList<Widget> pathElements=new ArrayList<Widget>();
        LayoutTree layoutTree = new LayoutTree(layoutPath);//得到所有节点的path
        for(int i=0;i<layoutTree.eventPosition.size();i++) {
            String widgetPath=layoutTree.eventPosition.get(i);
            //System.out.println("event position:  "+widgetPath);
            Widget widgetObj=getWidget(widgetPath);
            pathElements.add(widgetObj);
        }
        return pathElements;
    }


    public static Widget getWidget(String widgetPath){
        Widget widgetObj=new Widget();
        String widgetPath_save=widgetPath;
        ArrayList<Element> elements=new ArrayList<Element>();
        while(true){
            int indexOf = widgetPath.indexOf("/");
            //System.out.println("widgetPath: "+widgetPath);
            String temp = widgetPath.substring(0,indexOf);//获得一个element
            //System.out.println("temp: "+temp);
            widgetPath=widgetPath.substring(indexOf+1);//去除element+"/"
            int indexOf1=temp.indexOf("[");
            Element element=new Element();
            element.type=temp.substring(0,indexOf1);
            element.index=temp.substring(indexOf1);
            //System.out.println("widgetPath_save: "+widgetPath_save);
            elements.add(element);
            //System.out.println("type: "+element.type+"  "+"index: "+element.index);
            if(!widgetPath.contains("/")){
                break;
            }
        }
        widgetObj.widgetPath=widgetPath_save;
        widgetObj.widget=elements;
        return widgetObj;
    }

    /*
    相同的三个指标：（1）长度相同；（2）类型相同；（3）相似的结构
     */
    public static boolean compareWidget(Widget widget1, Widget widget2){

        if(widget1.widget.size()!=widget2.widget.size()){
            return false;
        }else if(!(widget1.widget.get(widget1.widget.size()-1).type.contains(widget2.widget.get(widget2.widget.size()-1).type))){
            return false;
        }else {
            for(int i=0;i<widget1.widget.size();i++){
                if(!(widget1.widget.get(i).type.contains(widget2.widget.get(i).type))){
                    return false;
                }
            }
        }
        return true;
    }
}
