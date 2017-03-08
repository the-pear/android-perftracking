## Building everything

```sh
$ cd Example
$ ../gradlew --daemon clean build
```

## Publishing a new version
Make sure `MODULE_VERSION` (in `gradle.properties`) has been updated first.

```
$ cd <REPO_ROOT>
$ ./gradlew --daemon clean build
$ ./gradlew --daemon uploadRelease
```

