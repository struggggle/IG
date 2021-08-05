package edu.nju.ics.alex.inputgenerator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * 这里的目的是存储对单个loop变化过程中的数据。
 * 仅仅涉及到heap dump，image data，时间戳。
 * */
public class UZipFile
{
    /**
     * 解压到指定目录
     */
    public static void unZipFiles(String zipPath,String descDir)throws IOException
    {
        unZipFiles(new File(zipPath), descDir);
    }
    /**
     * 解压文件到指定目录
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile,String descDir)throws IOException
    {
        File pathFile = new File(descDir);
        if(!pathFile.exists())
        {
            pathFile.mkdirs();
        }
        //解决zip文件中有中文目录或者中文文件
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
        for(Enumeration entries = zip.entries(); entries.hasMoreElements();)
        {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if(!file.exists())
            {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if(new File(outPath).isDirectory())
            {
                continue;
            }
            //输出文件路径信息
            System.out.println(outPath);
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while((len=in.read(buf1))>0)
            {
                out.write(buf1,0,len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************finish unzip********************");
    }
    public static void main(String[] args) throws IOException {
        /**压缩文件*/
        String srcPath=MainKt.getDirectoryPath()+"/testOutput/QKSMS-v3.8.1browseContent.sh";
        String dstPath=MainKt.getDirectoryPath()+"/testOutput/QKSMS-v3.8.1browseContent.shCompress.zip";
        compress(srcPath , dstPath);
        /**
         * 解压文件
         */
        File zipFile = new File(MainKt.getDirectoryPath()+"/testOutput/temp5.zip");
        String path = MainKt.getDirectoryPath()+"/testOutput/";
        unZipFiles(zipFile, path);
        //change folder name
        File file1 = new File(MainKt.getDirectoryPath()+"/testOutput/temp");
        //将原文件夹更改为A，其中路径是必要的。注意
        file1.renameTo(new File(MainKt.getDirectoryPath()+"/testOutput/temp5"));
    }

    public static void createfolder(String zipPath, String zipFolderName,String parentPath, String newName){
        File zipFile = new File(zipPath);
        try {
            unZipFiles(zipFile, parentPath);
        }catch (IOException e){
            e.printStackTrace();
        }
        File file1 = new File(zipFolderName);
        //将原文件夹更改为A，其中路径是必要的。注意
        file1.renameTo(new File(newName));
    }


    /**compress folders*/
    static final int BUFFER = 8192;

    public static void compress(String srcPath , String dstPath) throws IOException{
        File srcFile = new File(srcPath);
        File dstFile = new File(dstPath);
        if (!srcFile.exists()) {
            throw new FileNotFoundException(srcPath + "不存在！");
        }

        FileOutputStream out = null;
        ZipOutputStream zipOut = null;
        try {
            out = new FileOutputStream(dstFile);
            CheckedOutputStream cos = new CheckedOutputStream(out,new CRC32());
            zipOut = new ZipOutputStream(cos);
            String baseDir = "";
            compress(srcFile, zipOut, baseDir);
        }
        finally {
            if(null != zipOut){
                zipOut.close();
                out = null;
            }

            if(null != out){
                out.close();
            }
        }
    }

    private static void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException{
        if (file.isDirectory()) {
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    /** 压缩一个目录 */
    private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException{
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            compress(files[i], zipOut, baseDir + dir.getName() + "/");
        }
    }

    /** 压缩一个文件 */
    private static void compressFile(File file, ZipOutputStream zipOut, String baseDir)  throws IOException{
        if (!file.exists()){
            return;
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }

        }finally {
            if(null != bis){
                bis.close();
            }
        }
    }
}