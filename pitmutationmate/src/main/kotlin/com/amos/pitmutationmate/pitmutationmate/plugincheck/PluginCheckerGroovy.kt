// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.plugincheck

import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.MethodCallExpression

class PluginCheckerGroovy : CodeVisitorSupport() {

    var pitestPluginAvailable = false
    var companionPluginAvailable = false

    override fun visitMethodCallExpression(call: MethodCallExpression?) {
        val method = call?.methodAsString
        if(method.equals("plugins") || method.equals("apply") || method.equals("version")) {
            super.visitMethodCallExpression(call)
        } else if(method.equals("id")) {
            if (call != null) {
                val pluginName = call.arguments.text
                if(pluginName.contains("pitest")) {
                    pitestPluginAvailable = true
                }
                if(pluginName.contains("io.github.amosproj.pitmutationmate.override")) {
                    companionPluginAvailable = true
                }
            }
        }
    }
}
