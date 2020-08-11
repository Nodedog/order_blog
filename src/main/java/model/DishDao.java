package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 操作菜品表.
// 1. 新增菜品
// 2. 删除菜品
// 3. 查询所有菜品
// 4. 查询指定菜品
public class DishDao {

    //1.添加菜品
    public void add(Dish dish){
        //1.获取数据连接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql
        String sql = "insert into dishes values(null,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,dish.getName());
            statement.setInt(2,dish.getPrice());
            //3.执行sql
            int ret = statement.executeUpdate();
            if (ret != 1){
                System.out.println("添加菜品失败");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //4.关闭连接
            DBUtil.close(connection,statement,null);
        }
    }


    // 2. 删除菜品
    public void delete(int dishId){
        //1.获取连接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql
        String sql = "delete from dishes where dishId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,dishId);
            int ret = statement.executeUpdate();
            if (ret != 1){
                System.out.println("删除菜品失败");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }



    // 3. 查询所有菜品
    public List<Dish> selectAll(){
        List<Dish> dishes = new ArrayList<>();
        //1.建立链接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql
        String sql = "select * from dishes";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            //执行sql
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                Dish dish = new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                dishes.add(dish);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }finally {
            //4.关闭连接
            DBUtil.close(connection,statement,resultSet);
        }
        return dishes;
    }


    // 4. 查询指定菜品
    public Dish selectByDishId(int dishId){
        //1.建立链接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql
        String sql = "select * from dishes where dishId = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,dishId);
            //3.执行sql
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                Dish dish = new Dish();
                dish.setDishId(resultSet.getInt("dishId"));
                dish.setName(resultSet.getString("name"));
                dish.setPrice(resultSet.getInt("price"));
                return dish;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }


    public static void main(String[] args) {
        DishDao dishDao = new DishDao();
          /*  // 1. 测试新增
            Dish dish = new Dish();
            dish.setName("红烧肉");
            dish.setPrice(3000); // 单位是 分
            dishDao.add(dish);*/
        /*// 2. 测试查找
        List<Dish> dishes = dishDao.selectAll();
        System.out.println("查看所有");
        System.out.println(dishes);

        Dish dish = dishDao.selectByDishId(2);
        System.out.println("查看单个");
        System.out.println(dish);*/

        // 3. 测试删除
        dishDao.delete(1);
    }

}
