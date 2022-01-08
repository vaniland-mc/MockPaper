package land.vani.mockpaper.plugin

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.permissions.Permissible
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.AuthorNagException
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.isEnabledPublic
import java.io.File
import java.util.logging.Level
import kotlin.test.fail

class PluginManagerMock(private val server: ServerMock) : PluginManager {
    private val plugins: MutableList<Plugin> = mutableListOf()
    private val events: MutableList<Event> = mutableListOf()
    private val permissions: MutableList<Permission> = mutableListOf()
    private val permissionSubscriptions: MutableMap<Permissible, MutableSet<String>> = mutableMapOf()

    private val listeners: MutableMap<String, MutableList<Listener>> = mutableMapOf()

    @JvmOverloads
    fun assertEventFired(message: String = "Event assert failed", predicate: (Event) -> Boolean) {
        if (events.any(predicate)) return
        fail(message)
    }

    fun assertEventFired(clazz: Class<out Event>) {
        assertEventFired("No event of that type has been fired", clazz::isInstance)
    }

    override fun registerInterface(loader: Class<out PluginLoader>) {
        throw UnimplementedOperationException()
    }

    override fun getPlugin(name: String): Plugin? = plugins.find { it.name == name }

    override fun getPlugins(): Array<Plugin> = plugins.toTypedArray()

    override fun isPluginEnabled(name: String): Boolean = plugins.any { it.name == name && it.isEnabled }

    override fun isPluginEnabled(plugin: Plugin?): Boolean = plugins.any { it == plugin && plugin.isEnabled }

    override fun loadPlugin(file: File): Plugin? {
        throw UnimplementedOperationException()
    }

    override fun loadPlugins(directory: File): Array<Plugin> {
        throw UnimplementedOperationException()
    }

    override fun disablePlugins() {
        plugins.forEach {
            disablePlugin(it)
        }
    }

    override fun clearPlugins() {
        disablePlugins()
        plugins.clear()
    }

    /**
     * This method clears the history of [Event]. Doing that can be very useful if you want to assert fresh
     * events using [assertEventFired] or similar.
     */
    fun clearEvents() {
        events.clear()
    }

    override fun callEvent(event: Event) {
        if (event.isAsynchronous && server.isOnMainThread) {
            throw IllegalStateException("Asynchronous event cannot be called on the main thread.")
        }

        events += event
        event.handlers
            .registeredListeners
            .filter { it.plugin.isEnabled }
            .forEach {
                try {
                    it.callEvent(event)
                } catch (ex: AuthorNagException) {
                    it.plugin.isNaggable = true
                    server.logger.log(
                        Level.SEVERE,
                        "Nag author(s): %s of '%s' about the following: %s".format(
                            it.plugin.description.authors,
                            it.plugin.description.fullName,
                            ex.message
                        )
                    )
                } catch (ex: Exception) {
                    server.logger.log(
                        Level.SEVERE,
                        "Could not pass event ${event.eventName} to ${it.plugin.description.fullName}",
                        ex
                    )
                }
            }
    }

    override fun registerEvents(listener: Listener, plugin: Plugin) {
        if (!plugin.isEnabled) {
            throw IllegalPluginAccessException("Plugin attempted to register $listener while not enabled")
        }
        listeners.getOrPut(plugin.name) { mutableListOf() }.add(listener)

        plugin.pluginLoader.createRegisteredListeners(listener, plugin)
            .forEach { (event, registeredListeners) ->
                getEventListeners(event).registerAll(registeredListeners)
            }
    }

    override fun registerEvent(
        event: Class<out Event>,
        listener: Listener,
        priority: EventPriority,
        executor: EventExecutor,
        plugin: Plugin,
    ) = registerEvent(event, listener, priority, executor, plugin, false)

    override fun registerEvent(
        event: Class<out Event>,
        listener: Listener,
        priority: EventPriority,
        executor: EventExecutor,
        plugin: Plugin,
        ignoreCancelled: Boolean,
    ) {
        if (!plugin.isEnabled) {
            throw IllegalPluginAccessException("Plugin attempted to register $event while not enabled")
        }
        listeners.getOrPut(plugin.name) { mutableListOf() }
            .apply { add(listener) }
        getEventListeners(event).register(RegisteredListener(listener, executor, priority, plugin, ignoreCancelled))
    }

    private fun getEventListeners(type: Class<out Event>): HandlerList {
        val method = getRegistrationClass(type).getDeclaredMethod("getHandlerList").apply {
            isAccessible = true
        }
        try {
            return method.invoke(null) as HandlerList
        } catch (ex: Exception) {
            throw IllegalPluginAccessException(ex.message).apply {
                addSuppressed(ex)
            }
        }
    }

    private fun getRegistrationClass(clazz: Class<out Event>): Class<out Event> {
        try {
            clazz.getDeclaredMethod("getHandlerList")
            return clazz
        } catch (ex: NoSuchMethodException) {
            if (
                clazz.superclass != null &&
                clazz.superclass != Event::class.java &&
                Event::class.java.isAssignableFrom(clazz.superclass)
            ) {
                return getRegistrationClass(clazz.superclass.asSubclass(Event::class.java))
            } else {
                throw IllegalPluginAccessException(
                    "Unable to find handler list for event ${clazz.name}. Static getHandlerList method required!"
                ).apply {
                    addSuppressed(ex)
                }
            }
        }
    }

    override fun enablePlugin(plugin: Plugin) {
        if (plugin is JavaPlugin) {
            if (!plugin.isEnabled) {
                plugin.isEnabledPublic = true
                callEvent(PluginEnableEvent(plugin))
            }
        } else {
            throw IllegalArgumentException("Not a JavaPlugin")
        }
    }

    override fun disablePlugin(plugin: Plugin) {
        if (plugin is JavaPlugin) {
            if (plugin.isEnabled) {
                listeners[plugin.name]?.flatMap {
                    plugin.pluginLoader.createRegisteredListeners(it, plugin).entries
                }?.forEach { (event, _) ->
                    getEventListeners(getRegistrationClass(event)).unregister(plugin)
                }

                plugin.isEnabledPublic = false
                callEvent(PluginDisableEvent(plugin))
            }
        } else {
            throw IllegalArgumentException("Not a JavaPlugin")
        }
    }

    override fun getPermission(name: String): Permission? =
        permissions.find { it.name == name }

    override fun addPermission(perm: Permission) {
        permissions += perm
    }

    override fun removePermission(perm: Permission) {
        permissions -= perm
    }

    override fun removePermission(name: String) {
        throw UnimplementedOperationException()
    }

    override fun getDefaultPermissions(op: Boolean): Set<Permission> =
        permissions.filter {
            it.default == PermissionDefault.TRUE ||
                (op && it.default == PermissionDefault.OP)
        }.toSet()

    override fun recalculatePermissionDefaults(perm: Permission) {
        // Nothing to do
    }

    override fun subscribeToPermission(permission: String, permissible: Permissible) {
        getPermissionSubscriptions(permissible) += permission
    }

    override fun unsubscribeFromPermission(permission: String, permissible: Permissible) {
        getPermissionSubscriptions(permissible) -= permission
    }

    private fun getPermissionSubscriptions(permissible: Permissible): MutableSet<String> =
        permissionSubscriptions.getOrPut(permissible) { mutableSetOf() }

    override fun getPermissionSubscriptions(permission: String): Set<Permissible> =
        permissionSubscriptions.filterValues { permission in it }.keys

    override fun subscribeToDefaultPerms(op: Boolean, permissible: Permissible) {
        throw UnimplementedOperationException()
    }

    override fun unsubscribeFromDefaultPerms(op: Boolean, permissible: Permissible) {
        throw UnimplementedOperationException()
    }

    override fun getDefaultPermSubscriptions(op: Boolean): MutableSet<Permissible> {
        throw UnimplementedOperationException()
    }

    override fun getPermissions(): Set<Permission> = permissions.toSet()

    override fun useTimings(): Boolean = false
}
