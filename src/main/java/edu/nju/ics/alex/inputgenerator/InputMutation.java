package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.Event;
import edu.nju.ics.alex.inputgenerator.layout.LayoutTree;
import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeClick;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static edu.nju.ics.alex.inputgenerator.Utils.judeFileExists;


/*
*这里是我们定位到了一个loop，然后得到每个event的Xpath，然后得到所有相似的widget list，确定数量。
* 1)识别相似的widget
* 2)根据widget生成相似的event
* 3)替换event，插入loop
* 4)执行新的测试用例。
*
* */

public class InputMutation {

    static int eventCount=0;
    static String rollBackEvent="adb shell input keyevent 4"+"\n";

    public static void main(String[] args){
        String pathEvent=MainKt.getDirectoryPath()+"/browseBlog.sh";//
        String pathAC=MainKt.getDirectoryPath()+"/browseBlogAC.txt";

        int index=6;
        //读取这个event所对应的activity堆栈信息
        String acStackPath=MainKt.getDirectoryPath()+"/ACStack/ACStack"+index+".txt";
        try{
            List<String> linesAcStack= Files.readAllLines(Paths.get(acStackPath));
            System.out.println("the content of linesAcStack:   "+linesAcStack);
            System.out.println("lines:  "+linesAcStack.get(0));
            readLines(linesAcStack,pathEvent,pathAC,index);//在一个event sequence中，index是从0开始计数
            //下面开始生成所有的mutation测试用例
            readLinesM(linesAcStack,pathEvent,pathAC,index);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void writeFile(String str,String path){
        byte[] strByte=str.getBytes();
        try{
            File file=new File(path);
            if(!file.exists()){
                File dir=new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream=new FileOutputStream(file);
            outStream.write(strByte);
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //acStack   the event index
    public static int[] countRollback(List<String> stack, int start,List<String> linesA){
        int[] count={0,0};//第一个是标记是否可以roll back，第二个是记录roll back的次数
        for(int i=0;i<stack.size();i++){//对栈进行由外到里遍历,第一个ac就是decoding event所在的page
            if(linesA.get(start).equals(stack.get(i))){//比较第几层才能到达向前截取的event的位置，表示可以roll back，标记为1，并记录要roll back的次数
                count[0]=1;
                break;
            }
            //第二个条件确定start的地方和start的上一个event的activity name是不一样的。
            if(!(linesA.get(start).equals(stack.get(i)))){//一层一层的分析，直到找到相同的activity name
                count[1]++;//如果第一个ac就是，就不需要回滚了，如果不是，则先假设加1
            }
        }
        if((start-1)>=0){
            //第二个条件确定start的地方和start的上一个event的activity name是不一样的。
            if((!(linesA.get(start).equals(linesA.get(start-1))))){//一层一层的分析，直到找到相同的activity name
                count[1]++;//如果第一个ac就是，就不需要回滚了，如果不是，则先假设加1
            }

        }
        return count;
    }

    public static StringBuilder copeLines(List<String> lines,int index){
        StringBuilder sb1=new StringBuilder();
        for(int i=0;i<lines.size();i++){
            if(i<index){
                sb1.append(lines.get(i)+"\n");
            }
        }
        return sb1;
    }

    public static StringBuilder copeBuilder(StringBuilder builder){
        StringBuilder result=new StringBuilder();
        result.append(builder.toString());
        return result;
    }

    public static void test(){
        StringBuilder sb1=new StringBuilder();
        sb1.append("hello1");

        StringBuilder sb2=new StringBuilder();
        sb2.append(sb1.toString());
        sb1.append("hello2");
        sb2.append("hello3");
        System.out.println("sb1:"+sb1.toString());
        System.out.println("sb2:"+sb2.toString());

    }


    /**
    * dividing a file into two parts and inserting the added part in the divided position
    * */
    public static void insertLines(List<String> stack,List<String> linesE,StringBuilder sb2E,List<String> linesA,int index){
        for(int i=0;i<index+1;i++){//最多mutation生成的子代的数目，index+1个
            System.out.println("mutation--i=:"+i);
            StringBuilder sb1E=copeLines(linesE,index);//这里是得到sb1E
            //下面开始具体生成每一个子代，每个子代添加足量的返回事件
//            for(int j=i;j<index+1;j++){
            if(linesE.get(i).contains("keyevent 4")){//对"返回"事件不进行处理
                continue;
            }
                int[] num=countRollback(stack,i,linesA);//这里的i是指从event sequence的第几个event进行copy    linesA是每个event的ac
                if(1==num[0]){
                    for(int k=0;k<num[1];k++){
                        sb1E.append(rollBackEvent);
                    }
                    //开始添加event
                    for(int q=i;q<index+1;q++){
                        sb1E.append(linesE.get(q)+"\n");
                    }
                    sb1E.append(sb2E);//把后半部分接上
                    System.out.println(sb1E.toString()+"\n");
                    String path=MainKt.getDirectoryPath()+"/mutationInput/generated"+i+".txt";
                    writeFile(sb1E.toString(),path);
                }


                //below is used to generate multiple copy test input
//            for(int j=0;j<4;j++){
//                if(1==num[0]){
//                    for(int k=0;k<num[1];k++){
//                        sb1E.append(rollBackEvent);
//                    }
//                    //开始添加event
//                    for(int q=i;q<index+1;q++){
//                        sb1E.append(linesE.get(q)+"\n");
//                    }
//                    sb1E.append(sb2E);//把后半部分接上
//                    System.out.println(sb1E.toString()+"\n");
//                }
//            }
//            String path="$directoryPath/mutationInput+/generated"+i+".txt";
//            writeFile(sb1E.toString(),path);
        }
    }

    /**
     * dividing a file into two parts and inserting the added part in the divided position
     * */
    public static void insertLinesM(List<String> stack,List<String> linesE,StringBuilder sb2E,List<String> linesA,int index){
        //预处理，首先要得到每个event的所有可能的event的集合
        ArrayList<ArrayList<String>> layoutEventLists=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Event>> layoutEventListsE=new ArrayList<ArrayList<Event>>();

        for(int m=0;m<linesE.size();m++){
            String path="/Users/wenjieli/My-floder/InputGenerator/layoutsStable/eventLayout"+(m+1)+".xml";
            //if(linesE.get(m).contains("tap")){
                //开始分析layout
                LayoutTree layoutTree=new LayoutTree(path);//解析xml文件？看看layouttree对xml文件的解析。
                layoutEventLists.add(layoutTree.eventList);//add的内容应该可以为null吧？
                layoutEventListsE.add(layoutTree.eventListE);

            String path_temp=MainKt.getDirectoryPath()+"/mutationInputEventList/eventList"+m+".txt";
            writeFile(layoutTree.eventList.toString(),path_temp);//写入文件？
            //}
        }
        System.out.println("the number of event: "+
                layoutEventLists.get(0).size()+"  "+
                layoutEventLists.get(1).size()+"  "+
                layoutEventLists.get(2).size()+"  "+
                layoutEventLists.get(3).size()+"  "+
                layoutEventLists.get(4).size()+"  "+
                layoutEventLists.get(5).size()+"  "+
                layoutEventLists.get(6).size());

        //接下来开始进行组合
        for(int i=0;i<index+1;i++){//向前推，最多mutation copy的event的数量是index+1个，index从0开始
            System.out.println("mutation--i=:"+i);
            StringBuilder sb1E=copeLines(linesE,index);//这里是得到sb1E，这个sb1E不能变化
            StringBuilder sb1E_temp=sb1E;

            //下面开始具体生成每一个子代，每个子代添加足量的返回事件
            if(linesE.get(i).contains("keyevent 4")){//对input中的"返回"事件，不进行处理
                continue;
            }
            int[] num=countRollback(stack,i,linesA);//这里的i是指从event sequence的第几个event进行copy    linesA是每个event的ac
            if(1==num[0]){
                for(int k=0;k<num[1];k++){
                    sb1E_temp.append(rollBackEvent);//添加返回event
                }
                //这个时候的sb1E_temp已经添加好来"返回"事件，接下来开始添加eventList中的事件，这里应该可以将sb1E_temp作为参数进行传递
                //开始添加event-------这里没有递归，所以每次都是新都值
                addEvent(linesE,sb1E_temp,layoutEventListsE,i,index,sb2E,i,sb1E);

//                for(int q=i;q<index+1;q++){
//                    //TODO 下面应该是从eventList集合中进行遍历了------这里需要写一个迭代循环---这里是每个event突变一次
//                    layoutEventLists.get(q);//得到第q个event的变体列表
//                    for(int h=0;h<layoutEventLists.get(q).size();h++){//这里变体的数量是a*b*c的量级
//                        //添加一个判断语句来进行
//
//                    }
//
//                    sb1E.append(linesE.get(q)+"\n");//这里是直接copy，这一句应该去掉
//                }
//                sb1E.append(sb2E);//把后半部分片段接上
//                System.out.println(sb1E.toString()+"\n");
//                String path="$directoryPath/mutationInputM/generated"+i+".txt";
//                writeFile(sb1E.toString(),path);
            }
        }
    }

    public static void addEvent(List<String> linesE,StringBuilder sb1E, ArrayList<ArrayList<Event>> layoutEventListsE,int start,int index,StringBuilder sb2E,int flag,StringBuilder sb1E_stable){
        //这里需要判断命令的类型
        //if(linesE.get(start).contains("tap")){//如果是点击类型
        int[] site= Utils.getLocation(linesE.get(start));//得到人工测试输入event对坐标
        String clazz=getWidget(layoutEventListsE,site[0],site[1],index);
        System.out.println("clazz:  "+clazz);

        if(linesE.get(start).contains("tap")){//如果是点击类型，
            for(int i=0;i<layoutEventListsE.get(start).size();i++){
                //这里增加一个对widget类型对判断
                if(layoutEventListsE.get(start).get(i).layoutNode.className.equals(clazz)){
                    StringBuilder sb1E_temp=copeBuilder(sb1E);//这里必须要创建一个copy，不能直接赋值，因为赋值会指向同一个对象，同步修改
                    sb1E_temp.append(layoutEventListsE.get(start).get(i).event);
                    if(start==index){//这是终止条件
                        eventCount++;
                        sb1E_temp.append(sb2E);//把后半部分片段接上

                        String path=MainKt.getDirectoryPath()+"/mutationInputM/generated"+flag+"--"+eventCount+".txt";
                        writeFile(sb1E_temp.toString(),path);
                    }else{//这里是需要进一步进行遍历
                        addEvent(linesE,sb1E_temp, layoutEventListsE, start+1, index, sb2E,flag,sb1E_stable);
                    }
                }
            }
        }else {
            sb1E.append(linesE.get(start)+"\n");
            if(start==index){//这是终止条件
                eventCount++;
                sb1E.append(sb2E);//把后半部分片段接上
                //System.out.println(sb1E.toString()+"\n");
                String path=MainKt.getDirectoryPath()+"/mutationInputM/generated"+flag+"--"+eventCount+".txt";
                writeFile(sb1E.toString(),path);
            }else{//这里是需要进一步进行遍历
                addEvent(linesE,sb1E, layoutEventListsE, start+1, index, sb2E,flag,sb1E_stable);
            }
        }
    }


    //这里将一个event sequence分为两块，index之前的分为一块，index和它以后的event分为一块
    public static void readLines(List<String> stack,String pathEvent,String pathAC,int index){
        try{
            List<String> linesE= Files.readAllLines(Paths.get(pathEvent));
            List<String> linesA= Files.readAllLines(Paths.get(pathAC));

            StringBuilder sb1E=new StringBuilder();
            StringBuilder sb2E=new StringBuilder();

            for(int i=0;i<linesE.size();i++){
                if(i<index){
                    sb1E.append(linesE.get(i)+"\n");
                }
                if(i>=index){
                    sb2E.append(linesE.get(i)+"\n");
                }
            }
            insertLines(stack,linesE,sb2E,linesA,index);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //这里将一个event sequence分为两块，index之前的分为一块，index和它以后的event分为一块
    public static void readLinesM(List<String> stack,String pathEvent,String pathAC,int index){
        try{
            List<String> linesE= Files.readAllLines(Paths.get(pathEvent));
            List<String> linesA= Files.readAllLines(Paths.get(pathAC));

            StringBuilder sb1E=new StringBuilder();
            StringBuilder sb2E=new StringBuilder();

            for(int i=0;i<linesE.size();i++){
                if(i<index){
                    sb1E.append(linesE.get(i)+"\n");
                }
                if(i>=index){
                    sb2E.append(linesE.get(i)+"\n");
                }
            }
            insertLinesM(stack,linesE,sb2E,linesA,index);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //TODO 如何在文件中替换某一行语句并进行组合----可以直接arrayList处理啊
    /**
     * 我们目前的mutation仅仅是repeat，可以在repeat的过程中进行替换，只是需要收集几个event的替换集合，然后进行几个for循环的组合，
     * 从集合中提取要替换的event。
     * 其实下面要做的是对每个layout文件进行分析，得到所有的event。
     *
     * 现在的策略应该是，得到event，得到每个event所对应的path，得到每个event的node，这些信息将用于以后的分析
     *
     * 后续的启发式无非是在以上的基础上，对集合中的event进行一个排名，根据position，根据path，然后在循环的时候进行优先设定就可以了
     * **/



    //这里对index是layout文件对编号，编号是从1开始
    public static String getWidget(ArrayList<ArrayList<Event>> layoutEventListsE, int x, int y, int index){
        String result="null";
        System.out.println("size"+layoutEventListsE.get(index).size());
        for(int i=0;i<layoutEventListsE.get(index).size();i++){//遍历所有对node
            int[] bound=layoutEventListsE.get(index).get(i).layoutNode.bound;
            boolean clickable=layoutEventListsE.get(index).get(i).layoutNode.clickable;
            String widget=layoutEventListsE.get(index).get(i).layoutNode.className;
            //下面开始计算边界值是否在范围内
            if(x>bound[0]&&x<bound[2]){
                if(y>bound[1]&&y<bound[3]){
                    if(clickable){
                        result=widget;
                    }
                }
            }
        }
        return result;
    }
    /*
    *             int x=(layoutNode.bound[2]+layoutNode.bound[0])/2;
            int y=(layoutNode.bound[1]+layoutNode.bound[3])/2;
    * */






    //这里仅仅读取后面的四个数值,//左上角的x,y+右下角的x,y [0,0][1080,2135]xy,xy---已经确认
    public static boolean positionCompare(int x, int y, ArrayList<Integer> bounds){
        boolean result=false;
        int number=bounds.size();
        System.out.println("bounds:   "+"("+x+","+y+")"+"x,x,y,y"+bounds.get(number-4)+" "+bounds.get(number-3)+"  "+bounds.get(number-2)+"  "+bounds.get(number-1));
        if((x>=bounds.get(number-4))
                &&(x<=bounds.get(number-3))
                &&(y>=bounds.get(number-2))
                &&(y<=bounds.get(number-1))
        ) {
            result = true;
        }
        return result;
    }


    //这里是解析layout tree中的语句，得到的是xxyy: [35,217][237,551]解析成35,237,217,551
    public static ArrayList<Integer> getNumber(String str) {
        ArrayList<Integer> result=new ArrayList<Integer>();
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group());
            System.out.println(x);
            result.add(x);
        }
        return result;
    }


    //因为我们存储的都是叶节点，讲道理是不会出现点击重叠的情况的。
    public static String getTargetWidgetClick(String eventStr, String path){
        System.out.println("call getTargetWidgetClick");
        System.out.println("path: "+path);
        String result="";
        ArrayList<ArrayList<Integer>> candidateWidget=new ArrayList<>();
        ArrayList<String> candidateWidgetLine=new ArrayList<>();
        //just analyze click event
        if(eventStr.contains("tap")){
            ArrayList<Integer> xy=getNumber(eventStr);//这里得到的是xx,yy
            //obtain x,y positions
            int x=xy.get(0);
            int y=xy.get(1);
            try {
                File file = new File(path);
                InputStream inputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                System.out.println("x,y:  "+x+","+y);
                while ((line = bufferedReader.readLine()) != null) {
                    ArrayList<Integer> bounds=getNumber(line);//得到x,x,y,y和layout tree中x,y,x,y不一样
                    System.out.println("widget line:  "+line);
                    if(positionCompare(x,y,bounds)){
                        System.out.println("Find the target Widget:    "+line);
                        candidateWidget.add(bounds);
                        candidateWidgetLine.add(line);
                    }
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(candidateWidget.size()>=1) {
                if (candidateWidget.size() > 1) {
                    System.out.println("to find the small x,y widget:  " + x + "," + y);
                    int target = findSmallestWidget(candidateWidget);
                    result = candidateWidgetLine.get(target);
                } else {
                    result = candidateWidgetLine.get(0);
                }
            }else {
                result=null;
            }
        }else{
            result=eventStr;
        }
        return result;
    }

    //这里是根据event的坐标来得到对应的widget
    public static String getTargetWidget(String eventStr, String path){
        System.out.println("call getTargetWidget");
        System.out.println("path: "+path);
        String result="";
        ArrayList<ArrayList<Integer>> candidateWidget=new ArrayList<>();
        ArrayList<String> candidateWidgetLine=new ArrayList<>();
        //just analyze click event
        if(eventStr.contains("adb shell input tap")){
            ArrayList<Integer> xy=getNumber(eventStr);//这里得到的是xx,yy
            //obtain x,y positions
            int x=xy.get(0);
            int y=xy.get(1);
            try {
                File file = new File(path);
                InputStream inputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                System.out.println("x,y:  "+x+","+y);
                while ((line = bufferedReader.readLine()) != null) {
                    ArrayList<Integer> bounds=getNumber(line);//得到x,x,y,y和layout tree中x,y,x,y不一样
                    System.out.println("widget line:  "+line);
                    if(positionCompare(x,y,bounds)){
                        System.out.println("Find the target Widget:    "+line);
                        candidateWidget.add(bounds);
                        candidateWidgetLine.add(line);
                    }
                }
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            if(candidateWidget.size()>1) {
                System.out.println("to find the small x,y widget:  " + x + "," + y);
                int target = findSmallestWidget(candidateWidget);
                result = candidateWidgetLine.get(target);
            }else {
                System.out.println("candidateWidgetLine number: "+candidateWidgetLine.size());
                result=candidateWidgetLine.get(0);
            }

        }else{
            result=eventStr;
        }
        return result;
    }

    public static int findSmallestWidget(ArrayList<ArrayList<Integer>> candidateWidget){
        ArrayList<Integer> temp=candidateWidget.get(0);
        /**
         * x>=bounds.get(number-4))
         *                 &&(x<=bounds.get(number-3))
         *                 &&(y>=bounds.get(number-2))
         *                 &&(y<=bounds.get(number-1))
         * */
        int xSmallest=temp.get(temp.size()-3)-temp.get(temp.size()-4);
        int ySmallest=temp.get(temp.size()-1)-temp.get(temp.size()-2);
        int indexSmallest=0;
        System.out.println("temp:   "+xSmallest+"  "+ySmallest);

        for(int i=1;i<candidateWidget.size();i++){
            ArrayList<Integer> temp1=candidateWidget.get(i);
            int x1=temp1.get(temp1.size()-3)-temp1.get(temp1.size()-4);
            int y1=temp1.get(temp1.size()-1)-temp1.get(temp1.size()-2);
            System.out.println("temp1:   "+x1+"  "+y1);
            if((xSmallest<=x1)&&(ySmallest<=y1)){
            }else {
                indexSmallest=i;
                xSmallest=x1;
                ySmallest=y1;
                System.out.println("xSmallest: "+xSmallest+"    "+"ySmallest:  "+ySmallest);
            }
        }
        System.out.println("indexSmallest:   "+indexSmallest);
        return indexSmallest;
    }

    //写入文件
    private static void saveAsFileWriter(String str, String filePath) {
        filePath = "E:\\test.txt";
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filePath, true);
            fwriter.write(str);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //public static void insertLinesM(List<String> stack,List<String> linesE,StringBuilder sb2E,List<String> linesA,int index) {
    //这里是测试得到所有的events，同时得到path等等这些信息
    public static String parseLayoutEvent(String appName,int EN, String event, String layoutPath, String fileName){
        System.out.println("Call parseLayoutEvent()");
        //预处理，首先要得到每个event的所有可能的event的集合
        ArrayList<ArrayList<String>> layoutEventLists = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Event>> layoutEventListsE = new ArrayList<ArrayList<Event>>();

            //String path = "$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout1.xml";
        //TODO layout tree中的坐标是x,x,y,y
            LayoutTreeClick layoutTreeClick = new LayoutTreeClick(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
            LayoutTree layoutTree = new LayoutTree(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
            //layoutEventLists.add(layoutTree.eventList);//add的内容应该可以为null吧？
            //layoutEventListsE.add(layoutTree.eventListE);

            //"$directoryPath/testOutput/wordPress/layout/"+fileName+"Layout$EN.xml"
            String path_allXpathClick = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/XpathsClick/"+fileName+"Xpath"+EN+".txt";
            String path_allXpath = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/Xpaths/"+fileName+"Xpath"+EN+".txt";
            //这里删除index信息
            //String path_allXpath_deleteIndex = "$directoryPath/testOutput/"+appName+"/Xpaths/"+fileName+"Xpath_deleteIndex"+EN+".txt";
            //writeFile(layoutTree.eventList.toString(), path_all_widget);//这里是将所有可能的event写入file

        //String path_allXpath = "$directoryPath/testOutput/wordPress/Xpaths/browseBlog.shXpaths.txt";
        //writeFile(layoutTree.eventPosition.toString(), path_allXpath);//这里是将所有可能的event写入file

        try{
            writeArrayList2File(layoutTreeClick.eventPositionClick,path_allXpathClick);
            writeArrayList2File(layoutTree.eventPosition,path_allXpath);
            //writeArrayList2File(layoutTree.eventPosition,path_allXpath_deleteIndex);

        }catch (IOException ex) {
            ex.printStackTrace();
        }

        try{
            readFile(path_allXpath);//这个没有什么用，仅仅是遍历操作读取到一个list数据结构中
        }catch (IOException e){
            e.printStackTrace();
        }

        //在前一个页面中定位event的相对位置---这里要求下面的两个路径文件已经存在了。56,1384,2189,2357
        String path_allXpathClick1 = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/XpathsClick/"+fileName+"Xpath"+(EN-1)+".txt";
        String path_allXpath1 = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/Xpaths/"+fileName+"Xpath"+(EN-1)+".txt";
        String widget="null";
        //widget=getTargetWidgetClick(event, path_allXpathClick1);
        //if(widget==null){
            widget=getTargetWidget(event, path_allXpath1);
        //}
        System.out.println("Target widget:  "+widget);
        return widget;
    }

    public static void parseLayoutEventNoWidget(String appName,int EN, String event, String layoutPath, String fileName){
        System.out.println("Call parseLayoutEventNoWidget（）");
        //预处理，首先要得到每个event的所有可能的event的集合
        ArrayList<ArrayList<String>> layoutEventLists = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Event>> layoutEventListsE = new ArrayList<ArrayList<Event>>();

        //String path = "$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout1.xml";
        //TODO layout tree中的坐标是x,x,y,y
        LayoutTreeClick layoutTreeClick = new LayoutTreeClick(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
        LayoutTree layoutTree = new LayoutTree(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
        //layoutEventLists.add(layoutTree.eventList);//add的内容应该可以为null吧？
        //layoutEventListsE.add(layoutTree.eventListE);

        //"$directoryPath/testOutput/wordPress/layout/"+fileName+"Layout$EN.xml"
        String path_allXpathClick = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/XpathsClick/"+fileName+"Xpath"+EN+".txt";
        String path_allXpath = MainKt.getDirectoryPath()+"/testOutput/"+appName+"/Xpaths/"+fileName+"Xpath"+EN+".txt";
        //这里删除index信息
        //String path_allXpath_deleteIndex = "$directoryPath/testOutput/"+appName+"/Xpaths/"+fileName+"Xpath_deleteIndex"+EN+".txt";
        //writeFile(layoutTree.eventList.toString(), path_all_widget);//这里是将所有可能的event写入file

        //String path_allXpath = "$directoryPath/testOutput/wordPress/Xpaths/browseBlog.shXpaths.txt";
        //writeFile(layoutTree.eventPosition.toString(), path_allXpath);//这里是将所有可能的event写入file

        try{
            writeArrayList2File(layoutTreeClick.eventPositionClick,path_allXpathClick);
            writeArrayList2File(layoutTree.eventPosition,path_allXpath);
            //writeArrayList2File(layoutTree.eventPosition,path_allXpath_deleteIndex);

        }catch (IOException ex) {
            ex.printStackTrace();
        }

        try{
            readFile(path_allXpath);//这个没有什么用，仅仅是遍历操作读取到一个list数据结构中
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static ArrayList<String> getLayoutEvents(String layoutPath){
        LayoutTree layoutTree = new LayoutTree(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
        return layoutTree.eventPosition;
    }


    //在调用这个方法的时候，仅仅需要对点击或者输入event就可以了，所以调用此方法之前做一个简单的if 判断就行。
    public static ArrayList<int[]> getTargetPosition(String widget,String widgetListFile){
        ArrayList<int[]> result=new ArrayList<int[]>();

        String[] widgetStrs = widget.split("@");//这里是空格区分
        String widgetPath=widgetStrs[0];//得到widget的前半部分

        //遍历所有的widget
        ArrayList<String> widgetList= new ArrayList<String>();
        try{
            widgetList=readFile(widgetListFile);//这个没有什么用，仅仅是遍历操作读取到一个list数据结构中
            for(int i=0;i<widgetList.size();i++){
                String widget_temp=widgetList.get(i);
                System.out.println("original path2:  "+widgetList.get(i));
                String[] widgetListStrs = widget_temp.split("@");
                String widgetListPath=widgetListStrs[0];
                System.out.println("Path1:  "+widgetPath);
                System.out.println("Path2:  "+widgetListPath);
                if(widgetPath.contains(widgetListPath)){//发现使用eques报告错误
                    System.out.println("widget:  "+widgetListPath);
                    String boundStr=widgetListStrs[1];
                    String[] bounds = boundStr.split(",");//这里是空格区分
                    int x1=Integer.parseInt(bounds[0]);//这里是变成int
                    int x2=Integer.parseInt(bounds[1]);
                    int y1=Integer.parseInt(bounds[2]);
                    int y2=Integer.parseInt(bounds[3]);
                    int[] result_temp={0,0};
                    result_temp[0]=(x1+x2)/2;
                    result_temp[1]=(y1+y2)/2;
                    result.add(result_temp);
                    System.out.println("position:  "+result_temp[0]+"    "+result_temp[1]);
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //如果没有path相同的，就需要进行相似性比较了
        if(result==null){
            return result;
        }
        return result;
    }


    public static ArrayList<int[]> getTargetPositionFromWidgetList(String widget,ArrayList<String> widgetList){
        ArrayList<int[]> result=new ArrayList<int[]>();
        String[] widgetStrs = widget.split("@");//这里是空格区分
        String widgetPath=widgetStrs[0];//得到widget的前半部分

            for(int i=0;i<widgetList.size();i++){
                String widget_temp=widgetList.get(i);
                String[] widgetListStrs = widget_temp.split("@");//这里是空格区分
                String widgetListPath=widgetListStrs[0];
                if(widgetPath.equals(widgetListPath)){
                    System.out.println("widget:  "+widgetListPath);
                    String boundStr=widgetListStrs[1];
                    String[] bounds = boundStr.split(",");//这里是空格区分
                    int x1=Integer.parseInt(bounds[0]);//这里是变成int
                    int x2=Integer.parseInt(bounds[1]);
                    int y1=Integer.parseInt(bounds[2]);
                    int y2=Integer.parseInt(bounds[3]);
                    int[] result_temp={0,0};
                    result_temp[0]=(x1+x2)/2;
                    result_temp[1]=(y1+y2)/2;
                    result.add(result_temp);
                    System.out.println("position:  "+result_temp[0]+"    "+result_temp[1]);
                }

            }
        //如果没有path相同的，就需要进行相似性比较了
        if(result==null){
            return result;
        }
        return result;
    }

    //写入文件
    private static void saveAsFileWriter1(String str, String filePath) {
        filePath = "E:\\test.txt";
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filePath, true);
            fwriter.write(str);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //有多种写入文件的方式，有的方式会有问题，下面的这个没有问题。
    public static void writeArrayList2File(ArrayList<String> eventPosition,String path) throws IOException {
        System.out.println("path: "+path);
        File fout = new File(path);
//        if (!fout.exists()) {
//            createFile(fout);
//        }
//        fout = new File(path);
        if (!fout.exists()) {
            fout.createNewFile();//有路径才能创建文件
            System.out.println(fout);
        }

        FileOutputStream fos= new FileOutputStream(fout);
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
        for(int i=0;i<eventPosition.size();i++) {
            System.out.println("event position:  "+eventPosition.get(i));
            bw.write(eventPosition.get(i));//写入文件，并加断行
            bw.newLine();
        }
        bw.close();
    }

    public static void createFile(File testFile){
        File fileParent = testFile.getParentFile();//返回的是File类型,可以调用exsit()等方法
        String fileParentPath = testFile.getParent();//返回的是String类型
        System.out.println("fileParent:" + fileParent);
        System.out.println("fileParentPath:" + fileParentPath);
        if (!fileParent.exists()) {
            fileParent.mkdirs();// 能创建多级目录
        }
        try {
            if (!testFile.exists())
                testFile.createNewFile();//有路径才能创建文件
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException{
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

    //肯定是要adb命令来进行获取指定进程的heap
    public static void dumpHeap(){

    }


}

