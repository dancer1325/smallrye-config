# Getting Started

## ways to configure

### default configuration sources
#### System properties
##### if you want to pass to the application -> | startup, through the `-D` flag

* `mvn compile exec:java -Dapp.name="From System Properties" -Dapp.priority=400 -Dapp.source="System Properties via -D"`

#### environment variables

```bash
export APP_NAME="From Environment Variable"
export APP_PRIORITY=300
mvn compile exec:java
```

#### [`.env` file | current working directory](.env)
* run [ConfigSourcesPriorityTest.()](src/test/java/io/smallrye/config/ConfigSourcesPriorityTest.java)

#### [config/application.properties](config/application.properties)
* run [ConfigSourcesPriorityTest.()](src/test/java/io/smallrye/config/ConfigSourcesPriorityTest.java)

#### [application.properties](src/main/resources/application.properties)
* run [ConfigSourcesPriorityTest.()](src/test/java/io/smallrye/config/ConfigSourcesPriorityTest.java)

#### [META-INF/microprofile-config.properties](src/main/resources/META-INF/microprofile-config.properties)
* run [ConfigSourcesPriorityTest.()](src/test/java/io/smallrye/config/ConfigSourcesPriorityTest.java)

## `ConfigSource`
### specification's interface / implementations extend
* run `ConfigSourcesPriorityTest.shouldLoadDefaultConfigSources()`
  * get logs
### configuration property: lookup starts -- by -- the highest ordinal configuration source available TILL match is found
* run App.java
  * check get logs / properties come -- from --  

## how to retrieve the Configuration?
### programmatically
#### `org.eclipse.microprofile.config.ConfigProvider.getConfig()`
* run App.java
##### `Config` instance
###### if NO configuration is created and registered -> created and registered | CURRENT context class loader
* TODO:
#### `io.smallrye.config.SmallRyeConfigBuilder`
* run App.java
##### uses: detached instance
* TODO: 
### -- via -- CDI
* TODO:

## Running the Tests

### Run all tests
```bash
mvn clean test
```

### Run a specific test
```bash
mvn test -Dtest=ConfigSourcesPriorityTest#shouldListAllConfigSourcesWithOrdinals
```

### Run with System Property override
```bash
mvn test -Dapp.name="Override from Command Line" -Dtest=ConfigSourcesPriorityTest
```

### Run with Environment Variable
```bash
export APP_NAME="From Environment"
mvn test -Dtest=ConfigSourcesPriorityTest#shouldUseEnvironmentVariables
```

## Test Descriptions

### `ConfigSourcesPriorityTest`

This test class verifies each configuration source:

1. **shouldLoadDefaultConfigSources** - Lists all available config sources
2. **shouldPrioritizeSystemProperties** - Verifies System Properties (ordinal 400) have highest priority
3. **shouldUseEnvironmentVariables** - Verifies Environment Variables (ordinal 300) are loaded
4. **shouldReadFromDotEnvFile** - Verifies `.env` file (ordinal 295) is loaded
5. **shouldReadFromConfigDirectory** - Verifies `config/application.properties` (ordinal 260) is loaded
6. **shouldReadFromClasspathApplicationProperties** - Verifies classpath `application.properties` (ordinal 250) is loaded
7. **shouldReadFromMicroProfileConfig** - Verifies MicroProfile config (ordinal 100) is loaded
8. **shouldDemonstratePriorityOverride** - Shows how higher priority sources override lower ones
9. **shouldListAllConfigSourcesWithOrdinals** - Detailed listing of all sources and their properties

## Expected Behavior

When you query `app.name`, the value returned depends on which sources have defined it:

- If **System Property** `-Dapp.name=X` is set → returns `X`
- Else if **Environment Variable** `APP_NAME=Y` is set → returns `Y`
- Else if **`.env`** defines `APP_NAME=Z` → returns `Z`
- Else if **`config/application.properties`** defines `app.name=W` → returns `W`
- Else if **classpath `application.properties`** defines `app.name=V` → returns `V`
- Else if **`META-INF/microprofile-config.properties`** defines `app.name=U` → returns `U`

## Key Concepts

### ConfigSource
A `ConfigSource` is the abstraction for a configuration source. Each source:
- Has a **name** (identifier)
- Has an **ordinal** (priority level)
- Provides a set of **properties** (key-value pairs)

### Property Name Mapping
- Environment variables use `UPPERCASE_WITH_UNDERSCORES`
- Properties use `lowercase.with.dots`
- SmallRye Config automatically maps between them:
  - `app.name` ↔ `APP_NAME`
  - `app.database.url` ↔ `APP_DATABASE_URL`

## Verification Steps

To manually verify the configuration source priorities:

1. **Start with lowest priority** - Run tests to see MicroProfile config values
2. **Add classpath application.properties** - See it override MicroProfile config
3. **Add config/application.properties** - See it override classpath version
4. **Create .env file** - See it override config directory
5. **Set environment variable** - See it override .env
6. **Add system property** - See it override everything

```bash
# Example: Test the full priority chain
mvn test -Dtest=ConfigSourcesPriorityTest#shouldDemonstratePriorityOverride
```

## Additional Resources

- [SmallRye Config Documentation](https://smallrye.io/docs/smallrye-config)
- [MicroProfile Config Specification](https://github.com/eclipse/microprofile-config)
- [Configuration Sources Guide](https://smallrye.io/docs/smallrye-config/config-sources/config-sources.html)
