### Shared Library

This project holds the common files for Platform 3.0. Currently this is only entities, but we could be adding repositories or other common classes and resources.

When adding new files ensure to follow these steps:

1. Update the version in `pom.xml` for example `<version>3.0.3</version> `

2. Then run `mvn install`. This will build and copy the jar to ~/.m2/repository/com/optculture/oc-shared/{version}/oc-shared-{version}.jar

3. In the dependent project update the dependency in `pom.xml`.
``` xml
<dependency>
    <groupId>com.optculture</groupId>
    <artifactId>oc-shared</artifactId>
    <version>new.version.number</version>
</dependency>
```

