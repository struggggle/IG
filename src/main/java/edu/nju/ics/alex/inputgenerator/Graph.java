package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;

/**
 * 这里可以不以图的形式进行存储，而是以pair的形式存储，<loop,mutate,tag>,表示进行mutation后，界面是否发生变化，
 * 1）我们的顺序就是先尝试所有tag为不变化的，而后尝试所有变化的，接着再尝试所有不变化的。
 * 2）mutate中存储了对哪几个event做什么样对mutate。
 * 3）我们确实只能比较swipe之前的layout，因为只要是点击，就会导致loop后面的layout发生变化啊，例如点了不同的图片，出现了不同的content。
 * 这样说来，我们仅仅需要判断点击的layout
 */

//TODO 可以通过实验来说明是不是loop选择的越大，则成功的可能性越高
public class Graph {
    ArrayList<String> mutate;//记录对loop做来什么样的操作   "M"表示mutate  "S"表示滑动 "N"表示没有任何操作
    boolean changeTag;//loop的界面是否发生变换，这里应该是只对swipe有用
}
