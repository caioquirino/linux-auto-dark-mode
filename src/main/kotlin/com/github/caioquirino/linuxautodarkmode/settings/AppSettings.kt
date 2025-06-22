package com.github.caioquirino.linuxautodarkmode.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.NonNls


/*
* Supports storing the application settings in a persistent way.
* The {@link com.intellij.openapi.components.State State} and {@link Storage}
* annotations define the name of the data and the filename where these persistent
* application settings are stored.
*/
@State(name = "com.github.caioquirino.linuxautodarkmode.settings.AppSettings", storages = [Storage("LinuxAutoDarkModePlugin.xml")])
internal class AppSettings

    : PersistentStateComponent<AppSettings.State?> {
    internal class State {
        @NonNls
        var lightTheme: String = "Light"
        var darkTheme: String = "Dark"
        var syncWithOSOption: Boolean = true
    }

    private var appState = State()

    override fun getState(): State {
        return appState
    }

    override fun loadState(state: State) {
        appState = state
    }

    companion object {
        val instance: AppSettings
            get() = ApplicationManager.getApplication()
                .getService<AppSettings>(AppSettings::class.java)
    }
}