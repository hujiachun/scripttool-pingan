package com.hjc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.FileChannel;

/**
 * Created by hujiachun on 15/12/12.
 */
public class FileUtil {

    public static void removeLineFromFile(String file, String lineToRemove) {

        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().contains(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

           //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


    /**
     * 移动指定文件或文件夹(包括所有文件和子文件夹)
     *
     * @param fromDir
     *            要移动的文件或文件夹
     * @param toDir
     *            目标文件夹
     * @throws Exception
     */
    public static void MoveFolderAndFileWithSelf(String from, String to) throws Exception {
        try {
            File dir = new File(from);
            // 目标
            to +=  File.separator + dir.getName();
            File moveDir = new File(to);
            if(dir.isDirectory()){
                if (!moveDir.exists()) {
                    moveDir.mkdirs();
                }
            }else{
                File tofile = new File(to);
                dir.renameTo(tofile);
                return;
            }

            //System.out.println("dir.isDirectory()"+dir.isDirectory());
            //System.out.println("dir.isFile():"+dir.isFile());

            // 文件一览
            File[] files = dir.listFiles();
            if (files == null)
                return;

            // 文件移动
            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {
                    MoveFolderAndFileWithSelf(files[i].getPath(), to);
                    // 成功，删除原文件
                    files[i].delete();
                }
                File moveFile = new File(moveDir.getPath() + File.separator + files[i].getName());
                // 目标文件夹下存在的话，删除
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                files[i].renameTo(moveFile);
            }
            dir.delete();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 复制单个文件(可更名复制)
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名(注：目录路径需带文件名)
     * @return
     */
    public static void CopySingleFile(String oldPathFile, String newPathFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制单个文件(原名复制)
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名(注：目录路径需带文件名)
     * @return
     */
    public static void CopySingleFileTo(String oldPathFile, String targetPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            String targetfile = targetPath + File.separator +  oldfile.getName();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(targetfile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制整个文件夹的内容(含自身)
     * @param oldPath 准备拷贝的目录
     * @param newPath 指定绝对路径的新目录
     * @return
     */
    public static void copyFolderWithSelf(String oldPath, String newPath) {
        try {
            new File(newPath).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File dir = new File(oldPath);
            // 目标
            newPath +=  File.separator + dir.getName();
            File moveDir = new File(newPath);
            if(dir.isDirectory()){
                if (!moveDir.exists()) {
                    moveDir.mkdirs();
                }
            }
            String[] file = dir.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath +
                            "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) { //如果是子文件夹
                    copyFolderWithSelf(oldPath + "/" + file[i], newPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * RandomAccessFile的关闭
     *
     * @param f RandomAccessFile对象
     */
    public static void closeRandomAccessFile(RandomAccessFile f)
    {
        if (f != null) {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 输出流的关闭
     *
     * @param out
     */
    public static void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭bufferReader
     *
     * @param br
     */
    public static void closeReader(Reader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}