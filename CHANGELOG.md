# CHANGELOG

Inspired from [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

### Breaking Changes

### Features

### Enhancements

### Bug Fixes
- Replace non-thread-safe `SimpleDateFormat` with `DateTimeFormatter` in `QueryRequest` timestamp generation. ([#180](https://github.com/opensearch-project/user-behavior-insights/pull/179))

### Infrastructure
- Rename CI workflow job IDs to valid identifiers (no spaces) so the workflow validates, while preserving display names that match branch-protection rules. ([#179](https://github.com/opensearch-project/user-behavior-insights/pull/179))

### Documentation

### Maintenance
- Bump OpenSearch to 3.7.0-SNAPSHOT. ([#179](https://github.com/opensearch-project/user-behavior-insights/pull/179))
- Update Jackson to 3.1.2. ([#174](https://github.com/opensearch-project/user-behavior-insights/pull/174))
- Upgrade Gradle wrapper from 9.2.0 to 9.4.1. ([#180](https://github.com/opensearch-project/user-behavior-insights/pull/180))

### Refactoring
- Replace Mockito-based `XContent` stubbing in `UbIParametersTests` with a real JSON serialize/parse roundtrip. ([#179](https://github.com/opensearch-project/user-behavior-insights/pull/180))
