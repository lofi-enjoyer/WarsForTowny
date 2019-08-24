package com.aurgiyalgo.WarsForTowny;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.aurgiyalgo.WarsForTowny.utils.TWarMessageHandler;
import com.palmergames.bukkit.towny.event.NationAddTownEvent;
import com.palmergames.bukkit.towny.event.NationRemoveTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class WarListener implements Listener {
	//TODO
//	private static Town townAdd;
//	private static Town townRemove;

	public WarListener(WarsForTowny instance) {
		
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		try {
			Resident re = TownyUniverse.getDataSource().getResident(player.getName());
			Nation nation = re.getTown().getNation();

			War ww = WarManager.getWarForNation(nation);
			if (ww != null) {
				TWarMessageHandler.sendMessage(player, WarsForTowny.getTranslatedMessage("nation_at_war"));
				if ((WarManager.hasBeenOffered(ww, nation)) && ((nation.hasAssistant(re)) || (re.isKing()))) {
					TWarMessageHandler.sendChatMessage(player, WarsForTowny.getTranslatedMessage("offered_peace"));
				}
			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void onResidentLeave(TownRemoveResidentEvent event) {
		Nation n;
		try {
			n = event.getTown().getNation();
		} catch (NotRegisteredException ex) {
			return;
		}
		War war = WarManager.getWarForNation(n);
		if (war == null) {
			return;
		}
		war.chargeTownPoints(event.getTown(), WarsForTowny.getpPlayer());
	}

	@EventHandler
	public void onResidentAdd(TownAddResidentEvent event) {
		Nation n;
		try {
			n = event.getTown().getNation();
		} catch (NotRegisteredException ex) {
			return;
		}
		War war = WarManager.getWarForNation(n);
		if (war == null) {
			return;
		}
		war.chargeTownPoints(event.getTown(), -WarsForTowny.getpPlayer());
	}

	@EventHandler
	public void onNationAdd(NationAddTownEvent event) {
		War war = WarManager.getWarForNation(event.getNation());
		if (war == null) {
			return;
		}
		war.addTownToNation(event.getNation(), event.getTown());
		event.getTown().setAdminEnabledPVP(true);
	}
	
	@EventHandler
	public void onNationLeave(NationRemoveTownEvent event) {
		War war = WarManager.getWarForNation(event.getNation());
		if (war == null) {
			return;
		}
		event.getTown().setAdminEnabledPVP(false);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player plr = event.getEntity();
		EntityDamageEvent edc = event.getEntity().getLastDamageCause();
		if (!(edc instanceof EntityDamageByEntityEvent)) {
			return;
		}
		EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) edc;
		if (!(edbee.getDamager() instanceof Player)) {
			return;
		}
		Player attacker = (Player) edbee.getDamager();
		Player victim = (Player) edbee.getEntity();
		try {
			Town townAttacker = TownyUniverse.getDataSource().getResident(attacker.getName()).getTown();
			Nation nationAttacker = townAttacker.getNation();

			Town townVictim = TownyUniverse.getDataSource().getResident(victim.getName()).getTown();
			Nation nationVictim = townVictim.getNation();

			War war = WarManager.getWarForNation(nationVictim);
			if (war == null) {
				return;
			}
			
			if (war.getEnemy(nationVictim) != nationAttacker) return;
			
			if (WarsForTowny.isCapitalLastConquered() && townVictim.isCapital() && nationVictim.getNumTowns() > 1) return;
			
			try {
				if (war.chargeTownPoints(townAttacker, -1.0D))
					return;
				if (war.chargeTownPoints(townVictim, 1.0D))
					return;
				int lP = war.getTownPoints(townVictim).intValue();
				if (lP <= 10) {
					String message = WarsForTowny.getTranslatedMessage("be_careful");
					message = message.replaceAll("%points%", String.valueOf(lP));
					TWarMessageHandler.sendMessage(plr, message);
				}
			} catch (Exception ex) {
//				plr.sendMessage(WarsForTowny.getTranslatedMessage("error"));
//				ex.printStackTrace();
			}
			if (nationVictim.getHoldingBalance() < WarsForTowny.getpKill()) {
				WarManager.endWar(nationAttacker, nationVictim, false);
				return;
			}
			war.chargeTownPoints(townVictim, war.getTownPoints(townVictim));
			townVictim.payTo(WarsForTowny.getpKill(), nationAttacker, WarsForTowny.getTranslatedMessage("death_cost"));
			
		} catch (Exception ex) {
			
		}
	}
}
