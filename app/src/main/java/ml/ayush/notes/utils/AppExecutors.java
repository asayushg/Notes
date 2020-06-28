package ml.ayush.notes.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    private static AppExecutors instance;

    public AppExecutors() {

    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}

