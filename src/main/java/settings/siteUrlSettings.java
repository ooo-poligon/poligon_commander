package settings;

import entities.Settings;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Igor Klekotnev on 26.02.2016.
 */
public class SiteUrlSettings {
    private final String settingsTitle = "siteUrl";
    private final String settingsKind = "ProgramSettings";

    public void saveSetting(String settingValue) {
        Integer setId = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("from Settings " +
                "where title=\'" + settingsTitle + "\'" +
                "and   kind =\'" + settingsKind  + "\'").list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Settings setting = (Settings) iterator.next();
            setId = setting.getId();
        }
        Transaction tx = session.beginTransaction();
        if (setId == 0) {
            Query query = session.createSQLQuery("insert into settings (title, kind, text_value) VALUES (\'"
                    + settingsTitle + "\', \'"
                    + settingsKind + "\', \'"
                    + settingValue + "\')");
            query.executeUpdate();
        } else {
            Query query = session.createQuery("update Settings set textValue = :textValue" +
                    " where title = :title and kind = :kind");
            query.setParameter("textValue", settingValue);
            query.setParameter("title", settingsTitle);
            query.setParameter("kind", settingsKind);
            query.executeUpdate();
        }
        tx.commit();
        session.close();
    }
    public String loadSetting() {
        String settingValue = "";
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            List res = session.createQuery("from Settings where title=\'" + settingsTitle + "\'").list();
            for (Iterator iterator = res.iterator(); iterator.hasNext(); ) {
                Settings settings = (Settings) iterator.next();
                settingValue = settings.getTextValue();
            }
            session.close();
        } catch (NullPointerException ne) {}
        return settingValue;
    }
}
