[![Maven Package](https://github.com/umjammer/vavi-script-apple/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions/workflows/maven-publish.yml)
[![Java CI with Maven](https://github.com/umjammer/vavi-script-apple/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/vavi-script-apple/actions)


# vavi-script-apple

jsr223 for apple script. mac jdk stopped including the apple script engine after version 6.

rococoa version is included also.

Note: this is a fork of https://github.com/mik3hall/AppleScriptEngine

##  install

### mavem
   * repositiry
```xml
    <repository>
      <id>github</id>
      <name>GitHub umjammer Apache Maven Packages</name>
      <url>https://maven.pkg.github.com/umjammer/*</url>
    </repository>
```
   * this
```xml
    <dependency>
     <groupId>vavi</groupId>
     <artifactId>vavi-script-apple</artifactId>
      <version>1.1.1</version>
    </dependency>
```
   * dylib
```xml
      <plugin>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.10</version>
        <executions>
          <execution>
            <id>copy</id>
            <phase>generate-resources</phase>
            <goals>
              <goal>copy</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
                  <groupId>apple</groupId>
                  <artifactId>AppleScriptEngine</artifactId>
                  <type>dylib</type>
                  <overWrite>false</overWrite>
                  <outputDirectory>${project.build.testOutputDirectory}</outputDirectory>
                  <destFileName>libAppleScriptEngine.dylib</destFileName>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
```
   * jvm option
```
      -Djava.library.path=${project.build.testOutputDirectory}
```

   * rococoa
```xml
      <plugin>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.10</version>
        <executions>
          <execution>
            <id>copy</id>
            <phase>generate-resources</phase>
            <goals>
              <goal>copy</goal>
            </goals>
            <configuration>
              <artifactItems>
                <artifactItem>
                  <groupId>org.rococoa</groupId>
                  <artifactId>rococoa-core</artifactId>
                  <type>dylib</type>
                  <overWrite>false</overWrite>
                  <outputDirectory>${project.build.testOutputDirectory}</outputDirectory>
                  <destFileName>librococoa.dylib</destFileName>
                </artifactItem>
              </artifactItems>
            </configuration>
          </execution>
        </executions>
      </plugin>
```


## caution

this package includes both jni and jnr version.
the name "AppleScript" conflicts and the engine manager returns `null`.
so that specify long name for each like "AppleScriptEngine", "AppleScriptRococoa".

## TODO

 * ~~use jna instead of jni~~ * notification by application (see javapackager plugin in pom.xml)
 * notification by application (see javapackager plugin in pom.xml)
   * javapackager (use snapshot because: [issue](https://github.com/fvarrui/JavaPackager/issues/239))
   * ~~weired behavior~~ -> check runtime jdk version
     * bundle 1,8 jre (info.plist :JavaX:JVMVersion doesn't work)
     * stub direct doesn't work (selecting jdk version is still wrong)

|        | app click on finder | run stub on commandline | open command |
|--------|---------------------|-------------------------|--------------|
|rococoa | OK                  |         crash           | OK           |
|jni     | OK                  |         crash           | OK           |
