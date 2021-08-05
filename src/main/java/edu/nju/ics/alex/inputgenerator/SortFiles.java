package edu.nju.ics.alex.inputgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortFiles {
    public static ArrayList<FilePath> sortFile(String caseName, String path) {
        ArrayList<FilePath> files = new ArrayList<>();
        File file = new File(path);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if ((f.getAbsolutePath().contains(caseName))&&(!f.getAbsolutePath().contains("DS_Store"))) {
                String filePath = f.getAbsolutePath();
                long time = f.lastModified();
                System.out.println("File time:  "+time);
                files.add(new FilePath(time, filePath));
            }
        }

        Collections.sort(files, new Comparator<FilePath>() {

            @Override
            public int compare(FilePath o1, FilePath o2) {
                long result=o1.time - o2.time;
                return (int)result;
            }
        });

        /*System.out.println("排序后:");
        for (FilePath file1 : files) {
            System.out.println("时间：" + file1.time + " 路径：" + file1.filePath);
        }*/

        return files;
    }

}
