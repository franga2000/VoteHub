package com.franga2000.votehub;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.crypto.RSA;
import com.vexsoftware.votifier.model.Vote;

/**
 * @author franga2000
 *
 */
public class VoteHub extends JavaPlugin {
	public static ConfigurationSection config;
	private List<VoteServer> servers;
	
	public void onEnable() {
		this.servers = new ArrayList<VoteServer>();
		
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
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
	
	public void processVote(final Vote vote) {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				try {
					for (VoteServer server : VoteHub.this.servers) {
						if (server.address.length() == 0) continue;
						
						try {
							if (server.custom.length() > 0) vote.setServiceName(server.custom);
							
							String VoteString = "VOTE\n" + vote.getServiceName() + "\n" + vote.getUsername() + "\n" + vote.getAddress() + "\n" + vote.getTimeStamp() + "\n";
							SocketAddress sockAddr = new InetSocketAddress(server.address, server.port);
							Socket socket = new Socket();
							socket.connect(sockAddr, 1000);
							OutputStream socketOutputStream = socket.getOutputStream();
							socketOutputStream.write(RSA.encrypt(VoteString.getBytes(), server.pubkey));
							socketOutputStream.close();
							socket.close();
							if (VoteHub.config.getBoolean("logToConsole")) Bukkit.getLogger().info("§b" + Bukkit.getPluginManager().getPlugin("VoteHub").getDescription().getPrefix() + "§b Vote sent to §e" + server + "§b: §e" + vote.toString());
						} catch (SocketTimeoutException e) {
							Bukkit.getLogger().info("§b" + Bukkit.getPluginManager().getPlugin("VoteHub").getDescription().getPrefix() + "§4 Connection to §e" + server + "§4 timed out!");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
						Bukkit.getLogger().severe("§b" + this.getDescription().getPrefix() + "§4 Invalid key for server " + server);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}