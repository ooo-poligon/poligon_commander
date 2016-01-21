package settings;

import entities.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor Klekotnev on 21.01.2016.
 */
public class PriceCalcSettings {

    private final String settingsType = "PriceCalcSettings";
    private String addCBR;


    public String getAddCBR() {
        return addCBR;
    }

    public void setAddCBR(String addCBR) {
        this.addCBR = addCBR;
    }

    public void saveSetting(String settingTitle, String settingValue) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Settings where title=\'" + settingTitle + "\'").list();
        if (res.size()==0) {
            insertSetting(settingTitle, settingValue);
        } else {
            updateSetting(settingTitle, settingValue);
        }

    }
    public String loadSetting(String settingTitle) {
        String settingValue = "";
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Settings where title=\'" + settingTitle + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Settings settings = (Settings) iterator.next();
            settingValue = settings.getTextValue();
        }
        session.close();
        return settingValue;
    }

    private void insertSetting(String settingTitle, String settingValue) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createSQLQuery("insert into settings (title, kind, text_value) VALUES (\'"
                + settingTitle + "\', \'"
                + settingsType + "\', \'"
                + settingValue + "\')");
        query.executeUpdate();
        tx.commit();
        session.close();
    }

    private void updateSetting(String settingTitle, String settingValue) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("update Settings set textValue = :textValue where title = :title");
        query.setParameter("textValue", settingValue);
        query.setParameter("title", settingTitle);
        query.executeUpdate();
        tx.commit();
        session.close();
    }

}
