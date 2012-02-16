lpackage com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public class CmdCreate extends FCommand
{
	public CmdCreate()
	{
		super();
		this.aliases.add("create");
		
		this.requiredArgs.add("faction tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.CREATE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		String tag = this.argAsString(0);
		
		if (fme.hasFaction())
		{
			msg("<b>Vous devez quitter votre guilde d'abord.");
			return;
		}
		
		if (Factions.i.isTagTaken(tag))
		{
			msg("<b>Ce tag est deja pris.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = Factions.validateTag(tag);
		if (tagValidationErrors.size() > 0)
		{
			sendMessage(tagValidationErrors);
			return;
		}
		
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostCreate, "to create a new faction", "for creating a new faction")) return;

		Faction faction = Factions.i.create();

		// TODO: Why would this even happen??? Auto increment clash??
		if (faction == null)
		{
			msg("<b>Une erreur interne s'est produite durant la creation de votre guilde.Veuillez reessayer");
			return;
		}

		faction.setTag(tag);
		fme.setRole(Rel.LEADER);
		fme.setFaction(faction);

		for (FPlayer follower : FPlayers.i.getOnline())
		{
			follower.msg("%s<i> vient de creer la guilde %s", fme.describeTo(follower, true), faction.getTag(follower));
		}
		
		msg("<i>You should now: %s", p.cmdBase.cmdDescription.getUseageTemplate());

		if (Conf.logFactionCreate)
			P.p.log(fme.getName()+" cree la guilde: "+tag);
	}
	
}
