package ru.mirea.ippo.stdlib.mbean;

import javax.management.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

final class DefaultConfigurationServiceMBean
        extends StandardMBean
        implements ConfigurationServiceMBean, ConfigurationService, NotificationBroadcaster {
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final AtomicReference<String> message = new AtomicReference<>("Wait...");
    private final MBeanObject<ConfigurationServiceMBean> object;
    private final Lock lock;
    private final Condition condition;

    DefaultConfigurationServiceMBean(Lock lock, Condition condition)
            throws NotCompliantMBeanException, MalformedObjectNameException {
        super(ConfigurationServiceMBean.class);
        this.lock = lock;
        this.condition = condition;
        this.object = new MBeanObject<>(OBJECT_NAME);
    }

    void register()
            throws MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException {
        object.register(this);
    }

    void unregister() throws InstanceNotFoundException, MBeanRegistrationException {
        object.unregister();
    }

    boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public String getMessage() {
        return message.get();
    }

    @Override
    public void setMessage(String message) {
        fireEvent(EVENT_CHANGE_MESSAGE, message);
        this.message.set(message);
        signal();
    }

    @Override
    public void stop() {
        fireEvent(EVENT_STOP_SERVICE);
        isRunning.set(false);
        signal();
    }

    private void signal() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private static final class NotifyObject {
        final NotificationListener listener;
        final NotificationFilter filter;
        final Object handback;

        private NotifyObject(NotificationListener listener, NotificationFilter filter, Object handlback) {
            this.listener = listener;
            this.filter = filter;
            this.handback = handlback;
        }

        void notify(Notification notification) {
            if (filter != null) {
                if (filter.isNotificationEnabled(notification)) {
                    listener.handleNotification(notification, handback);
                }
            } else if (listener != null) {
                listener.handleNotification(notification, handback);
            }
        }

    }

    private final Map<NotificationListener, NotifyObject> listeners = new HashMap<>();
    private final AtomicLong notificationSequence = new AtomicLong(0);

    private Notification createNotification(String type, String message) {
        return new Notification(type, this, notificationSequence.incrementAndGet(), new Date().getTime(), message);
    }

    private Notification createNotification(String type) {
        return createNotification(type, "");
    }

    private void fireEvent(String type, String message) {
        Notification notification = createNotification(type, message);
        for (NotifyObject object : listeners.values()) {
            object.notify(notification);
        }
    }

    private void fireEvent(String type) {
        fireEvent(type, "");
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
            throws IllegalArgumentException {
        listeners.put(listener, new NotifyObject(listener, filter, handback));
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        NotifyObject object = listeners.remove(listener);
        if (object != null) {
            object.notify(createNotification(EVENT_REMOVE_LISTENER));
        }
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }
}
