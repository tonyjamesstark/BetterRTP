package me.SuperRonanCraft.BetterRTP.references;

import lombok.Getter;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.file.FileOther;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class RTPLogger {

    @Getter private String format;
    @Getter private File file;
    private final Logger logger = Logger.getLogger("BetterRTP-rtp");
    private FileHandler handler;
    private BetterRTP plugin;
    private boolean enabled, toConsole;

    public void setup(BetterRTP plugin) {
        FileOther.FILETYPE config = plugin.getFiles().getType(FileOther.FILETYPE.CONFIG);
        enabled = config.getBoolean("Settings.Logger.Enabled");
        this.plugin = plugin;
        unload();
        if (!enabled) return;
        this.format = config.getString("Settings.Logger.Format");
        toConsole = config.getBoolean("Settings.Logger.LogToConsole");
        try {
            this.file = new File(plugin.getDataFolder() + File.separator + "log.txt");
            this.handler = new FileHandler(file.getPath(), true);
            handler.setFormatter(new MyFormatter());
            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
            plugin.getLogger().addHandler(handler);
        } catch (IOException e) {
            enabled = false;
            e.printStackTrace();
        }
    }

    public void log(String str) {
        if (enabled && !toConsole)
            logger.info(str);
        else
            BetterRTP.getInstance().getLogger().info(str);
    }

    private static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat(BetterRTP.getInstance().getRtpLogger().getFormat());
        return format.format(new Date());
    }

    public void unload() {
        if (handler == null) return;
        logger.removeHandler(handler);
        plugin.getLogger().removeHandler(handler);
        handler.close();
        handler = null;
    }

    static class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return getDate() + " [" + record.getLevel().getName() + "]: " + record.getMessage() + '\n';
        }
    }
}
