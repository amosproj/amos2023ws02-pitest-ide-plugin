package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement

class EditorLineMarker: LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*> {
        println("EditorLineMarker getLineMarkerInfo")
    }
}