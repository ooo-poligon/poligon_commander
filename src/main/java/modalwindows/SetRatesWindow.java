package modalwindows;

import entities.Categories;
import entities.Products;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;
import utils.NewCategory;
import utils.SetRates;

import java.util.*;

/**
 * Created by Igor Klekotnev on 14.01.2016.
 */
public class SetRatesWindow {
    Integer selectedNodeID;
    Integer selectedProductID;
    ArrayList<Integer> completeChildrenProducts = new ArrayList<>();

    public Integer getSelectedNodeID() {
        return selectedNodeID;
    }

    public void setSelectedNodeID(Integer selectedNodeID) {
        this.selectedNodeID = selectedNodeID;
    }

    public Integer getSelectedProductID() {
        return selectedProductID;
    }

    public void setSelectedProductID(Integer selectedProductID) {
        this.selectedProductID = selectedProductID;
    }

    public SetRatesWindow(Integer selectedNodeID, Integer selectedProductID) {
        this.selectedNodeID = selectedNodeID;
        this.selectedProductID = selectedProductID;
    }

    private SetRates getExistingRatesPack(String tableName, Integer itemId) {
        SetRates ratesPack = new SetRates();
        if (tableName.equals("categories")) {
            try {
                ArrayList<Integer> allItemIds = completeChildrenProducts(itemId);
                if (allItemIds.size()>1) {
                    for (int i = 1; i < allItemIds.size(); i++) {
                        if (!(getSetRatePack(ratesPack, allItemIds.get(i)).getRate().equals(getSetRatePack(ratesPack, allItemIds.get(i-1)).getRate() ))) {
                            ratesPack.setRate(-999.0);
                        } else if(!(getSetRatePack(ratesPack, allItemIds.get(i)).getTenPlusDiscount().equals(getSetRatePack(ratesPack, allItemIds.get(i-1)).getTenPlusDiscount() ))) {
                            ratesPack.setTenPlusDiscount(-999.0);
                        } else if (!(getSetRatePack(ratesPack, allItemIds.get(i)).getOptDiscount().equals(getSetRatePack(ratesPack, allItemIds.get(i-1)).getOptDiscount() ))) {
                            ratesPack.setOptDiscount(-999.0);
                        } else if (!(getSetRatePack(ratesPack, allItemIds.get(i)).getDealerDiscount().equals(getSetRatePack(ratesPack, allItemIds.get(i-1)).getDealerDiscount() ))) {
                            ratesPack.setDealerDiscount(-999.0);
                        } else {
                            ratesPack = getSetRatePack(ratesPack, allItemIds.get(i));
                        }
                    }
                } else {
                    ratesPack = getSetRatePack(ratesPack, allItemIds.get(0));
                }
            } catch (IndexOutOfBoundsException ioobe) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Внимание!");
                alert.setHeaderText("Выбранная категория не содержит элементов!");
                alert.setContentText("Любые внесённые данные не будут сохранены,\nтак как их не к чему применить.\n" +
                "В следующем окне нажмите кнопку \"Отмена\"\nи выберите другую категорию. ");
                alert.showAndWait();
            }
            return ratesPack;
        } else if (tableName.equals("products")) {
            ratesPack = getSetRatePack(ratesPack, itemId);
        }
        return ratesPack;
    }

    private SetRates getSetRatePack(SetRates ratesPack, Integer itemId) {
        SetRates rp= new SetRates();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where id=" + itemId).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            rp = new SetRates(
                    product.getRate(),
                    product.getDiscount1(),
                    product.getDiscount2(),
                    product.getDiscount3()
            );
        }
        session.close();
        return rp;
    }

    private void alertDifferentRates() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Установленные значения не одинаковы для всех элементов категории!");
        alert.setContentText("Любые внесённые данные при сохранении перезапишут все уникальные\n" +
                "настройки, установленные на данный момент!");
        alert.showAndWait();
    }

    public void showModalWindow() {
        Dialog<SetRates> dialog = new Dialog<>();
        dialog.setTitle("Установка коэффициентов");
        dialog.setHeaderText("Коэффициенты, установленные в этом окне\nбудут применены " +
                "ко всей группе товаров,\n из которой вызван данноый диалог.");
        dialog.setResizable(false);

        Label label1 = new Label("Коэффициент наценки:  ");
        Label label2 = new Label("Скидка от розничной цены:  ");
        Label label3 = new Label("Скидка 10+ (%):  ");
        Label label4 = new Label("Скидка оптовая (%):  ");
        Label label5 = new Label("Скидка диллерская (%):  ");
        Label label6 = new Label("      Текущие значения:");
        Label br = new Label("");
        Label br1 = new Label("");
        Label br2 = new Label("");
        Label br3 = new Label("");
        Label br4 = new Label("");
        Label br5 = new Label("");
        TextField text1 = new TextField();
        TextField text3 = new TextField();
        TextField text4 = new TextField();
        TextField text5 = new TextField();
        SetRates currentlySetRates = new SetRates();
        if (selectedNodeID == null) {
            currentlySetRates = getExistingRatesPack("products", selectedProductID);
        } else {
            currentlySetRates = getExistingRatesPack("categories", selectedNodeID);
        }
        try {
            if ( currentlySetRates.getRate().equals(-999.0)) {
                alertDifferentRates();
                text1.setText("разные значения");
                text3.setText(currentlySetRates.getTenPlusDiscount().toString());
                text4.setText(currentlySetRates.getOptDiscount().toString());
                text5.setText(currentlySetRates.getDealerDiscount().toString());
            } else if (currentlySetRates.getTenPlusDiscount().equals(-999.0)) {
                alertDifferentRates();
                text1.setText(currentlySetRates.getRate().toString());
                text3.setText("разные значения");
                text4.setText(currentlySetRates.getOptDiscount().toString());
                text5.setText(currentlySetRates.getDealerDiscount().toString());
            } else if(currentlySetRates.getOptDiscount().equals(-999.0)) {
                alertDifferentRates();
                text1.setText(currentlySetRates.getRate().toString());
                text3.setText(currentlySetRates.getTenPlusDiscount().toString());
                text4.setText("разные значения");
                text5.setText(currentlySetRates.getDealerDiscount().toString());
            } else if(currentlySetRates.getDealerDiscount().equals(-999.0)) {
                alertDifferentRates();
                text1.setText(currentlySetRates.getRate().toString());
                text3.setText(currentlySetRates.getTenPlusDiscount().toString());
                text4.setText(currentlySetRates.getOptDiscount().toString());
                text5.setText("разные значения");
            } else {
                text1.setText(currentlySetRates.getRate().toString());
                text3.setText(currentlySetRates.getTenPlusDiscount().toString());
                text4.setText(currentlySetRates.getOptDiscount().toString());
                text5.setText(currentlySetRates.getDealerDiscount().toString());
            }
        } catch (NullPointerException ne) {}

        GridPane grid = new GridPane();
        grid.add(label6, 2, 1);
        grid.add(br, 1, 2);
        grid.add(label1, 1, 3);
        grid.add(text1, 2, 3);
        grid.add(br1, 1, 4);
        grid.add(label2, 1, 5);
        grid.add(br2, 1, 6);
        grid.add(label3, 1, 7);
        grid.add(text3, 2, 7);
        grid.add(br3, 1, 8);
        grid.add(label4, 1, 9);
        grid.add(text4, 2, 9);
        grid.add(br4, 1, 10);
        grid.add(label5, 1, 11);
        grid.add(text5, 2, 11);
        grid.add(br5, 1, 12);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Установить", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new SetRates(Double.parseDouble(text1.getText()),
                                    Double.parseDouble(text3.getText()),
                                    Double.parseDouble(text4.getText()),
                                    Double.parseDouble(text5.getText()));
            }
            return null;
        });
        Optional<SetRates> result = dialog.showAndWait();
        try {
            Double rate = result.get().getRate();
            Double discount1 = result.get().getTenPlusDiscount();
            Double discount2 = result.get().getOptDiscount();
            Double discount3 = result.get().getDealerDiscount();
            SetRates setRates = new SetRates(rate, discount1, discount2, discount3);
            if (result.isPresent()) {
                if (selectedNodeID == null) {
                    setRates.writeRates("rate", selectedProductID);
                    setRates.writeRates("discount1", selectedProductID);
                    setRates.writeRates("discount2", selectedProductID);
                    setRates.writeRates("discount3", selectedProductID);
                    //res = session.createQuery("from Products where id =" + selectedProductID).list();
                } else {
                    completeChildrenProducts(selectedNodeID).stream().forEach((productID) -> {
                        setRates.writeRates("rate", productID);
                        setRates.writeRates("discount1", productID);
                        setRates.writeRates("discount2", productID);
                        setRates.writeRates("discount3", productID);
                    });
                }
            }
        } catch (NoSuchElementException nse) {}
    }

    private ArrayList<Integer> completeChildrenProducts(Integer catID) {
        ArrayList<Integer> childrenProducts = new ArrayList<>();
        ArrayList<Integer> childrenCategories = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List response = session.createQuery("From Products where categoryId=" + catID).list();
        for (Iterator iterator = response.iterator(); iterator.hasNext();) {
            Products product = (Products) iterator.next();
            childrenProducts.add(product.getId());
        }
        session.close();
        Session session1 = HibernateUtil.getSessionFactory().openSession();
        List response1 = session1.createQuery("From Categories where parent=" + catID).list();
        for (Iterator iterator = response1.iterator(); iterator.hasNext();) {
            Categories category = (Categories) iterator.next();
            childrenCategories.add(category.getId());
        }
        session1.close();
        if        ( childrenProducts.isEmpty() &&  childrenCategories.isEmpty()) {
            return completeChildrenProducts;
        } else if (!childrenProducts.isEmpty() &&  childrenCategories.isEmpty()) {
            completeChildrenProducts.addAll(childrenProducts);
            //return completeChildrenProducts;
        } else if ( childrenProducts.isEmpty() && !childrenCategories.isEmpty()) {
            childrenCategories.stream().forEach((cat) -> {
                completeChildrenProducts(cat);
            });
        } else {
            completeChildrenProducts.addAll(childrenProducts);
            childrenCategories.stream().forEach((cat) -> {
                completeChildrenProducts(cat);
            });
        }
        return completeChildrenProducts;
    }
}
