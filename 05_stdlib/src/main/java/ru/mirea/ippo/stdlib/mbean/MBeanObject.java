package ru.mirea.ippo.stdlib.mbean;

import javax.management.*;
import java.lang.management.ManagementFactory;

@SuppressWarnings("WeakerAccess")
final class MBeanObject<E> {
    final MBeanServer server;
    final ObjectName objectName;

    private MBeanObject(ObjectName objectName) {
        this.server = ManagementFactory.getPlatformMBeanServer();
        this.objectName = objectName;
    }

    MBeanObject(String objectName) throws MalformedObjectNameException {
        this(new ObjectName(objectName));
    }

    public final void unregister() throws MBeanRegistrationException, InstanceNotFoundException {
        if (isRegistered())
            server.unregisterMBean(objectName);
    }

    public final void register(E implement) throws NotCompliantMBeanException, InstanceAlreadyExistsException,
            MBeanRegistrationException {
        if (!isRegistered())
            server.registerMBean(implement, objectName);
    }

    public final ObjectInstance loadInstance() throws InstanceNotFoundException {
        return server.getObjectInstance(objectName);
    }

    public final MBeanInfo info() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
        return server.getMBeanInfo(objectName);
    }

    public final Object invoke(String operationName, Object[] args, String[] signatures) throws MBeanException,
            InstanceNotFoundException, ReflectionException {
        return server.invoke(objectName, operationName, args, signatures);
    }

    public final boolean isRegistered() {
        return server.isRegistered(objectName);
    }

    @SuppressWarnings("unchecked")
    public final <T> T as(Class<T> clazz) throws InstanceNotFoundException, MBeanException, ReflectionException {
        if (server.isInstanceOf(objectName, clazz.getCanonicalName())) {
            return JMX.newMBeanProxy(server, objectName, clazz);
        }
        return null;
    }
}
