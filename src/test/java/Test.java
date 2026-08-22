import lk.jiat.calivon.entity.Status;
import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Test {
    public static void main(String[] args) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Status.Type[] values = Status.Type.values();
        Transaction transaction = s.beginTransaction();
        for (Status.Type t : values) {
            Status status = new Status();
            status.setValue(t.name());
            s.persist(status);
        }
        transaction.commit();
    }
}
