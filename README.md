<!--
SPDX-License-Identifier: MIT
SPDX-FileCopyrightText: 2023
-->

# Pitest IDE Plugin (AMOS WS 2023/2024 Project #2)

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="Deliverables/sprint-01/team-logo-darkmode.png" width="600">
  <img alt="Text changing depending on mode. Light: 'So light!' Dark: 'So dark!'" src="Deliverables/sprint-01/team-logo.png" width="600">
</picture>

## About the Project

[PIT (Pitest)](https://pitest.org/) is a popular mutation testing framework for Java / JVM-based languages.
This project is to develop a plugin for the [IntelliJ](https://www.jetbrains.com/idea/) / [Android Studio (JetBrains)](https://developer.android.com/studio) IDE so that mutation tests can be conveniently run from within the IDE, similar to the JUnit plugins available for most IDEs.
Users of the IDE shall be able to

- Start a Pitest run for a class directly in the code editor
- Control (rerun, stop, ...) the execution
- Configure (verbosity, mutators, targeting tests, ...) the execution
- Receive results as inline annotations of the code
- Inspect the results, in particular any surviving mutations

## Meet the team

Scrum Master: [@oliviadargel](https://github.com/oliviadargel)

Product Owner: [@emuguy1](https://github.com/emuguy1), [@Felix-012](https://github.com/Felix-012)

Developer: [@lheimbs](https://github.com/lheimbs), [@QW3RAT](https://github.com/QW3RAT), [@nikomall34](https://github.com/nikomall34), [@timherzig](https://github.com/timherzig), [@lfogarty98](https://github.com/lfogarty98), [@brianneoberson](https://github.com/brianneoberson)

## Product Vision

Software quality hinges on robust testing practices. While code coverage remains a prevalent metric, evaluating the true effectiveness of tests in ensuring expected behavior often gets overlooked. This is where Mutation Testing steps in—a method that generates code variations to evaluate the ability of tests to detect changes.

PiTest, a leading tool in Mutation Testing, falls short due to its limited integration capabilities. It lacks the functionality to display test run results and configure test scope dynamically, creating a gap in assessing test effectiveness within the environment best known to the developer.

Our product vision is to introduce an IntelliJ IDE plugin that not only presents PiTest results but also empowers users to seamlessly fine-tune test scopes, even down to specific classes. By integrating these features, we aim to bridge the existing gap, providing enhanced visibility and control within the familiar IntelliJ environment, thereby ensuring higher-quality test outcomes.

## Product Mission

Our mission is to enhance software mutation testing within the IntelliJ IDE by implementing a specifically designed plugin that integrates with PiTest. The approach involves several key steps:

Integration Development: We will develop an plugin that integrates with IntelliJ IDE, ensuring that PiTest's functionalities are easily accessible within the developer's primary workspace.

Dynamic Test Configuration: A core feature of our plugin will be to enable dynamic configuration of test scopes. This will allow developers to selectively fine-tune their testing efforts, focusing on specific classes or modules.

Result Visualization: The plugin will provide visualizations of Mutation Testing results. This will make it more comfortable for developers to interpret PiTest outputs.

User-Centric Design: The interface and functionality of the plugin will be designed with a strong focus on user experience, ensuring that it is both powerful and easy to use.

By following these steps, we aim to not only enhance PiTest’s functionality within IntelliJ IDE but also empower developers with more efficient, precise, and user-friendly software testing tools, ultimately leading to higher quality software development.
