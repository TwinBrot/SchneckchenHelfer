package de.Strobl.Commands.Setup;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ModRolle {
	private static final Logger logger = LogManager.getLogger(ModRolle.class);

	public static void add(SlashCommandInteractionEvent event, InteractionHook Hook) {
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
			Settings.load();
			EmbedBuilder builder = Discord.standardEmbed(Color.CYAN,
					"Rolle " + NewRole.getName() + "(" + NewRole.getId() + ")" + " als " + Stufe + " hinzugefügt!",
					event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			Hook.editOriginal("").setEmbeds(builder.build()).queue();
		} catch (IOException e) {
			Hook.editOriginal("IO Fehler Beim Auslesen").queue();
			logger.error("IO Fehler Beim Auslesen:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}

	public static void remove(SlashCommandInteractionEvent event, InteractionHook Hook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String RemoveRole = event.getOption("rolle").getAsString();

			EmbedBuilder builder = Discord.standardEmbed(Color.CYAN, "ModRolle Entfernt", event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());

			if (ini.get("ModRollen", "Admin").contains(RemoveRole)) {
				if (ini.get("ModRollen", "Admin").contains(", " + RemoveRole)) {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(", " + RemoveRole, ""));
				} else if (ini.get("ModRollen", "Admin").contains(RemoveRole + ", ")) {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(RemoveRole + ", ", ""));
				} else {
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Admin").replace(RemoveRole, ""));
				}
				builder.addField("Rolle aus Admins Entfernt", RemoveRole, false);
			}

			if (ini.get("ModRollen", "Mod").contains(RemoveRole)) {
				if (ini.get("ModRollen", "Mod").contains(", " + RemoveRole)) {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(", " + RemoveRole, ""));
				} else if (ini.get("ModRollen", "Mod").contains(RemoveRole + ", ")) {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(RemoveRole + ", ", ""));
				} else {
					ini.put("ModRollen", "Mod", ini.get("ModRollen", "Mod").replace(RemoveRole, ""));
				}
				builder.addField("Rolle aus Mods Entfernt", RemoveRole, false);
			}

			if (ini.get("ModRollen", "Channelmod").contains(RemoveRole)) {
				if (ini.get("ModRollen", "Channelmod").contains(", " + RemoveRole)) {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(", " + RemoveRole, ""));
				} else if (ini.get("ModRollen", "Channelmod").contains(RemoveRole + ", ")) {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(RemoveRole + ", ", ""));
				} else {
					ini.put("ModRollen", "Channelmod", ini.get("ModRollen", "Channelmod").replace(RemoveRole, ""));
				}
				builder.addField("Rolle aus Channelmods Entfernt", RemoveRole, false);
			}
			ini.store();
			Settings.load();
			if (builder.getFields().size() == 0) {
				Hook.editOriginal("Angegebene Rolle ist keine ModRolle").queue();
				return;
			}
			Hook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();
		} catch (IOException e) {
			Hook.editOriginal("IO Fehler Beim Auslesen").queue();
			logger.error("IO Fehler Beim Auslesen:", e);
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}

	public static void list(SlashCommandInteractionEvent event, InteractionHook Hook) {
		try {
			String[] AdminList = Settings.Admin;
			String[] ModList = Settings.Mod;
			String[] ChannelModList = Settings.Channelmod;

			EmbedBuilder builder = Discord.standardEmbed(Color.CYAN, "Modrollen:", event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());

			try {
				if (!(AdminList.length == 0)) {
					String Admin = "";
					for (int i = 0; i < AdminList.length; i++) {
						try {
							Admin = Admin + "\n" + event.getGuild().getRoleById(AdminList[i]).getAsMention();
						} catch (NullPointerException e) {
							Admin = Admin + "\n" + AdminList[i];
						}
					}
					builder.addField("Admins:", Admin, false);
				} else {
					builder.addField("Admins:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				builder.addField("Channelmods:", "Fehler beim Auslesen", false);
			}

			try {
				if (!(ModList.length == 0)) {
					String Mod = "";
					for (int i = 0; i < ModList.length; i++) {
						try {
							Mod = Mod + "\n" + event.getGuild().getRoleById(ModList[i]).getAsMention();
						} catch (NullPointerException e) {
							Mod = Mod + "\n" + ModList[i];
						}
					}
					builder.addField("Mods:", Mod, false);
				} else {
					builder.addField("Mods:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				builder.addField("Channelmods:", "Fehler beim Auslesen", false);
			}

			try {
				if (!(ChannelModList.length == 0)) {
					String ChannelMod = "";
					for (int i = 0; i < ChannelModList.length; i++) {
						try {
							ChannelMod = ChannelMod + "\n" + event.getGuild().getRoleById(ChannelModList[i]).getAsMention();
						} catch (NullPointerException e) {
							ChannelMod = ChannelMod + "\n" + ChannelModList[i];
						}
					}
					builder.addField("Channelmods:", ChannelMod, false);
				} else {
					builder.addField("Channelmods:", "Keine eingerichtet", false);
				}
			} catch (NumberFormatException e) {
				builder.addField("Channelmods:", "Fehler beim Auslesen", false);
			}

			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());

			Hook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();
		} catch (Exception e) {
			Hook.editOriginal("Fehler beim auslesen").queue();
			logger.error("Fehler beim auslesen:", e);
		}
	}

}