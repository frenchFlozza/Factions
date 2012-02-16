package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdAdmin extends FCommand
{
	public CmdAdmin()
	{
		super();
		this.aliases.add("admin");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flip");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		fme.setHasAdminMode(this.argAsBool(0, ! fme.hasAdminMode()));
		
		if ( fme.hasAdminMode())
		{
			fme.msg("<i>Le mode admin bypass est active");
			P.p.log(fme.getName() + " a active le bypass pour les admins.");
		}
		else
		{
			fme.msg("<i>Le mode admin bypass est desactive.");
			P.p.log(fme.getName() + " a desactive le bypass pour les admins.");
		}
	}
}
