package edu.nju.ics.alex.inputgenerator.layout;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Boolean.parseBoolean;

/**
 * Created by ysht on 2016/3/7 0007.
 * 用于保存Layout的数据结构（以树的形式）
 */
public class LayoutTreeSimilar {
    int count=0;
    public ArrayList<String> eventPosition=new ArrayList<String>();
    public ArrayList<String> eventList=new ArrayList<String>();
    public ArrayList<Event> eventListE=new ArrayList<Event>();//Event包括了string和LayoutNode
    private LayoutNode root;//LayoutTree是一个根节点为空的树，根节点不包含数据，全局变量
    private String layoutXML;
    //findAll函数的变量，用于保存findAll中的中间结果
    private List<LayoutNode> findList = new ArrayList<LayoutNode>();
    private int totalChildrenCountBeforeCompress = 0;

    public LayoutTreeSimilar(String layoutXML){
        root = new LayoutNode();//新建root节点
        try{
            this.layoutXML = layoutXML;
            createTree(layoutXML);//构建tree

            //totalChildrenCountBeforeCompress = root.getTotalChildrenCount();
            //System.out.println("totalChildrenCountBeforeCompress:   "+totalChildrenCountBeforeCompress);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getTreeSize(){
        return root.getTotalChildrenCount();
    }


    private void createTree(String layoutXML) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        //Document doc = builder.parse(new ByteArrayInputStream(layoutXML.getBytes("utf-8")));//这行语句会报告错误
        Document doc = builder.parse(layoutXML);
        Element rootEle = doc.getDocumentElement();
        if(rootEle == null)
        {
            System.out.println("The rootEle is null");
        }
        NodeList nodes = rootEle.getChildNodes();//这里的都是标准都解析w3c.dom类库    这里就是从根结点开始，进行层次遍历
        System.out.println("root node:   "+rootEle.getTagName());
        if(nodes == null){
            System.out.println("The nodes is null");
        }

        //层次遍历
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if(node != null && node.getNodeType() == Node.ELEMENT_NODE){
                LayoutNode ln = parseActivityNode(node);//------这里的得到各个layout node
                //生成event
                String ev=eventGen(ln);
                if(ev!="null"){
                    if(isLeafNode(node)){
                        eventList.add(eventGen(ln));
                        eventListE.add(eventGenM(ln));
                    }
                }

                ln.indexXpath =ln.className+ "/";//这里存储路径

                if(isLeafNode(node)){
                    //System.out.println("ln.indexXpath"+(++count)+":   "+ln.indexXpath);
                    eventPosition.add(ln.indexXpath);
                }
                root.addChild(ln);//得到节点之间的关系
                recursionCreateTree(node, ln);//进行循环遍历
            }
        }
    }
    //Xpth example:/android.widget.FrameLayout[1]/android.widget.LinearLayout[3]/android.widget.ScrollView[1]
    // /android.widget.LinearLayout[1]/android.view.View[3]")).click()

    private void recursionCreateTree(Node curNode, LayoutNode parent){
        //System.out.println("run recursionCreateTree");
        if(curNode == null)
            return;
        NodeList nodes = curNode.getChildNodes();
        if(nodes == null){
            System.out.println("The nodes is null  leaf node");
            return;
        }
        for(int i=0; i<nodes.getLength(); i++){
            Node node = nodes.item(i);
            if(node != null && node.getNodeType() == Node.ELEMENT_NODE){
                LayoutNode ln = parseActivityNode(node);//由一个node变成一个Layout节点
                //生成event
                String ev=eventGen(ln);
                if(ev!="null"){
                    if(isLeafNode(node)){
                        eventList.add(eventGen(ln));
                        eventListE.add(eventGenM(ln));
                    }
                }
                //开始记录path
                ln.indexXpath = parent.indexXpath+ln.className+ "/";//这里存储初始路径
                if(isLeafNode(node)){
                    //System.out.println("ln.indexXpath"+(++count)+":   "+ln.indexXpath);
                    eventPosition.add(ln.indexXpath);
                }
                parent.addChild(ln);//保存所有节点的路径，扁平化的存储
                recursionCreateTree(node, ln);
            }
        }
    }

    public boolean isLeafNode(Node curNode){
        boolean result=false;
        NodeList nodes = curNode.getChildNodes();
        if(nodes.getLength() == 0){
            result=true;
        }
        return result;
    }

    public static String eventLocation(LayoutNode layoutNode){
        String result="null";
        //这里对location进行了重新对排列，和原始坐标不一样。
        result=layoutNode.bound[0]+","+layoutNode.bound[2]+","+layoutNode.bound[1]+","+layoutNode.bound[3];
        //System.out.println("eventLocation:  "+result);
        return result;
    }


    /**
     * 通过XML中的Node节点创建LayoutNode
     * @param node 用于转换的节点
     * @return 根据node创建的LayoutNode
     */
    private LayoutNode parseActivityNode(Node node){
        LayoutNode layoutNode = new LayoutNode();//这里是新建一个node对象
        NamedNodeMap nnm = node.getAttributes();
        layoutNode.index = Integer.parseInt(nnm.getNamedItem("index").getNodeValue());
        layoutNode.text = nnm.getNamedItem("text").getNodeValue();
        layoutNode.className = nnm.getNamedItem("class").getNodeValue();
        layoutNode.packageName = nnm.getNamedItem("package").getNodeValue();
        layoutNode.contentDesc = nnm.getNamedItem("content-desc").getNodeValue();
        layoutNode.checkable = parseBoolean(nnm.getNamedItem("checkable").getNodeValue());
        layoutNode.checked = parseBoolean(nnm.getNamedItem("checked").getNodeValue());
        layoutNode.clickable = parseBoolean(nnm.getNamedItem("clickable").getNodeValue());
        layoutNode.enabled = parseBoolean(nnm.getNamedItem("enabled").getNodeValue());
        layoutNode.focusable = parseBoolean(nnm.getNamedItem("focusable").getNodeValue());
        layoutNode.focuesd = parseBoolean(nnm.getNamedItem("focused").getNodeValue());
        layoutNode.scrollable = parseBoolean(nnm.getNamedItem("scrollable").getNodeValue());
        layoutNode.longClickable = parseBoolean(nnm.getNamedItem("long-clickable").getNodeValue());
        layoutNode.password = parseBoolean(nnm.getNamedItem("password").getNodeValue());
        layoutNode.selected = parseBoolean(nnm.getNamedItem("selected").getNodeValue());
        String boundStr = nnm.getNamedItem("bounds").getNodeValue();
        Matcher matcher = Pattern.compile("[0-9]+").matcher(boundStr);
        if(matcher.find())
            layoutNode.bound[0] = Integer.parseInt(matcher.group());
        if(matcher.find())
            layoutNode.bound[1] = Integer.parseInt(matcher.group());
        if(matcher.find())
            layoutNode.bound[2] = Integer.parseInt(matcher.group());
        if(matcher.find())
            layoutNode.bound[3]= Integer.parseInt(matcher.group());

        return layoutNode;
    }

    //TODO----对每个节点的类型进行判断，并生成相应的event，目前仅仅考虑clickable
    //其实我们并不需要自动的判别是否是左右滑动还是上下滑动，我们做的仅仅是修改坐标而已
    //但是我们需要找到相同action的widget，这个时候是需要识别滑动方向的。
    //暂时先做一个假设：在一个gui中，可以滑动的widget一般都只有一个，因此，很难出现一个gui中出现多个swipe的情况，所以几乎不需要考虑
    //目前仅仅考虑"点击"事件

    public static String eventGen(LayoutNode layoutNode){
        String result="null";
        if(layoutNode.clickable){
            int x=(layoutNode.bound[2]+layoutNode.bound[0])/2;
            int y=(layoutNode.bound[1]+layoutNode.bound[3])/2;
            result="adb shell input tap "+x+" "+y+"\n";
        }
        return result;
    }

    public static Event eventGenM(LayoutNode layoutNode){
        Event event=new Event();
        String result="null";
        if(layoutNode.clickable){
            int x=(layoutNode.bound[2]+layoutNode.bound[0])/2;
            int y=(layoutNode.bound[1]+layoutNode.bound[3])/2;
            result="adb shell input tap "+x+" "+y+"\n";
        }
        event.event=result;
        event.layoutNode=layoutNode;
        return event;
    }

    /*
    * //get input command via adb
fun toSendEventCommand(action: Actions, node: LayoutNode) : String
        = when {
    action == Actions.LONGCLICK -> {

        //这里对/2是为了得到点击的控件的中间点
        val x = (node.coords[2] + node.coords[0])/2
        val y = (node.coords[1] + node.coords[3])/2
        "swipe $x $y $x $y 1000"
    }
    action == Actions.CLICK -> {
        val x = (node.coords[2] + node.coords[0])/2
        val y = (node.coords[1] + node.coords[3])/2
        "tap $x $y"
    }
    action == Actions.SWIPE_LEFT -> {
        val y = (node.coords[1] + node.coords[3])/2
        "swipe ${node.coords[0]} $y ${node.coords[2]} $y 500"
    }
    action == Actions.SWIPE_RIGHT -> {
        val y = (node.coords[1] + node.coords[3])/2
        "swipe ${node.coords[2]} $y ${node.coords[0]} $y 500"
    }
    action == Actions.SWIPE_DOWN -> {
        val x = (node.coords[2] + node.coords[0])/2
        "swipe $x ${node.coords[1]} $x ${node.coords[3]}"
    }
    action == Actions.SWIPE_UP -> {
        val x = (node.coords[2] + node.coords[0])/2
        "swipe $x ${node.coords[3]} $x ${node.coords[1]}"
    }
    action == Actions.SET_TEXT -> {
        "text \"test\""
    }
    action == Actions.CLEAR_TEXTFIELD -> {
        "text \"\""
    }
    else -> "null"
}
    * */

//这里很重要，得到path
    public LayoutNode getNodeByXPath(String indexXPath){
        String[] indexStrs = indexXPath.split(" ");//这里是空格区分
        int[] indexes = new int[indexStrs.length];
        for(int i=0; i<indexes.length; i++){
            indexes[i] = Integer.parseInt(indexStrs[i]);//这里是变成int数组
        }
        LayoutNode node = root;
        for(int i=0; i<indexes.length; i++){
           if(node.getChildrenCount() <= indexes[i])
               return null;
            node = node.getChildren().get(indexes[i]);
        }
       return node;
    }

    //TODO 如何根据path对tree进行遍历，得到位置，然后对邻居和整个tree进行遍历
}
