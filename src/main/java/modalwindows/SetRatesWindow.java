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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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

    public void showModalWindow() {
        Dialog<SetRates> dialog = new Dialog<>();
        dialog.setTitle("Установка коэффициентов");
        dialog.setHeaderText("Коэффициенты, установленные в этом окне\nбудут применены" +
                "ко всей группе товаров\n из которой вызван данноый диалог.");
        dialog.setResizable(false);

        Label label1 = new Label("Коэффициент наценки:  ");
        Label label2 = new Label("Скидка от розничной цены:  ");
        Label label3 = new Label("Скидка 10+:  ");
        Label label4 = new Label("Скидка оптовая:  ");
        Label label5 = new Label("Скидка диллерская:  ");
        TextField text1 = new TextField();
        TextField text3 = new TextField();
        TextField text4 = new TextField();
        TextField text5 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);
        grid.add(label4, 1, 4);
        grid.add(text4, 2, 4);
        grid.add(label5, 1, 5);
        grid.add(text5, 2, 5);
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
        Double rate      = result.get().getRate();
        Double discount1 = result.get().getTenPlusDiscount();
        Double discount2 = result.get().getOptDiscount();
        Double discount3 = result.get().getDealerDiscount();
        SetRates setRates = new SetRates(rate, discount1, discount2, discount3);
        if (result.isPresent()) {
            if (selectedNodeID==null) {
                setRates.writeRates("rate",      selectedProductID);
                setRates.writeRates("discount1", selectedProductID);
                setRates.writeRates("discount2", selectedProductID);
                setRates.writeRates("discount3", selectedProductID);
                //res = session.createQuery("from Products where id =" + selectedProductID).list();
            } else {
                completeChildrenProducts(selectedNodeID).stream().forEach((productID) -> {
                    setRates.writeRates("rate",      productID);
                    setRates.writeRates("discount1", productID);
                    setRates.writeRates("discount2", productID);
                    setRates.writeRates("discount3", productID);
                });
            }
        }
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
