package com.aurgiyalgo.WarsForTowny;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.WarsForTowny.utils.TWarMessageHandler;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class WarManager {
	private static Set<War> activeWars = new HashSet<War>();
	private static Set<Nation> requestedPeace = new HashSet<Nation>();
	private static int scheduleID;

	public static void startEndCheck() {
		scheduleID = Bukkit.getScheduler().scheduleSyncRepeatingTask(WarsForTowny.getInstance(), new Runnable() {

			@Override
			public void run() {
				for (War w : activeWars) {
					if (w.getEndTime() <= System.currentTimeMillis()) endWar((Nation)w.getNations().toArray()[0], (Nation)w.getNations().toArray()[1], true);
				}
			}

		}, 0, 100);
	}
	
	public static void stopEndCheck() {
		Bukkit.getScheduler().cancelTask(scheduleID);
	}

	public static void load(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			return;
		}
		File f = new File(dataFolder, "activeWars.dat");
		if (!f.exists()) {
			return;
		}
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		int ver = dis.readInt();
		int tWars = dis.readInt();
		if (ver == 1) {
			for (int i = 1; i <= tWars; i++) {
				War ww = new War(dis, true);
				activeWars.add(ww);
			}
		} else {
			for (int i = 1; i <= tWars; i++) {
				War ww = new War(dis);
				activeWars.add(ww);
			}
		}
		dis.close();
	}

	public static void save(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File f = new File(dataFolder, "activeWars.dat");
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
		dos.writeInt(2);
		dos.writeInt(activeWars.size());
		for (Iterator<War> i = activeWars.iterator(); i.hasNext();) {
			War war = i.next();
			war.save(dos);
		}
		dos.flush();
		dos.close();
	}

	public static void createWar(Nation nat, Nation onat, CommandSender cs) {
		if ((getWarForNation(nat) != null) || (getWarForNation(onat) != null)) {
			cs.sendMessage(WarsForTowny.getTranslatedMessage("nation_already_at_war"));
		} else {
			try {
				try {
					TownyUniverse.getDataSource().getNation(nat.getName()).addEnemy(onat);
				} catch (AlreadyRegisteredException ex) {

				}
				try {
					TownyUniverse.getDataSource().getNation(onat.getName()).addEnemy(nat);
				} catch (AlreadyRegisteredException e) {

				}
			} catch (NotRegisteredException ex) {
				Logger.getLogger(WarManager.class.getName()).log(Level.SEVERE, null, ex);
			}
			War war = new War(nat, onat, System.currentTimeMillis() + WarsForTowny.getMaxWarTime()*1000);
			activeWars.add(war);

			String message = WarsForTowny.getTranslatedMessage("now_at_war_with");
			message = message.replace("%nation%", onat.getName());
			TWarMessageHandler.sendNationMessage(nat, message);

			message = WarsForTowny.getTranslatedMessage("now_at_war_with");
			message = message.replace("%nation%", nat.getName());
			TWarMessageHandler.sendNationMessage(onat, message);

			for (Town to : nat.getTowns()) {
				Town t = null;
				try {
					t = TownyUniverse.getDataSource().getTown(to.getName());
				} catch (NotRegisteredException e) {
					e.printStackTrace();
				}
				t.setAdminEnabledPVP(true);
				t.setPVP(true);
			}
			for (Town to : onat.getTowns()) {
				Town t = null;
				try {
					t = TownyUniverse.getDataSource().getTown(to.getName());
				} catch (NotRegisteredException e) {
					e.printStackTrace();
				}
				t.setAdminEnabledPVP(true);
				t.setPVP(true);
			}
		}
	}

	public static boolean requestPeace(Nation nat, Nation onat, boolean admin) {
		if (admin) {
			endWar(nat, onat, true);
			return true;
		}
		if (requestedPeace.contains(onat)) {
			endWar(nat, onat, true);
			try {
				nat.pay(WarsForTowny.getEndCost(), "Finish cost");
				onat.pay(WarsForTowny.getEndCost(), "Finish cost");
			} catch (EconomyException ex) {
				Logger.getLogger(WarManager.class.getName()).log(Level.SEVERE, null, ex);
			}
			return true;
		}
		requestedPeace.add(nat);
		for (Resident re : onat.getResidents()) {
			Player plr = Bukkit.getPlayer(re.getName());
			if (plr != null) {
				String message = WarsForTowny.getTranslatedMessage("just_requested_peace");
				message = message.replaceAll("%nation%", nat.getName());
				plr.sendMessage(message);
			}
		}
		return false;
	}

	public static void endWar(Nation winner, Nation looser, boolean peace) {
		try {
			if (winner.hasEnemy(looser)) {
				TownyUniverse.getDataSource().getNation(winner.getName())
						.removeEnemy(TownyUniverse.getDataSource().getNation(looser.getName()));
			}
			if (looser.hasEnemy(winner)) {
				TownyUniverse.getDataSource().getNation(looser.getName())
						.removeEnemy(TownyUniverse.getDataSource().getNation(winner.getName()));
			}
		} catch (NotRegisteredException ex) {
			Logger.getLogger(WarManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		activeWars.remove(getWarForNation(winner));
		requestedPeace.remove(winner);
		requestedPeace.remove(looser);
		TWarMessageHandler.sendNationChatMessage(winner, WarsForTowny.getTranslatedMessage("peace"));
		TWarMessageHandler.sendNationChatMessage(looser, WarsForTowny.getTranslatedMessage("peace"));
		for (Town to : winner.getTowns()) {
			Town t = null;
			try {
				t = TownyUniverse.getDataSource().getTown(to.getName());
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			t.setAdminEnabledPVP(false);
			t.setPVP(false);
		}
		if (peace) {
			for (Town to : looser.getTowns()) {
				Town t = null;
				try {
					t = TownyUniverse.getDataSource().getTown(to.getName());
				} catch (NotRegisteredException e) {
					e.printStackTrace();
				}
				t.setAdminEnabledPVP(false);
				t.setPVP(false);
			}
		} else {
			TownyUniverse.getDataSource().removeNation(looser);
			looser.clear();
		}
	}

	public static War getWarForNation(Nation onation) {
		for (War w : activeWars) {
			if (w.hasNation(onation)) {
				return w;
			}
		}
		return null;
	}

	public static boolean hasBeenOffered(War ww, Nation nation) {
		return requestedPeace.contains(ww.getEnemy(nation));
	}

	public static Set<War> getWars() {
		return activeWars;
	}
}
