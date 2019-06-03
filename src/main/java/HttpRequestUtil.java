
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

/**
 * Create by Gethin Wang on 2019/5/28
 */
public class HttpRequestUtil {

    private static  <T extends HttpRequest> T header(T request) {
        return (T) request.header("accept", "application/json").header("Content-Type", "application/json;charset=utf-8");
    }

    private static String buildUrl(String url, String[][] params){
        if(params!=null&&params.length!=0){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                stringBuilder.append("&").append(params[i][0]).append("=").append(params[i][1]);

            }
            url += "?" + stringBuilder.toString().substring(1);
        }
        return url;
    }

    /**
     * get请求
     * @param url 请求地址
     * @param params 请求参数
     * @return 返回信息，json格式
     * @throws UnirestException
     */
    public static String getRequest(String url, String[][] params) throws UnirestException {
        url = buildUrl(url,params);
        try {
            HttpRequest httpRequest = Unirest.get(url);
            String result = header(httpRequest).asString().getBody();
            System.out.println(result);
            return result;
        } catch (UnirestException ex) {
            throw ex;
        }
    }

    /**
     * post请求
     * @param url 请求地址
     * @param bodyContent 请求主体，json格式
     * @param params 请求参数
     * @return  返回信息，json格式
     * @throws UnirestException
     */
    public static String postRequest(String url,String bodyContent, String[][] params) throws UnirestException {
        url = buildUrl(url,params);
        try {
            HttpRequestWithBody httpRequest = Unirest.post(url);
            String result = header(httpRequest).body(bodyContent).asString().getBody();
            System.out.println(result);
            return result;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * put请求
     * @param url 请求地址
     * @param bodyContent 请求主体，json格式
     * @param params 请求参数
     * @return  返回信息，json格式
     * @throws UnirestException
     */
    public static String putRequest(String url,String bodyContent, String[][] params) throws UnirestException {
        url = buildUrl(url,params);
        try {
            HttpRequestWithBody httpRequest = Unirest.put(url);
            String result = header(httpRequest).body(bodyContent).asString().getBody();
            System.out.println(result);
            return result;
        } catch (UnirestException ex) {
            throw ex;
        }
    }

    /**
     * delete请求
     * @param url 请求地址
     * @param params 请求参数
     * @return  返回信息，json格式
     * @throws UnirestException
     */
    public static String deleteChannels(String url, String[][] params) throws UnirestException {
        url = buildUrl(url,params);
        try {
            HttpRequest httpRequest = Unirest.delete(url);
            String result = header(httpRequest).asString().getBody();
            System.out.println(result);
            return result;
        } catch (UnirestException e) {
            throw e;
        }
    }

    public static void main(String[] args) throws Exception{
        String[][] params = {{"page_number","1"},{"page_size","100"},{"sort_property","EPISODE"},{"sort_direction","ASC"},{"country_id","24"}};
        HttpRequestUtil.getRequest("http://localhost:port/vcms-service/v1/vup/programs/contents/300/subprograms/contents",params);
    }
}
