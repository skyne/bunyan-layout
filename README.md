# bunyan-layout

A very simple log4j 2.x Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan

### Notable differences to the node-bunyan JSON:

1. "pid" is the thread id as a long integer rather than the JVM process id.
1. "levelStr" is added with a string representation of the log level in addition to the "level" field.

## Usage with Maven

```
<dependency>
    <groupId>se.kth.infosys.log4j2</groupId>
    <artifactId>bunyan-layout</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Configuration

Extremely simple, make sure jar is in classpath and add the layout class to the log4j2 configuration.
There are no options.

```
# log4j2.xml
...
<Console name="stdout" target="SYSTEM_OUT">
  <BunyanLayout/>
</Console>
```
