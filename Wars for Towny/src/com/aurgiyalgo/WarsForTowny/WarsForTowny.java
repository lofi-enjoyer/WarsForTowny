package com.aurgiyalgo.WarsForTowny;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.aurgiyalgo.WarsForTowny.commands.WfTCommand;
import com.aurgiyalgo.WarsForTowny.utils.Metrics;
import com.aurgiyalgo.WarsForTowny.utils.TWarMessageHandler;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class WarsForTowny extends JavaPlugin {
	
	private static YamlConfiguration languageFile;
	
	private static WarsForTowny instance;
	
	private static double pPlayer;
	private static double pPlot;
	private static double pKill;
	private static double declareCost;
	private static double endCost;
	private static double neutralityCost;
	private static double maxWarTime;
	private static boolean capitalLastConquered;
	private Metrics metrics;

	public void onEnable() {
		instance = this;
		
		try {
			WarManager.load(getDataFolder());
			NeutralityManager.load(getDataFolder());
		} catch (Exception ex) {
			Logger.getLogger(WarsForTowny.class.getName()).severe(ex.getMessage());
		}
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new WarListener(this), this);
		getCommand("warsfortowny").setExecutor(new WfTCommand(this));
		
		for (War w : WarManager.getWars()) {
			for (Nation nation : w.getNations()) {
				for (Town t : nation.getTowns()) {
					t.setAdminEnabledPVP(true);
				}
			}
		}
		
		metrics = new Metrics(this);
		
		getConfig().addDefault("pper-player", Double.valueOf(50.0D));
		getConfig().addDefault("pper-plot", Double.valueOf(25.5D));
		getConfig().addDefault("declare-cost", Double.valueOf(500.0D));
		getConfig().addDefault("end-cost", Double.valueOf(250.0D));
		getConfig().addDefault("death-cost", Double.valueOf(50.0D));
		getConfig().addDefault("neutrality-cost", Double.valueOf(1000.0D));
		getConfig().addDefault("language", String.valueOf("en_US"));
		getConfig().addDefault("use-titles", Boolean.valueOf(true));
		getConfig().addDefault("max-war-time", Double.valueOf(604800));
		getConfig().addDefault("capital-last-conquered", Boolean.valueOf(false));
		getConfig().options().copyDefaults(true);
		saveConfig();

		pPlayer = getConfig().getDouble("pper-player");
		pPlot = getConfig().getDouble("pper-plot");
		declareCost = getConfig().getDouble("declare-cost");
		endCost = getConfig().getDouble("end-cost");
		pKill = getConfig().getDouble("death-cost");
		neutralityCost = getConfig().getDouble("neutrality-cost");
		maxWarTime = getConfig().getDouble("max-war-time");
		capitalLastConquered = getConfig().getBoolean("capital-last-conquered");
		TWarMessageHandler.setUsingTitles(getConfig().getBoolean("use-titles"));
		
		File file = new File(getDataFolder(), getConfig().getString("language") + ".yml");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		languageFile = YamlConfiguration.loadConfiguration(file);
		languageFile.addDefault("war_issues", "War issues...");
		languageFile.addDefault("town_conquered", "&3%town% &ahas been conquered and joined your nation in the war");
		languageFile.addDefault("lost_war", "&cYou lost the war");
		languageFile.addDefault("peace", "&aYou are now at peace");
		languageFile.addDefault("nation_at_war", "&cWarning: Your nation is at war");
		languageFile.addDefault("offered_peace", "&cThe other nation has offered peace");
		languageFile.addDefault("be_careful", "&cBe careful! Your town only has %points% left");
		languageFile.addDefault("nation_already_at_war", "&cYour nation is already at war with another nation!");
		languageFile.addDefault("now_at_war_with", "&cYour nation is now at war with %nation%");
		languageFile.addDefault("just_requested_peace", "'&c%nation% has requested peace");
		languageFile.addDefault("death_cost", "&bDeath cost");
		languageFile.addDefault("no-longer-neutral", "&cYour nation is no longer neutral");
		languageFile.addDefault("at_war_cannot_be_neutral", "&4A nation at war cannot be neutral!");
		languageFile.addDefault("neutrality_cost", "Neutrality cost");
		languageFile.addDefault("now_neutral", "&aYour nation is now neutral");
		languageFile.addDefault("no_longer_neutral", "&cYour nation is no longer neutral");
		languageFile.addDefault("not_part_of_a_nation", "&cYou are not not part of a town, or your town is not part of a nation!");
		languageFile.addDefault("nation_now_neutral", "&aaThe nation %nation% is now neutral!");
		languageFile.addDefault("nation_no_longer_neutral", "&caThe nation %nation% is no longer neutral!");
		languageFile.addDefault("nation_not_found", "&cThe nation called %nation% cannot be found");
		languageFile.addDefault("no_permission", "&cYou are not allowed to do this!");
		languageFile.addDefault("error", "&4An error occured, check the console!");
		languageFile.addDefault("command_not_found", "&cCommand not found!");
		languageFile.addDefault("specify_two_nations", "&cYou need to specify two nations!");
		languageFile.addDefault("not_at_war", "&cThe nation %nation% is not at war");
		languageFile.addDefault("forced_peace", "&aForced peace!");
		languageFile.addDefault("requested_peace", "&aRequested peace!");
		languageFile.addDefault("specify_a_nation", "&cYou need to specify a nation!");
		languageFile.addDefault("already_at_war", "&cNation already at war!");
		languageFile.addDefault("war_with_itself", "&cA nation cannot be at war with itself");
		languageFile.addDefault("nation_neutral", "&cThat nation is neutral!");
		languageFile.addDefault("nation_cannot_declare", "&cA neutral nation cannot declare war on others");
		languageFile.addDefault("declared_war", "&cWar declared to %nation%");
		languageFile.addDefault("not-enough-money", "&cNot enough money!");
		languageFile.options().copyDefaults(true);
		
		try {
			languageFile.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WarManager.startEndCheck();
	}

	public void onDisable() {
		try {
			WarManager.save(getDataFolder());
			NeutralityManager.save(getDataFolder());
		} catch (Exception ex) {
			Logger.getLogger(WarsForTowny.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	//Getters

	public static double getpPlayer() {
		return pPlayer;
	}

	public static double getpPlot() {
		return pPlot;
	}

	public static double getpKill() {
		return pKill;
	}

	public static double getDeclareCost() {
		return declareCost;
	}

	public static double getEndCost() {
		return endCost;
	}
	
	public static String getTranslatedMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', languageFile.getString(message));
	}
	
	public static WarsForTowny getInstance() {
		return instance;
	}

	public static double getNeutralityCost() {
		return neutralityCost;
	}
	
	public static double getMaxWarTime() {
		return maxWarTime;
	}
	
	public static boolean isCapitalLastConquered() {
		return capitalLastConquered;
	}
}
