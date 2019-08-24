package com.aurgiyalgo.WarsForTowny;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class NeutralityManager {

	private static Set<Nation> neutrals = new HashSet<Nation>();
	
	public static void load(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			return;
		}
		File f = new File(dataFolder, "activeNeutrality.dat");
		if (!f.exists()) {
			return;
		}
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		int ver = dis.readInt();
		int tNeutrality = dis.readInt();
		for (int i = 1; i <= tNeutrality; i++) {
			Nation n = TownyUniverse.getDataSource().getNation(dis.readUTF());
			neutrals.add(n);
		}
		dis.close();
	}
	
	public static void save(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File f = new File(dataFolder, "activeNeutrality.dat");
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
		dos.writeInt(1);
		dos.writeInt(neutrals.size());
		for (Iterator<Nation> i = neutrals.iterator(); i.hasNext();) {
			Nation n = i.next();
			dos.writeUTF(n.getName());
		}
		dos.flush();
		dos.close();
	}

	public static Set<Nation> getNeutrals() {
		return neutrals;
	}

}
