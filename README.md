# bunyan-layout

Tools for logging in Bunyan JSON format (see See: https://github.com/trentm/node-bunyan)
in various logging frameworks for Java.

* [log4j2-bunyan](log4j2-bunyan/) - Log4j 2.x log layout.
* [log4j-bunyan](log4j-bunyan/) - Log4j 1.4.x log layout. 
* [java-logging-bunyan](java-logging-bunyan/) - Java Util Logging log formatter.
* [logback-bunyan](logback-bunyan/) - Logback classic log layout.

## Development

The project uses git-flow branch strategy, see
[introduction](http://nvie.com/posts/a-successful-git-branching-model/)
and the [git-flow tool](https://github.com/nvie/gitflow). Mainly all
development goes into development branch and via releases into master
which is built and pushed to docker hub continously by Jenkins.

### Building

Complete build and testing is run with maven: `mvn clean install`

See the jenkins job at:
https://jenkins.sys.kth.se/view/Integral/job/bunyan-layout

Pre-built binaries are published into Maven central. Publishing is done
with `mvn -Prelease` but requires additional configuration for access
to OSS Sonatype and a GPG key for signing.

### Release process with git flow

```
git flow release start x.y.z
mvn versions:set -DnewVersion=x.y.z
 *do whatever testing and update of RELEASENOTES.md*
 *commit changes*

git flow release finish x.y.z
```

It is possible to publish the release branch `git flow release publish x.y.z` to the 
repository if it is to be shared between developers or used in some CI environment.
