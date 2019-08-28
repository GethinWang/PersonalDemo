import java.io.*;

/**
 * Create by Gethin Wang on 2019/5/16
 * 读取，写入TXT文件
 */

public class ReadTxt {

    public static void main(String args[]) {
        String content = readFile("C:\\Users\\10007886\\Desktop\\videoresourcePlayUrlAndSizeMapping.txt");
        writeFile("C:\\Users\\10007886\\Desktop\\tmp.txt",content);
    }

    /**
     * 读入TXT文件
     */
    public static String readFile(String pathname) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader);

            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\r\n");
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return stringBuffer.toString();
        }
    }

    /**
     * 写入TXT文件
     */
    public static void writeFile(String pathname,String content) {
        try {
            File writeName = new File(pathname);
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(content);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


