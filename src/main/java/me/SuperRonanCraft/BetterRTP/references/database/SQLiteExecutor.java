package me.SuperRonanCraft.BetterRTP.references.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SQLiteExecutor {
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "BetterRTP-SQLite");
        thread.setDaemon(true);
        return thread;
    });

    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
