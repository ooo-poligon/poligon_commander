/*
 *
 *
 */
package main;

import utils.ProductImage;
import treeviews.CategoriesTreeView;
import entities.Analogs;
import entities.Categories;
import entities.FileTypes;
import entities.Files;
import entities.ImportFields;
import entities.Products;
import entities.Quantity;
import entities.Settings;
//import java.awt.event.KeyEvent;
import javafx.scene.control.TextField;
import tableviews.ProductsTableView;
import tableviews.AnalogsTableView;
import tableviews.DatasheetTableView;
import tableviews.PricesTableView;
import tableviews.QuantityTableView;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.AutoCompleteComboBoxListener;
import utils.HibernateUtil;
import utils.XLSHandler;
import utils.XLSToDBImport;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Igor Klekotnev
 */
public class PCGUIController implements Initializable {
    
    // Scenes
    public static Scene scene;
    
    // Panes
    @FXML private AnchorPane anchorPane; 
    @FXML private AnchorPane allTables;
    
    @FXML private StackPane  stackPane; 
    
    @FXML private GridPane   gridPane;      
    @FXML private GridPane   gridPanePDF;
    
    // TreeViews
    @FXML private TreeView<String>                       categoriesTree; 
    
    // TableViews & TableColumns 
    @FXML private TableView<ProductsTableView>            productsTable;   
    @FXML private TableColumn<ProductsTableView, String>  productArticle;       
    @FXML private TableColumn<ProductsTableView, String>  productTitle;    
    @FXML private TableColumn<ProductsTableView, String>  productDescription;
    
    @FXML private TableView<PricesTableView>              pricesTable;    
    @FXML private TableColumn<PricesTableView, String>    priceType;   
    @FXML private TableColumn<PricesTableView, Double>    priceValue;    
    @FXML private TableColumn<PricesTableView, Double>    priceValueRub; 
    
    @FXML private TableView<QuantityTableView>            quantitiesTable;    
    @FXML private TableColumn<QuantityTableView, String>  quantityStock;   
    @FXML private TableColumn<QuantityTableView, String>  quantityReserved;   
    @FXML private TableColumn<QuantityTableView, String>  quantityOrdered;   
    @FXML private TableColumn<QuantityTableView, String>  quantityMinimum;    
    @FXML private TableColumn<QuantityTableView, String>  quantityPiecesPerPack;

    @FXML private TableView<AnalogsTableView>             analogsTable;    
    @FXML private TableColumn<AnalogsTableView, String>   analogTitle;    
    @FXML private TableColumn<AnalogsTableView, String>   analogVendor;
    
    @FXML private TableView<ProductsTableView>            deliveryTable;
    @FXML private TableColumn<ProductsTableView, String>  deliveryTime;

    @FXML private TableView<DatasheetTableView>           datasheetFileTable;    
    @FXML private TableColumn<DatasheetTableView, String> datasheetFileName; 
    
    // Buttons
    @FXML private Button openPictureButton; 
    @FXML private Button headersRowSetButton;
    @FXML private Button chooseXLSButton;
    @FXML private Button compareXLSToDBButton;
    @FXML private Button startImportXLSButton;
    @FXML private Button cancelSetImportButton;
    
    // Labels
    
    // ProgressBars
    @FXML private ProgressBar progressBar;
    @FXML private ProgressBar progressBarImportXLS;
    
    // ImageViews
    @FXML private ImageView imageView;
    
    // ListViews
    @FXML private ListView<String> headersXLS;
    @FXML private ListView<String> comparedXLSAndDBFields;
    
    // ComboBoxes
    @FXML private ComboBox<String> searchComboBox;    
    @FXML private ComboBox<String> importFieldsComboBox;
    @FXML private ComboBox<String> importKeysComboBox;    
    
    // TextFields
    @FXML private TextField headersRowTextField;
    
    // CheckBoxes
    @FXML private CheckBox treeViewHandlerMode;
    
    //Files & FileChoosers
    File fileXLS;
    final   FileChooser fileChooser = new FileChooser(); 
    
    // Lists
    ArrayList<ArrayList<String>> allImportXLSContent = new ArrayList<>();    

    ArrayList<ArrayList<String>> allCompareDetails = new ArrayList<>();
    ObservableList<CategoriesTreeView> subCategoriesTreeViewList;
    ObservableList<String> allProducts = FXCollections.observableArrayList();    
    ObservableList<ImportFields> importFields = FXCollections.observableArrayList();
    ObservableList<String> comparedPairs = FXCollections.observableArrayList();    
    // Maps
    Map<String, Double> allPrices = new HashMap<>();
    
    // Threads
    Thread thread;
    
    // Numbers
    private Integer headersRowNumber = 0;
    public final  Double course = 74.5;    
    Double basePrice;
     
    // Strings
    String selectedProduct;    

    String selectedDBKey = "Наименование продукта";
    String catalogHeader = "Каталог товаров";
    
    //booleans
    boolean clear = true;   
 
    @Override
    // Выполняется при запуске программы
    public void initialize(URL url, ResourceBundle rb) { 
        loadSavedSettings();        
        openPictureButton.setDisable(false);
        //startImportXLSButton.setDisable(true);
        buildCategoryTree();
        populateComboBox ();
        getAllPrices();
        loadImportFields();
        //loadImportKeys();
    }
    // Загружает в память настройки программы, сохранённые в БД    
    private void loadSavedSettings() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Settings where title=\'TreeViewMode\' and kind=\'ProgramSettings\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Settings setting = (Settings) iterator.next();
            //System.out.println(setting.getIntValue());
        }
        session.close();
    }
    // Загружает в память список полей БД, доступных для импорта через xls-файл
    //Заполняет список значений importFieldComboBox
    private void loadImportFields() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From ImportFields").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ImportFields field = (ImportFields) iterator.next();
            importFields.add(new ImportFields(field.getId(), field.getTitle(), field.getTableName(), field.getField()));
        }
        session.close();
        ObservableList<String> fieldsNames = FXCollections.observableArrayList();        
        importFields.stream().forEach((field) -> {
            fieldsNames.add(field.getTitle());
        });
        importFieldsComboBox.setItems(fieldsNames);
    }
    // Загружает в память список полей БД, доступных для импорта через xls-файл 
    //Заполняет список значений importKeyComboBox
    private void loadImportKeys() {
        ObservableList<String> fieldsNames = FXCollections.observableArrayList();        
        importFields.stream().forEach((field) -> {
            fieldsNames.add(field.getTitle());
        });
        importKeysComboBox.setItems(fieldsNames);
    }
    // Получает все продукты из БД для отображения в таблице
    // В виде аргумента использует id родительской категории
    private ObservableList<ProductsTableView> getProductList(Integer selectedNodeID) {        
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();    
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            //tx = session.beginTransaction();
            List response = session.createQuery("From Products where category_id=" + selectedNodeID).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products product = (Products) iterator.next();
                data.add(new ProductsTableView(
                        product.getArticle(), 
                        product.getTitle(), 
                        product.getDescription(),
                        product.getDeliveryTime()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }         
        return data;
    } 
    // Получает все продукты из БД для отображения в таблице
    // В виде аргумента использует title родительской категории
    private ObservableList<ProductsTableView> getProductList(String selectedNode) {        
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();    
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Products where category_id=" + getCategoryIdFromTitle(selectedNode)).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products product = (Products) iterator.next();
                data.add(new ProductsTableView(
                        product.getArticle(), 
                        product.getTitle(), 
                        product.getDescription(),
                        product.getDeliveryTime()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }         
        return data;
    }
    // Получает данные о количестве продукта из БД
    // В виде аргумента принимает название продукта
    private ObservableList<QuantityTableView> getQuantities(String productName) {
        ObservableList<QuantityTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            //Make an HQL query to get the results from books table . You can also use SQL here.
            List response = session.createQuery("From Quantity where productId=" + getProductIdFromTitle(productName)).list();
            //Iterate over the result and print it.
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Quantity q = (Quantity) iterator.next();
                data.add(new QuantityTableView(
                        q.getStock(),
                        q.getReserved(),
                        q.getOrdered(),
                        q.getMinimum(),
                        q.getPiecesPerPack()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }         
        return data;        
    }
    // Получает данные об аналогах для продукта из БД
    // В виде аргумента принимает название продукта    
    private ObservableList<AnalogsTableView> getAnalogs(String productName) {
        ObservableList<AnalogsTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //Make an HQL query to get the results from books table . You can also use SQL here.
            List response = session.createQuery("From Analogs where prototypeId=" + getProductIdFromTitle(productName)).list();
            //Iterate over the result and print it.
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Analogs a = (Analogs) iterator.next();
                data.add(new AnalogsTableView(
                    a.getTitle(),
                    a.getVendor().getTitle()
                    )
                );
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }         
        return data;  
    } 
    // Получает из БД hashmap базовых цен, сопоставленных с продуктом по его названию
    private void getAllPrices() {  
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Products").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products p = (Products) iterator.next();
                    allPrices.put(p.getTitle(), p.getPrice());
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }        
    }
    
    // Построение таблиц с данными полученными из БД
    private void buildProductsTable(ObservableList<ProductsTableView> data) {
        productArticle.setCellValueFactory(new PropertyValueFactory<>("article"));
        productTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        productDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Создаём обработчик событий от мыши
        EventHandler<MouseEvent> mouseEventHandle1 = (MouseEvent event1) -> {
            // Вызываем метод, возврщающий нам ...
            handleProductTableMousePressed(event1);
        };
        // Добавляем обработчик событий от мыши к нашей таблице
        productsTable.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandle1);
        productsTable.setItems(data);
        productsTable.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandle1);                  
    }
    private void buildDeliveryTimeTable(String selectedProduct) {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Products where id=" + getProductIdFromTitle(selectedProduct)).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products p = (Products) iterator.next();
                data.add(new ProductsTableView(
                    p.getDeliveryTime()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }         
        deliveryTime.setCellValueFactory(new PropertyValueFactory<>("delivery_time"));
        deliveryTable.setItems(data);       
    }    
    private void buildPricesTable(String selectedProduct) {
        //getAllPrices();
        allPrices.entrySet().stream().filter((entry) -> (entry.getKey().equals(selectedProduct))).map((entry) -> {
            return entry;
        }).forEach((entry) -> {
            basePrice = entry.getValue();
        });       

        ObservableList<PricesTableView> data = FXCollections.observableArrayList(); 
        String[] priceTypes = {
                "Розничная",
                "Мелкий опт",
                "Оптовая",
                "Диллер1",
                "Диллер2",
                "Диллер3",
                "Базовая",
                "Входная"
        };
        
        for (String type : priceTypes) { 
            try {
               
            switch (type) {
                case "Розничная":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.5)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.5 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;
                case "Мелкий опт":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.4)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.4 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break; 
                case "Оптовая":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.3)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.3 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;  
                case "Диллер1":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.2)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.2 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break; 
                case "Диллер2":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.1)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.1 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;  
                case "Диллер3":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.05)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.05 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;                     
                case "Базовая":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 1.0)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 1.0 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;     
                case "Входная":
                    data.add(new PricesTableView(type, ((new BigDecimal(basePrice * 0.85)).setScale(2, RoundingMode.UP)).doubleValue(),
                                                  ((new BigDecimal(basePrice * 0.85 * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                    break;                     
                default:
                    break;
            }
            } catch (NullPointerException nex) {
                System.out.println("nulled pricesTable!!!");
            }
        }                     
        priceType.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceValue.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceValueRub.setCellValueFactory(new PropertyValueFactory<>("priceR"));
        pricesTable.setItems(data);               
    }
    private void buildQuantityTable(String selectedProduct) {
        ObservableList<QuantityTableView> quantities = getQuantities(selectedProduct);
        quantityStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        quantityReserved.setCellValueFactory(new PropertyValueFactory<>("reserved"));
        quantityOrdered.setCellValueFactory(new PropertyValueFactory<>("ordered"));
        quantityMinimum.setCellValueFactory(new PropertyValueFactory<>("minimum"));
        quantityPiecesPerPack.setCellValueFactory(new PropertyValueFactory<>("pieces_per_pack"));        
        quantitiesTable.setItems(quantities);             
    }
    private void buildAnalogsTable(String selectedProduct) {
        ObservableList<AnalogsTableView> analogs = getAnalogs(selectedProduct);
        analogTitle.setCellValueFactory(new PropertyValueFactory<>("analog_title"));
        analogVendor.setCellValueFactory(new PropertyValueFactory<>("analog_vendor"));       
        analogsTable.setItems(analogs);                
    }
    private void buildDatasheetFileTable(String selectedProduct) {
        ObservableList<DatasheetTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Files where ownerId=" + getProductIdFromTitle(selectedProduct) + " and fileTypeId=2").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Files f = (Files) iterator.next();
                data.add(new DatasheetTableView(
                    f.getName()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }  
        datasheetFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        datasheetFileTable.setItems(data);     
    }
    private void buildImageView(String selectedProduct) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List pics = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(selectedProduct) + " and fileTypeId=1").list();
        for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
            Files pic = (Files) iterator.next();
            File picFile = new File(pic.getPath());
            ProductImage.open(picFile, gridPane, imageView);
        }        
    }
    
    // Построение дерева категорий каталога
    private void buildCategoryTree() {
        ObservableList<CategoriesTreeView> sections = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();     
        try {
            List categories = session.createQuery("FROM Categories").list();
            for (Iterator iterator = categories.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                sections.add(new CategoriesTreeView(category.getId(), category.getTitle(), category.getParent()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }            
        ArrayList<CategoriesTreeView> categories = new ArrayList();
        sections.stream().forEach((section) -> {
            categories.add(section);
        });            
        CategoriesTreeView catalogRoot = new CategoriesTreeView(0, catalogHeader, 0);
        TreeItem<String> rootItem = new TreeItem<> (catalogRoot.getTitle());
        rootItem.setExpanded(true); 
        buildTreeNode(categories, rootItem, catalogRoot);
        categoriesTree = new TreeView<> (rootItem);
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleCatregoryTreeMouseClicked(event);
        };               
        categoriesTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);            
        stackPane.getChildren().add(categoriesTree);            
    }
    private void buildTreeNode (ArrayList<CategoriesTreeView> categories, TreeItem<String> rootItem, CategoriesTreeView catalogRoot) {
        categories.stream().filter((categorie) -> (categorie.getParent().equals(catalogRoot.getId()))).forEach((CategoriesTreeView categorie) -> {
            TreeItem<String> treeItem = new TreeItem<> (categorie.getTitle());
            rootItem.getChildren().add(treeItem);
            buildTreeNode(categories, treeItem, categorie);
        });
    }    
    private void subCategoriesList(String selectedNode) {
        subCategoriesTreeViewList = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List subCategories = session.createSQLQuery("SELECT title FROM categories t1, (SELECT id FROM categories WHERE title=" + "\"" + normalize(selectedNode) + "\") t2 WHERE t2.id = t1.parent").list();
                for (Iterator iterator = subCategories.iterator(); iterator.hasNext();) {
                    String sub = (String) iterator.next();                    
                    subCategoriesTreeViewList.add(new CategoriesTreeView(sub));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
    }
    private void includeLowerItems(ObservableList<ProductsTableView> data, String selectedNode) {
        reсursiveItems(data, getCategoryIdFromTitle(selectedNode)); 
        // повторяет в SONDERE 2 раза - надо исправить!!!!!!!
        getProductList(selectedNode).stream().forEach((product) -> {
            excludeLowerItems(data, selectedNode);
        });
    }
    private void reсursiveItems(ObservableList<ProductsTableView> data, Integer selectedNode) {
        ArrayList<Integer> childs = arrayChilds(selectedNode);
        if(!childs.isEmpty()) {
            childs.stream().forEach((ch) -> {
                reсursiveItems(data, ch);
            });
        } else {
            getProductList(selectedNode).stream().forEach((product) -> {
                data.add(product);
            });        
        }
    }
    private void excludeLowerItems(ObservableList<ProductsTableView> data, String selectedNode) {
        getProductList(selectedNode).stream().forEach((product) -> {
            data.add(product);
        }); 
    } 
    private ArrayList<Integer> arrayChilds(Integer parent) {
        ArrayList<Integer> childs = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Categories where parent=" + parent).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Categories cat = (Categories) iterator.next();
            childs.add(cat.getId());
        }
        session.close();
        return childs;
    }
    private Task<Void> createTask(MouseEvent event) {
        return new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
                // Вызываем метод, возврщающий нам название кликнутого узла
                Node node = event.getPickResult().getIntersectedNode();
                // Accept clicks only on node cells, and not on empty spaces of the TreeView
                if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                    String selectedNode = (String) ((TreeItem)categoriesTree.getSelectionModel().getSelectedItem()).getValue();
                    subCategoriesList(selectedNode);
                    if (treeViewHandlerMode.isSelected()) {
                        //System.out.println("checked");
                        includeLowerItems(data, selectedNode);
                    } else {
                        excludeLowerItems(data, selectedNode);
                        //System.out.println("unchecked");
                    } 
                    openPictureButton.setDisable(true);
                    buildProductsTable(data);
                }
                Platform.runLater(() -> {
                    //progressIndicator.progressProperty().unbind();
                    //progressIndicator.setVisible(false);
                    //progressIndicator.setProgress(0.0);                    
                    progressBar.progressProperty().unbind();
                    progressBar.setProgress(0.0);
                });                
                return null;
            }
        };
    }    
    
    // Получает из БД список всех названий продуктов и прикрепляет его к Combobox поиска по названию
    // Также вызывает экземпляр класса автодополнения при поиске
    private void populateComboBox () {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Products").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products p = (Products) iterator.next();
            allProducts.add(p.getTitle());
        }
        searchComboBox.getItems().clear();
        searchComboBox.getItems().addAll(allProducts);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(searchComboBox);
    } 
    
    // Группа методов для импорта данных в БД из xls-файла
    public void startProgressBarImportXLS() {
        Task task = createImportXLSTask();
        progressBarImportXLS.progressProperty().bind(task.progressProperty());
        thread  = new Thread(task);
        thread.start();
    }    
    private Task<Void> createImportXLSTask() {
        return new Task<Void>() {
            @Override
            public Void call() throws InterruptedException { 
                try {
                    allImportXLSContent = XLSHandler.grabData(fileXLS.getAbsolutePath());
                } catch (IOException ex) {
                    Logger.getLogger(PCGUIController.class.getName()).log(Level.SEVERE, null, ex);
                }            
                Platform.runLater(() -> {
                    //progressIndicator.progressProperty().unbind();
                    //progressIndicator.setVisible(false);
                    //progressIndicator.setProgress(0.0);                    
                    progressBarImportXLS.progressProperty().unbind();
                    progressBarImportXLS.setProgress(0.0);
                });                
            return null;    
            }
        }; 
    }
    
    // Утилиты разные
    private Integer getCategoryIdFromTitle (String title) { 
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List ids = session.createSQLQuery("select id from categories where title=\"" + title + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
                return id;
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }        
        return id;
    }
    private Integer getProductIdFromTitle (String title) { 
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List ids = session.createSQLQuery("select id from products where title=\"" + title + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
                return id;
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }        
        return id;
    } 
    private String normalize(String string) {
        //проверить корректность работы "normalize" позже
        if (string.contains("\t") || (string.contains("\n"))) {
            string.replace('\t', ' ');
            string.replace('\n', ' ');
        }
        String s = "";
        if ((string.contains("\""))) {
            String[] sub = string.split("\"");
            for (int i = 0; i < sub.length; i++) {
                if (i == 0) {
                    s = sub[i] + "\"";
                } else if (i == sub.length - 1) {
                    s = "\"" + sub[i];
                } else {
                    s = "\"" + sub[i] + "\"";
                }
                string += s;
            }
        }
        return string;
    }  
    @FXML private void getSelectedHeader() {
        sHeader();
    }    
    private String sHeader() {
        String sh = new String();
        try {
            sh = headersXLS.getSelectionModel().getSelectedItem();            
        } catch (NullPointerException ne) {} 
        return sh;
    }
    // Обработчики событий от элементов интерфейса
    @FXML private void handleCatregoryTreeMouseClicked(MouseEvent event) {
        startProgressBar(event);        
    }   
    
    @FXML public void startProgressBar(MouseEvent event) {
        Task task = createTask(event);
        //progressIndicator.setVisible(true);
        //progressIndicator.progressProperty().bind(task.progressProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        thread  = new Thread(task);
        thread.start();
    }
    
    @FXML private void handleProductTableMousePressed(MouseEvent event1) {
        try {    
            selectedProduct = (String) (productsTable.getSelectionModel().getSelectedItem()).getTitle();
            buildPricesTable(selectedProduct);
            buildQuantityTable(selectedProduct);
            buildDeliveryTimeTable(selectedProduct);
            buildAnalogsTable(selectedProduct);
            buildDatasheetFileTable(selectedProduct);
            buildImageView(selectedProduct);
            openPictureButton.setDisable(false);
            datasheetFileTable.refresh();
        } catch (NullPointerException ex) {}
    }    
    
    @FXML private void handleSearchComboBox() {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Products where title= \'" + normalize(searchComboBox.getValue()) + "\'").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products product = (Products) iterator.next();
                data.add(new ProductsTableView(
                        product.getArticle(), 
                        product.getTitle(), 
                        product.getDescription(),
                        product.getDeliveryTime()
                    )
                );
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        buildProductsTable(data);  
    }    

    @FXML private void setPictureButtonPress() {
        openPictureButton.setOnAction((final ActionEvent e) -> {
            File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
            if (file != null) {                
                ProductImage.open(file, gridPane, imageView);
                ProductImage.save(file, selectedProduct);
            }
        });         
    }
    
    @FXML private void setDatasheetFile() { 
        datasheetFileTable.setOnMousePressed((final MouseEvent e) -> {
            File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
            if (file != null) { 
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();              
                List pdfList = session.createQuery("From Files where ownerId=" + getProductIdFromTitle(selectedProduct) + "and fileTypeId=2").list();
                if (pdfList.isEmpty()) {
                    Files pdfFile = new Files(file.getName(), file.getPath(), "Это даташит для " + selectedProduct, (new FileTypes(2)), (new Products(getProductIdFromTitle(selectedProduct))));
                    session.saveOrUpdate(pdfFile);
                    datasheetFileTable.refresh();                    
                } else {
                    for (Iterator iterator = pdfList.iterator(); iterator.hasNext();) {
                        Files pdf = (Files) iterator.next();
                        if ((!pdf.getName().equals(file.getName())) || (!pdf.getPath().equals(file.getPath()))) {
                            pdf.setName(file.getName());
                            pdf.setPath(file.getPath());
                            pdf.setDescription("Это даташит для " + selectedProduct);
                            session.saveOrUpdate(pdf);
                            datasheetFileTable.refresh();                             
                        }
                    }            
                }
                tx.commit();
            }
        });
        buildDatasheetFileTable(selectedProduct);        
        gridPanePDF.getChildren().clear();
        gridPanePDF.getChildren().add(datasheetFileTable);
        gridPanePDF.getChildren().add(deliveryTable);
        productsTable.setFocusTraversable(true);
        datasheetFileTable.refresh();        
    }     

    @FXML private void setChooseXLSButtonPress() {
            fileXLS = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
            if (fileXLS != null) {                
                startProgressBarImportXLS();                
            }       
    } 
     
    @FXML private void getHeadersRowNumber(KeyEvent e) {
        if(e.getCode().toString().equals("ENTER"))
        {
            ObservableList<String> data = FXCollections.observableArrayList();
            if (allImportXLSContent.isEmpty()) {
                    showWarningWindow("Сначала необходимо выбрать xls-файл, содержащий данные для импорта.");
                }            
            try {
                headersRowNumber = Integer.parseInt(headersRowTextField.getText()) - 1;
                if (headersRowNumber < 0) {
                    showWarningWindow("Вы ввели недопустимое число. Будет установленно значение по умолчанию, что соответствует числу 1.");
                    headersRowTextField.setText("1");
                    headersRowNumber = 0;
                }                
            } catch (NumberFormatException ex) {
                showWarningWindow("Вы ввели недопустимое число. Будет установленно значение по умолчанию, что соответствует числу 1.");
                headersRowTextField.setText("1");
                headersRowNumber = 0;
            }
            try {
            allImportXLSContent.stream().forEach((column) -> {
                data.add(column.get(headersRowNumber));            
            });
            } catch (NullPointerException ne) {
                showWarningWindow("Не введён номер строки, содержащей заголовки колонок в xls-файле. Перед вводом номера строки убедитесь, что xls-файл с данными уже выбран.");
            } catch (IndexOutOfBoundsException ie) {
                showWarningWindow("Введён номер строки, превышшающий общее количество строк в выбранном xls-файле. Откорректируйте входящие данные.");                
            }
            headersXLS.setItems(data);
        }
        clear = false;
    }    
    
    // Запускается при выборе значения в importFieldsComboBox
    @FXML private void getSelectedDBField() {        
        sField();
    }
    
    private String sField() {
        return importFieldsComboBox.getValue();
    }
    // Запускается при выборе значения в importKeysComboBox
    @FXML private void getSelectedDBKey() {
        selectedDBKey = "Наименование продукта";
        //selectedDBKey = importKeysComboBox.getValue();
        startImportXLSButton.setDisable(false);        
    }
    // Запускается при нажатии кнопки compareXLSToDBButton
    @FXML private void compareFields() throws IOException {
        compareFieldsMechanics();
    }
    // Запускается при нажатии кнопки startImportXLSButton
    @FXML private void startImportFromXLSToDB() {       
        XLSToDBImport importer = new XLSToDBImport(allCompareDetails);
        importer.startImport(allImportXLSContent, importFields, allProducts);
    }
    @FXML private void showWarningWindow(String warningText) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Неправильная настройка импорта данных.");
        alert.setContentText(warningText);
        alert.showAndWait();
    }
    
    private void compareFieldsMechanics() throws IOException {
        ArrayList<String> compareDetails = new ArrayList<>();    
        String selectedHeader = sHeader();
        String selectedDBField = sField();        
        if (allImportXLSContent.isEmpty()) {
            showWarningWindow("Нечего импортировать. Выберите xls-файл с данными\nи сопоставьте его колонки с полями в базе данных.");
        } else if (headersRowTextField.getText().equals("")) {
            showWarningWindow("Выберите номер строки в xls-файле, где содержатся заголовки колонок для импорта.");
        } else {
            if (selectedHeader==null && selectedDBField==null) {
                showWarningWindow("Не выбраны данные для сопоставления.");
            } else if (selectedHeader==null) { 
                showWarningWindow("Не выбран заголовок колонки в xls-файле.");
            } else if (selectedDBField==null) { 
                showWarningWindow("Не выбрано поле для импорта в базе данных.");
            } else {

                boolean wasAdded = false;
                boolean wasHeader = false;
                boolean wasField = false;
                for (int i = 0; i < comparedXLSAndDBFields.getItems().size(); i++) {
                    if (comparedXLSAndDBFields.getItems().get(i).equals(selectedHeader + "  -->  " + selectedDBField)) {
                        wasAdded = true;
                    } else if (comparedXLSAndDBFields.getItems().get(i).contains(selectedHeader)) {
                        wasHeader = true;
                    } else if (comparedXLSAndDBFields.getItems().get(i).contains(selectedDBField)) {
                        wasField = true;
                    }
                }
                if (!wasAdded && !wasHeader && !wasField) {
                    compareDetails.add(selectedHeader);        
                    compareDetails.add(selectedDBField);                    
                    allCompareDetails.add(compareDetails);                    
                    comparedPairs.add(selectedHeader + "  -->  " + selectedDBField);                    
                } else if (!wasAdded && wasHeader && !wasField) {
                    showWarningWindow("Этот заголовок уже лобавлен.");
                } else if (!wasAdded && !wasHeader && wasField) {
                    showWarningWindow("Это поле уже сопоставлено с другим заголовком.");
                } else {
                    showWarningWindow("Эти данные уже участвовали в сопоставлении.");
                }
                comparedXLSAndDBFields.setItems(comparedPairs); 
                clear = false;
            }            
        } 
    }    
    @FXML private void cancelSetImportDetails() {
        
        ObservableList<String> data = FXCollections.observableArrayList();
        headersRowNumber = 0;
        try {
            allImportXLSContent.clear();            
        } catch (NullPointerException ne) {
            showWarningWindow("Нет данных для очистки.");
        }       
        try {
            allCompareDetails.clear();            
        } catch (NullPointerException ne) {
            showWarningWindow("Нет данных для очистки.");
        }
        try {
            comparedPairs.clear();            
        } catch (NullPointerException ne) {
            showWarningWindow("Нет данных для очистки.");
        }        
        headersRowTextField.clear();
        
        headersXLS.setItems(data);
        comparedXLSAndDBFields.setItems(data);
        headersXLS.refresh();
        comparedXLSAndDBFields.refresh();
        clear = true;
    }
}
 