package land.vani.mockpaper.help

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.help.HelpMap
import org.bukkit.help.HelpTopic
import org.bukkit.help.HelpTopicComparator
import org.bukkit.help.HelpTopicFactory
import org.jetbrains.annotations.VisibleForTesting
import java.util.TreeMap
import kotlin.test.assertTrue

class HelpMapMock : HelpMap {
    private val topics: MutableMap<String, HelpTopic> = TreeMap(
        HelpTopicComparator.topicNameComparatorInstance()
    )
    private val factories: MutableMap<Class<*>, HelpTopicFactory<*>> = mutableMapOf()

    override fun getHelpTopic(topicName: String): HelpTopic? = topics[topicName]

    override fun getHelpTopics(): Collection<HelpTopic> =
        topics.filterKeys { it.isNotEmpty() }.values

    override fun addTopic(topic: HelpTopic) {
        topics[topic.name] = topic
    }

    override fun clear() {
        topics.clear()
    }

    override fun getIgnoredPlugins(): MutableList<String> {
        throw UnimplementedOperationException()
    }

    override fun registerHelpTopicFactory(commandClass: Class<*>, factory: HelpTopicFactory<*>) {
        if (
            !Command::class.java.isAssignableFrom(commandClass) &&
            !CommandExecutor::class.java.isAssignableFrom(commandClass)
        ) {
            throw IllegalArgumentException("CommandClass must inherit from types Command or CommandExecutor")
        }
        factories[commandClass] = factory
    }

    /**
     * Asserts that [factory] is registered.
     */
    @VisibleForTesting
    fun assertRegistered(factory: HelpTopicFactory<*>) {
        assertTrue(factory in factories.values)
    }
}
