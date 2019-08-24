package com.aurgiyalgo.WarsForTowny.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;

public class TWarMessageHandler {

	private static boolean usingTitles = true;

	public static void sendNationMessage(Nation nat, String message) {
		if (usingTitles) {
			sendNationTitleMessage(nat, message);
		} else {
			sendNationChatMessage(nat, message);
		}
	}

	public static void sendNationChatMessage(Nation nat, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);

		for (Resident res : nat.getResidents()) {
			Player p = Bukkit.getPlayer(res.getName());
			if (p != null) {
				p.sendMessage(message);
			}
		}
	}

	public static void sendNationTitleMessage(Nation nat, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);

		for (Resident res : nat.getResidents()) {
			Player p = Bukkit.getPlayer(res.getName());
			if (p != null) {
				p.sendTitle("", message, 10, 40, 10);
			}
		}
	}

	public static void sendMessage(Player player, String message) {
		if (usingTitles) {
			sendTitle(player, message);
		} else {
			sendChatMessage(player, message);
		}
	}

	public static void sendTitle(Player player, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);

		player.sendTitle("", message, 10, 40, 10);

	}

	public static void sendChatMessage(Player player, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		player.sendMessage(message);
	}

	public static boolean isUsingTitles() {
		return usingTitles;
	}

	public static void setUsingTitles(boolean usingTitles) {
		TWarMessageHandler.usingTitles = usingTitles;
	}

}
