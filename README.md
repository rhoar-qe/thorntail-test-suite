# WildFly Swarm Standalone Test Suite

- tests for fractions
  - `javaee`: Java EE technologies
  - `microprofile`: MicroProfile technologies
  - `netflix`: Netflix OSS integration
  - `wildfly`: WildFly subsystems and other fractions
- other tests
  - `protocols`: tests for exposing an application over various protocols
    (note that plain HTTP and remote EJBs are in `javaee`)
  - `fraction-autodetection`: tests for fraction autodetection
  - `custom-main`: tests for custom `main` method
  - `hollow-jar`: tests for hollow JARs

For some tests, Docker is required.

## Branching Strategy

The `master` branch is always meant for latest upstream/downstream development.
For each downstream `major.minor` version, there's a corresponding maintenance
branch:

- `7.0.x` for RHOAR WildFly Swarm 7.0.x (corresponding upstream version: `2017.10.1+`)
