package edu.nju.ics.alex.inputgenerator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JustTest {
    public static void main(String[] args){


    }

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
}
