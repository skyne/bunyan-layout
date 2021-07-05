package se.kth.infosys.logback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.LayoutBase;

public class BunyanLayout extends LayoutBase<ILoggingEvent> {
    private static final Map<Level, Integer> BUNYAN_LEVEL;
    static {
        BUNYAN_LEVEL = new HashMap<>();
        BUNYAN_LEVEL.put(Level.ERROR, 50);
        BUNYAN_LEVEL.put(Level.WARN, 40);
        BUNYAN_LEVEL.put(Level.INFO, 30);
        BUNYAN_LEVEL.put(Level.DEBUG, 20);
        BUNYAN_LEVEL.put(Level.TRACE, 10);
    }

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public String doLayout(ILoggingEvent event) {
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("v", 0);
        jsonEvent.addProperty("level", BUNYAN_LEVEL.get(event.getLevel()));
        jsonEvent.addProperty("levelStr", event.getLevel().toString());
        jsonEvent.addProperty("name", event.getLoggerName());
        try {
            jsonEvent.addProperty("hostname", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            jsonEvent.addProperty("hostname", "unkown");
        }
        jsonEvent.addProperty("pid", getThreadId(event));
        jsonEvent.addProperty("time", formatAsIsoUTCDateTime(event.getTimeStamp()));
        jsonEvent.addProperty("msg", event.getFormattedMessage());

        if (event.getLevel().isGreaterOrEqual(Level.ERROR) && event.getThrowableProxy() != null) {
            JsonObject jsonError = new JsonObject();
            IThrowableProxy e = event.getThrowableProxy();

            jsonError.addProperty("message", e.getMessage());
            jsonError.addProperty("name", e.getClassName());

            StringBuilder stackTrace = new StringBuilder();
            StackTraceElementProxy[] stackTraceElementProxyArray = e.getStackTraceElementProxyArray();
            for (StackTraceElementProxy element : stackTraceElementProxyArray) {
                stackTrace.append(element.getStackTraceElement().toString()).append('\n');
            }

            jsonError.addProperty("stack", stackTrace.toString());
            jsonEvent.add("err", jsonError);
        }
        return GSON.toJson(jsonEvent) + "\n";    }

    private long getThreadId(ILoggingEvent event) {
        long threadId;
        String threadName = event.getThreadName();
        if(Thread.currentThread().getName().equals(threadName)){
            threadId = Thread.currentThread().getId();
        } else {
            try {
                threadId = Long.parseLong(threadName.substring(threadName.lastIndexOf('-')));
            } catch (NumberFormatException e) {
                threadId = 0;
            }
        }
        return threadId;
    }

    private static String formatAsIsoUTCDateTime(long timeStamp) {
        final Instant instant = Instant.ofEpochMilli(timeStamp);
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_INSTANT);
    }


}
