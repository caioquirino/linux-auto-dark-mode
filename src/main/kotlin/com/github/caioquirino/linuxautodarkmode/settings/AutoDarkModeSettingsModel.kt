package com.github.caioquirino.linuxautodarkmode.settings

// If you have a data class for the model, add a custom equals method or comparison function:
data class AutoDarkModeSettingsModel(
    var lightTheme: String? = null,
    var darkTheme: String? = null,
    var lightColorScheme: String? = null,
    var darkColorScheme: String? = null,
    var syncWithOS: Boolean = true,
    var showNotifications: Boolean = true
)