import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFile {

    public static boolean httpDownload(String httpUrl, String saveFile) {
        // 1.下载网络文件
        int byteRead;
        URL url;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }

        try {
            //2.获取链接
            URLConnection conn = url.openConnection();
            //3.输入流
            InputStream inStream = conn.getInputStream();
            //3.写入文件
            FileOutputStream fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        httpDownload("https://r2---sn-oguelned.googlevideo.com/videoplayback?expire=1575536617&ei=iHPoXaXJJrugz7sP6dOTsA0&ip=124.121.185.30&id=o-ABflZ-H0CO2AgZN68Xhaw1A5Xx-mdixa1t51JfEuMo8r&itag=22&source=youtube&requiressl=yes&mime=video%2Fmp4&ratebypass=yes&dur=731.591&lmt=1554199868620914&fvip=2&fexp=23842630&c=WEB&txp=5535432&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cmime%2Cratebypass%2Cdur%2Clmt&sig=ALgxI2wwRAIgZC9upipu2ijFJ3dFZf4o7ekfYK2_P3leUhnkRYu4wAMCIAFKgcZVqswVBFe34rHayHbhByhXpPZsEiw0da51O4m0&cms_redirect=yes&mip=160.16.53.149&mm=31&mn=sn-oguelned&ms=au&mt=1575515558&mv=m&mvi=1&pl=17&lsparams=mip,mm,mn,ms,mv,mvi,pl&lsig=AHylml4wRQIgfhZOQUC4j5rVwlgneAXFuZ4o82LQhzbbFo2LhBDA6yYCIQDPeH5K-N5B2OUqwyIiCoxXu59pi7S3kweY1tomhPdYrQ==",
                "C:\\Users\\10007886\\Desktop\\file\\a.mp4");
    }
}


