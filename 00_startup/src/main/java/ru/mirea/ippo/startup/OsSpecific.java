package ru.mirea.ippo.startup;

public final class OsSpecific {
    private static String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Задержка если Unix-like
     * FIXME: В Windows кэширование не важное, поэтому задержка не нужна
     * @param ms time to wait FS
     * @throws InterruptedException
     */
    public void fsWaitIfNeeded(long ms) throws InterruptedException {
        if (!isWindows()) {
            Thread.sleep(ms);
        }
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    private static boolean isSolaris() {
        return (OS.contains("sunos"));
    }
}
