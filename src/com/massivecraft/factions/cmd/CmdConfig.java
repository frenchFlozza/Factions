package com.massivecraft.factions.cmd;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

public class CmdConfig extends FCommand
{
	private static HashMap<String, String> properFieldNames = new HashMap<String, String>();

	public CmdConfig()
	{
		super();
		this.aliases.add("config");
		
		this.requiredArgs.add("setting");
		this.requiredArgs.add("value");
		this.errorOnToManyArgs = false;
		
		this.permission = Permission.CONFIG.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}

	@Override
	public void perform()
	{
		// store a lookup map of lowercase field names paired with proper capitalization field names
		// that way, if the person using this command messes up the capitalization, we can fix that
		if (properFieldNames.isEmpty())
		{
			Field[] fields = Conf.class.getDeclaredFields();
			for(int i = 0; i < fields.length; i++)
			{
				properFieldNames.put(fields[i].getName().toLowerCase(), fields[i].getName());
			}
		}

		String field = this.argAsString(0).toLowerCase();
		if (field.startsWith("\"") && field.endsWith("\""))
		{
			field = field.substring(1, field.length() - 1);
		}
		String fieldName = properFieldNames.get(field);

		if (fieldName == null || fieldName.isEmpty())
		{
			msg("<b>Dans la configuration, aucun parametre \"<h>%s<b>\" n'a ete trouve.", field);
			return;
		}

		String success = "";

		String value = args.get(1);
		for(int i = 2; i < args.size(); i++)
		{
			value += ' ' + args.get(i);
		}

		try
		{
			Field target = Conf.class.getField(fieldName);

			// boolean
			if (target.getType() == boolean.class)
			{
				boolean targetValue = this.strAsBool(value);
				target.setBoolean(null, targetValue);
				
				if (targetValue)
				{
					success = "Parametre \""+fieldName+"\" defini sur true (active).";
				}
				else
				{
					success = "Parametre \""+fieldName+"\" defini sur false (desactive).";
				}
			}

			// int 
			else if (target.getType() == int.class)
			{
				try
				{
					int intVal = Integer.parseInt(value);
					target.setInt(null, intVal);
					success = "Parametre \""+fieldName+"\" redefini a "+intVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Impossible de modifier le parametre \""+fieldName+"\": ne supporte qu'un entier(nombre/chiffre).");
					return;
				}
			}

			// double
			else if (target.getType() == double.class)
			{
				try
				{
					double doubleVal = Double.parseDouble(value);
					target.setDouble(null, doubleVal);
					success = "Parametre \""+fieldName+"\" defini a "+doubleVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Impossible de redefinir le parametre \""+fieldName+"\": ne supportant qu'un double (numerique).");
					return;
				}
			}

			// float
			else if (target.getType() == float.class)
			{
				try
				{
					float floatVal = Float.parseFloat(value);
					target.setFloat(null, floatVal);
					success = "Parametre \""+fieldName+"\" defini a "+floatVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Impossible de redefinir le parametre \""+fieldName+"\": ne supportant qu'un nombre reel simple)");
					return;
				}
			}

			// String
			else if (target.getType() == String.class)
			{
				target.set(null, value);
				success = "Parametre \""+fieldName+"\" defini en \""+value+"\".";
			}

			// ChatColor
			else if (target.getType() == ChatColor.class)
			{
				ChatColor newColor = null;
				try
				{
					newColor = ChatColor.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException ex)
				{
					
				}
				if (newColor == null)
				{
					sendMessage("Impossible de redefinir le parametre \""+fieldName+"\": \""+value.toUpperCase()+"\" n'est pas une couleur valide.");
					return;
				}
				target.set(null, newColor);
				success = "Parametre de couleur de  \""+fieldName+"\" defini en \""+value.toUpperCase()+"\".";
			}

			// Set<?> or other parameterized collection
			else if (target.getGenericType() instanceof ParameterizedType)
			{
				ParameterizedType targSet = (ParameterizedType)target.getGenericType();
				Type innerType = targSet.getActualTypeArguments()[0];

				// not a Set, somehow, and that should be the only collection we're using in Conf.java
				if (targSet.getRawType() != Set.class)
				{
					sendMessage("La parametre \""+fieldName+"\" ne peut pas etre modifie avec cette commande.");
					return;
				}

				// Set<Material>
				else if (innerType == Material.class)
				{
					Material newMat = null;
					try
					{
						newMat = Material.valueOf(value.toUpperCase());
					}
					catch (IllegalArgumentException ex)
					{
						
					}
					if (newMat == null)
					{
						sendMessage("Impossible de changer le parametre \""+fieldName+"\": \""+value.toUpperCase()+"\" n'est pas un bloc-materiel valide.");
						return;
					}

					@SuppressWarnings("unchecked")
					Set<Material> matSet = (Set<Material>)target.get(null);

					// Material already present, so remove it
					if (matSet.contains(newMat))
					{
						matSet.remove(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" removed.";
					}
					// Material not present yet, add it
					else
					{
						matSet.add(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" added.";
					}
				}

				// Set<String>
				else if (innerType == String.class)
				{
					@SuppressWarnings("unchecked")
					Set<String> stringSet = (Set<String>)target.get(null);

					// String already present, so remove it
					if (stringSet.contains(value))
					{
						stringSet.remove(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" removed.";
					}
					// String not present yet, add it
					else 
					{
						stringSet.add(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" added.";
					}
				}

				// Set of unknown type
				else
				{
					sendMessage("\""+fieldName+"\" is not a data type set which can be modified with this command.");
					return;
				}
			}

			// unknown type
			else
			{
				sendMessage("\""+fieldName+"\" is not a data type which can be modified with this command.");
				return;
			}
		}
		catch (NoSuchFieldException ex)
		{
			sendMessage("Configuration setting \""+fieldName+"\" couldn't be matched, though it should be... please report this error.");
			return;
		}
		catch (IllegalAccessException ex)
		{
			sendMessage("Error setting configuration setting \""+fieldName+"\" to \""+value+"\".");
			return;
		}

		if (!success.isEmpty())
		{
			sendMessage(success);
			if (sender instanceof Player)
			{
				P.p.log(success + " Command was run by "+fme.getName()+".");
			}
		}
		// save change to disk
		Conf.save();

		// in case some Spout related setting was changed
		SpoutFeatures.updateAppearances();
	}
	
}
