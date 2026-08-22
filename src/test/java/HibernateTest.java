import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class HibernateTest {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    }
}
