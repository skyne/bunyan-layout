package se.kth.infosys.logback;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    private static Logger LOG = LoggerFactory.getLogger(LogTest.class);

    @Test
    public void printStuff() {
        LOG.trace("Trace log");
        LOG.debug("Debug log");
        LOG.info("Info log");
        LOG.warn("Warn log");
        LOG.error("Error log");

        try {
            throw new RuntimeException("A runtime exception");
        }
        catch (Exception e) {
            LOG.error("An exception", e);
        }
    }
}
