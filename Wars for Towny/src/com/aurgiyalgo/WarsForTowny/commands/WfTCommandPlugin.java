package com.aurgiyalgo.WarsForTowny.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.WarsForTowny.WarsForTowny;

public class WfTCommandPlugin {

	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8--------&6&lWars for Towny&r&8--------\n"
		+ "&eVersion &8- &f&l" + WarsForTowny.getInstance().getDescription().getVersion() + "\n"
		+ "&eDeveloper &8- &f&lJoselu\n"
		+ "&cFor help &4/wft help\n\n"
		+ "&8------------------------------"));
		return true;
	}
}
