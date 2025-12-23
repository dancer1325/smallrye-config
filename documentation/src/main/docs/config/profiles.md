# Profiles


* Profiles
  * 👀enable👀
    * multiple configurations | same file OR separate files
    * select them -- via a -- profile name
  * use cases
    * applications / DIFFERENT configurations -- based on the -- target environment
      * _Example:_ local development environment != production environment
  * ways to be configured
    * primary sources (system properties, environment variables, `application.properties`)
    * source / does NOT require configuration

## Profile aware properties

* `%{profile-name}.config.name`
  * allows
    * set properties / same name

        ```properties title="META-INF/microprofile-config.properties"
        http.port=8080
        
        # profile dev
        %dev.http.port=8181
        # if you want to activate it -> set `smallrye.config.profile=dev` | any valid `ConfigSource` 
        ```

## Profile aware files

* `microprofile-config-{profile}.properties` named file
  * _Example:_ == PREVIOUS example

    ```properties title="META-INF/microprofile-config.properties"
    http.port=8080
    ```

    ```properties title="META-INF/microprofile-config-dev.properties"
    # NOT require prefix 
    http.port=8181
    ```

* ⚠️Profile aware files' priority > Profile aware properties' priority⚠️
* ❌`smallrye.config.profile` does NOT work | profile aware files❌
  * Reason:🧠the profile is required in ADVANCE -- to -- load the profile aware files🧠

## Priority

Profile lookups are only valid if the `ConfigSource` has a higher ordinal than a lookup to the regular configuration 
name
* Consider:

```properties title="main.properties"
config_ordinal=1000
http.port=8080
```

```properties title="profile.properties"
config_ordinal=100
%dev.http.port=8181
```

Even with the profile `dev` active, the lookup value for `my.prop` is `1234`
* This prevents lower ordinal sources to 
set a profile property value that cannot be overridden unless the profile property is also overridden.

## Multiple Profiles

Multiple Profiles may be active at the same time
* The configuration `smallrye.config.profile` accepts a comma-separated 
list of profile names: `smallrye.config.profile=common,dev`
* Both `common` and `dev` are separate profiles.

When multiple profiles are active, the rules for profile configuration are the same
* If two profiles define the same 
configuration, then the last listed profile has priority
* Consider:

```properties
smallrye.config.profile=common,dev

my.prop=1234
%common.my.prop=1234
%dev.my.prop=5678

%common.commom.prop=common
%dev.dev.prop=dev
%test.test.prop=test
```

Then

- `common.prop` value is `common`
- `dev.prop` value is `dev`
- `my.prop` value is `5678`
- `test.prop` does not have a value

It is also possible to define multiple profile properties, with a comma-separated list of profile names:


```properties
%prod,dev.my.prop=1234
```

The property name `common.prop` is active in both `dev` and `prod` profile
* If the same property name exists in 
multiple profile properties then, the property name with the most specific profile wins:

```properties
smallrye.config.profile=dev

%prod,dev.my.prop=1234

%dev.my.prop=5678
```

Then `my.prop` value is `5678`.

## Parent Profile

A Parent Profile adds multiple levels of hierarchy to the current profile
* The configuration 
`smallrye.config.profile.parent` also acccepts a comma-separated list of profile names.

When the Parent Profile is active, if a property cannot be found in the current active Profile, the config lookup 
fallbacks to the Parent Profile
* Consider:

```properties
smallrye.config.profile=dev
smallrye.config.profile.parent=common

my.prop=1234
%common.my.prop=0
%dev.my.prop=5678

%common.commom.prop=common
%dev.dev.prop=dev
%test.test.prop=test
```

Then

- `common.prop` value is `common`
- `dev.prop` value is `dev`
- `my.prop` value is `0`
- `test.prop` does not have a value

!!! attention

    Do not use Profile aware files to set smallrye.config.profile.parent`. This will not work because the 
    the profile is required in advance to load the profile aware files.

### Multi-level Hierarchy

The Parent Profile also supports multiple levels of hierarchies:

```properties
smallrye.config.profile=child
%child.smallrye.config.profile.parent=parent
%parent.smallrye.config.profile.parent=grandparent
%grandparent.smallrye.config.profile.parent=greatgrandparent
%greatgrandparent.smallrye.config.profile.parent=end
```

Will load the following profiles in order: `child`, `parent`, `grandparent`, `greatgrandparent`, `end` 

