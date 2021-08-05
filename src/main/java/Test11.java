import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test11 {
    int a;
    public static void main(String[] args){
        String s1 = "aaaaaaaa";
        String s2 = "baaaaaaa";
        String s3 = "caaaaaaa";
        String s4 = "daaaaaaa";
        String s5 = "eaaaaaaa";
        String s6 = "faaaaaaa";
        String s7 = "gaaaaaaa";
        String s8 = "haaaaaaa";
        String s9 = "iaaaaaaa";
        String s10 = "jaaaaaaa";
        Map<String,String> hashSet = new LinkedHashMap<>();
        hashSet.put("1",s1);
        hashSet.put("2",s2);
        hashSet.put("3",s3);
        hashSet.put("4",s4);
        hashSet.put("5",s5);
        hashSet.put("6",s6);
        hashSet.put("7",s7);
        hashSet.put("8",s8);
        hashSet.put("9",s9);
        hashSet.put("10",s10);
        Iterator<Map.Entry<String, String>> iter = hashSet.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            System.out.println(entry.getKey()+"------"+entry.getValue());

            if(entry.getKey()=="6"){
                iter.remove();
            }
        }

        iter = hashSet.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, String> entry = iter.next();
            System.out.println(entry.getKey()+"------"+entry.getValue());
        }
        System.out.println("hello");
    }

    public void test1(){
        System.out.println("hahhahaha ");
    }
}
