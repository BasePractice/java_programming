package ru.mirea.ippo;

import java.sql.Connection;

abstract class PutTransaction extends PutConnect {
    @Override
    protected final void afterConnected(Connection c) throws Exception {
        c.setAutoCommit(false);
        try {
            inTransaction(c);
            c.commit();
        } catch (Exception ex) {
            c.rollback();
            throw ex;
        }
    }

    protected abstract void inTransaction(Connection c) throws Exception;
}
