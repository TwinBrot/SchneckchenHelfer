package de.Strobl.CommandsMod.Setup;

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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Dateiüberwachung {
	private static final Logger logger = LogManager.getLogger(Dateiüberwachung.class);

	public static void add(SlashCommandInteractionEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Allowed = ini.get("Dateiüberwachung", "Allowed").split(",\\s+");
			String newname = event.getOptionsByName("name").get(0).getAsString().toLowerCase().replaceAll("\\s+", "");
			if (newname.contains("")) {
				EventHook.editOriginal("Fehler verhindert. Zeichen '' sorgt für Fehler im Programm.").queue();
				return;
			}
			String list = "";
			for (int i = 0; i < Allowed.length; i++) {
				if (Allowed[i].equals(newname)) {
					EventHook.editOriginal("Datei bereits Erlaubt!").queue();
					return;
				}
				;
				list = list + Allowed[i] + "\n";
			}
			list = list + newname;
			if (ini.get("Dateiüberwachung", "Allowed") == null || ini.get("Dateiüberwachung", "Allowed").equals("")) {
				ini.put("Dateiüberwachung", "Allowed", newname);
			} else {
				ini.put("Dateiüberwachung", "Allowed", ini.get("Dateiüberwachung", "Allowed") + ", " + newname);
			}
			ini.store();
			Settings.load();

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Dateiüberwachung Liste (Erlaubt) \n (Alle Endungen kleingeschrieben! Das ist gewollt!)",
					event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setDescription(list);
			builder.addField("Hinzugefügt: ", newname, true);
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			EventHook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();

		} catch (IOException e) {
			logger.error("IO-Fehler bei Info-Befehl", e);
			EventHook.editOriginal("IO-Fehler beim Ausführen.").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}

	public static void remove(SlashCommandInteractionEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Allowed = ini.get("Dateiüberwachung", "Allowed").split(",\\s+");
			String oldname = event.getOptionsByName("name").get(0).getAsString().toLowerCase().replaceAll("\\s+", "");
			String list = "";
			Boolean vorhanden = false;
			for (int i = 0; i < Allowed.length; i++) {
				if (!Allowed[i].equals(oldname)) {
					if (i == 0) {
						list = list + Allowed[i];
					} else {
						list = list + "\n" + Allowed[i];
					}
				} else {
					vorhanden = true;
				}
				;
			}
			if (!vorhanden) {
				EventHook.editOriginal("Name war nicht erlaubt!").queue();
				return;
			}
			ini.put("Dateiüberwachung", "Allowed", list.replaceAll("\n", ", "));
			ini.store();
			Settings.load();

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Dateiüberwachung Liste (Erlaubt) \n (Alle Endungen kleingeschrieben! Das ist gewollt!)",
					event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setDescription(list);
			builder.addField("Entfernt: ", oldname, true);
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			EventHook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();

		} catch (IOException e) {
			logger.error("IO-Fehler bei Info-Befehl", e);
			EventHook.editOriginal("IO-Fehler beim Ausführen.").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}

	public static void list(SlashCommandInteractionEvent event, InteractionHook EventHook) {
		try {
			String[] Allowed = Settings.Datei;

			String list = "";
			for (int i = 0; i < Allowed.length; i++) {
				list = list + Allowed[i] + "\n";
			}

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Dateiüberwachung Liste (Erlaubt) \n (Alle Endungen kleingeschrieben! Das ist gewollt!)",
					event.getGuild().getSelfMember().getId(), event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setDescription(list);
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			EventHook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}

	public static void onoff(SlashCommandInteractionEvent event, InteractionHook EventHook) {
		try {
			Boolean stateold = Settings.DateiActive;
			String state;
			Boolean statenew;
			if (event.getSubcommandName().equals("activate")) {
				state = "Aktiv";
				statenew = true;
			} else {
				state = "Inaktiv";
				statenew = false;
			}
			if (stateold.equals(statenew)) {
				EventHook.editOriginal("Dateiüberwachung ist bereits " + state).queue();
			} else {
				Settings.set("Dateiüberwachung", "Active", statenew.toString().toLowerCase());
				EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Dateiüberwachung", event.getGuild().getSelfMember().getEffectiveName(),
						event.getGuild().getSelfMember().getEffectiveAvatarUrl());
				builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
				builder.setDescription("Dateiüberwachung jetzt " + state);
				EventHook.editOriginal("").setEmbeds(builder.build()).queue();
				builder.clear();
			}
		} catch (IOException e) {
			logger.error("IO-Fehler bei Info-Befehl", e);
			EventHook.editOriginal("IO-Fehler beim Ausführen.").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}
}
