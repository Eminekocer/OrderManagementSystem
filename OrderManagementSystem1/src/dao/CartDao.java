package dao;

import bussiness.ProductController;
import core.Database;
import entity.Cart;
import entity.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CartDao {
    private Connection connection;
    private ProductDao productDao;
    private CustomerDao customerDao;


    public CartDao() {
        this.connection = Database.getInstance();
        this.productDao = new ProductDao();
        this.customerDao= new CustomerDao();

    }
    public boolean save(Cart  cart){
        String query = "INSERT INTO cart (customer_id,product_id,price,date,note)" +
                " VALUES (?,?,?,?,?)";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,cart.getCustomerId());
            pr.setInt(2,cart.getProuctId());
            pr.setInt(3,cart.getPrice());
            pr.setDate(4,Date.valueOf( cart.getDate()));
            pr.setString(5,cart.getNote());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;}
    public ArrayList<Cart> findAll(){
        ArrayList<Cart> carts = new ArrayList<>();
        try {
            ResultSet rs= this.connection.createStatement().executeQuery("SELECT *FROM cart");
            while (rs.next()){
                carts.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carts;}

    public Cart match(ResultSet rs ) throws SQLException {
        Cart carts = new Cart();
        carts.setId(rs.getInt("Id"));
        carts.setCustomerId(rs.getInt("customer_id"));
        carts.setProuctId(rs.getInt("product_id"));
        carts.setPrice(rs.getInt("price"));
        carts.setDate(LocalDate.parse(rs.getString("date")));
        carts.setNote(rs.getString("note"));
        carts.setCustomer(this.customerDao.getById(carts.getCustomerId()));
        carts.setProduct(this.productDao.getById(carts.getProuctId()));

        return carts;
    }

}