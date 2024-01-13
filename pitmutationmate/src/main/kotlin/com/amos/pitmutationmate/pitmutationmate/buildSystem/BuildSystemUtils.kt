// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.buildSystem

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.idea.configuration.isMavenized

class BuildSystemUtils {
    companion object {
        fun getProjectBuildFiles(project: Project): Collection<VirtualFile> {
            if (project.isMavenized) {
                thisLogger().debug("Project is mavenized")
                return FilenameIndex.getVirtualFilesByName("pom.xml", GlobalSearchScope.projectScope(project))
            }
            thisLogger().debug("Project is not mavenized. Returning all build.gradle and build.gradle.kts files")
            return FilenameIndex.getVirtualFilesByName("build.gradle", GlobalSearchScope.projectScope(project)) +
                FilenameIndex.getVirtualFilesByName("build.gradle.kts", GlobalSearchScope.projectScope(project))
        }
    }
}
