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

public class BanyanLayout extends Layout {
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
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
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
		jsonEvent.addProperty("logger", event.getLoggerName());
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
	 * The throwable object is rendered in the output.
	 */
	public boolean ignoresThrowable() {
		return false;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	public void activateOptions() {}
}
