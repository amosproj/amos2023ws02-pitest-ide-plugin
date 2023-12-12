// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.plugincheck

import org.jetbrains.kotlin.idea.structuralsearch.visitor.KotlinRecursiveElementVisitor
import org.jetbrains.kotlin.psi.KtCallExpression

class PluginCheckerKotlin : KotlinRecursiveElementVisitor() {

    var pitestPluginAvailable = false
    var companionPluginAvailable = false

    override fun visitCallExpression(expression: KtCallExpression) {
        val method = expression.calleeExpression?.text
        if(method.equals("plugins")) {
            expression.acceptChildren(this)
        }
        if(method.equals("id")) {
            if(expression.valueArgumentList != null) {
                for (arg in expression.valueArgumentList!!.arguments) {
                    val pluginName = arg.text
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
}
