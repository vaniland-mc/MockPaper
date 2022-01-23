package land.vani.mockpaper.plugin

import land.vani.mockpaper.MockPlugin
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.command.PluginCommand
import org.bukkit.command.createPluginCommand
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
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import org.bukkit.plugin.java.isEnabledPublic
import java.io.File
import java.io.FileNotFoundException
import java.lang.reflect.Constructor
import java.util.logging.Level
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.test.fail

class PluginManagerMock(private val server: ServerMock) : PluginManager {
    private val plugins: MutableList<Plugin> = mutableListOf()
    private val events: MutableList<Event> = mutableListOf()
    private val permissions: MutableList<Permission> = mutableListOf()
    private val permissionSubscriptions: MutableMap<Permissible, MutableSet<String>> = mutableMapOf()

    private val listeners: MutableMap<String, MutableList<Listener>> = mutableMapOf()

    @Suppress("DEPRECATION")
    private val loader = JavaPluginLoader(server)

    private val _commands: MutableList<PluginCommand> = mutableListOf()
    val command: List<PluginCommand>
        get() = _commands.asUnmodifiable()

    /**
     * Asserts that at least one event conforms to the given [predicate].
     */
    @JvmOverloads
    fun assertEventFired(message: String = "Event assert failed", predicate: (Event) -> Boolean) {
        if (events.any(predicate)) return
        fail(message)
    }

    /**
     * Asserts that at least one event of a certain [clazz] for which the [predicate] is true.
     */
    @JvmOverloads
    fun <T : Event> assertEventFired(
        clazz: Class<T>,
        predicate: (T) -> Boolean = { true },
    ) = assertEventFired(
        "No event of that type has been fired"
    ) {
        clazz.isInstance(it) && predicate(clazz.cast(it))
    }

    /**
     * Asserts that at least one event of a certain [T] for which the predicate is true.
     */
    @JvmName("assertEventFiredInline")
    inline fun <reified T : Event> assertEventFired(
        noinline predicate: (T) -> Boolean = { true },
    ) = assertEventFired(T::class.java, predicate)

    /**
     * Asserts that at none event of a certain [T] for which the predicate is true.
     */
    @JvmOverloads
    fun <T : Event> assertEventNotFired(
        clazz: Class<T>,
        predicate: (T) -> Boolean = { true },
    ) {
        assertEventFired(clazz) { !predicate(it) }
    }

    /**
     * Asserts that at none event of a certain [T] for which the predicate is true.
     */
    inline fun <reified T : Event> assertEventNotFired(
        noinline predicate: (T) -> Boolean = { true },
    ) {
        assertEventNotFired(T::class.java, predicate)
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

    private fun isConstructorCompatible(
        constructor: Constructor<*>,
        types: Array<Class<*>>,
    ): Boolean {
        val parameters = constructor.parameterTypes
        for (i in types.indices) {
            val type = types[i]
            val parameter = parameters[i]
            if (i < 4) {
                if (type != parameter) return false
            } else if (!parameter.isAssignableFrom(type)) {
                return false
            }
        }
        return true
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : JavaPlugin> getCompatibleConstructor(
        clazz: Class<T>,
        types: Array<Class<*>>,
    ): Constructor<T> = clazz.declaredConstructors.find { constructor ->
        val parameters = constructor.parameterTypes
        parameters.size == types.size && isConstructorCompatible(constructor, types)
    } as Constructor<T>

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    private fun addSection(command: PluginCommand, name: String, value: Any?) {
        when (name) {
            "description" -> command.description = value as String
            "aliases" -> {
                if (value is List<*>) {
                    command.aliases = value as List<String>
                } else if (value != null) {
                    command.aliases = listOf(value.toString())
                } else {
                    command.aliases = listOf()
                }
            }
            "permission" -> command.permission = value as String?
            "permission-message" -> command.permissionMessage = value as String?
            "usage" -> command.usage = value as String
            else -> throw UnsupportedOperationException("Unknown section: $value")
        }
    }

    private fun addCommandsFrom(plugin: Plugin) {
        plugin.description.commands.forEach { (label, sections) ->
            val command = createPluginCommand(label, plugin)
            sections.forEach { (name, value) ->
                addSection(command, name, value)
            }
            _commands += command
            server.commandMap.register(plugin.name, command)
        }
    }

    private fun registerLoadedPlugin(plugin: Plugin) {
        addCommandsFrom(plugin)
        plugins += plugin
        plugin.onLoad()
    }

    private val pluginConstructorTypes = listOf(
        JavaPluginLoader::class.java,
        PluginDescriptionFile::class.java,
        File::class.java,
        File::class.java
    )

    /**
     * Load a plugin from [clazz] with [description].
     */
    @JvmOverloads
    fun <T : JavaPlugin> loadPlugin(
        clazz: Class<T>,
        description: PluginDescriptionFile = findPluginDescription(clazz),
        parameters: Array<Any> = arrayOf(),
    ): T {
        val types = pluginConstructorTypes + parameters

        @Suppress("UNCHECKED_CAST")
        val constructor = getCompatibleConstructor(
            clazz,
            (types as List<Class<*>>).toTypedArray()
        ).apply {
            isAccessible = true
        }

        val arguments = arrayOf(
            loader,
            description,
            createTempDirectory(
                "MockPaper-${description.name}-${description.version}"
            ).toFile(),
            createTempFile(
                "MockPaper-${description.name}-${description.version}", ".jar"
            ).toFile(),
        )
        System.arraycopy(parameters, 0, arguments, 4, parameters.size)

        val plugin = constructor.newInstance(*arguments)
        registerLoadedPlugin(plugin)

        enablePlugin(plugin)

        return plugin
    }

    /**
     * Load a plugin [T] with [description].
     */
    @JvmOverloads
    inline fun <reified T : JavaPlugin> loadPlugin(
        description: PluginDescriptionFile = findPluginDescription(T::class.java),
        parameters: Array<Any> = arrayOf(),
    ): T = loadPlugin(T::class.java, description, parameters)

    /**
     * Load a plugin [T] with [description].
     */
    fun <T : JavaPlugin> loadPlugin(
        clazz: Class<T>,
        description: String,
        parameters: Array<Any> = arrayOf(),
    ): T = loadPlugin(clazz, PluginDescriptionFile(description.reader()), parameters)

    /**
     * Load a plugin [T] with [description].
     */
    inline fun <reified T : JavaPlugin> loadPlugin(
        description: String,
        parameters: Array<Any> = arrayOf(),
    ): T = loadPlugin(T::class.java, description, parameters)

    @PublishedApi
    internal fun findPluginDescription(clazz: Class<out JavaPlugin>): PluginDescriptionFile {
        val resources = clazz.classLoader.getResources("plugin.yml")
        return resources.toList()
            .map { PluginDescriptionFile(it.openStream()) }
            .find { it.main == clazz.name }
            ?: throw FileNotFoundException(
                "Could not find file 'plugin.yml'. Maybe forgot to add the 'main' property?"
            )
    }

    /**
     * Load a [MockPlugin] with [pluginName] for mocking.
     */
    @JvmOverloads
    fun createMockPlugin(pluginName: String = "MockPlugin"): MockPlugin = loadPlugin(
        PluginDescriptionFile(
            pluginName,
            "1.0.0",
            MockPlugin::class.java.name,
        )
    )

    /**
     * Load a plugin [T].
     */
    @JvmOverloads
    fun <T : JavaPlugin> loadSimple(clazz: Class<T>, parameters: Array<Any> = arrayOf()): T {
        val description = PluginDescriptionFile(
            clazz.name,
            "1.0.0",
            clazz.canonicalName
        )
        return loadPlugin(clazz, description, parameters)
    }

    /**
     * Load a plugin [T].
     */
    inline fun <reified T : JavaPlugin> loadSimple(parameters: Array<Any> = arrayOf()): T =
        loadSimple(T::class.java, parameters)
}
