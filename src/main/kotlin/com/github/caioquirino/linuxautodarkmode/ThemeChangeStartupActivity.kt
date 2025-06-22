package com.github.caioquirino.linuxautodarkmode

import com.github.caioquirino.linuxautodarkmode.intellij.ThemeManager
import com.github.caioquirino.linuxautodarkmode.os.xdg.XdgSettings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.application.ApplicationManager
import com.github.caioquirino.linuxautodarkmode.settings.AppSettings

@Service
class ThemeListenerService : Disposable {
    private val xdgSettings = XdgSettings.instance
    private var lastTheme: Theme? = null

    init {
        xdgSettings.startListening { theme: Theme ->
            if (theme != lastTheme) {
                val appSettings = AppSettings.instance.state
                if (appSettings.syncWithOS) {
                    update(theme)
                }
                lastTheme = theme
            }
        }
    }

    private fun update(theme: Theme) {
        val settings = AppSettings.instance.state
        // Change IDE theme
        when (theme) {
            Theme.LIGHT -> {
                settings.lightTheme?.let { ThemeManager.instance.setCurrentTheme(it) }
                settings.lightColorScheme?.let { ThemeManager.instance.setCurrentColorScheme(it) }
            }
            Theme.DARK -> {
                settings.darkTheme?.let { ThemeManager.instance.setCurrentTheme(it) }
                settings.darkColorScheme?.let { ThemeManager.instance.setCurrentColorScheme(it) }
            }
            else -> null
        }

        // Send notification
        if(settings.showNotifications) {
            val message = when (theme) {
                Theme.LIGHT -> "System theme changed to LIGHT"
                Theme.DARK -> "System theme changed to DARK"
                Theme.ERROR -> "Error detecting system theme"
            }
            NotificationGroupManager.getInstance()
                .getNotificationGroup("LinuxAutoDarkMode")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(null)
        }
    }

    override fun dispose() {
        xdgSettings.stopListening()
        xdgSettings.disconnect()
    }
}

class ThemeChangeStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().getService(ThemeListenerService::class.java)
    }
} 