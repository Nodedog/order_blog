package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 主要实现三个功能
// 1. 插入用户 - 注册的时候使用
// 2. 按名字查找用户 - 登陆时使用
// 3. 按照用户 id 查找 - 展示信息时使用
public class UserDao {
    // 1. 插入用户 - 注册的时候使用
    public void add(User user) {
        // JDBC 编程的基本流程
        // 1. 先获取和数据库的连接(DataSource)
        Connection connection = DBUtil.getconnection();
        // 2. 拼装 SQL 语句(PrepareStatement)
        String sql = "insert into user value(null,?,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,user.getName());
            statement.setString(2,user.getPassword());
            statement.setInt(3,user.getIsAdmin());
            // 3. 执行 SQL 语句(executeQuery, executeUpdate)
            int ret = statement.executeUpdate();
            if (ret != 1){
                System.out.println("插入失败");
                return;
            }
            System.out.println("插入成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            // 4. 关闭连接(close) (如果是查询语句, 还需要遍历结果集合)
            DBUtil.close(connection,statement,null);
        }
    }


    // 2. 按名字查找用户 - 登陆时使用
    public User selectByName(String name){
        // 1. 先获取和数据库的连接(DataSource)
        Connection connection = DBUtil.getconnection();
        // 2. 拼装 SQL 语句(PrepareStatement)
        String sql = "select * from user where name = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            //查询就用executeQuery（）返回的是一个resultset类
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }


    // 3. 按照用户 id 查找 - 展示信息时使用
    public User selectByUserId(int userId){
        //1.与数据库建立链接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql语句
        String sql = "select * from user where userId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            //查询就用executeQuery（）返回的是一个resultset类
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setPassword(resultSet.getString("password"));
                user.setIsAdmin(resultSet.getInt("isAdmin"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDao();

        /*验证插入数据
        User user = new User();
        user.setName("xxn");
        user.setPassword("123");
        user.setIsAdmin(0);
        userDao.add(user);*/

        //验证通过名字查找数据
        User user = userDao.selectByName("xxn");
        System.out.println(user);

        user = userDao.selectByUserId(1);
        System.out.println(user);
    }

}
