package 工具.文件名称加密;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*javac -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar -encoding UTF-8 -d . 生成本地文件记录加密定制版.java*/
/*java -cp .;commons-io-2.8.0.jar;fastjson-1.2.75.jar 工具.文件名称加密.生成本地文件记录加密定制版 */

/**
 * 基于:工具.生成文件记录.生成本地文件记录 1.0.4版本修改
 * 用以输出本地加密文件记录
 * @author aotmd
 * @version 1.0.4
 * @date 2021/3/15 12:28
 */
public class 生成本地文件记录加密定制版 {
    public static final String 已加密标记_MD = "已加密标记.md";
    /**文件名称对应加密关系*/
    private Map<String,String> map = new HashMap<>();
    /** 待输出缓冲区*/
    private StringBuffer sb=new StringBuffer();
    /** 文件,文件夹计数*/
    private long n=0,m=0;

    public static void main (String[]args)  {
        if (args.length!=2){
            System.out.println("需要两个参数,第一个为是否为严格模式,第二个为json路径");
            return;
        }
        生成本地文件记录加密定制版 notes=new 生成本地文件记录加密定制版();
        /*开始初始化*/
        notes.start(args[0],new File(args[1]));
    }

    /**开始生成加密文件记录
     * @param flag 严格模式,true则调用绝对路径,false则调用相对路径
     * @param jsonPath json路径
     */
    private void start(String flag,File jsonPath){
        /*校验参数*/
        if (jsonPath==null||!jsonPath.exists()||jsonPath.isDirectory()){
            System.out.println("json文件不存在!");
            return;
        }
        if (!Objects.equals(flag, "true") && !Objects.equals(flag, "false")) {
            System.out.println("第一个参数无效,需要为true或者false");
            return;
        }
        System.out.println("当前给出的JSON路径为:"+jsonPath.getPath());
        /*导入解密map*/
        this.importJson(jsonPath);

        /*开始获取文件记录*/
        String indexPath;
        if (Objects.equals(flag, "true")){
            indexPath=map.get("文件名称加密解密绝对地址");
        }else {
            indexPath=jsonPath.getParent()+File.separator+map.get("文件名称加密解密相对地址");
        }
        File index=new File(indexPath);
        if(!index.exists()){
            System.out.printf("文件路径%s不存在!",index.getPath());
            return;
        }
        this.generateFileRecords(index);

        /*事务性说明日志:*/
        /*路径*/
        sb.append("路径:").append(indexPath).append("\n");
        /*文件数量,文件夹数量*/
        sb.append("文件数量:").append(this.n).append(",文件夹数量:").append(this.m).append("\n");
        /*获取当前时间*/
        String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        sb.append("记录时间:").append(date).append("\n");
        /*输出文件记录到控制台*/
        // System.out.println(sb);

        /*输出文件记录到文件*/
        BufferedWriter bw;
        try {
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(index.getPath()+File.separator+"文件记录.txt"), StandardCharsets.UTF_8));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 导入json
     * @param jsonPath json地址
     */
    private void importJson(File jsonPath){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonPath), StandardCharsets.UTF_8));
            char[] buffer = new char[100000];
            StringBuilder sb = new StringBuilder();
            int n;
            while ((n = br.read(buffer)) != -1) {
                sb.append(buffer, 0, n);
            }
            br.close();
            //将字符串转换为HashMap
            map= JSON.parseObject(sb.toString(),new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 生成文件记录
     */
    private void generateFileRecords(File file) {
        char[] empty=new char[200];
        Arrays.fill(empty, '1');
        cycle(file,1,empty);
    }
    public void cycle(File file, int count, char[] empty) {
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            /*输出名称,未加密前的名称,该目录下的目录与文件数量和,目录大小*/
            sb.append(String.format("%s [%s] [%d] [%s]", file.getName(),map.get(file.getName()), 0, this.getPathSize(file))).append('\n');
            return;
        }
        /*输出名称,未加密前的名称,该目录下的目录与文件数量和,目录大小*/
        sb.append(String.format("%s [%s] [%d] [%s]", file.getName(),map.get(file.getName()), this.getTheNumberOfFiles(file), this.getPathSize(file))).append('\n');
        List<File> list = Arrays.asList(fileArray);
        /*排序*/
        Collections.sort(list, (f1, f2) -> {
            int a = f1.isDirectory() ? 1 : 0;
            int b = f2.isDirectory() ? 1 : 0;
            /*文件在文件夹前面*/
            int result = a - b;
            /*相等则比较文件名称,A-Z然后中文排序,升序*/
            Collator collator = Collator.getInstance(Locale.CHINA);
            if (a - b == 0) {
                result = collator.compare(f1.getName(), f2.getName());
            }
            return result;
        });
        for (int i = 0; i < list.size(); i++) {
            File temp = list.get(i);
            if (temp.isDirectory()) {
                m++;
                empty[count - 1] = '1';
                /*如果上一个是文件那么空一行*/
                if (i > 0 && !list.get(i - 1).isDirectory()) {
                    sb.append(printFilePrefix(empty, count).replaceAll("\\s+$","")).append('\n');
                }
                /*输出前缀*/
                sb.append(printFilePrefix(empty, count - 1));
                /*如果是最后一个文件夹*/
                if (i == list.size() - 1) {
                    sb.append("└─");
                    empty[count - 1] = '0';
                } else {
                    sb.append("├─");
                }
                /*迭代*/
                cycle(temp, count + 1, empty);
            } else {/*如果是文件*/
                /*如果是加密标志则跳过,不记录*/
                if (temp.getName().equals(已加密标记_MD)){continue;}
                n++;
                sb.append(printFilePrefix(empty, count - 1));
                /*如果该目录下没有文件夹*/
                if (!list.get(list.size() - 1).isDirectory()) {
                    sb.append("    ");
                } else {
                    sb.append("│  ");
                }
                /*输出名称,未加密前的名称,大小*/
                sb.append(String.format("%s [%s] \t [%s]", temp.getName(),map.get(temp.getName()), this.getPathSize(temp))).append('\n');
            }
            /*如果是最后一个*/
            if (i == list.size() - 1) {
                sb.append(printFilePrefix(empty, count - 1).replaceAll("\\s+$","")).append('\n');
            }
        }
    }
    /**
     * 添加文件前缀
     * @param empty 空位控制符
     * @param n 减少输出数量
     */
    private String printFilePrefix(char[] empty,int n){
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = empty[i];
            if (c == '0') {
                s.append("    ");
            } else if (c == '1') {
                s.append("│  ");
            }
        }
        return s.toString();
    }
    /**
     * 获取路径下文件数量
     *
     * @param file 文件路径
     * @return 长整型
     */
    private  long getTheNumberOfFiles(File file) {
        long n=0;
        if (!file.isDirectory()) {
            return 0;
        }
        File[] array = file.listFiles();
        if (array != null) {
            for (File file1 : array) {
                if (file1.isDirectory()) {
                    n+=getTheNumberOfFiles(file1);
                } else {
                    n++;
                }
            }
        }
        return n;
    }
    /**
     * 获取路径大小
     * @param file 文件路径
     * @return 字符串,保留两位小数
     */
    private String getPathSize(File file){
        if (file.isDirectory()){
            return this.formatFileSize(FileUtils.sizeOfDirectory(file));
        } else {
            return this.formatFileSize(file.length());
        }
    }
    /**
     * 转换文件夹大小
     * @param size 文件大小
     * @return 字符串
     */
    private   String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}