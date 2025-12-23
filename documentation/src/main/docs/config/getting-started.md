# Getting Started

## ways to configure 

* default SmallRye Config configuration sources
  1. (`400`) [System properties](#system-properties)
  2. (`300`) [Environment variables](#environment-variables)
  3. (`295`) `.env` file | current working directory
  4. (`260`) `config/application.properties` | current working directory
  5. (`250`) `application.properties` | classpath 
  6. (`100`) [`META-INF/microprofile-config.properties` | classpath](#microprofile-config-configuration-file----meta-infmicroprofile-configproperties---)
     * == MicroProfile Config configuration file

* ordinal of priority
  * 👀higher -> MORE priority👀

### System Properties

* if you want to pass to the application -> | startup, through the `-D` flag
  * _Example:_ `java -Dmy.prop -jar my.jar`

### Environment Variables

* set | host operating system
* follow the [conversion rules](environment-variables.md)

### MicroProfile Config configuration file -- `META-INF/microprofile-config.properties` --

* located | classpath
  * Reason:🧠they are under src/main/resource🧠
* follows the `properties` files standard convention

### Additional Config Sources

* additional extensions / cover other configuration formats
  - [YAML](../config-sources/yaml.md)
  - [File System](../config-sources/filesystem.md)
  - [ZooKeeper](../config-sources/zookeeper.md)
  - [HOCON](../config-sources/hocon.md)

* [custom ConfigSource](../config-sources/custom.md)

## [`ConfigSource`](https://github.com/microprofile/microprofile-config/blob/main/api/src/main/java/org/eclipse/microprofile/config/spi/ConfigSource.java)

* == specification's interface /
  * implementations extend  

* final configuration
  * == aggregation of the properties / defined -- by -- ALL these sources

* configuration property
  * ⚠️lookup starts -- by -- the highest ordinal configuration source available TILL match is found⚠️
    * == if you set another value | higher ordinal config source -> value is override 
    * _Example:_ property /
      * configured by an Environment Variable overrides -- the -- configuration by  `microprofile-config.properties`

## how to retrieve the Configuration?

### programmatically

#### `org.eclipse.microprofile.config.ConfigProvider.getConfig()`
* allows
  * access the `org.eclipse.microprofile.config.Config` API programmatically

* `Config` instance
  * if NO configuration is created and registered -> created and registered | CURRENT context class loader
    * if SAME context class loader -> subsequent calls to `ConfigProvider.getConfig()` return the same `Config` instance 

#### `io.smallrye.config.SmallRyeConfigBuilder`
* uses
  * detached instance

### -- via -- CDI

* steps
    ```
    @Inject
    @ConfigProperty 
    ```

* TODO:
In a CDI environment, configuration can be injected in CDI aware beans with `@Inject` and 
the `org.eclipse.microprofile.config.inject.ConfigProperty` qualifier.

```java
@Inject
@ConfigProperty(name = "greeting.message") 
String message;

@Inject
@ConfigProperty(name = "greeting.suffix", defaultValue="!") 
String suffix;

@Inject
@ConfigProperty(name = "greeting.name")
Optional<String> name; 

@Inject
SmallRyeConfig config;
```

- If a value if not provided for this `greeting.message`, the application startup fails with a 
`jakarta.enterprise.inject.spi.DeploymentException: No config value of type [class java.lang.String] exists for: greeting.message`.
- The default value `!` is injected if the configuration does not provide a value for `greeting.suffix`.
- The property `greeting.name` is optional - an empty Optional is injected if the configuration does not provide a 
value for it.

## how to override?

It is possible to override `Config` default initialization `ConfigProvider.getConfig()`, by extending 
`io.smallrye.config.SmallRyeConfigFactory` and registering the implementation with the `ServiceLoader` mechanism.  

## Config vs SmallRyeConfig

The `io.smallrye.config.SmallRyeConfig` is an implementation of `org.eclipse.microprofile.config.Config` and provides 
additional APIs and helper methods not available in `org.eclipse.microprofile.config.Config`. To obtain an instance of 
`io.smallrye.config.SmallRyeConfig`, the original `org.eclipse.microprofile.config.Config` can be unwrapped:

```java
Config config = ConfigProvider.getConfig();
SmallRyeConfig smallRyeConfig = config.unwrap(SmallRyeConfig.class);
```

Or if using the builder it can be obtained directly:

```java
SmallRyeConfig config = new SmallRyeConfigBuilder().build();
```

A few notable APIs provided by `io.smallrye.config.SmallRyeConfig` allow to:

- Retrieve multiple values into a specified `Collection`
- Retrieve [Indexed Values](indexed-properties.md)
- Retrieve [Config Mappings](mappings.md) instances
- Retrieve the raw value of a configuration
- Check if a property is present
- Retrieve a `Converter`
- Convert values

## Converters

The `ConfigSource` retrieves a configuration value as a `String`
Other data types require a conversion using the  `org.eclipse.microprofile.config.spi.Converter` API.

Most of the common `Converter` types are provided by default:

* `boolean` and `java.lang.Boolean`; the values "true", "1", "YES", "Y" "ON" represent `true`. Any other value will be 
interpreted as `false`
* `byte` and `java.lang.Byte`
* `short` and `java.lang.Short`
* `int`, `java.lang.Integer`, and `java.util.OptionalInt`
* `long`, `java.lang.Long`, and `java.util.OptionalLong`
* `float` and `java.lang.Float`; a dot '.' is used to separate the fractional digits
* `double`, `java.lang.Double`, and `java.util.OptionalDouble`; a dot '.' is used to separate the fractional digits
* `char` and `java.lang.Character`
* `java.lang.Class` based on the result of `Class.forName`
* `java.net.InetAddress`
* `java.util.UUID`
* `java.util.Currency`
* `java.util.regex.Pattern`
* `java.nio.file.Path`
* Any class with declared static methods `of`, `valueOf` or `parse` that take a `String` or a `CharSequence`
* Any class with declared constructors that takes a `String` or a `CharSequence` 

All default converters have a priority of `1`.
