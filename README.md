# Thorntail Standalone Test Suite

- tests for fractions
  - `javaee`: Java EE technologies
  - `microprofile`: MicroProfile technologies
  - `netflix`: Netflix OSS integration
  - `opentracing`: OpenTracing integration
  - `wildfly`: WildFly subsystems and other fractions
- other tests
  - `protocols`: tests for exposing an application over various protocols
    (note that plain HTTP and remote EJBs are in `javaee`)
  - `fraction-autodetection`: tests for fraction autodetection
  - `custom-main`: tests for custom `main` method
  - `hollow-jar`: tests for hollow JARs
  - `java11`: tests for Java 11+

For some tests, Docker is required.

## Branching Strategy

The `master` branch is always meant for latest upstream/downstream development.
For each downstream `major.minor` version, there's a corresponding maintenance
branch:

- `7.0.x` for RHOAR WildFly Swarm 7.0.x (corresponding upstream version: `2017.10.1+`)
- `7.1.x` for RHOAR WildFly Swarm 7.1.x (corresponding upstream version: `2018.3.3+`)
- `2.2.x` for RHOAR Thorntail 2.2.x (corresponding upstream version: `2.2.0.Final+`)
- `2.4.x` for RHOAR Thorntail 2.4.x (corresponding upstream version: `2.4.0.Final+`)
- `2.5.x` for RHOAR Thorntail 2.5.x (corresponding upstream version: `2.5.0.Final+`)
