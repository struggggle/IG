package edu.nju.ics.alex.inputgenerator;

//https://blog.csdn.net/shuiguaiQQ/article/details/84602315
public class MinDistance {
    public static void main(String[] args) {
        String str1 = "adbe";
        String str2 = "abc";
        int[][] dp = new int[str1.length()+1][str2.length()+1];
        int dis = calculateDis(str1, str1.length(), str2, str2.length(), dp);
        display(dp,str1,str2);
        System.out.println("最短编辑距离为:"+dis);

    }

    public static int minDistance(String str1, String str2) {
        int[][] dp = new int[str1.length()+1][str2.length()+1];
        int dis = calculateDis(str1, str1.length(), str2, str2.length(), dp);
        //display(dp,str1,str2);
        System.out.println("Min distance:     "+dis);
        return dis;

    }

    public static int calculateDis(String str1,int index1,String str2,int index2,int[][] dp){
        if(index1==0 && index2==0){
            dp[index1][index2] = 0;
            return 0;
        }

        if(index1==0 && index2>0){
            dp[index1][index2] = index2;
            return index2;
        }

        if(index1>0 && index2==0){
            dp[index1][index2] = index1;
            return index1;
        }

        int t1 = calculateDis(str1, index1-1, str2, index2, dp)+1;
        int t2 = calculateDis(str1, index1, str2, index2-1, dp)+1;
        int t3 = calculateDis(str1, index1-1, str2, index2-1, dp);
        if(str1.charAt(index1-1)!=str2.charAt(index2-1)){
            t3 = t3+1;
        }
        int result =  min(t1,t2,t3);
        dp[index1][index2] = result;
        return result;

    }

    private static int min(int a,int b,int c){
        return a<b?(a<c?a:c):(b<c?b:c);
    }

    private static void display(int[][] dp,String str1,String str2){
        System.out.print("\t\t");
        for(char a :str2.toCharArray()){
            System.out.print(a+"\t");
        }
        System.out.println();
        int count = -1;
        for(int[] a : dp){
            if(count>=0){
                System.out.print(str1.charAt(count));
            }
            System.out.print("\t");

            for(int b:a){
                System.out.print(b+"\t");
            }
            System.out.println();
            count++;

        }
    }


}
