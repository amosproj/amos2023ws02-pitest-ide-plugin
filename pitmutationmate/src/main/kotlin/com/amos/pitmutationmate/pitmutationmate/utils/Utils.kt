// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

class Utils {
    companion object {
        fun getPsiFileFromPath(project: Project, filePath: String): PsiFile? {
            return LocalFileSystem.getInstance().findFileByPath(filePath)
                ?.let { PsiManager.getInstance(project).findFile(it) }
        }
    }
}
