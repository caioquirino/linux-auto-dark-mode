package com.github.caioquirino.linuxautodarkmode

import com.github.caioquirino.linuxautodarkmode.os.Theme
import com.github.caioquirino.linuxautodarkmode.os.xdg.Settings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.ui.Messages
import com.github.caioquirino.linuxautodarkmode.settings.AppSettings
import javax.swing.UIManager

@Service
class ThemeListenerService : Disposable {
    private val connection = DBusConnectionBuilder.forSessionBus().build()
    private val settings = Settings(connection)
    private var lastTheme: Theme? = null

    init {
        settings.startListening { theme: Theme ->
            if (theme != lastTheme) {
                val appSettings = AppSettings.instance.state
                if (appSettings.syncWithOSOption) {
                    notify(theme)
                }
                lastTheme = theme
            }
        }
    }

    private fun notify(theme: Theme) {
        val message = when (theme) {
            Theme.DARK -> "System theme changed to DARK"
            Theme.LIGHT -> "System theme changed to LIGHT"
            Theme.ERROR -> "Error detecting system theme"
        }
        NotificationGroupManager.getInstance()
            .getNotificationGroup("LinuxAutoDarkMode")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(null)

        // Change IDE theme
        val lafManager = LafManager.getInstance()
        val appSettings = AppSettings.instance.state
        val targetThemeName = when (theme) {
            Theme.DARK -> appSettings.darkTheme
            Theme.LIGHT -> appSettings.lightTheme
            else -> null
        }
        if (targetThemeName != null) {
            val laf = lafManager.installedLookAndFeels.firstOrNull { it.name == targetThemeName }
            if (laf != null && lafManager.currentLookAndFeel?.name != laf.name) {
                ApplicationManager.getApplication().invokeLater {
                    lafManager.setCurrentLookAndFeel(laf)
                    lafManager.updateUI()
                }
            }
        }
    }

    override fun dispose() {
        settings.stopListening()
        connection.disconnect()
    }
}

class ThemeChangeStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().getService(ThemeListenerService::class.java)
    }
} 