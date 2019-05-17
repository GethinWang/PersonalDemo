import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Create by Gethin Wang on 2019/5/16
 * 使用JDBC创建Mysql连接
 */

public class JDBCUtil {

    public static Connection createConnection(String url,String name,String psw) throws SQLException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("找不到驱动程序类，加载驱动失败");
            e.printStackTrace();
        }
        Connection connect = DriverManager.getConnection(url, name, psw);
        return connect;
    }

    public static void closeConnection(Connection connect) throws SQLException{
        connect.close();
    }

    public static void main(String[] args) {
        try {
            Connection connect = createConnection("jdbc:mysql://localhost:3306/test?useSSL=false","root","123456");

            //具体sql逻辑
            String sql = "select * from content where id = ?";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, 1);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                System.out.println(result.getInt("id"));
                System.out.println(result.getString("name"));
            }
            result.close();
            ps.close();

            closeConnection(connect);
        }catch (SQLException e){
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }
    }
}