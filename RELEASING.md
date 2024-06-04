- [Overview](#overview)
- [Branching](#branching)
  - [OpenSearch Branching](#opensearch-branching)
  - [Plugin Branching](#plugin-branching)
  - [Feature Branches](#feature-branches)
- [Versioning](#versioning)
  - [Version Numbers](#version-numbers)
  - [Incrementing Versions](#incrementing-versions)
- [Tagging](#tagging)
- [Release Labels](#release-labels)
- [Releasing](#releasing)
  - [Release Process for OpenSearch Bundle Minor Releases and Patch Releases](#Release-Process-for-OpenSearch-Bundle-Minor-Releases-and-Patch-Releases)
    - [Entrance Criteria to Start Release Window](#Entrance-Criteria-to-Start-Release-Window)
    - [Exit Criteria to Close Release Window](#Exit-Criteria-to-Close-Release-Window)
- [Security Reviews](#security-reviews)
- [Backporting](#backporting)

## Overview

This document explains the release strategy for artifacts in this organization.

## Branching

Projects create a new branch when they need to start working on 2 separate versions of the product, with the `main` branch being the furthermost release. 

### OpenSearch Branching

[OpenSearch](https://github.com/opensearch-project/OpenSearch) typically tracks 3 releases in parallel. For example, given the last major release of 2.0, OpenSearch in this organization maintains the following active branches.

* **main**: The _next major_ release, currently 3.0. This is the branch where all merges take place, and code moves fast.
* * **2.x**: The _next minor_ release. Once a change is merged into `main`, decide whether to backport it to `2.x`.
* **1.3**: The _next patch_ release. Once a change is merged into `main`, decide whether to backport it to `1.3`.

Label PRs with the next major version label (e.g. `3.0.0`) and merge changes into `main`. Label PRs that you believe need to be backported as `2.x`, `1.x` and `1.3`. Backport PRs by checking out the versioned branch, cherry-pick changes and open a PR against each target backport branch.

### Plugin Branching

Plugins are bundled and shiped together along with OpenSearch for every release. Plugin branching follows OpenSearch core branching that will allow working on 3 releases at the same time.

### Feature Branches

Do not creating branches in the upstream repo, use your fork, for the exception of long lasting feature branches that require active collaboration from multiple developers. Name feature branches `feature/<thing>`. Once the work is merged to `main`, please make sure to delete the feature branch.

## Versioning

OpenSearch versioning [follows semver](https://opensearch.org/blog/technical-post/2021/08/what-is-semver/). 

### Version Numbers

The build number of the engine is 3-digit `major.minor.patch` (e.g. `2.9.0`), while plugins use 4 digits (`2.9.0.45`). See [OpenSearch#1093](https://github.com/opensearch-project/OpenSearch/issues/1093) for a proposal to remove this difference. 

### Incrementing Versions

Versions are incremented as soon as development starts on a given version to avoid confusion. In the examples above versions are as follows.

* OpenSearch: `main` = 3.0.0, `2.x` = 2.10.0 `1.x` = 1.4.0, and `1.2` = 1.2.5
* job-scheduler: `main` = 3.0.0.0, `2.x` = 2.10.0.0 , `1.x` = 1.4.0.0 and `1.2` = 1.2.5.0

## Tagging

Create tags after a release that match the version number, `major.minor.patch`, without a `v` prefix.

* [OpenSearch tags](https://github.com/opensearch-project/OpenSearch/tags): [1.0.0](https://github.com/opensearch-project/OpenSearch/releases/tag/1.0.0)
* [job-scheduler tags](https://github.com/opensearch-project/job-scheduler/tags): [1.0.0.0](https://github.com/opensearch-project/job-scheduler/releases/tag/1.0.0.0)

For a discussion on whether to add a prefixing `v` to release tags, see [#35](https://github.com/opensearch-project/.github/issues/35).  

## Release Labels

Repositories create consistent release labels, such as `v2.9.0`, `v1.0.0`, `v1.1.0` and `v2.0.0`, as well as `patch` and `backport`. Use release labels to target an issue or a PR for a given release. See [MAINTAINERS](MAINTAINERS.md#triage-open-issues) for more information on triaging issues.

## Releasing

OpenSearch follows semver, which means we will only release breaking changes in major versions. All minor versions are compatible with every other minor version for that major. For example, 1.2.0 will work with 1.3.2, 1.4.1, etc, but may not work with 2.0.

OpenSearch uses a “release-train” model. For minor version, that train leaves approximately every six weeks we release a new minor version which includes all the new features and fixes that are ready to go. Having a set release schedule makes sure OpenSearch is releasing in a predictable way and prevents a backlog of unreleased changes.

In contrast, OpenSearch releases new major versions only when there are a critical mass of breaking changes (e.g. changes that are incompatible with existing APIs). These tend to be tied to Lucene major version releases, and will be announced in the forums at least 4 weeks prior to the release date.

### Release Process for OpenSearch Bundle Minor Releases and Patch Releases

At the beginning of every year, the project will publish on [OpenSearch.org](https://opensearch.org/releases.html) the “release windows start” dates for the year.  These dates will be spaced out ~6 weeks.

On release window start date:
1. We generate the first candidate with all plug-ins/components that have met the entrance criteria. If a plug-in/component hasn't met all of the criteria, we'll version bump the last released version and release that.  Once the release window opens and the first candidate is generated, no additioanl features can be added, and we will not delay the start of a release window for any plug-in/component.
1. During the release window we will do final quality testing, documentation and performance testing.  Bug fixes can be added in during this time, but no new features will be added. 
1. We will generate a new candidate every day and post on the release issue about the status of the exit criteria.  When all the exit criteria have been met, we'll announce that the candidate is ready and release it 2 days later (unless that falls on Friday, in which case we'll release on Monday)
1. If we cannot clear the exit criteria within 2 weeks from the start of the window (1 week for patch releases), we will cancel that release window and try again in the next window.

Please note:  This process is for regularly scheduled minor and patch releases.  For "hot" patches (patches required for security or other urgent issues) we will build, test and release as quickly as possible.

#### Entrance Criteria to Start Release Window
* Documentation draft PRs are up and in tech review for all component changes.
* Sanity testing is done for all components.
* Code coverage has not decreased (all new code has tests).
* Release notes are ready and available for all components.
* Roadmap is up-to-date (information is available to create release highlights).
* Release ticket is cut, and there's a forum post announcing the start of the window.
* [Any necessary security reviews](##Security-Reviews) are complete. 

#### Exit Criteria to Close Release Window
* Performance tests are run, results are posted to the release ticket and there no unexpected regressions
* Documentation has been fully reviewed and signed off by the documentation community.
* All integration tests are passing
* Release blog is ready

### Release Process for OpenSearch Major Releases

OpenSearch only does major releases when there are significant breaking changes that are ready for release.  Once we become aware of the need for a major version, we will start a major version release window which will be similar to a minor release, except for two things:  Participation is mandatory for all components and the release window will be at least 4 weeks long to accommodate the testing required. 

For the actual steps to build a release, please see [Releasing OpenSearch](https://github.com/opensearch-project/opensearch-build/blob/main/README.md#releasing-opensearch).


### Security Reviews 

If you discover a potential security issue in this project we ask that you notify the OpenSearch Security Team by email at opensearch-security@amazon.com. Please do not create a public GitHub issue. See [SECURITY.md](SECURITY.md) for more information on the security response process.

The OpenSearch Project currently performs security reviews before releasing signed artifacts. These are typically conducted for any of the following:

* Releases from a new GitHub repository, such as a new plugin or extension, client, or tool. 
* Major new features added to an existing application, including significant UX or API changes. 
* Changes to authentication, authorization, cryptography, or other security-impacting functions. 
* New software or infrastructure deployed to support the project, such as CI/CD. 
* New 3rd-party providers or vendors being used by the project. 

The review process consists of building a threat model for the proposed change and optionally engaging a specialist to perform additional testing, such as a penetration testing. This process is done in parallel and in private within the project, during development, and usually takes 4-10 weeks. A repository maintainer will assess the scope of the new changes, initiate and manage a security review, provide public updates, and, if needed, communicate privately by email with the contributors. Please add a note in your pull request if you believe a security review is warranted. 

Please see [opensearch-project/.github#81](https://github.com/opensearch-project/.github/issues/81) for a discussion on improving this and other security-related processes.​

## Backporting

This project follows [semantic versioning](https://semver.org/spec/v2.0.0.html). Backwards-incompatible changes always result in a new major version and will __never__ be backported. Small improvements and features will be backported to a new minor version (e.g. `2.9.0`). Security fixes will be backported to a new patch version (e.g. `2.9.1`).

Here are the commands we typically run to backport changes to release branches:

1. Checkout the target release branch and pull the latest changes from `upstream`. In the examples below, our target release branch is `2.x`.

```
git checkout 2.x
git pull upstream 2.x
```

2. Create a local branch for the backport. A convenient naming convention is _backport-\[PR-id\]-\[target-release-branch\]_.

```
git checkout -b backport-pr-xyz-1.x
```

3. Cherry-pick the commit to backport. Remember to include [DCO signoff](CONTRIBUTING.md#developer-certificate-of-origin).

```
git cherry-pick <commit-id> -s
```

4. Push the local branch to your fork.

```
git push origin backport-pr-xyz-1.x
```

5. Create a pull request for the change.
