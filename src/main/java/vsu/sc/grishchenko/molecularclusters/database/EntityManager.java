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

/**
 * <p>Класс с набором статических методов для управления доступом к сущностям базы данных.</p>
 *
 * @author Грищенко Сергей
 */
public class EntityManager {
    /**
     * <p>Объект, создающий сессию соединения с базой данных.</p>
     */
    private static SessionFactory factory;
    /**
     * <p>Объект сессии соединения с базой данных.</p>
     */
    private static Session session;

    private EntityManager() {
    }

    /**
     * <p>Метод инициализации соединения с базой данных.</p>
     */
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

    /**
     * <p>Транзакционное выполнения некоторого действия с объектами базы данных.</p>
     *
     * @param <T>    тип объекта базы данных
     * @param action действие, которое необходимо выполнить
     * @return результат выполнения действия.
     */
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

    /**
     * <p>Сохраняет объект в базе данных.</p>
     *
     * @param entity объект, который требуется сохранить
     */
    public static void save(Object entity) {
        transactional(() -> session.save(entity));
    }

    /**
     * <p>Обновляет объект в базе данных.</p>
     *
     * @param entity объект, который требуется обновить
     */
    public static void update(Object entity) {
        transactional(() -> {
            session.update(entity);
            return null;
        });
    }

    /**
     * <p>Обновляет/сохраняет объект в базе данных.</p>
     *
     * @param entity объект, который требуется сохранить/обновить
     */
    public static void saveOrUpdate(Object entity) {
        transactional(() -> {
            session.saveOrUpdate(entity);
            return null;
        });
    }

    /**
     * <p>Получение объекта по индетификатору.</p>
     *
     * @param <T>    тип объекта базы данных
     * @param id     идентификатор
     * @param tClass класс объекта
     * @return искомый объект или <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> T find(long id, Class<T> tClass) {
        return transactional(() -> (T) session.createCriteria(tClass)
                .add(Restrictions.idEq(id))
                .uniqueResult());
    }

    /**
     * <p>Получение объекта по значению в определенном поле.</p>
     *
     * @param <T>       тип объекта базы данных
     * @param fieldName имя поля
     * @param value     эталонное значение
     * @param tClass    класс объекта
     * @return искомый объект или <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> T find(String fieldName, Object value, Class<T> tClass) {
        return transactional(() -> (T) session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .uniqueResult());
    }

    /**
     * <p>Получение списка объектов по значению в определенном поле.</p>
     *
     * @param <T>       тип объекта базы данных
     * @param fieldName имя поля
     * @param value     эталонное значение
     * @param tClass    класс объекта
     * @return список искомых объект или пустой список.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> findList(String fieldName, Object value, Class<T> tClass) {
        return transactional(() -> (List<T>) session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .list());
    }

    /**
     * <p>Получение списка всех объектов определенного класса.</p>
     *
     * @param <T>    тип объекта базы данных
     * @param tClass класс объекта
     * @return список искомых объект или пустой список.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> findAll(Class<T> tClass) {
        return transactional(() -> (List<T>) session.createCriteria(tClass)
                .list());
    }

    /**
     * <p>Проверка существования объектов по значению в определенном поле.</p>
     *
     * @param <T>       тип объекта базы данных
     * @param fieldName имя поля
     * @param value     эталонное значение
     * @param tClass    класс объекта
     * @return <code>true</code>, если существуют объекты, удовлетворяющие условиям поиска, иначе <code>false</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean isExists(String fieldName, Object value, Class<T> tClass) {
        return !transactional(() -> session.createCriteria(tClass)
                .add(Restrictions.eq(fieldName, value))
                .setProjection(Projections.id())
                .setMaxResults(1)
                .list().isEmpty());
    }

    /**
     * <p>Метод закрытия соединения с базой данных.</p>
     */
    public static void close() {
        session.close();
        factory.close();
    }
}
