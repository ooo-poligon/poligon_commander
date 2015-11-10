/*
 *
 *
 */
package poligoncommander;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 *
 * @author Igor Klekotnev
 */
public class PCommanderController implements Initializable {
        
    @FXML
    private StackPane stackPane;
    
    @FXML
    private TreeView<String> categoriesTree;    

    @FXML
    private TableView<Product> productsTable;

    //@FXML
    //private TableColumn<Product, String> productVendor;
    
    @FXML
    private TableColumn<Product, String> productSerie;    
    
    @FXML
    private TableColumn<Product, String> productTitle;
    
    @FXML
    private TableColumn<Product, String> productDescription;
    
    static private ObservableList<Category> sections;
    static private ObservableList<Category> subCategoriesList;

    // JDBC URL, username and password of MySQL server
    private static final String dbUrl = "jdbc:mysql://localhost:3306/poligon";
    private static final String user = "root";
    private static final String password = "poligon";
 
    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        buildCategoryTree();

    }

    private ObservableList<Product> getProductList(String selectedNode) {
        ObservableList<Product> data = FXCollections.observableArrayList();
        
        String query = "select vendor, serie, title, description from products where section=" + "\"" + selectedNode + "\"";
 
        try {

            // opening database connection to MySQL server
            con = DriverManager.getConnection(dbUrl, user, password);
 
            // getting Statement object to execute query
            stmt = con.createStatement();
 
            // executing SELECT query
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                data.add(new Product(
                                    rs.getString("vendor"),
                                    rs.getString("serie"),
                                    rs.getString("title"),
                                    rs.getString("description")
                                    )
                        );
            }
 
        } catch (SQLException sqlEx) {
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }            
        return data;
    }
    private void buildProductsTable(ObservableList<Product> data) {
            
        // To enable VENDOR column -> set it visible in fxml file and uncomment variable productVendor in begining of class declaration.
        //productVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        productSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));
        productTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        productDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        productsTable.setItems(data);
                   
    }
    private void buildCategoryTree() {
        
        String query = "select id, title, parent from sections";
 
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(dbUrl, user, password);
 
            // getting Statement object to execute query
            stmt = con.createStatement();
 
            // executing SELECT query
            rs = stmt.executeQuery(query);
            
            sections = FXCollections.observableArrayList();

            while (rs.next()) {
                sections.add(new Category(rs.getInt("id"), rs.getString("title"), rs.getInt("parent")));
            }
            
            ArrayList<Category> categories = new ArrayList();
            sections.stream().forEach((section) -> {
                categories.add(section);
            });            
            
            Category catalogRoot = new Category(0, "Каталог товаров", 0);
            TreeItem<String> rootItem = new TreeItem<> (catalogRoot.getTitle());
            rootItem.setExpanded(true);
            
            buildTreeNode(categories, rootItem, catalogRoot);

            categoriesTree = new TreeView<> (rootItem);
            
            // Создаём обработчик событий от мыши
            EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
                // Вызываем метод, возврщающий нам название кликнутого узла
                handleMouseClicked(event);
            };
            // Добавляем обработчик событий от мыши к нашему дереву
            categoriesTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
            
            stackPane.getChildren().add(categoriesTree);            
        } catch (SQLException sqlEx) {
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }                
    }
    private void buildTreeNode (ArrayList<Category> categories, TreeItem<String> rootItem, Category catalogRoot) {
        categories.stream().filter((categorie) -> (categorie.getParent().equals(catalogRoot.getId()))).forEach((Category categorie) -> {
            TreeItem<String> treeItem = new TreeItem<> (categorie.getTitle());
            rootItem.getChildren().add(treeItem);
            buildTreeNode(categories, treeItem, categorie);
        });
    }    
    private void handleMouseClicked(MouseEvent event) {
        ObservableList<Product> data = FXCollections.observableArrayList();
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            String selectedNode = (String) ((TreeItem)categoriesTree.getSelectionModel().getSelectedItem()).getValue();
            subCategoriesList(selectedNode);

            if (!subCategoriesList.isEmpty()) {
                subCategoriesList.stream().forEach((sub) -> {
                    getProductList(sub.getTitle()).stream().forEach((product) -> {
                        data.add(product);
                    });
                });
                buildProductsTable(data);
            } else {
                getProductList(selectedNode).stream().forEach((product) -> {
                    data.add(product);
                });
                buildProductsTable(data);
            }
        }
    }   
    private void subCategoriesList(String selectedNode) {

        String query = "SELECT title FROM sections t1, (SELECT id FROM sections WHERE title=" + "\"" + selectedNode + "\") t2 WHERE t2.id = t1.parent";
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(dbUrl, user, password);
 
            // getting Statement object to execute query
            stmt = con.createStatement();
 
            // executing SELECT query
            rs = stmt.executeQuery(query);
            
            subCategoriesList = FXCollections.observableArrayList();

            while (rs.next()) {
                subCategoriesList.add(new Category(rs.getString("title")));
            }
            
        } catch (SQLException sqlEx) {
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }
}
 