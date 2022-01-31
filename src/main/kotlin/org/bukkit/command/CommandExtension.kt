package org.bukkit.command

import org.bukkit.plugin.Plugin

fun createPluginCommand(name: String, owner: Plugin): PluginCommand =
    PluginCommand(name, owner)
