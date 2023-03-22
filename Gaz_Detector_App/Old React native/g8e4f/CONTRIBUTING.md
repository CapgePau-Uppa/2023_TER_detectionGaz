# Contributing

Welcome, and thank you for your interesting in contributing to this project!

New contributions are always welcome, but please follow these guidelines below.

## Pre-requisites

In order to download necessary tools, clone the repository , and install dependencies via `yarn`, you need network access.

You will need the following tools:

- [Git](https://git-scm.com/)
- [NodeJS](https://nodejs.org/en/)
- [Yarn](https://yarnpkg.com/)
- [React Native](https://reactnative.dev/docs/environment-setup) - Follow instructions for React Native CLI Quickstart to set up your React Native development environment.

## Installing

Use `yarn install` to install the packages.

## Build and Run

To start Metro, the JavaScript bundler for `react-native`, just run `yarn start`. Once this is running, follow instructions for running the mobile app on [React Native Docs](https://reactnative.dev/docs/environment-setup). Each time you update the code, the bundler should automatically update.

## Code

In general formatting, please follow the [EditorConfig](https://editorconfig.org/) guidelines for the project as well as the [Prettier](https://prettier.io/) config.

### TypeScript

We use TypeScript in our project to write Typed JavaScript. It helps speeds up the development experience by catching errors and providing fixes before the code is even run.

### Linting

We use [eslint](https://eslint.org/) for linting our sources. You can run eslint across the sources by running `yarn lint`.

## Work Branches

Even if you have push rights on this repository, you should create a personal fork and create feature branches there when you need them. This keeps the main repository clean and your personal workflow cruft out of sight.

## Pull Requests

To enable us to quickly review and accept your pull requests, always create one pull request per issue and link the issue in the pull request. Never merge multiple requests in one unless they have the same root cause. Be sure to follow the linting rules and keep code changes as small as possible. Avoid pure formatting changes to code that has not been modified otherwise. Pull requests should contain tests whenever possible.

Please follow the [seven rules of a great Git commit message](https://chris.beams.io/posts/git-commit/). This means clean, consistent and understandable history. It would also be preferable that you squash your commits into one before submitting a final Pull Request, but that can also be done by us when we merge in your code.

## Bugs and Issues

Please create [issues](https://github.com/OpenVentPk/ventilator-app/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc) for any bugs in the code. Well structured, detailed bug reports are hugely valuable for the project.

Guidelines for reporting bugs:

- Check the issue search to see if it has already been reported
- Isolate the problem to a simple test case
- Please include steps to reproduce it.
- Please provide any additional details associated with the bug, if it's screen density specific or only happens with certain configuration/data.
