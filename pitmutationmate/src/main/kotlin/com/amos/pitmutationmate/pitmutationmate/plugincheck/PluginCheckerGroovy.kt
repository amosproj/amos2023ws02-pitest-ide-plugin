// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.plugincheck

import com.intellij.openapi.diagnostic.thisLogger
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.MethodCallExpression

class PluginCheckerGroovy : CodeVisitorSupport() {

    var pitestPluginAvailable = false
    var companionPluginAvailable = false

    override fun visitMethodCallExpression(call: MethodCallExpression?) {
        thisLogger().debug("checking groovy plugins visitMethodCallExpression")

        val method = call?.methodAsString
        if (method.equals("plugins") || method.equals("apply") || method.equals("version")) {
            super.visitMethodCallExpression(call)
        } else if (method.equals("id")) {
            if (call != null) {
                val pluginName = call.arguments.text
                if (pluginName.contains(PluginName.PITEST)) {
                    pitestPluginAvailable = true
                    thisLogger().debug("groovy - pitest plugin available")
                }
                if (pluginName.contains(PluginName.COMPANION)) {
                    companionPluginAvailable = true
                    thisLogger().debug("groovy - companion plugin available")
                }
            }
        }
    }
}
