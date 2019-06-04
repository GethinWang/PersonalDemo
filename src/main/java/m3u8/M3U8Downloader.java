package m3u8;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Create by Gethin Wang on 2019/6/2
 * 参考地址：https://github.com/yankj12/M3U8Downloader.git
 */
public class M3U8Downloader {

    /**
     * 下载m3u8文件到本地文件夹，并且将ts文件合并为mp4文件
     *
     * @param m3u8Url
     * @param workRootDirName
     * @param downloadThreadNum 下载线程数目
     * @throws Exception
     */
    public static void downloadM3U8(String m3u8Url, String workRootDirName, int downloadThreadNum) throws Exception {
        int index = m3u8Url.lastIndexOf("/");
        int index2 = m3u8Url.lastIndexOf(".m3u8");

        String urlPre = m3u8Url.substring(0, index+1);
        String fileName = m3u8Url.substring(index + 1, index2);

        initFolder(workRootDirName, fileName);

        System.out.println("开始下载m3u8文件");
        // 下载m3u8文件
        String result = HttpsUtils.get(m3u8Url, null);

        // 创建m3u8文件
        String m3u8FileName = workRootDirName + "\\" + fileName + "\\m3u8\\" + fileName + ".m3u8";
        File m3u8File = new File(m3u8FileName);
        if(!m3u8File.exists()) {
            m3u8File.createNewFile();
        }
        // 将m3u8文件的内容写入文件
        writeToFile(m3u8FileName, result, "UTF-8");
        System.out.println("下载m3u8文件结束");

        // 从m3u8文件内容中获取ts文件的文件名
        List<String> tsFileNames = new ArrayList();
        String[] lines = result.split("[\\r\\n]");
        for(String line: lines) {
            if(!line.startsWith("#")) {
                //System.out.println(line);
                tsFileNames.add(line.substring(line.lastIndexOf('/')+1));
            }
        }

        // 定义一个闭锁
        CountDownLatch countDownLatch = new CountDownLatch(tsFileNames.size());
        // 创建下载线程
        List<TsFileDownloadThread> tsFileDownloadThreads = new ArrayList();
        for(int i=0;i<downloadThreadNum;i++) {
            TsFileDownloadThread tsFileDownloadThread = new TsFileDownloadThread(i, workRootDirName, fileName, countDownLatch);
            tsFileDownloadThreads.add(tsFileDownloadThread);
        }


        // 下载ts文件
        // 下载好的ts文件的集合
        List<File> tsFiles = new ArrayList();

        for(int i=0;i<tsFileNames.size();i++) {
            String tsFileName = tsFileNames.get(i);

            String tsFileUrl = urlPre + tsFileName;
            String fsFileFullName = workRootDirName + "\\" + fileName + "\\ts\\" + tsFileName;
            File tsFile = new File(fsFileFullName);
            if(!tsFile.exists()) {
                tsFile.createNewFile();
            }

            //System.out.println(tsFileUrl);
            // 将ts文件的url分别添加到几个下载线程当中
            int serialNo = i%downloadThreadNum;
            TsFileDownloadThread tsFileDownloadThread = tsFileDownloadThreads.get(serialNo);
            tsFileDownloadThread.addTsFileUrl(tsFileUrl);

            // 按照顺序收集tsFile的名称，避免后续合并ts文件顺序错乱
            tsFiles.add(tsFile);
        }

        // 启动下载ts文件的线程
        for(TsFileDownloadThread tsFileDownloadThread:tsFileDownloadThreads) {
            tsFileDownloadThread.start();
        }

        // 等待下载线程下载结束，进行合并
        countDownLatch.await();

        System.out.println("开始合并ts文件为一个mp4文件");
        // 合并ts文件为一个mp4文件，需要注意ts文件的顺序
        mergeTsFile(workRootDirName, fileName, tsFiles);

        System.out.println("下载[" + fileName + "]结束");
    }

    private static void initFolder(String workRootDirName, String fileName) {
        File workRootDir = new File(workRootDirName);
        // 如果文件夹不存在创建文件夹
        if(!workRootDir.exists()) {
            workRootDir.mkdirs();
        }


        File fileDir = new File(fileName);
        if(!fileDir.exists()) {
            fileDir.mkdirs();
        }

        // 存放m3u8文件的地方
        String m3u8DirName = workRootDirName + "\\" + fileName + "\\m3u8";
        File m3u8Dir = new File(m3u8DirName);
        if(!m3u8Dir.exists()) {
            m3u8Dir.mkdirs();
        }

        // 存放ts临时文件的地方
        String tsDirName = workRootDirName + "\\" + fileName + "\\ts";
        File tsDir = new File(tsDirName);
        if(!tsDir.exists()) {
            tsDir.mkdirs();
        }

        // 存放mp4文件的地方
        String mp4DirName = workRootDirName + "\\" + fileName + "\\mp4";
        File mp4Dir = new File(mp4DirName);
        if(!mp4Dir.exists()) {
            mp4Dir.mkdirs();
        }
    }


    /**
     * 将多个ts文件合并为一个mp4文件
     *
     * @param workRootDir
     * @param fileName mp4文件的文件名（不带后缀名）
     * @param tsFiles 按顺序排列好的ts文件
     * @throws Exception
     */
    public static void mergeTsFile(String workRootDir, String fileName, List<File> tsFiles) throws Exception {

        File mp4File = new File(workRootDir + "\\" + fileName + "\\mp4\\" + fileName + ".mp4");

        if(!mp4File.exists()) {
            mp4File.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(mp4File);

        if(tsFiles != null) {
            for(File tmpFile : tsFiles) {
                System.out.println("正在合并:"+tmpFile.getName());
                FileInputStream fileInputStream = new FileInputStream(tmpFile);

                int len = 0;
                int size = 1024 * 1024;
                byte[] bytes = new byte[size];
                while((len = fileInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }

                fileInputStream.close();
            }
        }
        fileOutputStream.close();
    }

    /**
     * 将内容写入到一个文件
     * @param filePath 文件的全路径
     * @param content 文件内容
     * @param chartset 字符集
     * @throws Exception
     */
    public static void writeToFile(String filePath, String content, String chartset) throws Exception {
        if(chartset == null || "".equals(chartset.trim())) {
            chartset = "UTF-8";
        }
        File file = new File(filePath);
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), chartset));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) throws Exception {

        // m3u8文件的url
        String m3u8Url = "http://10.0.224.243/vod/vod_2019_05_20_2_1000_240x320_535/vod.m3u8";

        // 输出目录
        String workRootDirName = "C:\\Users\\10007886\\Desktop\\data";

        // 下载线程数目
        int downloadThreadNum = 5;

        downloadM3U8(m3u8Url, workRootDirName, downloadThreadNum);
    }
}

