package com.aurgiyalgo.WarsForTowny.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.WarsForTowny.War;
import com.aurgiyalgo.WarsForTowny.WarManager;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class WfTCommandStatus {
	
	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (strings.length == 1) {
			if (WarManager.getWars().size() <= 0) {
				cs.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Error!" + ChatColor.RESET + "" + ChatColor.RED + " There are no on-going wars");
				return true;
			}
			cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8--------&c&lOn-going wars&r&8--------"));
			for (War war : WarManager.getWars()) {
				Nation first = null;
				Nation second = null;
				for (Nation st : war.getNations()) {
					if (first == null) {
						first = st;
					} else {
						second = st;
					}
				}
				cs.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + first.getName() + ChatColor.GRAY + " (" + war.getNationPoints(first) + ")" + ChatColor.DARK_GRAY + " VS " + ChatColor.YELLOW + second.getName() + ChatColor.GRAY + " ("
						+ war.getNationPoints(second) + ")");
			}
			return true;
		}
		String onation = strings[1];
		Nation t;
		try {
			t = TownyUniverse.getDataSource().getNation(onation);
		} catch (NotRegisteredException ex) {
			cs.sendMessage(ChatColor.GOLD + "No nation called " + onation + " could be found!");
			return true;
		}
		War w;
		w = null;
		try {
			w = WarManager.getWarForNation(TownyUniverse.getDataSource().getNation(onation));
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		if (w == null) {
			cs.sendMessage(ChatColor.RED + "That nation isn't in a war!");
			return true;
		}
		cs.sendMessage(t.getName() + " war info:");
		for (Town tt : t.getTowns()) {
			cs.sendMessage(ChatColor.GREEN + tt.getName() + ": " + w.getTownPoints(tt) + " points");
		}
		return true;
	}

}
