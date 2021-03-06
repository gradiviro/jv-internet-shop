package com.internet.shop.dao.jdbc;

import com.internet.shop.dao.ProductDao;
import com.internet.shop.exceptions.DataProcessException;
import com.internet.shop.lib.Dao;
import com.internet.shop.model.Product;
import com.internet.shop.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ProductDaoJdbcImpl implements ProductDao {
    @Override
    public Product create(Product product) {
        String query = "INSERT INTO products (product_name, product_price) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                product.setId(resultSet.getLong(1));
            }
            return product;
        } catch (SQLException e) {
            throw new DataProcessException("Can't create product " + product.getName(), e);
        }
    }

    @Override
    public Optional<Product> get(Long productId) {
        String query = "SELECT * FROM products WHERE product_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Product product = getProductFromSet(resultSet);
                return Optional.of(product);
            }
        } catch (SQLException e) {
            throw new DataProcessException("Can't find product with id " + productId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products WHERE is_deleted = FALSE";
        List<Product> allProducts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Product product = getProductFromSet(resultSet);
                allProducts.add(product);
            }
        } catch (SQLException e) {
            throw new DataProcessException("Can't get all products!", e);
        }
        return allProducts;
    }

    @Override
    public Product update(Product product) {
        String query = "UPDATE products SET product_name = ?, product_price = ? "
                + "WHERE product_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setLong(3, product.getId());
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new DataProcessException("Can't update product " + product, e);
        }
    }

    @Override
    public boolean delete(Long productId) {
        String query = "UPDATE products "
                + "SET is_deleted = TRUE WHERE product_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataProcessException("Can't execute update product " + productId, e);
        }
    }

    private Product getProductFromSet(ResultSet resultSet) {
        try {
            Long productId = resultSet.getLong("product_id");
            String productName = resultSet.getString("product_name");
            double price = resultSet.getDouble("product_price");
            return new Product(productId, productName, price);
        } catch (SQLException e) {
            throw new DataProcessException("Can't retrieve product from ResultSet", e);
        }
    }
}
