/*
 * MIT License
 *
 * Copyright (c) 2016 Kungliga Tekniska högskolan
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A Log4j 1.2 Layout which prints events in Node Bunyan JSON format.
 * The layout takes no options and requires no additional configuration.
 */
public class BunyanLayout extends Layout {
	private static final Map<Level, Integer> BANYAN_LEVEL;
	static {
		BANYAN_LEVEL = new HashMap<Level, Integer>();
		BANYAN_LEVEL.put(Level.FATAL, 60);
		BANYAN_LEVEL.put(Level.ERROR, 50);
		BANYAN_LEVEL.put(Level.WARN, 40);
		BANYAN_LEVEL.put(Level.INFO, 30);
		BANYAN_LEVEL.put(Level.DEBUG, 20);
		BANYAN_LEVEL.put(Level.TRACE, 10);
	}

	private static final TimeZone TZ = TimeZone.getTimeZone("UTC");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static {
		DATE_FORMAT.setTimeZone(TZ);
	}
	private static final Gson GSON = new GsonBuilder().create();
	
	/**
	 * Format the event as a Banyan style JSON object.
	 */
	public String format(LoggingEvent event) {
		JsonObject jsonEvent = new JsonObject();
		jsonEvent.addProperty("v", 0);
		jsonEvent.addProperty("level", BANYAN_LEVEL.get(event.getLevel()));
		jsonEvent.addProperty("levelStr", event.getLevel().toString());
		jsonEvent.addProperty("name", event.getLoggerName());
		try {
			jsonEvent.addProperty("hostname", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			jsonEvent.addProperty("hostname", "unkown");
		}
		jsonEvent.addProperty("pid", event.getThreadName());
		jsonEvent.addProperty("time", getTime(event.getTimeStamp()));
		jsonEvent.addProperty("msg", event.getMessage().toString());
		jsonEvent.addProperty("src", event.getLocationInformation().getClassName());

		if (event.getLevel().isGreaterOrEqual(Level.ERROR) && event.getThrowableInformation() != null) {
			JsonObject jsonError = new JsonObject();
			Throwable e = event.getThrowableInformation().getThrowable();
			
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
	 * The throwable object is rendered in the output as an "err" property.
	 */
	public boolean ignoresThrowable() {
		return false;
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
	 * No options, hence doing nothing.
	 */
	public void activateOptions() {}
}
