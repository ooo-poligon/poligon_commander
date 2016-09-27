package modalwindows;

import entities.Categories;
import entities.SeriesItems;
import entities.Vendors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import main.PCGUIController;
import main.PoligonCommander;
import main.Product;
import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class AlertWindow {
    private static String picPath = "";
    private static StackPane stackPane = new StackPane();
    public static void alertNoRbcServerConnection () {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Нет связи с сервером \"CBR\"!");
        alert.setContentText("Актуальные данные курсов валют, а следовательно,\n" +
                "все цены могут быть неверны!");
        alert.showAndWait();
    }
    public static void notLowestCategoryAlert() {
        Alert notLowestCategoryAlert = new Alert(Alert.AlertType.WARNING);
        notLowestCategoryAlert.setTitle("Внимание!");
        notLowestCategoryAlert.setHeaderText("Выбранная категория содержит вложенные категории " +
                "и не может содержать отдельные элементы каталога.");
        notLowestCategoryAlert.setContentText("Создайте в этой категории новую подкатегорию " +
                "для выбранных элементов, а затем перенесите в неё эти элементы. Вы также можете перенести " +
                "выбранные элементы в любую другую категорию, не содержащую подкатегорий.");
        notLowestCategoryAlert.show();
    }
    public static void showWarning(String warningText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Неправильная настройка импорта данных.");
        alert.setContentText(warningText);
        alert.showAndWait();
    }
    public static void showInfo(String infoText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Информация");
        alert.setHeaderText("Сообщение о ходе выполнения:");
        alert.setContentText(infoText);
        alert.showAndWait();
    }
    public static void illegalAction() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Недопустимое действие!");
        alert.setContentText("В таблице цен можно устанавливать только закупочную валютную цену.\n" +
                "Все остальные цены являются расчётными и редактированию не подлежат.");
        alert.showAndWait();
    }
    public static void illegalActionR() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Недопустимое действие!");
        alert.setContentText("В этой таблице цен можно устанавливать только закупочную и розничную цену.");
        alert.showAndWait();
    }
    public static void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Ошибка соединения с базой данных.\nРекомендуется перезапустить приложение.");
        alert.show();
    }
    public static void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
    public static void showErrorDeleteRefs() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Нельзя удалить эту запись, пока на неё ссылаются другие записи.");
        alert.show();
    }
    public static void functionNotReady() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Выбранное действие недоступно.");
        alert.setContentText("Эта функция будет реализована позже.");
        alert.show();
    }
    public static Optional<ButtonType> categoryDeleteAttention(String catTitle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Внимание!");
        alert.setTitle("Удаление категории");
        String s = "Категория \"" + catTitle + "\" будет удалена из каталога. " +
                "Все элементы, принадлежащие этой категории будут перенесены в ближайшшую вышестоящую категорию.";
        alert.setContentText(s);
        return alert.showAndWait();
    }
    public static Optional<ButtonType> changeProductCategoryDialog(StackPane stackPaneModal, ButtonType buttonTypeOk, ButtonType buttonTypeCancel) {
        stackPane = stackPaneModal;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogPane dialog = new DialogPane();
        alert.setDialogPane(dialog);
        alert.setTitle("Переместить выбранные элементы");
        alert.setHeaderText("Укажите категорию, в которую следует \nпереместить выбранные элементы:");
        alert.setResizable(false);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(stackPane);
        stackPane.setAlignment(Pos.CENTER);

        dialog.setContent(anchorPane);
        dialog.getButtonTypes().add(buttonTypeOk);
        dialog.getButtonTypes().add(buttonTypeCancel);
        return alert.showAndWait();
    }
    public static Optional<NewCategory> newCategoryDialog(TreeView<String> treeView) {
        String parentCategoryTitle = treeView.getSelectionModel().getSelectedItem().getValue();
        Dialog<NewCategory> dialog = new Dialog<>();
        dialog.setTitle("Создание новой категории");
        dialog.setHeaderText("Введите название новой категории. Она будет размещена внутри выбранной категории.");
        dialog.setResizable(true);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Краткое описание:  ");
        Label label3 = new Label("");
        Label label4 = new Label("");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();
        Button setImageButton = new Button("Выбрать изображение");
        ImageView image1 = new ImageView();
        image1.setFitHeight(300.0);
        image1.setPreserveRatio(true);
        GridPane grid  = new GridPane();
        GridPane grid1 = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(grid1, 2, 4);
        grid1.add(image1, 0, 0);
        grid.add(label4, 1, 5);
        grid.add(setImageButton, 2, 6);
        dialog.getDialogPane().setContent(grid);

        setImageButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(grid.getScene().getWindow());
                if (file != null) {
                    //ProductImage.open(new File(ProductImage.makeTemporaryResizedImage(file)), grid1, image1);
                    ProductImage.open(new File(file.getAbsolutePath()), grid1, image1, "categoryImageSetter");
                }
                picPath = file.getAbsolutePath();
            }
        });

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                if (picPath.equals("")) {
                    return new NewCategory(text1.getText(), text2.getText(), picPath);
                } else {
                    String picName = picPath.replace('\\', '@').split("@")[(picPath.replace('\\', '@')).split("@").length - 1];
                    String tempPath = PoligonCommander.tmpDir.getAbsolutePath() + "\\" + picName;
                    String newPicPath = "\\\\Server03\\бд_сайта\\poligon_images\\design\\categories\\" +
                            UtilPack.getCategoryVendorFromId(UtilPack.getCategoryIdFromTitle(parentCategoryTitle)) +
                            "\\" + picName;
                    try {
                        UtilPack.copyFile(new File(tempPath), new File(newPicPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new NewCategory(text1.getText(), text2.getText(), newPicPath);
                }
            }
            if (b == buttonTypeOk) {

            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<NewCategory> editCategoryDialog(ArrayList<String> details) {
        Dialog<NewCategory> dialog = new Dialog<>();
        dialog.setTitle("Редактирование категории");
        dialog.setHeaderText("Здесь Вы можете отредактировать название и/или описание выбранной категории.");
        dialog.setResizable(true);

        Label label1 = new Label("Введите название:  ");
        Label label2 = new Label("Краткое описание:  ");
        TextField text1 = new TextField();
        TextArea text2 = new TextArea();
        text1.setText(details.get(0));
        text2.setText(details.get(1));
        Label label3 = new Label("");
        Label label4 = new Label("");
        Button setImageButton = new Button("Выбрать изображение");
        ImageView image1 = new ImageView();
        image1.setFitHeight(300.0);
        image1.setPreserveRatio(true);
        GridPane grid = new GridPane();
        GridPane grid1 = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(grid1, 2, 4);
        grid1.add(image1, 0, 0);
        grid.add(label4, 1, 5);
        grid.add(setImageButton, 2, 6);
        dialog.getDialogPane().setContent(grid);
        if(details.get(2) != null) ProductImage.open(new File(details.get(2)), grid1, image1, "categoryImage");
        setImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(grid.getScene().getWindow());
                if (file != null) {
                    ProductImage.open(new File(file.getAbsolutePath()), grid1, image1, "categoryImageSetter");
                }
                picPath = file.getAbsolutePath();
            }
        });

        ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                if (picPath.equals("")) {
                    return new NewCategory(text1.getText(), text2.getText(), details.get(2));
                } else {
                    String picName = picPath.replace('\\', '@').split("@")[(picPath.replace('\\', '@')).split("@").length - 1];
                    String tempPath = PoligonCommander.tmpDir.getAbsolutePath() + "\\" + picName;
                    String newPicPath = "\\\\Server03\\бд_сайта\\poligon_images\\design\\categories\\" +
                            details.get(3) + "\\" + picName;
                    try {
                        UtilPack.copyFile(new File(tempPath), new File(newPicPath));
                    } catch (IOException e) {}
                    return new NewCategory(text1.getText(), text2.getText(), newPicPath);
                }
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<NewVendor> newVendorDialog(ObservableList<String> currencies) {
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

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewVendor(text1.getText(), text2.getText(), currency.getValue(), text3.getText(), Double.parseDouble(text5.getText()));
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<Product> newProductDialog() {
        ArrayList<String> allProductTypes = UtilPack.getAllProductTypes();
        ArrayList<String> allCurrencies = UtilPack.getAllCurrencies();
        ArrayList<Vendors> allVendors = UtilPack.getAllVendorsEntities();
        ArrayList<SeriesItems> allSeries = UtilPack.getAllSeries();
        ObservableList<String> productTypesTitles = FXCollections.observableArrayList();
        ObservableList<String> currenciesTitles = FXCollections.observableArrayList();
        ObservableList<String> vendorsTitles = FXCollections.observableArrayList();
        ObservableList<String> seriesTitles =  FXCollections.observableArrayList();
        productTypesTitles.addAll(allProductTypes);
        currenciesTitles.addAll(allCurrencies);
        allVendors.stream().forEach((vendor) -> {vendorsTitles.add(vendor.getTitle());});
        //vendorsTitles.addAll(allVendors);
        allSeries.stream().forEach(serie -> {
            seriesTitles.add(serie.getTitle());
        });

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Создание нового продукта");
        dialog.setHeaderText("Введите данные нового продукта.");
        dialog.setResizable(false);

        Label titleLabel                = new Label("Название:  ");
        TextField titleTextField        = new TextField("");
        Label articleLabel              = new Label("Артикул:  ");
        TextField articleTextField      = new TextField("");
        Label announceLabel             = new Label("\nАнонс:  ");
        Label eanLabel                  = new Label("EAN :  ");
        TextField eanTextField          = new TextField("");
        Label serieLabel                = new Label("Серия :  ");
        ComboBox<String> seriesComboBox = new ComboBox<>();
        Label deliveryTimeLabel         = new Label("Сроки поставки :  ");
        TextField deliveryTimeTextField = new TextField("");
        TextField announceTextField     = new TextField("");
        Label descriptionLabel          = new Label("Краткое описание:  ");
        TextArea descriptionTextArea    = new TextArea("");
        Label inputPriceLabel           = new Label("Закупочная цена: (в валюте производителя)");
        TextField inputPriceTextField   = new TextField("0.0");
        Label rateLabel                 = new Label("Коэффициент наценки: ");
        TextField rateTextField         = new TextField("0.0");
        Label discountLabel             = new Label("Скидка (%)\n\"+10\"-\"отповая\"-\"диллерская\": ");
        TextField discount1TextField    = new TextField("0.0");
        TextField discount2TextField    = new TextField("0.0");
        TextField discount3TextField    = new TextField("0.0");
        Label rubRetailLabel            = new Label("Цена в рублях для российских производителей.");
        TextField rubRetailTextField    = new TextField("0.0");
        Label availableLabel            = new Label("Доступен\nдля заказа ");
        CheckBox availableCheckBox      = new CheckBox();
        Label outdatedLabel             = new Label("Снят\nс производства ");
        CheckBox outdatedCheckBox       = new CheckBox();
        Label productTypeLabel          = new Label("Тип продукта: ");
        ComboBox<String> productTypeComboBox = new ComboBox<>();
        Label vendorLabel               = new Label("Производитель: ");
        Label currencyLabel             = new Label("Валюта: ");
        Label categoryLabel             = new Label("Выберите категорию: ");
        ComboBox<String> vendorComboBox = new ComboBox<>();
        ComboBox<String> currencyComboBox = new ComboBox<>();
        StackPane stackPane = new StackPane();
        TreeView<String> treeView = new TreeView<>();
        PCGUIController pcguiController = new PCGUIController();
        TreeView<String> category = pcguiController.buildModalCategoryTree(stackPane, treeView);

        productTypeComboBox.setItems(productTypesTitles);
        productTypeComboBox.setEditable(true);
        currencyComboBox.setItems(currenciesTitles);
        currencyComboBox.setEditable(true);
        vendorComboBox.setItems(vendorsTitles);
        vendorComboBox.setEditable(true);
        seriesComboBox.setItems(seriesTitles);
        seriesComboBox.setEditable(true);
        AutoCompleteComboBoxListener autoCompleteProductTypeComboBox = new AutoCompleteComboBoxListener(productTypeComboBox);
        AutoCompleteComboBoxListener autoCompleteCurrencyComboBox = new AutoCompleteComboBoxListener(currencyComboBox);
        AutoCompleteComboBoxListener autoCompleteVendorComboBox = new AutoCompleteComboBoxListener(vendorComboBox);
        AutoCompleteComboBoxListener autoCompleteSerieComboBox = new AutoCompleteComboBoxListener(seriesComboBox);

        GridPane grid = new GridPane();

        grid.add(productTypeLabel, 1, 1);
        grid.add(productTypeComboBox, 2, 1);
        grid.add(vendorLabel, 3, 1);
        grid.add(vendorComboBox, 4, 1);

        grid.add(titleLabel, 1, 2);
        grid.add(titleTextField, 2, 2);
        grid.add(articleLabel, 3, 2);
        grid.add(articleTextField, 4, 2);

        grid.add(eanLabel, 1, 3);
        grid.add(eanTextField, 2, 3);
        grid.add(serieLabel, 3, 3);
        grid.add(seriesComboBox, 4, 3);

        grid.add(availableLabel, 1, 4);
        grid.add(availableCheckBox, 2, 4);
        grid.add(outdatedLabel, 3, 4);
        grid.add(outdatedCheckBox, 4, 4);

        grid.add(deliveryTimeLabel, 1, 5);
        grid.add(deliveryTimeTextField, 2, 5);
        grid.add(currencyLabel, 3, 5);
        grid.add(currencyComboBox, 4, 5);

        grid.add(announceLabel, 1, 6);
        grid.add(announceTextField, 2, 5, 3, 5);

        grid.add(inputPriceLabel, 1, 9);
        grid.add(inputPriceTextField, 2, 9);
        grid.add(rateLabel, 3, 9);
        grid.add(rateTextField, 4, 9);

        grid.add(discountLabel, 1, 10);
        grid.add(discount1TextField, 2, 10);
        grid.add(discount2TextField, 3, 10);
        grid.add(discount3TextField, 4, 10);

        grid.add(rubRetailLabel, 1, 11);
        grid.add(rubRetailTextField, 2, 11);

        grid.add(descriptionLabel, 1, 13);
        grid.add(descriptionTextArea, 1, 13, 4, 13);

        grid.add(categoryLabel, 5, 1);
        grid.add(stackPane, 5, 2, 6, 24);

        grid.setHgap(10);
        grid.setVgap(10);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            int selectedCategoryId = 0;
            String selectedCategory = "";
            if(!category.getSelectionModel().getSelectedItems().isEmpty()) {
                selectedCategory = category.getSelectionModel().getSelectedItem().getValue();
                Session session = HibernateUtil.getSessionFactory().openSession();
                Query query = session.createQuery("from Categories where title = :title");
                query.setParameter("title", selectedCategory);
                List result = query.list();
                for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                    Categories cat = (Categories) iterator.next();
                    selectedCategoryId = cat.getId();
                }
            }
            if (b == buttonTypeOk) {
                return new Product(
                    selectedCategoryId,
                    titleTextField.getText(),
                    descriptionTextArea.getText(),
                    announceTextField.getText(),
                    articleTextField.getText(),
                    (availableCheckBox.isSelected()? 1 : 0),
                    deliveryTimeTextField.getText(),
                    eanTextField.getText(),
                    (outdatedCheckBox.isSelected()? 1 : 0),
                    Double.parseDouble(inputPriceTextField.getText()),
                    seriesComboBox.getValue(),
                    UtilPack.getProductKindIdFromTitle(productTypeComboBox.getValue()),
                    new Integer(0),
                    UtilPack.getCurrencyIdFromTitle(currencyComboBox.getValue()),
                    Double.parseDouble(rateTextField.getText()),
                    Double.parseDouble(discount1TextField.getText()),
                    Double.parseDouble(discount2TextField.getText()),
                    Double.parseDouble(discount3TextField.getText()),
                    Double.parseDouble(rubRetailTextField.getText())
                );
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<NewSerie> newSerieDialog() {
        ArrayList<String> allVendors = UtilPack.getAllVendors();
        ObservableList<String> vendorsTitles = FXCollections.observableArrayList();
        vendorsTitles.addAll(allVendors);

        Dialog<NewSerie> dialog = new Dialog<>();
        dialog.setTitle("Создание новой серии");
        dialog.setHeaderText("Введите название новой серии устройств.");
        dialog.setResizable(false);

        Label titleLabel = new Label("Введите название серии:  ");
        Label vendorLabel = new Label("Выберите производителя данной серии:  ");
        Label descriptionLabel = new Label("Описание серии (необязательно):  ");
        TextField titleTextField = new TextField();
        TextArea descriptionTextArea = new TextArea();

        GridPane grid = new GridPane();
        grid.add(titleLabel, 1, 1);
        grid.add(titleTextField, 2, 1);

        grid.add(vendorLabel, 1, 2);
        ComboBox<String> vendorComboBox = new ComboBox<>();
        grid.add(vendorComboBox, 2, 2);

        grid.add(descriptionLabel, 1, 3);
        grid.add(descriptionTextArea, 2, 3);
        dialog.getDialogPane().setContent(grid);

        vendorComboBox.setItems(vendorsTitles);
        vendorComboBox.setEditable(true);
        AutoCompleteComboBoxListener autoCompleteVendorComboBox = new AutoCompleteComboBoxListener(vendorComboBox);

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewSerie(titleTextField.getText(), descriptionTextArea.getText(), vendorComboBox.getValue());
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<NewSerie> editSerieDialog(String selectedSerie) {
        String vendorTitle = "";
        SeriesItems seriesItem = new SeriesItems();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from SeriesItems where title = :title");
        query.setParameter("title", selectedSerie);
        List result = query.list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            seriesItem = (SeriesItems) iterator.next();
        }
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Query query1 = session1.createQuery("from Vendors where id = :id");
        query1.setParameter("id", seriesItem.getVendorId().getId());
        List result1 = query1.list();
        for(Iterator iterator = result1.iterator(); iterator.hasNext();) {
            Vendors vendor = (Vendors) iterator.next();
            vendorTitle = vendor.getTitle();
        }

        ArrayList<String> allVendors = UtilPack.getAllVendors();
        ObservableList<String> vendorsTitles = FXCollections.observableArrayList();
        vendorsTitles.addAll(allVendors);

        Dialog<NewSerie> dialog = new Dialog<>();
        dialog.setTitle("Редактирование серии");
        dialog.setHeaderText("Здесь можно отредактировать выбранную серию устройств.");
        dialog.setResizable(false);

        Label titleLabel = new Label("Название серии:  ");
        Label vendorLabel = new Label("Производитель данной серии:  ");
        Label descriptionLabel = new Label("Описание серии (необязательно):  ");
        TextField titleTextField = new TextField();
        titleTextField.setText(seriesItem.getTitle());
        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setText(seriesItem.getDescription());

        GridPane grid = new GridPane();
        grid.add(titleLabel, 1, 1);
        grid.add(titleTextField, 2, 1);

        grid.add(vendorLabel, 1, 2);
        ComboBox<String> vendorComboBox = new ComboBox<>();
        grid.add(vendorComboBox, 2, 2);

        grid.add(descriptionLabel, 1, 3);
        grid.add(descriptionTextArea, 2, 3);
        dialog.getDialogPane().setContent(grid);

        vendorComboBox.setItems(vendorsTitles);
        vendorComboBox.setEditable(true);
        vendorComboBox.getSelectionModel().select(vendorTitle);
        AutoCompleteComboBoxListener autoCompleteVendorComboBox = new AutoCompleteComboBoxListener(vendorComboBox);

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewSerie(titleTextField.getText(), descriptionTextArea.getText(), vendorComboBox.getValue());
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static void fillRequiredFields() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Не заполнены необходимые поля!");
        alert.setContentText("Поля \"Производитель\", \"Тип продукта\", \"Категория\" и \"Серия продукта\" должны быть заполнены обязательно!");
        alert.show();
    }
    public static void waiting() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Подождите...");
        alert.show();
    }
    public static void tooManyColumnsForExport() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Количество устройств в группе больше допустимого!");
        alert.setHeaderText("Для успешного экспорта количество устройств в группе не должно превышать 252!\nЭкспорт отменён.");
        alert.show();
    }
    public static void productKindNotFound() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание! В базе данных нет такого типа устройств!");
        alert.setHeaderText("Добавьте желаемый тип устройств на вкладке \"Настройки\" и повторите импорт.\nОперация прерввана.");
        alert.show();
    }
    public static void taskComplete(String taskName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Операция завершена!");
        alert.setHeaderText(taskName + " выполнен.");
        alert.show();
    }
}
