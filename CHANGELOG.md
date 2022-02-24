# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.9.0-SNAPSHOT()] - 2022-02-24

### Changed

- Refactored all yet unrefactored forwarders to base on AbstractForwarder and to use the HttpForwarderService

## [0.1.1-SNAPSHOT]() - 2022-02-19

### Changed

- Fixed rain rates for wetter.com (mm/h instead of m/h)
- Using 10 min average for wind values (except gust) at wetter.com
- Changed forwarding to asynchronous
- Refactored wunderground forwarder

### Added

- awekas forwarder
- Prometheus metrics

## [0.1.0-SNAPSHOT]() - 2022-02-18

### Added

- Added wetter.com forwarder

### Changed

- Starting forwarder refactoring

## [0.0.4-SNAPSHOT]() - 2022-02-10

### Added

- Added dewpoint calculation

### Changed

- Renamed application to JWeatherFlux

## [0.0.3-SNAPSHOT]() - 2022-02-07

### Added

- Added windchill calculation

### Changed

- Improved handling of forwarders
- Improved logging