package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite extends FCommand
{
	
	public CmdDeinvite()
	{
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");
		
		this.requiredArgs.add("player");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.DEINVITE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myFaction)
		{
			msg("%s<i> est deja un membre de %s", you.getName(), myFaction.getTag());
			msg("<i>Peut etre vouliez vous: %s", p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.msg("%s<i> retire votre invitation a <h>%s<i>.", fme.describeTo(you), myFaction.describeTo(you));
		
		myFaction.msg("%s<i> annule l'invitation de %s<i>.", fme.describeTo(myFaction), you.describeTo(myFaction));
	}
	
}
