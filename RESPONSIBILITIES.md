- [Overview](#overview)
- [Current Maintainers](#current-maintainers)
- [Maintainer Responsibilities](#maintainer-responsibilities)
  - [Uphold Code of Conduct](#uphold-code-of-conduct)
  - [Prioritize Security](#prioritize-security)
  - [Review Pull Requests](#review-pull-requests)
  - [Merging a Pull Request](#merging-a-pull-request) 
  - [Triage Open Issues](#triage-open-issues)
    - [Automatically Label Issues](#automatically-label-issues)
  - [Be Responsive](#be-responsive)
  - [Maintain Overall Health of the Repo](#maintain-overall-health-of-the-repo)
    - [Keep Dependencies up to Date](#keep-dependencies-up-to-date)
  - [Manage Roadmap](#manage-roadmap)
  - [Add Continuous Integration Checks](#add-continuous-integration-checks)
  - [Use Semver](#use-semver)
  - [Release Frequently](#release-frequently)
  - [Promote Other Maintainers](#promote-other-maintainers)
  - [Describe the Repo](#describe-the-repo)
- [Becoming a Maintainer](#becoming-a-maintainer)
  - [Nomination](#nomination)
  - [Interest](#interest)
  - [Addition](#addition)
- [Removing a Maintainer](#removing-a-maintainer)
  - [Moving On](#moving-on)
  - [Inactivity](#inactivity)
  - [Negative Impact on the Project](#negative-impact-on-the-project)

## Overview

This document explains who maintainers are, what they do in various repos of opensearch-project, and how they should be doing it. If you're interested in contributing, see [CONTRIBUTING](CONTRIBUTING.md).

## Current Maintainers

Each repo contains a [MAINTAINERS.md](MAINTAINERS.md) file that lists current maintainers, and points to this document.

## Maintainer Responsibilities

Maintainers are active and visible members of the community, and have [maintain-level permissions on a repository](https://docs.github.com/en/organizations/managing-access-to-your-organizations-repositories/repository-permission-levels-for-an-organization). Use those privileges to serve the community and evolve code as follows.

### Uphold Code of Conduct

Model the behavior set forward by the [Code of Conduct](CODE_OF_CONDUCT.md) and raise any violations to other maintainers and [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md).

### Prioritize Security

Security is your number one priority. Maintainer's Github keys must be password protected securely and any reported security vulnerabilities are addressed before features or bugs.

Note that this repository is monitored and supported 24/7 by Amazon Security, see [Reporting a Vulnerability](SECURITY.md) for details.

### Review Pull Requests

It's our responsibility to ensure the content and code in pull requests are correct and of high quality before they are merged. Here are some best practices:

- Leverage the issue triaging process to review pull requests and assign them to maintainers for review.
- In cases of uncertainty on how to proceed, search for related issues and reference the pull request to find additional collaborators.
- When providing feedback on pull requests, make sure your feedback is actionable to guide the pull request towards a conclusion.
- If a pull request is valuable but isn't gaining traction, consider reaching out to fulfill the necessary requirements. This way, the pull request can be merged, even if the work is done by several individuals.
- Lastly, strive for progress, not perfection.

### Merging a Pull Request 

It is important that commit messages are helpful in understanding the reasons for a given commit and maintain good commit hygiene by only keeping the relevant information. 

Most repositories in [opensearch-project](https://github.com/opensearch-project) are configured to require commits to be squashed into a single commit when merging pull requests. If the pull request contains multiple commits then messages from all commits will be appended into a single message, which usually requires editing to produce a high quality commit message. When merging pull requests, edit commit messages by following these steps as much as possible:

- The commit subject should be concise (ideally within 50 characters) and clearly convey what is being merged.
- The commit body should include the details (if any, and possibly within 72 characters) about the commit, typically inline with the PR description.
- The commit body should include the 'Signed-off-by:*' for all committers involved in the change and thereby indicating that all code contributors agree to the [DCO](https://github.com/opensearch-project/.github/blob/main/CONTRIBUTING.md#developer-certificate-of-origin).
- There need to be a matching 'Signed-Off-By:' line for the `This commit will be authored by *` email address otherwise backport DCO checks will fail.

### Triage Open Issues

Manage labels, review issues regularly, and triage by labelling them.

All repositories in this organization have a standard set of labels, including `bug`, `documentation`, `duplicate`, `enhancement`, `good first issue`, `help wanted`, `blocker`, `invalid`, `question`, `wontfix`, and `untriaged`, along with release labels, such as `v1.0.0`, `v1.1.0`, `v2.0.0`, `patch`, and `backport`.

Use labels to target an issue or a PR for a given release, add `help wanted` to good issues for new community members, and `blocker` for issues that scare you or need immediate attention. Request for more information from a submitter if an issue is not clear. Create new labels as needed by the project.

See [TRIAGING](TRIAGING.md) for more information on how to attend triage meetings.

#### Automatically Label Issues

There are many tools available in GitHub for controlling labels on issues and pull requests.  Use standard issue templates in the [./.github/ISSUE_TEMPLATE](./.github/ISSUE_TEMPLATE) directory to apply appropriate labels such as `bug` and `untriaged`.  Repositories can choose to use GitHub actions such as [add-untriaged.yml](./.github/workflows/add-untriaged.yml) to apply labels automatically.

### Be Responsive

Respond to enhancement requests, and forum posts. Allocate time to reviewing and commenting on issues and conversations as they come in.

### Maintain Overall Health of the Repo

Keep the `main` branch at production quality at all times. Backport features as needed. Cut release branches and tags to enable future patches.

#### Keep Dependencies up to Date

Maintaining up-to-date dependencies on third party projects reduces the risk of security vulnerabilities. The Open Source Security Foundation (OpenSSF) [recommends](https://github.com/ossf/scorecard/blob/main/docs/checks.md#dependency-update-tool) either [dependabot](https://docs.github.com/en/code-security/dependabot) or [renovatebot](https://docs.renovatebot.com/). Both of these applications generate Pull Requests for dependency version updates.
 - Renovate is integrated as part of the Remediate app in [Mend for Github](https://github.com/apps/mend-for-github-com), which is enabled on all opensearch-project repositories. It can be enabled in the `.whitesource` configuration file as described in the [Mend Remediate and Renovate](https://docs.mend.io/bundle/integrations/page/mend_remediate_and_renovate.html#Integration-with-Mend-Renovate) documentation. The [Merge Confidence](https://docs.renovatebot.com/merge-confidence/) feature can be configured to provide maintainers more information on the age, adoption rate, and percent test passing rate of other repositories. Mend maintains a "Dependency Dashboard" Issue in the repository with centralized information on pending version update PRs.
 - Dependabot is integrated with GitHub and can be enabled by adding a [`dependabot.yml`](https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/configuring-dependabot-version-updates) file to the repo. Dependabot does not have any centralized management dashboard, so maintainers may use tags or other PR filters to track pending updates.

### Manage Roadmap

Ensure the repo highlights features that should be elevated to the project roadmap. Be clear about the feature’s status, priority, target version, and whether or not it should be elevated to the roadmap. Any feature that you want highlighted on the OpenSearch Roadmap should be tagged with "roadmap". The OpenSearch [project-meta maintainers](https://github.com/opensearch-project/project-meta/blob/main/MAINTAINERS.md) will highlight features tagged "roadmap" on the project wide [OpenSearch Roadmap](https://github.com/orgs/opensearch-project/projects/1).

### Add Continuous Integration Checks

Add integration checks that validate pull requests and pushes to ease the burden on Pull Request reviewers.

### Use Semver

Use and enforce [semantic versioning](https://semver.org/) and do not let breaking changes be made outside of major releases.

### Release Frequently

Make frequent project releases to the community.

### Promote Other Maintainers

Assist, add, and remove [MAINTAINERS](MAINTAINERS.md). Exercise good judgement, and propose high quality contributors to become co-maintainers. See [Becoming a Maintainer](#becoming-a-maintainer) for more information.

### Describe the Repo

Make sure the repo has a well-written, accurate, and complete description. See [opensearch-project/.github#38](https://github.com/opensearch-project/.github/issues/38) for some helpful tips to describe your repo.

## Becoming a Maintainer

You can become a maintainer by actively [contributing](CONTRIBUTING.md) to any project, and being nominated by an existing maintainer.

### Nomination

Any current maintainer starts a private e-mail thread (until we have a better mechanism, e-mail addresses can usually be found via MAINTAINERS.md + DCO) with all other maintainers on that repository to discuss nomination using the template below. In order to be approved, at least three positive (+1) maintainer votes are necessary, and no vetoes (-1). In rare cases when there are fewer than three maintainers, the positive (+1) votes from all maintainers are required. Any disagreements can be escalated to the [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md).

The nomination should clearly identify the person with their real name and a link to their GitHub profile, and the rationale for the nomination, with concrete example contributions.

### Interest

Upon receiving at least three positive (+1) maintainer votes, and no vetoes (-1), from existing maintainers after a one week period, the nominating maintainer asks the nominee whether they might be interested in becoming a maintainer on the repository via private e-mail message.

> This is great work! Based on your valuable contribution and ongoing engagement with the project, the current maintainers invite you to become a co-maintainer for this project. Please respond and let us know if you accept the invitation to become maintainer.

Individuals accept the nomination by replying, or commenting, for example _"Thank you! I would love to."_

### Addition

Upon receiving three positive (+1) maintainer votes, and no vetoes (-1), from other maintainers, and after having privately confirmed interest with the nominee, the maintainer opens a pull request adding the proposed co-maintainer to MAINTAINERS.md. The pull request is approved and merged.

> _Content from the above nomination._
>
> The maintainers have voted and agreed to this nomination.

The [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md) adjusts the new maintainer’s permissions accordingly, and merges the pull request.

## Removing a Maintainer

Removing a maintainer is a disruptive action that the community of maintainers should not undertake lightly. There are several reasons a maintainer will be removed from the project, such as violating the [code of conduct](https://github.com/opensearch-project/.github/blob/main/CODE_OF_CONDUCT.md), or taking other actions that negatively impact the project.

### Moving On

There are plenty of reasons that might cause someone to want to take a step back or even a hiatus from a project. Existing maintainers can choose to leave the project at any time, with or without reason, by making a pull request to move themselves to the "Emeritus" section of MAINTAINERS.md, and asking an [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md) to remove their permissions.

### Inactivity

Maintainer status never expires. If a maintainer becomes inactive for a time (usually several months), or a maintainer can confirm that they are no longer involved with the project for any reason, the [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md) may make a pull request to move them to the "Emeritus" section of the MAINTAINERS.md, remove them from CODEOWNERS, and upon merging the pull request, revoke the maintainer level access. Any past maintainer can be reinstated via another pull request, and have their permissions restored by the [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md) at any time upon request.

If the repo is left without any maintainers, either by maintainer inactivity or moving on, the repo is considered unmaintained. The [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md) will seek out new maintainers and note the maintenance status in the repo README file.

### Negative Impact on the Project

Actions that negatively impact the project will be handled by the [members of the admin team](https://github.com/opensearch-project/.github/blob/main/ADMINS.md), in coordination with other maintainers, in balance with the urgency of the issue. Examples would be [Code of Conduct](CODE_OF_CONDUCT.md) violations, deliberate harmful or malicious actions, and security risks.
