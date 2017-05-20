# Releasing

 1. Change the version in `gradle.properties` to a non-SNAPSHOT verson.
 2. Update the `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. `git commit -am "Prepare for release X.Y.Z"` (where X.Y.Z is the new version)
 5. `./gradlew clean uploadArchives`.
 6. Visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact.
 7. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 8. `git push && git push --tags` 
 9. Update the `gradle.properties` to the next SNAPSHOT version.
 10. `git commit -am "Prepare next development version"`

If step 5 or 6 fails, drop the Sonatype repo, fix the problem, commit, and start again at step 5.

## Prerequisites

In `~/.gradle/gradle.properties`, set the following:

 * `NEXUS_USERNAME` - Sonatype username for releasing to `inc.greyfox`.
 * `NEXUS_PASSWORD` - Sonatype password for releasing to `inc.greyfox`.