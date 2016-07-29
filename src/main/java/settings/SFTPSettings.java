package settings;

import entities.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor Klekotnev on 28.07.2016.
 */

public class SFTPSettings {
    private String serverSFTP;
    private String portSFTP;
    private String userSFTP;
    private String passwordSFTP;
    private final String settingsType = "SFTPSettings";

    public String getServerSFTP() {
        return serverSFTP;
    }

    public void setServerSFTP(String serverSFTP) {
        this.serverSFTP = serverSFTP;
    }

    public String getPortSFTP() {
        return portSFTP;
    }

    public void setPortSFTP(String portSFTP) {
        this.portSFTP = portSFTP;
    }

    public String getUserSFTP() {
        return userSFTP;
    }

    public void setUserSFTP(String userSFTP) {
        this.userSFTP = userSFTP;
    }

    public String getPasswordSFTP() {
        return passwordSFTP;
    }

    public void setPasswordSFTP(String passwordSFTP) {
        this.passwordSFTP = passwordSFTP;
    }

    public String getSettingsType() {
        return settingsType;
    }

    public void saveSetting(String settingTitle, String settingValue) {
        Integer setId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Settings " +
                "where title=\'"+ settingTitle +"\'" +
                "and kind=\'SFTPSettings\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Settings setting = (Settings) iterator.next();
            setId = setting.getId();
        }
        Transaction tx = session.beginTransaction();
        if (setId == 0) {
            Query query = session.createSQLQuery("insert into settings (title, kind, text_value) VALUES (\'"
                    + settingTitle + "\', \'"
                    + settingsType + "\', \'"
                    + settingValue + "\')");
            query.executeUpdate();
        } else {
            Query query = session.createQuery("update Settings set textValue = :textValue" +
                    " where title = :title and kind = :kind");
            query.setParameter("textValue", settingValue);
            query.setParameter("title", settingTitle);
            query.setParameter("kind", settingsType);
            query.executeUpdate();
        }
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

