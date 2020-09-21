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
        String query = "INSERT INTO products (productName, price) VALUES (?, ?)";
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
            throw new DataProcessException("Can't create product!", e);
        }
    }

    @Override
    public Optional<Product> get(Long productId) {
        String query = "SELECT * FROM products WHERE product_id = ? AND deleted != 1";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Product product = productFromSet(resultSet);
                product.setId(productId);
                return Optional.of(product);
            }
        } catch (SQLException e) {
            throw new DataProcessException("Can't find product with id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products WHERE deleted != 1";
        List<Product> allProducts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Product product = productFromSet(resultSet);
                product.setId(resultSet.getLong("product_id"));
                allProducts.add(product);
            }
        } catch (SQLException e) {
            throw new DataProcessException("Can't execute query!", e);
        }
        return allProducts;
    }

    @Override
    public Product update(Product product) {
        String query = "UPDATE products SET productName = ?, price = ? WHERE product_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setLong(3, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessException("Can't update product", e);
        }
        return product;
    }

    @Override
    public boolean delete(Long productId) {
        String query = "UPDATE products SET deleted = true WHERE product_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataProcessException("Can't execute update query!", e);
        }
    }

    private Product productFromSet(ResultSet resultSet) {
        try {
            String productName = resultSet.getString("productName");
            double price = resultSet.getDouble("price");
            return new Product(productName, price);
        } catch (SQLException e) {
            throw new DataProcessException("Can't generate product...", e);
        }
    }
}
