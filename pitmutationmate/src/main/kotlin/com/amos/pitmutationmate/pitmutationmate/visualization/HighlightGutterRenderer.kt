import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import javax.swing.Icon


class HighlightGutterRenderer: GutterIconRenderer() {
    val redIcon: Icon = AllIcons.Actions.Suspend
    val greenIcon: Icon = AllIcons.Actions.StartDebugger
    val yellowIcon: Icon = AllIcons.Actions.IntentionBulb
    private val toolTip = "PITest run"
    val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }
    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override fun getIcon(): Icon {
        return this.redIcon
    }


}