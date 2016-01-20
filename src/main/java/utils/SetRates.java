package utils;

import entities.Products;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor Klekotnev on 14.01.2016.
 */
public class SetRates {
    Double rate;
    Double tenPlusDiscount;
    Double optDiscount;
    Double dealerDiscount;

    public SetRates () {
        this.rate = null;
        this.tenPlusDiscount = null;
        this.optDiscount = null;
        this.dealerDiscount = null;
    }

    public SetRates (Double rate, Double tenPlusDiscount, Double optDiscount, Double dealerDiscount) {
        this.rate = rate;
        this.tenPlusDiscount = tenPlusDiscount;
        this.optDiscount = optDiscount;
        this.dealerDiscount = dealerDiscount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getTenPlusDiscount() {
        return tenPlusDiscount;
    }

    public void setTenPlusDiscount(Double tenPlusDiscount) {
        this.tenPlusDiscount = tenPlusDiscount;
    }

    public Double getOptDiscount() {
        return optDiscount;
    }

    public void setOptDiscount(Double optDiscount) {
        this.optDiscount = optDiscount;
    }

    public Double getDealerDiscount() {
        return dealerDiscount;
    }

    public void setDealerDiscount(Double dealerDiscount) {
        this.dealerDiscount = dealerDiscount;
    }

    public void writeRates(String field, Integer productID) {
        Double value = 0.0;
        switch (field) {
            case "rate":
                value = rate;
                break;
            case "discount1":
                value = tenPlusDiscount;
                break;
            case "discount2":
                value = optDiscount;
                break;
            case "discount3":
                value = dealerDiscount;
                break;
        }
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Products set " + field + " = " + value + " where id = :id");
        query.setParameter("id", productID);
        query.executeUpdate();
        tx.commit();
        session.close();
    }

    public static SetRates getRatesPack(String selectedProduct) {
        SetRates ratesPack = new SetRates();
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Products where title=\'" + selectedProduct + "\'").list();
        for(Iterator iterator = res.iterator(); iterator.hasNext();) {
            Products p = (Products) iterator.next();
            ratesPack = new SetRates(p.getRate(), p.getDiscount1(), p.getDiscount2(), p.getDiscount3());
        }
        session.close();
        return ratesPack;
    }
}
