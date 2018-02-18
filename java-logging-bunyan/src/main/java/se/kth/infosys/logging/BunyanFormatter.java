/*
 * MIT License
 *
 * Copyright (c) 2017 Kungliga Tekniska högskolan
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
package se.kth.infosys.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A java.util.logging.Formatter which formats logs in node-bunyan JSON format.
 *
 * Use it e.g. by setting the property:
 * java.util.logging.ConsoleHandler.formatter = se.kth.infosys.logging.BunyanFormatter
 *
 * See https://github.com/trentm/node-bunyan for details about log format.
 */
public class BunyanFormatter extends Formatter {
    private static final Gson GSON = new GsonBuilder().create();

    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final Map<Level, Integer> BUNYAN_LEVEL;
    static {
        BUNYAN_LEVEL = new HashMap<Level, Integer>();
        BUNYAN_LEVEL.put(Level.SEVERE, 50);
        BUNYAN_LEVEL.put(Level.WARNING, 40);
        BUNYAN_LEVEL.put(Level.CONFIG, 30);
        BUNYAN_LEVEL.put(Level.INFO, 30);
        BUNYAN_LEVEL.put(Level.FINE, 20);
        BUNYAN_LEVEL.put(Level.FINER, 10);
        BUNYAN_LEVEL.put(Level.FINEST, 10);
    }

    private static String HOSTNAME;
    static {
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            HOSTNAME = "unknown";
        }
    }

    private static String formatAsIsoUTCDateTime(long timeStamp) {
        final Date date = new Date(timeStamp);
        return ZonedDateTime.ofInstant(date.toInstant(), SYSTEM_ZONE)
                .format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(LogRecord record) {
        final JsonObject jsonEvent = new JsonObject();

        jsonEvent.addProperty("v", 0);
        jsonEvent.addProperty("level", BUNYAN_LEVEL.get(record.getLevel()));
        jsonEvent.addProperty("name", record.getLoggerName());
        jsonEvent.addProperty("hostname", HOSTNAME);
        jsonEvent.addProperty("pid", record.getThreadID());
        jsonEvent.addProperty("time", formatAsIsoUTCDateTime(record.getMillis()));
        jsonEvent.addProperty("msg", record.getMessage());
        jsonEvent.addProperty("src", record.getSourceClassName());

        final Throwable thrown = record.getThrown();
        if (thrown != null && record.getLevel().intValue() >= Level.WARNING.intValue()) {
            JsonObject jsonError = new JsonObject();

            jsonError.addProperty("message", thrown.getMessage());
            jsonError.addProperty("name", thrown.getClass().getSimpleName());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            jsonError.addProperty("stack", sw.toString());
            jsonEvent.add("err", jsonError);
        }

        return GSON.toJson(jsonEvent) + "\n";
    }
}
