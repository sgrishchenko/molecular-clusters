package vsu.sc.grishchenko.molecularclusters.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Supplier;

public class EntityManager {
    private static SessionFactory factory;
    private static Session session;
    private static EntityManager instance = new EntityManager();

    private EntityManager() {
        try {
            factory = new Configuration()
                    .configure(getClass().getResource("/hibernate/hibernate.cfg.xml"))
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        session = factory.openSession();
    }

    private static <T> T transactional(Supplier<T> action) {
        Transaction tx = null;
        T result = null;
        try {
            tx = session.beginTransaction();

            result = action.get();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return result;
    }

    public static void save(Object entity) {
        transactional(() -> session.save(entity));
    }

    public static void close() {
        session.close();
        factory.close();
    }
}
