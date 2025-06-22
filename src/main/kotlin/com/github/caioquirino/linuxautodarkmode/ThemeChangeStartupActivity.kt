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

@Service
class ThemeListenerService : Disposable {
    private val connection = DBusConnectionBuilder.forSessionBus().build()
    private val settings = Settings(connection)

    init {
        notify(settings.theme)
        settings.startListening {
            theme: Theme -> notify(theme)
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