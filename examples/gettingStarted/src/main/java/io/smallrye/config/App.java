package io.smallrye.config;

import java.util.Comparator;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Demo application to show SmallRye Config configuration sources.
 */
public class App {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("SmallRye Config - Configuration Sources");
        System.out.println("========================================\n");

        // Build config with default sources
        SmallRyeConfig config = new SmallRyeConfigBuilder()
                .addDefaultSources()
                .addDefaultInterceptors()
                .build();

        // List all configuration sources
        System.out.println("Configuration Sources (by priority):");
        System.out.println("------------------------------------");
        StreamSupport.stream(config.getConfigSources().spliterator(), false)
                .sorted(Comparator.comparingInt(ConfigSource::getOrdinal).reversed())
                .forEach(source -> System.out.printf("[%3d] %s%n", source.getOrdinal(), source.getName()));

        // Show config values
        System.out.println("\n\nConfiguration Values:");
        System.out.println("--------------------");

        String appName = config.getOptionalValue("app.name", String.class)
                .orElse("NOT FOUND");
        String appPriority = config.getOptionalValue("app.priority", String.class)
                .orElse("NOT FOUND");
        String appSource = config.getOptionalValue("app.source", String.class)
                .orElse("NOT FOUND");

        System.out.println("app.name     = " + appName);
        System.out.println("app.priority = " + appPriority);
        System.out.println("app.source   = " + appSource);

        // Show which source provided the value
        System.out.println("\n\nProperty Resolution:");
        System.out.println("-------------------");
        showPropertySource(config, "app.name");
        showPropertySource(config, "app.priority");
        showPropertySource(config, "app.source");

        System.out.println("\n========================================");

        // 2.   `org.eclipse.microprofile.config.ConfigProvider.getConfig()`
        Config configProviderConfig = ConfigProvider.getConfig();
        String appNameFromProviderConfig = configProviderConfig.getValue("app.name", String.class);
        System.out.println("appNameFromProviderConfig " + appNameFromProviderConfig);


    }

    private static void showPropertySource(SmallRyeConfig config, String propertyName) {
        ConfigValue configValue = config.getConfigValue(propertyName);
        if (configValue != null && configValue.getValue() != null) {
            System.out.printf("'%s' comes from: %s (ordinal: %d)%n",
                    propertyName,
                    configValue.getConfigSourceName(),
                    configValue.getConfigSourceOrdinal());
        } else {
            System.out.printf("'%s' not found in any config source%n", propertyName);
        }
    }
}
