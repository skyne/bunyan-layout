# bunyan-layout

A very simple log4j 2.x Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan. There are no configuration options.

See [parent project](../) for other log frameworks.

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
    <version>x.y.z</version>
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
