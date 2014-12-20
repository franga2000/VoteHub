package com.franga2000.votehub;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteHub extends JavaPlugin {
	public static ConfigurationSection config;
	List<VoteServer> servers;
	
	public void onEnable() {
		Util.prefix = "[" + getDescription().getPrefix() + "] §b";
		this.servers = new ArrayList<VoteServer>();
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getCommand("vhreload").setExecutor(new CommandListener(this));
		this.getCommand("sendvote").setExecutor(new CommandListener(this));
		
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		
		File file = new File(this.getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", false);
		}
		this.reload();
	}
	
	public void onDisable() {
		
	}
	
	public void reload() {
		this.reloadConfig();
		this.servers.clear();
		config = this.getConfig().getConfigurationSection("servers");
		if (config != null) {
			for (String server : config.getKeys(false)) {
				ConfigurationSection serverConfig = config.getConfigurationSection(server);
				if (serverConfig != null) {
					server = server.toLowerCase();
					try {
						this.servers.add(new VoteServer(server, serverConfig.getString("pubkey"), serverConfig.getString("address"), serverConfig.getInt("port"), serverConfig.getString("custom") != null ? serverConfig.getString("custom") : ""));
					} catch (InvalidKeySpecException e) {
						Util.logToConsole("§4 Invalid key for server " + server);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}