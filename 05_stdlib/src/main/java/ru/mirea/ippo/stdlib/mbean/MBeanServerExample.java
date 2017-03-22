package ru.mirea.ippo.stdlib.mbean;

import javax.management.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.mirea.ippo.stdlib.mbean.ConfigurationServiceMBean.EVENT_CHANGE_MESSAGE;

/**
 * -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.port=1088
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 */
public final class MBeanServerExample {

    private static final CountDownLatch STOP = new CountDownLatch(1);

    private static final class ConfigurationServer implements Runnable {
        private static final Lock lock = new ReentrantLock();
        private static final Condition condition = lock.newCondition();

        @Override
        public void run() {
            try {
                DefaultConfigurationServiceMBean serviceMBean = new DefaultConfigurationServiceMBean(lock, condition);
                serviceMBean.register();
                STOP.countDown();
                while (serviceMBean.isRunning()) {
                    waitSignal();
                    System.out.println(serviceMBean.getMessage());
                }
                serviceMBean.unregister();
            } catch (MBeanRegistrationException | InstanceAlreadyExistsException | NotCompliantMBeanException |
                    MalformedObjectNameException | InstanceNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean waitSignal() {
            lock.lock();
            try {
                return condition.await(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //Empty
            } finally {
                lock.unlock();
            }
            return false;
        }
    }

    public static void main(String[] args)
            throws NotCompliantMBeanException, MalformedObjectNameException,
            InstanceAlreadyExistsException, MBeanException, InstanceNotFoundException,
            ReflectionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new ConfigurationServer());
        executorService.shutdown();
        STOP.await();
        MBeanObject<ConfigurationServiceMBean> object = new MBeanObject<>(ConfigurationServiceMBean.OBJECT_NAME);
        object.server.addNotificationListener(object.objectName, (notification, handback) -> {
            if (EVENT_CHANGE_MESSAGE.equalsIgnoreCase(notification.getType())) {
                System.out.println("Изменено сообщение на: " + notification.getMessage());
            } else {
                System.out.println(notification);
            }
        }, (NotificationFilter) notification -> true, object);
        ConfigurationService service = object.as(ConfigurationService.class);
        assert service != null;
        service.setMessage("Ожидание...");
    }
}
