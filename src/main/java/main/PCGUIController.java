package main;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import entities.*;
import entities.Properties;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import jxl.read.biff.BiffException;
import modalwindows.AlertWindow;
import modalwindows.SetRatesWindow;
import new_items.NewCategory;
import new_items.NewSerie;
import new_items.NewVendor;
import org.hibernate.*;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import settings.LocalDBSettings;
import settings.PriceCalcSettings;
import settings.SiteDBSettings;
import settings.SiteSettings;
import tableviews.*;
import treeviews.CategoriesTreeView;
import utils.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 *
 * @author Igor Klekotnev
 */

public class PCGUIController implements Initializable {

    // Panes
    @FXML private AnchorPane anchorPane;
    @FXML private AnchorPane editorAnchorPane;
    @FXML private StackPane  stackPane;
    @FXML private GridPane   gridPane;
    @FXML private GridPane   gridPanePDF;
    @FXML private GridPane   productDimsGridPane;
    @FXML private GridPane   plug1GridPane;
    @FXML private GridPane   plug2GridPane;
    @FXML private GridPane   productTabGridPaneImageView;
    @FXML private GridPane   functionGridPaneImageView;
    @FXML private ScrollPane scroller;
    StackPane stackPaneModal = new StackPane();
    Stack<ProductKindPropertiesTableView> deletedLines = new Stack<>();

    @FXML private HTMLEditor htmlEditor;
    @FXML private TextArea htmlCode;

    // TreeViews
    @FXML private TreeView<String> categoriesTree;
    TreeView<String> treeView;

    //Tabs
    @FXML TabPane tabPane;
    @FXML Tab mainTab;
    @FXML Tab productTab;
    @FXML Tab pdfTab;
    @FXML Tab settingsTab;
    @FXML Tab editorTab;
    @FXML Tab plug1Tab;
    @FXML Tab plug2Tab;

    // ContextMenus
    @FXML private ContextMenu treeViewContextMenu;
    @FXML private ContextMenu productTableContextMenu;
    @FXML private ContextMenu datasheetTableContextMenu;
    @FXML private ContextMenu productKindsListContextMenu;
    @FXML private ContextMenu functionsTableContextMenu;
    @FXML private ContextMenu functionsTable1ContextMenu;
    @FXML private ContextMenu accessoriesTableContextMenu;
    @FXML private ContextMenu newsItemsListContextMenu;
    @FXML private ContextMenu articlesListContextMenu;
    @FXML private ContextMenu videosListContextMenu;
    @FXML private ContextMenu reviewsListContextMenu;
    @FXML private ContextMenu additionsListContextMenu;
    @FXML private ContextMenu contentsListContextMenu;
    @FXML private ContextMenu analogsTableContextMenu;
    @FXML private ContextMenu usersTableContextMenu;
    @FXML private ContextMenu companiesTableContextMenu;
    @FXML private ContextMenu groupsTableContextMenu;

    // TableViews & TableColumns
    @FXML private TableView<ProductsTableView>            productsTable;
    @FXML private TableColumn<ProductsTableView, Boolean> productAvailable;
    @FXML private TableColumn<ProductsTableView, Boolean> productOutdated;
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
    @FXML private TableColumn<QuantityTableView, Integer> quantityMinimum;
    @FXML private TableColumn<QuantityTableView, Integer> quantityPiecesPerPack;

    @FXML private TableView<AnalogsTableView>             analogsTable;
    @FXML private TableColumn<AnalogsTableView, String>   analogTitle;
    @FXML private TableColumn<AnalogsTableView, String>   analogVendor;

    @FXML private TableView<ProductsTableView>            deliveryTable;
    @FXML private TableColumn<ProductsTableView, String>  deliveryTime;

    @FXML private TableView<DatasheetTableView>           datasheetFileTable;
    @FXML private TableColumn<DatasheetTableView, String> datasheetFileName;

    @FXML private TableView<ProductPropertiesForKindTableView>           productPropertiesForKindTable;
    @FXML private TableColumn<ProductPropertiesForKindTableView, String> productPropertiesForKindOrderNumberColumn;
    @FXML private TableColumn<ProductPropertiesForKindTableView, String> productPropertiesForKindTitleColumn;
    @FXML private TableColumn<ProductPropertiesForKindTableView, String> productPropertiesForKindOptionalColumn;
    @FXML private TableColumn<ProductPropertiesForKindTableView, String> productPropertiesForKindSymbolColumn;
    @FXML private TableColumn<ProductPropertiesForKindTableView, String> productPropertiesForKindValueColumn;

    @FXML private TableView<FunctionsTableView>           functionsTable;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableTitleColumn;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableSymbolColumn;

    @FXML private TableView<FunctionsTableView>           functionsTable1;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableTitleColumn1;
    @FXML private TableColumn<FunctionsTableView, String> functionsTableSymbolColumn1;

    @FXML private TableView<AccessoriesTableView>           accessoriesTable;
    @FXML private TableColumn<AccessoriesTableView, String> accessoriesTableTitleColumn;
    @FXML private TableColumn<AccessoriesTableView, String> accessoriesTableDescriptionColumn;

    @FXML private TableView<UsersTableView>                 usersTable;
    @FXML private TableColumn<UsersTableView, Integer>      userIdTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userNameTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userEmailTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userPasswordTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userGroupTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userCompanyTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userPositionTableColumn;
    @FXML private TableColumn<UsersTableView, String>       userPhoneTableColumn;

    @FXML private TableView<CompaniesTableView>             companiesTable;
    @FXML private TableColumn<CompaniesTableView, Boolean>  companyDealerColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companyTitleColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companyPhoneColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companyEmailColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companySiteColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companyAddressColumn;
    @FXML private TableColumn<CompaniesTableView, String>   companyFaxColumn;

    @FXML private TableView<GroupsTableView>                groupsTable;
    @FXML private TableColumn<GroupsTableView, String>      groupTitleColumn;
    @FXML private TableColumn<GroupsTableView, String>      groupDescriptionColumn;

    @FXML private TableView<ProductKindPropertiesTableView>           productKindPropertiesTable;
    @FXML private TableColumn<ProductKindPropertiesTableView, String> productKindPropertiesOrderColumn;
    @FXML private TableColumn<ProductKindPropertiesTableView, String> productKindPropertiesTitleColumn;
    @FXML private TableColumn<ProductKindPropertiesTableView, String> productKindPropertiesOptionalColumn;
    @FXML private TableColumn<ProductKindPropertiesTableView, String> productKindPropertiesSymbolColumn;

    // Buttons
    @FXML private Button startImportXLSButton;

    // Labels
    @FXML private Label productTabTitle;
    @FXML private Label pdfTabTitle;
    @FXML private Label courseEUROLabel;
    @FXML private Label courseDateLabel;
    @FXML private Label productTabKind;
    @FXML private Label fileForExportPathLabel;
    @FXML private Label dirForExportPricesPathLabel;
    @FXML private Label currentZoomLabel;

    // ProgressBars
    @FXML private ProgressBar progressBar;
    @FXML private ProgressBar progressBarImportXLS;

    // ImageViews
    @FXML private ImageView imageView;
    @FXML private ImageView productTabImageView;
    @FXML private ImageView productDimsImageView;
    @FXML private ImageView plug1ImageView;
    @FXML private ImageView plug2ImageView;
    @FXML private ImageView functionImageView;

    // ListViews
    @FXML private ListView<String> headersXLS;
    @FXML private ListView<String> comparedXLSAndDBFields;
    @FXML private ListView<String> productKindsList;
    @FXML private ListView<String> newsListView;
    @FXML private ListView<String> articlesListView;
    @FXML private ListView<String> videosListView;
    @FXML private ListView<String> reviewsListView;
    @FXML private ListView<String> additionsListView;
    @FXML private ListView<String> categoriesListView;
    @FXML private ListView<String> contentsListView;

    // ComboBoxes
    @FXML private ComboBox<String> searchComboBox;
    @FXML private ComboBox<String> importFieldsComboBox;
    @FXML private ComboBox<String> tableDBForExportComboBox;

    // TextFields
    @FXML private TextField headersRowTextField;
    @FXML private TextArea picDescriptionTextArea;
    @FXML private TextArea plug1TextArea;
    @FXML private TextArea plug2TextArea;
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
    @FXML private TextField categorySearch;
    @FXML private TextField pageTitleTextField;
    @FXML private TextField directoryTitleTextField;
    @FXML private TextField contentTitleTextField;
    @FXML private TitledPane categoriesTitledPane;
    @FXML private TextField exportFileNameTextField;
    @FXML private TextField siteOrdersReceiverTextField;
    @FXML private TextField serverSFTP;
    @FXML private TextField portSFTP;
    @FXML private TextField userSFTP;
    @FXML private TextField passwordSFTP;

    // CheckBoxes
    @FXML private CheckBox treeViewHandlerMode;

    //Paginations
    @FXML private Pagination pagination;

    //Files & FileChoosers
    File fileXLS;
    File fileXLSExport;
    final FileChooser fileChooser = new FileChooser();
    final DirectoryChooser directoryForExport = new DirectoryChooser();
    final DirectoryChooser directoryForExportPrices = new DirectoryChooser();
    final DirectoryChooser directoryForExportProperties = new DirectoryChooser();

    // Lists
    ArrayList<ArrayList<String>> allImportXLSContent = new ArrayList<>(120);
    ArrayList<ArrayList<String>> allCompareDetails = new ArrayList<>(20);
    ObservableList<CategoriesTreeView> subCategoriesTreeViewList = FXCollections.observableArrayList();
    ObservableList<ImportFields> importFields = FXCollections.observableArrayList();
    ObservableList<String> comparedPairs = FXCollections.observableArrayList();

    // Numbers
    private Integer headersRowNumber = 0;
    public Double course;
    Double basePrice;
    Double rubRetail;
    private static final double ZOOM_DELTA = 1.05;
    Double newVendorRate;
    Double addCBR;

    // Strings
    String selectedProduct;
    String newCatTitle = "";
    String newCatDescription;
    String newCatPicturePath;
    String selectedDBKey = "Наименование продукта";
    String catalogHeader = "Каталог товаров";
    String newVendorTitle = "";
    String newVendorDescription;
    String newVendorCurrency;
    String newVendorAddress;
    String targetDir;
    String fileChooserDirectoryCash = "C:\\";
    String noImageFile = "\\\\Server03\\бд_сайта\\poligon_images\\noImage.gif";
    String defaultImagesDir = "\\\\Server03\\бд_сайта\\poligon_images\\";
    String defaultPdfsDir = "\\\\Server03\\бд_сайта\\poligon_datasheets\\datasheets\\";
    String selectedCategory = "";
    String focusedProduct = "";
    String parentCategoryTitle = "";
    private String lastKey = null;

    //booleans
    boolean clear = true;

    private ObjectProperty<PDFFile> currentFile;
    private ObjectProperty<ImageView> currentImage;
    private DoubleProperty zoom;
    private PageDimensions currentPageDimensions;
    private ExecutorService imageLoadService;
    public static DBConnection connection = new DBConnection("local");
    public static ArrayList<Product> allProductsList = new ArrayList<>(22000);
    public static ArrayList<FileOfProgram> allFilesOfProgramList = new ArrayList<>(22000);
    public static ArrayList<QuantityOfProduct> allQuantitiesList = new ArrayList<>();
    public static ObservableList<String> allProductsTitles = FXCollections.observableArrayList();
    public static ObservableList<CategoriesTreeView> allCategoriesList = FXCollections.observableArrayList();
    public static ObservableList<ProductKindPropertiesTableView> productKindsPropertiesList = FXCollections.observableArrayList();
    private static int loadProgramCounter = 0;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try { resetProgram(); } catch (SQLException ignored) {}
    }
    // Загружает в память настройки программы, сохранённые в БД
    @FXML public void loadSavedSettings() {
        SiteDBSettings siteDBSettings = new SiteDBSettings();
        LocalDBSettings localDBSettings = new LocalDBSettings();
        PriceCalcSettings priceCalcSettings = new PriceCalcSettings();
        addCBRTextField.setText(priceCalcSettings.loadSetting("addCBR"));
        addressSiteDB.setText(siteDBSettings.loadSetting("addressSiteDB"));
        portSiteDB.setText(siteDBSettings.loadSetting("portSiteDB"));
        titleSiteDB.setText(siteDBSettings.loadSetting("titleSiteDB"));
        userSiteDB.setText(siteDBSettings.loadSetting("userSiteDB"));
        passwordSiteDB.setText(siteDBSettings.loadSetting("passwordSiteDB"));

        serverSFTP.setText(siteDBSettings.loadSetting("serverSFTP"));
        portSFTP.setText(siteDBSettings.loadSetting("portSFTP"));
        userSFTP.setText(siteDBSettings.loadSetting("userSFTP"));
        passwordSFTP.setText(siteDBSettings.loadSetting("passwordSFTP"));

        addressLocalDB.setText(localDBSettings.loadSetting("addressLocalDB"));
        portLocalDB.setText(localDBSettings.loadSetting("portLocalDB"));
        titleLocalDB.setText(localDBSettings.loadSetting("titleLocalDB"));
        userLocalDB.setText(localDBSettings.loadSetting("userLocalDB"));
        passwordLocalDB.setText(localDBSettings.loadSetting("passwordLocalDB"));
        siteOrdersReceiverTextField.setText(SiteSettings.loadSetting("siteOrdersReceiver"));
    }
    @FXML private void resetProgram() throws SQLException {
        if (loadProgramCounter != 0) {
            getAllProductsList();
            getAllFilesOfProgramList();
            getAllQuantitiesList();
            getAllCategoriesList();
        }
        loadProgramCounter ++;
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
        } catch (IOException ioe) { AlertWindow.alertNoRbcServerConnection(); }
        buildCategoryTree();
        populateComboBox ();
        loadImportFields();
        loadExportTables();
        buildUsersTable();
        buildCompaniesTable();
        buildGroupsTable();
        buildProductKindsList();
        productsTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        productsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                try {
                    selectedProduct = newValue.getTitle();
                    productsTable.getSelectionModel().select(newValue);
                    buildPricesTable(selectedProduct);
                    buildQuantityTable(selectedProduct);
                    buildDeliveryTimeTable(selectedProduct);
                    buildAnalogsTable(selectedProduct);
                    buildDatasheetFileTable(selectedProduct);
                    buildImageView(selectedProduct);
                    datasheetFileTable.refresh();
                } catch (NullPointerException ignored) {}
                productsTable.setContextMenu(productTableContextMenu);
            }
        );
        tabPane.getSelectionModel().selectedItemProperty().addListener(
            (ov, t, t1) -> {
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
                        zoom.addListener((observable, oldValue, newValue) -> {
                            updateImage(pagination.getCurrentPageIndex());
                        });
                        currentZoomLabel.textProperty().bind(Bindings.format("%.0f %%", zoom.multiply(100)));
                        bindPaginationToCurrentFile();
                        createPaginationPageFactory();
                        String product = productTabTitle.getText();
                        try {
                            loadPdfFile(product);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
                else if (t1.equals(mainTab)) {
                    try {
                        try {fillMainTab(productTabTitle.getText());} catch (SQLException ignored) {}
                    } catch (NullPointerException ignored) {}
                } else if (t1.equals(editorTab)) {
                    ExtendHtmlEditor.addPictureFunction(htmlEditor, editorAnchorPane);
                } else if (t1.equals(productTab)) {
                    try {
                        fillProductTab(selectedProduct);
                    } catch (NullPointerException ignored) {}
                }
            }
        );
        try {fillMainTab(productTabTitle.getText());} catch (SQLException ignored) {}
    }
    @FXML private void saveAddCBRToDB() throws SQLException {
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
            buildPricesTable(selectedProduct);
        } catch (IOException ioe) { AlertWindow.alertNoRbcServerConnection(); }
    }
    // Утилиты разные
    @FXML private void productsMoveUpDownByKeyboard(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.DOWN) {
            onFocusedProductTableItem(selectedProduct);
        } else if(keyEvent.getCode() == KeyCode.UP) {
            onFocusedProductTableItem(selectedProduct);
        }
    }
    @FXML private void productKindsMoveUpDownByKeyboard(KeyEvent keyEvent) {
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        if(keyEvent.getCode() == KeyCode.DOWN) {
            buildFunctionsTable(selectedProductKind);
            buildProductKindPropertiesTable(selectedProductKind);
        } else if(keyEvent.getCode() == KeyCode.UP) {
            buildFunctionsTable(selectedProductKind);
            buildProductKindPropertiesTable(selectedProductKind);
        }
    }
    public static void getAllProductsList() throws SQLException {
        allProductsTitles.clear();
        allProductsList.clear();
        ResultSet resultSet = connection.getResult("select * from products");
        while (resultSet.next()) {
            allProductsTitles.add(resultSet.getString("title"));
            allProductsList.add(new Product(
                    resultSet.getInt   ("id"),
                    resultSet.getInt   ("category_id"),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("anons"),
                    resultSet.getString("article"),
                    resultSet.getInt   ("available"),
                    resultSet.getString("delivery_time"),
                    resultSet.getString("ean"),
                    resultSet.getInt   ("outdated"),
                    resultSet.getDouble("price"),
                    resultSet.getString("serie"),
                    resultSet.getInt   ("product_kind_id"),
                    resultSet.getInt   ("vendor_id"),
                    resultSet.getInt   ("currency_id"),
                    resultSet.getDouble("special"),
                    resultSet.getDouble("rate"),
                    resultSet.getDouble("discount1"),
                    resultSet.getDouble("discount2"),
                    resultSet.getDouble("discount3"),
                    resultSet.getDouble("rub_retail")
            ));
        }
    }
    public static void getAllFilesOfProgramList() throws SQLException {
        allFilesOfProgramList.clear();
        ResultSet resultSet = connection.getResult("select * from files");
        while (resultSet.next()) {
            allFilesOfProgramList.add(new FileOfProgram (
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("path"),
                    resultSet.getString("description"),
                    resultSet.getInt("file_type_id"),
                    resultSet.getInt("owner_id")
            ));
        }
    }
    public static void getAllQuantitiesList() throws SQLException {
        allQuantitiesList.clear();
        ResultSet resultSet = connection.getResult("select * from quantity");
        while (resultSet.next()) {
            allQuantitiesList.add(new QuantityOfProduct(
                            resultSet.getInt("id"),
                            resultSet.getInt("stock"),
                            resultSet.getInt("reserved"),
                            resultSet.getInt("ordered"),
                            resultSet.getInt("minimum"),
                            resultSet.getInt("pieces_per_pack"),
                            resultSet.getInt("product_id"),
                            UtilPack.getProductTitleFromId(resultSet.getInt("product_id"), allProductsList)
                    )
            );
        }
    }
    public static void getAllCategoriesList() {
        allCategoriesList.clear();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List categories = session.createQuery("FROM Categories").list();
            for (Iterator iterator = categories.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                allCategoriesList.add(new CategoriesTreeView(category.getId(), category.getTitle(), category.getParent(), category.getPublished()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Номенклатура" //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Получает все продукты из БД для отображения в таблице
    private ObservableList<ProductsTableView> getProductList(Integer selectedNodeID) {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        allProductsList.stream().forEach(product -> {
            if(product.getCategoryId() == selectedNodeID) {
                data.add(new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime(),
                        product.getAvailable() == 1 ? true : false,
                        product.getOutdated()  == 1 ? true : false)
                );
            }
        });
        return data;
    }
    private ObservableList<ProductsTableView> getProductList(String selectedNode) {
        int selectedNodeID = UtilPack.getCategoryIdFromTitle(selectedNode);
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        allProductsList.stream().forEach(product -> {
            if(product.getCategoryId() == selectedNodeID) {
                data.add(new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime(),
                        product.getAvailable() == 1 ? true : false,
                        product.getOutdated()  == 1 ? true : false)
                );
            }
        });
        return data;
    }
    // Получает данные о количестве продукта из БД
    // В виде аргумента принимает название продукта
    private ObservableList<QuantityTableView> getQuantities(String productName) {
        ObservableList<QuantityTableView> data = FXCollections.observableArrayList();
        allQuantitiesList.stream().forEach(q -> {
            //int product_id = UtilPack.getProductIdFromTitle(productName, allProductsList);
            if (q.getProduct_title().equals(productName)) {
                data.add(new QuantityTableView(
                        q.getStock(),
                        q.getReserved(),
                        q.getOrdered(),
                        q.getMinimum(),
                        q.getPieces_per_pack()
                ));
            }
        });
        return data;
    }
    private ObservableList<QuantityTableView> getNullQuantities() {
        ObservableList<QuantityTableView> data = FXCollections.observableArrayList();
        data.add(new QuantityTableView(0,0,0,0,0));
        return data;
    }
    private ObservableList<AnalogsTableView> getNullAnalogs() {
        ObservableList<AnalogsTableView> data = FXCollections.observableArrayList();
        data.add(new AnalogsTableView("аналог не указан", ""));
        return data;
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
                        String previousProductTitle = productsTable.getSelectionModel().getSelectedItem().getTitle();
                        ((ProductsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTitle(t.getNewValue());
                        setNewCellValue("title", t.getNewValue(), previousProductTitle);
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
        productAvailable.setCellValueFactory(new PropertyValueFactory<ProductsTableView, Boolean>("available"));
        productAvailable.setCellFactory(CheckBoxTableCell.forTableColumn(productAvailable));
        // add listeners to boolean properties:
        for (ProductsTableView productsTableView : data) {
            productsTableView.availableProperty().addListener((obs, oldValue, newValue) ->{
                setNewCellValue("available", newValue, productsTableView.getTitle());
            });
        }
        productOutdated.setCellValueFactory(new PropertyValueFactory<ProductsTableView, Boolean>("outdated"));
        productOutdated.setCellFactory(CheckBoxTableCell.forTableColumn(productOutdated));
        // add listeners to boolean properties:
        for (ProductsTableView productsTableView : data) {
            productsTableView.outdatedProperty().addListener((obs, oldValue, newValue) ->{
                setNewCellValue("outdated", newValue, productsTableView.getTitle());
            });
        }
        productsTable.setItems(data);
    }
    private void buildDeliveryTimeTable(String selectedProduct) {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        int id = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
        allProductsList.stream().forEach(product -> {
            if (product.getId() == id) {
                data.add(new ProductsTableView(product.getDeliveryTime()));
            }
        });
        deliveryTime.setCellValueFactory(new PropertyValueFactory<>("delivery_time"));
        deliveryTime.setCellFactory(TextFieldTableCell.forTableColumn());
        deliveryTime.setOnEditCommit(
                new EventHandler<CellEditEvent<ProductsTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<ProductsTableView, String> t) {
                        ((ProductsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDeliveryTime(t.getNewValue());
                        setNewCellValue("delivery_time", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        deliveryTable.setItems(data);
    }
    private void buildPricesTable(String selectedProduct) {
        if (UtilPack.getProductCurrency(selectedProduct, allProductsList) == 1) {
            SetRates ratesPack = SetRates.getRatesPack(selectedProduct);
            allProductsList.stream().forEach((product) -> {
                if (product.getTitle().equals(selectedProduct)) basePrice = product.getPrice();
            });
            ObservableList<PricesTableView> data = FXCollections.observableArrayList();
            String[] priceTypes = {
                    "Розничная цена",
                    "СПЕЦИАЛЬНАЯ ЦЕНА",
                    "Мелкий опт (+10)",
                    "Оптовая цена",
                    "Диллерская цена",
                    "Закупочная цена"
            };

            for (String type : priceTypes) {
                try {
                    Double retailPrice = basePrice * ratesPack.getRate();
                    Double specialDiscount = retailPrice - (retailPrice*ratesPack.getSpecial()/100.0);
                    Double tenPlusDiscount = retailPrice - (retailPrice*ratesPack.getTenPlusDiscount()/100.0);
                    Double optDiscount     = retailPrice - (retailPrice*ratesPack.getOptDiscount()/100.0);
                    Double dealerDiscount  = retailPrice - (retailPrice*ratesPack.getDealerDiscount()/100.0);
                    switch (type) {
                        case "Розничная цена":
                            data.add(new PricesTableView(type, ((new BigDecimal(retailPrice)).setScale(2, RoundingMode.UP)).doubleValue(),
                                    ((new BigDecimal(retailPrice * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                            break;
                        case "СПЕЦИАЛЬНАЯ ЦЕНА":
                            data.add(new PricesTableView(type, ((new BigDecimal(specialDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                    ((new BigDecimal(specialDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                            break;
                        case "Мелкий опт (+10)":
                            if (specialDiscount >= tenPlusDiscount) {
                                data.add(new PricesTableView(type, ((new BigDecimal(tenPlusDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(tenPlusDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            } else {
                                data.add(new PricesTableView(type, ((new BigDecimal(specialDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(specialDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            }
                        case "Оптовая цена":
                            if (specialDiscount >= optDiscount) {
                                data.add(new PricesTableView(type, ((new BigDecimal(optDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(optDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            } else {
                                data.add(new PricesTableView(type, ((new BigDecimal(specialDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(specialDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            }
                        case "Диллерская цена":
                            if (specialDiscount >= dealerDiscount) {
                                data.add(new PricesTableView(type, ((new BigDecimal(dealerDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(dealerDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            } else {
                                data.add(new PricesTableView(type, ((new BigDecimal(specialDiscount)).setScale(2, RoundingMode.UP)).doubleValue(),
                                        ((new BigDecimal(specialDiscount * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                                break;
                            }
                        case "Закупочная цена":
                            data.add(new PricesTableView(type, ((new BigDecimal(basePrice)).setScale(2, RoundingMode.UP)).doubleValue(),
                                    ((new BigDecimal(basePrice * course)).setScale(2, RoundingMode.UP)).doubleValue()));
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException ignored) {}
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
                                try {
                                    getAllProductsList();
                                } catch (SQLException e) {}
                                buildPricesTable(selectedProduct);
                            } else {
                                AlertWindow.illegalAction();
                                buildPricesTable(selectedProduct);
                            }
                        }
                    }
            );
            priceValueRub.setCellValueFactory(new PropertyValueFactory<>("priceR"));
            pricesTable.setItems(data);
        } else if (UtilPack.getProductCurrency(selectedProduct, allProductsList) == 2) {
            SetRates ratesPack = SetRates.getRatesPack(selectedProduct);
            allProductsList.stream().forEach((product) -> {
                if (product.getTitle().equals(selectedProduct)) {
                    basePrice = product.getPrice();
                    rubRetail = product.getRubRetail();
                }
            });
            ObservableList<PricesTableView> data = FXCollections.observableArrayList();
            String[] priceTypes = {
                    "Розничная цена",
                    "СПЕЦИАЛЬНАЯ ЦЕНА",
                    "Мелкий опт (+10)",
                    "Оптовая цена",
                    "Диллерская цена",
                    "Закупочная цена"
            };

            for (String type : priceTypes) {
                try {
                    Double retailPrice = rubRetail;
                    Double specialDiscount = 0.0;
                    Double tenPlusDiscount = 0.0;
                    Double optDiscount     = 0.0;
                    Double dealerDiscount  = 0.0;
                    switch (type) {
                        case "Розничная цена":
                            data.add(new PricesTableView(type, 0.0, rubRetail));
                            break;
                        case "СПЕЦИАЛЬНАЯ ЦЕНА":
                            data.add(new PricesTableView(type, 0.0, 0.0));
                            break;
                        case "Мелкий опт (+10)":
                            data.add(new PricesTableView(type, 0.0, 0.0));
                            break;
                        case "Оптовая цена":
                            data.add(new PricesTableView(type, 0.0, 0.0));
                            break;
                        case "Диллерская цена":
                            data.add(new PricesTableView(type, 0.0, 0.0));
                            break;
                        case "Закупочная цена":
                            data.add(new PricesTableView(type, 0.0, basePrice));
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException ignored) {}
            }
            priceType.setCellValueFactory(new PropertyValueFactory<>("type"));
            priceValue.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceValueRub.setCellValueFactory(new PropertyValueFactory<>("priceR"));
            priceValueRub.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    return object.toString();
                }

                @Override
                public Double fromString(String string) {
                    return Double.parseDouble(string);
                }
            }));
            priceValueRub.setOnEditCommit(
                    new EventHandler<CellEditEvent<PricesTableView, Double>>() {
                        @Override
                        public void handle(CellEditEvent<PricesTableView, Double> t) {
                            if ((t.getRowValue().getType().equals("Закупочная цена"))) {
                                ((PricesTableView) t.getTableView().getItems().get(
                                        t.getTablePosition().getRow())
                                ).setPriceR(t.getNewValue());
                                setNewPriceValue("price", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                                try {
                                    getAllProductsList();
                                } catch (SQLException e) {}
                                buildPricesTable(selectedProduct);
                            } else if ((t.getRowValue().getType().equals("Розничная цена"))) {
                                ((PricesTableView) t.getTableView().getItems().get(
                                        t.getTablePosition().getRow())
                                ).setPriceR(t.getNewValue());
                                setNewPriceValue("rub_retail", t.getNewValue(), productsTable.getFocusModel().getFocusedItem().getTitle());
                                try {
                                    getAllProductsList();
                                } catch (SQLException e) {}
                                buildPricesTable(selectedProduct);
                            } else {
                                AlertWindow.illegalActionR();
                                buildPricesTable(selectedProduct);
                            }
                        }
                    }
            );
            pricesTable.setItems(data);
        }
    }
    private void buildQuantityTable(String selectedProduct) {
        ObservableList<QuantityTableView> quantities = FXCollections.emptyObservableList();
        ArrayList<String> existingProductsTitles = new ArrayList<>();
        allQuantitiesList.stream().forEach(q -> {
            existingProductsTitles.add(q.getProduct_title());
        });
        if (existingProductsTitles.contains(selectedProduct)) {
            quantities = getQuantities(selectedProduct);
        } else {
            quantities = getNullQuantities();
        }
        quantityStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        quantityReserved.setCellValueFactory(new PropertyValueFactory<>("reserved"));
        quantityOrdered.setCellValueFactory(new PropertyValueFactory<>("ordered"));
        quantityMinimum.setCellValueFactory(new PropertyValueFactory<>("minimum"));
        quantityPiecesPerPack.setCellValueFactory(new PropertyValueFactory<>("pieces_per_pack"));

        quantityMinimum.setCellFactory(TextFieldTableCell.<QuantityTableView, Integer>forTableColumn(new IntegerStringConverter()));
        quantityMinimum.setOnEditCommit(
                new EventHandler<CellEditEvent<QuantityTableView, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<QuantityTableView, Integer> t) {
                        ((QuantityTableView) t.getTableView().getItems().get(t.getTablePosition().getRow())
                        ).setPiecesPerPack(t.getNewValue());
                        setNewQuantityValue("minimum", t.getNewValue(),
                                UtilPack.getProductIdFromTitle(productsTable.getFocusModel().getFocusedItem().getTitle(), allProductsList));
                        try {
                            allQuantitiesList.clear();
                            getAllQuantitiesList();
                        } catch (SQLException e) {}
                        buildQuantityTable(selectedProduct);
                    }
                }
        );
        quantityPiecesPerPack.setCellFactory(TextFieldTableCell.<QuantityTableView, Integer>forTableColumn(new IntegerStringConverter()));
        quantityPiecesPerPack.setOnEditCommit(
                new EventHandler<CellEditEvent<QuantityTableView, Integer>>() {
                    @Override
                    public void handle(CellEditEvent<QuantityTableView, Integer> t) {
                        ((QuantityTableView) t.getTableView().getItems().get(t.getTablePosition().getRow())).setPiecesPerPack(t.getNewValue());
                        setNewQuantityValue("pieces_per_pack", t.getNewValue(),
                                UtilPack.getProductIdFromTitle(productsTable.getFocusModel().getFocusedItem().getTitle(),
                                        allProductsList)
                        );
                        try {
                            allQuantitiesList.clear();
                            getAllQuantitiesList();
                        } catch (SQLException e) {}
                        buildQuantityTable(selectedProduct);
                    }
                }
        );
        quantitiesTable.setItems(quantities);
    }
    private void buildAnalogsTable(String selectedProduct) {
        String likeProduct = selectedProduct.split(" ")[0];
        ObservableList<AnalogsTableView> analogs = FXCollections.observableArrayList();
        ArrayList<Analogs> analogsItems = new ArrayList<>(10);
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("From Analogs where prototype like :prototype");
            query.setParameter("prototype", likeProduct);
            List response = query.list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Analogs a = (Analogs) iterator.next();
                analogsItems.add(a);
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        analogsItems.stream().forEach(a -> {
            analogs.add(new AnalogsTableView(a.getTitle(), a.getVendor()));
        });
        if (analogs.size() == 0) {
            analogs.add(new AnalogsTableView("не найдено", ""));
        }

        analogTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        analogVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        analogsTable.setItems(analogs);
        analogsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        try {
                            fillMainTab(productsTable.getSelectionModel().getSelectedItem().getTitle());
                        } catch (SQLException e) {}
                        fillProductTab(productsTable.getSelectionModel().getSelectedItem().getTitle());
                    }
                }
            }
        });
    }
    @FXML private void handleAnalogTableMousePressed(MouseEvent mouseEvent) {
        if (analogsTable.getSelectionModel().getSelectedItems().size() == 0) {
            analogsTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Добавить аналог для выбранного продукта").onAction((ActionEvent arg0) -> {
                        Analog.addToSelectedOn(productsTable);
                        buildAnalogsTable(selectedProduct);
                    }).build()
            ).build();
        } else if (analogsTable.getSelectionModel().getSelectedItems().size() == 1) {
            analogsTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Добавить аналог для выбранного продукта").onAction((ActionEvent arg0) -> {
                        Analog.addToSelectedOn(productsTable);
                        buildAnalogsTable(selectedProduct);
                    }).build(),
                    MenuItemBuilder.create().text("Удалить выбранный аналог").onAction((ActionEvent arg0) -> {
                        Analog.removeAnalogFrom(analogsTable, selectedProduct);
                        buildAnalogsTable(selectedProduct);
                    }).build()
            ).build();
        }
        analogsTable.setContextMenu(analogsTableContextMenu);
    }
    private String getTitleFromId(String table, int id) {
        ResultSet resultSet = null;
        try {
            resultSet = connection.getResult("select title from " + table + " where id =" + id);
            while (resultSet.next()) {
                return resultSet.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void buildUsersTable() {
        ObservableList<UsersTableView> data = FXCollections.observableArrayList();
        ObservableList<String> groups = FXCollections.observableArrayList();
        ObservableList<String> companies = FXCollections.observableArrayList();

        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        ResultSet resultSet3 = null;
        ResultSet resultSet4 = null;
        ResultSet resultSet5 = null;
        ResultSet resultSet6 = null;

        ArrayList<ArrayList> bigHash = new ArrayList<>();

        try {
            resultSet1 = connection.getResult("select id, group_id, company_id  from users");
            while (resultSet1.next()) {
                ArrayList<Integer> miniHash = new ArrayList<>();
                miniHash.add(resultSet1.getInt("id"));
                miniHash.add(resultSet1.getInt("group_id"));
                miniHash.add(resultSet1.getInt("company_id"));
                bigHash.add(miniHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            resultSet5 = connection.getResult("select * from companies");
            while (resultSet5.next()) {
                companies.add(resultSet5.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            resultSet6 = connection.getResult("select * from groups");
            while (resultSet6.next()) {
                groups.add(resultSet6.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (ArrayList miniHash: bigHash) {
            try {
                resultSet4 = connection.getResult("select * from users where id =" + miniHash.get(0));
                while (resultSet4.next()) {
                    data.add(new UsersTableView(
                            resultSet4.getInt("id"),
                            resultSet4.getString("name"),
                            resultSet4.getString("email"),
                            resultSet4.getString("encrypted_password"),
                            getTitleFromId("groups", (int)miniHash.get(1)),
                            getTitleFromId("companies", (int)miniHash.get(2)),
                            resultSet4.getString("position"),
                            resultSet4.getString("phone")

                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


/*
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("from Users ").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Users u = (Users) iterator.next();
                data.add(new UsersTableView(u.getId(), u.getName(), u.getEmail()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
*/

        userIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameTableColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<UsersTableView, String>>() {
                @Override
                public void handle(CellEditEvent<UsersTableView, String> t) {
                    ((UsersTableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setName(t.getNewValue());
                    setNewUserValue("name", t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                }
            }
        );
        userEmailTableColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userEmailTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userEmailTableColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<UsersTableView, String>>() {
                @Override
                public void handle(CellEditEvent<UsersTableView, String> t) {
                    ((UsersTableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setEmail(t.getNewValue());
                    setNewUserValue("email", t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                }
            }
        );
        userPasswordTableColumn.setCellValueFactory(new PropertyValueFactory<>("encrypted_password"));
        userPasswordTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userPasswordTableColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<UsersTableView, String>>() {
                @Override
                public void handle(CellEditEvent<UsersTableView, String> t) {
                    ((UsersTableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setEncryptedPassword(t.getNewValue());
                    setNewUserPassword(t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                }
            }
        );
        userGroupTableColumn.setCellValueFactory(new PropertyValueFactory<>("group"));
        userGroupTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(groups));
        userGroupTableColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<UsersTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<UsersTableView,String> t) {
                        ((UsersTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setGroup(t.getNewValue());
                        setNewUserGroup(t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                    };
                }
        );
        userCompanyTableColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        userCompanyTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(companies));
        userCompanyTableColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<UsersTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<UsersTableView,String> t) {
                        ((UsersTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setCompany(t.getNewValue());
                        setNewUserCompany(t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                    };
                }
        );

        userPositionTableColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        userPositionTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userPositionTableColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<UsersTableView, String>>() {
                @Override
                public void handle(CellEditEvent<UsersTableView, String> t) {
                    ((UsersTableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setPosition(t.getNewValue());
                    setNewUserValue("position", t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                }
            }
        );
        userPhoneTableColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userPhoneTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userPhoneTableColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<UsersTableView, String>>() {
                @Override
                public void handle(CellEditEvent<UsersTableView, String> t) {
                    ((UsersTableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setPhone(t.getNewValue());
                    setNewUserValue("phone", t.getNewValue(), usersTable.getFocusModel().getFocusedItem().getId());
                }
            }
        );
        usersTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить нового пользователя").onAction((ActionEvent arg0) -> {
                    ContextBuilder.addNewUser();
                    buildUsersTable();
                }).build(),
                MenuItemBuilder.create().text("Удалить пользователя").onAction((ActionEvent arg0) -> {
                    ContextBuilder.deleteUser(usersTable.getFocusModel().getFocusedItem().getId());
                    buildUsersTable();
                }).build()
        ).build();
        usersTable.setContextMenu(usersTableContextMenu);
        usersTable.setItems(data);
    }
    private void buildCompaniesTable() {
        ObservableList<CompaniesTableView> data = FXCollections.observableArrayList();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getResult("select * from companies");
            while (resultSet.next()) {
                data.add(new CompaniesTableView(
                        resultSet.getBoolean("dealer"),
                        resultSet.getString("title"),
                        resultSet.getString("address"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("site"),
                        resultSet.getString("fax")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
/*
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("from Companies ").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Companies c = (Companies) iterator.next();
                data.add(new CompaniesTableView(c.getDealer(), c.getTitle(), c.getAddress(), c.getPhone(), c.getEmail(),
                        c.getSite(), c.getFax()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
*/

        companyDealerColumn.setCellValueFactory(new PropertyValueFactory<CompaniesTableView, Boolean>("dealer"));
        companyDealerColumn.setCellFactory(CheckBoxTableCell.forTableColumn(companyDealerColumn));
        // add listeners to boolean properties:
        for (CompaniesTableView companiesTableView : data) {
            companiesTableView.dealerProperty().addListener((obs, oldValue, newValue) ->{
                setDealerCompanyValue(newValue, companiesTableView.getTitle());
            });
        }
        companyTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        companyTitleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companyTitleColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTitle(t.getNewValue());
                        setNewCompanyValue("title", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companyPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        companyPhoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companyPhoneColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setPhone(t.getNewValue());
                        setNewCompanyValue("phone", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companyEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        companyEmailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companyEmailColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setEmail(t.getNewValue());
                        setNewCompanyValue("email", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companySiteColumn.setCellValueFactory(new PropertyValueFactory<>("site"));
        companySiteColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companySiteColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setSite(t.getNewValue());
                        setNewCompanyValue("site", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companyAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        companyAddressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companyAddressColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setAddress(t.getNewValue());
                        setNewCompanyValue("address", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companyFaxColumn.setCellValueFactory(new PropertyValueFactory<>("fax"));
        companyFaxColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        companyFaxColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<CompaniesTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<CompaniesTableView, String> t) {
                        ((CompaniesTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setFax(t.getNewValue());
                        setNewCompanyValue("fax", t.getNewValue(), companiesTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );
        companiesTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новую компанию").onAction((ActionEvent arg0) -> {
                    ContextBuilder.addNewCompany();
                    buildUsersTable();
                    buildCompaniesTable();
                }).build(),
                MenuItemBuilder.create().text("Удалить компанию").onAction((ActionEvent arg0) -> {
                    ContextBuilder.deleteCompany(companiesTable.getFocusModel().getFocusedItem().getTitle());
                    buildUsersTable();
                    buildCompaniesTable();
                }).build()
        ).build();
        companiesTable.setContextMenu(companiesTableContextMenu);
        companiesTable.setItems(data);
    }
    private void buildGroupsTable() {
        ObservableList<GroupsTableView> data = FXCollections.observableArrayList();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getResult("select * from groups");
            while (resultSet.next()) {
                data.add(new GroupsTableView(
                        resultSet.getString("title"),
                        resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
/*
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("from Groups ").list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Groups g = (Groups) iterator.next();
                data.add(new GroupsTableView(g.getTitle(), g.getDescription()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
*/
        groupTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        groupDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        groupDescriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupDescriptionColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<GroupsTableView, String>>() {
                    @Override
                    public void handle(CellEditEvent<GroupsTableView, String> t) {
                        ((GroupsTableView) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setDescription(t.getNewValue());
                        setNewGroupValue("description", t.getNewValue(), groupsTable.getFocusModel().getFocusedItem().getTitle());
                    }
                }
        );

        groupsTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Добавить новую группу").onAction((ActionEvent arg0) -> {
                    ContextBuilder.addNewGroup();
                    buildUsersTable();
                    buildGroupsTable();
                }).build(),
                MenuItemBuilder.create().text("Удалить группу").onAction((ActionEvent arg0) -> {
                    ContextBuilder.deleteGroup(groupsTable.getFocusModel().getFocusedItem().getTitle());
                    buildUsersTable();
                    buildGroupsTable();
                }).build()
        ).build();
        groupsTable.setContextMenu(groupsTableContextMenu);
        groupsTable.setItems(data);
    }
    private void buildDatasheetFileTable(String selectedProduct) {
        ObservableList<DatasheetTableView> data = FXCollections.observableArrayList();
        int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
        allFilesOfProgramList.stream().forEach(fileOfProgram -> {
            if((fileOfProgram.getOwner_id() == selectedProductId) && (fileOfProgram.getFile_type_id() == 2)) {
                data.add(new DatasheetTableView(fileOfProgram.getName()));
            }
        });
        datasheetFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        datasheetTableContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Изменить даташит pdf-файл").onAction((ActionEvent arg0) -> {
                    try {
                        setDatasheetFile();
                        getAllFilesOfProgramList();
                        buildDatasheetFileTable(selectedProduct);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).build()
        ).build();
        datasheetFileTable.setContextMenu(datasheetTableContextMenu);
        datasheetFileTable.setItems(data);
    }
    private void buildImageView(String selectedProduct) {
        if (selectedProduct == null) {
            selectedProduct = focusedProduct;
        }
        int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
        ArrayList<Integer> allOwners = new ArrayList<>();
        allFilesOfProgramList.stream().forEach(fileOfProgram -> {
            allOwners.add(fileOfProgram.getOwner_id());
        });
        if (allOwners.contains(selectedProductId)) {
            allFilesOfProgramList.stream().forEach(fileOfProgram -> {
                if((fileOfProgram.getOwner_id() == selectedProductId) && (fileOfProgram.getFile_type_id() == 1)) {
                    if (fileOfProgram.getPath().equals(defaultImagesDir)) {
                        File picFile = new File(noImageFile);
                        ImageFile.open(picFile, gridPane, imageView, "deviceImage");
                    } else {
                        File picFile = new File(fileOfProgram.getPath());
                        ImageFile.open(picFile, gridPane, imageView, "deviceImage");
                    }
                }
            });
        } else {
            File picFile = new File(noImageFile);
            ImageFile.open(picFile, gridPane, imageView, "deviceImage");

        }
    }
    // Построение дерева категорий каталога
    private void buildCategoryTree() {
        CategoriesTreeView catalogRoot = new CategoriesTreeView(0, catalogHeader, 0);
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<> (catalogRoot.getTitle());
        rootItem.setExpanded(true);
        buildTreeNode(allCategoriesList, rootItem, catalogRoot);
        categoriesTree = new TreeView<> (rootItem);
        categoriesTree.setShowRoot(false);
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> { handleCategoryTreeMouseClicked(event); };
        categoriesTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
        boolean showSpecial = false;
        treeViewContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Установить коэффициенты цен").onAction((ActionEvent arg0) -> {
                    String selectedNode = (String)((TreeItem)categoriesTree.getSelectionModel().getSelectedItem()).getValue();
                    Integer selectedNodeID = null;
                    selectedNodeID = UtilPack.getCategoryIdFromTitle(selectedNode);
                    SetRatesWindow ratesWindow = new SetRatesWindow(selectedNodeID, null);
                    ratesWindow.showModalWindow(showSpecial);
                    try { getAllProductsList(); } catch (SQLException e) {}
                    buildPricesTable(selectedProduct);
                }).build(),
                MenuItemBuilder.create().text("Создать категорию").onAction((ActionEvent arg0) -> {
                    try { newCategoryDialog("main", categoriesTree); } catch (SQLException e) {}
                    getAllCategoriesList();
                    buildCategoryTree();
                }).build(),
                MenuItemBuilder.create().text("Редактировать категорию").onAction((ActionEvent arg0) -> {
                    try { editCategoryDialog("main", categoriesTree); } catch (SQLException e) {}
                }).build(),
                MenuItemBuilder.create().text("Удалить категорию").onAction((ActionEvent arg0) -> {
                    try { deleteCategoryDialog("main", categoriesTree); } catch (SQLException e) {}
                }).build()
        ).build();
        categoriesTree.setContextMenu(treeViewContextMenu);
        categoriesTree.setCellFactory(CheckBoxTreeCell.forTreeView(new Callback<TreeItem<String>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeItem<String> param) {
                return null;
            }
        }));
        stackPane.getChildren().add(categoriesTree);
    }
    private void buildTreeNode (ObservableList<CategoriesTreeView> sections, CheckBoxTreeItem<String> rootItem, CategoriesTreeView catalogRoot) {
        sections.stream().filter((section) -> (
                section.getParent().equals(catalogRoot.getId()))).forEach((CategoriesTreeView category) -> {
            CheckBoxTreeItem<String> treeItem = new CheckBoxTreeItem<> (category.getTitle());
            treeItem.setSelected(category.getPublished() == 1 ? true : false);
            rootItem.getChildren().add(treeItem);
            rootItem.addEventHandler(CheckBoxTreeItem.<String>checkBoxSelectionChangedEvent(), new EventHandler<CheckBoxTreeItem.TreeModificationEvent<String>>() {
                public void handle(CheckBoxTreeItem.TreeModificationEvent<String> event) {

                    UtilPack.checkItemsSelected(rootItem);

                }
            });
            buildTreeNode(sections, treeItem, category);
        });
    }
    private void subCategoriesList(String selectedNode) {
        try {
            ResultSet resultSet = PCGUIController.connection.getResult(
                    "SELECT title FROM categories t1, (SELECT id FROM categories WHERE title=" +
                            "\"" + UtilPack.normalize(selectedNode) +
                            "\") t2 WHERE t2.id = t1.parent"
            );
            while (resultSet.next()) {
                subCategoriesTreeViewList.add(new CategoriesTreeView(resultSet.getString("title")));
            }
        } catch (SQLException e) {
            AlertWindow.showErrorMessage(e.getMessage());
        }
    }
    ////////////////////////////////////
    private void includeLowerItems(ObservableList<ProductsTableView> data, String selectedNode) throws SQLException {
        recursiveItems(data, UtilPack.getCategoryIdFromTitle(selectedNode));
        getProductList(UtilPack.getCategoryIdFromTitle(selectedNode)).stream().forEach((product) -> {
            excludeLowerItems(data, selectedNode);
        });
    }
    private void recursiveItems(ObservableList<ProductsTableView> data, Integer selectedNode) {
        ArrayList<Integer> children = UtilPack.arrayChildren(selectedNode);
        if(!children.isEmpty()) {
            children.stream().forEach((ch) -> {
                recursiveItems(data, ch);
            });
        } else {
            excludeLowerItems(data, UtilPack.getCategoryTitleFromId(selectedNode));
        }
    }
    private void excludeLowerItems(ObservableList<ProductsTableView> data, String selectedNode) {
        getProductList(selectedNode).stream().forEach((product) -> {
            data.add(product);
        });
    }
    ////////////////////////////////////////////////////////////////
    // Создаёт модальное окно с деревом категорий товаров для диалога переноса товаров в другую категорию.
    public TreeView<String> buildModalCategoryTree(StackPane stackPane, TreeView treeView) {
        CategoriesTreeView catalogRoot = new CategoriesTreeView(0, catalogHeader, 0);
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<> (catalogRoot.getTitle());
        rootItem.setExpanded(true);
        buildTreeNode(allCategoriesList, rootItem, catalogRoot);
        treeView = new TreeView(rootItem);
        final TreeView finalTreeView = treeView;
        final TreeView finalTreeView1 = treeView;
        final TreeView finalTreeView2 = treeView;
        ContextMenu treeViewContextMenu1 = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать категорию").onAction((ActionEvent arg0) -> {
                    try {
                        newCategoryDialog("modal", finalTreeView1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    getAllCategoriesList();
                    buildCategoryTree();
                }).build(),
                MenuItemBuilder.create().text("Редактировать категорию").onAction((ActionEvent arg0) -> {
                    try {
                        editCategoryDialog("modal", finalTreeView);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).build(),
                MenuItemBuilder.create().text("Удалить категорию").onAction((ActionEvent arg0) -> {
                    try {
                        deleteCategoryDialog("modal", finalTreeView2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).build()
        ).build();
        treeView.setContextMenu(treeViewContextMenu1);
        stackPane.getChildren().add(treeView);
        treeView.setPrefWidth(350.0);
        treeView.setPrefHeight(400.0);
        return treeView;
    }
    // Вызывает диалог переноса товавра или нескольких товаров в новую категорию.
    private void changeProductCategoryDialog() throws SQLException {
        ObservableList<ProductsTableView> selectedItems = productsTable.getSelectionModel().getSelectedItems();
        ButtonType buttonTypeOk = new ButtonType("Переместить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        TreeView<String> modalTreeView = buildModalCategoryTree(stackPaneModal, treeView);
        Optional<ButtonType> result = AlertWindow.changeProductCategoryDialog(stackPaneModal, buttonTypeOk, buttonTypeCancel);
        if ((result.isPresent()) && (result.get() == buttonTypeOk)) {
            selectedCategory = modalTreeView.getSelectionModel().getSelectedItem().getValue();
            for(ProductsTableView product: selectedItems) {
                try {
                    if(isCategoryLowest(selectedCategory)) {
                        updateCategoryId(selectedCategory, product.getTitle());
                    } else { AlertWindow.notLowestCategoryAlert(); }
                } catch (NullPointerException ignored) {
                    if(isCategoryLowest(newCatTitle)) {
                        updateCategoryId(newCatTitle, product.getTitle());
                    } else { AlertWindow.notLowestCategoryAlert(); }
                }
            }
        }
        allProductsList.clear();
        getAllProductsList();
        refreshProductsTable(selectedCategory);
        productsTable.getSelectionModel().clearAndSelect(0);
        productsTable.scrollTo(productsTable.getSelectionModel().getSelectedItem());
    }
    private boolean isCategoryLowest(String categoryTitle) throws SQLException {
        ArrayList<Categories> categories = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Categories where parent = " + UtilPack.getCategoryIdFromTitle(categoryTitle));
        List result = query.list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            Categories category = (Categories) iterator.next();
            categories.add(category);
        }
        session.close();
        if(categories.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
    // Вспомогательные методы, для смены id категории, к которой принадлежит товар.
    // Две версии для разных входных типов данных
    // Позже можно будет решить более красиво, но пока так...
    private void updateCategoryId(String value, String productTitle) throws SQLException {
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
            final Integer[] id = {0};
            allProductsList.stream().forEach(product -> {
                if(product.getTitle().equals(productTitle)) {
                    id[0] = product.getId();
                }
            });
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            /*
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            */
            if (!(id[0] == 0)) {
                Products product = (Products) session.get(Products.class, id[0]);
                try {
                    if (cat.getTitle().equals(value)) {
                        product.setCategoryId(cat);
                    }
                } catch (NullPointerException ignored) {}
                session.save(product);
            }
            session.getTransaction().commit();
            session.close();

        }
    }
    private void updateCategoryId(Integer value, String productTitle) throws SQLException {
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
            final Integer[] id = {0};
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            allProductsList.stream().forEach(product -> {
                if(product.getTitle().equals(productTitle)) {
                    id[0] = product.getId();
                }
            });
            /*
            List ids = session.createSQLQuery("select id from products where title=\"" + productTitle + "\"").list();
            for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
                id = (Integer) iterator.next();
            }
            */
            if (!(id[0] == 0)) {
                Products product = (Products) session.get(Products.class, id[0]);
                try {
                    if (cat.getId().equals(value)) {
                        product.setCategoryId(cat);
                    }
                } catch (NullPointerException ignored) {}
                session.save(product);
            }
            session.getTransaction().commit();
            session.close();
        }
    }
    private void setNewUserValue(String fieldName, String newValue, int userId) {
        try {
            int resultSet = connection.getUpdateResult("update users set " + fieldName + "=\"" + newValue.replace("\"", "\\\"") + "\" where id =" + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setNewUserGroup(String newValue, int userId) {
        try {
            int resultSet = connection.getUpdateResult("update users set group_id=(select id from groups where title =\"" + newValue.replace("\"", "\\\"") + "\") where id =" + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setNewUserCompany(String newValue, int userId) {
        try {
            int resultSet = connection.getUpdateResult("update users set company_id=(select id from companies where title =\"" + newValue.replace("\"", "\\\"") + "\")where id =" + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setNewUserPassword(String newPassword, int userId) {
        String newEncryptedPassword = Password.hashPassword(newPassword);
        try {
            int resultSet = connection.getUpdateResult("update users set encrypted_password =\"" + newEncryptedPassword + "\" where id =" + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setDealerCompanyValue(Boolean newValue, String companyTitle) {
        Integer intNewValue = newValue ? 1 : 0;
        try {
            int resultSet = connection.getUpdateResult("update companies set dealer=" + newValue + " where title =\"" + companyTitle.replace("\"", "\\\"") + "\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setNewCompanyValue(String fieldName, String newValue, String companyTitle) {
        try {
            int resultSet = connection.getUpdateResult("update companies set " + fieldName + "=\"" + newValue.replace("\"", "\\\"") + "\" where title =\"" + companyTitle.replace("\"", "\\\"") + "\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setNewGroupValue(String fieldName, String newValue, String groupTitle) {
        try {
            int resultSet = connection.getUpdateResult("update groups set " + fieldName + "=\"" + newValue.replace("\"", "\\\"") + "\" where title =\"" + groupTitle.replace("\"", "\\\"") + "\"");
        } catch (SQLException e) {
            e.printStackTrace();
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

        try {
            getAllProductsList();
            productsTable.refresh();
        } catch (SQLException e) {}
    }
    private void setNewCellValue(String fieldName, Boolean newValue, String productTitle) {
        Integer intNewValue = newValue ? 1 : 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("UPDATE Products set " + fieldName + "= :intNewValue where title= :title");
        query.setParameter("intNewValue", intNewValue);
        query.setParameter("title", productTitle);
        query.executeUpdate();
        tx.commit();
        session.close();
        try {
            getAllProductsList();
            productsTable.refresh();
        } catch (SQLException e) {}
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
        try {
            getAllProductsList();
            productsTable.refresh();
        } catch (SQLException e) {}
    }
    private void setNewQuantityValue(String fieldName, Integer newValue, Integer productId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery("from Quantity where productId=" + productId);
        List res = q.list();
        if (res.size() > 0) {
            Transaction tx = session.beginTransaction();
            Query query = session.createQuery("UPDATE Quantity set " + fieldName + "= :newValue where productId =" + productId);
            query.setParameter("newValue", newValue);
            query.executeUpdate();
            tx.commit();
            session.close();
        } else {
            Transaction tx = session.beginTransaction();
            Quantity quantity = new Quantity();
            quantity.setProductId(new Products(productId));
            quantity.setStock(0);
            quantity.setOrdered(0);
            quantity.setReserved(0);
            quantity.setMinimum(0);
            quantity.setPiecesPerPack(0);
            session.saveOrUpdate(quantity);
            tx.commit();
            session.close();
            setNewQuantityValue(fieldName, newValue, productId);
        }
        try {
            getAllProductsList();
            productsTable.refresh();
        } catch (SQLException e) {}
    }
    // Перерисовывает таблицу товаров в зависимости от выбранного пункта в дереве категорий.
    private void refreshProductsTable(String selectedCategory) {
        Integer selectedTreeId = UtilPack.getCategoryIdFromTitle(selectedCategory);
        buildProductsTable(getProductList(selectedTreeId));
    }
    // Вызывает диалог добавления нового товара из контектстного меню таблицы товаров.
    private void addProductDialog() {
        Optional<Product> result = AlertWindow.newProductDialog();
        if (result.isPresent()) {
            if ((result.get().getProductKindId() == 0) ||
                (result.get().getCategoryId() == 0)) {
                AlertWindow.fillRequiredFields();
            }
            Categories category = new Categories();
            ProductKinds productKind = new ProductKinds();
            Currencies currency = new Currencies();
            try {
                Session session1 = HibernateUtil.getSessionFactory().openSession();
                session1.beginTransaction();
                List res1 = session1.createQuery("from Categories where id = " + result.get().getCategoryId()).list();
                for (Iterator iterator = res1.iterator(); iterator.hasNext(); ) {
                    category = (Categories) iterator.next();
                }
                session1.save(category);
                session1.getTransaction().commit();
                session1.close();

                Session session3 = HibernateUtil.getSessionFactory().openSession();
                session3.beginTransaction();
                List res3 = session3.createQuery("from ProductKinds where id = " + result.get().getProductKindId()).list();
                for (Iterator iterator = res3.iterator(); iterator.hasNext(); ) {
                    productKind = (ProductKinds) iterator.next();
                }
                session3.save(productKind);
                session3.getTransaction().commit();
                session3.close();

                Session session5 = HibernateUtil.getSessionFactory().openSession();
                session5.beginTransaction();
                Query query5 = session5.createQuery("from Currencies where id = :id");
                query5.setParameter("id", result.get().getCurrencyId());
                List res5 = query5.list();
                for (Iterator iterator = res5.iterator(); iterator.hasNext(); ) {
                    currency = (Currencies) iterator.next();
                }
                session5.save(currency);
                session5.getTransaction().commit();
                session5.close();

                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                Products product = new Products();
                product.setTitle(result.get().getTitle());
                product.setCategoryId(category);
                product.setProductKindId(productKind);
                product.setDescription(result.get().getDescription());
                product.setAnons(result.get().getAnons());
                product.setArticle(result.get().getArticle());
                product.setAvailable(result.get().getAvailable());
                product.setDeliveryTime(result.get().getDeliveryTime());
                product.setEan(result.get().getEan());
                product.setOutdated(result.get().getOutdated());
                product.setPrice(result.get().getPrice());
                product.setRate(result.get().getRate());
                product.setDiscount1(result.get().getDiscount1());
                product.setDiscount2(result.get().getDiscount2());
                product.setDiscount3(result.get().getDiscount3());
                product.setRubRetail(result.get().getRubRetail());
                product.setCurrencyId(currency);
                session.save(product);
                session.getTransaction().commit();
                session.close();
                System.out.println("All over! ");
            } catch (PropertyValueException pve) {
            } catch (TransientPropertyValueException tpve) {}
        }
        try {
            allProductsList.add(result.get());
        } catch (NoSuchElementException nse) {}
    }
    // Переводит программу на отображение вкладки со свойствами выбранного товара.
    private void openProductTab() throws SQLException {
        tabPane.getSelectionModel().select(productTab);
        fillProductTab(selectedProduct);
    }
    private void fillMainTab(String selectedProduct) throws SQLException {
        final ProductsTableView[] selectedProductsTableViewItem = {new ProductsTableView()};
        final Integer[] categoryId = {0};
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        allProductsList.stream().forEach(product -> {
            if(product.getTitle().equals(selectedProduct)) {
                selectedProductsTableViewItem[0] = new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime(),
                        product.getAvailable() == 1 ? true : false,
                        product.getOutdated()  == 1 ? true : false
                );
                categoryId[0] = product.getCategoryId();
            }
        });
        allProductsList.stream().forEach(product -> {
            if(product.getCategoryId() == categoryId[0]) {
                data.add(new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime(),
                        product.getAvailable() == 1 ? true : false,
                        product.getOutdated()  == 1 ? true : false)
                );
            }
        });
        buildProductsTable(data);
        for (int i = 0; i < productsTable.getItems().size(); i++) {
            if (productsTable.getItems().get(i).getTitle().equals(selectedProduct)) {
                productsTable.getSelectionModel().clearAndSelect(i);
                productsTable.scrollTo(productsTable.getSelectionModel().getSelectedItem());
            }
        }
        productsTable.getSelectionModel().select(selectedProductsTableViewItem[0]);
        //setVendorSelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
        setCategorySelected(productsTable.getSelectionModel().getSelectedItem().getTitle());
    }
    private void newCategoryDialog(String whatTree, TreeView<String> treeView) throws SQLException {
        Optional<NewCategory> result = AlertWindow.newCategoryDialog(treeView);
        if (result.isPresent()) {
            newCatTitle = result.get().getTitle();
            newCatDescription = result.get().getDescription();
            newCatPicturePath  = result.get().getImagePath();
            createNewCategory(whatTree, treeView);
            if (whatTree.equals("main")) {
                buildCategoryTree();
            } else if (whatTree.equals("modal")) {
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal, treeView);
            }
        }
    }
    private void editCategoryDialog(String whatTree, TreeView<String> treeView) throws SQLException {
        if (whatTree.equals("main")) {
            ArrayList<String> details = getCategoryDetails(categoriesTree.getSelectionModel().getSelectedItem().getValue());
            Optional<NewCategory> result = AlertWindow.editCategoryDialog(details);
            if (result.isPresent()) {
                newCatTitle = result.get().getTitle();
                newCatDescription = result.get().getDescription();
                newCatPicturePath  = result.get().getImagePath();
                editCategory(categoriesTree.getSelectionModel().getSelectedItem().getValue(), newCatTitle, newCatDescription, newCatPicturePath);
                getAllCategoriesList();
                buildCategoryTree();
            }
        } else if (whatTree.equals("modal")) {
            ArrayList<String> details = getCategoryDetails(treeView.getSelectionModel().getSelectedItem().getValue());
            Optional<NewCategory> result = AlertWindow.editCategoryDialog(details);
            if (result.isPresent()) {
                newCatTitle = result.get().getTitle();
                newCatDescription = result.get().getDescription();
                newCatPicturePath  = result.get().getImagePath();
                editCategory(treeView.getSelectionModel().getSelectedItem().getValue(), newCatTitle, newCatDescription, newCatPicturePath);
                getAllCategoriesList();
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal, treeView);
            }
        }
    }
    private void deleteCategoryDialog(String whatTree, TreeView<String> treeView) throws SQLException {
        if (whatTree.equals("main")) {
            String catTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
            Optional<ButtonType> result = AlertWindow.categoryDeleteAttention(catTitle);
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                Integer parentCatId = UtilPack.getParentCatId(catTitle);
                Integer catId = UtilPack.getCategoryIdFromTitle(catTitle);
                replaceProductsUp(catId, parentCatId);
                deleteCategory(catTitle);
                getAllCategoriesList();
                buildCategoryTree();
                ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
                productsTable.setItems(data);
            }
        } else if (whatTree.equals("modal")) {
            String catTitle = treeView.getSelectionModel().getSelectedItem().getValue();
            Optional<ButtonType> result = AlertWindow.categoryDeleteAttention(catTitle);
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                Integer parentCatId = UtilPack.getParentCatId(catTitle);
                Integer catId = UtilPack.getCategoryIdFromTitle(catTitle);
                replaceProductsUp(catId, parentCatId);
                deleteCategory(catTitle);
                getAllCategoriesList();
                buildCategoryTree();
                buildModalCategoryTree(stackPaneModal, treeView);
                ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
                productsTable.setItems(data);
            }
        }
    }
    private void createNewCategory(String whatTree, TreeView<String> treeView) throws SQLException {
        if (whatTree.equals("main")) {
            parentCategoryTitle = categoriesTree.getSelectionModel().getSelectedItem().getValue();
        } else if (whatTree.equals("modal")) {
            parentCategoryTitle = treeView.getSelectionModel().getSelectedItem().getValue();
        }
        parentCategoryTitle = treeView.getSelectionModel().getSelectedItem().getValue();
        Integer parentId = UtilPack.getCategoryIdFromTitle(parentCategoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Categories category = new Categories();
        category.setTitle(newCatTitle);
        category.setSummary(newCatDescription);
        category.setImagePath(newCatPicturePath);
        category.setParent(parentId);
        category.setPublished(0);
        try {
            session.save(category);
        } catch (ConstraintViolationException e) {
            AlertWindow.showErrorMessage("Создание категории не удалось, возможно, имя новой категории не уникально.");
        }
        tx.commit();
        session.close();
        if (newCatPicturePath.contains(".jpg") ||
                newCatPicturePath.contains(".JPG") ||
                newCatPicturePath.contains(".png") ||
                newCatPicturePath.contains(".PNG") ||
                newCatPicturePath.contains(".gif") ||
                newCatPicturePath.contains(".GIF")) {
            String targetRemoteCatPixFolder = "/var/www/poligon/data/www/poligon.info/images/design/categories/" +
                    UtilPack.getCategoryVendorFromId(UtilPack.getCategoryIdFromTitle(parentCategoryTitle)) + "/";
            UtilPack.startFTP(newCatPicturePath, targetRemoteCatPixFolder);
        }
    }
    private void editCategory(String categoryTitle, String newTitle, String NewDescription, String NewImagePath) throws SQLException {
        Integer id = UtilPack.getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Categories set title = :title, summary = :summary, imagePath = :imagePath where id = :id");
        query.setParameter("title", newTitle);
        query.setParameter("summary", NewDescription);
        query.setParameter("imagePath", NewImagePath);
        query.setParameter("id", id);
        query.executeUpdate();
        tx.commit();
        session.close();
        if (NewImagePath.contains(".jpg") ||
                NewImagePath.contains(".JPG") ||
                NewImagePath.contains(".png") ||
                NewImagePath.contains(".PNG") ||
                NewImagePath.contains(".gif") ||
                NewImagePath.contains(".GIF") &&
                !NewImagePath.equals(UtilPack.getCategoryImagePathFromTitle(categoryTitle))) {
            String targetRemoteCatPixFolder = "/var/www/poligon/data/www/poligon.info/images/design/categories/" +
                    UtilPack.getCategoryVendorFromId(id) + "/";
            UtilPack.startFTP(NewImagePath, targetRemoteCatPixFolder);
        }
    }
    private void deleteCategory(String categoryTitle) throws SQLException {
        Integer id = UtilPack.getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("delete Categories where id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
        tx.commit();
        session.close();
    }
    private ArrayList<String> getCategoryDetails(String categoryTitle) throws SQLException {
        ArrayList<String> details = new ArrayList<>();
        Integer id = UtilPack.getCategoryIdFromTitle (categoryTitle);
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From Categories where id=" + id).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                Categories category = (Categories) iterator.next();
                details.add(category.getTitle());
                details.add(category.getSummary());
                details.add(category.getImagePath());
                details.add(UtilPack.getCategoryVendorFromId(category.getParent()));
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        return details;
    }
    private void replaceProductsUp(Integer catId, Integer parentCatId) throws SQLException {
        ArrayList<String> productsUp = new ArrayList<>(100);
        allProductsList.stream().forEach(product -> {
            if(product.getCategoryId() == catId) {
                productsUp.add(product.getTitle());
            }
        });
        /*
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
        */
        for (String product: productsUp) {
            updateCategoryId(parentCatId, product);
        }
    }
    @FXML private void onFocusedProductTableItem(String selectedProduct) {
        try {
            buildPricesTable(selectedProduct);
            buildQuantityTable(selectedProduct);
            buildDeliveryTimeTable(selectedProduct);
            buildAnalogsTable(selectedProduct);
            buildDatasheetFileTable(selectedProduct);
            buildAccessoriesTable(selectedProduct);
            buildImageView(selectedProduct);
            datasheetFileTable.refresh();
            setCategorySelected(selectedProduct);
        } catch (NullPointerException | SQLException ignored) {
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
        Optional<NewVendor> result = AlertWindow.newVendorDialog(currencies);
        if (result.isPresent()) {
            newVendorTitle = result.get().getTitle();
            newVendorDescription = result.get().getDescription();
            newVendorCurrency = result.get().getCurrency();
            newVendorAddress = result.get().getAddress();
            newVendorRate = result.get().getRate();
            createNewVendor();
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
    private void addSerieDialog() {
        Optional<NewSerie> result = AlertWindow.newSerieDialog();
        if (result.isPresent()) {
            Vendors vendor = new Vendors();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery("from Vendors where title = :title");
            q.setParameter("title", result.get().getVendor());
            List res = q.list();
            for(Iterator iterator = res.iterator(); iterator.hasNext();) {
                vendor = (Vendors) iterator.next();
            }
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();
            SeriesItems seriesItem = new SeriesItems();
            seriesItem.setTitle(result.get().getTitle());
            seriesItem.setDescription(result.get().getDescription());
            seriesItem.setVendorId(vendor);
            session1.save(seriesItem);
            tx.commit();
            session1.close();
        }
    }
    private void editSerieDialog(String selectedSerie) {
        Optional<NewSerie> result = AlertWindow.editSerieDialog(selectedSerie);
        if (result.isPresent()) {
            SeriesItems seriesItem = new SeriesItems();
            Session session0 = HibernateUtil.getSessionFactory().openSession();
            Query q0 = session0.createQuery("from SeriesItems where title = :title");
            q0.setParameter("title", result.get().getTitle());
            List res0 = q0.list();
            for(Iterator iterator = res0.iterator(); iterator.hasNext();) {
                seriesItem = (SeriesItems) iterator.next();
            }
            session0.close();
            Vendors vendor = new Vendors();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query q = session.createQuery("from Vendors where title = :title");
            q.setParameter("title", result.get().getVendor());
            List res = q.list();
            for(Iterator iterator = res.iterator(); iterator.hasNext();) {
                vendor = (Vendors) iterator.next();
            }
            session.close();
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session1.beginTransaction();
            seriesItem.setTitle(result.get().getTitle());
            seriesItem.setDescription(result.get().getDescription());
            seriesItem.setVendorId(vendor);
            session1.saveOrUpdate(seriesItem);
            tx.commit();
            session1.close();
        }
    }
    private void deleteSerieDialog(String selectedSerie) {

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
                } catch (NullPointerException ignored) {}
            }
            session.save(vendor);
            session.getTransaction().commit();
            session.close();
        }
    }
    private void setCategorySelected(String selectedProduct) throws SQLException {
        CheckBoxTreeItem<String> productOwner = new CheckBoxTreeItem<>();
        final Categories[] category = {new Categories()};
        allProductsList.stream().forEach(product -> {
            if (product.getTitle().equals(selectedProduct)) {
                category[0] = new Categories(product.getCategoryId());
            }
        });
        try {
            recursiveTreeCall(categoriesTree.getRoot(), UtilPack.getCategoryTitleFromId(category[0].getId()));
        } catch (NullPointerException ignored) {}

    }
    private void recursiveTreeCall(TreeItem<String> root, String categoryTitle) {
        TreeItem<String> productOwner = new CheckBoxTreeItem<>();
        for (TreeItem<String> item: root.getChildren()) {
            if (item.getValue().equals(categoryTitle)) {
                productOwner = item;
                categoriesTree.getSelectionModel().select(item);
                if (!item.getParent().equals(null)) {
                    item.getParent().setExpanded(true);
                    expandParents(item.getParent());
                    categoriesTree.scrollTo(categoriesTree.getRow(item));
                }
            } else {
                recursiveTreeCall(item, categoryTitle);
            }
        }
    }
    private ArrayList<TreeItem<String>> expandParents(TreeItem<String> owner) {
        ArrayList<TreeItem<String>> allParents = new ArrayList<>();
        try {
            if (!owner.getParent().equals(null)) {
                owner.getParent().setExpanded(true);
                allParents.add(owner.getParent());
                expandParents(owner.getParent());
            }
        } catch (NullPointerException ignored) {}
        return allParents;
    }
    private void setDatasheetFile() throws SQLException {
        fileChooser.setInitialDirectory(new File(fileChooserDirectoryCash));
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            List pdfList = session.createQuery("From Files where ownerId=" +
                    UtilPack.getProductIdFromTitle(selectedProduct, allProductsList) + "and fileTypeId=2").list();
            if (pdfList.isEmpty()) {
                Files pdfFile = new Files(file.getName(), file.getPath(), "Это даташит для " +
                        selectedProduct, (new FileTypes(2)), (new Products(UtilPack.getProductIdFromTitle(selectedProduct, allProductsList))));
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
            fileChooserDirectoryCash = makeCorrectDirectoryPath(file.getPath());
            System.out.println("fileChooserDirectoryCash is " + fileChooserDirectoryCash);
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
    private String makeCorrectDirectoryPath(String fullPath) {
        String listString = "";
        ArrayList<String> pathParts = new ArrayList<>();
        for (int i = 0; i < fullPath.split("\\\\").length; i++) {
            pathParts.add(fullPath.split("\\\\")[i]);
        }
        pathParts.remove(pathParts.size() - 1);
        for (String s : pathParts) {
            listString += s + "\\";
        }
        return listString;
    }
    @FXML private void handleCategoryTreeMouseClicked(MouseEvent event) {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        // Вызываем метод, возврщающий нам название кликнутого узла
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            String selectedNode = (String) ((CheckBoxTreeItem) categoriesTree.getSelectionModel().getSelectedItem()).getValue();
            subCategoriesList(selectedNode);
            if (treeViewHandlerMode.isSelected()) {
                try {
                    includeLowerItems(data, selectedNode);
                } catch (SQLException e) {
                    AlertWindow.showErrorMessage(e.getMessage());
                }
            } else {
                excludeLowerItems(data, selectedNode);
            }
            buildProductsTable(data);
            if (!productsTable.getSelectionModel().isEmpty()) {
                productsTable.getSelectionModel().select(0);
                focusedProduct = productsTable.getSelectionModel().getSelectedItem().getTitle();
                onFocusedProductTableItem(selectedProduct);
            }
        }
    }
    @FXML private void handleProductTableMousePressed(MouseEvent event1) {
        onFocusedProductTableItem(selectedProduct);
        if (productsTable.getSelectionModel().getSelectedItems().size() == 1) {
            boolean showSpecial = true;
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Установить коэффициенты цен").onAction((ActionEvent arg0) -> {
                        Integer selectedProductID = null;
                        selectedProductID = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
                        SetRatesWindow ratesWindow = new SetRatesWindow(null, selectedProductID);
                        ratesWindow.showModalWindow(showSpecial);
                        try {
                            getAllProductsList();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        buildPricesTable(selectedProduct);
                    }).build(),
                    MenuItemBuilder.create().text("Открыть вкладку обзора свойств устройства").onAction((ActionEvent arg0) -> {
                        try {
                            openProductTab();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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
                        try {
                            loadPdfFile(productsTable.getSelectionModel().getSelectedItem().getTitle());
                        } catch (SQLException e) {}
                    }).build(),
                    MenuItemBuilder.create().text("Установить изображение...").onAction((ActionEvent arg0) -> {
                        changePictureForSelectedProducts();
                    }).build(),
                    MenuItemBuilder.create().text("Изменить производителя устройства").onAction((ActionEvent arg0) -> {
                        ContextBuilder.changeProductVendor(productsTable);
                        try {
                            getAllProductsList();
                            fillMainTab(selectedProduct);
                        } catch (SQLException e) {}
                    }).build(),
                    MenuItemBuilder.create().text("Изменить тип устройства").onAction((ActionEvent arg0) -> {
                        ContextBuilder.changeProductKind(productsTable);
                        try {
                            getAllProductsList();
                            fillMainTab(selectedProduct);
                        } catch (SQLException e) {}
                    }).build(),
                    MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                        try {changeProductCategoryDialog();} catch (SQLException e) {}
                    }).build(),
                    MenuItemBuilder.create().text("Добавить элемент...").onAction((ActionEvent arg0) -> {
                        addProductDialog();
                        try {
                            getAllProductsList();
                            fillMainTab(selectedProduct);
                            productsTable.refresh();
                        } catch (SQLException e) {}
                    }).build(),
                    MenuItemBuilder.create().text("Удалить элемент...").onAction((ActionEvent arg0) -> {
                        ContextBuilder.deleteTheProduct(selectedProduct);
                        try {
                            getAllProductsList();
                            fillMainTab(selectedProduct);
                            productsTable.refresh();
                            categoriesTree.getSelectionModel().select(categoriesTree.getSelectionModel().getSelectedItem());
                        } catch (SQLException e) {}
                    }).build(),
                    SeparatorMenuItemBuilder.create().build(),
                    MenuItemBuilder.create().text("Добавить аксессуар к выбранному устройству").onAction((ActionEvent arg0) -> {
                        Accessory.addToSelectedOn(productsTable);
                        fillProductTab(selectedProduct);
                    }).build()
            ).build();
        } else if(productsTable.getSelectionModel().getSelectedItems().size() == 0) {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Импортировать элементы...").onAction((ActionEvent arg0) -> {
                        tabPane.getSelectionModel().select(settingsTab);}).build()
            ).build();
        } else {
            productTableContextMenu = ContextMenuBuilder.create().items(
                    MenuItemBuilder.create().text("Установить изображение...").onAction((ActionEvent arg0) -> {
                        changePictureForSelectedProducts();
                    }).build(),
                MenuItemBuilder.create().text("Переместить в категорию...").onAction((ActionEvent arg0) -> {
                    try {
                        changeProductCategoryDialog();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранные элементы").onAction((ActionEvent arg0) -> {

                    ContextBuilder.deleteSelectedProducts(productsTable);
                    try {
                        getAllProductsList();
                        fillMainTab(selectedProduct);
                        productsTable.refresh();
                        categoriesTree.getSelectionModel().select(categoriesTree.getSelectionModel().getSelectedItem());
                    } catch (SQLException e) {}

                ;}).build(),
                SeparatorMenuItemBuilder.create().build(),
                MenuItemBuilder.create().text("Изменить производителя устройства").onAction((ActionEvent arg0) -> {
                    ContextBuilder.changeProductVendor(productsTable);
                    try {
                        getAllProductsList();
                        fillMainTab(selectedProduct);
                    } catch (SQLException e) {}
                }).build(),
                MenuItemBuilder.create().text("Изменить тип устройства").onAction((ActionEvent arg0) -> {
                    ContextBuilder.changeProductKind(productsTable);
                    try {
                        getAllProductsList();
                        fillMainTab(selectedProduct);
                    } catch (SQLException e) {}
                }).build(),
                MenuItemBuilder.create().text("Добавить аксессуар к выбранным устройствам").onAction((ActionEvent arg0) -> {
                    Accessory.addToSelectedOn(productsTable);
                }).build()
            ).build();
        }
        productsTable.setContextMenu(productTableContextMenu);
        //fillProductTab(selectedProduct);
    }
    private void changePictureForSelectedProducts() {
        final ArrayList<String> selectedProductsTitles = new ArrayList<>();
        productsTable.getSelectionModel().getSelectedItems().stream().forEach((item) -> {
            try {
                selectedProductsTitles.add(item.getTitle());
            } catch (NullPointerException ignored) {
                AlertWindow.showErrorMessage("Ошибка ввода данных.");
            }
        });
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            Task task = new Task<Void>() {
                @Override public Void call() {
                    int i = 2;
                    String tempFileName = new File(ImageFile.makeTemporaryResizedImage(file)).getName();
                    String tempFilePath = PoligonCommander.tmpDir + "\\" + tempFileName;
                    for (String title : selectedProductsTitles) {
                        ImageFile.save(new File(tempFilePath), title);
                        updateProgress(i, selectedProductsTitles.size());
                        i++;
                    }
                    updateProgress(0, 0);
                    Platform.runLater(() -> {
                        try {
                            resetProgram();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        AlertWindow.showInfo("Готово!");
                    });
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        }
        try { getAllFilesOfProgramList(); } catch (SQLException e) {}
    }
    private Task<Void> deleteProductsTask() {
        return new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {

                populateComboBox();

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
    @FXML private void handleSearchComboBox() throws SQLException {
        ObservableList<ProductsTableView> data = FXCollections.observableArrayList();
        final String[] title = {""};
        allProductsList.stream().forEach(product -> {
            if (product.getTitle().equals(searchComboBox.getValue())) {
                data.add(new ProductsTableView(
                        product.getArticle(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getDeliveryTime(),
                        product.getAvailable() == 1 ? true : false,
                        product.getOutdated()  == 1 ? true : false)
                );
                title[0] = product.getTitle();
            }
        });
        buildProductsTable(data);
        productsTable.getSelectionModel().select(0);
        //setVendorSelected(title[0]);
        setCategorySelected(title[0]);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Таб "Страница товара" ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String dimsImagePathFromTitle (String productTitle) {
        String validTitle = "";
        validTitle = defaultImagesDir + "catalog\\" + UtilPack.getVendorFromProductTitle(productTitle) + "\\dims\\" + productTitle.replace(" ", "_").replace("/", "_") + "_dim.jpg";
        return validTitle;
    }
    private String plugsImagePathFromTitle (String productTitle, int plugNumber) {
        String validTitle = "";
        validTitle = defaultImagesDir + "catalog\\" + UtilPack.getVendorFromProductTitle(productTitle) + "\\plugs\\" + productTitle.replace(" ", "_").replace("/", "_") + "_plug"+ plugNumber +".jpg";
        return validTitle;
    }
    // Заполняет заголовок вкладки свойств товара названием текущего выбранного товара,
    // а также отображает картинку товара, еасли она определена.
    private void fillProductTab(String selectedProduct) {
        ObservableList<ProductPropertiesForKindTableView> pList = FXCollections.observableArrayList();
        ObservableList<FunctionsTableView> fList = FXCollections.observableArrayList();

        if (selectedProduct == null) {
            pList.add(new ProductPropertiesForKindTableView("", "", "", "", ""));
        } else {
            ArrayList<Properties> properties = new ArrayList<>(10);
            ArrayList<PropertyValues> propertyValues = new ArrayList<>(10);
            String pkTitle = getProductKindTitle(selectedProduct);
            int productKindId = UtilPack.getProductKindIdFromTitle(pkTitle);

            Session session1 = HibernateUtil.getSessionFactory().openSession();
            List res1 = session1.createQuery(
                    "from Properties where productKindId =" + productKindId
            ).list();
            for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
                Properties property = (Properties) iterator.next();
                properties.add(property);
            }
            List res2 = session1.createQuery(
                    "from PropertyValues where productId =" + UtilPack.getProductIdFromTitle(selectedProduct, allProductsList)
            ).list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                PropertyValues pv = (PropertyValues) iterator.next();
                propertyValues.add(pv);
            }
            session1.close();

            for (Properties p : properties) {
                String value = "";
                for (PropertyValues pv: propertyValues) {
                    if (Objects.equals(pv.getPropertyId().getId(), p.getId())) {
                        value = pv.getValue();
                    }
                }
                pList.add(new ProductPropertiesForKindTableView(
                        p.getOrderNumber().toString(),
                        p.getTitle(),
                        p.getOptional(),
                        p.getSymbol(),
                        value
                ));
            }
        }
        productPropertiesForKindOrderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        productPropertiesForKindTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        productPropertiesForKindOptionalColumn.setCellValueFactory(new PropertyValueFactory<>("optional"));
        productPropertiesForKindSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        productPropertiesForKindValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        productPropertiesForKindValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        productPropertiesForKindValueColumn.setOnEditCommit(
                t -> {
                    int rowNumber = t.getTablePosition().getRow();
                    String propertyOrderNumber = t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber();
                    String propertyTitle = t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle();
                    (t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue(t.getNewValue());
                    setPropertyValueForProduct(t.getNewValue(), propertyOrderNumber, propertyTitle, selectedProduct);
                    t.getTableView().getSelectionModel().selectBelowCell();
                    t.getTableView().edit(rowNumber + 1, t.getTableView().getEditingCell().getTableColumn());
                }
        );
        productPropertiesForKindTable.setItems(pList);

        functionsTable1.setItems(fList);
        functionDescriptionTextArea.setText("");
        try {
            int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
            ImageFile.open(new File(noImageFile), productDimsGridPane, productDimsImageView, "dimsImage");
            Session session = HibernateUtil.getSessionFactory().openSession();
            List pics = session.createQuery("from Files where ownerId=" + selectedProductId + " and fileTypeId=" + 4).list();
            if (pics.size()==0) {
                try {
                    ImageFile.open(new File(dimsImagePathFromTitle(selectedProduct)), productDimsGridPane, productDimsImageView, "dimsImage");
                } catch (Exception e) {
                    ImageFile.open(new File(noImageFile), productDimsGridPane, productDimsImageView, "dimsImage");
                }
            } else {
                for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                    Files pic = (Files) iterator.next();
                    File picFile = new File(pic.getPath());
                    if (pic.getName() != "" || pic.getName() != null) {
                        ImageFile.open(picFile, productDimsGridPane, productDimsImageView, "dimsImage");
                    } else {
                        ImageFile.open(new File(noImageFile), productDimsGridPane, productDimsImageView, "dimsImage");
                    }

                }
            }
            session.close();
        } catch (NullPointerException ignored) {}

        try {
            int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
            ImageFile.open(new File(noImageFile), plug1GridPane, plug1ImageView, "plugsImage");
            plug1TextArea.setText("");
            Session session = HibernateUtil.getSessionFactory().openSession();
            List pics = session.createQuery("from Files where ownerId=" + selectedProductId + " and fileTypeId=" + 3).list();
            if (pics.size()==0) {
                try {
                    ImageFile.open(new File(plugsImagePathFromTitle(selectedProduct, 1)), plug1GridPane, plug1ImageView, "plugsImage");
                } catch (Exception e) {
                    ImageFile.open(new File(noImageFile), plug1GridPane, plug1ImageView, "plugsImage");
                }
                plug1TextArea.setText("");
            } else {
                for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                    Files pic = (Files) iterator.next();
                    File picFile = new File(pic.getPath());
                    if (pic.getName() != "" || pic.getName() != null) {
                        ImageFile.open(picFile, plug1GridPane, plug1ImageView, "plugsImage");
                    } else {
                        ImageFile.open(new File(noImageFile), plug1GridPane, plug1ImageView, "plugsImage");
                    }
                }
                plug1TextArea.setText("");
            }
            session.close();
        } catch (NullPointerException ignored) {}

        try {
            int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
            ImageFile.open(new File(noImageFile), plug2GridPane, plug2ImageView, "plugsImage");
            plug2TextArea.setText("");
            Session session = HibernateUtil.getSessionFactory().openSession();
            List pics = session.createQuery("from Files where ownerId=" + selectedProductId + " and fileTypeId=" + 7).list();
            if (pics.size()==0) {
                try {
                    ImageFile.open(new File(plugsImagePathFromTitle(selectedProduct, 2)), plug2GridPane, plug2ImageView, "plugsImage");
                } catch (Exception e) {
                    ImageFile.open(new File(noImageFile), plug2GridPane, plug2ImageView, "plugsImage");
                }
                plug2TextArea.setText("");
            } else {
                for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                    Files pic = (Files) iterator.next();
                    File picFile = new File(pic.getPath());
                    if (pic.getName() != "" || pic.getName() != null) {
                        ImageFile.open(picFile, plug2GridPane, plug2ImageView, "plugsImage");
                    } else {
                        ImageFile.open(new File(noImageFile), plug2GridPane, plug2ImageView, "plugsImage");
                    }
                }
                plug2TextArea.setText("");
            }
            session.close();
        } catch (NullPointerException ignored) {}


        ImageFile.open(new File(noImageFile), functionGridPaneImageView, functionImageView, "functionImage");
        try {
            productTabTitle.setText(selectedProduct);
            productTabKind.setText(getProductKindTitle(selectedProduct));
            int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);
            ImageFile.open(new File(noImageFile), productTabGridPaneImageView, productTabImageView, "functionImage");
            picDescriptionTextArea.setText("");
            Session session = HibernateUtil.getSessionFactory().openSession();
            List pics = session.createQuery("from Files where ownerId=" + selectedProductId + " and fileTypeId=" + 1).list();
            if (pics.size()==0) {
                ImageFile.open(new File(noImageFile), productTabGridPaneImageView, productTabImageView, "functionImage");
                picDescriptionTextArea.setText("");
            } else {
                for (Iterator iterator = pics.iterator(); iterator.hasNext();) {
                    Files pic = (Files) iterator.next();
                    File picFile = new File(pic.getPath());
                    ImageFile.open(picFile, productTabGridPaneImageView, productTabImageView, "functionImage");
                    picDescriptionTextArea.setText(pic.getDescription());
                }
            }
            session.close();
        } catch (NullPointerException ignored) {}
        buildAccessoriesTable(selectedProduct);
        buildFunctionsTable1(selectedProduct);
        setSelectFunction();
    }
    private void setPropertyValueForProduct(String newValue, String propertyOrderNumber, String propertyTitle, String selectedProduct) {
        ArrayList<Integer> propertyIds = UtilPack.getPropertyIdFromTitle(propertyTitle);
        ArrayList<Properties> properties = new ArrayList<>();
        int selectedProductId = UtilPack.getProductIdFromTitle(selectedProduct, allProductsList);

        for(Integer id : propertyIds ) {
            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List result2 = session2.createQuery("from Properties where id=" + id).list();
            for (Iterator iterator = result2.iterator(); iterator.hasNext(); ) {
                Properties p = (Properties) iterator.next();
                properties.add(p);
            }
            session2.close();
        }
        for(Properties p : properties ) {
            PropertyValues currentPropertyValue = null;
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            List result = session1.createQuery("from PropertyValues where propertyId=" + p.getId() + " and productId=" + selectedProductId).list();
            for (Iterator iterator = result.iterator(); iterator.hasNext(); ) {
                currentPropertyValue = (PropertyValues) iterator.next();
            }
            session1.close();
            if (currentPropertyValue == null) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction tx = session.beginTransaction();
                PropertyValues pv = new PropertyValues();
                pv.setValue(newValue);
                pv.setProductId(new Products(selectedProductId));
                pv.setPropertyId(new Properties(p.getId()));
                session.save(pv);
                tx.commit();
                session.close();
            } else {
                if (p.getOrderNumber().toString().equals(propertyOrderNumber)) {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tx = session.beginTransaction();
                    currentPropertyValue.setValue(newValue);
                    session.saveOrUpdate(currentPropertyValue);
                    tx.commit();
                    session.close();
                }
            }
        }
    }
    // Сохраняет в БД описание картинки товара.
    @FXML private void changePicDescription() throws SQLException {
        String product = productTabTitle.getText();
        String picDescription = picDescriptionTextArea.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Files set description = :description" + " where ownerId =" +
                UtilPack.getProductIdFromTitle(product, allProductsList));
        query.setParameter("description", picDescription);
        int result = query.executeUpdate();
        tx.commit();
        session.close();
    }
    @FXML private void setPlug1ImageDescButtonPress() throws SQLException {
        String product = productTabTitle.getText();
        String picDescription = plug1TextArea.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Files set description = :description" + " where ownerId =" +
                UtilPack.getProductIdFromTitle(product, allProductsList) + " and fileTypeId = " + 3 );
        query.setParameter("description", picDescription);
        int result = query.executeUpdate();
        tx.commit();
        session.close();
    }
    @FXML private void setPlug2ImageDescButtonPress() throws SQLException {
        String product = productTabTitle.getText();
        String picDescription = plug1TextArea.getText();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Files set description = :description" + " where ownerId =" +
                UtilPack.getProductIdFromTitle(product, allProductsList) + " and fileTypeId = " + 7 );
        query.setParameter("description", picDescription);
        int result = query.executeUpdate();
        tx.commit();
        session.close();
    }
    private ObservableList<ProductPropertiesForKindTableView> buildProductPropertiesForKindTable(String selectedProduct) {
        Products product = new Products();
        ObservableList<ProductPropertiesForKindTableView> productPropertiesForKind = FXCollections.observableArrayList();
        ArrayList<Properties> properties = new ArrayList<>(10);
        ArrayList<PropertyValues> propertyValues = new ArrayList<>(10);

        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery(
                "from Products where title =\"" + selectedProduct + "\""
        ).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            product = (Products) iterator.next();
        }
        session.close();

        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List res1 = session1.createQuery(
                "from Properties where productKindId =" + product.getProductKindId().getId()
        ).list();
        for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
            Properties property = (Properties) iterator.next();
            properties.add(property);
        }
        session1.close();

        Session session2 = HibernateUtil.getSessionFactory().openSession();
        List res2 = session2.createQuery(
                "from PropertyValues"
        ).list();
        for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
            PropertyValues propertyValue = (PropertyValues) iterator.next();
            propertyValues.add(propertyValue);
        }
        session2.close();

        productPropertiesForKind.add(new ProductPropertiesForKindTableView(
                "2",
                "2",
                "2",
                "2",
                "2"
        ));


        return productPropertiesForKind;
    }
    private void buildFunctionsTable1(String selectedProduct) {
        ArrayList<Functions> functions = new ArrayList<>(10);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from ProductsFunctions where productId =" +
                UtilPack.getProductIdFromTitle(selectedProduct, allProductsList)).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            ProductsFunctions func = (ProductsFunctions) iterator.next();
            functions.add(func.getFunctionId());
        }
        session.close();
        ObservableList<FunctionsTableView> list = FXCollections.observableArrayList();
        functions.stream().forEach((f) -> { list.add(new FunctionsTableView(f.getTitle(), f.getSymbol(), f.getId())); });
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
                    ContextBuilder.removeFunctionFromProduct(functionsTable1, productTabTitle, functionDescriptionTextArea,
                            noImageFile, functionGridPaneImageView, functionImageView);
                    buildFunctionsTable1(selectedProduct);
                }).build()
        ).build();
        functionsTable1.setContextMenu(functionsTable1ContextMenu);
        functionsTable1.setItems(list);
    };
    private void setFunctionPicture(String functionPicturePath) {
        File picFile = new File(functionPicturePath);
        ImageFile.open(picFile, functionGridPaneImageView, functionImageView, "functionImage");
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
            ImageFile.open(file, gridPane, imageView, "deviceImage");
            ImageFile.open(file, productTabGridPaneImageView, productTabImageView, "deviceImage");
            ImageFile.save(file, selectedProduct);
        }
        try { getAllFilesOfProgramList(); } catch (SQLException e) {}
        fillProductTab(selectedProduct);
    }
    @FXML private void setDimsImageButtonPress() {
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            ImageFile.open(file, productDimsGridPane, productDimsImageView, "dimsImage");
            ImageFile.saveDimImage(file, selectedProduct);
        }
    }
    @FXML private void setPlug1ImageButtonPress() {
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            ImageFile.open(file, plug1GridPane, plug1ImageView, "plugsImage");
            ImageFile.savePlugImage(file, selectedProduct, 1);
        }
    }
    @FXML private void setPlug2ImageButtonPress() {
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            ImageFile.open(file, plug2GridPane, plug2ImageView, "plugsImage");
            ImageFile.savePlugImage(file, selectedProduct, 2);
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
        } catch (NullPointerException ignored) {}
    }
    private String getProductKindTitle(String selectedProductTitle) {
        final int[] selectedProductKindID = {0};
        String productTabKindText = "";
        allProductsList.stream().forEach(product -> {
            if(product.getTitle().equals(selectedProductTitle)) {
                selectedProductKindID[0] = product.getProductKindId();
            }
        });
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        try {
            List res1 = session1.createQuery("from ProductKinds where id = " + selectedProductKindID[0]).list();
            for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
                ProductKinds productKind = (ProductKinds) iterator.next();
                productTabKindText = productKind.getTitle();
            }
        } catch (JDBCConnectionException jdbce) {
            AlertWindow.showError();
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
                        buildFunctionsTable1(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                        try {
                            setSelectFunction();
                        } catch (NullPointerException ignored) {}
                        fillProductTab(accessoriesTable.getSelectionModel().getSelectedItem().getTitle());
                    }
                }
            }
        });
        ArrayList<Integer> paIds = new ArrayList<>(10);
        ArrayList<Product> aProducts = new ArrayList<>(10);
        ObservableList<AccessoriesTableView> data = FXCollections.observableArrayList();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            List response = session.createQuery("From ProductsAccessories where productId=" + UtilPack.getProductIdFromTitle(selectedProduct, allProductsList)).list();
            for (Iterator iterator = response.iterator(); iterator.hasNext();) {
                ProductsAccessories pa = (ProductsAccessories) iterator.next();
                paIds.add(pa.getAccessoryId());
            }
        } catch (HibernateException e) {
        } finally {
            session.close();
        }
        for (Integer id: paIds) {
            aProducts.addAll(allProductsList.stream().filter(product -> product.getId() == id).collect(Collectors.toList()));
        }
        data.addAll(aProducts.stream().map(p -> new AccessoriesTableView(p.getTitle(), p.getDescription(), p.getId())).collect(Collectors.toList()));
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
    private void loadPdfFile(String product) throws SQLException {
        final String[] pdfFilePath = {new String()};
        pdfTabTitle.setText(product);
        int productId = UtilPack.getProductIdFromTitle(product, allProductsList);
        allFilesOfProgramList.stream().forEach(fileOfProgram -> {
            if((fileOfProgram.getOwner_id() == productId) && (fileOfProgram.getFile_type_id() == 2)) {
                pdfFilePath[0] = fileOfProgram.getPath();
            }
        });
        File file = new File(pdfFilePath[0]);
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
    @FXML private void handleCategorySearch() {
        String searchRequest = categorySearch.getText();
        ObservableList<String> categoryTitles = categoriesListView.getItems();
        for(String cat: categoryTitles) {
            if (cat.equals(searchRequest)) {
                categoriesTitledPane.setExpanded(true);
                categoriesListView.scrollTo(cat);
                categoriesListView.getSelectionModel().select(cat);
                editSelectedCategory();
            }
        }
    }
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
        } else if (itemsType.equals("videos")) {
            List res = session.createQuery("from Videos").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                Videos video = (Videos) iterator.next();
                items.add(new String(video.getTitle()));
            }
        } else if (itemsType.equals("reviews")) {
            List res = session.createQuery("from Reviews").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                Reviews review = (Reviews) iterator.next();
                items.add(new String(review.getTitle()));
            }
        } else if (itemsType.equals("additions")) {
            List res = session.createQuery("from Additions").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                Additions addition = (Additions) iterator.next();
                items.add(new String(addition.getTitle()));
            }
        } else if (itemsType.equals("contents")) {
            List res = session.createQuery("from StaticContents").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                StaticContents staticContent = (StaticContents) iterator.next();
                items.add(new String(staticContent.getTitle()));
            }
        } else if (itemsType.equals("categories")) {
            List res = session.createQuery("from Categories").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                Categories categories = (Categories) iterator.next();
                items.add(new String(categories.getTitle()));
            }
        }
        session.close();
        if(items.isEmpty()) {
            items.add("");
        }
        return items;
    }
    private void populateContentSiteLists() {
        ObservableList<String> emptyList = FXCollections.emptyObservableList();
        //emptyList.add("");
        newsItemsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать новость").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeNewsItem();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную новость").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteNewsItem(newsListView);
                    populateContentSiteLists();
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
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
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
                }).build()
        ).build();
        videosListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать статью").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeVideo();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную статью").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteVideo(videosListView);
                    populateContentSiteLists();
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
                }).build()
        ).build();
        reviewsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать статью").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeReview();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную статью").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteReview(reviewsListView);
                    populateContentSiteLists();
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
                }).build()
        ).build();
        additionsListContextMenu = ContextMenuBuilder.create().items(
                MenuItemBuilder.create().text("Создать статью").onAction((ActionEvent ae1) -> {
                    ContextBuilder.makeAddition();
                    populateContentSiteLists();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную статью").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteAddition(additionsListView);
                    populateContentSiteLists();
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
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
                    pageTitleTextField.setText("");
                    directoryTitleTextField.setText("");
                    contentTitleTextField.setText("");
                }).build()
        ).build();
        newsListView.setContextMenu(newsItemsListContextMenu);
        try {
            newsListView.setItems(UtilPack.sortRussianList(getListItems("news")));
        } catch (NullPointerException ignored) {
            newsListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        articlesListView.setContextMenu(articlesListContextMenu);
        try {
            articlesListView.setItems(UtilPack.sortRussianList(getListItems("articles")));
        } catch (NullPointerException ignored) {
            articlesListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        videosListView.setContextMenu(videosListContextMenu);
        try {
            videosListView.setItems(UtilPack.sortRussianList(getListItems("vdeos")));
        } catch (NullPointerException ignored) {
            videosListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        reviewsListView.setContextMenu(reviewsListContextMenu);
        try {
            reviewsListView.setItems(UtilPack.sortRussianList(getListItems("reviews")));
        } catch (NullPointerException ignored) {
            reviewsListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        additionsListView.setContextMenu(additionsListContextMenu);
        try {
            additionsListView.setItems(UtilPack.sortRussianList(getListItems("additions")));
        } catch (NullPointerException ignored) {
            additionsListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        contentsListView.setContextMenu(contentsListContextMenu);
        try {
            contentsListView.setItems(UtilPack.sortRussianList(getListItems("contents")));
        } catch (NullPointerException ignored) {
            contentsListView.setItems(UtilPack.sortRussianList(emptyList));
        }
        try {
            categoriesListView.setItems(UtilPack.sortRussianList(getListItems("categories")));
        } catch (NullPointerException ignored) {
            categoriesListView.setItems(UtilPack.sortRussianList(emptyList));
        }
    }
    private void setEmptyHtmlEditor() {
        htmlEditor.setHtmlText("");
        htmlEditor.setHtmlText("");
        htmlCode.setText("");
        contentTitleTextField.setText("");
        pageTitleTextField.setDisable(true);
        directoryTitleTextField.setDisable(true);
    }
    @FXML private void editSelectedNewsItem() {
        try {
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
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(newsItem.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedArticle() {
        try {
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
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(article.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedVideo() {
        try {
            Videos video = new Videos();
            String selectedVideo = (String)videosListView.getSelectionModel().getSelectedItem();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery("from Videos where title =\'" + selectedVideo + "\'").list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                video = (Videos) iterator.next();
            }
            session2.close();
            htmlEditor.setHtmlText("");
            htmlEditor.setHtmlText(video.getContent());
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(video.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedReview() {
        try {
            Reviews review = new Reviews();
            String selectedReview = (String)reviewsListView.getSelectionModel().getSelectedItem();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery("from Reviews where title =\'" + selectedReview + "\'").list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                review = (Reviews) iterator.next();
            }
            session2.close();
            htmlEditor.setHtmlText("");
            htmlEditor.setHtmlText(review.getContent());
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(review.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedAddition() {
        try {
            Additions addition = new Additions();
            String selectedAddition = (String)additionsListView.getSelectionModel().getSelectedItem();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery("from Additions where title =\'" + selectedAddition + "\'").list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                addition = (Additions) iterator.next();
            }
            session2.close();
            htmlEditor.setHtmlText("");
            htmlEditor.setHtmlText(addition.getContent());
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(addition.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedContent() {
        try {
            StaticContents staticContent = new StaticContents();
            String selectedStaticContent = (String) contentsListView.getSelectionModel().getSelectedItem();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery("from StaticContents where title =\'" + selectedStaticContent + "\'").list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext(); ) {
                staticContent = (StaticContents) iterator.next();
            }
            session2.close();
            htmlEditor.setHtmlText("");
            htmlEditor.setHtmlText(staticContent.getContent());
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(staticContent.getTitle());
            pageTitleTextField.setText(staticContent.getPage());
            directoryTitleTextField.setText(staticContent.getDirectory());
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
    }
    @FXML private void editSelectedCategory() {
        try {
            Categories category = new Categories();
            String selectedCategory = (String)categoriesListView.getSelectionModel().getSelectedItem();

            Session session2 = HibernateUtil.getSessionFactory().openSession();
            List res2 = session2.createQuery("from Categories where title =\'" + selectedCategory + "\'").list();
            for (Iterator iterator = res2.iterator(); iterator.hasNext();) {
                category = (Categories) iterator.next();
            }
            session2.close();
            htmlEditor.setHtmlText("");
            htmlEditor.setHtmlText(category.getDescription());
            htmlCode.setText(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            contentTitleTextField.setText(category.getTitle());
            pageTitleTextField.setDisable(true);
            directoryTitleTextField.setDisable(true);
        } catch (NullPointerException ignored) {
            setEmptyHtmlEditor();
        }
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
            newsItem.setTitle(contentTitleTextField.getText());
            newsItem.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
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
            article.setTitle(contentTitleTextField.getText());
            article.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            article.setUpdatedAt(new Date());
            session.save(article);
            tx.commit();
            session.close();
        } else if (!videosListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Videos video = new Videos();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from Videos where title=\'" + videosListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                video = (Videos) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            video.setTitle(contentTitleTextField.getText());
            video.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            video.setUpdatedAt(new Date());
            session.save(video);
            tx.commit();
            session.close();
        } else if (!reviewsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Reviews review = new Reviews();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from Reviews where title=\'" + reviewsListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                review = (Reviews) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            review.setTitle(contentTitleTextField.getText());
            review.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            review.setUpdatedAt(new Date());
            session.save(review);
            tx.commit();
            session.close();
        } else if (!additionsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Additions addition = new Additions();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from Additions where title=\'" + additionsListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                addition = (Additions) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            addition.setTitle(contentTitleTextField.getText());
            addition.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            addition.setUpdatedAt(new Date());
            session.save(addition);
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
            staticContent.setTitle(contentTitleTextField.getText());
            staticContent.setPage(pageTitleTextField.getText());
            staticContent.setDirectory(directoryTitleTextField.getText());
            staticContent.setContent(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            staticContent.setUpdatedAt(new Date());
            session.save(staticContent);
            tx.commit();
            session.close();
        } else if (!categoriesListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Categories category = new Categories();
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery(
                    "from Categories where title=\'" + categoriesListView.getSelectionModel().getSelectedItem() + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                category = (Categories) iterator.next();
            }
            Transaction tx = session.beginTransaction();
            category.setTitle(contentTitleTextField.getText());
            category.setDescription(UtilPack.cleanHtml(htmlEditor.getHtmlText()));
            category.setSummary(category.getSummary());
            category.setImagePath(category.getImagePath());
            category.setParent(category.getParent());
            category.setPublished(category.getPublished());
            category.setMoreInfo(category.getMoreInfo());
            session.save(category);
            tx.commit();
            session.close();
        }
        populateContentSiteLists();
    }
    @FXML private void refreshHtml() {
        htmlEditor.setHtmlText(htmlCode.getText());
    }
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
    // Загружает в память список таблиц БД, доступных для экспорта в xls-файл
    //Заполняет список значений tableDBForExportChoiceBox
    private void loadExportTables() {
        ArrayList<String> allTablesNames = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createSQLQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES\n" +
                "where TABLE_TYPE = 'BASE TABLE'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            String tableName = (String) iterator.next();
            allTablesNames.add(tableName);
        }
        session.close();
        ObservableList<String> tableNames = FXCollections.observableArrayList();
        allTablesNames.stream().forEach((table) -> {
            tableNames.add(table);
        });
        tableDBForExportComboBox.setItems(tableNames);
    }
    // Получает из БД список всех названий продуктов и прикрепляет его к Combobox поиска по названию
    // Также вызывает экземпляр класса автодополнения при поиске
    private void populateComboBox () {
        searchComboBox.getItems().clear();
        searchComboBox.getItems().addAll(allProductsTitles);
        AutoCompleteComboBoxListener autoCompleteComboBoxListener = new AutoCompleteComboBoxListener(searchComboBox);
    }
    private void compareFieldsMechanics() throws IOException {
        ArrayList<String> compareDetails = new ArrayList<>(20);
        String selectedHeader = sHeader();
        String selectedDBField = sField();
        if (allImportXLSContent.isEmpty()) {
            AlertWindow.showWarning("Нечего импортировать. Выберите xls-файл с данными\n" +
                    "и сопоставьте его колонки с полями в базе данных.");
        } else if (headersRowTextField.getText().equals("")) {
            AlertWindow.showWarning("Выберите номер строки в xls-файле, где содержатся заголовки колонок для импорта.");
        } else {
            if (selectedHeader==null && selectedDBField==null) {
                AlertWindow.showWarning("Не выбраны данные для сопоставления.");
            } else if (selectedHeader==null) {
                AlertWindow.showWarning("Не выбран заголовок колонки в xls-файле.");
            } else if (selectedDBField==null) {
                AlertWindow.showWarning("Не выбрано поле для импорта в базе данных.");
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
                    AlertWindow.showWarning("Этот заголовок уже лобавлен.");
                } else if (!wasAdded && !wasHeader && wasField) {
                    AlertWindow.showWarning("Это поле уже сопоставлено с другим заголовком.");
                } else {
                    AlertWindow.showWarning("Эти данные уже участвовали в сопоставлении.");
                }
                comparedXLSAndDBFields.setItems(comparedPairs);
                clear = false;
            }
        }
    }
    private void buildProductKindsList() {
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
                    functionsTable.refresh();
                    productKindsList.refresh();
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранный тип").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateTheProductKind(productKindsList);
                    buildProductKindsList();
                    functionsTable.refresh();
                    productKindsList.refresh();
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранный тип устройств").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteTheProductKind(productKindsList);
                    buildProductKindsList();
                    functionsTable.refresh();
                    productKindsList.refresh();
                }).build()
        ).build();
        productKindsList.setContextMenu(productKindsListContextMenu);

        productKindsList.setItems(UtilPack.sortRussianList(items));
    }
    private void tableExcelBehavior() {
        productKindPropertiesTable.setUserData(deletedLines);
        productKindPropertiesTable.setItems(productKindsPropertiesList);
        productKindPropertiesTable.getSelectionModel().setCellSelectionEnabled(true);
        productKindPropertiesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        productKindPropertiesTable.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (productKindPropertiesTable.getEditingCell() == null && t.getCode() == KeyCode.ENTER) {
                if (t.isShiftDown()) {
                    productKindPropertiesTable.getSelectionModel().selectAboveCell();
                } else {
                    productKindPropertiesTable.getSelectionModel().selectBelowCell();
                }
                t.consume();
            }
            if (!t.isControlDown() && t.getCode() == KeyCode.TAB) {
                if (t.isShiftDown()) {
                    productKindPropertiesTable.getSelectionModel().selectLeftCell();
                } else {
                    productKindPropertiesTable.getSelectionModel().selectRightCell();
                }
                t.consume();
            }
        });

        productKindPropertiesTable.setOnKeyPressed((KeyEvent t) -> {
            TablePosition tp;
            if (!t.isControlDown() && (t.getCode().isLetterKey() || t.getCode().isDigitKey())) {
                lastKey = t.getText();
                tp = productKindPropertiesTable.getFocusModel().getFocusedCell();
                productKindPropertiesTable.edit(tp.getRow(),tp.getTableColumn());
                lastKey = null;
            }
        });

        productKindPropertiesTable.setOnKeyReleased((KeyEvent t) -> {
            TablePosition tp;
            switch (t.getCode()) {
                //other code cut out here
                case Z:
                    if (t.isControlDown()) {
                        if (!deletedLines.isEmpty()) {
                            productKindsPropertiesList.add(deletedLines.pop());
                        }
                        break; //don't break for regular Z
                    }
                default:
                    if (t.getCode().isLetterKey() || t.getCode().isDigitKey()) {
                        lastKey = t.getText();
                        tp = productKindPropertiesTable.getFocusModel().getFocusedCell();
                        productKindPropertiesTable.edit(tp.getRow(), tp.getTableColumn());
                        lastKey = null;
                    }
            }
        });

        productKindPropertiesTable.sortPolicyProperty().set( new Callback<TableView<ProductKindPropertiesTableView>, Boolean>() {
            @Override
            public Boolean call(TableView<ProductKindPropertiesTableView> param) {
                Comparator<ProductKindPropertiesTableView> comparator = new Comparator<ProductKindPropertiesTableView>() {
                    @Override
                    public int compare(ProductKindPropertiesTableView r1, ProductKindPropertiesTableView r2) {
                        if (param.getComparator() == null) {
                            return 0;
                        } else {
                            return param.getComparator().compare(r1, r2);
                        }
                    }
                };
                FXCollections.sort(productKindPropertiesTable.getItems(), comparator);
                return true;
            }
        });
    }
    private void populateProductKindPropertiesList(String selectedProductKind) {
        productKindsPropertiesList.clear();
        ArrayList<Integer> selectedProductKindPropertiesIds = new ArrayList<>();
        ArrayList<Properties> propertiesList = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = connection.getResult("select id from properties where product_kind_id =" + UtilPack.getProductKindIdFromTitle(selectedProductKind));
            while (resultSet.next()) {
                selectedProductKindPropertiesIds.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {}

        selectedProductKindPropertiesIds.stream().forEach((pid) -> {
            Session session1 = HibernateUtil.getSessionFactory().openSession();
            List res1 = session1.createQuery("from Properties where id =" + pid).list();
            for (Iterator iterator = res1.iterator(); iterator.hasNext();) {
                Properties p = (Properties) iterator.next();
                propertiesList.add(p);
            }
            session1.close();
        });

        propertiesList.stream().forEach((p) -> {
            productKindsPropertiesList.add(new ProductKindPropertiesTableView(p.getOrderNumber().toString(), p.getTitle(), p.getOptional(), p.getSymbol()));
        });
    }
    private void buildProductKindPropertiesTable(String selectedProductKind) {
        populateProductKindPropertiesList(selectedProductKind);
        tableExcelBehavior();
        Callback<TableColumn<ProductKindPropertiesTableView, String>, TableCell<ProductKindPropertiesTableView, String>> cellFactory
                = (TableColumn<ProductKindPropertiesTableView, String> p) -> new EditingCell();

        productKindPropertiesOrderColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        productKindPropertiesOrderColumn.setCellFactory(cellFactory);
        productKindPropertiesOrderColumn.setOnEditCommit(
                t -> {
                    String orderNumber = (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber());
                    (t.getTableView().getItems().get(t.getTablePosition().getRow())).setOrderNumber(t.getNewValue());
                    setProductKindsPropertyCellValue(orderNumber, "orderNumber", t);
                }
        );
        productKindPropertiesTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        productKindPropertiesTitleColumn.setCellFactory(cellFactory);
        productKindPropertiesTitleColumn.setOnEditCommit(
                t -> {
                    String orderNumber = (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber());
                    (t.getTableView().getItems().get(t.getTablePosition().getRow())).setTitle(t.getNewValue());
                    setProductKindsPropertyCellValue(orderNumber, "title", t);
                }
        );
        productKindPropertiesOptionalColumn.setCellValueFactory(new PropertyValueFactory<>("optional"));
        productKindPropertiesOptionalColumn.setCellFactory(cellFactory);
        productKindPropertiesOptionalColumn.setOnEditCommit(
                t -> {
                    String orderNumber = (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber());
                    (t.getTableView().getItems().get(t.getTablePosition().getRow())).setOptional(t.getNewValue());
                    setProductKindsPropertyCellValue(orderNumber, "optional", t);
                }
        );
        productKindPropertiesSymbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        productKindPropertiesSymbolColumn.setCellFactory(cellFactory);
        productKindPropertiesSymbolColumn.setOnEditCommit(
                t -> {
                    String orderNumber = (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber());
                    (t.getTableView().getItems().get(t.getTablePosition().getRow())).setSymbol(t.getNewValue());
                    setProductKindsPropertyCellValue(orderNumber, "symbol", t);
                }
        );
    }
    private class EditingCell extends TableCell {

        private TextField textField;

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                Platform.runLater(() -> {//without this space erases text, f2 doesn't
                    textField.requestFocus();//also selects
                });
                if (lastKey != null) {
                    textField.setText(lastKey);
                    Platform.runLater(() -> {
                        textField.deselect();
                        textField.end();
                        textField.positionCaret(textField.getLength()+2);//works sometimes
                    });
                }
            }
        }

        public void commit(){
            commitEdit(textField.getText());
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            try {
                setText(getItem().toString());
            } catch (Exception e) {}
            setGraphic(null);
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }

        private void createTextField() {
            textField = new TextField(getString());

            //doesn't work if clicking a different cell, only focusing out of table
            textField.focusedProperty().addListener(
                    (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                        if (!arg2) commitEdit(textField.getText());
                    });

            textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                    EditingCell.this.getTableView().requestFocus();//why does it lose focus??
                    EditingCell.this.getTableView().getSelectionModel().selectBelowCell();
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

            textField.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {
                if (t.getCode() == KeyCode.DELETE) {
                    t.consume();//stop from deleting line in table keyevent
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

        public void commitEdit(String item) {

            // This block is necessary to support commit on losing focus, because the baked-in mechanism
            // sets our editing state to false before we can intercept the loss of focus.
            // The default commitEdit(...) method simply bails if we are not editing...
            if (! isEditing() && ! item.equals(getItem())) {
                TableView<ProductKindPropertiesTableView> table = getTableView();
                if (table != null) {
                    TableColumn<ProductKindPropertiesTableView, String> column = getTableColumn();
                    CellEditEvent<ProductKindPropertiesTableView, String> event = new CellEditEvent<>(table,
                            new TablePosition<ProductKindPropertiesTableView,String>(table, getIndex(), column),
                            TableColumn.editCommitEvent(), item);
                    Event.fireEvent(column, event);
                }
            }

            super.commitEdit(item);
        }
    }
    private void setProductKindsPropertyCellValue(String orderNumber, String fieldName, CellEditEvent<ProductKindPropertiesTableView, String> t) {
        int productKindId = UtilPack.getProductKindIdFromTitle(productKindsList.getSelectionModel().getSelectedItem());
        Properties currentProperty = new Properties();

        Session session0 = HibernateUtil.getSessionFactory().openSession();
        if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() != null) &&
            (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() != null) &&
            (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() != null)
                ) {
            Query query0 = session0.createQuery("from Properties where (orderNumber = " +
                    orderNumber +
                    " and title = :title) and (optional = :optional and symbol = :symbol) and productKindId = " +
                    productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle());
            query0.setParameter("optional", t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional());
            query0.setParameter("symbol", t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol());
            List<Properties> res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() == null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and title = :title and optional = :optional and productKindId = " + productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle());
            query0.setParameter("optional", t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() != null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and title = :title and symbol = :symbol and productKindId = " + productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle());
            query0.setParameter("symbol", t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext();) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() != null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and optional = :optional and symbol = :symbol and productKindId = " + productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional());
            query0.setParameter("symbol", t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() != null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and symbol = :symbol and productKindId = " + productKindId);
            query0.setParameter("symbol", t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() == null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and optional = :optional and productKindId = " + productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() != null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() == null)
                ) {
            System.out.println("title is " + t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle());
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and title = :title and productKindId = " + productKindId);
            query0.setParameter("title", t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle());
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                currentProperty = (Properties) iterator.next();
            }
        } else if ((t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional() == null) &&
                (t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol() == null)
                ) {
            Query query0 = session0.createQuery(
                    "from Properties where orderNumber = " + orderNumber + " and productKindId = " + productKindId);
            List res = query0.list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                currentProperty = (Properties) iterator.next();
            }
        }
        session0.close();

        String newValue = "";
        String request = "";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        if (fieldName.equals("orderNumber")) {
            newValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getOrderNumber();
            System.out.println("newValue is " + newValue + "and orderNumber is " + orderNumber);
            request = "UPDATE Properties set " + fieldName + " = " + newValue + " where id = " + currentProperty.getId();
        } else {
            switch (fieldName) {
                case "title":
                    newValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getTitle();
                    break;
                case "optional":
                    newValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getOptional();
                    break;
                case "symbol":
                    newValue = t.getTableView().getItems().get(t.getTablePosition().getRow()).getSymbol();
                    break;
            }
            request = "UPDATE Properties set " + fieldName + " = :newValue where orderNumber = " + orderNumber + " and productKindId = " + productKindId;
        }
        Query query = session.createQuery(request);
        if (!fieldName.equals("orderNumber")) {
            query.setParameter("newValue", newValue);
        }
        query.executeUpdate();
        tx.commit();
        session.close();
        try {
            getAllProductsList();
            productKindPropertiesTable.refresh();
        } catch (SQLException e) {}
    }
    private void buildFunctionsTable(String selectedProductKind) {
        int productKindId = UtilPack.getProductKindIdFromTitle(selectedProductKind);
        ArrayList<Functions> functions = new ArrayList<>(10);
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Functions where productKindId =" + productKindId).list();
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
                    ContextBuilder.createNewFunction(productKindId);
                    buildFunctionsTable(selectedProductKind);
                }).build(),
                MenuItemBuilder.create().text("Редактировать выбранную функцию").onAction((ActionEvent ae2) -> {
                    ContextBuilder.updateTheFunction(productKindId, functionsTable);
                }).build(),
                MenuItemBuilder.create().text("Удалить выбранную функцию").onAction((ActionEvent ae3) -> {
                    ContextBuilder.deleteTheFunction(functionsTable);
                    buildFunctionsTable(selectedProductKind);
                }).build()
        ).build();
        functionsTable.setContextMenu(functionsTableContextMenu);
        functionsTable.setItems(list);
    };
    private String sField() {
        return importFieldsComboBox.getValue();
    }
    private String sTable() {
        return tableDBForExportComboBox.getValue();
    }
    private String sHeader() {
        String sh = new String();
        try {
            sh = headersXLS.getSelectionModel().getSelectedItem();
        } catch (NullPointerException ignored) {}
        return sh;
    }
    @FXML private void getSelectedDBTable() {
        sTable();
    }
    @FXML private void getSelectedDBField() {
        sField();
    }
    @FXML private void getSelectedHeader() {
        sHeader();
    }
    @FXML private void getSelectedDBKey() {
        selectedDBKey = "Наименование продукта";
        startImportXLSButton.setDisable(false);
    }
    @FXML private void compareFields() throws IOException {
        compareFieldsMechanics();
    }
    @FXML private void cancelSetImportDetails() {
        ObservableList<String> data = FXCollections.observableArrayList();
        headersRowNumber = 0;
        try {
            allImportXLSContent.clear();
        } catch (NullPointerException ignored) {
            AlertWindow.showWarning("Нет данных для очистки.");
        }
        try {
            allCompareDetails.clear();
        } catch (NullPointerException ignored) {
            AlertWindow.showWarning("Нет данных для очистки.");
        }
        try {
            comparedPairs.clear();
        } catch (NullPointerException ignored) {
            AlertWindow.showWarning("Нет данных для очистки.");
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
            try {
                allImportXLSContent = XLSHandler.grabData(fileXLS.getAbsolutePath(), progressBarImportXLS);
            } catch (IOException e) {}
        }
    }
    @FXML private void chooseDirectoryForExport() {
        fileXLSExport = directoryForExport.showDialog(anchorPane.getScene().getWindow());
        if (fileXLSExport != null) {
            fileForExportPathLabel.setText(fileXLSExport.getPath());
        }
    }
    @FXML private void startExport() {
        String dbTable = tableDBForExportComboBox.getValue();
        String targetLocationPath = fileForExportPathLabel.getText() + "\\" + exportFileNameTextField.getText();
        XLSHandler.exportDBTableTo(dbTable, targetLocationPath, progressBarImportXLS);
    }
    @FXML private void chooseDirectoryForExportPrices() {
        fileXLSExport = directoryForExportPrices.showDialog(anchorPane.getScene().getWindow());
        targetDir = fileXLSExport.getPath();
        if (fileXLSExport != null) {
            dirForExportPricesPathLabel.setText(targetDir);
        }
    }
    @FXML private void startExportPrices() {
        XLSHandler.exportDBPricesTo(allProductsList, targetDir, progressBarImportXLS);
    }
    @FXML private void getHeadersRowNumber(KeyEvent e) {
        if(e.getCode().toString().equals("ENTER")) {
            ObservableList<String> data = FXCollections.observableArrayList();
            if (allImportXLSContent.isEmpty()) {
                AlertWindow.showWarning("Сначала необходимо выбрать xls-файл, содержащий данные для импорта.");
            }
            try {
                headersRowNumber = Integer.parseInt(headersRowTextField.getText()) - 1;
                if (headersRowNumber < 0) {
                    AlertWindow.showWarning("Вы ввели недопустимое число. Будет установленно значение по умолчанию, " +
                            "что соответствует числу 1.");
                    headersRowTextField.setText("1");
                    headersRowNumber = 0;
                }
            } catch (NumberFormatException ex) {
                AlertWindow.showWarning("Вы ввели недопустимое число. Будет установленно значение по умолчанию, " +
                        "что соответствует числу 1.");
                headersRowTextField.setText("1");
                headersRowNumber = 0;
            }
            try {
                allImportXLSContent.stream().forEach((column) -> {
                    data.add(column.get(headersRowNumber));
                });
            } catch (NullPointerException ignored) {
                AlertWindow.showWarning("Не введён номер строки, содержащей заголовки колонок в xls-файле. " +
                        "Перед вводом номера строки убедитесь, что xls-файл с данными уже выбран.");
            } catch (IndexOutOfBoundsException ie) {
                AlertWindow.showWarning("Введён номер строки, превышшающий общее количество строк в выбранном xls-файле. " +
                        "Откорректируйте входящие данные.");
            }
            headersXLS.setItems(data);
        }
        clear = false;
    }
    @FXML public  void startImportProgressBar() {
        XLSToDBImport importer = new XLSToDBImport(allCompareDetails);
        importer.startImport(allImportXLSContent, allProductsTitles);
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
    @FXML private void saveServerSFTP() {
        SiteDBSettings server = new SiteDBSettings();
        server.saveSetting("serverSFTP", serverSFTP.getText());
    }
    @FXML private void savePortSFTP() {
        SiteDBSettings port = new SiteDBSettings();
        port.saveSetting("portSFTP", portSFTP.getText());
    }
    @FXML private void saveUserSFTP() {
        SiteDBSettings user = new SiteDBSettings();
        user.saveSetting("userSFTP", userSFTP.getText());
    }
    @FXML private void savePasswordSFTP() {
        SiteDBSettings password = new SiteDBSettings();
        password.saveSetting("passwordSFTP", passwordSFTP.getText());
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
    @FXML private void saveSiteOrdersReceiver() {
        SiteSettings siteSettings = new SiteSettings();
        siteSettings.saveSetting("siteOrdersReceiver", siteOrdersReceiverTextField.getText());
    }
    @FXML private void addPropertyToTable() {
        int newRowNumber = productKindPropertiesTable.getItems().size() + 1;
        int productKindId = UtilPack.getProductKindIdFromTitle(productKindsList.getSelectionModel().getSelectedItem());
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createSQLQuery("insert into properties (order_number, product_kind_id) values (" + newRowNumber +
                ", (select id from product_kinds where id = " + productKindId + "))");
        query.executeUpdate();
        tx.commit();
        session.close();
        populateProductKindPropertiesList(productKindsList.getSelectionModel().getSelectedItem());
    }
    @FXML private void deletePropertyFromTable() {
        String num = productKindPropertiesTable.getSelectionModel().getSelectedItem().getOrderNumber();
        // I've tried for usual Integer.parseInt(), but in this case it returns null!!!!! I don't know why?!!!
        int orderNumber = UtilPack.stringToIntByChars(num);

        Integer productKindId = UtilPack.getProductKindIdFromTitle(productKindsList.getSelectionModel().getSelectedItem());
        Properties property = new Properties();

        Session session0 = HibernateUtil.getSessionFactory().openSession();
        Query query = session0.createQuery("from Properties where orderNumber = " + orderNumber + " and productKindId = " + productKindId);
        List result = query.list();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            property = (Properties) iterator.next();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        Query query1 = session.createQuery("delete PropertyValues where propertyId=" + property.getId());
        query1.executeUpdate();
        tx1.commit();
        Transaction tx2 = session.beginTransaction();
        Query query2 = session.createQuery("delete Properties where id=" + property.getId() + " and productKindId=" + productKindId);
        query2.executeUpdate();
        tx2.commit();
        session.close();

        int ix = productKindPropertiesTable.getSelectionModel().getSelectedIndex();
        // Select a row
        if (!(productKindPropertiesTable.getItems().size() == 0)) {
            productKindsPropertiesList.remove(ix);
        }
        if ((productKindPropertiesTable.getItems().size() == 0)) {
            return;
        }
        if (ix != 0) {
            ix = ix -1;
        }
        populateProductKindPropertiesList(productKindsList.getSelectionModel().getSelectedItem());
    }
    @FXML private void handleProductsKindSelected() throws SQLException {
        String selectedProductKind = productKindsList.getSelectionModel().getSelectedItem();
        buildFunctionsTable(selectedProductKind);
        buildProductKindPropertiesTable(selectedProductKind);
    }
    @FXML private void exportPropertiesTableToXls() {
        File file = directoryForExportProperties.showDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            XLSHandler.exportPropertiesByProductKinds(productKindsList.getSelectionModel().getSelectedItem(),
                    file.getPath(), progressBarImportXLS);
        }
    }
    @FXML private void importPropertiesFromXls() {
        File file = new FileChooser().showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            try {
                XLSHandler.importPropertyValues(file.getAbsolutePath(), progressBarImportXLS);
            } catch (IOException e) {
                AlertWindow.showErrorMessage(e.getMessage());
            } catch (BiffException e) {
                AlertWindow.showErrorMessage(e.getMessage());
            }
        }
    }
}