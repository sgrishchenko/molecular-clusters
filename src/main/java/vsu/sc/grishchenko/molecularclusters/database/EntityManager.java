package vsu.sc.grishchenko.molecularclusters.database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.function.Supplier;

public class EntityManager {
    private static SessionFactory factory;
    private static Session session;

    private EntityManager() {
    }

    public static void initialise() {
        try {
            factory = new Configuration()
                    .configure(EntityManager.class.getResource("/hibernate/hibernate.cfg.xml"))
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
        } finally {
            session.flush();
        }
        return result;
    }

    public static void save(Object entity) {
        transactional(() -> session.save(entity));
    }

    public static void update(Object entity) {
        transactional(() -> {
            session.update(entity);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T find(long id, Class<T> tClass) {
        return transactional(() -> (T) session.createCriteria(tClass)
                .add(Restrictions.idEq(id))
                .uniqueResult());
    }

    @SuppressWarnings("unchecked")
    public static <T> T find(String fieldName, Object value, Class<T> tClass) {
        return transactional(() -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .uniqueResult());
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> findList(String fieldName, Object value, Class<T> tClass) {
        return transactional(() -> (List<T>) session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .list());
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> findAll(Class<T> tClass) {
        return transactional(() -> (List<T>) session.createCriteria(tClass)
                .list());
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean isExists(String fieldName, Object value, Class<T> tClass) {
        return transactional(() -> session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .setProjection(Projections.id())
                .setMaxResults(1)
                .list() != null);
    }

    public static void close() {
        session.close();
        factory.close();
    }
}
