[![Maven Package](https://github.com/umjammer/AppleScriptEngine/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/AppleScriptEngine/actions/workflows/maven-publish.yml)
[![Java CI with Maven](https://github.com/umjammer/AppleScriptEngine/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/umjammer/AppleScriptEngine/actions)


# apple-script-engine

jsr223 for apple script. mac jdk stopped including the apple script engine after version 6.

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
     <groupId>apple</groupId>
     <artifactId>apple-script-engine</artifactId>
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

## TODO

 * use jna instead of jni