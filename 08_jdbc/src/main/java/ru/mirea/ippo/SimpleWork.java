package ru.mirea.ippo;

import java.sql.Statement;

public final class SimpleWork extends PutConnect {
    public static void main(String[] args) throws Exception {
        new SimpleWork().start();
    }

    @Override
    protected void statement(Statement stmt) throws Exception {
        putProperty(stmt);
    }
}
