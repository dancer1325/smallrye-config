* goal
  * uses | Java application

## how has it been created?
* 
    ```
    mvn archetype:generate -DgroupId=com.example -DartifactId=javaApplication -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
    ```
* | [pom.xml](pom.xml), add

    ```
    <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>3.8.1</version>
          <scope>test</scope>
    </dependency>
    ```
### ways / create | ANOTHER dependency tools
#### Gradle (Groovy)
```groovy
implementation 'io.smallrye.config:smallrye-config:{{attributes['version']}}'
```
#### Gradle (Kotlin)
```kotlin
implementation("io.smallrye.config:smallrye-config:{{attributes['version']}}")
```
#### JBang
```java
//DEPS io.smallrye.config:smallrye-config:{{attributes['version']}}
```
