package com.massivecraft.factions.cmd;

import java.util.Set;

import org.bukkit.Location;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdClaim extends FCommand
{
	
	public CmdClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("radius", "1");
		
		this.permission = Permission.CLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		// Read and validate input
		Faction forFaction = this.argAsFaction(0, myFaction);

		// just to cut the unauthorized off immediately instead of going on to do radius calculations
		if (! fme.canClaimForFactionAtLocation(forFaction, me.getLocation(), false))
		{
			msg("<b>Actuellement vous n'avez pas le droit de reclamer un terrain pour la faction "+forFaction.describeTo(fme) +"<b>.");
			return;
		}

		double radius = this.argAsDouble(1, 1d);
		radius -= 0.5;
		if (radius <= 0)
		{
			msg("<b>Ce rayon est trop petit.");
			return;
		}
		else if (radius > 100)  // huge radius can crash server
		{
			msg("<b>Ce rayon est trop large. Souvenez vous qu'il est exprime en chunk (16x16 blocs), pas en bloc individuel.");
			return;
		}
		
		// Get the FLocations
		Set<FLocation> flocs = new FLocation(me).getCircle(radius);
		for (FLocation floc : flocs)
		{
			fme.attemptClaim(forFaction, new Location(floc.getWorld(), floc.getX() << 4, 1, floc.getZ() << 4), true);
		}
	}
	
}
