// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.execution

import com.intellij.openapi.util.SystemInfo

fun interface SystemInfoProvider {
    fun isWindows(): Boolean
}

class SystemInfo : SystemInfoProvider {
    override fun isWindows(): Boolean {
        return SystemInfo.isWindows
    }
}
