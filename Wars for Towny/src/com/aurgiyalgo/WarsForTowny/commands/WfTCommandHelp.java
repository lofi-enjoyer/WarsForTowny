package com.aurgiyalgo.WarsForTowny.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WfTCommandHelp {
	
	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8------------&6&l&nWars for Towny&r&8------------\n"
				+ "&e/wft&r &8- &7Shows plugin info\n"
				+ "&e/wft help&r &8- &7Shows help\n"
				+ "&e/wft info&r &8- &7Shows the parameters for wars\n"
				+ "&e/wft status&r &8- &7List the on-going wars\n"
				+ "&e/wft status [nation]&r &8- &cList&7 of the nation's towns and defense points\n"
				+ "&e/wft declare [nation]&r &8- &cStarts a war&7 with another nation\n"
				+ "&e/wft end&r &8- &cRequest&7 to enemy to end the ongoing war\n"
				+ "&e/wft neutral&r &8- &7Toggle the &cneutrality&7 of your nation\n"
				+ "&e/wft astart [nation] [nation]&r &8- &7Force two nations to war\n"
				+ "&e/wft aend [nation] [nation]&r &8- &7Force a nation to stop a war\n"
				+ "&e/wft neutral [nation] &r &8- &7Toggle a nation's neutrality\n"
				+ "&e/wft reload&r &8- &cReloads&7 the plugin\n"
				+ "&8--------------------------------------"));
		return true;
	}

}
