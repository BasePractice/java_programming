package ru.mirea.ippo;

import java.sql.Connection;
import java.sql.Statement;

public final class TransactionWork extends PutTransaction {

    private final boolean generateException;

    private TransactionWork(boolean generateException) {
        this.generateException = generateException;
    }

    public static void main(String[] args) throws Exception {
        try {
            new TransactionWork(true).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.flush();
        new TransactionWork(false).start();
        System.out.flush();
    }

    @Override
    protected void inTransaction(Connection c) throws Exception {
        try (Statement stmt = c.createStatement()) {
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
            putProperty(stmt);
        }
        if (generateException)
            throw new RuntimeException("Откат транзакции");
    }
}
