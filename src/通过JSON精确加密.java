package 工具.文件名称加密;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aotmd
 * @version 1.0
 * @date 2021/4/6 18:43
 */
/*
javac -cp .;fastjson-1.2.75.jar -encoding UTF-8 -d . 通过JSON精确加密.java
java -cp .;fastjson-1.2.75.jar 工具.文件名称加密.通过JSON精确加密
*/
public class 通过JSON精确加密 {
    public static final String 已加密标记_MD = "已加密标记.md";
    /**文件更名记录*/
    private Map<String,String> map = new HashMap<>();
    private StringBuffer erreo=new StringBuffer();

    public 通过JSON精确加密() {}

    public static void main(String[] args) {
        if (args.length!=1){
            System.out.println("参数不正确");
            return;
        }
        通过JSON精确加密 a=new 通过JSON精确加密();
        a.startEncryption(new File(args[0]));
    }



    /**
     * 开始加密
     * @param jsonPath json路径
     */
    public void startEncryption(File jsonPath){
        if (jsonPath==null||jsonPath.isDirectory()){
            System.out.println("json文件不存在!");
            return;
        }
        this.importJson(jsonPath);
        File path = new File(jsonPath.getParent() + File.separator + map.get("文件名称加密解密相对地址"));
        if (!path.exists()) {
            System.out.println("加密路径:" + path + "不存在,请确保json与要精确加密的文件夹在同一个父文件夹下");
            return;
        }
        File flag=new File(path+File.separator+已加密标记_MD);
        if (flag.exists()){
            System.out.println("该目录已加密!不需要再加密!");
            return;
        }

        System.out.println("当前要加密的路径为:"+path);
        this.encryption(path);

        /*创建已加密标记*/
        try {
            flag.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*输出错误日志*/
        System.out.println(erreo);
        //打印加密路径到控制台,bat回调接收值传入另一个类
        System.out.println(path);
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
     * 精确加密文件,文件夹名称
     *
     * @param path 路径
     */
    private void encryption(File path) {
        // 获取该目录下所有的文件或者文件夹的File数组
        File[] fileArray = path.listFiles();
        if (fileArray == null) return;
        // 遍历该File数组，得到每一个File对象
        for (File file : fileArray) {
            //先将目录加密后递归
            if (file.isDirectory()) {
                encryption(encryptedRename(file));
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
        String name="";
        for(String key:map.keySet()){
            if (map.get(key).equals(file.getName())){
                name=key;
            }
        }
        // 如果map里没有对应的名字则放弃加密
        if (Objects.equals(name, "")){
            return file;
        }
        File rename=new File(file.getParent()+File.separator+name);
        if (!file.renameTo(rename)){
            System.out.println("更名失败!:"+file.getPath());
            System.out.println("要更名为:"+rename.getName());
        }
        return rename;
    }
}
