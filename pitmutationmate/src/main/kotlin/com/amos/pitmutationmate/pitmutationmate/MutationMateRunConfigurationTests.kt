//package com.amos.pitmutationmate.pitmutationmate
//
//import com.intellij.execution.ExecutionException
//import com.intellij.execution.Executor
//import com.intellij.execution.configurations.*
//import com.intellij.execution.process.ProcessHandler
//import com.intellij.execution.process.ProcessHandlerFactory
//import com.intellij.execution.process.ProcessTerminatedListener
//import com.intellij.execution.runners.ExecutionEnvironment
//import com.intellij.openapi.options.SettingsEditor
//import com.intellij.openapi.project.Project
//import org.gradle.tooling.*
//import org.gradle.tooling.model.GradleProject
//import org.jetbrains.annotations.NotNull
//import java.io.File
//
//class MutationMateRunConfigurationTests(
//    project: Project,
//    factory: ConfigurationFactory?,
//    name: String?
//) : RunConfigurationBase<MutationMateRunConfigurationOptions?>(project, factory, name) {
//    private val projectDir: String
//    private val tasks: Array<String>
//
//    init {
//        projectDir = project.basePath!!
//        tasks = arrayOf(taskName ?: "pitest")
//    }
//
//    override fun getOptions(): MutationMateRunConfigurationOptions {
//        return super.getOptions() as MutationMateRunConfigurationOptions
//    }
//
//    var taskName: String?
//        get() = options.taskName
//        set(taskName) {
//            options.taskName = taskName
//        }
//    var cliOrGradle: Boolean
//        get() = options.cliOrGradle
//        set(cliOrGradle) {
//            options.cliOrGradle = cliOrGradle
//        }
//
////    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
////        return MutationMateSettingsEditor()
////    }
//
//    override fun getState(
//        executor: Executor,
//        environment: ExecutionEnvironment
//    ): RunProfileState {
//        return if (cliOrGradle) {
//            getGradleCliState(environment)
//        } else {
//            getCLIState(environment)
//        }
//    }
//
//    private fun getCLIState(environment: ExecutionEnvironment): RunProfileState {
//        return object : CommandLineState(environment) {
//            @NotNull
//            @Throws(ExecutionException::class)
//            override fun startProcess(): ProcessHandler {
////                try {
////                    val connector = GradleConnector.newConnector()
////                    connector.forProjectDirectory(File(project.basePath!!))
////                    val connection = connector.connect()
////                    val model = connection.getModel(IdeaProject::class.java)
////
////
////                    // Access Gradle model
//////                val ideaModules: List<IdeaModule> = model.modules
////                    for (ideaModule: IdeaModule in model.modules) {
////                        // Access module information
////                        val moduleName = ideaModule.name
////                        ideaModule
//////                    val modulePath = ideaModule.
////                        Messages.showMessageDialog(project, "Module: $moduleName\nPath: modulePath", "Module Information", Messages.getInformationIcon())
////                    }
////
////                    connection.close()
////                } catch (ex: Exception) {
////                    ex.printStackTrace()
////                    Messages.showErrorDialog(project, "Error accessing Gradle model: ${ex.message}", "Error")
////                }
////
////                GradleConnector.newConnector()
////                    .forProjectDirectory(File(projectDir))
////                    .connect().use { connection ->
////
////                        val project: GradleProject = connection.getModel(GradleProject::class.java)
////
////                        println("Project: " + project.name)
////                        println("Tasks:")
////                        for (task in project.tasks) {
////                            var taskName = task.name
////                            if (taskName.startsWith("pitest")) {
////                                taskName = taskName.replace("pitest", "pitestMain")
////                            }
////                            println("    " + task.name)
////                        }
////                    }
//
//                val commandLine = GeneralCommandLine("./gradlew", "pitest")
//                    .withWorkDirectory(projectDir)
//                val processHandler = ProcessHandlerFactory.getInstance()
//                    .createColoredProcessHandler(commandLine)
//                ProcessTerminatedListener.attach(processHandler)
//                return processHandler
//            }
//        }
//    }
//
//    private fun getGradleCliState(environment: ExecutionEnvironment) : RunProfileState {
//        return object : CommandLineState(environment) {
//            @NotNull
//            @Throws(ExecutionException::class)
//            override fun startProcess(): ProcessHandler {
//
//                GradleConnector.newConnector()
//                    .forProjectDirectory(File(projectDir))
//                    .connect().use { connection ->
//
//                        //run some tasks
//                        connection.newBuild()
//                            .forTasks("tasks")
//                            .setStandardOutput(System.out)
//                            .setStandardError(System.err)
//                            .run()
//                    }
//                return null!!
//            }
//        }
//    }
//}