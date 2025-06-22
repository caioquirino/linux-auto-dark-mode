package com.github.caioquirino.linuxautodarkmode.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel


/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */
class AppSettingsComponent {
    val panel: JPanel?
    private val lightTheme = JBTextField()
    private val darkTheme = JBTextField()
    private val syncWithOS = JBCheckBox("Sync with OS")

    // FIXME: Use ThemeListProvider stuff

    init {
        this.panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Light theme:"), lightTheme, 1, false)
            .addLabeledComponent(JBLabel("Dark theme:"), darkTheme, 1, false)
            .addComponent(syncWithOS, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = lightTheme

    var lightThemeText: String
        get() = lightTheme.getText()
        set(newText) {
            lightTheme.setText(newText)
        }

    var darkThemeText: String
        get() = darkTheme.getText()
        set(newText) {
            darkTheme.setText(newText)
        }

    var syncWithOSOption: Boolean
        get() = syncWithOS.isSelected()
        set(newStatus) {
            syncWithOS.setSelected(newStatus)
        }
}