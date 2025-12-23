# YAML Config Source

* allows
  * load configuration values

* ALLOWED files
  1. | current working directory
     * (`265`) `config/application.yaml|yml` 
  2. | classpath
     * (`255`) `application.yaml|yml` 
     * (`110`) MicroProfile Config configuration file `META-INF/microprofile-config.yaml|yml`

* requirements

    ```xml
    <dependency>
        <groupId>io.smallrye.config</groupId>
        <artifactId>smallrye-config-source-yaml</artifactId>
        <version>{{attributes['version']}}</version>
    </dependency>
    ```
