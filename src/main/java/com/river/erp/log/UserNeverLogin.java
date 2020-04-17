package com.river.erp.log;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserNeverLogin {

    private static final String _inputPath = "D:\\Logs";
    private static final String _outputPath = "D:\\result.txt";

    private static ArrayList<String> fileList = new ArrayList<String>();
    private static Map<String,String[]> resultMap = new HashMap<String, String[]>();

    /**
     * 遍历文件夹内的日志文件，取得文件并进行分析。
     */
    public static void main(String[] args) {
        fileList.clear();
        File file = new File(_inputPath);
        collectFiles(file);
        System.out.println("========File number is [" + fileList.size() + "]========");
        int count = 0;
        for (String fp:fileList) {
            try {
                System.out.println("====[" + ++count + "/" + fileList.size() + "]:" + fp);
                readFile(fp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeFile(resultMap);
    }

    /**
     * 取得文件夹内的所有文件。
     */
    private static void collectFiles(File file){
        File[] files = file.listFiles();
        for (File f:files){
            if (f.isDirectory()){
                collectFiles(f);
            }
            if (f.isFile()){
                fileList.add(f.getAbsolutePath());
            }
        }
    }

    /**
     * 读取文件内容，并逐行处理。
     */
    private static void readFile(String filePath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"GBK"));
        String lineString;
        while ((lineString = bufferedReader.readLine()) != null){
            if (0 != lineString.length()){
                doLine(lineString);
            }
        }
        bufferedReader.close();
    }

    /**
     * 处理每一行日志。
     */
    private static void doLine(String lineStr){
        //在这里做业务逻辑
        /**
         * string[0]是IP
         * string[1]是日期+空格+时间
         * string[5]是账套/ERP号/姓名
         */
        String[] strings = lineStr.split("\t");
        String str_ip = strings[0];
        String str_date = strings[1].split(" ")[0].replaceAll("/","");
        if (!strings[5].equals("NotLogin")) {
            String[] strings1 = strings[5].split("/");
            String str_compId = strings1[0];
            String str_userId = strings1[1];
            String str_name = strings1[2];
            if (resultMap.containsKey(str_userId)){
                //如果已经在map中
                String[] value = resultMap.get(str_userId);
                if (Integer.parseInt(str_date) > Integer.parseInt(value[2])){
                    value[2] = str_date;
                    resultMap.put(str_userId,value);
                }
            }else{
                //如果不在map中
                String[] value = {str_name,str_compId,str_date,str_ip};
                resultMap.put(str_userId,value);
                System.out.println("====User Number:[" + resultMap.size() +"]====");
            }
        }
    }

    /**
     * 将最后结果写入到result文件。
     */
    private static void writeFile(Map<String,String[]> map){
        File file = new File(_outputPath);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(_outputPath,true);
            if (map.entrySet().size() > 0){
                for (Map.Entry<String,String[]> entry:map.entrySet()){
                    fileWriter.write(entry.getKey()+","+entry.getValue()[0]+","+entry.getValue()[1]+","+entry.getValue()[2]+","+entry.getValue()[3]);
                    fileWriter.write("\r\n");
                }
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
