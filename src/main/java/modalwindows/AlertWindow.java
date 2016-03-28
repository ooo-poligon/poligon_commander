package modalwindows;

import entities.Categories;
import entities.Series;
import entities.Vendors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import main.PCGUIController;
import main.Product;
import org.hibernate.Query;
import org.hibernate.Session;
import utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Igor Klekotnev on 11.03.2016.
 */
public class AlertWindow {
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
    public static void illegalAction() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Недопустимое действие!");
        alert.setContentText("В таблице цен можно устанавливать только закупочную валютную цену.\n" +
                "Все остальные цены являются расчётными и редактированию не подлежат.");
        alert.showAndWait();
    }
    public static void showError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Ошибка соединения с базой данных.\nРекомендуется перезапустить приложение.");
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
    public static Optional<NewCategory> newCategoryDialog() {
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

        ButtonType buttonTypeOk = new ButtonType("Создать", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewCategory(text1.getText(), text2.getText());
            }
            return null;
        });
        return dialog.showAndWait();
    }
    public static Optional<NewCategory> editCategoryDialog(ArrayList<String> details) {
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

        ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new NewCategory(text1.getText(), text2.getText());
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
        ArrayList<String> allVendors = UtilPack.getAllVendors();
        ArrayList<Series> allSeries = UtilPack.getAllSeries();
        ObservableList<String> productTypesTitles = FXCollections.observableArrayList();
        ObservableList<String> vendorsTitles = FXCollections.observableArrayList();
        ObservableList<String> seriesTitles =  FXCollections.observableArrayList();
        productTypesTitles.addAll(allProductTypes);
        vendorsTitles.addAll(allVendors);
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
        Label inputPriceLabel           = new Label("Закупочная цена: ");
        TextField inputPriceTextField   = new TextField("0.0");
        Label rateLabel                 = new Label("Коэффициент наценки: ");
        TextField rateTextField         = new TextField("0.0");
        Label discountLabel             = new Label("Скидка (%)\n\"+10\"-\"отповая\"-\"диллерская\": ");
        TextField discount1TextField    = new TextField("0.0");
        TextField discount2TextField    = new TextField("0.0");
        TextField discount3TextField    = new TextField("0.0");
        Label availableLabel            = new Label("Доступен\nдля заказа ");
        CheckBox availableCheckBox      = new CheckBox();
        Label outdatedLabel             = new Label("Снят\nс производства ");
        CheckBox outdatedCheckBox       = new CheckBox();
        Label productTypeLabel          = new Label("Тип продукта: ");
        ComboBox<String> productTypeComboBox = new ComboBox<>();
        Label vendorLabel               = new Label("Производитель: ");
        Label categoryLabel               = new Label("Выберите категорию: ");
        ComboBox<String> vendorComboBox = new ComboBox<>();
        StackPane stackPane = new StackPane();
        TreeView<String> treeView = new TreeView<>();
        PCGUIController pcguiController = new PCGUIController();
        TreeView<String> category = pcguiController.buildModalCategoryTree(stackPane, treeView);

        productTypeComboBox.setItems(productTypesTitles);
        productTypeComboBox.setEditable(true);
        vendorComboBox.setItems(vendorsTitles);
        vendorComboBox.setEditable(true);
        seriesComboBox.setItems(seriesTitles);
        seriesComboBox.setEditable(true);
        AutoCompleteComboBoxListener autoCompleteProductTypeComboBox = new AutoCompleteComboBoxListener(productTypeComboBox);
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

        grid.add(descriptionLabel, 1, 11);

        grid.add(descriptionTextArea, 1, 12, 4, 12);
        grid.add(categoryLabel, 5, 1);
        grid.add(stackPane, 5, 2, 6, 22);

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
                    vendorComboBox.getValue(),
                    Double.parseDouble(rateTextField.getText()),
                    Double.parseDouble(discount1TextField.getText()),
                    Double.parseDouble(discount2TextField.getText()),
                    Double.parseDouble(discount3TextField.getText())
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
        Series serie = new Series();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from Series where title = :title");
        query.setParameter("title", selectedSerie);
        List result = query.list();
        for(Iterator iterator = result.iterator(); iterator.hasNext();) {
            serie = (Series) iterator.next();
        }
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        Query query1 = session1.createQuery("from Vendors where id = :id");
        query1.setParameter("id", serie.getVendorId().getId());
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
        titleTextField.setText(serie.getTitle());
        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setText(serie.getDescription());

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
}
