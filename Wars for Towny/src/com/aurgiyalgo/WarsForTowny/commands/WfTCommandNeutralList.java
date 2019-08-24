package com.aurgiyalgo.WarsForTowny.commands;

import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.WarsForTowny.NeutralityManager;
import com.palmergames.bukkit.towny.object.Nation;

import net.md_5.bungee.api.ChatColor;

public class WfTCommandNeutralList {
	
	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (NeutralityManager.getNeutrals().size() <= 0) {
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError!&r &cThere are no neutral nations"));
			return true;
		}
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8--------&a&lNeutral nations&r&8--------"));
		for (Iterator<Nation> i = NeutralityManager.getNeutrals().iterator(); i.hasNext();) {
			cs.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + i.next().getName());
		}
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8-------------------------------"));
		return true;
	}

}
