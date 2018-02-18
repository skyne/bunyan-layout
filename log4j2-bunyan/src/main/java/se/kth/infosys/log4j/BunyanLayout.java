/*
 * MIT License
 *
 * Copyright (c) 2018 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.infosys.log4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A Log4j 1.2 Layout which prints events in Node Bunyan JSON format.
 * The layout takes no options and requires no additional configuration.
 */
@Plugin(name = "BunyanLayout", category = "Core", elementType = "layout", printObject = true)
public class BunyanLayout extends AbstractStringLayout {
    protected BunyanLayout(Charset charset) {
        super(charset);
    }

    @PluginFactory
    public static BunyanLayout createLayout(@PluginAttribute("locationInfo") boolean locationInfo,
            @PluginAttribute("properties") boolean properties,
            @PluginAttribute("complete") boolean complete,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset) {
        return new BunyanLayout(charset);
    }

    private static final Map<Level, Integer> BUNYAN_LEVEL;
    static {
        BUNYAN_LEVEL = new HashMap<Level, Integer>();
        BUNYAN_LEVEL.put(Level.FATAL, 60);
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

    /**
     * Format the event as a Bunyan style JSON object.
     */
    private String format(LogEvent event) {
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
        jsonEvent.addProperty("pid", event.getThreadId());
        jsonEvent.addProperty("time", getTime(event.getTimeMillis()));
        jsonEvent.addProperty("msg", event.getMessage().getFormattedMessage());
        jsonEvent.addProperty("src", event.getSource().getClassName());

        if (event.getLevel().isMoreSpecificThan(Level.WARN) && event.getThrown() != null) {
            JsonObject jsonError = new JsonObject();
            Throwable e = event.getThrown();

            jsonError.addProperty("message", e.getMessage());
            jsonError.addProperty("name", e.getClass().getSimpleName());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            jsonError.addProperty("stack", sw.toString());
            jsonEvent.add("err", jsonError);
        }
        return GSON.toJson(jsonEvent) + "\n";
    }

    private String getTime(long timeStamp) {
        return DATE_FORMAT.format(new Date(timeStamp));
    }

    /**
     * This Layout renders JSON objects, hence we use application/json.
     * This is in a strict sense untrue, since the entire stream is not proper JSON.
     */
    @Override
    public String getContentType() {
        return "application/json";
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public byte[] toByteArray(LogEvent event) {
        return format(event).getBytes();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String toSerializable(LogEvent event) {
        return format(event);	
    }
}
