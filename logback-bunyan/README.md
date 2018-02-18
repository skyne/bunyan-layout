# bunyan-layout

A very simple Logback classic Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan. There are no configuration options.

See (parent project)[../] for other log frameworks.

### Notable differences to the node-bunyan JSON:

1. "pid" is the name of the thread as a string rather than the JVM process id.
1. "levelStr" is added with a string representation of the log level in addition to the "level" field.

## Configuration

### Maven dependency

```
<dependency>
    <groupId>se.kth.infosys.logback</groupId>
    <artifactId>bunyan-layout</artifactId>
    <version>x.y.z</version>
</dependency>
```

### General logback.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="se.kth.infosys.logback.BunyanLayout" />
    </encoder>
  </appender>

  <root level="ALL">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

### Spring boot configuration, logback-spring.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="org/springframework/boot/logging/logback/console-appender.xml" />

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="se.kth.infosys.logback.BunyanLayout" />
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```
