package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdChatSpy extends FCommand
{
	public CmdChatSpy()
	{
		super();
		this.aliases.add("chatspy");
		
		this.optionalArgs.put("on/off", "flip");
		
		this.permission = Permission.CHATSPY.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		fme.setSpyingChat(this.argAsBool(0, ! fme.isSpyingChat()));
		
		if ( fme.isSpyingChat())
		{
			fme.msg("<i>Vous avez active le mode d'espionnage du tchat");
			P.p.log(fme.getName() + " a demarre l'espionnage du tchat");
		}
		else
		{
			fme.msg("<i>Vous avez desactive le mode d'espionnage du tchat");
			P.p.log("L'espionnage du tchat a ete stoppe par " + fme.getName());
		}
	}
}
