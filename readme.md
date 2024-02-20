[![Release](https://jitpack.io/v/umjammer/vavi-script-apple.svg)](https://jitpack.io/#umjammer/vavi-script-apple)
[![Java CI with Maven](https://github.com/umjammer/vavi-script-apple/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions)
[![CodeQL](https://github.com/umjammer/vavi-script-apple/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-script-apple

jsr223 for apple script. mac jdk stopped including the apple script engine after version 6.

rococoa version is included also.

Note: this is a fork of https://github.com/mik3hall/AppleScriptEngine

##  install

### maven

   * repository

https://jitpack.io/#umjammer/vavi-script-apple

   * dylib

https://github.com/umjammer/vavi-script-apple/wiki/How-To-Install

* jvm option

```
      -Djava.library.path=${project.build.testOutputDirectory}              # for AppleScriptEngine
      -Djna.library.path=${project.build.testOutputDirectory}               # for AppleScriptRococoa
      -XstartOnFirstThread
```

## caution

~~this package includes both jni and jnr version.
the name "AppleScript" conflicts and the engine manager returns `null`.
so that specify long name for each like "AppleScriptEngine", "AppleScriptRococoa".~~

jni version is deprecated

## TODO

 * ~~use jna instead of jni~~
 * ~~notification by application~~ (see javapackager plugin in pom.xml)
   * javapackager (use snapshot because: [issue](https://github.com/fvarrui/JavaPackager/issues/239))
   * ~~weired behavior~~ -> check runtime jdk version
     * bundle 1.8 jre (because `info.plist:JavaX:JVMVersion` doesn't work)
     * stub's jdk direction doesn't work well <sup>*1</sup>

|        | app click on finder | run stub on commandline | open command |
|--------|---------------------|-------------------------|--------------|
|rococoa | OK                  | ~~crash~~<sup>*1</sup>  | OK           |
|jni     | OK                  | ~~crash~~<sup>*1</sup>  | OK           |

<sub>[1] application path needs to be specified by full path</sub>

 * sticky notification
   * https://github.com/vjeantet/alerter