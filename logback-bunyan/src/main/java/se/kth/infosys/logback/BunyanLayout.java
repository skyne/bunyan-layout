package se.kth.infosys.logback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
        BUNYAN_LEVEL = new HashMap<Level, Integer>();
        BUNYAN_LEVEL.put(Level.ERROR, 50);
        BUNYAN_LEVEL.put(Level.WARN, 40);
        BUNYAN_LEVEL.put(Level.INFO, 30);
        BUNYAN_LEVEL.put(Level.DEBUG, 20);
        BUNYAN_LEVEL.put(Level.TRACE, 10);
    }

    private static final TimeZone TZ = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        DATE_FORMAT.setTimeZone(TZ);
    }
    private static final Gson GSON = new GsonBuilder().create();

    private String getTime(long timeStamp) {
        return DATE_FORMAT.format(new Date(timeStamp));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("v", 0);
        jsonEvent.addProperty("level", BUNYAN_LEVEL.get(event.getLevel()));
        jsonEvent.addProperty("name", event.getLoggerName());
        try {
            jsonEvent.addProperty("hostname", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            jsonEvent.addProperty("hostname", "unkown");
        }
        jsonEvent.addProperty("pid", event.getThreadName());
        jsonEvent.addProperty("time", getTime(event.getTimeStamp()));
        jsonEvent.addProperty("msg", event.getMessage().toString());

        if (event.getLevel().isGreaterOrEqual(Level.ERROR) && event.getThrowableProxy() != null) {
            JsonObject jsonError = new JsonObject();
            IThrowableProxy e = event.getThrowableProxy();

            jsonError.addProperty("message", e.getMessage());
            jsonError.addProperty("name", e.getClassName());

            String stackTrace = "";
            StackTraceElementProxy[] stackTraceElementProxyArray = e.getStackTraceElementProxyArray();
            for (StackTraceElementProxy element : stackTraceElementProxyArray) {
                stackTrace += element.getStackTraceElement().toString() + '\n';
            }

            jsonError.addProperty("stack", stackTrace);
            jsonEvent.add("err", jsonError);
        }
        return GSON.toJson(jsonEvent) + "\n";    }
}
