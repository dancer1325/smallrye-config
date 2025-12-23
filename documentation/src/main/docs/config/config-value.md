# Config Value -- `io.smallrye.config.ConfigValue` --

* == metadata object /
  * AFTER lookup of a configuration property, 👀holds ADDITIONAL information👀 
    * configuration property name, value, profile
    * `ConfigSource` | configuration was loaded
    * ordinal of the `ConfigSource`
    * if configuration ALREADY exists -> line number | configuration was loaded
