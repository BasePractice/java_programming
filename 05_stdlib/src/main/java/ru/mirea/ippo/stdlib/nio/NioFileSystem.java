package ru.mirea.ippo.stdlib.nio;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

public final class NioFileSystem {
    private static final CountDownLatch START = new CountDownLatch(1);
    private static final String TEMPORARY_TXT = ".signal.txt";

    private static final class Watcher implements Runnable {
        private final Path watch;

        private Watcher(Path watch) {
            this.watch = watch;
        }

        @Override
        public void run() {
            WatchService watcher;
            System.out.println("Watching: " + watch);
            try {
                watcher = FileSystems.getDefault().newWatchService();
                watch.register(watcher,
                        ENTRY_CREATE,
                        ENTRY_DELETE,
                        ENTRY_MODIFY);
                START.countDown();
                boolean running = true;
                for (; running; ) {
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException x) {
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == OVERFLOW) {
                            continue;
                        }
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        Path origin = watch.resolve(filename);
                        if (kind == ENTRY_CREATE) {
                            System.out.println("Created : " + origin);
                        } else if (kind == ENTRY_DELETE) {
                            System.out.println("Deleted : " + origin);
                            running = !filename.endsWith(TEMPORARY_TXT);
                        } else if (kind == ENTRY_MODIFY) {
                            System.out.println("Modify  : " + origin);
                        }
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path path = Paths.get("05_stdlib/tmp");
        path = path.toAbsolutePath();

        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Watcher(path));
        START.await();
        Thread.sleep(1000);
        Path resolve = path.resolve(TEMPORARY_TXT);
        Files.deleteIfExists(resolve);
        Thread.sleep(1000);
        resolve = Files.createFile(resolve);
        Files.write(resolve, "Maine signal".getBytes("UTF-8"));
        Thread.sleep(10000);
        Files.deleteIfExists(resolve);
        service.shutdown();
    }
}
