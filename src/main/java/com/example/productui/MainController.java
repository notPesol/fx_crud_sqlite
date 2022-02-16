package com.example.productui;

import com.example.productui.db.Datasource;
import com.example.productui.model.ProductView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public TableView<ProductView> tableView;
    @FXML
    public TableColumn<ProductView, String> columnName;
    @FXML
    public TableColumn<ProductView, String> columnBrand;
    @FXML
    public TableColumn<ProductView, String> columnCategory;
    @FXML
    public TableColumn<ProductView, Integer> columnStock;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label progressText;

    private final Datasource datasource = Datasource.getInstance();

    @FXML
    public BorderPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Edit");
        menuItem.setOnAction(actionEvent -> {
            try {
                showEditProductDialog();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        contextMenu.getItems().add(menuItem);

        menuItem = new MenuItem("Delete");
        menuItem.setOnAction(actionEvent -> showDeleteProductDialog());
        contextMenu.getItems().add(menuItem);

        tableView.setContextMenu(contextMenu);

        GetProductViewTask task = new GetProductViewTask();
        tableView.itemsProperty().bind(task.valueProperty());
        progressBar.visibleProperty().bind(task.runningProperty());
        progressText.visibleProperty().bind(task.runningProperty());

        task.setOnSucceeded(workerStateEvent -> tableView.getSelectionModel().select(0));

        new Thread(task).start();
    }

    @FXML
    public void showAddNewProductDialog() throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Add Product...");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product-dialog.fxml"));
        dialog.getDialogPane().setContent(fxmlLoader.load());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ProductDialogController controller = fxmlLoader.getController();
            ProductView productView = controller.getNewProduct();
            datasource.insertProduct(
                    productView.getName(),
                    productView.getStock(),
                    productView.getBrand(),
                    productView.getCategory()
            );

            tableView.getItems().add(productView);
            tableView.refresh();
        }

    }

    @FXML
    public void showEditProductDialog() throws IOException {
        ProductView productView = tableView.getSelectionModel().getSelectedItem();

        if (productView == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("product-dialog.fxml"));

        dialog.getDialogPane().setContent(fxmlLoader.load());
        ProductDialogController controller = fxmlLoader.getController();
        controller.setTextsField(productView);
        dialog.setTitle("Edit The Product");
        dialog.setHeaderText("Edit Product...");



        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateProductView(productView);
            tableView.refresh();
        }
    }

    @FXML
    public void showDeleteProductDialog() {
        ProductView productView = tableView.getSelectionModel().getSelectedItem();
        if (productView == null) return;

        String productName = productView.getName();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete the product");
        alert.setHeaderText("Product name: " + productName);
        alert.setContentText("Press OK button to confirm...");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            datasource.deleteProductByName(productName);
            tableView.getItems().remove(productView);
        }
    }

    @FXML
    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
    }
}

class GetProductViewTask extends Task<ObservableList<ProductView>> {
    @Override
    protected ObservableList<ProductView> call() {
        return FXCollections.observableList(
                Datasource.getInstance().queryAllProductView(Datasource.ORDER_BY.ASC)
        );
    }
}