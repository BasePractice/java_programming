package ru.mirea.ippo.web.service;

import ru.mirea.ippo.web.api.VersionApi;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

public final class VersionService extends DataSourceService implements VersionApi {

    public VersionService() {
        super(new String[] {"jboss/datasources/SimpleDataSource"});
    }

    @Override
    public Response version(String product, String type) {
        try (Connection c = ds.getConnection()) {
            return Response.status(Response.Status.OK).entity(new Version(1, 0, 3, product)).build();
        } catch (SQLException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new DatabaseException(ex)).build();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Version {
        public final int major;
        public final int minor;
        public final int build;
        public final String product;

        public Version(int major, int minor, int build, String product) {
            this.major = major;
            this.minor = minor;
            this.build = build;
            this.product = product;
        }

        /**NOTICE: Only for freemarker */
        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getBuild() {
            return build;
        }

        public String getProduct() {
            return product;
        }
    }
}
