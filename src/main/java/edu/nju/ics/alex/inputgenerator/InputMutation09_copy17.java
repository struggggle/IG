package edu.nju.ics.alex.inputgenerator;//package edu.nju.ics.alex.inputgenerator;
//
//import edu.nju.ics.alex.inputgenerator.layout.Event;
//import edu.nju.ics.alex.inputgenerator.layout.LayoutTree;
//import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeContent;
//import edu.nju.ics.alex.inputgenerator.layout.LayoutTreeSwipe;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static edu.nju.ics.alex.inputgenerator.ExplorationSetterKt.getCurrentLayoutExplore;
//
//
////此为修改的目标版本：2020-10-17
//
///**
// * 关于路径管理，可以先统一都用单独都路径进行存储，然后在后期再进行统一，例如将所有都路径表示为全局变量，同时仅仅是修改app名字就ok了。
// * 统一的路径：$directoryPath/testOutput/wordPress
// */
//
///**
//* 这里是我们定位到了一个loop，然后得到每个event的Xpath，然后得到所有相似的widget list，确定数量。
//* 我们需要在中间执行的过程中，对已经mutate的widget和未mutate的widget进行比较，让loop不一样。
//* 因为涉及到具体的执行，所以需要调用一些代码，主要是cmd的命令。所以可能需要有java和kotlin直接的交互。reference: https://blog.csdn.net/mjb00000/article/details/79218161
//* ----->直接new，直接调用： DataTest mData = new DataTest(); mData.doPrint();
//*
//* 1)识别相似的widget
//* 2)根据widget生成相似的event
//* 3)替换event，插入loop
//* 4)执行新的测试用例。
//*
// *
// * 关于widget 和swipe的mutation，是存在顺序的，都是首先widget，然后再swipe----这是对于单箭头的layout，对于双箭头，倒是没有什么区别了。
// *
//*
//* 目前的目标：先完成explore阶段，然后在explore的基础上，完成exploit阶段。
//* （1）现在先完成对单个对event进行调整对处理，修改和添加swipe，后续可以尝试对多个event进行处理
//* （2）分析存在的变化，实际上是将手动变成自动。要多进行手动分析。看看是否一致
//*
//* 后续的小操作
// * 1）判断image是否相同，排除一些widget不同但是image相同的
//*
//* */
//
//
//public class InputMutation09_copy17 {
//    //测试用例执行停止
//    public static boolean stopFlag=false;
//    //记录所有的explore的结果
//    ArrayList<Graph> exploreResult=new ArrayList<>();
//
//    ArrayList<ArrayList<Integer>> mutatePointsM=new ArrayList<ArrayList<Integer>>();//这里存储对一个loop中的哪几个event进行mutate的记录，主要是记录成功正确与否
//    ArrayList<Integer> mutatePointsO=new ArrayList<Integer>();
//
//    static int eventCount=0;
//
//    //用于保存最后的执行结果
//    static ArrayList<String> generatedTestCase=new ArrayList<>();
//
//    /**这里存储所有运行过的loop，第一层是loop lists，第二层是每个loop中的每一个event，第三层是每个event的变体（包括swipe和click等）*/
//    public static ArrayList<ArrayList<ArrayList<String>>> loopMemory=new ArrayList<ArrayList<ArrayList<String>>>();
//
//    /**存储explore所进行的分组，第一层是loop中的每一个event，第二层是做某一个维度的group分组（例如分支距离包括1，2，3，4等）*/
//    public static ArrayList<ArrayList<ArrayList<Integer>>> groupSignal=new ArrayList<ArrayList<ArrayList<Integer>>>();
//
//
//    public static void main(String[] args){
//        String widgetCasePath="$directoryPath/testOutput/wordPress/caseWidget/browseBlog.sh.txt";//replay的case
//        String layoutsPath="$directoryPath/testOutput/wordPress/layout/";//进行比较的layout  browseBlog.shLayout1.xml
//        String caseName="browseBlog.sh";
//        /** 注意，这个loop涉及到4个状态，但是中间只涉及到3个event*/
//        int[] loopPoints={2,5};//loop的start和end index   这里是layout从1开始计数----我们是通过layout来判断loop
//
//        /**预处理阶段，分析得出loop中的layout中的哪些event可以被mutate和添加swipe。对于每一个widget，返回:
//         * 1）是否具有相似性的widget
//         * 2）是否存在可以滑动的大的widget
//         * 注意点：最后的layout和第一个layout是相同的，构成loop
//         *
//         * 验证结果：测试用例执行通过
//         */
//        ArrayList<Integer> mutateTag=preprocessPhase(widgetCasePath,layoutsPath,caseName,loopPoints);
//        System.out.println("Pre-process result:"+mutateTag.size());
//        for(int i=0;i<mutateTag.size();i++){
//            System.out.println(mutateTag.get(i));
//        }
//
//        /**探索阶段
//         * 这里可以不以图的形式进行存储，而是以pair的形式存储，<loop,mutate,tag>,表示进行mutation后，界面是否发生变化？
//         * 1）我们的顺序就是先尝试所有tag为不变化的，而后尝试所有变化的，接着再尝试所有不变化的。
//         * 2）mutate中存储了对哪几个event做什么样对mutate。
//         * 3）我们只比较swipe之前的layout，因为只要是点击，就会导致loop后面的layout发生变化，例如点了不同的图片，出现了不同的content。
//         * 这样说来，我们仅仅需要判断点击的layout
//         */
//        //王珏的有序是先执行，然后分析哪些event执行相关，然后连接起来。
//        //相同之处是递归迭代，不断探索。等于我们有了一个记忆，如果我们知道了一个loop，知道了在这个loop上的mutation操作，我们就知道走的是一条不归路。
//        //-----》如果是比较完全一致，那么就比较叶节点的数量还有内容，具体涉及叶节点的path还有文本信息。这里可能需要尝试分析一下对于相册滑动，如果没有文本（视觉上），如何进行处理
//        ArrayList<String> generatedTestCase=exploreAndExploit(widgetCasePath,layoutsPath,caseName,loopPoints,mutateTag);
//        System.out.println("Test case length:   "+generatedTestCase.size());
//    }
//
//    /**
//     *
//     * @param widgetCasePath
//     * @param layoutsPath
//     * @param caseName
//     * @param loopPoints  记录loop的起点和终点
//     * @return
//     */
//    public static ArrayList<Integer> preprocessPhase(String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints){
//        ArrayList<Integer> mutateTag=new ArrayList<>();//标记loop中的各个event可以进行什么类别的mutate
//        /**
//         * 得到loop事件序列
//         */
//        ArrayList<EventInfo> loopEvents=new ArrayList<EventInfo>();//event+index
//        try{
//            //这里的event是一个widget的path:包括路径上widget的index,widget,bounds，不包括是否可以滑动
//            List<String> eventlines= Files.readAllLines(Paths.get(widgetCasePath));//读取记录的event（path的形式）从0开始计数
//
//            for(int i=0;i<=eventlines.size();i++){
//                //event是3-5，对应index是2-4
//                if((i>=(loopPoints[0]))&&(i<(loopPoints[1]))){//第i个event对应第i+1个layout，因为event从0开始计数
//                    EventInfo eventInfo=new EventInfo();
//                    eventInfo.index=i;//这里index就是event在widgetCase中的索引，从0开始
//                    eventInfo.event=eventlines.get(i);//event也是从第二个开始读取
//                    loopEvents.add(eventInfo);
//                    System.out.println("index: "+i+"    event: "+eventlines.get(i));
//                }
//            }
//            System.out.println("Event number in the loop: "+loopEvents.size());
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        /**
//         * 对loop中的event进行遍历，判断是否是可以进行滑动和变化的widget
//         * 注意：第i个widget要和第i-1个layout进行分析比较
//         *
//         * 输出：4表示点击界面可滑动且可以变换点击位置 3表示可以变换点击位置  2表示点击界面可以滑动 1表示本身是swipe事件  0表示啥都没有，普通点击
//         */
//        for(int i=0;i<loopEvents.size();i++) {
//            //判断是否为需要进行分析的event，例如滑动事件，是无法找相似或者滑动的widget的
//            String widgetCase=loopEvents.get(i).event;
//            System.out.println("widgetCase:"  +widgetCase);
//            if(widgetCase.contains("adb shell")){
//                if (widgetCase.contains("adb shell input swipe")) {
//                    mutateTag.add(1);
//                }else {
//                    //TODO 查看属性，是否有滑动方向的提示---->需要找到合适的app来具体分析表现形式
//                    //do nothing
//                    mutateTag.add(0);
//                }
//            } else {
//                //String layoutPath="$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout2.xml";
//                System.out.println("index: " + (loopEvents.get(i).index));
//                String layoutPath = layoutsPath + caseName + "Layout" + (loopEvents.get(i).index) + ".xml";////loopPoint从0开始,layout编号从1开始
//                System.out.println("layoutPath:  " + layoutPath);
//                ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(layoutPath, widgetCase);
//                System.out.println("similarEvents:  " + similarEvents.size());//ok
//                ArrayList<String> swipeWidgets = getSwipeWidgets(layoutPath, widgetCase);
//                System.out.println("swipeWidgets:  " + swipeWidgets.size());
//
//                //是否可以改变widget
//                if (swipeWidgets.size() > 0 && (similarEvents.size() > 1)) {
//                    mutateTag.add(4);//表示可以变化widget和滑动
//                } else if (similarEvents.size() > 1) {
//                    mutateTag.add(3);//表示可以变化widget
//                } else if (swipeWidgets.size() > 0) {
//                    mutateTag.add(2);//表示可以滑动
//                } else {
//                    mutateTag.add(0);//表示不可mutate
//                }
//            }
//        }
//        return mutateTag;
//    }
//
//    /**
//     *获得所有可以滑动的widget
//     * ---->首先获得event的bounds，然后检查所有scrollable的并且能覆盖这个event的widget
//     */
//    public static ArrayList<String> getSwipeWidgets(String layoutPath, String event){
//        System.out.println("Start getSwipeWidgets--------------");
//
//        ArrayList<String> result=new ArrayList<>();
//        //获得坐标数据
//        ArrayList<Integer> bound1=getNumber(event);//获得一个坐标四元组
//        for(int i=0;i<bound1.size();i++){
//            System.out.println(bound1.get(i));
//        }
//
//        ArrayList<String> widgets=getSwipeWidgets(layoutPath);//获得所有的widget
//        System.out.println("Swipe event number:  "+widgets.size());//果然只有一个  @0,1440,280,2392#True
//        String widget;
//        for(int i=0;i<widgets.size();i++){
//            widget=widgets.get(i);
//            System.out.println("Swipe event: "+widget);
//            int index = widget.indexOf("#");
//            String scrollable = widget.substring(index + 1);
//            System.out.println("scrollable: "+scrollable);
//            if(scrollable.equals("True")){//可滑动
//                //获得坐标数据
//                ArrayList<Integer> bound2=getNumber(widget);//讲道理应该获得一个四元组
//                for(int k=0;k<bound2.size();k++){
//                    System.out.println(bound1.get(k));
//                }
//                if(boundChecking(bound1,bound2)){//比较坐标数据
//                    result.add(widget);
//                }
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 这里给出一个path，然后得到所有的widgets，包括location和是否可以swipe
//     */
//    public static ArrayList<String> getSwipeWidgets(String layoutPath){
//        LayoutTreeSwipe layoutTreeSwipe = new LayoutTreeSwipe(layoutPath);
//        ArrayList<String> eventSwipe = new ArrayList<String>();
//        Set<String> set = new HashSet<>();
//
//        set=layoutTreeSwipe.eventPositionSwipe;
//        eventSwipe.addAll(set);
//        return eventSwipe;
//    }
//
//    /**
//     *if((x>=bounds.get(number-4))
//     *                 &&(x<=bounds.get(number-3))
//     *                 &&(y>=bounds.get(number-2))
//     *                 &&(y<=bounds.get(number-1))
//     *         )
//     *533
//     * 624
//     * 1057
//     * 1114
//     *         0,1440,280,2392
//     */
//    public static boolean boundChecking(ArrayList<Integer> bound1, ArrayList<Integer> bound2){//@850,900,200,2100@846,954,126,238 x1,x2,y1,y2
//        boolean result;
//        if((bound1.get(0)>=bound2.get(0))
//                &&(bound1.get(1)<=bound2.get(1))
//                &&(bound1.get(2)>=bound2.get(2))
//                &&bound1.get(3)<=bound2.get(3)){
//            result=true;
//        }else {
//            result=false;
//        }
//        return result;
//    }
//
//    /**
//     * 这里需要对loop进行尝试执行：
//     * 1）根据标记，寻找相似的widgets+滑动
//     * 2）每次执行将loop重复执行两次，原始+mutation，比较两个loop的状态差别，每次都执行一个完整的测试用例，即prefix+loop+loop'+suffix
//     * 4）实现给定一个或多个可以变化的event，然后都能生成并执行，并得到结果
//     *
//     * 额外注意：每次滑动都是整个widget的幅度。
//     * ---------暂时先不考虑分组----因为还没有确定好分组规则-------
//     * 1）我们还要记录explore的中间结果，如何进行记录？？？这里记录的应该是具有什么样的结构，例如最小分支距离为1，最小分支距离为2这样的分组依据。
//     *    例如对于每个event，记录一个int值，这个值代表的是一类widget类型。（暂时是int值，后续在做进一步的修改）
//     *
//     * 2）如何比较两个layout是否相同？？？？
//     * ----》首先比较结构相似（基本条件），然后比较所有的widget信息，这个可以加上文本信息就可以，或者对Layout做一点功能对添加就ok了
//     * 验证swipe: adb shell input swipe 700 1400  700 400    x,y的点
//     * 经过实际的观察，每个layout中每个event所对应都可滑动的widget似乎有且仅有一个
//     *
//     * 4表示点击界面可滑动且可以变换点击位置 3表示可以变换点击位置  2表示点击界面可以滑动 1表示本身是swipe事件  0表示啥都没有，普通点击
//     *          *
//     */
//    public static ArrayList<String> exploreAndExploit(String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints,ArrayList<Integer> mutageTag){
//        ArrayList<ExploreResult> exploreResults=new ArrayList<>();
//        ArrayList<EventInfo> prefix=new ArrayList<EventInfo>();
//        ArrayList<EventInfo> suffix=new ArrayList<EventInfo>();
//        ArrayList<EventInfo> loopEvents=new ArrayList<EventInfo>();
//        try{
//            List<String> eventlines= Files.readAllLines(Paths.get(widgetCasePath));
//
//            //String layoutPath="$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout2.xml";
//            //String layoutPath = layoutsPath + caseName + "Layout" + (loopEvents.get(i).index) + ".xml";////loopPoint从0开始,layout编号从1开始
//
//            EventInfo eventInfo=new EventInfo();
//
//            //(i>=(loopPoints[0]))&&(i<(loopPoints[1])
//            for(int i=0;i<eventlines.size();i++){
//
//                if(i<loopPoints[0]){
//                        eventInfo.index = i;
//                        eventInfo.event = eventlines.get(i);
//                        eventInfo.targetLayoutIndex=i;//这里注意index为0的layout不存在
//                        prefix.add(eventInfo);
//                }else if(i>=loopPoints[1]){
//                    eventInfo.index=i;
//                    eventInfo.event=eventlines.get(i);
//                    eventInfo.targetLayoutIndex=i;
//                    suffix.add(eventInfo);
//                }else {
//                    eventInfo.index=i;
//                    eventInfo.event=eventlines.get(i);
//                    eventInfo.targetLayoutIndex=i;
//                    loopEvents.add(eventInfo);
//                }
//            }
//            System.out.println("prefix: "+prefix.size()+"   loop: "+loopEvents.size()+"  suffix: "+suffix.size());
//
//            /**
//             * 这里是要遍历执行所有可能的mutate，现在暂时先执行增加或者修改单个widget，不进行组合
//             * 顺序上应该也是先确定了真正可以mutate的event，然后再进行组合，而不是直接进行组合
//             */
//            int index=0;//从loop的第一个event开始进行
//            exploreResults=recursionMutageTagOne(index,widgetCasePath,layoutsPath,caseName,loopPoints,mutageTag,prefix,loopEvents,suffix,exploreResults);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        System.out.println("Explore results:  "+exploreResults.size());
//        for(int i=0;i<exploreResults.size();i++){
//            System.out.println("Index:  "+exploreResults.get(i).index);
//            System.out.println("swipeDiff:   "+exploreResults.get(i).swipeDiff);
//            System.out.println("tag:    "+exploreResults.get(i).tag);
//            System.out.println("swipeWidget:    "+exploreResults.get(i).swipeWidget);
//        }
//
//        /**开始exploit阶段*/
//        //TODO 目前的假设：如果单个widget变化可以成功，那么多个widget的变化也可以执行成功。-------还没有做的：是否添加induce new image的判断，
//        //TODO 感觉是需要的，这样能让exploit阶段更加的简洁，更容易成功。----》现在可以先写一个简单的
//        //TODO 和exploit的差别仅仅是没有穷尽枚举而已--------真正的explore的结果是记录所有能造成new image decoding的组合，现在的结果是假设所有可以变化的事件都ok
//
//        /**生成新的test case
//         //TODO 这里主要是对图进行遍历，这里的遍历策略很简单，根据我们存储的memory来进行，首先执行完双向的，然后执行单向的。实现起来应该不难。
//         具体策略：
//         1）首先遍历随机连接完成可以执行对，no difference的，即遍历执行各种组合
//         2）然后添加引发变化的滑动，在引发滑动后，重复1）中的步骤
//         */
//        ArrayList<String> generatedTestCase=new ArrayList<>();
//        //TODO 对loopEvents的存储进行转变
//        ArrayList<ArrayList<EventInfo>> loopItems=new ArrayList<>();
//        for(int m=0;m<loopEvents.size();m++){
//            ArrayList<EventInfo> item=new ArrayList<>();
//            item.add(loopEvents.get(m));
//            loopItems.add(item);
//        }
//        generatedTestCase=exploitPhase(widgetCasePath,layoutsPath,caseName,prefix,loopItems,suffix,exploreResults);
//
//
//        //TODO 后续还有一个memory的使用，暂时不管（目前觉得可能实际没有用处，因为已经在explore阶段处理完成）
//
//        return generatedTestCase;
//    }
//
//    /**
//     * 每次仅仅处理一个event的mutation：
//     * 因为一个event可能同时可以添加swipe和变化widget，所以，要进行区分处理。
//     */
//    //TODO 相似性分组，首先分支距离得到一个分组，然后结构相似得到一个分组（倒过来也行）这个以后再进行调整，目前感觉使用两个层次分组比较好：content->结构相似
//    //----》实际也有问题，或许分组也有一个迭代的关系
//    //TODO 现在是否要尝试进行组合？？？？？？
//    //输出：4表示点击界面可滑动且可以变换点击位置 3表示可以变换点击位置  2表示点击界面可以滑动 1表示本身是swipe事件  0表示啥都没有，普通点击
//    public static ArrayList<ExploreResult> recursionMutageTagOne(int index,String widgetCasePath,String layoutsPath,String caseName,int[] loopPoints,ArrayList<Integer> mutageTag,ArrayList<EventInfo> prefix,ArrayList<EventInfo> loopEvents,ArrayList<EventInfo> suffix,ArrayList<ExploreResult> exploreResults) {
//
//        System.out.println("run recursionMutageTag");
//        /**对eventInfo存储对event进行变换或者添加swipe*/
//        EventInfo eventInfo=loopEvents.get(index);
//        String swipeEvent;//记录每次尝试的swipe事件
//
//        //index代表loop中的第index个event。mutageTag包含了是否可以进行mutate或swipe
//        if((index)<mutageTag.size()){
//            //保存执行记录
//            ExploreResult exploreResult=new ExploreResult();
//            if((mutageTag.get(index)==0)||(mutageTag.get(index)==0)){
//                //do nothing
////-----------------------------------
//            }else if(mutageTag.get(index)==2){//explore滑动
//                boolean canSwipe=false;
//
//                //执行过程中的layout存储
//                String layoutPathExplorePrefix="$directoryPath/testOutput/wordPress/layoutExplorePrefix/";
//                String layoutPathExploreLoop="$directoryPath/testOutput/wordPress/layoutExploreLoop";
//                String layoutPathExploreSuffix="$directoryPath/testOutput/wordPress/layoutExploreSuffix/";
//
//                /**接着explore滑动*/
//                String layoutPathSwipe=layoutsPath+caseName+".shLayout"+eventInfo.targetLayoutIndex+".xml";
//                System.out.println("Explore swipe:  : "+layoutPathSwipe);
//                //这里很好办，找到并生成事件就ok
//                ArrayList<String> swipeWidgets=getSwipeWidgets(layoutPathSwipe,loopEvents.get(index).event);
//                System.out.println("Explore phase:  swipeWidgets number: "+swipeWidgets.size());
//                //TODO 对于本身就是swipe的event，确实没有必要进行widgets判断，不能带来任何的收益，也不需要进行explore
//                // 仅仅是如何decoding是由于swipe引发，则需要多次执行swipe
//
//
//                for(int j=0;j<swipeWidgets.size();j++){//遍历所有的swipe  实际最多只有一个
//                    //用来存储要比较的layout
//                    String layout1="";
//                    String layout2="";
//                    //现在需要将loop执行两次，进行前后state是否一致的比较
//                    String widget_temp=swipeWidgets.get(j);
//
//                    //开始解析坐标，生成滑动命令
//                    int[] swipeBounds=getSwipeBounds(widget_temp);
//                    swipeEvent="adb shell input swipe "+swipeBounds[0]+" "+swipeBounds[1]+" "+swipeBounds[2]+" "+swipeBounds[3];
//                    System.out.println("swipe event  : "+swipeEvent);
//
//                    //添加swipe滑动
//                    ArrayList<EventExecutionResult> executionResultsPrefix1=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath1=layoutPathExploreLoop+0+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop1=executeSwipeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent,savePath1);
//                    layout1=executionResultsLoop1.get(index).layoutPath;
//
//                    ArrayList<EventExecutionResult> executionResultsSuffix1=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//
//                    //原始循环
//                    ArrayList<EventExecutionResult> executionResultsPrefix2=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath2=layoutPathExploreLoop+1+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent,savePath2);
//                    layout2=executionResultsLoop2.get(index).layoutPath;
//                    ArrayList<EventExecutionResult> executionResultsSuffix2=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//                    //TODO 开始进行执行结果check
//                    //1)首先检查相似性; 2)检查layout是否相同。
//                    if(checkChangeResult(executionResultsPrefix1)
//                            &&checkChangeResult(executionResultsLoop1)
//                            &&checkChangeResult(executionResultsSuffix1)
//                            &&checkChangeResult(executionResultsPrefix1)
//                            &&checkChangeResult(executionResultsLoop1)
//                            &&checkChangeResult(executionResultsSuffix1)
//                    ){
//                        //TODO 取出要比较的layout  这里直接根据index进行读取
//                        if(completeCompare(layout1,layout2)){//对滑动之前的layout进行比较
//                            canSwipe=true;
//                        }
//                    }else {
//                        canSwipe=false;
//                    }
//
//                    //TODO 下面开始添加标签记录信息
//
//                    //String[] result=executeSwipeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent);
//                    //在下面的基础上进行修改
//                    //ArrayList<EventExecutionResult> executionResultsLoop=executeChangeExplore(layoutsPath,caseName,loopEvents,index,mutateWidget,savePath);
//
//                    if(canSwipe){
//                        exploreResult.index=index;
//                        exploreResult.swipeWidget.add(widget_temp);
//                        exploreResult.swipeEvent.add(swipeEvent);
//                        exploreResult.tag=2;
//                        exploreResult.swipeDiff="True";
//                    }else {
//                        exploreResult.index=index;
//                        exploreResult.tag=0;
//                        exploreResult.swipeDiff="False";
//                    }
//                }
//                exploreResults.add(exploreResult);
//
////------------------------------------------------------
//            }else if(mutageTag.get(index)==2){//变换
//                boolean canChange=true;
//
//                /**首先explore变换*/
//                //执行过程中的layout存储
//                String layoutPathExplorePrefix="$directoryPath/testOutput/wordPress/layoutExplorePrefix/";
//                String layoutPathExploreLoop="$directoryPath/testOutput/wordPress/layoutExploreLoop";
//                String layoutPathExploreSuffix="$directoryPath/testOutput/wordPress/layoutExploreSuffix/";
//
//                //这里首先获得所有可能的相似的widgets
//                //TODO 确实可以在这里就进行选择，因为我们执行的是原始的测试用例
//                String layoutPathChange=layoutsPath+caseName+".shLayout"+eventInfo.targetLayoutIndex+".xml";
//                System.out.println("Explore change:  : "+layoutPathChange);
//                ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(layoutPathChange,loopEvents.get(index).event);
//
//                //TODO 实现content优先(后续再尝试，具体是先explore content，然后explore结构信息，但是都会随机选取两个来执行) 如果没有content，就依靠结构和编辑距离   这里的尝试是否排除原loop中的event
//                List<Integer> randomTwo=randomList(2,similarEvents.size());
//
//                //开始执行这两个widget，如果两个widget都执行通过（和原始loop相似），那么就是ok的。
//                ArrayList<Boolean> changeResults=new ArrayList<>();
//                for(int k=0;k<randomTwo.size();k++){
//                    String mutateWidget=similarEvents.get(randomTwo.get(k)).widgetPath;//随机得到进行替换都widget
//                    //这里需要都返回值：是否和原loop相似和layout list
//                    ArrayList<EventExecutionResult> executionResultsPrefix=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath=layoutPathExploreLoop+k+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop=executeChangeExplore(layoutsPath,caseName,loopEvents,index,mutateWidget,savePath);
//                    ArrayList<EventExecutionResult> executionResultsSuffix=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//                    //TODO 分析和保存测试结果，判断是否为有效滑动-->loop的state相似就ok,是否decode新的image，根据插桩输出进行判断
//                    if(checkChangeResult(executionResultsPrefix)
//                            &&checkChangeResult(executionResultsLoop)
//                            &&checkChangeResult(executionResultsSuffix)
//                    ){
//                        changeResults.add(true);
//                    }else {
//                        changeResults.add(false);
//                    }
//                }
//                for(int c=0;c<changeResults.size();c++){
//                    if(changeResults.get(c)){
//                        //do nothing
//                    }else {
//                        canChange=false;
//                    }
//                }
//                if(canChange){
//                    exploreResult.index=index;
//                    exploreResult.tag=2;
//                }else {
//                    exploreResult.index=index;
//                    exploreResult.tag=0;
//                }
//                exploreResults.add(exploreResult);
//
////----------------------------------------------
//            }
//            else {//同时包含滑动和变换，执行两遍，每一次两个loop
//                boolean canChange=true;
//                boolean canSwipe=false;
//
//                /**首先explore变换*/
//                //执行过程中的layout存储
//                String layoutPathExplorePrefix="$directoryPath/testOutput/wordPress/layoutExplorePrefix/";
//                String layoutPathExploreLoop="$directoryPath/testOutput/wordPress/layoutExploreLoop";
//                String layoutPathExploreSuffix="$directoryPath/testOutput/wordPress/layoutExploreSuffix/";
//
//                //这里首先获得所有可能的相似的widgets
//                //TODO 确实可以在这里就进行选择，因为我们执行的是原始的测试用例
//                String layoutPathChange=layoutsPath+caseName+".shLayout"+eventInfo.targetLayoutIndex+".xml";
//                System.out.println("Explore change:  : "+layoutPathChange);
//                ArrayList<Widget> similarEvents=SimilarEvents.getSimilarEvents(layoutPathChange,loopEvents.get(index).event);
//
//                //TODO 实现content优先(后续再尝试，具体是先explore content，然后explore结构信息，但是都会随机选取两个来执行) 如果没有content，就依靠结构和编辑距离   这里的尝试是否排除原loop中的event
//                List<Integer> randomTwo=randomList(2,similarEvents.size());
//
//                //开始执行这两个widget，如果两个widget都执行通过（和原始loop相似），那么就是ok的。
//                ArrayList<Boolean> changeResults=new ArrayList<>();
//                for(int k=0;k<randomTwo.size();k++){
//                    String mutateWidget=similarEvents.get(randomTwo.get(k)).widgetPath;//随机得到进行替换都widget
//                    //这里需要都返回值：是否和原loop相似和layout list
//                    ArrayList<EventExecutionResult> executionResultsPrefix=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath=layoutPathExploreLoop+k+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop=executeChangeExplore(layoutsPath,caseName,loopEvents,index,mutateWidget,savePath);
//                    ArrayList<EventExecutionResult> executionResultsSuffix=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//                    //TODO 分析和保存测试结果，判断是否为有效滑动-->loop的state相似就ok,是否decode新的image，根据插桩输出进行判断
//                    if(checkChangeResult(executionResultsPrefix)
//                            &&checkChangeResult(executionResultsLoop)
//                            &&checkChangeResult(executionResultsSuffix)
//                    ){
//                        changeResults.add(true);
//                    }else {
//                        changeResults.add(false);
//                    }
//                }
//                for(int c=0;c<changeResults.size();c++){
//                    if(changeResults.get(c)){
//                        //do nothing
//                    }else {
//                        canChange=false;
//                    }
//                }
//
//
//                /**接着explore滑动*/
//                String layoutPathSwipe=layoutsPath+caseName+".shLayout"+eventInfo.targetLayoutIndex+".xml";
//                System.out.println("Explore swipe:  : "+layoutPathSwipe);
//                //这里很好办，找到并生成事件就ok
//                ArrayList<String> swipeWidgets=getSwipeWidgets(layoutPathSwipe,loopEvents.get(index).event);
//                System.out.println("Explore phase:  swipeWidgets number: "+swipeWidgets.size());
//                //TODO 对于本身就是swipe的event，确实没有必要进行widgets判断，不能带来任何的收益，也不需要进行explore
//                // 仅仅是如何decoding是由于swipe引发，则需要多次执行swipe
//
//
//                for(int j=0;j<swipeWidgets.size();j++){//遍历所有的swipe  实际最多只有一个
//                    //用来存储要比较的layout
//                    String layout1="";
//                    String layout2="";
//                    //现在需要将loop执行两次，进行前后state是否一致的比较
//                    String widget_temp=swipeWidgets.get(j);
//
//                    //开始解析坐标，生成滑动命令
//                    int[] swipeBounds=getSwipeBounds(widget_temp);
//                    swipeEvent="adb shell input swipe "+swipeBounds[0]+" "+swipeBounds[1]+" "+swipeBounds[2]+" "+swipeBounds[3];
//                    System.out.println("swipe event  : "+swipeEvent);
//
//                    //添加swipe滑动
//                    ArrayList<EventExecutionResult> executionResultsPrefix1=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath1=layoutPathExploreLoop+0+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop1=executeSwipeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent,savePath1);
//                    layout1=executionResultsLoop1.get(index).layoutPath;
//
//                    ArrayList<EventExecutionResult> executionResultsSuffix1=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//
//                    //原始循环
//                    ArrayList<EventExecutionResult> executionResultsPrefix2=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//                    //对mutateTag中对第index个进行mutate
//                    String savePath2=layoutPathExploreLoop+1+"/";//layout保存路径
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop2=executeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent,savePath2);
//                    layout2=executionResultsLoop2.get(index).layoutPath;
//                    ArrayList<EventExecutionResult> executionResultsSuffix2=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//                    //TODO 开始进行执行结果check
//                    //1)首先检查相似性; 2)检查layout是否相同。
//                    if(checkChangeResult(executionResultsPrefix1)
//                            &&checkChangeResult(executionResultsLoop1)
//                            &&checkChangeResult(executionResultsSuffix1)
//                            &&checkChangeResult(executionResultsPrefix1)
//                            &&checkChangeResult(executionResultsLoop1)
//                            &&checkChangeResult(executionResultsSuffix1)
//                    ){
//                        //TODO 取出要比较的layout  这里直接根据index进行读取
//                        if(completeCompare(layout1,layout2)){//对滑动之前的layout进行比较
//                            canSwipe=true;
//                        }
//                    }else {
//                        canSwipe=false;
//                    }
//
//                    //TODO 下面开始添加标签记录信息
//
//                    //String[] result=executeSwipeExplore(layoutsPath,caseName,loopEvents,index,swipeEvent);
//                    //在下面的基础上进行修改
//                    //ArrayList<EventExecutionResult> executionResultsLoop=executeChangeExplore(layoutsPath,caseName,loopEvents,index,mutateWidget,savePath);
//
//                    if(canChange&&canSwipe){
//                        exploreResult.index=index;
//                        exploreResult.swipeWidget.add(widget_temp);//已经添加
//                        exploreResult.swipeEvent.add(swipeEvent);
//                        exploreResult.tag=4;
//                        exploreResult.swipeDiff="True";
//                    }
//                }
//                exploreResults.add(exploreResult);
//
//
//            }
//            //继续对下一个index+1的event进行explore
//            exploreResults=recursionMutageTagOne(index+1,widgetCasePath,layoutsPath,caseName,loopPoints,mutageTag,prefix,loopEvents,suffix,exploreResults);//开始下一个
//        }
//        return exploreResults;
//    }
//
//
//
//    public static boolean checkChangeResult(ArrayList<EventExecutionResult> eventExecutionResults){
//        for(int i=0;i<eventExecutionResults.size();i++){
//            if(eventExecutionResults.get(i).isSimilar=="False"){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static List<Integer> randomList(int n,int bound){
//        Random rand = new Random();
//        List<Integer> list = new ArrayList<>();
//        int i = 0;
//        while (i < n) {
//            int num = rand.nextInt(bound);
//            if (!list.contains(num)) {
//                list.add(num);
//                i++;
//            }
//        }
//        return list;
//    }
//
//    public static int[] getSwipeBounds(String widget_temp){
//        int[] result=new int[4];
//        //获得坐标数据   //@850,900,200,2100 @846,954,126,238 x1,x2,y1,y2
//        //adb shell input swipe 700 1400  700 400    x,y的点
//
//        //swipe的坐标：
//        ArrayList<Integer> bound=getNumber(widget_temp);//讲道理应该获得一个四元组
//        //向上滑动
//        int x=(bound.get(0)+bound.get(1))/2;
//        int y1=bound.get(3)-5;//y2
//        int y2=bound.get(2)-5;//y1
//
//        result[0]=x;
//        result[1]=y2;
//        result[2]=x;
//        result[3]=y1;
//
//        return result;
//    }
//
//
//
//    private static void writeFile(String str,String path){
//        byte[] strByte=str.getBytes();
//        try{
//            File file=new File(path);
//            if(!file.exists()){
//                File dir=new File(file.getParent());
//                dir.mkdirs();
//                file.createNewFile();
//            }
//            FileOutputStream outStream=new FileOutputStream(file);
//            outStream.write(strByte);
//            outStream.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//
//    /**
//     * 1）在loop执行迭代阶段进行遍历
//     * 2）添加swipe后继续进行执行遍历
//     */
//    public static ArrayList<String> exploitPhase(String widgetCasePath,String layoutsPath,String caseName,ArrayList<EventInfo> prefix,ArrayList<ArrayList<EventInfo>> loopItems,ArrayList<EventInfo> suffix,ArrayList<ExploreResult> exploreResults){
//        ArrayList<String> generatedTestCase=new ArrayList<>();
//        boolean stopFlag=true;
//
//        //执行过程中的layout存储
//        String layoutPathExplorePrefix="$directoryPath/testOutput/wordPress/layoutExplorePrefix/";
//        String layoutPathExploreLoop="$directoryPath/testOutput/wordPress/layoutExploreLoop0";
//        String layoutPathExploreSuffix="$directoryPath/testOutput/wordPress/layoutExploreSuffix/";
//
//        /**执行前缀*/
//        ArrayList<EventExecutionResult> executionResultsPrefix=executeEvents(layoutsPath,caseName,prefix,layoutPathExplorePrefix);//要比较的layout保存在prefix中
//        //TODO 检查执行结果是否正常
//
//        /**执行loop变换*/
//            /**基本策略：
//             * 一个迭代过程，而且这个迭代过程是反向的，即先执行深层次的单向swipe，同时对添加对swipe事件进行记录
//             * */
//            int eventIndex=0;//这里从0开始就意味着是从最后的一个swipe event开始进行遍历
//            travelSwipes(stopFlag,eventIndex,layoutsPath,caseName, loopItems,layoutPathExploreLoop,exploreResults);
//
//        /**执行后缀*/
//        ArrayList<EventExecutionResult> executionResultsSuffix=executeEvents(layoutsPath,caseName,suffix,layoutPathExploreSuffix);
//
//
//
//
//
//        //获得对event进行修改后的loop，每次只能修改一个event，所以就是得到一个相似event的list，然后从其中选中一个进行替换就ok。
//            //我们需要的是，每执行结束一个event，将event记录下来，例如保存在arraylist里面。
//            //executeEvents("Prefix",layoutsPath,caseName,prefix);
//
//            //下面对loop做操作
//            //executeLoops(layoutsPath,caseName,loopEvents,mutateTag);
//
//            //executeEvents("Suffix",layoutsPath,caseName,suffix);
//            //insertLinesM(prefix,sb2,widget,layout,caseName,loop,mutatePoints);
//
//        return generatedTestCase;
//    }
//
//    //这里很简单，只是在执行event之前，加一个滑动就ok，然后记录我们需要的那个layout
//    public static ArrayList<EventExecutionResult> addSwipe(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
//    {
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//
//        String[] rightExecution;
//        for(int i=0;i<loopEvents.size();i++){//逐个执行event
//            EventInfo eventInfo=loopEvents.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//
//            //TODO 开始添加swipe event
//            if(i==index) {
//                //eventInfo.index就是event在case中的编号    widget替换
//                executeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }else {
//                //eventInfo.index就是event在case中的编号
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }
//        }
//        return executionResults;
//    }
//
//
//    /**
//    * 我们有了一个loop，然后我们需要将一个loop变成一个loop sequence
//    * 主要要处理以下几点：
//    * （1）每执行一个event，需要进行一次state比较，同时为后续的event的变化做准备，获取最新的layout。但是可以知道layout的结构是相似的。
//    * （2）每次需要得到相似的widget，然后选择一个，直接list存储
//    * （4）我们是否需要比较两个layout是否完全一致？感觉需要，这里是判断是否滑动到了底部，仅仅比较两个layout
//    * （5）添加swipe，和对应的state进行比较
//    * （6）如何判断和设置swipe添加的数目。
//     *
//     * 具体的操作：
//     * 1）对于一个原始loop，检索exploreResult，首先遍历所有tag为S的，然后在遍历到过程中遍历所有tag为M的。
//     * ----->这里如何添加分组规则，把list变对象list而已
//     * 2）这里需要一个操作：将添加swipe的作为一个整体，然后在这个整体上进行操作，需要新定义一个集合来存储
//    * */
//
//
//    //在滑动的时候，还要判断滑动是否到了最底部--进行比较
////我们的整体包含两层递归：
////递归1：swipe--这一层是出现变化的，这个和正常迭代一样
////递归2：mutate---包括没有变化的swipe，每次swipe次数+1，然后有一个判断什么时候到底，这个可以在explore阶段得到。----将for循环变为while
//
//    public static void travelSwipes(boolean stopFlag, int eventIndex, String layouts,String caseName,ArrayList<ArrayList<EventInfo>> loopItems,String layoutPathExploreLoop,ArrayList<ExploreResult> exploreResults) {
//        if (stopFlag) {//达到来终止条件
//            return;
//        }
//
//        /**
//         * 对于每一个滑动的增加，都需要判断是否滑动到底部，如果全部都滑动到底部，就是滑动到终止信号，也是整个遍历过程的终止信号
//         * 每滑动一次就进行一次组合，但是仅仅涉及到与之相关的组合---这个也正常
//         * */
//
//        //现在是确定了一个被操作的event,现在是逐步的增加swipe
//        ExploreResult exploreResult=exploreResults.get(eventIndex);
//        //判断能否添加swipe
//        if((exploreResult.tag==4)||(exploreResult.tag==2)){
//            System.out.println("swipe event number:  "+exploreResult.swipeEvent.size());
//            //执行滑动事件
//            String swipeEvent=exploreResult.swipeEvent.get(0);
//            executeEvent(swipeEvent,eventIndex,caseName,layouts)//TODO 这里变成每执行一个event就在全局变量中添加
//
//            getCurrentLayoutExplore(1);//这里1是给的一个编号
//            String currentLayout1="$directoryPath/testOutput/wordPress/layoutsExplore/layout"+1+".xml";
//
//            //TODO 比较和上一次循环执行的layout是否完全相同---代表滑动到了底部
//            boolean isChange=completeCompare(currentLayout1,currentLayout2);;
//            if(isChange){
//                //TODO 这里需要一个记录
//
//            }else {//这里表示滑到了底部
//                //开始操作上一层的swipe，然后上一层会继续对下一层的swipe进行变化-----
//                //正常情况下是没有了，执行结束了
//                travelSwipes(stopFlag,eventIndex,layoutsPath,caseName, loopItems,layoutPathExploreLoop,exploreResults);
//            }
//        }
//
//        //判断是否已经可以开始进行变换的遍历
//        if(eventIndex==(loopItems.size()-1)){
//            //可以开始进行遍历
//            int combineIndex=0;
//            travelCombine(stopFlag,combineIndex,layoutsPath,caseName, loopItems,layoutPathExploreLoop,exploreResults);
//        }
//    }
//
//
//    /**
//     *这里需要注意的：
//     * //TODO 滑动的存在一个问题，如果一个点击会影响到后面的滑动呢？怎么办？滑动和点击是否真的可以被彻底的分开成独立的两个部分
//     * //TODO 或者说，要让它们成为两个独立的部分，需要满足什么样的条件？
//     */
//    public static void travelCombine(boolean stopFlag, int eventIndex, String layouts,String caseName,ArrayList<ArrayList<EventInfo>> loopItems,String layoutPathExploreLoop,ArrayList<ExploreResult> exploreResults) {
//        if (stopFlag) {//达到来终止条件
//            return;
//        }
//
//        //判断是否可以进行变换
//        //现在是确定了一个被操作的event,现在是逐步的增加swipe
//        ExploreResult exploreResult=exploreResults.get(eventIndex);
//        //判断能否添加swipe
//        if(exploreResult.tag==3){//如果可以进行变换
//            //查找相似的widget
//            String widget=exploreResult.widget;
//            getCurrentLayoutExplore(1);//这里1是给的一个编号
//            String currentLayout1="$directoryPath/testOutput/wordPress/layoutsExplore/layout"+1+".xml";
//
//            ArrayList<Widget> similarEvents = SimilarEvents.getSimilarEvents(currentLayout1, widget);
//
//            for (int i = 0; i < similarEvents.size(); i++) {
//                Widget widget1=similarEvents.get(i);
//                String chooseWidget=widget1.widgetPath;
//                //TODO 将widget加入到当前要执行的loop
//
//                if(eventIndex==loopItems.size()){
//                    //TODO 这里得到了一个完整的loop，开始执行
//                    System.out.println("Execute loop!");
//                    //index是目前的loop中的event的序号
//                    ArrayList<EventExecutionResult> executionResultsLoop=executeChangeExplore(layoutsPath,caseName,loopEvents,index,mutateWidget,savePath);
//                }
//                travelCombine(stopFlag,eventIndex+1,layoutsPath,caseName, loopItems,layoutPathExploreLoop,exploreResults);
//            }//递归位于for中
//        }
//    }
//
//
//    /**
//     *
//     * @param y 每一个layout的相似的数量
//     * @param n for的层数-----loop中需要进行mutate的event的数量
//     * @param current_n 目前所在的层
//     * @param a 存储每一个layout的相似的数量
//     * @param save  存储目前遍历的loop
//     */
//    public static void travelEvents1(int y, int n, int current_n, int[] a,String[] save) {
//        if (current_n == n) {//迭代层数超过了，则返回
//            return;
//        }
//        int i = 0;
//        for (i = 0; i < y; i++) {
//            System.out.println("a" + current_n + "[" + i + "]=" + a[i]);
//            String temp=("a" + current_n + "[" + i + "]=" + a[i]).toString();
//            save[current_n]=temp;
//            if(current_n==4){
//                //TODo 这里得到了一个完整的loop，开始执行
//                System.out.println("leaf: "+save[0]+save[1]+save[2]+save[3]+save[4]);
//            }
//            travelEvents1(y, n, current_n + 1, a,save);
//        }//递归位于for中
//    }
//
//
//    /**
//     * 我们有了一个loop，然后我们需要将一个loop变成另外一个loop，然后对这两个loop进行前后比较，需要设置一个临时的文件夹来保存layout信息
//     * 主要要处理以下几点：
//     * （1）每执行一个event，获取一次layout，进行一次state比较，同时为后续的event的变化做准备。
//     * （2）对指定index的event，替换为相似的widget
//     * -----下面是处理滑动事件的
//     * （3）我们是否需要比较两个layout是否完全一致？这里选择文本！！！把widget的每一个文本作为一个单元，比较有多少个单元是相同的和不同的。
//     * */
//    public static ArrayList<EventExecutionResult> executeChangeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
//    {
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//        String[] rightExecution;
//        for(int i=0;i<loopEvents.size();i++){//逐个执行event
//            EventInfo eventInfo=loopEvents.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//
//            if(i==index) {
//                //eventInfo.index就是event在case中的编号    widget替换
//                rightExecution = executeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }else {
//                //eventInfo.index就是event在case中的编号
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }
//        }
//        return executionResults;
//    }
//
//    public static ArrayList<EventExecutionResult> executeEvents1( String layoutsPath,String caseName,ArrayList<EventInfo> events,String savePath){
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//        String[] rightExecution;
//        for(int i=0;i<events.size();i++){//逐个执行event
//            EventInfo eventInfo=events.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//            //eventInfo.index就是event在case中的编号
//            rightExecution=executeEvent(eventInfo.event,eventInfo.index,caseName,layoutsPath,savePath);
//            eventExecutionResult.layoutPath=rightExecution[0];
//            eventExecutionResult.isSimilar=rightExecution[1];//"True"or"False"
//        }
//        return executionResults;
//    }
//
//
//
//
//    //这里不对loop做任何的修改
//    public static ArrayList<EventExecutionResult> executeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
//    {
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//
//        String[] rightExecution;
//        for(int i=0;i<loopEvents.size();i++){//逐个执行event
//            EventInfo eventInfo=loopEvents.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//
//                //eventInfo.index就是event在case中的编号
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//        }
//        return executionResults;
//    }
//
//
//    //这里很简单，只是在执行event之前，加一个滑动就ok，然后记录我们需要的那个layout
//    public static ArrayList<EventExecutionResult> executeSwipeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String mutateWidget,String savePath)
//    {
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//
//        String[] rightExecution;
//        for(int i=0;i<loopEvents.size();i++){//逐个执行event
//            EventInfo eventInfo=loopEvents.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//
//            //TODO 开始添加swipe event
//            if(i==index) {
//                //eventInfo.index就是event在case中的编号    widget替换
//                executeEvent(mutateWidget, eventInfo.index, caseName, layoutsPath, savePath);
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }else {
//                //eventInfo.index就是event在case中的编号
//                rightExecution = executeEvent(eventInfo.event, eventInfo.index, caseName, layoutsPath, savePath);
//                eventExecutionResult.layoutPath = rightExecution[0];
//                eventExecutionResult.isSimilar = rightExecution[1];//"True"or"False"
//                //保存执行结果
//                executionResults.add(eventExecutionResult);
//            }
//        }
//        return executionResults;
//    }
//    /**
//     *
//     * @param layoutsPath
//     * @param caseName
//     * @param loopEvents
//     * @param index  目前explore的event在loop中的位置
//     * @param swipe
//     * @return
//     */
////    public static String[] executeSwipeExplore(String layoutsPath,String caseName,ArrayList<EventInfo> loopEvents,int index,String swipe)
////    {
////        /**
////         *loop是否相同；直接两个loop进行比较，第一个循环添加swipe，第二个循环不添加，看看是否相同
////         * 仅仅比较swipe相关的layout
////         */
////
////        String[] result={"True","True"};//第一个是是否相似，第二个是loop前后是否相同
////
////        String[] executionResult;
////        ArrayList<String> layouts1=new ArrayList<>();//存储第一个loop的layout
////        for(int i=0;i<loopEvents.size();i++){
////            //这里加一个判断，确定swipe插入的位置，而且这里还要记录对应的layout
////            if(index==i){
////                //获得滑动之前的layout，即获得当前layout
////                getCurrentLayoutExplore(index);
////                String currentLayout="$directoryPath/testOutput/wordPress/layoutsExplore/layout"+index+".xml";
////                layouts1.add(currentLayout);
////                //执行滑动
////                //TODO 这里所有的执行的layout存储路径都需要做修改，使用上面都路径，每次执行一个loop，都覆盖掉之前都loop记录
////                executionResult=executeEvent(loopEvents.get(i).event,loopEvents.get(i).index,caseName,layoutsPath);
////            }
////            //这里是后续的继续执行
////            executionResult=executeEvent(loopEvents.get(i).event,loopEvents.get(i).index,caseName,layoutsPath);
////            layouts1.add(executionResult[0]);//添加layout的path
////            if (executionResult[1].equals("False")){
////                //System.out.println(tag+" execution: false in event(start with 0) "+i);
////                generatedTestCase.add("Wrong");
////                result[0]="False";
////            }else {
////                generatedTestCase.add(loopEvents.get(i).event);//add to event sequence
////            }
////        }
////
////        //第二次的loop执行原始loop
////        ArrayList<String> layouts2=new ArrayList<>();
////        for(int i=0;i<loopEvents.size();i++){
////            //TODO 获得相似的widgets，并选择一个进行执行，还有就是选择几个？是否使用文本协助，首先是文本，然后是相似距离，就像record和replay
////            //这里还需要重复执行很多次，并且需要获得之前分析到到widgets的集合，根据集合的数量来进行explore
////            executionResult=executeEvent(loopEvents.get(i).event,loopEvents.get(i).index,caseName,layoutsPath);
////            layouts2.add(executionResult[0]);//存储第一个loop的layout
////            if (executionResult[1].equals("False")){
////                //System.out.println(tag+" execution: false in event(start with 0) "+i);
////                generatedTestCase.add("Wrong");
////                result[0]="False";
////            }else {
////                generatedTestCase.add(loopEvents.get(i).event);//add to event sequence
////            }
////        }
////
////        //下面layouts比较
////        ArrayList<Boolean> compareLayoutResults=new ArrayList<>();
////        boolean temp;
////        for(int i=0;i<layouts2.size();i++){
////            temp=completeCompare(layouts1.get(i),layouts2.get(i));
////            compareLayoutResults.add(temp);
////        }
////
////        String temp2="True";
////        for(int i=0;i<compareLayoutResults.size();i++){
////            if(!(compareLayoutResults.get(i))){//如果存在一个为false
////                temp2="False";
////            }
////        }
////        result[1]=temp2;
////        return result;
////    }
//
//
//    /**
//     * //TODO 这里还需要进一步调整，例如可能bounds会发生改变，对于相同对layout在不同的执行顺序下，前后不一致
//     * @param layoutPath1
//     * @param layoutPath2
//     * @return
//     */
//    public static boolean completeCompare(String layoutPath1,String layoutPath2){
//        boolean result;
//        //接下里，获得每个layout的widget列表，仅仅需要叶节点，使用set集合，然后直接比较两个集合是否相等
//
//        Set<String> eventPositionContent1=new HashSet<>();
//        LayoutTreeContent layoutTree1 = new LayoutTreeContent(layoutPath1);//解析xml文件？看看layouttree对xml文件的解析。
//        eventPositionContent1=layoutTree1.eventPositionContent;
//
//        Set<String> eventPositionContent2=new HashSet<>();
//        LayoutTreeContent layoutTree2 = new LayoutTreeContent(layoutPath2);//解析xml文件？看看layouttree对xml文件的解析。
//        eventPositionContent2=layoutTree2.eventPositionContent;
//
//        if (eventPositionContent1.equals(eventPositionContent2)) {
//            result=true;
//        }else {
//            result=false;
//        }
//        return result;
//    }
//
//
//    /**
//     * @param layoutsPath 原始loop的Path
//     * @param caseName
//     * @param events eventInfo list
//     */
//    //TODO 如果执行出错，要及时退出并生成bug报告，所以需要设置一个全局执行的终止tag判断
//    public static ArrayList<EventExecutionResult> executeEvents( String layoutsPath,String caseName,ArrayList<EventInfo> events,String savePath){
//        //存储执行结果
//        ArrayList<EventExecutionResult> executionResults=new ArrayList<>();
//        String[] rightExecution;
//        for(int i=0;i<events.size();i++){//逐个执行event
//            EventInfo eventInfo=events.get(i);
//            EventExecutionResult eventExecutionResult=new EventExecutionResult();
//            //eventInfo.index就是event在case中的编号
//            rightExecution=executeEvent(eventInfo.event,eventInfo.index,caseName,layoutsPath,savePath);
//            eventExecutionResult.layoutPath=rightExecution[0];
//            eventExecutionResult.isSimilar=rightExecution[1];//"True"or"False"
//        }
//        return executionResults;
//    }
//
//    /**返回layoutPath和compareResult，layoutPath用来判断下一次event*/
//    /**
//     *
//     * @param event
//     * @param index  该event在prefix中都序列号
//     * @param caseName
//     * @param layoutsPath
//     * @return
//     */
//    public static String[] executeEvent(String event, int index,String caseName,String layoutsPath,String savePath){
//        String[] result={"null","null"};
//        KUtils kUtils=new KUtils();
//        result=kUtils.executeEvent(event,index,caseName,layoutsPath,savePath);
//        return result;
//    }
//
//
//    //这里对index是layout文件对编号，编号是从1开始
//    public static String getWidget(ArrayList<ArrayList<Event>> layoutEventListsE, int x, int y, int index){
//        String result="null";
//        System.out.println("size"+layoutEventListsE.get(index).size());
//        for(int i=0;i<layoutEventListsE.get(index).size();i++){//遍历所有对node
//            int[] bound=layoutEventListsE.get(index).get(i).layoutNode.bound;
//            boolean clickable=layoutEventListsE.get(index).get(i).layoutNode.clickable;
//            String widget=layoutEventListsE.get(index).get(i).layoutNode.className;
//            //下面开始计算边界值是否在范围内
//            if(x>bound[0]&&x<bound[2]){
//                if(y>bound[1]&&y<bound[3]){
//                    if(clickable){
//                        result=widget;
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    //             int x=(layoutNode.bound[2]+layoutNode.bound[0])/2;
//      //      int y=(layoutNode.bound[1]+layoutNode.bound[3])/2;
//
//
//    //这里仅仅读取后面的四个数值
//    public static boolean positionCompare(int x, int y, ArrayList<Integer> bounds){
//        boolean result=false;
//        int number=bounds.size();
//        System.out.println("bounds:   "+bounds.get(number-4));
//        if((x>=bounds.get(number-4))
//                &&(x<=bounds.get(number-3))
//                &&(y>=bounds.get(number-2))
//                &&(y<=bounds.get(number-1))
//        ) {
//            result = true;
//        }
//        return result;
//    }
//
//
//    //这里是获得一个test case event的坐标, 这里仅仅获得最后的四个数值  @533,624,1057,1114
//    public static ArrayList<Integer> getNumber(String str) {
//        ArrayList<Integer> result=new ArrayList<Integer>();
//        String regex = "\\d+";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//        while (matcher.find()) {
//            int x = Integer.parseInt(matcher.group());
//            result.add(x);
//        }
//        return result;
//    }
//
//
//    //因为我们存储的都是叶节点，讲道理是不会出现点击重叠的情况的。
//    public static String getTargetWidget(String eventStr, String path){
//        String result="";
//        //仅仅处理两种event类型
//        if(eventStr.contains("tap")){
//            ArrayList<Integer> xy=getNumber(eventStr);
//            int x=xy.get(0);
//            int y=xy.get(1);
//            System.out.println("getEventPosition:   "+"x: "+x+"     "+"y:   "+y);
//
//            try {
//                File file = new File(path);
//                InputStream inputStream = new FileInputStream(file);
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    ArrayList<Integer> bounds=getNumber(line);
//                    //System.out.println(line);
//                    if(positionCompare(x,y,bounds)){
//                        System.out.println("Find the target Widget:    "+line);
//                        result=result+line;
//                    }
//                }
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }else{
//            result=eventStr;
//        }
//        return result;
//    }
//
//    //写入文件
//    private static void saveAsFileWriter(String str, String filePath) {
//        filePath = "E:\\test.txt";
//        FileWriter fwriter = null;
//        try {
//            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
//            fwriter = new FileWriter(filePath, true);
//            fwriter.write(str);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                fwriter.flush();
//                fwriter.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    //public static void insertLinesM(List<String> stack,List<String> linesE,StringBuilder sb2E,List<String> linesA,int index) {
//    //这里是测试得到所有的events，同时得到path等等这些信息
//    public static String parseLayoutEvent(int EN, String event, String layoutPath, String fileName){
//        //预处理，首先要得到每个event的所有可能的event的集合
//        ArrayList<ArrayList<String>> layoutEventLists = new ArrayList<ArrayList<String>>();
//        ArrayList<ArrayList<Event>> layoutEventListsE = new ArrayList<ArrayList<Event>>();
//
//            //String path = "$directoryPath/testOutput/wordPress/layout/browseBlog.shLayout1.xml";
//            LayoutTree layoutTree = new LayoutTree(layoutPath);//解析xml文件？看看layouttree对xml文件的解析。
//            //layoutEventLists.add(layoutTree.eventList);//add的内容应该可以为null吧？
//            //layoutEventListsE.add(layoutTree.eventListE);
//
//            //"$directoryPath/testOutput/wordPress/layout/"+fileName+"Layout$EN.xml"
//            String path_allXpath = "$directoryPath/testOutput/wordPress/Xpaths/"+fileName+"Xpath"+EN+".txt";
//            //这里删除index信息
//            String path_allXpath_deleteIndex = "$directoryPath/testOutput/wordPress/Xpaths/"+fileName+"Xpath_deleteIndex"+EN+".txt";
//            //writeFile(layoutTree.eventList.toString(), path_all_widget);//这里是将所有可能的event写入file
//
//        //String path_allXpath = "$directoryPath/testOutput/wordPress/Xpaths/browseBlog.shXpaths.txt";
//        //writeFile(layoutTree.eventPosition.toString(), path_allXpath);//这里是将所有可能的event写入file
//
//        try{
//            writeArrayList2File(layoutTree.eventPosition,path_allXpath);
//            writeArrayList2File(layoutTree.eventPosition,path_allXpath_deleteIndex);
//
//        }catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        try{
//            readFile(path_allXpath);//这个没有什么用，仅仅是遍历操作读取到一个list数据结构中
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//        //在前一个页面中定位event的相对位置
//        String path_allXpath_1 = "$directoryPath/testOutput/wordPress/Xpaths/"+fileName+"Xpath"+(EN-1)+".txt";
//        String widget=getTargetWidget(event, path_allXpath_1);
//        System.out.println("Widget:  "+widget);
//        return widget;
//    }
//
//
//    //在调用这个方法的时候，仅仅需要对点击或者输入event就可以了，所以调用此方法之前做一个简单的if 判断就行。
//    public static ArrayList<int[]> getTargetPosition(String widget,String widgetListFile){
//        ArrayList<int[]> result=new ArrayList<int[]>();
//        //String widget="android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.RelativeLayout[0]/android.support.v4.view.ViewPager[1]/android.widget.FrameLayout[0]/android.view.ViewGroup[1]/android.widget.ImageButton[0]/@1188,1384,2140,2336";
//        //String widgetListFile="$directoryPath/testOutput/wordPress/Xpaths/browseBlog.shXpaths.txt";
//        //先将widget分成两个部分，然后再把它们继续细分为多个部分           这里也许真的可以使用最小编辑距离了，相似的event具有相似的widget
//        //widget例子：  android.widget.FrameLayout[0]/android.view.View[1]//0,1440,0,84
//        //android.widget.FrameLayout[0]/android.widget.LinearLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.FrameLayout[0]/android.widget.RelativeLayout[0]/android.support.v4.view.ViewPager[1]/android.widget.FrameLayout[0]/android.view.ViewGroup[1]/android.widget.ImageButton[0]//1188,1384,2140,2336
//
//        String[] widgetStrs = widget.split("@");//这里是空格区分
//        String widgetPath=widgetStrs[0];//得到widget的前半部分
//
//        //遍历所有的widget
//        ArrayList<String> widgetList= new ArrayList<String>();
//        try{
//            widgetList=readFile(widgetListFile);//这个没有什么用，仅仅是遍历操作读取到一个list数据结构中
//            for(int i=0;i<widgetList.size();i++){
//                String widget_temp=widgetList.get(i);
//                String[] widgetListStrs = widget_temp.split("@");//这里是空格区分
//                String widgetListPath=widgetListStrs[0];
//                if(widgetPath.equals(widgetListPath)){
//                    System.out.println("widget:  "+widgetListPath);
//                    String boundStr=widgetListStrs[1];
//                    String[] bounds = boundStr.split(",");//这里是空格区分
//                    int x1=Integer.parseInt(bounds[0]);//这里是变成int
//                    int x2=Integer.parseInt(bounds[1]);
//                    int y1=Integer.parseInt(bounds[2]);
//                    int y2=Integer.parseInt(bounds[3]);
//                    int[] result_temp={0,0};
//                    result_temp[0]=(x1+x2)/2;
//                    result_temp[1]=(y1+y2)/2;
//                    result.add(result_temp);
//                    System.out.println("position:  "+result_temp[0]+"    "+result_temp[1]);
//                }
//
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        //如果没有path相同的，就需要进行相似性比较了
//        if(result==null){
//            return result;
//        }
//        return result;
//    }
//
//    //写入文件
//    private static void saveAsFileWriter1(String str, String filePath) {
//        filePath = "E:\\test.txt";
//        FileWriter fwriter = null;
//        try {
//            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
//            fwriter = new FileWriter(filePath, true);
//            fwriter.write(str);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                fwriter.flush();
//                fwriter.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//    //有多种写入文件的方式，有的方式会有问题，下面的这个没有问题。
//    public static void writeArrayList2File(ArrayList<String> eventPosition,String path) throws IOException {
//        File fout = new File(path);
//        FileOutputStream fos= new FileOutputStream(fout);
//        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
//        for(int i=0;i<eventPosition.size();i++) {
//            System.out.println("event position:  "+eventPosition.get(i));
//            bw.write(eventPosition.get(i));//写入文件，并加断行
//            bw.newLine();
//        }
//        bw.close();
//    }
//
//    public static ArrayList<String> readFile(String path) throws FileNotFoundException, IOException{
//        ArrayList<String> result=new ArrayList<String>();
//        String tem="";
//        FileInputStream fileInputStream = new FileInputStream(path);
//        InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF-8");
//        BufferedReader reader = new BufferedReader(isr, 5*1024*1024);
//        while ((tem = reader.readLine()) != null) {
//            //System.out.println("tem:   "+tem);
//            result.add(tem);
//        }
//        reader.close();
//        isr.close();
//        fileInputStream.close();
//        return result;
//    }
//}