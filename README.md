# Exposure    [![Release](https://jitpack.io/v/EricssonBroadcastServices/AndroidClientExposure.svg)](https://jitpack.io/#EricssonBroadcastServices/AndroidClientExposure)

* [Features](#features)
* [Tutorials](#tutorials)
* [License](https://github.com/EricssonBroadcastServices/AndroidClientExposure/blob/master/LICENSE)
* [Requirements](#requirements)
* [Dependencies](#dependencies)
* [Installation](#installation)
* [Documentation](https://jitpack.io/com/github/EricssonBroadcastServices/AndroidClientExposure/master-SNAPSHOT/javadoc/)
* [Release Notes](#release-notes)
* [Upgrade Guides](#upgrade-guides)

## Features

- [x] Authentication
- [x] Entitlements
- [x] Metadata

## Tutorials

- [Login](tutorials/tutorial-login.md)
- [Fetching Metadata](tutorials/tutorial-metadata.md)

## Requirements

* `Android` 4.4+

## Dependencies

- [AndroidClientUtilities](https://github.com/EricssonBroadcastServices/AndroidClientUtilities)

## Installation

### JitPack
Releases are available on [JitPack](https://jitpack.io/#EricssonBroadcastServices/AndroidClientExposure) and can be automatically imported to your project using Gradle.

Add the jitpack.io repository to your project **build.gradle**:
```gradle
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```

Then add the dependency to your module **build.gradle**:
```gradle
dependencies {
    compile 'com.github.EricssonBroadcastServices:AndroidClientExposure:{version}'
}
```

Note: do not add the jitpack.io repository under *buildscript {}*

## Release Notes
Release specific changes can be found in the [CHANGELOG](CHANGELOG.md).

## Upgrade Guides
Major changes between releases will be documented with special [Upgrade Guides](UPGRADE_GUIDE.md).
