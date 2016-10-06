# bunyan-layout

A very simple log4j 1.2 Layout to log events formatted in the Bunyan JSON format.
See: https://github.com/trentm/node-bunyan

### Notable differences to the node-bunyan JSON:

1. "pid" is the thread name as a string rather than an integer for the process id.
1. "levelStr" is added with a string representation of the log level in addition to the "level" field.

## Usage

Extremely simple, make sure jar is in classpath and add the layout class to the log4j configuration.
There are no options.

```
# log4j.properties

...
log4j.appender.out.layout=se.kth.infosys.log4j.BunyanLayout
```
