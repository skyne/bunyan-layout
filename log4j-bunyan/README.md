# bunyan-layout

A very simple log4j 1.4.x Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan. There are no configuration options.

See [parent project](../) for other log frameworks.

### Notable differences to the node-bunyan JSON:

1. "pid" is the name of the thread as a string rather than the JVM process id.
1. "levelStr" is added with a string representation of the log level in addition to the "level" field.

## Configuration

### Maven dependency

```
<dependency>
    <groupId>se.kth.infosys.log4j</groupId>
    <artifactId>bunyan-layout</artifactId>
    <version>x.y.z</version>
</dependency>
```

### XML-style configuration.

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

### Configuration with properties

```
# log4j.properties
...
log4j.appender.out.layout=se.kth.infosys.log4j.BunyanLayout
 ```
 