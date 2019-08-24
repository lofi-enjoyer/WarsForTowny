package com.aurgiyalgo.WarsForTowny.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.WarsForTowny.WarsForTowny;

public class WfTCommandInfo {
	
	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8------------&6&lParameters&r&8------------\n"
				+ "&cDefense points&r\n"
				+ " - &ePer player: &r&l" + WarsForTowny.getpPlayer() + "\n"
				+ " - &ePer plot: &r&l" + WarsForTowny.getpPlot() + "\n"
				+ "&cCosts&r\n"
				+ " - &ePer death: &r&l" + WarsForTowny.getpKill() + "\n"
				+ " - &eDeclare cost: &r&l" + WarsForTowny.getDeclareCost() + "\n"
				+ " - &eEnd cost: &r&l" + WarsForTowny.getEndCost() + "\n"
				+ "&8----------------------------------"));
		return true;	}

}
