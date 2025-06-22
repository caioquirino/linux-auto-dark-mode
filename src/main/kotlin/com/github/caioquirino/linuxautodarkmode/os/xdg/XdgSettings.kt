package com.github.caioquirino.linuxautodarkmode.os.xdg


import com.github.caioquirino.linuxautodarkmode.Theme
import org.freedesktop.dbus.annotations.DBusInterfaceName
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder
import org.freedesktop.dbus.interfaces.DBusInterface
import org.freedesktop.dbus.interfaces.DBusSigHandler
import org.freedesktop.dbus.messages.DBusSignal
import org.freedesktop.dbus.types.UInt32
import org.freedesktop.dbus.types.Variant

const val APPEARANCE_NAMESPACE = "org.freedesktop.appearance"
const val COLOR_SCHEME_KEY = "color-scheme"

@DBusInterfaceName("org.freedesktop.portal.Settings")
interface SettingsInterface : DBusInterface {

    fun Read(namespace: String, key: String): Variant<*>

    class SettingChanged(objectpath: String, namespace: String, key: String, value: Variant<Any>) :
        DBusSignal(objectpath, namespace, key, value) {
        val colorSchemeChanged: Boolean =
            namespace == APPEARANCE_NAMESPACE && key == COLOR_SCHEME_KEY
        val rawValue: Variant<Any> = value
        val theme: Theme? = if(colorSchemeChanged) {
            mapVariantToTheme(rawValue)
        } else {
            null
        }
    }
}

private class SigHandler : DBusSigHandler<SettingsInterface.SettingChanged> {
    var eventHandler: ((theme: Theme) -> Unit)? = null
    override fun handle(signal: SettingsInterface.SettingChanged) {
        if (signal.colorSchemeChanged) {
            signal.theme?.let { eventHandler?.invoke(it) }
        }
    }
}

class XdgSettings(
    private val connection: DBusConnection = DBusConnectionBuilder.forSessionBus().build(),
) {
    private val settingsInterface: SettingsInterface = connection.getRemoteObject(
        "org.freedesktop.portal.Desktop",
        "/org/freedesktop/portal/desktop",
        SettingsInterface::class.java
    )

    private val sigHandler: SigHandler = SigHandler()

    val theme: Theme
        get() {
            val themeVariant = settingsInterface.runCatching {
                    Read(
                        APPEARANCE_NAMESPACE,
                        COLOR_SCHEME_KEY
                    )
            }.getOrElse { return Theme.ERROR }

            return mapVariantToTheme(themeVariant)
        }

    fun startListening(callback: (theme: Theme) -> Unit) {
        sigHandler.eventHandler = callback
        connection.addSigHandler(SettingsInterface.SettingChanged::class.java, sigHandler)
    }

    fun stopListening() = {
        connection.removeSigHandler(SettingsInterface.SettingChanged::class.java, sigHandler)
        sigHandler.eventHandler = null
    }

    fun disconnect() = connection.disconnect()

    companion object {
        val instance: XdgSettings
            get() = XdgSettings()
    }
}

private fun mapVariantToTheme(variant: Variant<*>): Theme {
    val value = recursiveVariantValue<UInt32>(variant).toInt()
    return when (value) {
        1 -> Theme.DARK
        else -> Theme.LIGHT
    }
}


@Suppress("UNCHECKED_CAST")
private fun <T> recursiveVariantValue(variant: Variant<*>): T {
    val value = variant.value
    return (if (value !is Variant<*>) value else recursiveVariantValue<T>(value)) as T
}