package com.franga2000.votehub;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import com.vexsoftware.votifier.crypto.RSA;
import com.vexsoftware.votifier.model.Vote;

public class Util {
	public static String prefix;
	
	public static void logToConsole(String m) {
		ConsoleCommandSender s = Bukkit.getConsoleSender();
		s.sendMessage(prefix + m);
	}
	
	public static void processVote(final Vote vote) {
		final VoteHub pl = (VoteHub) Bukkit.getPluginManager().getPlugin("VoteHub");
		pl.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
			@Override
			public void run() {
				try {
					for (VoteServer server : pl.servers) {
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
							
							if (VoteHub.config.getBoolean("logToConsole")) Util.logToConsole("§b Vote sent to §e" + server + "§b: §e" + vote.toString());
						} catch (SocketTimeoutException e) {
							Util.logToConsole("§4 Connection to §e" + server + "§4 timed out!");
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
}