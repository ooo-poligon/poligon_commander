package utils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by Igor Klekotnev on 14.01.2016.
 */
public class SetRates {
    Double rate;
    Double tenPlusDiscount;
    Double optDiscount;
    Double dealerDiscount;

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
}
