// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.GradleTaskExecutor
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ColoredProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationRequest
import org.apache.maven.shared.invoker.Invoker
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.build.BuildEnvironment
import org.jetbrains.annotations.NotNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream


class MutationMateRunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<MutationMateRunConfigurationOptions?>(project, factory, name) {

    private val projectDir: String

    init {
        projectDir = project.basePath!!
    }

    override fun getOptions(): MutationMateRunConfigurationOptions {
        return super.getOptions() as MutationMateRunConfigurationOptions
    }

    var taskName: String?
        get() = options.taskName
        set(taskName) {
            options.taskName = taskName
        }

    var gradleExecutable: String
        get() = options.gradleExecutable ?: ""
        set(gradleExecutable) {
            options.gradleExecutable = gradleExecutable
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return MutationMateSettingsEditor()
    }

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState {

        var buildSystem: String? = null

        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {

//            println("Module ${module.name} has path ${module.moduleFilePath}")

//            println("Buildsystem:")
            val moduleBuildSystem = getBuildSystemForModule(module)
            if (moduleBuildSystem != null) {
                buildSystem = moduleBuildSystem
            }

//            println("Class roots:")
//            ModuleRootManager.getInstance(module).orderEntries().withoutSdk().classes().roots.forEach {
//                println(it)
//            }
//
//            println("Full classpath:")
//            val fullClasspath = OrderEnumerator.orderEntries(module).withoutSdk().recursively().getPathsList().getPathsString()
//            println(fullClasspath)
//            println()


        }

        if (buildSystem == null) {
            throw ExecutionException("Could not find build system for project")
        }
        else if (buildSystem != "GRADLE") {
            return runMavenGoal(environment)
        }

        return object : CommandLineState(environment) {
            @NotNull
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val gradleTaskExecutor = GradleTaskExecutor()
                return gradleTaskExecutor.executeTask(projectDir, gradleExecutable, taskName)
            }
        }
    }

    fun runMavenGoal(environment: ExecutionEnvironment): RunProfileState {
        return MavenInvocationCommandLineState(environment)
    }

    fun getBuildSystemForModule(module: Module): String? {
        val manager = ExternalSystemModulePropertyManager.getInstance(module)
        return manager.getExternalSystemId()
    }

    fun extractPitestSettings() {
        val connection: ProjectConnection = GradleConnector.newConnector()
            .forProjectDirectory(File(projectDir))
            .connect()

        val model = connection.model(org.gradle.tooling.model.GradleProject::class.java).get()
        model.tasks.find { it.name == "pitest" }?.let {
            println("Found pitest task")
            println(it)
//            it.javaClass.
        }

        connection.close()
    }

    fun extractPitestClassPath() {
        val connection: ProjectConnection = GradleConnector.newConnector()
            .forProjectDirectory(File(projectDir))
            .connect()

        val buildScriptContent = """
        tasks.register("printRuntimeClasspath") {
            doLast {
                val pitest = configurations.getByName("pitest")
                if (pitest.isCanBeResolved) {
                    pitest.forEach { file ->
                        println(file)
                    }
                }
            }
        }
        """.trimIndent()

        //obtain some information from the build
        //obtain some information from the build
        val environment = connection.model(
            BuildEnvironment::class.java
        ).get()

        //run some tasks
        val outputStream = ByteArrayOutputStream()
        val dependenciesOutput = PrintStream(outputStream)

        //run some tasks
        connection.newBuild()
            .forTasks("dependencies")
            .setStandardOutput(dependenciesOutput)
            .run()

        println("Dependencies:")
        println(outputStream.toString())

    }

}



class MavenInvocationCommandLineState(private val environment: ExecutionEnvironment) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val goal = "org.pitest:pitest-maven:mutationCoverage"

        val invocationRequest: InvocationRequest = DefaultInvocationRequest()
        invocationRequest.pomFile = File(environment.project.basePath + "/pom.xml")
        invocationRequest.goals = listOf(goal) // Set the Maven goals you want to execute

        val invoker = DefaultInvoker()
        val outputHandler = StringBuilder()
        invoker.setOutputHandler { line: String? -> outputHandler.append(line).append("\n") }

        try {
            invoker.execute(invocationRequest)
        } catch (e: Exception) {
            outputHandler.append("Maven invocation failed: ${e.message}")
        }

        val generalCommandLine = GeneralCommandLine()
        generalCommandLine.exePath = "echo"
        generalCommandLine.addParameter(outputHandler.toString())

        val processHandler = ColoredProcessHandler(generalCommandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }
}