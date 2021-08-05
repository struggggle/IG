package edu.nju.ics.alex.inputgenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//这部分代码在最后测试脚本执行结束后调用

public class CompareImages {
    public static void main(String[] args) throws Exception {
        String path = "/Users/wenjieli/Desktop/decodedImages/images";
        String image1=MainKt.getDirectoryPath()+"/aaaa/6.jpg";
        String image2=MainKt.getDirectoryPath()+"/aaaa/13.jpg";
        boolean isSame=compareTwoImages(image1,image2);
        System.out.println("isSame: "+isSame);
        double similar=similarP(image1,image2);
        //double similar=ImageSimilar2.compareImage(image1,image2);//ImageSimilar.calSimilarity(image1,image2);
        System.out.println("similar: "+similar);
    }

    //obtain the set of unique images
    public static ArrayList<String> getUniqueImages(String path){
        ArrayList<String> differentImages = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String fullName=tempList[i].getAbsolutePath();
                if(fullName.endsWith(".DS_Store")&&(fullName.contains(".jpg"))){
                    continue;
                }else {
                    if(differentImages.size()==0){
                        System.out.println("lwjfullName:  "+fullName);
                        differentImages.add(fullName);
                    }else {
                        boolean isDifferent=isDifferentImage(fullName,differentImages);
                        if(isDifferent){
                            differentImages.add(fullName);
                        }
                    }
                }
            }
            if (tempList[i].isDirectory()) {
            }
        }
        return differentImages;
    }

    public static ArrayList<String> getUniqueImagesPrefix(String path,ArrayList<String> imagesEventBefore){
        ArrayList<String> differentImages = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                String fullName=tempList[i].getAbsolutePath();
                if(fullName.endsWith(".DS_Store")&&(fullName.contains(".jpg"))){
                    continue;
                }else {
                    if(differentImages.size()==0){
                        System.out.println("lwjfullName:  "+fullName);
                        differentImages.add(fullName);
                    }else {
                        boolean isDifferent=isDifferentImage(fullName,differentImages);
                        if(isDifferent){
                            differentImages.add(fullName);
                        }
                    }
                }
            }
            if (tempList[i].isDirectory()) {
            }
        }

        if(imagesEventBefore.size()>0) {
            String temp = imagesEventBefore.get(0);
            if (differentImages.size() == 0) {
                System.out.println("lwjfullName11:  " + temp);
                differentImages.add(temp);
            }
            for (int j = 0; j < imagesEventBefore.size(); j++) {
                String fullName1 = imagesEventBefore.get(j);
                boolean isDifferent = isDifferentImage(fullName1, differentImages);
                if (isDifferent) {
                    differentImages.add(fullName1);
                }
            }
        }
        return differentImages;
    }

    public static boolean isDifferentImage(String image,List<String> imageList){
        for(int i=0;i<imageList.size();i++){
            String temp=imageList.get(i);
            if(compareTwoImages(image,temp))
                return false;//if same, return false
        }
        return true;
    }

    public static boolean isRepeatedImage(String image,List<String> imageList){
        System.out.println("call isRepeatedImage   "+imageList.size());
        for(int i=0;i<imageList.size();i++){
            String temp=imageList.get(i);
            //if(compareTwoImages(image,temp))//以前是比较两张图片是否完全相同
            //File file1 = new File(image);
            //File file2 = new File(temp);
            double similar=0.1;
            //similar=ImageSimilar2.compareImage(image,temp);//似乎不靠谱
            similar=similarP(image,temp);
            System.out.println("lwjimage similar: "+similar);
//            try {
//                //similar = ImageSimilar.calSimilarity(file1, file2);//现在比较两张图片是否极为相似：99%
//                ImageSimilar2.compareImage(image,temp);
//                System.out.println("image similar: "+similar);
//            }catch (IOException e){
//                e.printStackTrace();
//            }
            if(similar>0.9) {
                //System.out.println("image similar: "+similar);
                System.out.println("image: " + image);
                System.out.println("temp: " + temp);
                return true;//if very similar
            }
        }
        return false;
    }


    public static boolean compareTwoImages(String path1,String path2){
        File file1 = new File(path1);
        File file2 = new File(path2);
        BufferedImage image1=null;
        BufferedImage image2=null;
        try {
            image1 = ImageIO.read(file1);
            image2 = ImageIO.read(file2);
        }catch (IOException e){
            System.out.println(e);
        }
        boolean compareResult=compare(image1,image2);
        System.out.println("image1: "+path1);
        System.out.println("image2: "+path2);
        System.out.println("compare result: "+compareResult);//if they are the same images, returns true
        return compareResult;
    }

    public static double similarP(String path1,String path2){
        File file1 = new File(path1);
        File file2 = new File(path2);
        BufferedImage image1=null;
        BufferedImage image2=null;
        try {
            image1 = ImageIO.read(file1);
            image2 = ImageIO.read(file2);
        }catch (IOException e){
            System.out.println(e);
        }
        double compareResult=compare1(image1,image2);
//        System.out.println("image1: "+path1);
//        System.out.println("image2: "+path2);
        System.out.println("compare result: "+compareResult);
        return compareResult;
    }

    public static boolean compare(BufferedImage image1,BufferedImage image2) {

        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return false;
        }
        // get the internal data
        int w = image1.getWidth();
        int h = image2.getHeight();
        int[] argb1 = new int[w * h];
        int[] argb2 = new int[w * h];
        image1.getRGB(0, 0, w, h, argb1, 0, w);
        image2.getRGB(0, 0, w, h, argb2, 0, w);

        // in this case we have to manually compare the alpha channel as the rest is garbage.
        final int length = w * h;//这里比较的是所有的像素
        for (int i = 0; i < length; i++) {
            if ((argb1[i] & 0xFF000000) != (argb2[i] & 0xFF000000)) {
                return false;
            }
        }
        return Arrays.equals(argb1, argb2);
    }

    public static double compare1(BufferedImage image1,BufferedImage image2) {

        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            System.out.println("compare: "+"different image size"+image1.getWidth()+"-"+image1.getHeight()+"   "+image2.getWidth()+"-"+image2.getHeight());
            return 0.0;
        }else {
            System.out.println("compare: "+"same image size"+image1.getWidth()+"-"+image1.getHeight()+"   "+image2.getWidth()+"-"+image2.getHeight());

        }
        // get the internal data
        int w = image1.getWidth();
        int h = image2.getHeight();
        int[] argb1 = new int[w * h];
        int[] argb2 = new int[w * h];
        image1.getRGB(0, 0, w, h, argb1, 0, w);
        image2.getRGB(0, 0, w, h, argb2, 0, w);

        // in this case we have to manually compare the alpha channel as the rest is garbage.
        final int length = w * h;//这里比较的是所有的像素
        int differentCount=0;
        for (int i = 0; i < length; i++) {
            if (argb1[i]!= argb2[i]) {
                //System.out.println(i+":  "+argb1+"  "+argb2);//639928:  [I@4783da3f  [I@378fd1ac
                differentCount++;
            }
        }
        double result=1-differentCount/length*1.0;
        return result;
    }
}
