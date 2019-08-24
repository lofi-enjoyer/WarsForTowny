package com.aurgiyalgo.WarsForTowny.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import com.aurgiyalgo.WarsForTowny.NeutralityManager;
import com.aurgiyalgo.WarsForTowny.War;
import com.aurgiyalgo.WarsForTowny.WarManager;
import com.aurgiyalgo.WarsForTowny.WarsForTowny;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class WfTCommand implements CommandExecutor {
	private WarsForTowny plugin;

	public WfTCommand(WarsForTowny aThis) {
		this.plugin = aThis;
	}

	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (strings.length == 0) {
			return WfTCommandPlugin.execute(cs, cmnd, string, strings);
		}
		String arg1 = strings[0];
		if (arg1.equals("reload")) {
			if (!cs.hasPermission("wft.admin")) {
				return true;
			}
			cs.sendMessage(ChatColor.GREEN + "Reloading plugin...");
			PluginManager pm = Bukkit.getServer().getPluginManager();
			pm.disablePlugin(this.plugin);
			pm.enablePlugin(this.plugin);
			cs.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
			return true;
		}
		switch (arg1) {
		case "help":
			return WfTCommandHelp.execute(cs, cmnd, string, strings);
		case "info":
			return WfTCommandInfo.execute(cs, cmnd, string, strings);
		case "status":
			return WfTCommandStatus.execute(cs, cmnd, string, strings);
		case "neutral":
			return WfTCommandNeutral.execute(cs, cmnd, string, strings);
		case "neutrallist":
			return WfTCommandNeutralList.execute(cs, cmnd, string, strings);
		case "astart":
			if (!cs.hasPermission("wft.astart")) {
				cs.sendMessage(ChatColor.RED + "You are not allowed to do this!");
				return true;
			}
			return declareWar(cs, strings, true);
		case "declare":
			if (!cs.hasPermission("wft.declare")) {
				cs.sendMessage(ChatColor.RED + "You are not allowed to do this!");
				return true;
			}
			return declareWar(cs, strings, false);
		case "end":
			if (!cs.hasPermission("wft.end")) {
				cs.sendMessage(ChatColor.RED + "You are not allowed to do this!");
				return true;
			}
			return declareEnd(cs, strings, false);
		case "aend":
			if (!cs.hasPermission("wft.aend")) {
				cs.sendMessage(ChatColor.RED + "You are not allowed to do this!");
				return true;
			}
			return declareEnd(cs, strings, true);
		}
		cs.sendMessage(WarsForTowny.getTranslatedMessage("command_not_found"));
		return true;
	}

	private boolean declareEnd(CommandSender cs, String[] strings, boolean admin) {
		if ((admin) && (strings.length <= 2)) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("specify_two_nations"));
			return true;
		}
		String sonat = "";
		if (admin) {
			sonat = strings[1];
		}
		Resident res = null;
		Nation nat;
		try {
			if (admin) {
				nat = TownyUniverse.getDataSource().getNation(strings[2]);
			} else {
				res = TownyUniverse.getDataSource().getResident(cs.getName());
				nat = res.getTown().getNation();
			}
		} catch (Exception ex) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("not_part_of_a_nation"));
			return true;
		}
		if (!admin) {
			War w = WarManager.getWarForNation(nat);
			if (w == null) {
				String message = WarsForTowny.getTranslatedMessage("not_at_war");
				message = message.replace("%nation%", nat.getName());
				cs.sendMessage(message);
				return true;
			}
			sonat = w.getEnemy(nat).getName();
		}
		Nation onat;
		try {
			onat = TownyUniverse.getDataSource().getNation(sonat);
		} catch (NotRegisteredException ex) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("nation_not_found"));
			return true;
		}
		if (WarManager.requestPeace(nat, onat, admin)) {
			return true;
		}
		if (admin) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("forced_peace"));
		} else {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("requested_peace"));
		}
		return true;
	}

	private boolean declareWar(CommandSender cs, String[] strings, boolean admin) {
		if ((strings.length == 2) && (admin)) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("specify_two_nations"));
			return true;
		}
		if (strings.length == 1) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("specify_a_nation"));
			return true;
		}
		String sonat = strings[1];
		Resident res;
		Nation nat;
		try {
			if (admin) {
				res = null;
				nat = TownyUniverse.getDataSource().getNation(strings[2]);
			} else {
				res = TownyUniverse.getDataSource().getResident(cs.getName());
				nat = res.getTown().getNation();
			}
		} catch (Exception ex) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("not_part_of_a_nation"));
			return true;
		}
		if (WarManager.getWarForNation(nat) != null) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("already_at_war"));
			return true;
		}
		Nation onat;
		try {
			onat = TownyUniverse.getDataSource().getNation(sonat);
		} catch (NotRegisteredException ex) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("nation_not_found"));
			return true;
		}
		if (nat.getName().equals(onat.getName())) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("war_with_itself"));
			return true;
		}
		if (NeutralityManager.getNeutrals().contains(onat)) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("nation_neutral"));
			return true;
		}
		if (NeutralityManager.getNeutrals().contains(nat)) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("nation_cannot_declare"));
			return true;
		}
		try {
			if (nat.getHoldingBalance() < WarsForTowny.getDeclareCost()) {
				cs.sendMessage(WarsForTowny.getTranslatedMessage("not-enough-money"));
				return true;
			}
			nat.pay(WarsForTowny.getDeclareCost(), "War declare!");
		} catch (EconomyException ex) {
			Logger.getLogger(WfTCommand.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (WarManager.getWarForNation(onat) != null) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("already_at_war"));
			return true;
		}
		WarManager.createWar(nat, onat, cs);
		String message = WarsForTowny.getTranslatedMessage("declared_war");
		message = message.replace("%nation%", onat.getName());
		cs.sendMessage(message);
		return true;
	}
}
