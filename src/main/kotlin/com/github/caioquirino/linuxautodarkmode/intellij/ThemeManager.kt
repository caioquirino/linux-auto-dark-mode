package com.github.caioquirino.linuxautodarkmode.intellij

import com.github.caioquirino.linuxautodarkmode.Theme
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.ThemeListProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager

class ThemeManager {
    private val lafManager = LafManager.getInstance()
    private val colorManager = EditorColorsManager.getInstance()

    fun allColorSchemes(): List<String> {
        return colorManager.allSchemes.map { it.displayName }
    }

    fun allThemes(theme: Theme): List<String> {
        val filterDark = theme == Theme.DARK
        return ThemeListProvider.getInstance().getShownThemes().items
            .filter { it.isDark == filterDark }.map { it.name }
    }

    fun setCurrentTheme(themeName: String) {
        ApplicationManager.getApplication().invokeLater {
            lafManager.setCurrentLookAndFeel(lafManager.installedLookAndFeels.first { it.name == themeName })
            lafManager.updateUI()
        }
    }

    fun setCurrentColorScheme(colorScheme: String) {
        ApplicationManager.getApplication().invokeLater {
            colorManager.setGlobalScheme(colorManager.allSchemes.first { it.displayName == colorScheme })
        }
    }




    companion object {
        val instance: ThemeManager
            get() = ThemeManager()
    }
}