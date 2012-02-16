package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;

public class CmdChat extends FCommand
{
	
	public CmdChat()
	{
		super();
		this.aliases.add("c");
		this.aliases.add("chat");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("mode", "next");
		
		this.permission = Permission.CHAT.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.factionOnlyChat )
		{
			msg("<b>Le tchat par categorie n'est pas autorise sur ce serveur.");
			return;
		}
		
		String modeString = this.argAsString(0);
		ChatMode modeTarget = fme.getChatMode().getNext();
		
		if (modeString != null)
		{
			modeString.toLowerCase();
			if(modeString.startsWith("p"))
			{
				modeTarget = ChatMode.PUBLIC;
			}
			else if (modeString.startsWith("a"))
			{
				modeTarget = ChatMode.ALLIANCE;
			}
			else if(modeString.startsWith("f"))
			{
				modeTarget = ChatMode.FACTION;
			}
			else
			{
				msg("<b>Categorie de tchat non reconnu. <i>Choisissez parmis 'a','f' ou 'p'");
				return;
			}
		}
		
		fme.setChatMode(modeTarget);
		
		if(fme.getChatMode() == ChatMode.PUBLIC)
		{
			msg("<i>Mode de tchat public.");
		}
		else if (fme.getChatMode() == ChatMode.ALLIANCE )
		{
			msg("<i>Mode de tchat alliance seulement");
		}
		else
		{
			msg("<i>Mode de tchat faction seulement.");
		}
	}
}
