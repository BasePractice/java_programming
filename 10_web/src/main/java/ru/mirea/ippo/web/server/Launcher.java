package ru.mirea.ippo.web.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

import javax.sql.DataSource;

public final class Launcher {
    public static void main(String[] args) throws Exception {
        final Server server;
        if (args.length >= 1) {
            System.out.println("Загружаемся с war файла");
            server = new Server(9090);
            Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
            classlist.addAfter(
                    "org.eclipse.jetty.webapp.FragmentConfiguration",
                    "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                    "org.eclipse.jetty.plus.webapp.PlusConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration"
            );
            WebAppContext context = new WebAppContext();
            context.setWar(args[0]);
            context.setContextPath("/");
            context.setParentLoaderPriority(true);
            final DataSource dataSource = createDataSource();
            new org.eclipse.jetty.plus.jndi.Resource(context, "jboss/datasources/SimpleDataSource", dataSource);
            server.setHandler(context);
        } else {
            final Resource confFile = Resource.newResource("jetty.xml");
            XmlConfiguration configuration = new XmlConfiguration(confFile.getInputStream());
            server = (Server) configuration.configure();
        }
        server.start();
        server.join();
    }

    public static DataSource createDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:stub;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }
}
