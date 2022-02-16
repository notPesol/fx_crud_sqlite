package com.example.productui.db;


import com.example.productui.model.Product;
import com.example.productui.model.ProductView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    private static final Datasource instance = new Datasource();

    private static final String DB_NAME = "store.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:";


    private static final String PRODUCT_TABLE_NAME = "product";
    private static final String BRAND_TABLE_NAME = "brand";
    private static final String CATEGORY_TABLE_NAME = "category";

    // ใช้ร่วมกันทุก TABLE
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";

    private static final String PRODUCT_COLUMN_STOCK = "stock";
    private static final String PRODUCT_COLUMN_BRAND = "brand";
    private static final String PRODUCT_COLUMN_CATEGORY = "category";


    private static final String PRODUCT_VIEW = "product_view";
    private static final String QUERY_PRODUCT_VIEW = "SELECT * FROM " + PRODUCT_VIEW;
    private static final String UPDATE_PRODUCT = "UPDATE " + PRODUCT_TABLE_NAME +
            " SET " + COLUMN_NAME + " = ?, " + PRODUCT_COLUMN_BRAND + " = ?, " + PRODUCT_COLUMN_CATEGORY + " = ?," +
            PRODUCT_COLUMN_STOCK + " = ?" +
            " WHERE " + COLUMN_NAME + " = ?";

    public enum ORDER_BY {
        NONE,
        ASC,
        DESC
    }

    private static final String QUERY_ALL_PRODUCTS = "SELECT " + COLUMN_NAME + ", " + PRODUCT_COLUMN_STOCK +
            " FROM " + PRODUCT_TABLE_NAME;
    private static final String QUERY_PRODUCT_BY_NAME = "SELECT " + COLUMN_NAME + ", " + PRODUCT_COLUMN_STOCK +
            " FROM " + PRODUCT_TABLE_NAME +
            " WHERE " + COLUMN_NAME + " = ? ORDER BY " + COLUMN_NAME + " COLLATE NOCASE ASC";

    private static final String QUERY_BRAND_ID_BY_NAME = "SELECT " + COLUMN_ID + " FROM " + BRAND_TABLE_NAME +
            " WHERE " + COLUMN_NAME + " = ?";
    private static final String QUERY_CATEGORY_ID_BY_NAME = "SELECT " + COLUMN_ID + " FROM " + CATEGORY_TABLE_NAME +
            " WHERE " + COLUMN_NAME + " = ?";

    private static final String INSERT_PRODUCT = "INSERT INTO " + PRODUCT_TABLE_NAME +
            "(" + COLUMN_NAME + ", " + PRODUCT_COLUMN_STOCK + ", " + PRODUCT_COLUMN_BRAND +
            ", " + PRODUCT_COLUMN_CATEGORY + ") VALUES(?, ?, ?, ?)";

    private static final String INSERT_BRAND = "INSERT INTO " + BRAND_TABLE_NAME +
            "(" + COLUMN_NAME + ") VALUES(?)";
    private static final String INSERT_CATEGORY = "INSERT INTO " + CATEGORY_TABLE_NAME +
            "(" + COLUMN_NAME + ") VALUES(?)";

    private static final String QUERY_PRODUCT_BY_NAME_AND_BRAND_AND_CATEGORY = "SELECT " + COLUMN_ID +
            " FROM " + PRODUCT_TABLE_NAME +
            " WHERE " + COLUMN_NAME + " = ? AND " + PRODUCT_COLUMN_BRAND + " = ? AND " +
            PRODUCT_COLUMN_CATEGORY + " = ?";

    private static final String DELETE_PRODUCT_BY_NAME = "DELETE FROM " + PRODUCT_TABLE_NAME +
            " WHERE " + COLUMN_NAME + " = ?";


    private static Connection con;
    private static PreparedStatement queryProductByName;
    private static PreparedStatement queryBrandIdByName;
    private static PreparedStatement queryCategoryIdByName;

    private static PreparedStatement insertProduct;
    private static PreparedStatement insertBrand;
    private static PreparedStatement insertCategory;

    private static PreparedStatement queryProductByNameAndBrandAndCategory;
    private static PreparedStatement deleteProductByName;

    private static PreparedStatement updateProduct;

    private Datasource() {
    }

    public static Datasource getInstance() {
        return instance;
    }

    public boolean deleteProductByName(String productName) {
        try {
            deleteProductByName.setString(1, productName);
            if (deleteProductByName.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void insertProduct(String productName, int stock, String brandName, String categoryName) {
        try {
            con.setAutoCommit(false);

            int brandId = queryBandIdByName(brandName);
            int categoryId = queryCategoryIdByName(categoryName);

            queryProductByNameAndBrandAndCategory.setString(1, productName);
            queryProductByNameAndBrandAndCategory.setInt(2, brandId);
            queryProductByNameAndBrandAndCategory.setInt(3, categoryId);
            ResultSet resultSet = queryProductByNameAndBrandAndCategory.executeQuery();
            if (resultSet.next()) {
                throw new SQLException("มีสินค้านี้อยู่แล้ว!");
            }

            insertProduct.setString(1, productName);
            insertProduct.setInt(2, stock);
            insertProduct.setInt(3, brandId);
            insertProduct.setInt(4, categoryId);

            int affectedRow = insertProduct.executeUpdate();
            if (affectedRow != 1) {
                throw new SQLException("ไม่สามารถ เพิ่ม product ได้!");
            }

            con.commit();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println("โอ้ ยำแล้ว ไม่สามารถ rollback ได้!");
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("โอ้ ยำแล้ว ไม่สามารถเปิด auto-commit กลับได้!");
            }
        }
    }

    public boolean updateProduct(String oldName, String newName, int stock, String brandName, String categoryName) {
        try {
            con.setAutoCommit(false);

            int brandId = queryBandIdByName(brandName);
            int categoryId = queryCategoryIdByName(categoryName);

            updateProduct.setString(1, newName);
            updateProduct.setInt(2, brandId);
            updateProduct.setInt(3, categoryId);
            updateProduct.setInt(4, stock);
            updateProduct.setString(5, oldName);

            System.out.println(UPDATE_PRODUCT);

            int affectedRow = updateProduct.executeUpdate();
            if (affectedRow != 1) {
                throw new SQLException("ไม่สามารถ อัพเดท product ได้!");
            }

            con.commit();
            return true;


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println("โอ้ ยำแล้ว ไม่สามารถ rollback ได้!");
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("โอ้ ยำแล้ว ไม่สามารถเปิด auto-commit กลับได้!");
            }
        }

        return false;
    }

    private int insertBrand(String brandName) throws SQLException {
        insertBrand.setString(1, brandName);
        insertBrand.executeUpdate();
        ResultSet resultSet = insertBrand.getGeneratedKeys();
        if (resultSet.next()) {
            return resultSet.getInt(1); // return _id
        }

        throw new SQLException("ไม่สามารถ เพิ่ม brand ได้!");

    }

    private int queryBandIdByName(String brandName) throws SQLException {
        queryBrandIdByName.setString(1, brandName);
        ResultSet resultSet = queryBrandIdByName.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(COLUMN_ID);
        } else {
            return insertBrand(brandName);
        }
    }

    private int queryCategoryIdByName(String categoryName) throws SQLException {
        queryCategoryIdByName.setString(1, categoryName);
        ResultSet resultSet = queryCategoryIdByName.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(COLUMN_ID);
        } else {
            return insertCategory(categoryName);
        }
    }

    private int insertCategory(String categoryName) throws SQLException {
        insertCategory.setString(1, categoryName);
        insertCategory.executeUpdate();
        ResultSet resultSet = insertCategory.getGeneratedKeys();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }

        throw new SQLException("ไม่สามารถ เพิ่ม category ได้!");
    }

    public List<ProductView> queryAllProductView(ORDER_BY orderBy) {
        StringBuilder sb = new StringBuilder(QUERY_PRODUCT_VIEW);
        if (orderBy != ORDER_BY.NONE) {
            sb.append(" ORDER BY ").append(COLUMN_NAME).append(" COLLATE NOCASE ");
            if (orderBy == ORDER_BY.DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            List<ProductView> productViews = new ArrayList<>();
            while (resultSet.next()) {
                ProductView productView = new ProductView(
                        resultSet.getString("name"),
                        resultSet.getInt("stock"),
                        resultSet.getString("brand"),
                        resultSet.getString("category")
                );
                productViews.add(productView);
            }
            return productViews;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> queryAllProducts(ORDER_BY orderBy) {
        StringBuilder sb = new StringBuilder(QUERY_ALL_PRODUCTS);
        if (orderBy != ORDER_BY.NONE) {
            sb.append(" ORDER BY ").append(COLUMN_NAME).append(" COLLATE NOCASE ");
            if (orderBy == ORDER_BY.DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sb.toString())) {

            return returnProducts(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> queryProductByName(String productName) {
        try {
            queryProductByName.setString(1, productName);
            ResultSet resultSet = queryProductByName.executeQuery();
            return returnProducts(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Product> returnProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();

        while (resultSet.next()) {
            Product product = new Product();
            product.setName(resultSet.getString(1));
            product.setStock(resultSet.getInt(2));

            products.add(product);
        }
        return products;
    }

    public boolean open() {
        try {
            con = DriverManager.getConnection(CONNECTION_STRING + DB_NAME);
            queryProductByName = con.prepareStatement(QUERY_PRODUCT_BY_NAME);
            queryBrandIdByName = con.prepareStatement(QUERY_BRAND_ID_BY_NAME);
            queryCategoryIdByName = con.prepareStatement(QUERY_CATEGORY_ID_BY_NAME);
            insertProduct = con.prepareStatement(INSERT_PRODUCT);
            insertBrand = con.prepareStatement(INSERT_BRAND, Statement.RETURN_GENERATED_KEYS);
            insertCategory = con.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS);
            queryProductByNameAndBrandAndCategory = con.prepareStatement(QUERY_PRODUCT_BY_NAME_AND_BRAND_AND_CATEGORY);
            deleteProductByName = con.prepareStatement(DELETE_PRODUCT_BY_NAME);
            updateProduct = con.prepareStatement(UPDATE_PRODUCT);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (queryProductByName != null) {
                queryProductByName.close();
            }
            if (queryBrandIdByName != null) {
                queryBrandIdByName.close();
            }
            if (queryCategoryIdByName != null) {
                queryCategoryIdByName.close();
            }
            if (insertProduct != null) {
                insertProduct.close();
            }
            if (insertBrand != null) {
                insertBrand.close();
            }
            if (insertCategory != null) {
                insertCategory.close();
            }
            if (queryProductByNameAndBrandAndCategory != null) {
                queryProductByNameAndBrandAndCategory.close();
            }
            if (deleteProductByName != null) {
                deleteProductByName.close();
            }
            if (updateProduct != null) {
                updateProduct.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
