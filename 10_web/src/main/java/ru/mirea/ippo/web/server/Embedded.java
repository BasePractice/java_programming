package ru.mirea.ippo.web.server;

import freemarker.ext.servlet.FreemarkerServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import ru.mirea.ippo.web.service.VersionService;

import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

import static org.eclipse.jetty.webapp.WebInfConfiguration.CONTAINER_JAR_PATTERN;
import static ru.mirea.ippo.web.server.Launcher.createDataSource;

public final class Embedded {

    public static void main(String[] args) throws Exception {
        final ServerContainer container = new ServerContainer();
        startRest(container);
        startHello(container);
        startFtl(container);
        startJsp(container);
        start(container);
    }

    private static void startJsp(ServerContainer container) throws Exception {
        container.addServlet("jsp", "*.jsp", new JettyJspServlet(), new HashMap<String, String>() {
            {
                put("logVerbosityLevel", "DEBUG");
                put("fork", "false");
                put("keepgenerated", "true");
            }
        });
        container.addServlet("jsp_sample", "/jsp_sample", new JspSampleServlet(), null);
    }

    private static void startFtl(ServerContainer container) throws Exception {
        container.addServlet("freemarker", "*.ftl", new FreemarkerServlet(), new HashMap<String, String>() {
            {
                put("TemplatePath", "/templates");
                put("NoCache", "true");
                put("ExceptionOnMissingTemplate", "true");
                put("incompatible_improvements", "2.3.23");
                put("template_exception_handler", "rethrow");
                put("template_update_delay", "30 s");
                put("default_encoding", "UTF-8");
                put("output_encoding", "UTF-8");
                put("locale", "ru_RU");
                put("number_format", "0.########");
            }
        });
        container.addServlet("ftl_sample", "/ftl_sample", new FreemarkerSampleServlet(), null);
        container.setBaseResource(Embedded.class.getResource("/"));

    }

    private static abstract class SampleServlet extends HttpServlet {
        private final String template;

        private SampleServlet(String template) {
            this.template = template;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");

            List<VersionService.Version> versions = new LinkedList<>();
            versions.add(new VersionService.Version(1, 0, 0, "freemarker"));
            versions.add(new VersionService.Version(1, 0, 1, "freemarker"));
            versions.add(new VersionService.Version(1, 0, 2, "freemarker"));
            versions.add(new VersionService.Version(1, 1, 0, "freemarker"));
            request.setAttribute("versions", versions);
            request.setAttribute("user", "Username");
            request.getRequestDispatcher(template).forward(request, response);
        }
    }

    private static final class FreemarkerSampleServlet extends SampleServlet {
        private FreemarkerSampleServlet() {
            super("/index.ftl");
        }
    }

    private static final class JspSampleServlet extends SampleServlet {
        private JspSampleServlet() {
            super("/jsp/index.jsp");
        }
    }

    private static void startHello(ServerContainer container) throws Exception {
        container.addServlet("hello", "/hello", new HelloServlet(), null);
    }

    private static void startRest(ServerContainer container) throws Exception {
        container.addContextListener(new ResteasyBootstrap());
        container.addServlet("rest", "/rest/*", new HttpServletDispatcher(), new HashMap<String, String>() {
            {
                put("javax.ws.rs.Application", "ru.mirea.ippo.web.ApplicationConfiguration");
            }
        });
        container.addContextParam("resteasy.servlet.mapping.prefix", "/rest");
        container.addResource("jboss/datasources/SimpleDataSource", createDataSource());
    }

    private static void start(final ServerContainer container) throws Exception {
        container.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    container.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(MediaType.TEXT_HTML);
            PrintWriter writer = resp.getWriter();
            writer.println("<h1>HELLO</h1>");
            writer.flush();
        }
    }

    private static final class ServerContainer {
        private final ServletContextHandler context = new ServletContextHandler();
        private final Server server;

        ServerContainer() throws Exception {
            this(Embedded.class.getResourceAsStream("/jetty.xml"));
        }

        ServerContainer(InputStream stream) throws Exception {
            XmlConfiguration configuration = new XmlConfiguration(stream);
            this.server = (Server) configuration.configure();
            setup();
        }

        private void setup() throws ServletException {
//            context.setAttribute(CONTAINER_INITIALIZERS, jspInitializers());
            context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
            context.setAttribute(CONTAINER_JAR_PATTERN,
                    ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$|jstl.*\\.jar$");
            context.setClassLoader(Embedded.class.getClassLoader());
            HashSessionIdManager idmanager = new HashSessionIdManager();
            server.setSessionIdManager(idmanager);
            HashSessionManager manager = new HashSessionManager();
            context.setSessionHandler(new SessionHandler(manager));
            Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
            classlist.addAfter(
                    "org.eclipse.jetty.webapp.FragmentConfiguration",
                    "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                    "org.eclipse.jetty.plus.webapp.PlusConfiguration",
                    "org.eclipse.jetty.annotations.AnnotationConfiguration"
            );
            classlist.remove("org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
            classlist.remove("org.eclipse.jetty.webapp.WebInfConfiguration");
            classlist.remove("org.eclipse.jetty.webapp.WebXmlConfiguration");
            server.setHandler(context);
        }

        void addResource(String name, Object resource) throws NamingException {
            new Resource(context, name, resource);
        }

        void addContextParam(String name, String value) {
            context.setInitParameter(name, value);
        }

        void addContextListener(ServletContextListener listener) {
            context.addEventListener(listener);
        }

        void setBaseResource(URL baseResource) {
            context.setBaseResource(JarResource.newResource(baseResource));
        }

        void addServlet(String name, String path, Servlet servlet,
                        Map<String, String> servletInitParameters) throws NamingException {
            final ServletHolder holder = new ServletHolder(name, servlet);
            if (servletInitParameters != null) {
                for (Map.Entry<String, String> entry : servletInitParameters.entrySet()) {
                    holder.setInitParameter(entry.getKey(), entry.getValue());
                }
            }
            context.addServlet(holder, path);
        }

        void start() throws Exception {
            ContextHandler.Context context = this.context.getServletContext();
            context.setAttribute(JarScanner.class.getName(), new StandardJarScanner());
            new JettyJasperInitializer().onStartup(new HashSet<>(), context);
            server.start();
        }

        void stop() throws Exception {
            server.stop();
        }

        void setContextAttribute(String name, Object value) {
            context.setAttribute(name, value);
        }
    }

}
