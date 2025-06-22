package com.github.caioquirino.linuxautodarkmode.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/*
* Supports storing the application settings in a persistent way.
* The {@link com.intellij.openapi.components.State State} and {@link Storage}
* annotations define the name of the data and the filename where these persistent
* application settings are stored.
*/
@Service(Service.Level.APP)
@State(name = "com.github.caioquirino.linuxautodarkmode.settings.AppSettings", storages = [Storage("LinuxAutoDarkModePlugin.xml")])
internal class AppSettings : PersistentStateComponent<AutoDarkModeSettingsModel?> {

    var model = AutoDarkModeSettingsModel()

    override fun getState(): AutoDarkModeSettingsModel {
        return model
    }

    override fun loadState(state: AutoDarkModeSettingsModel) {
        model = state
    }

    companion object {
        val instance: AppSettings
            get() = ApplicationManager.getApplication()
                .getService<AppSettings>(AppSettings::class.java)
    }
}