# bunyan-layout

A very simple log4j 2.x Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan. There are no configuration options.

### Notable differences to the node-bunyan JSON:

1. "pid" is the thread id as a long integer rather than the JVM process id.
1. "levelStr" is added with a string representation of the log level in addition to the "level" field.

## Configuration

### Log4j2

Maven dependency:

```
<dependency>
    <groupId>se.kth.infosys.log4j2</groupId>
    <artifactId>bunyan-layout</artifactId>
    <version>2.0.1</version>
</dependency>
```

You have to specify the package which includes the BunyanLayout in the log4j2.xml before
using the layout:

```
# log4j2.xml

<Configuration packages="se.kth.infosys.log4j">
  <Appenders>
    <Console name="stdout" target="SYSTEM_OUT">
      <BunyanLayout />
    </Console>
  </Appenders>

  <Loggers>
    <Root level="INFO">
      <AppenderRef ref="stdout"/>
    </Root>
  </Loggers>
</Configuration>
```

### Log4j

Maven dependency:

```
<dependency>
    <groupId>se.kth.infosys.log4j</groupId>
    <artifactId>bunyan-layout</artifactId>
    <version>1.0.0</version>
</dependency>
```

XML-style configuration.

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration debug="false" xmlns:log4j="http://jakarta.apache.org/log4j/">
    <appender name="console" class="org.apache.log4j.ConsoleAppender">
        <layout class="se.kth.infosys.log4j.BunyanLayout"/>
    </appender>

    <root>
        <level value="ERROR" />
        <appender-ref ref="console" />
    </root>
</log4j:configuration>
```