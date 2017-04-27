package ru.mirea.ippo.web.service;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;

abstract class DataSourceService {
    final DataSource ds;

    DataSourceService(String[] datasourceNames) {
        try {
            Context context = new InitialContext();
            ds = resolveDataSource(context, datasourceNames);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource resolveDataSource(Context context, String[] datasourceNames) {
        for (String dataSource : datasourceNames) {
            try {
                return (DataSource) context.lookup(dataSource);
            } catch (Exception ex) {
                //Skip
            }
        }
        throw new RuntimeException("Не найдены DataSource");
    }

    @SuppressWarnings("WeakerAccess")
    public final static class DatabaseException {

        public final int code;
        public final String message;

        DatabaseException(SQLException ex) {
            this.code = ex.getErrorCode();
            this.message = ex.getMessage();
        }
    }
}
