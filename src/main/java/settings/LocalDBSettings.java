package settings;

        import entities.Settings;
        import org.hibernate.Query;
        import org.hibernate.Session;
        import org.hibernate.Transaction;
        import utils.HibernateUtil;

        import java.util.Iterator;
        import java.util.List;

/**
 * Created by Igor Klekotnev on 02.02.2016.
 */
public class LocalDBSettings {
    private String addressLocalDB;
    private String portLocalDB;
    private String titleLocalDB;
    private String userLocalDB;
    private String passwordLocalDB;
    private final String settingsType = "LocalDBSettings";

    public String getAddressLocalDB() {
        return addressLocalDB;
    }

    public void setAddressLocalDB(String addressLocalDB) {
        this.addressLocalDB = addressLocalDB;
    }

    public String getPortLocalDB() {
        return portLocalDB;
    }

    public void setPortLocalDB(String portLocalDB) {
        this.portLocalDB = portLocalDB;
    }

    public String getTitleLocalDB() {
        return titleLocalDB;
    }

    public void setTitleLocalDB(String titleLocalDB) {
        this.titleLocalDB = titleLocalDB;
    }

    public String getUserLocalDB() {
        return userLocalDB;
    }

    public void setUserLocalDB(String userLocalDB) {
        this.userLocalDB = userLocalDB;
    }

    public String getPasswordLocalDB() {
        return passwordLocalDB;
    }

    public void setPasswordLocalDB(String passwordLocalDB) {
        this.passwordLocalDB = passwordLocalDB;
    }

    public String getSettingsType() {
        return settingsType;
    }

    public void saveSetting(String settingTitle, String settingValue) {
        Integer setId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Settings " +
                "where title=\'"+ settingTitle +"\'" +
                "and kind=\'LocalDBSettings\'").list();
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
