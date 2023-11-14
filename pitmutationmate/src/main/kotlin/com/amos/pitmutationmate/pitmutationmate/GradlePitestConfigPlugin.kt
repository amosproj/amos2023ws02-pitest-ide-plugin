//// SPDX-License-Identifier: MIT
//// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>
//
//package com.amos.pitmutationmate.pitmutationmate
//
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//
//class GradlePitestConfigPlugin : Plugin<Project> {
//    override fun apply(project: Project) {
//        println("GradlePitestConfigPlugin applied")
//        project.afterEvaluate {
//            println("GradlePitestConfigPlugin afterEvaluate")
//            // Access and inspect the project's model after evaluation
//            val pitestTask = project.tasks.findByName("pitest")
//            if (pitestTask != null) {
//                println("Pitest task found")
//                // The 'pitest' task (or extension) is present
//                // You can attempt to access and modify its configuration
//            }
//        }
//    }
//}
