package ru.mirea.ippo.stdlib.mbean;

public interface ConfigurationServiceMBean extends ConfigurationService {

    String EVENT_CHANGE_MESSAGE = "EventChangeMessage";
    String EVENT_STOP_SERVICE = "EventStopService";
    String EVENT_REMOVE_LISTENER = "EventRemoveListener";

    String OBJECT_NAME = "ru.mirea.ippo.stdlib.mbean:type=ConfigurationService";
}
