package edu.nju.ics.alex.inputgenerator;

import java.util.*;

//现在已经初步ok了
public class CheckCycle {
    static List<Integer> trace;
    static Set<Integer> searched = new HashSet<>();
    static Set<List<Integer>> allCircles = new HashSet<>();
    static Set<List<Integer>> allSortCircles = new HashSet<>();


    public static void main(String[] args) {

        String caseName="browseBlog";
        String path = MainKt.getDirectoryPath()+"/testOutput/wordPress/layout";
        ArrayList<FilePath> files=SortFiles.sortFile(caseName,path);

        ArrayList<int[]> loops = new ArrayList<int[]>();
        ArrayList<Integer> intseq = intSeq(files);
        int[] input = new int[intseq.size()];
        for (int i = 0; i < intseq.size(); i++) {
            input[i] = intseq.get(i);
            System.out.println("seq : "+input[i]);
        }

        int[][] result = stringToMtrix(input, intseq.size());
        printMatrix(result);
        //这里用的是状态模型
        cycles(intseq.size(), result);

        //现在不使用状态模型
        ArrayList<Loop> loopArrayList=findCycleInSeq(intseq);
        for(int i=0;i<loopArrayList.size();i++){
            System.out.println("loop: "+loopArrayList.get(i).start+"  "+loopArrayList.get(i).end);
        }


    }

    public static ArrayList<Loop> getLoops(String caseName,String layoutPath) {

        ArrayList<FilePath> files=SortFiles.sortFile(caseName,layoutPath);

        ArrayList<int[]> loops = new ArrayList<int[]>();
        ArrayList<Integer> intseq = intSeq(files);
        int[] input = new int[intseq.size()];
        for (int i = 0; i < intseq.size(); i++) {
            input[i] = intseq.get(i);
            System.out.println("seq : "+input[i]);
        }

        int[][] result = stringToMtrix(input, intseq.size());
        printMatrix(result);
        //这里用的是状态模型
        cycles(intseq.size(), result);

        //现在不使用状态模型
        ArrayList<Loop> loopArrayList=findCycleInSeq(intseq);
        for(int i=0;i<loopArrayList.size();i++){
            System.out.println("loop: "+loopArrayList.get(i).start+"  "+loopArrayList.get(i).end);
        }

        return loopArrayList;
    }

    public static void cycles(int stateNumber, int[][] edges) {
        int n = stateNumber;
        int result[][] = new int[100][100];
        result = edges;
        for (int i = 0; i < n; i++) {
            if (searched.contains(i))
                continue;
            trace = new ArrayList<>();
            findCyclesInModel(i, result);
        }

        Set<List<Integer>> circles = obtainCycle(allCircles, allSortCircles);
        for (List<Integer> list : circles)//allCircles
            System.out.println("circle: " + list);
    }


    static void findCyclesInModel(int v, int[][] e) {
        int j = trace.indexOf(v);
        if (j != -1) {
            List<Integer> circle = new ArrayList<>();
            while (j < trace.size()) {
                circle.add(trace.get(j));
                j++;
            }
//            System.out.println("circleBeforeSort:"+circle);
//            Collections.sort(circle);//这里是将一个循环大节点从大到小进行排序
//            System.out.println("circleAfterSort:"+circle);
            List<Integer> temp = new ArrayList<>();
            for (Integer integer : circle) {
                temp.add(integer);
            }
            allCircles.add(temp);
            Collections.sort(circle);
            allSortCircles.add(circle);
            return;
        }


        trace.add(v);
        for (int i = 0; i < e.length; i++) {
            if (e[v][i] == 1) {
                searched.add(i);
                findCyclesInModel(i, e);
            }
        }
        trace.remove(trace.size() - 1);
    }

    static Set<List<Integer>> obtainCycle(Set<List<Integer>> allCircles, Set<List<Integer>> allSortCircles) {
        List<Integer> sort;
        Set<List<Integer>> result = new HashSet<>();
        //对没有sort对进行遍历，在遍历对过程中进行sort，排序之后非常容易比较是否相等，然后得到起始对第一个节点对标记。
        for (List<Integer> listSort : allSortCircles) {
            sort = listSort;
            //System.out.println("sort:  "+listSort);
            for (List<Integer> list : allCircles) {
                List<Integer> noSort = new ArrayList<>();
                for (Integer integer : list) {
                    noSort.add(integer);
                }
                Collections.sort(noSort);
                if (sort.equals(noSort)) {
                    if (sort.get(0) == list.get(0)) {
                        result.add(list);
                    }
                }
            }
        }
        return result;
    }

    public static int[][] stringToMtrix(int[] seq, int size) {
        System.out.println("seq length: "+seq.length);
        int result[][] = new int[size][size];
        for (int i = 0; i < seq.length - 1; i++) {
            int from = seq[i];
            int to = seq[i + 1];
            result[from][to] = 1;
            System.out.println("from: "+from+"  to: "+to);
        }
        return result;
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0, j = 0; i < matrix.length; ) {
            System.out.println(i + " " + j + ":" + matrix[i][j]);
            j++;
            if (j >= matrix[i].length) {
                i++;
                j = 0;
            }
        }

    }


    public static ArrayList<Integer> intSeq(ArrayList<FilePath> files) {
        ArrayList<String> layoutSet = new ArrayList<String>();
        ArrayList<Integer> layoutIndex = new ArrayList<Integer>();
        String temp;
        int indexCount=0;

        for (FilePath f : files) {
                String tag = "True";
                if (layoutSet.size() == 0) {
                    layoutSet.add(f.filePath);
                    layoutIndex.add(indexCount);
                    System.out.println("add index: "+indexCount);
                    indexCount++;

                    System.out.println("add new one:  "+layoutSet.size()+"---"+f.filePath);
                } else {
                    String compareTarget;
                    for (int i = 0; i < layoutSet.size(); i++) {
                        compareTarget = layoutSet.get(i);
                        temp = CallPython.callPython(f.filePath, compareTarget);
                        if (temp.equals("True")) {
                            layoutIndex.add(i);//保存编号
                            System.out.println("add index: "+i);
                            tag = "False";//遇到相似的，做标记
                            break;
                        }
                    }
                    if (tag.contains("True")) {
                        layoutSet.add(f.filePath);
                        layoutIndex.add(indexCount);
                        System.out.println("add index: "+indexCount);
                        indexCount++;

                        System.out.println("add new one:  "+layoutSet.size()+"---"+f.filePath);
                    }
                }
        }
        return layoutIndex;
    }

    public static ArrayList<Loop> findCycleInSeq(ArrayList<Integer> intseq){

        ArrayList<Loop> loops=new ArrayList<>();

        for(int i=0;i<intseq.size();i++){
            for(int j=i+1;j<intseq.size();j++){
                if(intseq.get(i)==intseq.get(j)){
                    loops.add(new Loop(i,j));
                }
            }
        }

        return loops;

    }




}

