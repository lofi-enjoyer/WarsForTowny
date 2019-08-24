package com.aurgiyalgo.WarsForTowny.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.WarsForTowny.NeutralityManager;
import com.aurgiyalgo.WarsForTowny.WarManager;
import com.aurgiyalgo.WarsForTowny.WarsForTowny;
import com.aurgiyalgo.WarsForTowny.utils.TWarMessageHandler;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class WfTCommandNeutral {

	public static boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (!cs.hasPermission("wft.neutral") && !cs.hasPermission("wft.neutral.others")) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("no_permission"));
			return true;
		}
		if (strings.length >= 2 && cs.hasPermission("wft.neutral.others")) {
			String onation = strings[1];
			Nation n;
			try {
				n = TownyUniverse.getDataSource().getNation(onation);
			} catch (NotRegisteredException ex) {
				String message = WarsForTowny.getTranslatedMessage("nation_not_found");
				message = message.replace("%nation%", onation);
				cs.sendMessage(message);
				return true;
			}
			if (WarManager.getWarForNation(n) != null) {
				cs.sendMessage(WarsForTowny.getTranslatedMessage("at_war_cannot_be_neutral"));
				return true;
			}
			if (!NeutralityManager.getNeutrals().contains(n)) {
				NeutralityManager.getNeutrals().add(n);
				String message = WarsForTowny.getTranslatedMessage("nation_now_neutral");
				message = message.replace("%nation%", n.getName());
				cs.sendMessage(message);
				for (Resident re : n.getResidents()) {
					Player plr = Bukkit.getPlayer(re.getName());
					if (plr != null) {
						TWarMessageHandler.sendMessage(plr, WarsForTowny.getTranslatedMessage("now_neutral"));
					}
				}
			} else {
				NeutralityManager.getNeutrals().remove(n);
				String message = WarsForTowny.getTranslatedMessage("nation_no_longer_neutral");
				message = message.replace("%nation%", n.getName());
				cs.sendMessage(message);
				for (Resident re : n.getResidents()) {
					Player plr = Bukkit.getPlayer(re.getName());
					if (plr != null) {
						TWarMessageHandler.sendMessage(plr, WarsForTowny.getTranslatedMessage("no_longer_neutral"));
					}
				}
			}
			return true;
		}
		Nation csNat;
		try {
			Town csTown = TownyUniverse.getDataSource().getResident(cs.getName()).getTown();
			csNat = TownyUniverse.getDataSource().getTown(csTown.toString()).getNation();
		} catch (NotRegisteredException ex) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("not_part_of_a_nation"));
			Logger.getLogger(WfTCommand.class.getName()).log(Level.SEVERE, null, ex);
			return true;
		}
		if (strings.length < 2) {
			Nation n;
			n = csNat;
			if (WarManager.getWarForNation(n) != null) {
				cs.sendMessage(WarsForTowny.getTranslatedMessage("at_war_cannot_be_neutral"));
				return true;
			}
			if (!NeutralityManager.getNeutrals().contains(n)) {
				NeutralityManager.getNeutrals().add(n);
				try {
					if (n.getHoldingBalance() < WarsForTowny.getNeutralityCost()*n.getResidents().size()) return true;
					n.pay(WarsForTowny.getNeutralityCost(), WarsForTowny.getTranslatedMessage("neutrality_cost"));
				} catch (EconomyException e) {
				}
				cs.sendMessage(WarsForTowny.getTranslatedMessage("now_neutral"));
				for (Resident re : n.getResidents()) {
					Player plr = Bukkit.getPlayer(re.getName());
					if (plr != null) {
						TWarMessageHandler.sendMessage(plr, WarsForTowny.getTranslatedMessage("now_neutral"));
					}
				}
			} else {
				NeutralityManager.getNeutrals().remove(n);
				cs.sendMessage(WarsForTowny.getTranslatedMessage("no_longer_neutral"));
				for (Resident re : n.getResidents()) {
					Player plr = Bukkit.getPlayer(re.getName());
					if (plr != null) {
						TWarMessageHandler.sendMessage(plr, WarsForTowny.getTranslatedMessage("no_longer_neutral"));
					}
				}
			}
			return true;
		}
		return false;
	}

}
