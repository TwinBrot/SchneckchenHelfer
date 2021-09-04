package de.Strobl.Commands.Setup;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ModRolle {

	public static void add(SlashCommandEvent event, InteractionHook Hook) {
		Logger logger = Main.logger;
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String Stufe = event.getOption("zugriffsstufe").getAsString();
			String Old = ini.get("ModRollen", Stufe);
			Role NewRole = event.getOption("rolle").getAsRole();
			
			if (Old.contains(NewRole.getId())) {
				Hook.editOriginal("Rolle bereits in dieser Gruppe.").queue();
				return;
			}
			
			
			if (Old.equals("")) {
				ini.put("ModRollen", Stufe, NewRole.getId());
			} else {
				ini.put("ModRollen", Stufe, Old + ", " + event.getOption("rolle").getAsRole().getId());
			}
			ini.store();
			event.getChannel().sendMessage("Rolle " + NewRole.getId() + " hinzugefügt!").queue();
			list(event, Hook);
		} catch (IOException e) {
			Hook.editOriginal("IO Fehler Beim Auslesen").queue();
			logger.error("IO Fehler Beim Auslesen:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}


	public static void remove(SlashCommandEvent event, InteractionHook Hook) {
		Logger logger = Main.logger;
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Role RemoveRole = event.getOption("rolle").getAsRole();
			EmbedBuilder Response = new EmbedBuilder();
			if (ini.get("ModRollen", "Admin").contains(RemoveRole.getId())) {
				if (ini.get("ModRollen", "Admin").contains(", "+RemoveRole.getId())) {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(", "+RemoveRole.getId(), ""));
				} else if (ini.get("ModRollen", "Admin").contains(RemoveRole.getId() + ", ")) {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(RemoveRole.getId() + ", ", ""));
				} else {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(RemoveRole.getId(), ""));
				}
				Response.addField("Rolle aus Admins Entfernt", RemoveRole.getId(), false);
			}
			
			if (ini.get("ModRollen", "Mod").contains(RemoveRole.getId())) {
				if (ini.get("ModRollen", "Mod").contains(", "+RemoveRole.getId())) {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(", "+RemoveRole.getId(), ""));
				} else if (ini.get("ModRollen", "Mod").contains(RemoveRole.getId() + ", ")) {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(RemoveRole.getId() + ", ", ""));
				} else {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(RemoveRole.getId(), ""));
				}
				Response.addField("Rolle aus Mods Entfernt", RemoveRole.getId(), false);
			}
			
			if (ini.get("ModRollen", "Channelmod").contains(RemoveRole.getId())) {
				if (ini.get("ModRollen", "Channelmod").contains(", "+RemoveRole.getId())) {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(", "+RemoveRole.getId(), ""));
				} else if (ini.get("ModRollen", "Channelmod").contains(RemoveRole.getId() + ", ")) {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(RemoveRole.getId() + ", ", ""));
				} else {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(RemoveRole.getId(), ""));
				}
				Response.addField("Rolle aus Channelmods Entfernt", RemoveRole.getId(), false);
			}
			ini.store();
			if (Response.getFields().size() == 0) {
				Hook.editOriginal("Angegebene Rolle ist keine ModRolle").queue();
				return;
			}
			event.getChannel().sendMessageEmbeds(Response.build()).queue();
			list(event, Hook);
		} catch (IOException e) {
			Hook.editOriginal("IO Fehler Beim Auslesen").queue();
			logger.error("IO Fehler Beim Auslesen:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}

	public static void list(SlashCommandEvent event, InteractionHook Hook) {
		Logger logger = Main.logger;
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Section ModRollen = ini.get("ModRollen");
			EmbedBuilder Response = new EmbedBuilder();
			Response.setAuthor(event.getGuild().getName() + " Modrollen:");

			try {
				if (!ModRollen.get("Admin").replaceAll("\\s", "").replaceAll(",", "").equals("")) {
					String[] AdminList = ModRollen.get("Admin").replaceAll("\\s", "").split(",");
					String Admin = "";
					for (int i = 0; i < AdminList.length; i++) {
						try {
							Admin = Admin + "\n" + event.getGuild().getRoleById(AdminList[i]).getAsMention();
						} catch (NullPointerException e) {
							Admin = Admin + "\n" + AdminList[i];
						}
					}
					Response.addField("Admins:", Admin, false);
				} else {
					Response.addField("Admins:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				Response.addField("Channelmods:", "Fehler beim Auslesen", false);
			}

			try {
				if (!ModRollen.get("Mod").replaceAll("\\s", "").replaceAll(",", "").equals("")) {
					String[] ModList = ModRollen.get("Mod").replaceAll("\\s", "").split(",");
					String Mod = "";
					for (int i = 0; i < ModList.length; i++) {
						try {
							Mod = Mod + "\n" + event.getGuild().getRoleById(ModList[i]).getAsMention();
						} catch (NullPointerException e) {
							Mod = Mod + "\n" + ModList[i];
						}
					}
					Response.addField("Mods:", Mod, false);
				} else {
					Response.addField("Mods:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				Response.addField("Channelmods:", "Fehler beim Auslesen", false);
			}

			try {
				if (!ModRollen.get("Channelmod").replaceAll("\\s", "").replaceAll(",", "").equals("")) {

					String[] ChannelModList = ModRollen.get("Channelmod").replaceAll("\\s", "").split(",");
					String ChannelMod = "";
					for (int i = 0; i < ChannelModList.length; i++) {
						try {
							ChannelMod = ChannelMod + "\n" + event.getGuild().getRoleById(ChannelModList[i]).getAsMention();
						} catch (NullPointerException e) {
							ChannelMod = ChannelMod + "\n" + ChannelModList[i];
						}
					}
					Response.addField("Channelmods:", ChannelMod, false);
				} else {
					Response.addField("Channelmods:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				Response.addField("Channelmods:", "Fehler beim Auslesen", false);
			}



			event.getChannel().sendMessageEmbeds(Response.build()).queue();
			Hook.editOriginal("Erfolg.").queue();


		} catch (IOException e) {
			Hook.editOriginal("IO Fehler Beim Auslesen").queue();
			logger.error("IO Fehler Beim Auslesen:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}

}