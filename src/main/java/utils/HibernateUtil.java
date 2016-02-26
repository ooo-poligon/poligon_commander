/*
 * 
 * 
 */
package utils;


import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import settings.SystemConfig;


public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory1();

    private static SessionFactory buildSessionFactory1() {
        /*
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://" + SystemConfig.URL);
        configuration.setProperty("hibernate.connection.username", SystemConfig.USERNAME);
        configuration.setProperty("hibernate.connection.password", SystemConfig.PASSWORD);
        configuration = configuration.configure();
        */


        Configuration configuration = new Configuration().configure(); // configuration
                                                                        // settings
                                                                        // from
                                                                        // hibernate.cfg.xml
    
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();


        serviceRegistryBuilder.applySettings(configuration.getProperties());

        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}

