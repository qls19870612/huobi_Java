package com.huobi.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/**
 * Created by liangsong on 2018/4/14
 */
public class FileOperator {
    private static final Logger logger = LoggerFactory.getLogger(FileOperator.class);
    public static final String NEX_LINE = System.getProperty("line.separator");

    public static void openFile(String luaPath) {
        try {
            Runtime.getRuntime().exec("C:/Windows/explorer.exe " + luaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openFileAndSelect(String luaPath) {
        try {
            Runtime.getRuntime().exec("C:/Windows/explorer.exe /select," + luaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取控制台打印的文字
     * @param in
     * @return
     * @throws Exception
     */
    public static String readInputstream(InputStream in) {
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String runBat(String batStr) throws IOException {
        Process exec = Runtime.getRuntime().exec(batStr);
        //        exec.waitFor();
        String info = readInputstream(exec.getInputStream());

        String error = readInputstream(exec.getErrorStream());

        String ret = "";
        if (StringUtils.isNotEmpty(info)) {
            ret += "info:" + info;
        }
        if (StringUtils.isNotEmpty(error)) {
            ret += "error:" + error;
        }
        exec.destroy();
        return ret;
    }

    /**
     * 从打包的资源中读取
     * @param url
     * @return
     */
    public static String getResourceAsText(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        try {

            InputStream resourceAsStream = FileOperator.class.getResourceAsStream(url);
            BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream));

            String s1 = "";
            while ((s1 = br.readLine()) != null) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(FileOperator.NEX_LINE);
                }
                stringBuilder.append(s1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * 先从外部配置文件读取，如果读不到，再从打包的配置文件读取默认配置
     * @param configUrl
     * @param resourceUrl
     * @return
     */
    public static String getConfig(String configUrl, String resourceUrl) {
        String s = readFiles(new File(configUrl));
        if (s == null) {
            s = getResourceAsText(resourceUrl);
        }
        return s;
    }

    public static String getConfig(String url) {

        return getConfig(url, url);
    }

    public static interface Filter<T> {
        /**
         * Decides if the given directory entry should be accepted or filtered.
         *
         * @param   entry
         *          the directory entry to be tested
         *
         * @return  {@code true} if the directory entry should be accepted
         *
         */
        boolean accept(T entry);
    }

    public static ArrayList<File> getAllFiles(File root, String extName) {
        ArrayList<File> ret = new ArrayList<File>();
        readToList(root, ret, extName);

        return ret;
    }

    public static ArrayList<File> getAllFiles(File root, Filter<File> filter) {
        ArrayList<File> ret = new ArrayList<File>();
        try {
            readToList(root, ret, filter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
    private static void readToList(File root, ArrayList<File> ret, Filter<File> filter) throws IOException {

        if (root.isDirectory()) {
            File[] subFiles = root.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    readToList(file, ret, filter);
                }
            }
        } else {
            if (filter.accept(root)) {
                ret.add(root);
            }
        }
    }
    public static String readFiles(File file) {
        String code = null;
        try {
            code = get_charset(file);
        } catch (Exception e) {
            //            System.out.println(e.getMessage());
            return null;
        }
        String ret = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = null;
        FileInputStream fileInput = null;
        InputStreamReader input = null;
        try {
            fileInput = new FileInputStream(file.getPath());
            input = new InputStreamReader(fileInput, code);
            reader = new BufferedReader(input);
            String tempStr = null;
            stringBuffer = new StringBuffer();
            while ((tempStr = reader.readLine()) != null) {
                stringBuffer.append(tempStr).append(NEX_LINE);
            }

        } catch (IOException e) {
                        System.out.println(e.getMessage());
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
                if (input != null) {
                    input.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (stringBuffer != null) {
            ret = stringBuffer.toString();
            int lastIndex = ret.lastIndexOf(NEX_LINE);
            if (lastIndex != -1) {
                ret = ret.substring(0, lastIndex);
            }
        }


        return ret;
    }

    public static Boolean writeFile(File file, String contenxt) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        Boolean ret = false;
        BufferedWriter bufferedWriter = null;
        FileOutputStream writerStream = null;
        OutputStreamWriter outputStream = null;
        try {
            writerStream = new FileOutputStream(file);
            outputStream = new OutputStreamWriter(writerStream, StandardCharsets.UTF_8);
            bufferedWriter = new BufferedWriter(outputStream);
            bufferedWriter.write(contenxt);
            bufferedWriter.flush();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writerStream != null) {
                    writerStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    private static void readToList(File root, ArrayList<File> ret, String extName) {
        if (root.isDirectory()) {
            File[] subFiles = root.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    readToList(file, ret, extName);
                }
            }
        } else {
            if (root.getName().endsWith(extName)) {
                ret.add(root);
            }
        }
    }



    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static String get_charset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];//首先3个字节
        try {
            boolean checked = false;
            ;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                // int len = 0;
                int loc = 0;

                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                    {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                        // (0x80
                        // - 0xBF),也可能在GB编码内
                        {
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

            }

            bis.close();
        } catch (Exception e) {
            //            e.printStackTrace();
            //            System.out.println("get_charset:" + e.getMessage());
        }

        return charset;
    }

    public static void turnUTF8withoutBOM(File file, File targetFile) throws IOException {
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        BufferedReader br = null;
        BufferedWriter bw = null;
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
        int i = 0;
        String str = "";
        while ((str = br.readLine()) != null) {
            if (i == 0)//读取第一行，将前三个字节去掉，重新new个String对象
            {
                byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                str = new String(bytes, 3, bytes.length - 3);
                bw.write(str + NEX_LINE);
                i++;
            } else {
                bw.write(str + NEX_LINE);
            }
        }
        br.close();
        bw.close();
    }

}
