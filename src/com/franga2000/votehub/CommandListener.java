package com.franga2000.votehub;

import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.vexsoftware.votifier.model.Vote;

public class CommandListener implements CommandExecutor {
	VoteHub pl;
	
	public CommandListener(VoteHub pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		switch(cmd.getName().toLowerCase()) {
			case "vhreload":
				pl.reload();
				s.sendMessage(Util.prefix + "§bConfig Reloaded");
			return true;
			
			case "sendvote" :
				String arg1 = (args.length > 0) ? args[0] : null;
				String arg2 = (args.length > 1) ? args[1] : "testVote";
				if (arg1 == null) {
					s.sendMessage("§bUsage: §e" + pl.getCommand("sendvote").getUsage());
					return false;
				}
				
				Vote fakeVote = new Vote();
				Date date = new Date();
				
				fakeVote.setUsername(arg1);
				fakeVote.setServiceName(arg2);
				fakeVote.setAddress("1.2.3.4");
				fakeVote.setTimeStamp(String.valueOf(date.getTime()));
				
				Util.processVote(fakeVote);
				s.sendMessage("§bTest vote sent!");
				Util.logToConsole("§e" + s.getName() + "§b sent test vote: §e" + fakeVote.toString());
			return true;
		}
		return false;
	}
}
