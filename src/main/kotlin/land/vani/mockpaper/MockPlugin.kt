package land.vani.mockpaper

import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

class MockPlugin(
    loader: JavaPluginLoader,
    description: PluginDescriptionFile,
    dataFolder: File,
    file: File,
) : JavaPlugin(loader, description, dataFolder, file)
