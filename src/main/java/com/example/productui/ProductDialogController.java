package com.example.productui;

import com.example.productui.db.Datasource;
import com.example.productui.model.ProductView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductDialogController {

    public TextField nameField;
    public TextField brandField;
    public TextField categoryField;
    public TextField stockField;

    public ProductView getNewProduct() {
        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();
        String category = categoryField.getText().trim();
        String stockText = stockField.getText().trim();

        if (!stockText.matches("^\\d{1,3}$")) {
            stockText = "0";
        }
        int stock = Integer.parseInt(stockText);

        return new ProductView(name, stock, brand, category);
    }

    public void setTextsField(ProductView productView) {
        nameField.setText(productView.getName());
        brandField.setText(productView.getBrand());
        categoryField.setText(productView.getCategory());
        stockField.setText(String.valueOf(productView.getStock()));
    }

    public void updateProductView(ProductView productView) {
        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();
        String category = categoryField.getText().trim();
        String stockText = stockField.getText().trim();

        if (!stockText.matches("^\\d{1,3}$")) {
            stockText = "0";
        }
        int stock = Integer.parseInt(stockText);

        boolean isSuccess = Datasource.getInstance().updateProduct(
                productView.getName(), name, stock, brand, category);
        
        if (isSuccess) {
            productView.setName(name);
            productView.setBrand(brand);
            productView.setCategory(category);
            productView.setStock(stock);
        }
    }
}
