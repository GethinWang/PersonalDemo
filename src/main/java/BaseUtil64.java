import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Create by Gethin Wang on 2019/5/16
 * 用于将图片转换为Base64编码，或者将Base64编码解密为图片，可用于持久化图片，需要去除开头data:image/png;base64,
 */

public class BaseUtil64 {

    /**
     * @param path   图片地址
     * @param tmpPath   Base64临时存放目录
     * @Description: base64->图片
     */
    public static boolean generateImageByFilePath(String path, String tmpPath) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            // 解密
            fileReader = new FileReader(tmpPath);
            bufferedReader = new BufferedReader(fileReader);
            String str;
            StringBuilder stringBuilder = new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                if (null != str || !str.trim().equals("")) {
                    stringBuilder.append(str);
                }
            }
            byte[] b = decoder.decodeBuffer(stringBuilder.toString());
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * @param imgFile   图片地址
     * @param tmpPath   临时文件目录，为null表示不保存
     * @Description: 图片->base64
     */
    public static String getImageStr(String imgFile,String tmpPath) {
        InputStream inputStream = null;
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            if(tmpPath!=null&&tmpPath.length()!=0){
                File tmp = new File(tmpPath);
                FileWriter fileWriter = new FileWriter(tmp);
                fileWriter.write(encoder.encode(data));
                fileWriter.flush();
                fileWriter.close();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        return encoder.encode(data);
    }

    /**
     * @param imgFile   图片地址
     * @param msg   Base64码
     * @Description: base64->图片
     */
    public static boolean generateImageByString(String msg, String imgFile) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(msg.toString());
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFile);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 示例
     */
    public static void main(String[] args) throws Exception {
        String strImg = getImageStr("data\\test.jpg","data\\tmp.txt");
        System.out.println(strImg);
        generateImageByFilePath("data\\test2.jpg","data\\tmp.txt");
        generateImageByString(strImg,"data\\test3.jpg");
    }
}

