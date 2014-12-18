package com.franga2000.votehub;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

public class EventListener implements Listener {
	private VoteHub pl;

	public EventListener(VoteHub pl) {
		this.pl = pl;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVotifierEvent(final VotifierEvent event) {
		pl.processVote(event.getVote());
	}
}
