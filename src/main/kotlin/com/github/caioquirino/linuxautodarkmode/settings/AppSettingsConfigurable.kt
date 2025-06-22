package com.github.caioquirino.linuxautodarkmode.settings

import com.github.caioquirino.linuxautodarkmode.Theme
import com.github.caioquirino.linuxautodarkmode.intellij.ThemeManager
import com.github.caioquirino.linuxautodarkmode.os.xdg.XdgSettings
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel

/**
 * Provides controller functionality for application settings.
 */
internal class AppSettingsConfigurable : BoundConfigurable("Linux Auto Dark-Mode") {
    private var settings = AppSettings.instance.model
    private val xdgSettings = XdgSettings.instance



    override fun createPanel(): DialogPanel {

        val lightThemes = ThemeManager.instance.allThemes(Theme.LIGHT)
        val darkThemes = ThemeManager.instance.allThemes(Theme.DARK)

        val colorSchemeOptions = ThemeManager.instance.allColorSchemes()

        return panel {
            row("Light theme:") {
                comboBox(lightThemes)
                    .bindItem(settings::lightTheme)
            }
            row("Light editor color scheme:") {
                comboBox(colorSchemeOptions)
                    .bindItem(settings::lightColorScheme)
            }
            row("Dark theme:") {
                comboBox(darkThemes)
                    .bindItem(settings::darkTheme)
            }
            row("Dark editor color scheme:") {
                comboBox(colorSchemeOptions)
                    .bindItem(settings::darkColorScheme)
            }
            row {
                checkBox("Sync with OS")
                    .bindSelected(settings::syncWithOS)
            }
            row {
                checkBox("Show notifications")
                    .bindSelected(settings::showNotifications)
            }
        }
    }

    override fun apply() {
        super.apply()
        when(this.xdgSettings.theme) {
            Theme.ERROR -> return
            Theme.LIGHT -> {
                settings.lightTheme?.let { ThemeManager.instance.setCurrentTheme(it) }
                settings.lightColorScheme?.let { ThemeManager.instance.setCurrentColorScheme(it) }
            }
            Theme.DARK -> {
                settings.darkTheme?.let { ThemeManager.instance.setCurrentTheme(it) }
                settings.darkColorScheme?.let { ThemeManager.instance.setCurrentColorScheme(it) }
            }
        }
    }

    override fun disposeUIResources() {}
}
