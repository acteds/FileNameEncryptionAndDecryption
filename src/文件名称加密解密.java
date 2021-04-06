package 工具.文件名称加密;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 文件名称加密解密.java
java -cp .;fastjson-1.2.75.jar 工具.文件名称加密.文件名称加密解密
*/
/**
 * 参数详解:<br/>
 * +表示加密 第二三参数为文件,文件夹序列,第四个参数需要用引号,并且最后一个字符不能是斜杠,否则会转义,<br/>
 * -表示解密 第二个参数表示是否启用严格模式(true/false),第三个参数为解密的json文件,<br/>
 * 严格模式即是否将转换地址变为json内部记录的绝对地址,<br/>
 * 如果为false则转换json文件旁的特定文件夹;<br/>
 * 加密解密参数示例:<br/>
 * - true "F:\aotmd_guizheng\Cache\cache\初音的青葱index.json"<br/>
 * + 1 1 "F:\aotmd_guizheng\Cache\cache\初音的青葱"
 * @author aotmd
 * @version 1.0
 * @date 2021/3/12 14:42
 */
public class 文件名称加密解密 {
    public static final String 已加密标记_MD = "已加密标记.md";
    private static final String 文件加密名称 = "数据备份";
    private static final String 文件夹加密名称 = "分片";
    /**文件更名记录*/
    private Map<String,String> map = new HashMap<>();
    private int fileNO=1;
    private int folderNO=1;
    private boolean 严格模式;
    private StringBuffer erreo=new StringBuffer();

    public 文件名称加密解密(boolean 严格模式) {
        this.严格模式 = 严格模式;
    }

    public 文件名称加密解密(boolean 严格模式,int fileNO, int folderNO) {
        this.严格模式 = 严格模式;
        this.fileNO = fileNO;
        this.folderNO = folderNO;
    }

    public static void main(String[] args) {
        if (args.length!=4&&args.length!=3){
            System.out.println("参数不正确");
            return;
        }
        if (Objects.equals(args[0], "+")) {
            Matcher isNum = Pattern.compile("^\\d+$").matcher(args[1]);
            Matcher isNum2 = Pattern.compile("^\\d+$").matcher(args[2]);
            if (!isNum.matches()&&!isNum2.matches()){
                System.out.println("第二三参数不是数字!");
                return;
            }
            File path = new File(args[3]);
            if (!path.exists()) {
                System.out.println("要加密的路径不存在!");
                return;
            } else if (path.isFile()) {
                System.out.println("不是目录!");
                return;
            }
            文件名称加密解密 a = new 文件名称加密解密(false,Integer.parseInt(args[1]),Integer.parseInt(args[2]));
            a.startEncryption(path);
        } else if (Objects.equals(args[0], "-")) {
            文件名称加密解密 a;
            if (Objects.equals(args[1], "true")) {
                a = new 文件名称加密解密(true);
            } else if (Objects.equals(args[1], "false")) {
                a = new 文件名称加密解密(false);
            } else {
                System.out.println("第二个参数错误!");
                return;
            }
            if (args[2]==null){
                System.out.println("没有json路径!");
                return;
            }
            a.startDecrypt(new File(args[2]));
        } else {
            System.out.println("加密解密参数错误!");
        }
    }


    /**
     * 开始加密
     * @param path 加密地址
     */
    public void startEncryption(File path){
        File flag=new File(path.getPath()+File.separator+已加密标记_MD);
        if (flag.exists()){
            System.out.println("该目录已加密!不需要再加密!");
            return;
        }
        File[] fileArray=path.listFiles();
        for (File file : fileArray) {
            //先将目录加密后递归
            String name=file.getName();
            if (file.isDirectory()) {
                if (name.contains(文件夹加密名称)){
                    System.out.println("该目录已有加密文件夹!请解密后加密!");
                    return;
                }
            } else {
                if (name.contains(文件加密名称)){
                    System.out.println("该目录已有加密文件!请解密后加密!");
                    return;
                }
            }
        }


        map.put("文件名称加密解密绝对地址",path.getPath());
        map.put("文件名称加密解密相对地址",path.getName());

        recursion(path);

        map.put("最后的文件序列为",fileNO+"");
        map.put("最后的文件夹序列为",folderNO+"");
        exportJson(path);

        /*创建已加密标记*/
        try {
            flag.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始解密
     * @param jsonPath json路径
     */
    public void startDecrypt(File jsonPath){
        if (jsonPath==null||jsonPath.isDirectory()){
            System.out.println("json文件不存在!");
            return;
        }
        this.importJson(jsonPath);
        File path;
        if (严格模式){
            path=new File(map.get("文件名称加密解密绝对地址"));
            if (!path.exists()){
                System.out.println("解密路径:"+path+"不存在,当前为严格模式无法解密");
                return;
            }
        }else {
            path=new File(jsonPath.getParent()+File.separator+map.get("文件名称加密解密相对地址"));
            if (!path.exists()){
                System.out.println("解密路径:"+path+"不存在,请确保json与要解密的文件夹在同一个父文件夹下");
                return;
            }
        }
        System.out.println("当前要解密的路径为:"+path);
        this.decrypt(path);

        File flag=new File(path.getPath()+File.separator+ 已加密标记_MD);
        flag.delete();
        /*输出错误日志*/
        System.out.println(erreo);
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
            map=JSON.parseObject(sb.toString(),new TypeReference<HashMap<String,String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 解密文件,文件夹名称
     *
     * @param path 路径
     */
    private void decrypt(File path) {
        // 获取该目录下所有的文件或者文件夹的File数组
        File[] fileArray = path.listFiles();
        if (fileArray == null) return;
        // 遍历该File数组，得到每一个File对象
        for (File file : fileArray) {
            //先将目录解密后递归
            if (file.isDirectory()) {
                decrypt(decryptRename(file));
            } else {
                decryptRename(file);
            }
        }
    }
    /**
     * 文件/文件夹重命名解密
     * @param file 路径
     */
    private File decryptRename(File file){
        String name=map.get(file.getName());
        /*没有对应名称时不改变名称*/
        if (name==null){
            if (!file.getName().equals(已加密标记_MD)){
                erreo.append("路径:").append(file).append("没有找到对应解密\n");
            }
            return file;
        }
        File rename=new File(file.getParent()+File.separator+name);
        if (!file.renameTo(rename)){
            System.out.println("更名失败!:"+file.getPath());
        }
        return rename;
    }

    /**导出JSON
     *
     */
    private void exportJson(File path){
        String param= JSON.toJSONString(map);
        /*输出文件记录到文件*/
        BufferedWriter bw;
        try {
            String name=path.getParent()+File.separator+path.getName()+"-解密数据-文件-"+fileNO+"文件夹-"+folderNO+".json";
            //打印到控制台,bat回调接收值传入另一个类
            System.out.println(name);
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), StandardCharsets.UTF_8));
            bw.write(param);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @return 获取加密文件名称
     */
    private String getTheNameOfTheEncryptedFile(){
        return String.format(文件加密名称 + "%06d.tib", fileNO++);
    }
    /**
     * @return 获取加密文件夹名称
     */
    private String getTheNameOfTheEncryptedFolder(){
        return String.format(文件夹加密名称 + "%06d", folderNO++);
    }
    /**
     * 加密文件,文件夹名称
     *
     * @param path 路径
     */
    private void recursion(File path) {
        // 获取该目录下所有的文件或者文件夹的File数组
        File[] fileArray = path.listFiles();
        if (fileArray == null) return;
        // 遍历该File数组，得到每一个File对象
        for (File file : fileArray) {
            //先将目录加密后递归
            if (file.isDirectory()) {
                recursion(encryptedRename(file));
            } else {
                encryptedRename(file);
            }
        }
    }
    /**
     * 文件/文件夹重命名加密
     * @param file 路径
     */
    private File encryptedRename(File file){
        String name;
        if (file.isDirectory()){
            name=getTheNameOfTheEncryptedFolder();
        }else {
            name=getTheNameOfTheEncryptedFile();
        }

        File rename=new File(file.getParent()+File.separator+name);
        map.put(name,file.getName());
        if (!file.renameTo(rename)){
            System.out.println("更名失败!:"+file.getPath());
            System.out.println("要更名为:"+rename.getName());
        }
        return rename;
    }
}
