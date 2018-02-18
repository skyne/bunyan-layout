# java-logging-bunyan

A very simple java.util.logging.Formatter which formats log entries in
[node-bunyan](https://github.com/trentm/node-bunyan) which is a stream
of JSON objects.

# Building

Regular build with maven. The release profile (`-Prelease`) will deploy and release to maven central
provided that additional configuration of the environment for signing is done. Prebuilt binary can 
be found in maven central and included as a maven dependency:

```
<dependencies>
  <dependency>
    <groupId>se.kth.infosys.logging</groupId>
    <artifactId>java-logging-bunyan</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies
```

## Standalone fat jar.

Building with the profile fat `(-Pfat)` will build a fat "über jar" with Google GSON library included.
This can be convenient in some circumstances, such as running it with Tomcat.

# Use

You can use it generally by setting the formatter property in your logging.properties
configuration file, or similar programmatic configuration.

```
java.util.logging.ConsoleHandler.formatter = se.kth.infosys.logging.BunyanFormatter
```

## Use with Tomcat

We use the formatter as a means to get standardized structured logging from docker containers with
software using the java.util.logging package. An example of such an application is Tomcat.

In order to get Tomcat to logg in JSON format you add a fat jar as built above to the tomcat
bin folder and a file `setenv.sh` which sets the classpath to include the jar.

```
CLASSPATH=path/to/bin/java-logging-bunyan.jar
```

You then have to tell tomcat to log using the BunyanFormatter by configuring `conf/logging.properties`.

```
# Log only to console with BunyanFormatter and otherwise leave log handling to
# the docker container host.
#
handlers = java.util.logging.ConsoleHandler
.handlers = java.util.logging.ConsoleHandler
.level = INFO

java.util.logging.ConsoleHandler.level = ALL
java.util.logging.ConsoleHandler.formatter = se.kth.infosys.logging.BunyanFormatter
```

In order to log access logs to stdout in the same manner, you'll additionally need an
org.apache.catalina.AccessLog log Valve which does not explicetely write to file as the 
standard tomcat access log does. See https://github.com/KTH/tomcat-access-logging.