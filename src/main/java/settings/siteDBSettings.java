package settings;

import entities.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by kataev on 15.01.2016.
 */
public class SiteDBSettings {
    private String addressSiteDB;
    private String portSiteDB;
    private String titleSiteDB;
    private String userSiteDB;
    private String passwordSiteDB;
    private final String settingsType = "SiteDBSettings";

    public String getAddressSiteDB() {
        return addressSiteDB;
    }

    public void setAddressSiteDB(String addressSiteDB) {
        this.addressSiteDB = addressSiteDB;
    }

    public String getPortSiteDB() {
        return portSiteDB;
    }

    public void setPortSiteDB(String portSiteDB) {
        this.portSiteDB = portSiteDB;
    }

    public String getTitleSiteDB() {
        return titleSiteDB;
    }

    public void setTitleSiteDB(String titleSiteDB) {
        this.titleSiteDB = titleSiteDB;
    }

    public String getUserSiteDB() {
        return userSiteDB;
    }

    public void setUserSiteDB(String userSiteDB) {
        this.userSiteDB = userSiteDB;
    }

    public String getPasswordSiteDB() {
        return passwordSiteDB;
    }

    public void setPasswordSiteDB(String passwordSiteDB) {
        this.passwordSiteDB = passwordSiteDB;
    }

    public String getSettingsType() {
        return settingsType;
    }

    public void saveSetting(String settingTitle, String settingValue) {
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
}
