[![Maven Package](https://github.com/umjammer/vavi-script-apple/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions/workflows/maven-publish.yml)
[![Java CI with Maven](https://github.com/umjammer/vavi-script-apple/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions)
[![CodeQL](https://github.com/umjammer/vavi-script-apple/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-script-apple

jsr223 for apple script. mac jdk stopped including the apple script engine after version 6.

rococoa version is included also.

Note: this is a fork of https://github.com/mik3hall/AppleScriptEngine

##  install

### maven

   * repository

https://github.com/umjammer?tab=packages&repo_name=vavi-script-apple

   * dylib


https://github.com/umjammer/vavi-script-apple/wiki/How-To-Install

   * rococoa

https://github.com/umjammer/rococoa/wiki/How-To-Install

* jvm option

```
      -Djava.library.path=${project.build.testOutputDirectory}              # for AppleScriptEngine
      -Djan.library.path=${project.build.testOutputDirectory}               # for AppleScriptRococoa
      -XstartOnFirstThread
```

## caution

this package includes both jni and jnr version.
the name "AppleScript" conflicts and the engine manager returns `null`.
so that specify long name for each like "AppleScriptEngine", "AppleScriptRococoa".

## TODO

 * ~~use jna instead of jni~~
 * notification by application (see javapackager plugin in pom.xml)
   * javapackager (use snapshot because: [issue](https://github.com/fvarrui/JavaPackager/issues/239))
   * ~~weired behavior~~ -> check runtime jdk version
     * bundle 1,8 jre (info.plist :JavaX:JVMVersion doesn't work)
     * stub direct doesn't work (selecting jdk version is still wrong)

|        | app click on finder | run stub on commandline | open command |
|--------|---------------------|-------------------------|--------------|
|rococoa | OK                  |         crash           | OK           |
|jni     | OK                  |         crash           | OK           |

 * sticky notification
   * https://github.com/vjeantet/alerter