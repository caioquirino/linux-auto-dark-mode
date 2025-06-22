package com.github.caioquirino.linuxautodarkmode.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import java.util.*
import javax.swing.JComponent


/**
 * Provides controller functionality for application settings.
 */
internal class AppSettingsConfigurable : Configurable {
    private var settingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "SDK: Application Settings Example"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return settingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        settingsComponent = AppSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        return settingsComponent!!.lightThemeText != state.lightTheme ||
                settingsComponent!!.darkThemeText != state.darkTheme ||
                settingsComponent!!.syncWithOSOption != state.syncWithOSOption
    }

    override fun apply() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        state.lightTheme = settingsComponent!!.lightThemeText
        state.darkTheme = settingsComponent!!.darkThemeText
        state.syncWithOSOption = settingsComponent!!.syncWithOSOption
    }

    override fun reset() {
        val state: AppSettings.State =
            Objects.requireNonNull(AppSettings.instance.state)
        settingsComponent!!.lightThemeText = state.lightTheme
        settingsComponent!!.darkThemeText = state.darkTheme
        settingsComponent!!.syncWithOSOption = state.syncWithOSOption
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}