package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdDisband extends FCommand
{
	public CmdDisband()
	{
		super();
		this.aliases.add("disband");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.DISBAND.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		// The faction, default to your own.. but null if console sender.
		Faction faction = this.argAsFaction(0, fme == null ? null : myFaction);
		if (faction == null) return;
		
		if ( ! FPerm.DISBAND.has(sender, faction, true)) return;

		if (faction.getFlag(FFlag.PERMANENT))
		{
			msg("<i>Cette guilde a ete definie permanente. Vous ne pouvez pas la dissoudre.");
			return;
		}

		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			String who = senderIsConsole ? "A server admin" : fme.describeTo(fplayer);
			if (fplayer.getFaction() == faction)
			{
				fplayer.msg("<h>%s<i> a dissout votre guilde.", who);
			}
			else
			{
				fplayer.msg("<h>%s<i> a dissout la guilde %s.", who, faction.getTag(fplayer));
			}
		}
		if (Conf.logFactionDisband)
			P.p.log("La guilde "+faction.getTag()+" ("+faction.getId()+") a ete dissoute par "+(senderIsConsole ? "console command" : fme.getName())+".");

		if (Econ.shouldBeUsed() && ! senderIsConsole)
		{
			//Give all the faction's money to the disbander
			double amount = Econ.getBalance(faction.getAccountId());
			Econ.transferMoney(fme, faction, fme, amount, false);
			
			if (amount > 0.0)
			{
				String amountString = Econ.moneyString(amount);
				msg("<i>Vous recuperez la tresorerie de la guilde dissoute, soit un total de %s.", amountString);
				P.p.log(fme.getName() + " a recupere la somme de "+amountString+" par la dissolution de la guilde "+faction.getTag()+".");
			}
		}		
		
		faction.detach();

		SpoutFeatures.updateAppearances();
	}
}
