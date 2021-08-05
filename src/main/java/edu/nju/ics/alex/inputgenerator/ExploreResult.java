package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;

/**
 * 这里存储explore阶段的结果
 * 1）哪些mutate是有效decode a new image
 * 2）哪些mutate是无效decode a new image
 * 3）哪些mutate导致执行失败（跳出loop）---因为跳出了就回不来了，所以需要及时的知道
 */
public class ExploreResult {
    int index;//在loop中的编号
    ArrayList<String> swipeEvent=new ArrayList<>();//存储可以滑动的event，正常情况下只有一个event
    ArrayList<String> swipeWidget=new ArrayList<>();//存储可以滑动的widget，正常情况下一个event对应最多一个
    String widget;//测试的widget group，存储分组信息 //TODO 后续需要进行调整
    int tag;//mutation标记
    boolean swipeDiff;//是否产生不同的layout
    String result;//分别用new，nothing，fail来进行标记
    String metric;
    String textWidget;
}


