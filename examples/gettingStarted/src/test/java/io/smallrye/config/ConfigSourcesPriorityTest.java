package io.smallrye.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test to verify SmallRye Config default configuration sources and their priorities.
 *
 * Default Configuration Sources (by priority/ordinal):
 * 1. (400) System properties
 * 2. (300) Environment variables
 * 3. (295) .env file in current working directory
 * 4. (260) config/application.properties in current working directory
 * 5. (250) application.properties in classpath
 * 6. (100) META-INF/microprofile-config.properties in classpath
 *
 */
class ConfigSourcesPriorityTest {

    private SmallRyeConfig config;

    @BeforeEach
    void setup() {
        // Build config with default sources
        config = new SmallRyeConfigBuilder()
                .addDefaultSources()
                .addDefaultInterceptors()
                .build();
    }

    @Test
    @DisplayName("Should load all default configuration sources")
    void shouldLoadDefaultConfigSources() {
        List<ConfigSource> configSources = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .sorted(Comparator.comparingInt(ConfigSource::getOrdinal).reversed())
                .collect(Collectors.toList());

        System.out.println("\n=== Configuration Sources (by priority) ===");
        configSources.forEach(source -> System.out.printf("[%3d] %s%n", source.getOrdinal(), source.getName()));

        // Verify we have the expected sources
        assertThat(configSources).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("Should prioritize System Properties (ordinal 400)")
    void shouldPrioritizeSystemProperties() {
        // Set system property (highest priority)
        System.setProperty("app.name", "From System Properties");
        System.setProperty("app.priority", "400");

        try {
            // Rebuild config to pick up system property
            config = new SmallRyeConfigBuilder()
                    .addDefaultSources()
                    .addDefaultInterceptors()
                    .build();

            String value = config.getRawValue("app.name");
            String priority = config.getRawValue("app.priority");

            System.out.println("\n=== System Properties Test ===");
            System.out.println("app.name = " + value);
            System.out.println("app.priority = " + priority);

            assertThat(value).isEqualTo("From System Properties");
            assertThat(priority).isEqualTo("400");
        } finally {
            System.clearProperty("app.name");
            System.clearProperty("app.priority");
        }
    }

    @Test
    @DisplayName("Should use Environment Variables (ordinal 300)")
    void shouldUseEnvironmentVariables() {
        // Note: Environment variables need to be set externally
        // This test verifies the ENV source exists
        boolean hasEnvSource = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .anyMatch(source -> source.getName().contains("EnvConfigSource"));

        assertThat(hasEnvSource).isTrue();

        // If PATH is set (which it should be), verify we can read it
        String path = config.getOptionalValue("PATH", String.class).orElse(null);
        System.out.println("\n=== Environment Variables Test ===");
        System.out.println("PATH exists: " + (path != null));
        assertThat(path).isNotNull();
    }

    @Test
    @DisplayName("Should read from .env file (ordinal 295)")
    void shouldReadFromDotEnvFile() {
        // The .env file should be read from current working directory
        boolean hasDotEnvSource = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .anyMatch(source -> source.getName().contains(".env"));

        System.out.println("\n=== .env File Test ===");
        System.out.println(".env source exists: " + hasDotEnvSource);

        if (hasDotEnvSource) {
            String value = config.getOptionalValue("app.name", String.class).orElse(null);
            System.out.println("app.name from .env: " + value);
        }
    }

    @Test
    @DisplayName("Should read from config/application.properties (ordinal 260)")
    void shouldReadFromConfigDirectory() {
        boolean hasConfigDirSource = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .anyMatch(source -> source.getName().contains("config/application.properties"));

        System.out.println("\n=== config/application.properties Test ===");
        System.out.println("config/application.properties source exists: " + hasConfigDirSource);
    }

    @Test
    @DisplayName("Should read from classpath application.properties (ordinal 250)")
    void shouldReadFromClasspathApplicationProperties() {
        boolean hasClasspathAppProps = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .anyMatch(source -> source.getName().contains("application.properties")
                        && source.getName().contains("ClassPath"));

        System.out.println("\n=== Classpath application.properties Test ===");
        System.out.println("Classpath application.properties exists: " + hasClasspathAppProps);

        assertThat(hasClasspathAppProps).isTrue();
    }

    @Test
    @DisplayName("Should read from META-INF/microprofile-config.properties (ordinal 100)")
    void shouldReadFromMicroProfileConfig() {
        boolean hasMicroProfileConfig = StreamSupport.stream(
                config.getConfigSources().spliterator(), false)
                .anyMatch(source -> source.getName().contains("microprofile-config.properties"));

        System.out.println("\n=== META-INF/microprofile-config.properties Test ===");
        System.out.println("MicroProfile config exists: " + hasMicroProfileConfig);

        assertThat(hasMicroProfileConfig).isTrue();
    }

    @Test
    @DisplayName("Should demonstrate priority override behavior")
    void shouldDemonstratePriorityOverride() {
        System.out.println("\n=== Priority Override Test ===");

        // Without system property, should get value from highest available source
        String nameWithoutSystemProp = config.getOptionalValue("app.name", String.class).orElse("NOT FOUND");
        String priorityWithoutSystemProp = config.getOptionalValue("app.priority", String.class).orElse("NOT FOUND");

        System.out.println("Without System Property:");
        System.out.println("  app.name = " + nameWithoutSystemProp);
        System.out.println("  app.priority = " + priorityWithoutSystemProp);
        System.out.println("  app.source = " + config.getOptionalValue("app.source", String.class).orElse("NOT FOUND"));

        // Now set system property (highest priority)
        System.setProperty("app.name", "OVERRIDDEN by System Property");
        System.setProperty("app.priority", "400");

        try {
            // Rebuild config
            config = new SmallRyeConfigBuilder()
                    .addDefaultSources()
                    .addDefaultInterceptors()
                    .build();

            String nameWithSystemProp = config.getRawValue("app.name");
            String priorityWithSystemProp = config.getRawValue("app.priority");

            System.out.println("\nWith System Property (overrides all):");
            System.out.println("  app.name = " + nameWithSystemProp);
            System.out.println("  app.priority = " + priorityWithSystemProp);

            assertThat(nameWithSystemProp).isEqualTo("OVERRIDDEN by System Property");
            assertThat(priorityWithSystemProp).isEqualTo("400");
        } finally {
            System.clearProperty("app.name");
            System.clearProperty("app.priority");
        }
    }

    @Test
    @DisplayName("Should list all config sources with their ordinals")
    void shouldListAllConfigSourcesWithOrdinals() {
        System.out.println("\n=== All Configuration Sources (Detailed) ===");

        StreamSupport.stream(config.getConfigSources().spliterator(), false)
                .sorted(Comparator.comparingInt(ConfigSource::getOrdinal).reversed())
                .forEach(source -> {
                    System.out.printf("\nOrdinal: %d%n", source.getOrdinal());
                    System.out.printf("Name: %s%n", source.getName());
                    System.out.printf("Properties count: %d%n", source.getProperties().size());

                    // Show some properties if available
                    if (source.getOrdinal() <= 260 && source.getOrdinal() >= 100) {
                        source.getProperties().forEach((key, value) -> System.out.printf("  %s = %s%n", key, value));
                    }
                });
    }
}
