package m3u8;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create by Gethin Wang on 2019/6/2
 * 下载ts文件的线程,参考地址：https://github.com/yankj12/M3U8Downloader.git
 */
class TsFileDownloadThread extends Thread{

    private int serialNo;

    private CountDownLatch countDownLatch;

    private String workRootDirName;

    /**
     * 是要通过m3u8下载的文件的名称（不带后缀名的名称），不是ts文件的名称
     */
    private String fileName;

     /**
      * 待下载的ts文件url
      */
    private Queue<String> tsFileUrls;

    public TsFileDownloadThread(int serialNo, String workRootDirName, String fileName, CountDownLatch countDownLatch) {
        this.serialNo = serialNo;
        this.workRootDirName = workRootDirName;
        this.fileName = fileName;
        this.countDownLatch = countDownLatch;
        this.tsFileUrls = new LinkedBlockingQueue<String>();
    }

    public void addTsFileUrl(String tsFileUrl) {
        this.tsFileUrls.add(tsFileUrl);
    }

    @Override
    public void run() {
        while (tsFileUrls != null&&!tsFileUrls.isEmpty()) {
            String tsFileUrl = tsFileUrls.poll();
            // 从url中截取ts文件的名称
            int index = tsFileUrl.lastIndexOf("/");
            String tsFileName = tsFileUrl.substring(index + 1);

            // ts文件在本地的全路径名
            String fsFileFullName = workRootDirName + "\\" + fileName + "\\ts\\" + tsFileName;
            File tsFile = new File(fsFileFullName);
            // 如果ts文件在本地不存在，则创建
            if(!tsFile.exists()) {
                try {
                    tsFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 下载ts文件
            int res = 0;
            try {
                res = downloadTsFile(tsFileUrl, null, tsFile);
                System.out.println("TsFileDownloadThread-" + this.serialNo + ",下载ts文件结束:" + tsFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(res == HttpStatus.SC_OK){
                // 下载完ts文件要将计数器减一
                countDownLatch.countDown();
            }else {
                tsFileUrls.add(tsFileUrl);
            }
        }
    }

    /**
     * 根据ts文件的url下载ts文件
     * @param url
     * @param header
     * @param outFile
     * @return
     * @throws Exception
     */
    public static int downloadTsFile(String  url, Map<String, String> header, File outFile) throws Exception {
        int statusCode;
        CloseableHttpClient httpClient = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            httpClient = HttpsUtils.getHttpClient();
            HttpGet httpGet = new HttpGet(url);
            // 设置头信息
            if (header != null && header.size() > 0) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            HttpResponse httpResponse = httpClient.execute(httpGet);
            statusCode = httpResponse.getStatusLine().getStatusCode();

            HttpEntity entity = httpResponse.getEntity();
            if (statusCode == HttpStatus.SC_OK) {
                inputStream = entity.getContent();
                fileOutputStream = new FileOutputStream(outFile);

                int len = 0;
                int size = 1024;
                byte[] bytes = new byte[size];
                while((len = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                // 将文件输出到本地
                fileOutputStream.flush();

            } else {
                System.out.println(url + ":网络请求状态码不是200");
            }
            EntityUtils.consume(entity);

        } catch (Exception e) {
            throw e;
        } finally {
            if(fileOutputStream != null) {
                fileOutputStream.close();
            }

            if(inputStream != null) {
                inputStream.close();
            }

            if (httpClient != null) {
                httpClient.close();
            }
        }
        return statusCode;
    }


}

