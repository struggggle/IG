package edu.nju.ics.alex.inputgenerator;

import edu.nju.ics.alex.inputgenerator.layout.Event;
import edu.nju.ics.alex.inputgenerator.layout.LayoutTree;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/*
* 这里的实现：首先生成一个，然后再次重新执行，生成替换的。每一个event都有一个与之对应都layout.xml文件。要紧密都联系起来。
* */

public class InputMutation_copy {

    static int eventCount=0;

    static String rollBackEvent="adb shell input keyevent 4"+"\n";

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
                LayoutTree layoutTree=new LayoutTree(path);
                layoutEventLists.add(layoutTree.eventList);//add的内容应该可以为null吧？
                layoutEventListsE.add(layoutTree.eventListE);

            String path_temp=MainKt.getDirectoryPath()+"/mutationInputEventList/eventList"+m+".txt";
            writeFile(layoutTree.eventList.toString(),path_temp);
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
                addEvent(linesE,sb1E_temp,layoutEventLists,i,index,sb2E,i,sb1E);

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

    public static void addEvent(List<String> linesE,StringBuilder sb1E, ArrayList<ArrayList<String>> layoutEventLists,int start,int index,StringBuilder sb2E,int flag,StringBuilder sb1E_stable){
        //这里需要判断命令的类型
        //if(linesE.get(start).contains("tap")){//如果是点击类型
        int[] site= Utils.getLocation(linesE.get(start));//得到人工测试输入event对坐标



        if(linesE.get(start).contains("tap")){//如果是点击类型，增加对widget对判断
            for(int i=0;i<layoutEventLists.get(start).size();i++){
                StringBuilder sb1E_temp=copeBuilder(sb1E);//这里必须要创建一个copy，不能直接赋值，因为赋值会指向同一个对象，同步修改
                sb1E_temp.append(layoutEventLists.get(start).get(i));
                if(start==index){//这是终止条件
                    eventCount++;
                    sb1E_temp.append(sb2E);//把后半部分片段接上

                    String path=MainKt.getDirectoryPath()+"/mutationInputM/generated"+flag+"--"+eventCount+".txt";
                    writeFile(sb1E_temp.toString(),path);
                }else{//这里是需要进一步进行遍历
                    addEvent(linesE,sb1E_temp, layoutEventLists, start+1, index, sb2E,flag,sb1E_stable);
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
                addEvent(linesE,sb1E, layoutEventLists, start+1, index, sb2E,flag,sb1E_stable);
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

    //TODO 如何在文件中替换某一行语句并进行组合
    /**
     * 我们目前的mutation仅仅是repeat，可以在repeat的过程中进行替换，只是需要收集几个event的替换集合，然后进行几个for循环的组合，
     * 从集合中提取要替换的event。
     * 其实下面要做的是对每个layout文件进行分析，得到所有的event。
     *
     * 现在的策略应该是，得到event，得到每个event所对应的path，得到每个event的node，这些信息将用于以后的分析
     *
     * 后续的启发式无非是在以上的基础上，对集合中的event进行一个排名，根据position，根据path，然后在循环的时候进行优先设定就可以了
     * **/



    public static String getWidget(ArrayList<ArrayList<Event>> layoutEventListsE, int x, int y, String action, int index){
        String result="null";
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







    public static void main(String[] args){
        String pathEvent=MainKt.getDirectoryPath()+"/browseBlog.sh";//
        String pathAC=MainKt.getDirectoryPath()+"/browseBlogAC.txt";

        int index=6;//start with 0， 且这里的index不能为"返回"事件-----这个需要在事件识别里面做
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
}
