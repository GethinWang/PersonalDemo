import java.sql.*;

/**
 * Create by Gethin Wang on 2019/5/16
 * 使用MediumText储存到数据库，并且从数据库中获取文件
 */

public class MediumTextTest {
    public static void main(String[] args) {
        try {
            Connection connect = JDBCUtil.createConnection("jdbc:mysql://localhost:3306/blob?useSSL=false","root","123456");

            //插入数据库
            String insertSql = "insert into pic2(id , content) VALUES (?,?)";
            PreparedStatement insertPS = connect.prepareStatement(insertSql);
            insertPS.setInt(1,2);
            String picData = BaseUtil64.getImageStr("data\\test.jpg",null);
            insertPS.setString(2,picData);
            insertPS.execute();

            //读取数据库
            String querySql = "select * from pic2 where id = ?";
            PreparedStatement queryPS = connect.prepareStatement(querySql);
            queryPS.setInt(1, 2);
            ResultSet result = queryPS.executeQuery();
            String mediumText = null;
            //可能存在多个返回结果，此处只取最后一个
            while (result.next()) {
                mediumText = result.getString("content");
            }
            BaseUtil64.generateImageByString(mediumText,"data\\mediumText.jpg");

            JDBCUtil.closeConnection(connect);
        }catch (SQLException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}