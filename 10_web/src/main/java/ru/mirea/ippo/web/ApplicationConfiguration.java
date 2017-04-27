package ru.mirea.ippo.web;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import ru.mirea.ippo.web.api.JacksonObjectMapperProvider;
import ru.mirea.ippo.web.service.VersionService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public final class ApplicationConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>(super.getClasses());
        classes.add(VersionService.class);
        classes.add(JacksonObjectMapperProvider.class);
        classes.add(ResteasyJackson2Provider.class);
        return classes;
    }
}