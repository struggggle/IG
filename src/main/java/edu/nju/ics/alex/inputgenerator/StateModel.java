package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;

public class StateModel {
    public static ArrayList<State> stateSet=new ArrayList<State>();
    public static ArrayList<State_Event> stateEventSet=new ArrayList<State_Event>();
    public static ArrayList<StateEdge> stateEdges=new ArrayList<StateEdge>();
    public static int edges[][]=new int[100][100];

    public static String layoutfloderpath=MainKt.getDirectoryPath()+"/testOutput/wordPress/layout/";
    String testcasepath=MainKt.getDirectoryPath()+"/wordPress/";

    //public static void main(String[] arges){
    public static int[][] getedegs(){
        String testcasepath="";
        String target=MainKt.getDirectoryPath()+"/wordPress/browseBlog--long.sh";
        TestCase testCase=new TestCase();
        testCase.testCasePath=target;
        ArrayList<String> layoutList=new ArrayList<String>();
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout1.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout2.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout3.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout4.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout5.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout6.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout7.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout8.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout9.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout10.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout11.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout12.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout13.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout14.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout15.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout16.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout17.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout18.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout19.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout20.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout21.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout22.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout23.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout24.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout25.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout26.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout27.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout28.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout29.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout30.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout31.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout32.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout33.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout34.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout35.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout36.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout37.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout38.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout39.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout40.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout41.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout42.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout43.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout44.xml");
        layoutList.add("$directoryPath/testOutput/wordPress/layout/browseBlog--long/browseBlog--long.shLayout45.xml");
        testCase.layoutList=layoutList;
        getStateModel(testCase);


        //输出测试结果
        System.out.println("States:   "+stateSet.size());
        System.out.println("Edges:   "+stateEdges.size());
        edges=getEdges(stateEdges);
        int edgeCounter=0;
        for(int k=0;k<stateSet.size();k++) {
            for (int m = 0; m <stateSet.size(); m++) {
                System.out.print(edges[k][m] + ", ");
                if(edges[k][m]==1){
                    edgeCounter++;
                }
            }
            System.out.println();
        }
        System.out.println("Edge counter: "+edgeCounter);
        return edges;
    }

    public static int[][] getEdges(ArrayList<StateEdge> stateEdges){
        int result[][]=new int[100][100];
        for(int i=0;i<stateEdges.size();i++){
            StateEdge stateEdge=stateEdges.get(i);
            int x1=stateEdge.from;
            int x2=stateEdge.to;
            result[x1][x2]=1;
        }
        return result;
    }

    public static void getStateModel(TestCase testCase){
        //System.out.println("Name:"   +file.getName());//browseBlog--long.sh
        ArrayList<String> layoutList=testCase.layoutList;

        for(int i=0;i<layoutList.size();i++){//这里循环45次
            String layoutPath=layoutList.get(i);
            if(i==0){//if it is the initial state
                State state=new State();
                State_Event stateEvent=new State_Event();
                state.index=1;
                state.layout=layoutPath;
                stateEvent.indexE=1;
                stateEvent.indexR=1;
                stateEvent.layoutE=layoutPath;
                stateEvent.layoutR=layoutPath;
                stateSet.add(state);
                stateEventSet.add(stateEvent);
            }
            if(i!=0){
                int stateSetSize=stateSet.size();
                boolean newstate=true;
                int edgeTo=0;
                for(int j=0;j<stateSetSize;j++){
                    System.out.println("j:   "+j);
                    //比较是否是相似的state
                    int comparedIndex=stateSet.get(j).index;
                    String comparedLayout=stateSet.get(j).layout;
                    //System.out.println("layoutPath:   "+layoutPath);
                    //System.out.println("comparedLayout:   "+comparedLayout);
                    String compareResult=CallPython.callPython(layoutPath,comparedLayout);
                    //System.out.println("compareResult:    "+compareResult);
                    if(compareResult.equals("True")){
                        State_Event stateEvent=new State_Event();
                        stateEvent.indexE=comparedIndex;
                        stateEvent.indexR=i+1;
                        stateEvent.layoutE=comparedLayout;
                        stateEvent.layoutR=layoutPath;
                        stateEventSet.add(stateEvent);

                        edgeTo=comparedIndex;
                        newstate=false;

                    }
                    if(compareResult.equals("False")){
                        State_Event stateEvent=new State_Event();
                        stateEvent.indexE=stateSet.size()+1;
                        stateEvent.indexR=i+1;
                        stateEvent.layoutE=layoutPath;
                        stateEvent.layoutR=layoutPath;

                        //获得上一个event的事件信息
                        stateEventSet.add(stateEvent);
                    }
                }
                if(newstate){//如果是new state
                    State state=new State();
                    state.index=stateSet.size()+1;
                    state.layout=layoutPath;
                    stateSet.add(state);

                    StateEdge edge=new StateEdge();
                    edge.from=stateEventSet.get(i-1).indexE;
                    edge.to=stateSetSize+1;
                    stateEdges.add(edge);


                }else{
                    StateEdge edge=new StateEdge();
                    edge.from=stateEventSet.get(i-1).indexE;//这里似乎也对
                    edge.to=edgeTo;
                    stateEdges.add(edge);
                }
            }

        }




    }
}
