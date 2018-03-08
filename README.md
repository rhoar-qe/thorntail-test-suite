# Swarm Standalone Test Suite

- `javaee`: tests for fractions covering Java EE technologies
- `netflix`: tests for fractions covering Netflix OSS integration
- `wildfly`: tests for fractions covering WildFly subsystems or Swarm own fractions
- `microprofile`: tests for the `microprofile` fraction
- `protocols`: tests for exposing an application over various protocols
- `fraction-autodetection`: tests for fraction autodetection
- `custom-main`: tests for custom `main` method
- `hollow-jar`: tests for hollow JARs

For some tests, Docker is required.

## Branching Strategy

The `master` branch is always meant for latest upstream/downstream development.
For each downstream `major.minor` version, there's a corresponding maintenance
branch:

- `7.0.x` for RHOAR WildFly Swarm 7.0.x (corresponding upstream version: `2017.10.1`)
