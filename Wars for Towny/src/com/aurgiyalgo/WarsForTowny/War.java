package com.aurgiyalgo.WarsForTowny;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class War {
	
	//Variables
	private Map<Nation, MutableInteger> nations;
	private Map<Town, MutableInteger> towns;
	private double endTime = 0;

	//Constructors
	public War(Nation nat, Nation onat, double endTime) {
		nations = new HashMap<Nation, MutableInteger>();
		towns = new HashMap<Town, MutableInteger>();
		this.endTime = endTime;
		recalculatePoints(nat);
		recalculatePoints(onat);
	}

	public War(DataInputStream dis) throws Exception {
		loadLegacy(dis);
	}

	public War(DataInputStream dis, boolean legacy) throws Exception {
		if (legacy) {
			loadLegacy(dis);
		} else {
			load(dis);
		}
	}

	//MutableInteger class
	public static class MutableInteger {
		public int value;

		public MutableInteger(int value) {
			this.value = value;
		}
	}

	//Legacy load method
	public War loadLegacy(DataInputStream dis) throws Exception {
		int nNations = dis.readInt();
		for (int i = 1; i <= nNations; i++) {
			String nname = dis.readUTF();
			MutableInteger mi = new MutableInteger(dis.readInt());
			nations.put(TownyUniverse.getDataSource().getNation(nname), mi);
		}
		int nTowns = dis.readInt();
		for (int i = 1; i <= nTowns; i++) {
			String str = dis.readUTF();
			MutableInteger mi = new MutableInteger(dis.readInt());
			towns.put(TownyUniverse.getDataSource().getTown(str), mi);
		}
		endTime = System.currentTimeMillis() + WarsForTowny.getMaxWarTime() * 1000;
		return this;
	}

	//Load method
	public War load(DataInputStream dis) throws Exception {
		double endTime = dis.readDouble();
		this.endTime = endTime;
		int nNations = dis.readInt();
		for (int i = 1; i <= nNations; i++) {
			String nname = dis.readUTF();
			MutableInteger mi = new MutableInteger(dis.readInt());
			nations.put(TownyUniverse.getDataSource().getNation(nname), mi);
		}
		int nTowns = dis.readInt();
		for (int i = 1; i <= nTowns; i++) {
			String str = dis.readUTF();
			MutableInteger mi = new MutableInteger(dis.readInt());
			towns.put(TownyUniverse.getDataSource().getTown(str), mi);
		}
		return this;
	}
	
	//Save method
	public void save(DataOutputStream dos) throws Exception {
		dos.writeDouble(endTime);
		dos.writeInt(nations.size());
		for (Map.Entry<Nation, MutableInteger> ff : nations.entrySet()) {
			dos.writeUTF(ff.getKey().toString());
			dos.writeInt(((MutableInteger) ff.getValue()).value);
		}
		dos.writeInt(towns.size());
		for (Map.Entry<Town, MutableInteger> ff : towns.entrySet()) {
			dos.writeUTF(ff.getKey().toString());
			dos.writeInt(((MutableInteger) ff.getValue()).value);
		}
	}

	//Points utilities
	public void recalculatePoints(Nation nat) {
		if (nations.containsKey(nat)) {
			nations.remove(nat);
			for (Town town : nat.getTowns()) {
				towns.remove(town);
			}
		}
		nations.put(nat, new MutableInteger(nat.getNumTowns()));
		for (Town town : nat.getTowns()) {
			towns.put(town, new MutableInteger((int) (town.getNumResidents() * WarsForTowny.getpPlayer()
					+ WarsForTowny.getpPlot() * town.getTownBlocks().size())));
		}
	}
	
	protected int recalculateTownPoints(Town town) {
		return (int) (town.getNumResidents() * WarsForTowny.getpPlayer() + WarsForTowny.getpPlot() * town.getTownBlocks().size());
	}

	public boolean chargeTownPoints(Town town, double i) {
		MutableInteger miTown = towns.get(town);
		miTown.value -= i;
		
		if (miTown.value <= 0) {
			miTown.value = recalculateTownPoints(town);
			try {
				nations.get(town.getNation()).value -= 1;
			} catch (NotRegisteredException e) {
				return true;
			}
		}
		
//		MutableInteger tt = (MutableInteger) towns.get(town);
//		tt.value = (int) (tt.value - i);
//		Nation nation = null;
//		try {
//			nation = town.getNation();
//		} catch (NotRegisteredException e) {
//			return false;
//		}
//		if (tt.value <= 0) {
//			try {
//				towns.remove(town);
//				((MutableInteger) nations.get(town.getNation())).value -= 1;
//				Nation enemyNation = getEnemy(nation);
//				if (nation.getNumTowns() <= 1 || town.isCapital()) {
//					String message = WarsForTowny.getTranslatedMessage("town_conquered");
//					message = message.replaceAll("%town%", town.getName());
//					TWarMessageHandler.sendNationChatMessage(enemyNation, message);
//					Nation winner = getEnemy(nation);
//					Nation looser = nation;
//					winner.collect(looser.getHoldingBalance());
//					looser.pay(looser.getHoldingBalance(), WarsForTowny.getTranslatedMessage("lost_war"));
//					WarManager.endWar(winner, looser, false);
//					winner.addTown(town);
//					TownyUniverse.getDataSource().saveTown(town);
//					TownyUniverse.getDataSource().saveNation(winner);
//					return true;
//				} else {
//					nation.removeTown(town);
//					enemyNation.addTown(town);
//					TownyUniverse.getDataSource().saveTown(town);
//					TownyUniverse.getDataSource().saveNation(enemyNation);
//				}
//				int mr = nation.getNumTowns() + 1;
//				if (mr != 0) {
//					mr = (int) (nation.getHoldingBalance() / mr);
//					if (nation.getHoldingBalance() < mr) WarManager.endWar(enemyNation, nation, false);
//					nation.pay(mr, WarsForTowny.getTranslatedMessage("war_issues"));
//					enemyNation.collect(mr);
//				}
//				String message = WarsForTowny.getTranslatedMessage("town_conquered");
//				message = message.replaceAll("%town%", town.getName());
//				TWarMessageHandler.sendNationChatMessage(enemyNation, message);
//			} catch (Exception ex) {
//				Logger.getLogger(War.class.getName()).log(Level.SEVERE, null, ex);
//			}
//		}
		return false;
	}

	//Nation methods
	public void addTownToNation(Nation nation, Town town) {
		((MutableInteger) nations.get(nation)).value += 1;
		towns.put(town, new MutableInteger((int) (town.getNumResidents() * WarsForTowny.getpPlayer()
				+ WarsForTowny.getpPlot() * town.getTownBlocks().size())));
	}

	public Nation getEnemy(Nation onation) {
		for (Nation n : nations.keySet()) {
			if (!n.equals(onation)) {
				return n;
			}
		}
		return null;
	}

	boolean hasNation(Nation onation) {
		return nations.containsKey(onation);
	}

	public Set<Nation> getNations() {
		return nations.keySet();
	}

	public Map<Town, MutableInteger> getTowns() {
		return towns;
	}

	public Integer getNationPoints(Nation nation) {
		return Integer.valueOf(((MutableInteger) nations.get(nation)).value);
	}

	public Integer getTownPoints(Town town) {
		return Integer.valueOf(((MutableInteger) towns.get(town)).value);
	}
	
	public double getEndTime() {
		return endTime;
	}
}
