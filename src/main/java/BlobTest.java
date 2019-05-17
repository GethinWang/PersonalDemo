import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;

/**
 * Create by Gethin Wang on 2019/5/16
 * 使用blob储存到数据库，并且从数据库中获取文件
 */

public class BlobTest {
    public static void main(String[] args) {
        try {
            Connection connect = JDBCUtil.createConnection("jdbc:mysql://localhost:3306/blob?useSSL=false","root","123456");

            //插入数据库
            String insertSql = "insert into pic(id , content) VALUES (?,?)";
            PreparedStatement insertPS = connect.prepareStatement(insertSql);
            insertPS.setInt(1,2);
            String picData = BaseUtil64.getImageStr("data\\test.jpg",null);
            Blob picBlob = new SerialBlob(picData.getBytes("GBK"));
            insertPS.setBlob(2,picBlob);
            insertPS.execute();

            //读取数据库
            String querySql = "select * from pic where id = ?";
            PreparedStatement queryPS = connect.prepareStatement(querySql);
            queryPS.setInt(1, 2);
            ResultSet result = queryPS.executeQuery();
            Blob blob = null;
            while (result.next()) {
                blob = result.getBlob("content");
            }
            BaseUtil64.generateImageByString(new String(blob.getBytes(1, (int) blob.length()),"GBK"),"data\\blob2.jpg");

            JDBCUtil.closeConnection(connect);
        }catch (SQLException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}