package main;

import javafx.beans.property.*;
import javafx.scene.web.HTMLEditor;
import javafx.util.StringConverter;
import modalwindows.SetRatesWindow;
import settings.LocalDBSettings;
import settings.PriceCalcSettings;
import settings.SiteDBSettings;
import settings.SystemConfig;
import utils.DBConnection;
import entities.*;
import entities.Properties;
import tableviews.*;
import treeviews.CategoriesTreeView;
import treeviews.PropertiesTreeView;
import treetableviews.PropertiesTreeTableView;
import utils.*;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.*;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.net.URL;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Klekotnev
 */

public class PCGUIController implements Initializable {

    // Panes
    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane editorAnchorPane;
    @FXML private StackPane  stackPane;
    @FXML private StackPane  propertiesStackPane;
    @FXML private GridPane   gridPane;
    @FXML private GridPane   gridPanePDF;
    @FXML private GridPane productTabGridPaneImageView;
    @FXML private GridPane functionGridPaneImageView;
    StackPane stackPaneModal = new StackPane();

    @FXML private HTMLEditor htmlEditor;

    // TreeViews
    @FXML private TreeView<String> categoriesTree;
    @FXML private TreeView<String> propertiesTree;
    TreeView<String> treeView;

    //Tabs
    @FXML TabPane tabPane;
    @FXML Tab mainTab;
    @FXML Tab productTab;
    @FXML Tab pdfTab;
    @FXML Tab settingsTab;
    @FXML Tab editorTab;

    // ContextMenus
    @ FXML private ContextMenu treeViewContextMenu;
    @ FXML private ContextMenu productTableContextMenu;
    @ FXML private ContextMenu datasheetTableContextMenu;
    @ FXML private ContextMenu vendorsTableContextMenu;
    @ FXML private ContextMenu propertiesTableContextMenu;
    @ FXML private ContextMenu productKindsListContextMenu;
    @ FXML private ContextMenu propertiesTreeTableContextMenu;
    @ FXML private ContextMenu functionsTableContextMenu;
    @ FXML private ContextMenu functionsTable1ContextMenu;
    @ FXML private ContextMenu accessoriesTableContextMenu;
    @ FXML private ContextMenu propertiesTreeContextMenu;
    @ FXML private ContextMenu newsItemsListContextMenu;
    @ FXML private ContextMenu articlesListContextMenu;
    @ FXML private ContextMenu contentsListContextMenu;

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

    @FXML private TableView<VendorsTableView>             vendorsTable;
    @FXML private TableColumn<VendorsTableView, String>   vendorsTitleColumn;
    @FXML private TableColumn<VendorsTableView, String>   vendorsAddressColumn;
    @FXML private TableColumn<VendorsTableView, Double>   vendorsRateColumn;

    @FXML private TreeTableView<PropertiesTreeTableView>          propertiesTable;
    @FXML private TreeTableColumn<PropertiesTreeTableView, String>propertyTitleColumn;
    @FXML private TreeTableColumn<PropertiesTreeTableView, String>propertyValueColumn;
    @FXML private TreeTableColumn<PropertiesTreeTableView, String>propertyMeasureColumn;
    @FXML private TreeTableColumn<PropertiesTreeTableView, String>propertyConditionColumn;

    @FXML private TableView<FunctionsTableView>           functionsTable;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableTitleColumn;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableSymbolColumn;

    @FXML private TableView<FunctionsTableView>           functionsTable1;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableTitleColumn1;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableSymbolColumn1;

    @FXML private TableView<AccessoriesTableView> accessoriesTable;
    @FXML private TableColumn<AccessoriesTableView, String> accessoriesTableTitleColumn;
    @FXML private TableColumn<AccessoriesTableView, String> accessoriesTableDescriptionColumn;

    //TreeTableViews
    @FXML private TreeTableView<PropertiesTreeTableView>  propertiesTreeTable;
    @FXML private TreeTableColumn<PropertiesTreeTableView, String> propertiesTreeTableTitleColumn;

    //WebView
    @FXML private WebView tabBrowserWebView;

    // Buttons
    @FXML private Button startImportXLSButton;
    @FXML private Button loadSavedSettingsButton;

    // Labels
    @FXML private Label productTabTitle;
    @FXML private Label pdfTabTitle;
    @FXML private Label courseEUROLabel;
    @FXML private Label courseDateLabel;
    @FXML private Label addCBRLabel;
    @FXML private Label productTabKind;

    // ProgressBars
    @FXML private ProgressBar progressBar;
    @FXML private ProgressBar progressBarImportXLS;

    // ImageViews
    @FXML private ImageView imageView;
    @FXML private ImageView productTabImageView;
    @FXML private ImageView functionImageView;

    // ListViews
    @FXML private ListView<String> headersXLS;
    @FXML private ListView<String> comparedXLSAndDBFields;
    @FXML private ListView<String> productKindsList;
    @FXML private ListView<String> newsListView;
    @FXML private ListView<String> articlesListView;
    @FXML private ListView<String> contentsListView;



    // ComboBoxes
    @FXML private ComboBox<String> searchComboBox;
    @FXML private ComboBox<String> importFieldsComboBox;

    // TextFields
    @FXML private TextField headersRowTextField;
    @FXML private TextArea picDescriptionTextArea;
    @FXML private TextArea functionDescriptionTextArea;

    @FXML private TextField addressSiteDB;
    @FXML private TextField portSiteDB;
    @FXML private TextField titleSiteDB;
    @FXML private TextField userSiteDB;
    @FXML private PasswordField passwordSiteDB;

    @FXML private TextField addressLocalDB;
    @FXML private TextField portLocalDB;
    @FXML private TextField titleLocalDB;
    @FXML private TextField userLocalDB;
    @FXML private PasswordField passwordLocalDB;

    @FXML private TextField addCBRTextField;

    // CheckBoxes
    @FXML private CheckBox treeViewHandlerMode;

    //Files & FileChoosers
    File fileXLS;
    final   FileChooser fileChooser = new FileChooser();

    // Lists
    ArrayList<ArrayList<String>> allImportXLSContent = new ArrayList<>();
    ArrayList<ArrayList<String>> allCompareDetails = new ArrayList<>();
    ObservableList<CategoriesTreeView> subCategoriesTreeViewList;
    ObservableList<PropertiesTreeView> subPropertiesTreeViewList;
    ObservableList<String> allProducts = FXCollections.observableArrayList();
    ObservableList<ImportFields> importFields = FXCollections.observableArrayList();
    ObservableList<String> comparedPairs = FXCollections.observableArrayList();
    // ObservableList<PropertiesTableView> data;

    // Maps
    Map<String, Double> allPrices = new HashMap<>();

    // Numbers
    private Integer headersRowNumber = 0;
    public Double course;
    Double basePrice;
    private static final double ZOOM_DELTA = 1.05;
    Double newVendorRate;
    Double addCBR;

    // Strings
    String selectedProduct;
    String newCatTitle = "";
    String newCatDescription;
    String selectedDBKey = "Наименование продукта";
    String catalogHeader = "Каталог товаров";
    String newVendorTitle = "";
    String newVendorDescription;
    String newVendorCurrency;
    String newVendorAddress;
    String selectedPropertyType;

    private final String noImageFile = "C:\\Users\\gnato\\Desktop\\Igor\\progs\\java_progs\\PoligonCommanderJ\\src\\main\\resources\\images\\noImage.gif";
    String selectedCategory = "";
    String focusedProduct = "";
    TreeItem<String> productOwner = new TreeItem<>();

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
    ProductKinds selectedProductKind = new ProductKinds();

    @Override
    // Выполняется при запуске программы
    public void initialize(URL url, ResourceBundle rb) {

        SystemConfig.getSettingsDialog();

        loadSavedSettings();
        populateContentSiteLists();

        try {
            addCBR = Double.parseDouble(addCBRTextField.getText());
            CurrencyCourse euro = new CurrencyCourse("EUR");
            course = (Double) euro.getValueFromCBR().get(0);
            courseEUROLabel.setText(course.toString());
            courseDateLabel.setText(((String) euro.getValueFromCBR().get(1)).replace('/', '.'));
            addCBRTextField.setText(addCBR.toString());
            course = course + ((course / 100) * addCBR);
        } catch (IOException ioe) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Внимание!");
            alert.setHeaderText("Нет связи с сервером \"CBR\"!");
            alert.setContentText("Актуальные данные курсов валют, а следовательно,\n" +
                    "все цены могут быть неверны!");
            alert.showAndWait();
        }
        buildCategoryTree();
        populateComboBox ();
        getAllPrices();
        loadImportFields();
        buildVendorsTable();
        buildProductKindsList();
        WebEngine webEngine = tabBrowserWebView.getEngine();
        webEngine.load("http://localhost:3000");
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
        productsTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<ProductsTableView>() {
                    @Override
                    public void changed(ObservableValue<? extends ProductsTableView> observable, ProductsTableView oldValue, ProductsTableView newValue) {
                        try {
                            selectedProduct = newValue.getTitle();
                            buildPricesTable(selectedProduct);
                            buildQuantityTable(selectedProduct);
                            buildDeliveryTimeTable(selectedProduct);
                            buildAnalogsTable(selectedProduct);
                            buildDatasheetFileTable(selectedProduct);
                            buildImageView(selectedProduct);
                            datasheetFileTable.refresh();
                        } catch (NullPointerException ex) {}
                        productsTable.setContextMenu(productTableContextMenu);
                        fillProductTab(selectedProduct);
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
                        } else if (t1.equals(mainTab)) {
                            try {
                                fillMainTab(productTabTitle.getText());
                            } catch (NullPointerException ne) {}
                        } else if (t1.equals(editorTab)) {
                            ExtendHtmlEditor.addPictureFunction(htmlEditor, editorAnchorPane);
                        }
                    }
                }
        );
    }

    // Загружает в память настройки программы, сохранённые в БД
    // Пока не реализовано полностью.
    @FXML private void loadSavedSettings() {
        SiteDBSettings siteDBSettings = new SiteDBSettings();
        LocalDBSettings localDBSettings = new LocalDBSettings();
        PriceCalcSettings priceCalcSettings = new PriceCalcSettings();
        addCBRTextField.setText(priceCalcSettings.loadSetting("addCBR"));

        addressSiteDB.setText(siteDBSettings.loadSetting("addressSiteDB"));
        portSiteDB.setText(siteDBSettings.loadSetting("portSiteDB"));
        titleSiteDB.setText(siteDBSettings.loadSetting("titleSiteDB"));
        userSiteDB.setText(siteDBSettings.loadSetting("userSiteDB"));
        passwordSiteDB.setText(siteDBSettings.loadSetting("passwordSiteDB"));

        addressLocalDB.setText(localDBSettings.loadSetting("addressLocalDB"));
        portLocalDB.setText(localDBSettings.loadSetting("portLocalDB"));
        titleLocalDB.setText(localDBSettings.loadSetting("titleLocalDB"));
        userLocalDB.setText(localDBSettings.loadSetting("userLocalDB"));
        passwordLocalDB.setText(localDBSettings.loadSetting("passwordLocalDB"));
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
    private Integer getPropertyTypeIdFromTitle (String title) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List ids = session.createSQLQuery("select id from property_types where title=\"" + title + "\"").list();
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
    private Integer getPropertyKindIdFromTitle(String selectedPropertiesKind) {
        Integer id = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds where title=\'" + selectedPropertiesKind + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ProductKinds kindId = (ProductKinds) iterator.next();
            id = kindId.getId();
        }
        session.close();
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
    @FXML private void saveAddCBRToDB() {
        PriceCalcSettings priceCalcSettings = new PriceCalcSettings();
        priceCalcSettings.saveSetting("addCBR", addCBRTextField.getText());

        loadSavedSettings();

        try {
            addCBR = Double.parseDouble(addCBRTextField.getText());
            CurrencyCourse euro = new CurrencyCourse("EUR");
            course = (Double) euro.getValueFromCBR().get(0);
            courseEUROLabel.setText(course.toString());
            courseDateLabel.setText(((String) euro.getValueFromCBR().get(1)).replace('/', '.'));
            addCBRTextField.setText(addCBR.toString());
            course = course + ((course / 100) * addCBR);
            getAllPrices();
            buildPricesTable(selectedProduct);
        } catch (IOException ioe) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Внимание!");
            alert.setHeaderText("Нет связи с сервером \"CBR\"!");
            alert.setContentText("Актуальные данные курсов валют, а следовательно,\n" +
                    "все цены могут быть неверны!");
            alert.showAndWait();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Номенклатура" //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
            List response = session.createQuery("From Products where categoryId=" + getCategoryIdFromTitle(selectedNode)).list();
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
        productArticle.setCellFactory(TextFieldTableCell.forTableColumn());
        productArticle.setOnEditCommit(
                new EventHandler<CellEditEvent<ProductsTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<ProductsTableView, String> t) {
                        ((ProductsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setArticle(t.getNewValue());
                        setNewCellValue("article", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        productTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        productTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        productTitle.setOnEditCommit(
                new EventHandler<CellEditEvent<ProductsTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<ProductsTableView, String> t) {
                        ((ProductsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTitle(t.getNewValue());
                        setNewCellValue("title", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        productDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        productDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        productDescription.setOnEditCommit(
                new EventHandler<CellEditEvent<ProductsTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<ProductsTableView, String> t) {
                        ((ProductsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDescription(t.getNewValue());
                        setNewCellValue("description", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
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
        SetRates ratesPack = SetRates.getRatesPack(selectedProduct);
        allPrices.entrySet().stream().filter((entry) -> (entry.getKey().equals(selectedProduct))).map((entry) -> {
            return entry;
        }).forEach((entry) -> {
            basePrice = entry.getValue();
        });
        ObservableList<PricesTableView> data = FXCollections.observableArrayList();
        String[] priceTypes = {
                "Розничная цена",
                "Мелкий опт (+10)",
                "Оптовая цена",
                "Диллерская цена",
                "Закупочная цена"
        };

        for (String type : priceTypes) {
            try {
                Double retailPrice = basePrice * ratesPack.getRate();
                Double tenPlusDiscount = retailPrice - (retailPrice*ratesPack.getTenPlusDiscount()/100.0);
                Double optDiscount = retailPrice - (retailPrice*ratesPack.getOptDiscount()/100.0);
                Double dealerDiscount = retailPrice - (retailPrice*ratesPack.getDealerDiscount()/100.0);
                switch (type) {
                    case "Розничная цена":
                        data.add(new PricesTableView(type, ((new BigDecimal(retailPrice)).setScale(2, RoundingMode.UP)).doubleValue(),
                                ((new BigDecimal(retailPrice * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                        break;
                    case "Мелкий опт (+10)":
                        data.add(new PricesTableView(type, ((new BigDecimal(tenPlusDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                ((new BigDecimal(tenPlusDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                        break;
                    case "Оптовая цена":
                        data.add(new PricesTableView(type, ((new BigDecimal(optDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                ((new BigDecimal(optDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                        break;
                    case "Диллерская цена":
                        data.add(new PricesTableView(type, ((new BigDecimal(dealerDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                ((new BigDecimal(dealerDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                        break;
                    case "Закупочная цена":
                        data.add(new PricesTableView(type, ((new BigDecimal(basePrice)).setScale(2, RoundingMode.UP)).doubleValue(),
                                ((new BigDecimal(basePrice * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException nex) {}
        }
        priceType.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceValue.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceValue.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.toString();
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        }));
        priceValue.setOnEditCommit(
                new EventHandler<CellEditEvent<PricesTableView, Double>>() {
                    @Override
                    public void handle(CellEditEvent<PricesTableView, Double> t) {
                        if (t.getRowValue().getType().equals("Закупочная цена")) {
                            ((PricesTableView) t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())
                            ).setPrice(t.getNewValue());
                            setNewPriceValue("price", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                            getAllPrices();
                            buildPricesTable(selectedProduct);
                        } else {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle("Внимание!");
                            alert.setHeaderText("Недопустимое действие!");
                            alert.setContentText("В таблице цен можно устанавливать только закупочную валютную цену.\n" +
                                    "Все остальные цены являются расчётными и редактированию не подлежат.");
                            alert.showAndWait();
                            buildPricesTable(selectedProduct);
                        }

                    }
                }
        );
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
            List response = session.createQuery("From Files where ownerId=" + getProductIdFromTitle(selectedProduct) + " and fileTypeId=" + 2).list();
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
    private void buildVendorsTable() {
        ObservableList<VendorsTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Vendors").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Vendors v = (Vendors) iterator.next();
                data.add(new VendorsTableView(v.getTitle(), v.getAddress(), v.getRate()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        vendorsTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        vendorsAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        vendorsRateColumn.setCellValueFactory(new PropertyValueFactory<>("rate"));
        vendorsTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить нового производителя").onAction((ActionEvent arg0) -> {
                    addVendorDialog();}).build()
        ).build();
        vendorsTable.setContextMenu(vendorsTableContextMenu);
        vendorsTable.setItems(data);
    }
    private void buildImageView(String selectedProduct) {
        if (selectedProduct == null) {
            selectedProduct = focusedProduct;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        List pics = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(selectedProduct) + " and fileTypeId=" + 1).list();
        if (pics.size()==0) {
            ProductImage.open(new File(noImageFile), gridPane, imageView);
        } else {
            for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                Files pic = (Files) iterator.next();
                File picFile = new File(pic.getPath());
                ProductImage.open(picFile, gridPane, imageView);
            }
        }
        session.close();
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
        categoriesTree.setShowRoot(false);
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleCategoryTreeMouseClicked(event);
        };
        categoriesTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
        treeViewContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Установить коэффициенты цен").onAction((ActionEvent arg0) -> {
                    String selectedNode = (String)((TreeItem)categoriesTree.getSelectionModel().getSelectedItem()).getValue();
                    Integer selectedNodeID = getCategoryIdFromTitle(selectedNode);
                    SetRatesWindow ratesWindow = new SetRatesWindow(selectedNodeID, null);
                    ratesWindow.showModalWindow();
                }).build(),
                MenuItemBuilder.create().text("Создать категорию").onAction((ActionEvent arg0) -> {
                    newCategoryDialog("main");
                }).build(),
                MenuItemBuilder.create().text("Редактировать категорию").onAction((ActionEvent arg0) -> {
                    editCategoryDialog("main");
                }).build(),
                MenuItemBuilder.create().text("Удалить категорию").onAction((ActionEvent arg0) -> {
                    deleteCategoryDialog("main");
                }).build()
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
    ////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////
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
                        includeLowerItems(data, selectedNode);
                    } else {
                        excludeLowerItems(data, selectedNode);
                    }
                    buildProductsTable(data);
                    productsTable.getSelectionModel().select(0);
                    setVendorSelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
                    focusedProduct = productsTable.getSelectionModel().getSelectedItem().getTitle();
                    onFocusedProductTableItem();
                }
                Platform.runLater(() -> {
                    progressBar.progressProperty().unbind();
                    progressBar.setProgress(0.0);
                });
                return null;
            }
        };
    }
    private void buildPropertiesTree(String selectedProduct) {
        propertiesStackPane.getChildren().clear();
        //Получаем вид продукта для товара, преданного в параметре
        ProductKinds kind = new ProductKinds();
        ArrayList<KindsTypes> types = new ArrayList<>();
        ObservableList<PropertiesTreeView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List list = session.createQuery("From Products where id =" + getProductIdFromTitle(selectedProduct)).list();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            kind = product.getProductKindId();
        }
        session.close();
        //Получаем типы характеристик для этого вида товара
        Session session0 = HibernateUtil.getSessionFactory().openSession();
        List list0 = session0.createQuery("From KindsTypes where productKindId =" + kind.getId()).list();
        for (Iterator iterator0 = list0.iterator(); iterator0.hasNext();) {
            KindsTypes kindsType = (KindsTypes) iterator0.next();
            types.add(kindsType);
        }
        session0.close();
        types.stream().forEach((type) -> {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            List list1 = session1.createQuery("From PropertyTypes where id=" + type.getPropertyTypeId().getId()).list();
            for (Iterator iterator1 = list1.iterator(); iterator1.hasNext();) {
                PropertyTypes propertyType = (PropertyTypes) iterator1.next();
                data.add(new PropertiesTreeView(propertyType.getId(), propertyType.getTitle(), propertyType.getParent()));
            }
            session1.close();
        });
        ArrayList<PropertiesTreeView> properties = new ArrayList();
        data.stream().forEach((section) -> {
            properties.add(section);
        });
        PropertiesTreeView treeRoot = new PropertiesTreeView(0, "Все характекистики", 0);
        TreeItem<String> rootItem = new TreeItem<> (treeRoot.getTitle());
        rootItem.setExpanded(true);
        buildPropertiesTreeNode(properties, rootItem, treeRoot);
        propertiesTree = new TreeView<> (rootItem);
        rootItem.setExpanded(true);
        propertiesTree.setShowRoot(false);
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handlePropertiesTreeMouseClicked(event);
        };
        propertiesTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
        propertiesStackPane.getChildren().add(propertiesTree);
    }
    private void buildPropertiesTreeNode (ArrayList<PropertiesTreeView> properties, TreeItem<String> rootItem, PropertiesTreeView treeRoot) {
        properties.stream().filter((property) -> (property.getParent().equals(treeRoot.getId()))).forEach((PropertiesTreeView property) -> {
            TreeItem<String> treeItem = new TreeItem<> (property.getTitle());
            rootItem.getChildren().add(treeItem);
            buildPropertiesTreeNode(properties, treeItem, property);
        });
    }

    // Создаёт модальное окно с деревом категорий товаров для диалога переноса товаров в другую категорию.
    private void buildModalCategoryTree(StackPane stackPane) {
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

        treeView = new TreeView(rootItem);
        ContextMenu treeViewContextMenu1 = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать категорию").onAction((ActionEvent arg0) -> {
                    newCategoryDialog("modal");
                }).build(),
                MenuItemBuilder.create().text("Редактировать категорию").onAction((ActionEvent arg0) -> {
                    editCategoryDialog("modal");
                }).build(),
                MenuItemBuilder.create().text("Удалить категорию").onAction((ActionEvent arg0) -> {
                    deleteCategoryDialog("modal");
                }).build()
        ).build();
        treeView.setContextMenu(treeViewContextMenu1);
        stackPane.getChildren().add(treeView);
        treeView.setPrefWidth(350.0);
        treeView.setPrefHeight(400.0);
    }

    // Вызывает диалог переноса товавра или нескольких товаров в новую категорию.
    private void changeProductCategoryDialog() {
        ObservableList<ProductsTableView> selectedItems = productsTable.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        DialogPane dialog = new DialogPane();
        alert.setDialogPane(dialog);

        alert.setTitle("Переместить выбранные элементы");
        alert.setHeaderText("Укажите категорию, в которую следует \nпереместить выбранные элементы:");
        alert.setResizable(false);

        AnchorPane anchorPane = new AnchorPane();

        anchorPane.getChildren().add(stackPaneModal);
        stackPaneModal.setAlignment(Pos.CENTER);
        buildModalCategoryTree(stackPaneModal);

        dialog.setContent(anchorPane);

        ButtonType buttonTypeOk = new ButtonType("Переместить", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().add(buttonTypeOk);
        dialog.getButtonTypes().add(buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == buttonTypeOk)) {
            selectedCategory = treeView.getSelectionModel().getSelectedItem().getValue();
            for(ProductsTableView product: selectedItems) {
                updateCategoryId(selectedCategory, product.getTitle());
            }
        }
        refreshProductsTable();
    }

    // Вспомогательные методы, для смены id категории, к которой принадлежит товар.
    // Две версии для разных вхзодных типов данных
    // Позже можно будет решить более красиво, но пока так...
    private void updateCategoryId(String value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            Categories cat = new Categories();
            Session sess = HibernateUtil.getSessionFactory().openSession();
            List list = sess.createQuery("From Categories").list();
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                if (category.getTitle().equals(value)) {
                    cat = category;
                }
            }
            sess.close();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                try {
                    if (cat.getTitle().equals(value)) {
                        product.setCategoryId(cat);
                    }
                } catch (NullPointerException ne) {}
                session.save(product);
            }
            session.getTransaction().commit();
            session.close();
        }
    }
    private void updateCategoryId(Integer value, String productTitle) {
        if (!(value.equals("") || value.equals(null))) {
            Categories cat = new Categories();
            Session sess = HibernateUtil.getSessionFactory().openSession();
            List list = sess.createQuery("From Categories").list();
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                if (category.getId().equals(value)) {
                    cat = category;
                }
            }
            sess.close();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            if (!(id == 0)) {
                Products product = (Products) session.get(Products.class, id);
                try {
                    if (cat.getId().equals(value)) {
                        product.setCategoryId(cat);
                    }
                } catch (NullPointerException ne) {}
                session.save(product);
            }
            session.getTransaction().commit();
            session.close();
        }
    }

    // Вносит в БД новые значения, полученные при редактировании таблицы товаров.
    private void setNewCellValue(String fieldName, String newValue, String productTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("UPDATE Products set " + fieldName + "= :newValue where title= :title");
        query.setParameter("newValue", newValue);
        query.setParameter("title", productTitle);
        query.executeUpdate();
        tx.commit();
        session.close();
    }

    private void setNewPriceValue(String fieldName, Double newValue, String productTitle) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("UPDATE Products set " + fieldName + "= :newValue where title= :title");
        query.setParameter("newValue", newValue);
        query.setParameter("title", productTitle);
        query.executeUpdate();
        tx.commit();
        session.close();
    }

    // Перерисовывает таблицу товаров в зависимости от выбранного пункта в дереве категорий.
    private void refreshProductsTable() {
        try {
            Integer selectedTreeId = getCategoryIdFromTitle(categoriesTree.getSelectionModel().getSelectedItem().getValue());
            buildProductsTable(getProductList(selectedTreeId));
        } catch (NullPointerException ne) {
            Integer selectedTreeId = getCategoryIdFromTitle(treeView.getSelectionModel().getSelectedItem().getValue());
            buildProductsTable(getProductList(selectedTreeId));
        }
    }

    // Вызывает диалог добавления нового товара из контектстного меню таблицы товаров.
    // Пока не реализован.
    private void addProductDialog() {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        //tabPane.getSelectionModel().select(productTab);
    }

    // Вызывает диалог удаления выбранного товара (или нескольких товаров) из контектстного меню таблицы товаров.
    // Пока не реализован.
    private void deleteProductDialog() {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        //tabPane.getSelectionModel().select(productTab);
    }

    // Переводит программу на отображение вкладки со свойствами выбранного товара.
    private void openProductTab() {
        tabPane.getSelectionModel().select(productTab);
        fillProductTab(selectedProduct);
    }

    private void fillMainTab(String selectedProduct) {
        ProductsTableView selectedProductsTableViewItem = new ProductsTableView();
        Integer categoryId = 0;
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Products where title= \'" + normalize(selectedProduct) + "\'").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Products product = (Products) iterator.next();
                selectedProductsTableViewItem = new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime()
                );
                categoryId = product.getCategoryId().getId();
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        try {
            List response1 = session1.createQuery("From Products where categoryId= " + categoryId).list();
            for (Iterator iterator1 = response1.iterator(); iterator1.hasNext();) {
                Products product1 = (Products) iterator1.next();
                data.add(new ProductsTableView(
                                product1.getArticle(),
                                product1.getTitle(),
                                product1.getDescription(),
                                product1.getDeliveryTime()
                        )
                );
            }
        } catch (HibernateException e1) {
        } finally {
            session1.close();
        }
        buildProductsTable(data);

        for (int i = 0; i < productsTable.getItems().size(); i++) {
            if (productsTable.getItems().get(i).getTitle().equals(selectedProduct)) {
                productsTable.getSelectionModel().clearAndSelect(i);
                productsTable.scrollTo(productsTable.getSelectionModel().getSelectedItem());
            }
        }

        productsTable.getSelectionModel().select(selectedProductsTableViewItem);
        setVendorSelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
        setCategorySelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
    }

    private void newCategoryDialog(String whatTree) {
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
            createNewCategory(whatTree);
            if (whatTree.equals("main")) {
                buildCategoryTree();
            } else if (whatTree.equals("modal")) {
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal);
            }
        }
    }
    private void editCategoryDialog(String whatTree) {
        if (whatTree.equals("main")) {
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
        } else if (whatTree.equals("modal")) {
            ArrayList<String> details = getCategoryDetails(treeView.getSelectionModel().getSelectedItem().getValue());
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
                editCategory(treeView.getSelectionModel().getSelectedItem().getValue(), newCatTitle, newCatDescription);
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal);
            }
        }
    }
    private void deleteCategoryDialog(String whatTree) {
        if (whatTree.equals("main")) {
            String catTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Внимание!");
            alert.setTitle("Удаление категории");
            String s = "Категория \"" + catTitle + "\" будет удалена из каталога. Все элементы, принадлежащие этой категории будут перенесены в ближайшшую вышестоящую категорию.";
            alert.setContentText(s);
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                Integer parentCatId = getParentCatId(catTitle);
                Integer catId = getCategoryIdFromTitle(catTitle);
                replaceProductsUp(catId, parentCatId);
                deleteCategory(catTitle);
                buildCategoryTree();
                ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
                productsTable.setItems(data);
            }
        } else if (whatTree.equals("modal")) {
            String catTitle = treeView.getSelectionModel().getSelectedItem().getValue();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Внимание!");
            alert.setTitle("Удаление категории");
            String s = "Категория \"" + catTitle + "\" будет удалена из каталога. Все элементы, принадлежащие этой категории будут перенесены в ближайшшую вышестоящую категорию.";
            alert.setContentText(s);
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                Integer parentCatId = getParentCatId(catTitle);
                Integer catId = getCategoryIdFromTitle(catTitle);
                replaceProductsUp(catId, parentCatId);
                deleteCategory(catTitle);
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal);
                ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
                productsTable.setItems(data);
            }
        }
    }
    private void createNewCategory(String whatTree) {
        String parentCategoryTitle = new String();
        if (whatTree.equals("main")) {
            parentCategoryTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
        } else if (whatTree.equals("modal")) {
            parentCategoryTitle = treeView.getSelectionModel().getSelectedItem().getValue();
        }
        Integer parentId = getCategoryIdFromTitle(parentCategoryTitle);
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
        for (Products product: productsUp) {
            updateCategoryId(parentCatId, product.getTitle());
        }
    }
    private void onFocusedProductTableItem() {
        try {
            selectedProduct = (String) (productsTable.getFocusModel().getFocusedItem()).getTitle();
            buildPricesTable(selectedProduct);
            buildQuantityTable(selectedProduct);
            buildDeliveryTimeTable(selectedProduct);
            buildAnalogsTable(selectedProduct);
            buildDatasheetFileTable(selectedProduct);
            buildImageView(selectedProduct);
            setVendorSelected(selectedProduct);
            buildPropertiesTree(selectedProduct);
            datasheetFileTable.refresh();
            setSelectProperty(selectedProduct);
            buildFunctionsTable1(selectedProduct);
            setSelectFunction();
            buildAccessoriesTable(selectedProduct);
        } catch (NullPointerException ex) {
        }
    }
    private void addVendorDialog() {
        ObservableList<String> currencies = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List response = session.createQuery("From Currencies").list();
        for (Iterator iterator = response.iterator(); iterator.hasNext();) {
            Currencies c = (Currencies) iterator.next();
            currencies.add(c.getTitle());
        }
        session.close();

        Dialog<NewVendor> dialog = new Dialog<>();
        dialog.setTitle("Создание нового производителя");
        dialog.setHeaderText("Введите данные нового производителя.");
        dialog.setResizable(false);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Описание (необязательно):  ");
        Label label3 = new Label("Страна происхождения:  ");
        Label label4 = new Label("Валюта расчёта с этим производителем:  ");
        Label label5 = new Label("Наценка на продукцию этого производителя:  ");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();
        TextField text3 = new TextField();
        ChoiceBox<String> currency = new ChoiceBox<>();
        currency.setItems(currencies);
        TextField text5 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);
        grid.add(label4, 1, 4);
        grid.add(currency, 2, 4);
        grid.add(label5, 1, 5);
        grid.add(text5, 2, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewVendor(text1.getText(), text2.getText(), currency.getValue(), text3.getText(), Double.parseDouble(text5.getText()));
            }
            return null;
        });
        Optional<NewVendor> result = dialog.showAndWait();
        if (result.isPresent()) {
            newVendorTitle = result.get().getTitle();
            newVendorDescription = result.get().getDescription();
            newVendorCurrency = result.get().getCurrency();
            newVendorAddress = result.get().getAddress();
            newVendorRate = result.get().getRate();
            createNewVendor();
            buildVendorsTable();
        }
    }
    private void createNewVendor() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Vendors vendor = new Vendors();
        vendor.setTitle(newVendorTitle);
        vendor.setDescription(newVendorDescription);
        vendor.setAddress(newVendorAddress);
        vendor.setRate(newVendorRate);
        session.save(vendor);
        tx.commit();
        session.close();
        setCurrency(newVendorCurrency, newVendorTitle);
    }
    private void setCurrency(String value, String vendorTitle) {
        if (!(value.equals("") || value.equals(null))) {
            ArrayList<Currencies> curList = new ArrayList<>();
            Integer id = 0;
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Long countL = (Long) session.createQuery("select count(*) from Currencies ").uniqueResult();
            Integer count = (int)(long) countL;
            for (int i = 0; i < count; i++) {
                Currencies cur = (Currencies) session.get(Currencies.class, i+1);
                curList.add(cur);
            }
            List ids = session.createSQLQuery("select id from vendors where title=\"" + vendorTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            Vendors vendor = (Vendors) session.get(Vendors.class, id);
            for (Currencies c: curList) {
                try {
                    if (c.getTitle().equals(value)) {
                        vendor.setCurrencyId(c);
                    }
                } catch (NullPointerException ne) {}
            }
            session.save(vendor);
            session.getTransaction().commit();
            session.close();
        }
    }
    private void subPropertiesList(String selectedNode) {
        subPropertiesTreeViewList = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List subProperties = session.createSQLQuery(
                    "SELECT title FROM property_types t1, (SELECT id FROM property_types WHERE title=" +
                            "\"" + normalize(selectedNode) + "\") t2 WHERE t2.id = t1.parent").list();
            for (Iterator iterator = subProperties.iterator(); iterator.hasNext();) {
                String sub = (String) iterator.next();
                subPropertiesTreeViewList.add(new PropertiesTreeView(sub));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
    }
    private void setCategorySelected(String selectedProduct) {
        Categories category = new Categories();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("From Products where title = :title");
        query.setParameter("title", selectedProduct);
        List list = query.list();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Products p = (Products) iterator.next();
            if (p.getTitle().equals(selectedProduct)) {
                category = p.getCategoryId();
            }
        }
        session.close();
        recursiveTreeCall(categoriesTree.getRoot(), category);
        expandParents(productOwner);
        categoriesTree.getSelectionModel().select(productOwner);
        categoriesTree.scrollTo(categoriesTree.getRow(productOwner));
    }
    private void recursiveTreeCall(TreeItem<String> root, Categories category) {
        for (TreeItem<String> item: root.getChildren()) {
            if (item.getValue().equals(category.getTitle())) {
                productOwner = item;
            } else {
                recursiveTreeCall(item, category);
            }
        }
    }
    private void setVendorSelected(String selectedProduct) {
        Vendors vendor = new Vendors();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("From Products where title = :title");
        query.setParameter("title", selectedProduct);
        List list = query.list();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Products p = (Products) iterator.next();
            if (p.getTitle().equals(selectedProduct)) {
                vendor = p.getVendor();
            }
        }
        for (int i = 0; i < vendorsTable.getItems().size(); i++) {
            if (vendorsTable.getItems().get(i).getTitle().equals(vendor.getTitle())) {
                vendorsTable.getSelectionModel().clearAndSelect(i);
                vendorsTable.scrollTo(vendorsTable.getSelectionModel().getSelectedItem());
            }
        }
    }
    private void setDatasheetFile() {
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
            session.close();
        }
        buildDatasheetFileTable(selectedProduct);
        gridPanePDF.getChildren().clear();
        gridPanePDF.getChildren().add(datasheetFileTable);
        gridPanePDF.getChildren().add(deliveryTable);
        productsTable.setFocusTraversable(true);
        datasheetFileTable.refresh();
    }
    private ArrayList<TreeItem<String>> expandParents(TreeItem<String> owner) {
        ArrayList<TreeItem<String>> allParents = new ArrayList<>();
        try {
            if (!owner.getParent().equals(null)) {
                owner.getParent().setExpanded(true);
                allParents.add(owner.getParent());
                expandParents(owner.getParent());
            }
        } catch (NullPointerException ne) {}
        return allParents;
    }
    @FXML private void handleCategoryTreeMouseClicked(MouseEvent event) {
        startProgressBar(event);
    }
    @FXML public  void startProgressBar(MouseEvent event) {
        Task task = createTask(event);
        progressBar.progressProperty().bind(task.progressProperty());
        Platform.runLater(task);
        /*
        thread  = new Thread(task);
        thread.start();
        */
    }
    @FXML private void handlePropertiesTreeMouseClicked(MouseEvent event) {
        ObservableList<PropertiesTreeTableView> data = FXCollections.observableArrayList();
        // Вызываем метод, возврщающий нам название кликнутого узла
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            String selectedNode = (String) ((TreeItem)propertiesTree.getSelectionModel().getSelectedItem()).getValue();
            subPropertiesList(selectedNode);
            buildPropertiesTable(selectedNode, productTabTitle.getText());
            propertiesTable.getSelectionModel().select(0);
        }
    }
    @FXML private void handleProductTableMousePressed(MouseEvent event1) {
        onFocusedProductTableItem();
        if (productsTable.getSelectionModel().getSelectedItems().size() == 1) {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Установить коэффициенты цен").onAction((ActionEvent arg0) -> {
                        Integer selectedProductID = getProductIdFromTitle(selectedProduct);
                        SetRatesWindow ratesWindow = new SetRatesWindow(null, selectedProductID);
                        ratesWindow.showModalWindow();
                    }).build(),
                    MenuItemBuilder.create().text("Открыть вкладку обзора свойств устройства").onAction((ActionEvent arg0) -> {
                        openProductTab();
                    }).build(),
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
                        loadPdfFile(productsTable.getSelectionModel().getSelectedItem().getTitle());
                    }).build(),
                    MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                        changeProductCategoryDialog();
                    }).build(),
                    MenuItemBuilder.create().text("Добавить элемент...").onAction((ActionEvent arg0) -> {
                        addProductDialog();
                    }).build(),
                    MenuItemBuilder.create().text("Удалить элемент...").onAction((ActionEvent arg0) -> {
                        deleteProductDialog();
                    }).build(),
                    SeparatorMenuItemBuilder.create().build(),
                    MenuItemBuilder.create().text("Добавить аксессуар к выбранному устройству").onAction((ActionEvent arg0) -> {
                        Accessory.addToSelectedOn(productsTable);
                    }).build()
            ).build();
        } else if(productsTable.getSelectionModel().getSelectedItems().size() == 0) {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Импортировать элементы...").onAction((ActionEvent arg0) -> {
                        tabPane.getSelectionModel().select(settingsTab);}).build()
            ).build();
        } else {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                        changeProductCategoryDialog();}).build(),
                    MenuItemBuilder.create().text("Удалить выбранные элементы").onAction((ActionEvent arg0) -> {
                        deleteProductDialog();}).build(),
                    SeparatorMenuItemBuilder.create().build(),
                    MenuItemBuilder.create().text("Добавить аксессуар к выбранным устройствам").onAction((ActionEvent arg0) -> {
                        Accessory.addToSelectedOn(productsTable);
                    }).build()
            ).build();
        }
        productsTable.setContextMenu(productTableContextMenu);
        fillProductTab(selectedProduct);
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
        productsTable.getSelectionModel().select(0);
        setVendorSelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
        setCategorySelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Страница товара" ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Заполняет заголовок вкладки свойств товара названием текущего выбранного товара,
    // а также отображает картинку товара, еасли она определена.
    private void fillProductTab(String selectedProduct) {
        //ProductsTableView product = productsTable.getSelectionModel().getSelectedItem();
        try {
            productTabTitle.setText(selectedProduct);
            productTabKind.setText(getProductKindTitle(selectedProduct));
            Session session = HibernateUtil.getSessionFactory().openSession();
            List pics = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(selectedProduct) + " and fileTypeId=" + 1).list();
            if (pics.size()==0) {
                ProductImage.open(new File(noImageFile), productTabGridPaneImageView, productTabImageView);
                picDescriptionTextArea.setText("");
            } else {
                for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                    Files pic = (Files) iterator.next();
                    File picFile = new File(pic.getPath());
                    ProductImage.open(picFile, productTabGridPaneImageView, productTabImageView);
                    picDescriptionTextArea.setText(pic.getDescription());
                }
            }
            session.close();
        } catch (NullPointerException ne) {}
        buildAccessoriesTable(selectedProduct);
    }

    // Сохраняет в БД описание картинки товара.
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
    private void buildPropertiesTable(String selectedPropertyType, String selectedProduct) {
        ArrayList<Integer> propertyIds = new ArrayList<>();
        ArrayList<PropertyValues> propertyValuesList = new ArrayList<>();
        ArrayList<PropertiesTreeTableView> propertyValues = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Properties where propertyTypeId =" + getPropertyTypeIdFromTitle(selectedPropertyType)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Properties pr = (Properties) iterator.next();
            propertyIds.add(pr.getId());
        }
        session.close();

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from PropertyValues where productId=" + getProductIdFromTitle(selectedProduct)).list();
        for(Iterator iterator =  res1.iterator(); iterator.hasNext();) {
            PropertyValues pv = (PropertyValues) iterator.next();
            if(propertyIds.contains(pv.getPropertyId().getId())) {
                propertyValuesList.add(pv);
            }
        }
        session1.close();

        String currentTitle = "";
        for (int i = 0; i < propertyValuesList.size(); i++) {
            if (!currentTitle.equals(propertyValuesList.get(i).getPropertyId().getTitle())) {
                propertyValues.add(new PropertiesTreeTableView(
                        propertyValuesList.get(i).getPropertyId().getTitle(),
                        propertyValuesList.get(i).getCond(),
                        propertyValuesList.get(i).getValue() + " " + propertyValuesList.get(i).getMeasureId().getSymbolRu(),
                        propertyValuesList.get(i).getMeasureId().getTitle(),
                        propertyValuesList.get(i).getId()
                ));
                currentTitle = propertyValuesList.get(i).getPropertyId().getTitle();
            } else {
                propertyValues.add(new PropertiesTreeTableView(
                        "",
                        propertyValuesList.get(i).getCond(),
                        propertyValuesList.get(i).getValue() + " " + propertyValuesList.get(i).getMeasureId().getSymbolRu(),
                        propertyValuesList.get(i).getMeasureId().getTitle(),
                        propertyValuesList.get(i).getId()
                ));
            }
        }

        ObservableList<TreeItem<PropertiesTreeTableView>> treeItems = FXCollections.observableArrayList();
        propertyValues.stream().forEach((prop) -> {
            treeItems.add(new TreeItem<PropertiesTreeTableView>(prop));
        });

        TreeItem<PropertiesTreeTableView> root = new TreeItem<>(new PropertiesTreeTableView("Все свойства"));
        root.setExpanded(true);

        treeItems.stream().forEach((item) -> {
            ArrayList<TreeItem> children = treeItemChildren(item);
            if (children.size() > 0) {
                children.stream().forEach((child) -> {
                    item.getChildren().add(child);
                });
            }
            root.getChildren().add(item);
        });

        propertyTitleColumn.setCellValueFactory(new TreeItemPropertyValueFactory("title"));
        propertyConditionColumn.setCellValueFactory(new TreeItemPropertyValueFactory("cond"));
        propertyValueColumn.setCellValueFactory(new TreeItemPropertyValueFactory("value"));
        propertyMeasureColumn.setCellValueFactory(new TreeItemPropertyValueFactory("measure"));

        propertiesTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новое свойство").onAction((ActionEvent ae1) -> {
                    ContextBuilder.createNewPropertyValue(productTabTitle, propertiesTree);
                    buildPropertiesTable(selectedPropertyType, selectedProduct);
                    propertiesTable.getSelectionModel().select(0);
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранное свойство").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateThePropertyValue(productTabTitle, propertiesTree, propertiesTable);
                    buildPropertiesTable(selectedPropertyType, selectedProduct);
                    propertiesTable.getSelectionModel().select(0);
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранное свойство").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteThePropertyValue(productTabTitle, propertiesTree, propertiesTable);
                    buildPropertiesTable(selectedPropertyType, selectedProduct);
                    propertiesTable.getSelectionModel().select(0);
                }).build()
        ).build();
        propertiesTable.setContextMenu(propertiesTableContextMenu);
        propertiesTable.setRoot(root);
        propertiesTable.setShowRoot(false);
        propertiesTable.getColumns().setAll(propertyTitleColumn,
                propertyConditionColumn,
                propertyValueColumn,
                propertyMeasureColumn);
    }
    private void buildFunctionsTable1(String selectedProduct) {
        ArrayList<Functions> functions = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductsFunctions where productId =" + getProductIdFromTitle(selectedProduct)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ProductsFunctions func = (ProductsFunctions) iterator.next();
            functions.add(func.getFunctionId());
        }
        session.close();

        ObservableList<FunctionsTableView> list = FXCollections.observableArrayList();
        functions.stream().forEach((f) -> {
            list.add(new FunctionsTableView(f.getTitle(), f.getSymbol(), f.getId()));
        });
        functionsTableTitleColumn1.setCellValueFactory(new PropertyValueFactory<>("title"));
        functionsTableSymbolColumn1.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        functionsTable1ContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новую функцию").onAction((ActionEvent ae1) -> {
                    String selFunction = ContextBuilder.addFunctionToProduct(functionsTable1, productTabTitle, productTabKind);
                    buildFunctionsTable1(selectedProduct);
                    setFunctionDescriptionAndPicture(selFunction);
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранную функцию").onAction((ActionEvent ae2) -> {
                    ContextBuilder.editFunctionOfProduct();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную функцию").onAction((ActionEvent ae3) -> {
                    ContextBuilder.removeFunctionFromProduct(functionsTable1, productTabTitle);
                    buildFunctionsTable1(selectedProduct);
                }).build()
        ).build();
        functionsTable1.setContextMenu(functionsTable1ContextMenu);
        functionsTable1.setItems(list);
    };
    private void setFunctionPicture(String functionPicturePath) {
        File picFile = new File(functionPicturePath);
        ProductImage.open(picFile, functionGridPaneImageView, functionImageView);
    }
    private void setSelectProperty(String selectedProduct) {
        ObservableList<PropertiesTreeTableView> data = FXCollections.observableArrayList();
        propertiesTree.getSelectionModel().select(0);
        String selectedPropertyTitle = propertiesTree.getSelectionModel().getSelectedItem().getValue();
        subPropertiesList(selectedPropertyTitle);
        buildPropertiesTable(selectedPropertyTitle, selectedProduct);
        propertiesTable.getSelectionModel().select(0);
    }
    private void setSelectFunction() {
        functionDescriptionTextArea.setText("");
        setFunctionPicture(noImageFile);
        functionsTable1.getSelectionModel().select(0);
        showFunctionDescription();
    }
    @FXML private void setPictureButtonPress() {
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            ProductImage.open(file, gridPane, imageView);
            ProductImage.open(file, productTabGridPaneImageView, productTabImageView);
            ProductImage.save(file, selectedProduct);
        }
    }
    private void setFunctionDescriptionAndPicture(String selectedFunction) {
        String functionDescription = "";
        String functionPicturePath = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Functions where title=\'" + selectedFunction + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
            Functions f = (Functions) iterator.next();
            functionDescription = f.getDescription();
            functionPicturePath = f.getPicturePath();
        }
        if ((functionDescription!=null) && (!functionDescription.equals("") &&
                (functionPicturePath!=null) && (!functionPicturePath.equals("")))) {
            functionDescriptionTextArea.setText(functionDescription);
            setFunctionPicture(functionPicturePath);
        } else {
            functionDescriptionTextArea.setText("");
            setFunctionPicture(noImageFile);
        }
    }
    private void showFunctionDescription() {
        String selectedFunction = functionsTable1.getFocusModel().getFocusedItem().getTitle();
        if (selectedFunction!=null) {
            setFunctionDescriptionAndPicture(selectedFunction);
        }
    }
    @FXML private void handleFunctionsTable1MouseClicked() {
        try {
            String selectedFunction = functionsTable1.getSelectionModel().getSelectedItem().getTitle();
            setFunctionDescriptionAndPicture(selectedFunction);
        } catch (NullPointerException ne) {}
    }
    private String getProductKindTitle(String selectedProductTitle) {
        Integer selectedProductKindID = 0;
        String productTabKindText = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title = \'" + selectedProductTitle + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            selectedProductKindID = product.getProductKindId().getId();
        }
        session.close();

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from ProductKinds where id = " + selectedProductKindID).list();
        for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
            ProductKinds productKind = (ProductKinds) iterator.next();
            productTabKindText = productKind.getTitle();
        }
        session1.close();
        return productTabKindText;
    }
    private void buildAccessoriesTable(String selectedProduct) {
        accessoriesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        buildPropertiesTree(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                        setSelectProperty(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                        buildFunctionsTable1(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                        try {
                            setSelectFunction();
                        } catch (NullPointerException ne) {}
                        fillProductTab(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                    }
                }
            }
        });

        ArrayList<Integer> paIds = new ArrayList<>();
        ArrayList<Products> aProducts = new ArrayList<>();
        ObservableList<AccessoriesTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From ProductsAccessories where productId=" + getProductIdFromTitle(selectedProduct)).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                ProductsAccessories pa = (ProductsAccessories) iterator.next();
                paIds.add(pa.getAccessoryId());
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }

        for (Integer id: paIds) {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            try {
                List response1 = session1.createQuery("From Products where id=" + id).list();
                for (Iterator iterator = response1.iterator(); iterator.hasNext();) {
                    Products pr = (Products) iterator.next();
                    aProducts.add(pr);
                }
            } catch (HibernateException e) {
            } finally {
                session1.close();
            }
        }

        for(Products p: aProducts) {
            data.add(new AccessoriesTableView(p.getTitle(), p.getDescription(), p.getId()));
        }

        accessoriesTableTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        accessoriesTableDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        accessoriesTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новый аксессуар").onAction((ActionEvent arg0) -> {
                    Accessory.addToSelectedOn(selectedProduct);
                    buildAccessoriesTable(selectedProduct);
                    }).build(),
                MenuItemBuilder.create().text("Удалить выбранный аксессуар").onAction((ActionEvent arg0) -> {
                    Accessory.removeAccessoryFrom(accessoriesTable, selectedProduct);
                    buildAccessoriesTable(selectedProduct);
                    }).build()
        ).build();
        accessoriesTable.setContextMenu(accessoriesTableContextMenu);
        accessoriesTable.setItems(data);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Даташит" ///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        try {
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer pageNumber) {
                    if (currentFile.get() == null) {
                        return null;
                    } else {
                        if (pageNumber >= currentFile.get().getNumPages() || pageNumber < 0) {
                            return null;
                        } else {
                            updateImage(pageNumber);
                            return scroller;
                        }
                    }
                }
            });
        } catch (StackOverflowError se) {}
    }

    // ************** Event Handlers ****************
    private void loadPdfFile(String product) {
        String pdfFilePath = new String();
        //String product = productTabTitle.getText();
        pdfTabTitle.setText(product);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List pdfs = session.createQuery("from Files where ownerId=" + getProductIdFromTitle(product) + " and fileTypeId=" + 2).list();
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
                    //showErrorMessage("Could not load file "+file.getName(), loadFileTask.getException());
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

    // Конец заимствования здесь


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Контент сайта" ///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ObservableList<String> getListItems(String itemsType) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        ObservableList<String> items = FXCollections.observableArrayList();
        if (itemsType.equals("news")) {
            List res = session.createQuery("from NewsItems").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                NewsItems newsItem = (NewsItems) iterator.next();
                items.add(new String(newsItem.getTitle()));
            }
        } else if (itemsType.equals("articles")) {
            List res = session.createQuery("from Articles").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                Articles article = (Articles) iterator.next();
                items.add(new String(article.getTitle()));
            }
        } else if (itemsType.equals("contents")) {
            List res = session.createQuery("from StaticContents").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                StaticContents staticContent = (StaticContents) iterator.next();
                items.add(new String(staticContent.getTitle()));
            }
        }
        session.close();
        return items;
    }
    private void populateContentSiteLists() {
        newsItemsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать новость").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeNewsItem();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную новость").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteNewsItem(newsListView);
                    populateContentSiteLists();
                }).build()
        ).build();
        articlesListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать статью").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeArticle();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную статью").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteArticle(articlesListView);
                    populateContentSiteLists();
                }).build()
        ).build();
        contentsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать страницу со статическим контентом").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeContent();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную страницу со статическим контентом").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteContent(contentsListView);
                    populateContentSiteLists();
                }).build()
        ).build();
        newsListView.setContextMenu(newsItemsListContextMenu);
        newsListView.setItems(getListItems("news"));
        articlesListView.setContextMenu(articlesListContextMenu);
        articlesListView.setItems(getListItems("articles"));
        contentsListView.setContextMenu(contentsListContextMenu);
        contentsListView.setItems(getListItems("contents"));
    }
    @FXML private void editSelectedNewsItem() {
        NewsItems newsItem = new NewsItems();
        String selectedNewsItem = (String)newsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from NewsItems where title =\'" + selectedNewsItem + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            newsItem = (NewsItems) iterator.next();
        }
        session2.close();
        htmlEditor.setHtmlText("");
        htmlEditor.setHtmlText(newsItem.getContent());
    }
    @FXML private void editSelectedArticle() {
        Articles article = new Articles();
        String selectedArticle = (String)articlesListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from Articles where title =\'" + selectedArticle + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            article = (Articles) iterator.next();
        }
        session2.close();
        htmlEditor.setHtmlText("");
        htmlEditor.setHtmlText(article.getContent());
    }
    @FXML private void editSelectedContent() {
        StaticContents staticContent = new StaticContents();
        String selectedStaticContent = (String)contentsListView.getSelectionModel().getSelectedItem();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery("from StaticContents where title =\'" + selectedStaticContent + "\'").list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            staticContent = (StaticContents) iterator.next();
        }
        session2.close();
        htmlEditor.setHtmlText("");
        htmlEditor.setHtmlText(staticContent.getContent());
    }
    @FXML private void saveContent () {
        if (!newsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            NewsItems newsItem = new NewsItems();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from NewsItems where title=\'" + newsListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                newsItem = (NewsItems) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            newsItem.setContent(cleanHtml(htmlEditor.getHtmlText()));
            newsItem.setUpdatedAt(new Date());
            session.save(newsItem);
            tx.commit();
            session.close();
        } else if (!articlesListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Articles article = new Articles();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from Articles where title=\'" + articlesListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                article = (Articles) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            article.setContent(cleanHtml(htmlEditor.getHtmlText()));
            article.setUpdatedAt(new Date());
            session.save(article);
            tx.commit();
            session.close();
        } else if (!contentsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            StaticContents staticContent = new StaticContents();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from StaticContents where title=\'" + contentsListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                staticContent = (StaticContents) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            staticContent.setContent(cleanHtml(htmlEditor.getHtmlText()));
            staticContent.setUpdatedAt(new Date());
            session.save(staticContent);
            tx.commit();
            session.close();
        }
    }
    private String cleanHtml(String rawHtml) {
        String cleanHtml = new String();
        String cutString1 = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">";
        String cutString2 = "</body></html>";
        cleanHtml = rawHtml.replace(cutString1, "");
        cleanHtml = cleanHtml.replace(cutString2, "");
        return cleanHtml;
    }

    ///////////////////
    // Таб "Браузер" //
    ///////////////////

    /////////////////////
    // Таб "Настройки" //
    /////////////////////

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
        Platform.runLater(task);
        /*
        thread  = new Thread(task);
        thread.start();
        */
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

    // Создаёт задачу запуска процесса импорта данных из xls-файла в новом потоке.
    // Параллельно отображается индикатор для этого процесса.
    private Task<Void> createImportTask() {
        return new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                startImportFromXLSToDB();
                Platform.runLater(() -> {
                    progressBarImportXLS.progressProperty().unbind();
                    progressBarImportXLS.setProgress(0.0);
                });
                return null;
            }
        };
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

    private void buildProductKindsList() {
        String selectedPropertiesKind = productKindsList.getSelectionModel().getSelectedItem();
        ObservableList<String> items = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductKinds").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ProductKinds pKind = (ProductKinds) iterator.next();
            String item = new String(pKind.getTitle());
            items.add(item);
        }
        session.close();
        productKindsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать новый тип устройств").onAction((ActionEvent ae1) -> {
                    ContextBuilder.createNewProductKind();
                    buildProductKindsList();
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранный тип").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateTheProductKind(productKindsList);
                    buildProductKindsList();
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранный тип устройств").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteTheProductKind(productKindsList);
                    buildProductKindsList();
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build()
        ).build();
        productKindsList.setContextMenu(productKindsListContextMenu);
        productKindsList.setItems(items);
    }
    private void buildPropertiesTreeTable(String selectedPropertiesKind) {
        ArrayList<Integer> typesIds = new ArrayList<>();
        ArrayList<PropertiesTreeTableView> properties = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from KindsTypes where productKindId =" + getPropertyKindIdFromTitle(selectedPropertiesKind)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            KindsTypes kt = (KindsTypes) iterator.next();
            typesIds.add(kt.getPropertyTypeId().getId());
        }
        session.close();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery("from PropertyTypes").list();
        for(Iterator iterator =  res1.iterator(); iterator.hasNext();) {
            PropertyTypes pt = (PropertyTypes) iterator.next();
            if(typesIds.contains(pt.getId())) {
                properties.add(new PropertiesTreeTableView(pt.getTitle()));
            }
        }
        session1.close();

        ObservableList<TreeItem<PropertiesTreeTableView>> treeItems = FXCollections.observableArrayList();
        properties.stream().forEach((prop) -> {
            treeItems.add(new TreeItem<PropertiesTreeTableView>(prop));
        });
        TreeItem<PropertiesTreeTableView> root = new TreeItem<>(new PropertiesTreeTableView("Все характеристики"));
        root.setExpanded(true);
        treeItems.stream().forEach((item) -> {
            ArrayList<TreeItem> children = treeItemChildren(item);
            if (children.size() > 0) {
                children.stream().forEach((child) -> {
                    item.getChildren().add(child);
                });
            }
            root.getChildren().add(item);
        });

        propertiesTreeTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новую характеристику").onAction((ActionEvent ae1) -> {
                    ContextBuilder.createNewProperty();
                    buildPropertiesTreeTable(selectedPropertiesKind);
                    buildFunctionsTable(selectedPropertiesKind);
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранную характеристику").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateTheProperty(propertiesTreeTable);
                    buildPropertiesTreeTable(selectedPropertiesKind);
                    buildFunctionsTable(selectedPropertiesKind);
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную характеристику").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteTheProperty(propertiesTreeTable);
                    buildPropertiesTreeTable(selectedPropertiesKind);
                    buildFunctionsTable(selectedPropertiesKind);
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                SeparatorMenuItemBuilder.create().build(),
                MenuItemBuilder.create().text("Добавить набор характеристик").onAction((ActionEvent ae3) -> {
                    ContextBuilder.addPropertyType(productKindsList);
                    buildPropertiesTreeTable(selectedPropertiesKind);
                    buildProductKindsList();
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build(),
                MenuItemBuilder.create().text("Удалить набор характеристик").onAction((ActionEvent ae3) -> {
                    ContextBuilder.removePropertyType(productKindsList, propertiesTreeTable);
                    buildPropertiesTreeTable(selectedPropertiesKind);
                    buildProductKindsList();
                    propertiesTreeTable.refresh();
                    functionsTable.refresh();
                }).build()
        ).build();
        propertiesTreeTable.setContextMenu(propertiesTreeTableContextMenu);
        propertiesTreeTable.setRoot(root);
        propertiesTreeTable.setShowRoot(false);
        propertiesTreeTable.getColumns().setAll(propertiesTreeTableTitleColumn);
        propertiesTreeTableTitleColumn.setCellValueFactory(new TreeItemPropertyValueFactory("title"));
    }
    private void buildFunctionsTable(String selectedPropertiesKind) {
        ArrayList<Functions> functions = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Functions where productKindId =" + getPropertyKindIdFromTitle(selectedPropertiesKind)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Functions func = (Functions) iterator.next();
            functions.add(func);
        }
        session.close();
        ObservableList<FunctionsTableView> list = FXCollections.observableArrayList();
        functions.stream().forEach((f) -> {
            list.add(new FunctionsTableView(f.getTitle(), f.getSymbol(), f.getId()));
        });
        functionsTableTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        functionsTableSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        functionsTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новую функцию").onAction((ActionEvent ae1) -> {
                    ContextBuilder.createNewFunction(getPropertyKindIdFromTitle(selectedPropertiesKind));
                    buildFunctionsTable(selectedPropertiesKind);
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранную функцию").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateTheFunction(getPropertyKindIdFromTitle(selectedPropertiesKind), functionsTable);
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную функцию").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteTheFunction(functionsTable);
                    buildFunctionsTable(selectedPropertiesKind);
                }).build()
        ).build();
        functionsTable.setContextMenu(functionsTableContextMenu);
        functionsTable.setItems(list);
    };

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
    @FXML public  void startImportProgressBar() {
        Task task = createImportTask();
        progressBarImportXLS.progressProperty().bind(task.progressProperty());
        Platform.runLater(task);
        /*
        thread  = new Thread(task);
        thread.start();
        */
    }
    @FXML private void handlePropertiesKindSelected() {
        String selectedPropertiesKind = productKindsList.getSelectionModel().getSelectedItem();
        buildPropertiesTreeTable(selectedPropertiesKind);
        buildFunctionsTable(selectedPropertiesKind);
    }
    private ArrayList<TreeItem> treeItemChildren(TreeItem<PropertiesTreeTableView> item) {
        String itemTitle = item.getValue().getTitle();
        ArrayList<TreeItem> childTreeItems = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Properties where propertyTypeId=" + getPropertyTypeIdFromTitle(itemTitle)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Properties property = (Properties) iterator.next();
            childTreeItems.add(new TreeItem(new PropertiesTreeTableView(property.getTitle())));
        }
        session.close();
        return childTreeItems;
    }

    @FXML Button testButton = new Button();
    @FXML private void writeToSite() {
        DBConnection siteConnection = new DBConnection();
        try {
            siteConnection.getUpdateResult("CREATE TABLE test1(id int, title varchar(50), PRIMARY KEY(id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void saveAddressSiteDB() {
        SiteDBSettings address = new SiteDBSettings();
        address.saveSetting("addressSiteDB", addressSiteDB.getText());
    }
    @FXML private void savePortSiteDB() {
        SiteDBSettings port = new SiteDBSettings();
        port.saveSetting("portSiteDB", portSiteDB.getText());
    }
    @FXML private void saveTitleSiteDB() {
        SiteDBSettings title = new SiteDBSettings();
        title.saveSetting("titleSiteDB", titleSiteDB.getText());
    }
    @FXML private void saveUserSiteDB() {
        SiteDBSettings user = new SiteDBSettings();
        user.saveSetting("userSiteDB", userSiteDB.getText());
    }
    @FXML private void savePasswordSiteDB() {
        SiteDBSettings password = new SiteDBSettings();
        password.saveSetting("passwordSiteDB", passwordSiteDB.getText());
    }

    @FXML private void saveAddressLocalDB() {
        LocalDBSettings address = new LocalDBSettings();
        address.saveSetting("addressLocalDB", addressLocalDB.getText());
    }
    @FXML private void savePortLocalDB() {
        LocalDBSettings port = new LocalDBSettings();
        port.saveSetting("portLocalDB", portLocalDB.getText());
    }
    @FXML private void saveTitleLocalDB() {
        LocalDBSettings title = new LocalDBSettings();
        title.saveSetting("titleLocalDB", titleLocalDB.getText());
    }
    @FXML private void saveUserLocalDB() {
        LocalDBSettings user = new LocalDBSettings();
        user.saveSetting("userLocalDB", userLocalDB.getText());
    }
    @FXML private void savePasswordLocalDB() {
        LocalDBSettings password = new LocalDBSettings();
        password.saveSetting("passwordLocalDB", passwordLocalDB.getText());
    }

}