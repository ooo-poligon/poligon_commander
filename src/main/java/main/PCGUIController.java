/*
 *
 *
 */
package main;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import utils.ProductImage;
import utils.NewCategory;
import treeviews.CategoriesTreeView;
import entities.Analogs;
import entities.Categories;
import entities.FileTypes;
import entities.Files;
import entities.ImportFields;
import entities.Products;
import entities.Quantity;
import entities.Settings;
import tableviews.ProductsTableView;
import tableviews.AnalogsTableView;
import tableviews.DatasheetTableView;
import tableviews.PricesTableView;
import tableviews.QuantityTableView;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.AutoCompleteComboBoxListener;
import utils.HibernateUtil;
import utils.XLSHandler;
import utils.XLSToDBImport;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.scene.input.ClipboardContent;
import org.hibernate.Query;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

/**
 *
 * @author Igor Klekotnev
 */
public class PCGUIController implements Initializable {
    
    // Scenes
    public static Scene scene;
    
    //Stages
        Stage newCatStage = new Stage();
    
    // Panes
    @FXML private AnchorPane anchorPane; 
    @FXML private AnchorPane allTables;
    @FXML private AnchorPane newCatDialogAnchorPane;
    @FXML private AnchorPane datasheetAnchorPane;

    
    @FXML private StackPane  stackPane; 
    
    @FXML private GridPane   gridPane;      
    @FXML private GridPane   gridPanePDF;
    @FXML private GridPane productTabGridPaneImageView;    
    
    // TreeViews
    @FXML private TreeView<String> categoriesTree;
    
    //Tabs
    @FXML TabPane tabPane;
    @FXML Tab productTab;
    @FXML Tab pdfTab;
    
    // ContextMenus
    @ FXML private ContextMenu treeViewContextMenu;
    @ FXML private ContextMenu productTableContextMenu;
    @ FXML private ContextMenu datasheetTableContextMenu;    
    @ FXML private MenuItem openProductTabMenu;
    @ FXML private MenuItem createCategoryItem;
    @ FXML private ContextMenu imageViewContextMenu;
    
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
    //@FXML private Button openPictureButton;
    @FXML private Button headersRowSetButton;
    @FXML private Button chooseXLSButton;
    @FXML private Button compareXLSToDBButton;
    @FXML private Button startImportXLSButton;
    @FXML private Button cancelSetImportButton;
    @FXML private Button saveNewCategory;
    @FXML private Button cancelNewCategory;
    @FXML private Button changePhotoButton;
    @FXML private Button changePicDescriptionButton;
    
    // Labels
    @FXML private Label productTabTitle;
    @FXML private Label pdfTabTitle;
    
    // ProgressBars
    @FXML private ProgressBar progressBar;
    @FXML private ProgressBar progressBarImportXLS;
    
    // ImageViews
    @FXML private ImageView imageView;
    @FXML private ImageView productTabImageView;    
    
    // ListViews
    @FXML private ListView<String> headersXLS;
    @FXML private ListView<String> comparedXLSAndDBFields;
    
    // ComboBoxes
    @FXML private ComboBox<String> searchComboBox;    
    @FXML private ComboBox<String> importFieldsComboBox;
    @FXML private ComboBox<String> importKeysComboBox;    
    
    // TextFields
    @FXML private TextField headersRowTextField;
    @FXML private TextField newCategoryTitleTextField;
    @FXML private TextArea newCategoryDescriptionTextArea;
    @FXML private TextArea picDescriptionTextArea;

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
    private static final double ZOOM_DELTA = 1.05;
     
    // Strings
    String selectedProduct;    
    String newCatTitle = "";
    String newCatDescription;
    String selectedDBKey = "Наименование продукта";
    String catalogHeader = "Каталог товаров";
    
    //booleans
    boolean clear = true;

    @FXML  private ScrollPane scroller;
    @FXML private Pagination pagination;
    @FXML private Label currentZoomLabel;
    private ObjectProperty<PDFFile> currentFile;
    private ObjectProperty<ImageView> currentImage;
    private DoubleProperty zoom;
    private PageDimensions currentPageDimensions;
    private ExecutorService imageLoadService;

    @Override
    // Выполняется при запуске программы
    public void initialize(URL url, ResourceBundle rb) { 
        loadSavedSettings();        
        //openPictureButton.setDisable(false);
        //startImportXLSButton.setDisable(true);
        buildCategoryTree();
        populateComboBox ();
        getAllPrices();
        loadImportFields();
        productsTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        productsTable.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = productsTable.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(productsTable.getSelectionModel().getSelectedItem().getTitle());
            /*
            productsTable.getItems().stream().forEach((item) -> {
            content.putString(item.getTitle());
            });
            */
            db.setContent(content);
            System.out.println(content.getString());
            event.consume();
        });   
        categoriesTree.setOnDragOver((DragEvent event) -> {
            final Dragboard db = event.getDragboard();
            if (event.getGestureSource() != categoriesTree && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });        
        categoriesTree.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != categoriesTree && event.getDragboard().hasString()) {
                //TreeItem selTreeItem = categoriesTree.getFocusModel().getFocusedItem();
                //selTreeItem.setGraphic(new ImageView(new Image("/images/greenTreePlus.gif")));
            }
            event.consume();
        });              
        categoriesTree.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                System.out.println("Dropped -> " + db.getString());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });         
        categoriesTree.setOnDragExited((DragEvent event) -> {
            if (event.getGestureSource() != categoriesTree && event.getDragboard().hasString()) {
                //TreeItem selTreeItem = categoriesTree.getFocusModel().getFocusedItem();
                //selTreeItem.setGraphic(new ImageView());
            }
            event.consume();
        });
        //loadImportKeys();
        /*
        imageViewContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Изменить даташит pdf-файл").onAction((ActionEvent arg0) -> {
                    setDatasheetFile();}).build()
        ).build();
        imageView.setContextMenu(imageViewContextMenu);
        */
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<ProductsTableView>() {
                    @Override
                    public void changed(ObservableValue<? extends ProductsTableView> observable, ProductsTableView oldValue, ProductsTableView newValue) {
                        System.out.println(newValue.getTitle());
                    }
                }

        );
        tabPane.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                    if (t1.equals(pdfTab)) {
                        try {
                            createAndConfigureImageLoadService();
                            currentFile = new SimpleObjectProperty<>();
                            currentImage = new SimpleObjectProperty<>();
                            scroller.contentProperty().bind(currentImage);
                            zoom = new SimpleDoubleProperty(1);
                            // To implement zooming, we just get a new image from the PDFFile each time.
                            // This seems to perform well in some basic tests but may need to be improved
                            // E.g. load a larger image and scale in the ImageView, loading a new image only
                            // when required.
                            zoom.addListener(new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                    updateImage(pagination.getCurrentPageIndex());
                                }
                            });
                            currentZoomLabel.textProperty().bind(Bindings.format("%.0f %%", zoom.multiply(100)));
                            bindPaginationToCurrentFile();
                            createPaginationPageFactory();
                            String product = productTabTitle.getText();
                            loadPdfFile(product);
                        } catch (NullPointerException ne) {}
                    }
                }
            }
        );
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
            List response = session.createQuery("From Products where categoryId=" + selectedNodeID).list();
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
                data.add(new DatasheetTableView(f.getName()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }  
        datasheetFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        datasheetTableContextMenu = ContextMenuBuilder.create().items(                
            MenuItemBuilder.create().text("Изменить даташит pdf-файл").onAction((ActionEvent arg0) -> {
                setDatasheetFile();}).build()                
        ).build();
        datasheetFileTable.setContextMenu(datasheetTableContextMenu);
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
        treeViewContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать категорию").onAction((ActionEvent arg0) -> {
                    newCategoryDialog();}).build(),
                MenuItemBuilder.create().text("Редактировать категорию").onAction((ActionEvent arg0) -> {
                    editCategoryDialog();}).build(),
                MenuItemBuilder.create().text("Удалить категорию").onAction((ActionEvent arg0) -> {
                    deleteCategoryDialog();}).build()                
            ).build();        
        categoriesTree.setContextMenu(treeViewContextMenu);
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
        recursiveItems(data, getCategoryIdFromTitle(selectedNode)); 
        getProductList(selectedNode).stream().forEach((product) -> {
            excludeLowerItems(data, selectedNode);
        });
    }
    private void recursiveItems(ObservableList<ProductsTableView> data, Integer selectedNode) {
        ArrayList<Integer> childs = arrayChilds(selectedNode);
        if(!childs.isEmpty()) {
            childs.stream().forEach((ch) -> {
                recursiveItems(data, ch);
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
                    //openPictureButton.setDisable(true);
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
            //openPictureButton.setDisable(false);
            datasheetFileTable.refresh();
        } catch (NullPointerException ex) {}
        
        if (productsTable.getSelectionModel().getSelectedItems().size() <= 1) {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Открыть вкладку обзора свойств устройства").onAction((ActionEvent arg0) -> {
                        openProductTab();}).build(),
                    MenuItemBuilder.create().text("Открыть просмотр PDF-файла").onAction((ActionEvent arg0) -> {
                        createAndConfigureImageLoadService();
                        currentFile = new SimpleObjectProperty<>();
                        currentImage = new SimpleObjectProperty<>();
                        scroller.contentProperty().bind(currentImage);
                        zoom = new SimpleDoubleProperty(1);
                        // To implement zooming, we just get a new image from the PDFFile each time.
                        // This seems to perform well in some basic tests but may need to be improved
                        // E.g. load a larger image and scale in the ImageView, loading a new image only
                        // when required.
                        zoom.addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                updateImage(pagination.getCurrentPageIndex());
                            }
                        });
                        currentZoomLabel.textProperty().bind(Bindings.format("%.0f %%", zoom.multiply(100)));
                        bindPaginationToCurrentFile();
                        createPaginationPageFactory();
                        tabPane.getSelectionModel().select(pdfTab);
                        loadPdfFile(productsTable.getSelectionModel().getSelectedItem().getTitle());}).build(),
                    MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                        changeProductCategoryDialog();}).build(),
                    MenuItemBuilder.create().text("Добавить элемент...").onAction((ActionEvent arg0) -> {
                        addProductDialog();}).build(),                
                    MenuItemBuilder.create().text("Удалить элемент...").onAction((ActionEvent arg0) -> {
                        deleteProductDialog();}).build()                
                ).build();            
        } else {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                        changeProductCategoryDialog();}).build(),               
                    MenuItemBuilder.create().text("Удалить выбранные элементы").onAction((ActionEvent arg0) -> {
                        deleteProductDialog();}).build()                
                ).build();            
        }
        productsTable.setContextMenu(productTableContextMenu);
        fillProductTab();
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
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            ProductImage.open(file, gridPane, imageView);
            ProductImage.open(file, productTabGridPaneImageView, productTabImageView);
            ProductImage.save(file, selectedProduct);
        }
    }
    
    private void setDatasheetFile() {
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
        System.out.println("allCompareDetails.size() = " + allCompareDetails.size());
        allCompareDetails.stream().forEach((d) -> {
            System.out.println(d.get(0) + " - " + d.get(1));
        });
        System.out.println("allImportXLSContent.size() = " + allImportXLSContent.size());
        System.out.println("importFields.size() = " + importFields.size());        
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
    
    private void newCategoryDialog() {
        Dialog<NewCategory> dialog = new Dialog<>();
        dialog.setTitle("Создание новой категории");
        dialog.setHeaderText("Введите название новой категории. Она будет размещена внутри выбранной категории.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Описание (необязательно):  ");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonData.CANCEL_CLOSE);        
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {                
                return new NewCategory(text1.getText(), text2.getText());
            }            
            return null;
        });
        Optional<NewCategory> result = dialog.showAndWait();
        if (result.isPresent()) {
            newCatTitle = result.get().getTitle();
            newCatDescription = result.get().getDescription();
            createNewCategory();
            buildCategoryTree();
        }
    }
    private void editCategoryDialog() {
        ArrayList<String> details = getCategoryDetails(categoriesTree.getSelectionModel().getSelectedItem().getValue());
        Dialog<NewCategory> dialog = new Dialog<>();
        dialog.setTitle("Редактирование категории");
        dialog.setHeaderText("Здесь Вы можете отредактировать название и/или описание выбранной категории.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Описание (необязательно):  ");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();
        text1.setText(details.get(0));
        text2.setText(details.get(1));

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonData.CANCEL_CLOSE);        
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {                
                return new NewCategory(text1.getText(), text2.getText());
            }            
            return null;
        });
        Optional<NewCategory> result = dialog.showAndWait();
        if (result.isPresent()) {
            newCatTitle = result.get().getTitle();
            newCatDescription = result.get().getDescription();
            editCategory(categoriesTree.getSelectionModel().getSelectedItem().getValue(), newCatTitle, newCatDescription);
            buildCategoryTree();
        }        
    }
    private void deleteCategoryDialog() {
        String catTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Внимание!");
        alert.setTitle("Удаление категории");
        String s = "Категория \"" + catTitle + "\" будет удалена из каталога. Все элементы, принадлежащие этой категории будут перенесены в ближайшшую вышестоящую категорию.";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            Integer parentCatId = getParentCatId(catTitle);
            Integer catId = getCategoryIdFromTitle (catTitle);
            replaceProductsUp(catId, parentCatId);            
            deleteCategory(catTitle);
            buildCategoryTree();
            ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
            productsTable.setItems(data);
        }    
    }    
    private void createNewCategory() {
        String parentCategoryTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
        Integer parentId = getCategoryIdFromTitle (parentCategoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Categories categorie = new Categories();
        categorie.setTitle(newCatTitle);
        categorie.setDescription(newCatDescription);
        categorie.setParent(parentId);
        session.save(categorie);
        tx.commit();
        session.close();
    }
    
    private void editCategory(String categoryTitle, String newTitle, String NewDescription) {
        Integer id = getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Categories set title = :title, description = :description where id = :id");
        query.setParameter("title", newTitle);
        query.setParameter("description", NewDescription);
        query.setParameter("id", id);
        query.executeUpdate();
        tx.commit();
        session.close();
    }  
    
    private void deleteCategory(String categoryTitle) {
        Integer id = getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("delete Categories where id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
        tx.commit();
        session.close();
    }
    
    private ArrayList<String> getCategoryDetails(String categoryTitle)  {
        ArrayList<String> details = new ArrayList<>();
        Integer id = getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Categories where id=" + id).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Categories categorie = (Categories) iterator.next();
                details.add(categorie.getTitle());
                details.add(categorie.getDescription());
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        } 
        return details;
    } 
    
    private Integer getParentCatId(String categoryTitle) {
        Integer id = getCategoryIdFromTitle(categoryTitle); 
        Integer parentId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();        
        try {
            List response = session.createQuery("From Categories where id=" + id).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Categories categorie = (Categories) iterator.next();
                parentId = categorie.getParent();
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        return parentId;
    }
    
    private void replaceProductsUp(Integer catId, Integer parentCatId) {
        ArrayList<Products> productsUp = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();        
        try {
            List response = session.createQuery("From Products where categoryId=" + catId).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products product = (Products) iterator.next();
                productsUp.add(product);
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Transaction tx1 = session1.beginTransaction();
        productsUp.stream().forEach((product) -> {
            Query query1 = session1.createQuery("update Products set categoryId = :category_id where id = :id");
            query1.setParameter("category_id", parentCatId);
            query1.setParameter("id", product.getId());
            query1.executeUpdate();
            tx1.commit();        
        });
        session1.close();        
    }
    
    private void openProductTab() {
        tabPane.getSelectionModel().select(productTab);
        fillProductTab();
    }
    
    private void changeProductCategoryDialog() {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        //tabPane.getSelectionModel().select(productTab);  
    } 
    
    private void addProductDialog() {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        //tabPane.getSelectionModel().select(productTab);  
    }   
    
    private void deleteProductDialog() {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        //tabPane.getSelectionModel().select(productTab);  
    } 
    
    private void fillProductTab() {
        ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();        
        productTabTitle.setText(product.getTitle());
        Session session = HibernateUtil.getSessionFactory().openSession();
        List pics = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(product.getTitle()) + " and fileTypeId=1").list();
        for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
            Files pic = (Files) iterator.next();
            File picFile = new File(pic.getPath());        
            ProductImage.open(picFile, productTabGridPaneImageView, productTabImageView);
            picDescriptionTextArea.setText(pic.getDescription());
        }
        session.close();
    }

    @FXML private void changePicDescription() {
        String product = productTabTitle.getText();
        String picDescription = picDescriptionTextArea.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Files set description = :description" + " where ownerId =" + getProductIdFromTitle(product));
        query.setParameter("description", picDescription);
        int result = query.executeUpdate();
        tx.commit();
        session.close();
    }

    // Дальше позаимствовал реализацию PDF Reader из тырнета)))

    private void createAndConfigureImageLoadService() {
        imageLoadService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    private void bindPaginationToCurrentFile() {
        currentFile.addListener(new ChangeListener<PDFFile>() {
            @Override
            public void changed(ObservableValue<? extends PDFFile> observable, PDFFile oldFile, PDFFile newFile) {
                if (newFile != null) {
                    pagination.setCurrentPageIndex(0);
                }
            }
        });
        pagination.pageCountProperty().bind(new IntegerBinding() {
            {
                super.bind(currentFile);
            }
            @Override
            protected int computeValue() {
                return currentFile.get()==null ? 0 : currentFile.get().getNumPages() ;
            }
        });
        pagination.disableProperty().bind(Bindings.isNull(currentFile));
    }

    private void createPaginationPageFactory() {
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageNumber) {
                if (currentFile.get() == null) {
                    return null ;
                } else {
                    if (pageNumber >= currentFile.get().getNumPages() || pageNumber < 0) {
                        return null ;
                    } else {
                        updateImage(pageNumber);
                        return scroller ;
                    }
                }
            }
        });
    }

    // ************** Event Handlers ****************

    private void loadPdfFile(String product) {
        String pdfFilePath = new String();
        //String product = productTabTitle.getText();
        pdfTabTitle.setText(product);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List pdfs = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(product) + " and fileTypeId=2").list();
        for (Iterator iterator = pdfs.iterator(); iterator.hasNext();) {
            Files pdf = (Files) iterator.next();
            pdfFilePath = pdf.getPath();
        }
        File file = new File(pdfFilePath);
        if (file != null) {
            final Task<PDFFile> loadFileTask = new Task<PDFFile>() {
                @Override
                protected PDFFile call() throws Exception {
                    try (
                            RandomAccessFile raf = new RandomAccessFile(file, "r");
                            FileChannel channel = raf.getChannel()
                    ) {
                        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                        return new PDFFile(buffer);
                    }
                }
            };
            loadFileTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    pagination.getScene().getRoot().setDisable(false);
                    final PDFFile pdfFile = loadFileTask.getValue();
                    currentFile.set(pdfFile);
                }
            });
            loadFileTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    pagination.getScene().getRoot().setDisable(false);
                    showErrorMessage("Could not load file "+file.getName(), loadFileTask.getException());
                }
            });
            pagination.getScene().getRoot().setDisable(true);
            imageLoadService.submit(loadFileTask);
        }
    }

    @FXML private void zoomIn() {
        zoom.set(zoom.get()*ZOOM_DELTA);
    }

    @FXML private void zoomOut() {
        zoom.set(zoom.get()/ZOOM_DELTA);
    }

    @FXML private void zoomFit() {
        // TODO: the -20 is a kludge to account for the width of the scrollbars, if showing.
        double horizZoom = (scroller.getWidth()-20) / currentPageDimensions.width ;
        double verticalZoom = (scroller.getHeight()-20) / currentPageDimensions.height ;
        zoom.set(Math.min(horizZoom, verticalZoom));
    }

    @FXML private void zoomWidth() {
        zoom.set((scroller.getWidth()-20) / currentPageDimensions.width) ;
    }

    // *************** Background image loading ****************

    private void updateImage(final int pageNumber) {
        final Task<ImageView> updateImageTask = new Task<ImageView>() {
            @Override
            protected ImageView call() throws Exception {
                PDFPage page = currentFile.get().getPage(pageNumber+1);
                Rectangle2D bbox = page.getBBox();
                final double actualPageWidth = bbox.getWidth();
                final double actualPageHeight = bbox.getHeight();
                // record page dimensions for zoomToFit and zoomToWidth:
                currentPageDimensions = new PageDimensions(actualPageWidth, actualPageHeight);
                // width and height of image:
                final int width = (int) (actualPageWidth * zoom.get());
                final int height = (int) (actualPageHeight * zoom.get());
                // retrieve image for page:
                // width, height, clip, imageObserver, paintBackground, waitUntilLoaded:
                java.awt.Image awtImage = page.getImage(width, height, bbox, null, true, true);
                // draw image to buffered image:
                BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                buffImage.createGraphics().drawImage(awtImage, 0, 0, null);
                // convert to JavaFX image:
                Image image = SwingFXUtils.toFXImage(buffImage, null);
                // wrap in image view and return:
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                return imageView ;
            }
        };

        updateImageTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                pagination.getScene().getRoot().setDisable(false);
                currentImage.set(updateImageTask.getValue());
            }
        });

        updateImageTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                pagination.getScene().getRoot().setDisable(false);
                updateImageTask.getException().printStackTrace();
            }

        });

        pagination.getScene().getRoot().setDisable(true);
        imageLoadService.submit(updateImageTask);
    }

    private void showErrorMessage(String message, Throwable exception) {

        // TODO: move to fxml (or better, use ControlsFX)

        final Stage dialog = new Stage();
        dialog.initOwner(pagination.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        final VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        StringWriter errorMessage = new StringWriter();
        exception.printStackTrace(new PrintWriter(errorMessage));
        final Label detailsLabel = new Label(errorMessage.toString());
        TitledPane details = new TitledPane();
        details.setText("Details:");
        Label briefMessageLabel = new Label(message);
        final HBox detailsLabelHolder =new HBox();

        Button closeButton = new Button("OK");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.hide();
            }
        });
        HBox closeButtonHolder = new HBox();
        closeButtonHolder.getChildren().add(closeButton);
        closeButtonHolder.setAlignment(Pos.CENTER);
        closeButtonHolder.setPadding(new Insets(5));
        root.getChildren().addAll(briefMessageLabel, details, detailsLabelHolder, closeButtonHolder);
        details.setExpanded(false);
        details.setAnimated(false);

        details.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    detailsLabelHolder.getChildren().add(detailsLabel);
                } else {
                    detailsLabelHolder.getChildren().remove(detailsLabel);
                }
                dialog.sizeToScene();
            }

        });
        final Scene scene = new Scene(root);

        dialog.setScene(scene);
        dialog.show();
    }


	/*
	 * Struct-like class intended to represent the physical dimensions of a page in pixels
	 * (as opposed to the dimensions of the (possibly zoomed) view.
	 * Used to compute zoom factors for zoomToFit and zoomToWidth.
	 *
	 */

    private class PageDimensions {
        private double width ;
        private double height ;
        PageDimensions(double width, double height) {
            this.width = width ;
            this.height = height ;
        }
        @Override
        public String toString() {
            return String.format("[%.1f, %.1f]", width, height);
        }
    }
}