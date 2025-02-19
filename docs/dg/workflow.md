{% set title = "Workflow" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

Our workflow is mostly based on the guidelines given at se-education.org/guides.
</div>

**To submit a PR**, follow [this guide](https://se-education.org/guides/guidelines/PRs.html), but note the following:

* As we squash the commits when merging a PR, there is ==no need to follow a strict commit organization or write elaborate commit messages for each commit==.<br>
  However, when pushing new commits to your PR branch, do clean up _new_ commits (i.e., commits not yet pushed) e.g., delete temporary print statements added for debugging purposes.
* In the PR description, please propose a commit message to be used when the PR is merged eventually. The commit message should follow the guidelines given [here](https://se-education.org/guides/guidelines/PRs.html). You may refer to [this PR](https://github.com/reposense/RepoSense/pull/1057) for an example.
* For simple documentation fixes or tasks with clear instructions, it is unnecessary to create an issue before creating a PR.
* You can refer to the [Architecture](architecture.html) and the [HTML Report](report.html) sections to learn about the design and implementation of RepoSense.
* The section below has more information about the various stages of submitting a PR.

<!-- ==================================================================================================== -->

## Find the suitable pull requests

* If you are contributing to RepoSense for the first time, you can check the list of [backend issues](https://github.com/reposense/RepoSense/issues?q=is%3Aopen+is%3Aissue+label%3Aa-Backend+label%3Ad.FirstTimers) or [frontend issues](https://github.com/reposense/RepoSense/issues?q=is%3Aopen+is%3Aissue+label%3Ad.FirstTimers+label%3Aa-FrontEnd) for first timers.

<box type="info" seamless>

The issues for first timers usually have guidance provided in the comment or have linked pull requests from previous contributors. You can refer to them for implementation details.
</box>

* If you are more experienced in contributing, aside from searching for issues in the issue tracker, you can find the list of issues organized in a more systematic way under the [Projects Tab](https://github.com/reposense/RepoSense/projects) of the RepoSense repository. This can help you to find issues with suitable workload and direction.


<!-- ==================================================================================================== -->

## Following the coding standards

* Make sure you know our coding standards.
  {{ embed('Appendix: Coding Standards', 'styleGuides.md', level=2) }}
* **Follow [the tutorial](https://se-education.org/guides/tutorials/intellijCodeStyle.html) to configure Intellij to follow our coding style**.
* **This project uses Checkstyle** to check the compliance of Java code. You can use [this document](https://se-education.org/guides/tutorials/checkstyle.html) to find how to use it. In particular, run `gradlew checkstyleMain checkstyleTest checkstyleSystemtest` to check the style of all the relevant Java code.
* **To check Pug files for style errors**, run `npm run lint` from the project root directory. You can use the `npm run lintfix` to automatically fix some of the JavaScript and CSS lint errors.

<!-- ==================================================================================================== -->

## Running the app from code

<div id="section-running-from-code">

* To run the app from code, run `gradlew run` from the project root. By default, it will run based on the config files in the `[project root]/config` folder, and generate the report in the `[project root]/reposense-report` folder.
* To supply flags to customize the report, you can use the `-Dargs="[FLAGS]"` format.<br>
  e.g., `gradlew run -Dargs="--since 31/12/2019 --formats java adoc xml"`
* Run `gradlew run -Dargs="--view"` to generate the report and view it in the default browser.
* You can refer to the panel below for the format of the flags that can be supplied in `-Dargs="[FLAGS]"`.

{{ embed("User guide → Appendix: **CLI syntax reference**", "../ug/cli.md") }}
</div>

<!-- ==================================================================================================== -->

## Debugging (front-end)

**You can use the hot reloading feature to see how your code changes the functionality of the website in real time.**
1. Navigate to the project root in your terminal.
1. Generate the desired data for the report using `gradlew run` with the appropriate flags.
1. Run `gradlew hotReloadFrontend`.
1. The website will be automatically opened in your browser shortly.

**You can use Vue.js devtools for frontend debugging on Chrome.** Here are the steps:
1. On your Chrome, visit the website of [Vue.js devtools](https://chrome.google.com/webstore/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd) and add the extension.
1. Go to the detail page of this extension in Chrome's extension management panel and select `Allow access to file URLs`. If you are unable to locate it, copy the link: `chrome://extensions/?id=nhdogjmejiglipccpnnnanhbledajbpd` and visit it on your Chrome.
1. Open any report generated by RepoSense.
1. Press `F12` or right click and choose `inspect` at the report page.
1. Choose `Vue` at the navigation bar.<br>
   ![Choose Vue](../images/choose-vue.png)
1. Debug using the tool.<br>
   ![Use Vue](../images/use-vue.png)

<box type="info" seamless>

See [vue-devtools project home](https://github.com/vuejs/vue-devtools) page for more details.
</box>

<!-- ==================================================================================================== -->

## Testing (front-end)

**We use [Cypress](https://www.cypress.io/) for automated end-to-end front-end testing.**

### Writing tests
1. Create a new test file in `frontend/cypress/tests`.
1. At project root start *Cypress Test Runner* by running `gradlew cypress`.
1. On the top right hand corner, set `Chrome` as the default browser.
1. Under **Integration Tests**, click on the newly created test file to run it.
![Cypress Test Runner](../images/cypress-test-runner.jpg "Cypress Test Runner")

<box type="info" seamless>

Read [Cypress's Documentation](https://docs.cypress.io/api/commands/document.html#Syntax) to familiarize yourself with its syntax and [Cypress's debugging guide](https://docs.cypress.io/guides/guides/debugging.html#Log-Cypress-events) to tackle problems with your tests.
</box>

<box type="warning" seamless>

Note that it is **compulsory** to add tests for the new front-end changes that you made to prevent regression bugs, except for trivial changes that are unlikely to cause any regression or other situations where testing does not apply to the change.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### Running tests

To run all tests locally, run `gradlew frontendTest`.

<box type="info" seamless>

If you encountered an invalid browser error, ensure that you have `Chrome` installed in the default installation directory. Otherwise, follow the instructions [here](https://docs.cypress.io/guides/guides/debugging.html#Launching-browsers) to create symbolic links so Cypress can locate `Chrome` in your system.
</box>

<!-- ==================================================================================================== -->

## Testing (back-end)

The back-end tests can be found at `[project root]/systemtest` and `[project root]/test`.

### Running tests

To run all the system tests, run `gradlew systemtest`.

To run all the unit and integration tests, run `gradlew test`.

<!-- ==================================================================================================== -->

## Writing documentation

**This project uses [MarkBind](https://markbind.org/)** for documentation. Follow [this tutorial](https://se-education.org/guides/tutorials/markbind.html) to learn how to use MarkBind for updating project documentation.

**To show some content only in the <tooltip content="i.e., https://reposense.org">production website</tooltip>**, add the `tags="production"` attribute to the HTML element enclosing the content. Similarly, `tags="dev"` will make the content appear only in the <tooltip content="i.e., https://reposense.org/RepoSense">dev website</tooltip>.

```html
<span tags="production">This will appear in the production website only.</span>
<span tags="dev">This will appear in the dev website only.</span>
This will appear in both sites.
```
