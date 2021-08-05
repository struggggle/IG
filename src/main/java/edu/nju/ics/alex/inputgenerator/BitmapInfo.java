package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BitmapInfo {
    Set<String> sampleSize=new HashSet();//最终是通过这个集合来进行判断的
    String stackInfo;//这里是对一个decoding的路径进行唯一特征：最后一个method name+所有的行号序列，进行唯一标识
    String stackTrace;
    ArrayList<String> nameSet=new ArrayList<>();//存储与这个路径相关的所有的image的name
    String name;
    int number;
}
