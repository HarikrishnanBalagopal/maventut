package com.mycompany.app;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class CustomerDao {
    public void save(Customer customer) {

        Transaction transaction = null;

        // auto close session object
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(customer);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
