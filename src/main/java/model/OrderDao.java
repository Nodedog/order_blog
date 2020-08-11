package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// 操作订单
// 1. 新增订单
// 2. 查看所有订单(管理员, 商家)
// 3. 查看指定用户的订单(普通用户, 顾客)
// 4. 查看指定订单的详细信息
// 5. 修改订单状态(订单是否已经完成)
public class OrderDao {
    // 新增订单
    // 订单是和两个表关联的.
    // 第一个表 order_user
    // 第二个表 order_dish, 一个订单中可能会涉及点多个菜, 就需要给这个表一次性插入多个记录.
    public void add(Order order){
        // 1. 先操作 order_user 表
        addOrderUser(order);
        // 2. 再操作 order_dish 表
        //    执行 add 方法的时候, order 对象中的 orderId 字段还是空着的
        //    这个字段要交给数据库, 由自增主键来决定.
        //虽然执行addOrderUser方法之后数据库已经生成了orderId，但是此时代码中的order不具有orderId这个属性
        //然后就在addOrderUser的statement中返回PreparedStatement.RETURN_GENERATED_KEYS 此时就能获取到
        //数据库自增主键生成的orderId了
        addOrderDish(order);
    }

    private void addOrderUser(Order order) {
        Connection connection = DBUtil.getconnection();
        String sql = "insert into order_user values(null,?,now(),0)";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1,order.getUserId());
            int ret = statement.executeUpdate();
            if (ret != 1){
                System.out.println("添加订单失败");
            }

            //把自增主键读取出来
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()){
                // 参数 1 读取 resultSet 的结果时, 可以使用列名, 也可以使用下标.
                // 由于一个表中的自增列可以有多个. 返回的时候都返回回来了. 下标填成 1
                // 就表示想获取到第一个自增列生成的值.
                order.setOrderId(resultSet.getInt(1));
            }

            System.out.println("插入订单第一步成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
    }

    private void addOrderDish(Order order) {
        //1.建立数据连接
        Connection connection = DBUtil.getconnection();
        //2.拼装sql
        String sql = "insert into order_dish values(?,?)";
        PreparedStatement statement = null;
        try {
            //3.关闭自动提交
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
            //4.遍历dishes 给sql添加多个values的值
            // 由于一个订单对应到多个菜品, 就需要遍历 Order 中包含的菜品数组, 把每个记录都取出来
            List<Dish> dishes = order.getDishes();
            for (Dish dish : dishes){
                statement.setInt(1,order.getOrderId());
                statement.setInt(2,dish.getDishId());
                statement.addBatch();// 给 sql 新增一组values，可以吧多组数据合并成一个sql语句.
            }
            //5.执行sql（不是真的执行）
            statement.executeBatch(); // 把刚才的 sql 进行执行.
            //6.发送给服务器（真的执行）commit 可以去执行多个 SQL, 一次调用 commit 统一发给服务器.
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // 如果上面的操作出现异常, 就认为整体的新增订单操作失败, 回滚之前的插入 order_user 表的内容
            deleteOrderUser(order.getOrderId());
        }finally {
            //7.关闭连接
            DBUtil.close(connection,statement,null);
        }
    }

    private void deleteOrderUser(int orderId) {
        Connection connection = DBUtil.getconnection();
        String sql = "delete form order_user where orderId = ?";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            int ret = statement.executeUpdate();
            if (ret != 1){
                System.out.println("回滚失败");
            }
            System.out.println("回滚成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,null);
        }
    }




    // 2. 查看所有订单(管理员, 商家)

}
