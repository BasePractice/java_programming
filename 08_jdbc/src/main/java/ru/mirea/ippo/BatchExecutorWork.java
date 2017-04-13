package ru.mirea.ippo;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class BatchExecutorWork extends PutConnect {
    public static void main(String[] args) throws Exception {
        new BatchExecutorWork().start();
    }

    @Override
    protected void afterConnected(Connection c) throws Exception {
        try (PreparedStatement stmt = c.prepareStatement("INSERT INTO property(value) VALUES(?)")) {
            stmt.setString(1, "PROPERTY1");
            stmt.addBatch();
            stmt.setString(1, "PROPERTY2");
            stmt.addBatch();
            stmt.setString(1, "PROPERTY3");
            stmt.addBatch();
            stmt.executeBatch();
        }
    }
}
