package edu.nju.ics.alex.inputgenerator.layout;

import java.io.File;
import java.util.ArrayList;

import static edu.nju.ics.alex.inputgenerator.CompareImages.compareTwoImages;

public class Satisfy {
    public static ArrayList<int[]> imageData=new ArrayList<int[]>();
    public static int imageCount=0;
    public static ArrayList<String> distinctImageSet=new ArrayList<String>();
    public static void main(String[] args){
        String pathSet[]={
                "$directoryPath/testOutput/de.danoeh.antennapod_2010195open_addPodcast.sh_1_3/images",
                "$directoryPath/testOutput/de.danoeh.antennapod_2010195broswe_podcasts_loop.sh_2_4/images",
                "$directoryPath/testOutput/eu.siacs.conversations_402send_images.sh_6_10/images",
                "$directoryPath/testOutput/fr.neamar.kiss_180clickItem.sh_3_7/images",
                "$directoryPath/testOutput/mtgfam_3.6.6.dbg.3MoJhoSto.sh_3_5/images",
                "$directoryPath/testOutput/net.nurik.roman.muzei_340300myPhotos.sh_10_15/images",
                "$directoryPath/testOutput/NewsBlur_v10.2.0ChangeListStyle_openStory.sh_8_10/images",
                "$directoryPath/testOutput/NewsBlur_v10.2.0Browse_SAVED_STORIES_two.sh_7_9/images",
                "$directoryPath/testOutput/NewsBlur_v10.2.0Browse_SAVED_STORIES_two.sh_5_10/images",
                "$directoryPath/testOutput/QKSMSv3.8.1browseContent.sh_1_3/images",
                "$directoryPath/testOutput/slide_for_reddit_6.6.1browseImages.sh_6_11/images",
                "$directoryPath/testOutput/Slideshow_2.9.0openImageList.sh_2_4/images",
                "$directoryPath/testOutput/Slideshow_2.9.0browseImage.sh_3_5/images",
                "$directoryPath/testOutput/tusky_v13.1open_an_image.sh_4_6/images",
                "$directoryPath/testOutput/tusky_v13.1openCommentGroup.sh_5_7",
                "$directoryPath/testOutput/wpandroid_16.3_universaleditBlogs.sh_9_13/images",
                "$directoryPath/testOutput/wpandroid_16.3_universaleditBlogs.sh_9_11/images",
                "$directoryPath/testOutput/wpandroid_16.3_universalviewBlog.sh_9_12/images",
                "$directoryPath/testOutput/wpandroid_16.3_universalopenMedia.sh_9_13/images"
        };
        //String path="$directoryPath/testOutput/wpandroid_16.3_universaleditBlogs.sh/images/recordImages";
        for(int i=0;i<pathSet.length;i++){
            imageCount=0;
            distinctImageSet.clear();
            System.out.println("liwenjie------:"+imageCount+"-----------"+distinctImageSet.size());

            int[] count={0,0};
            String path=pathSet[i];
            File file = new File(path);		//获取其file对象
            countImages(file);
            System.out.println("Path--"+i+"--"+"imageCount: "+imageCount);
            count[0]=imageCount;
            countDistinctImages(file);
            System.out.println("Path--"+i+"--"+"distinctImageCount: "+distinctImageSet.size());
            count[1]=distinctImageSet.size();
            imageData.add(count);
        }
        for(int i=0;i<imageData.size();i++){
            int[] count=imageData.get(i);
            System.out.println("index"+i+"--"+"imageCount: "+count[0]+"---"+"distinctImageCount: "+count[1]);
        }

    }
    public static void countImages(File file){
        File[] fs = file.listFiles();
        for(File f:fs){
            if(f.isDirectory())	//若是目录，则递归打印该目录下的文件
                countImages(f);
            if(f.isFile()) {        //若是文件，直接打印
                String fileName=f.toString();
                if((!fileName.contains(".DS_Store"))&&(fileName.contains(".jpg")&&(!fileName.contains("displaying")))){
                    imageCount++;
                    System.out.println(fileName);
                }

            }
        }
    }

    public static void countDistinctImages(File file){
        File[] fs = file.listFiles();
        for(File f:fs){
            if(f.isDirectory())	//若是目录，则递归打印该目录下的文件
                countDistinctImages(f);
            if(f.isFile()) {        //若是文件，直接打印
                String fileName=f.toString();
                if((!fileName.contains(".DS_Store"))&&(fileName.contains(".jpg")&&(!fileName.contains("displaying")))){
                    //接下里进行比较并添加到list集合
                    if(distinctImageSet.size()==0){
                        distinctImageSet.add(fileName);
                        System.out.println("add image to list");
                    }else {
                        //下面进行遍历操作
                        boolean isNotExist=true;
                        for(int i=0;i<distinctImageSet.size();i++){
                            String fileName_temp=distinctImageSet.get(i);
                            boolean isSame=compareTwoImages(fileName,fileName_temp);
                            if(isSame){
                                isNotExist=false;
                                break;
                            }
                        }
                        if(isNotExist){
                            distinctImageSet.add(fileName);
                        }
                    }
                }
            }
        }
    }
}
