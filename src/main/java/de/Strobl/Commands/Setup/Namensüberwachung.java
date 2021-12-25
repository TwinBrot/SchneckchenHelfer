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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Namensüberwachung {
	private static final Logger logger = LogManager.getLogger(Namensüberwachung.class);

	public static void add(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Verboten = Settings.Namen;
			String newname = event.getOptionsByName("name").get(0).getAsString().toLowerCase().replaceAll("\\s+", "");
			if (newname.contains("")) {
				EventHook.editOriginal("Fehler verhindert. Zeichen '' sorgt für Fehler im Programm.").queue();
				return;
			}
			String list = "";
			for (int i = 0; i < Verboten.length; i++) {
				if (Verboten[i].equals(newname)) {
					EventHook.editOriginal("Name bereits verboten!").queue();
					return;
				}
				;
				list = list + Verboten[i] + "\n";
			}
			list = list + newname;
			if (ini.get("Namensüberwachung", "Verboten") == null || ini.get("Namensüberwachung", "Verboten").equals("")) {
				ini.put("Namensüberwachung", "Verboten", newname);
			} else {
				ini.put("Namensüberwachung", "Verboten", ini.get("Namensüberwachung", "Verboten") + ", " + newname);
			}
			ini.store();
			Settings.load();
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Namensüberwachung Liste (Verboten) \n (Alle Namen kleingeschrieben! Das ist gewollt!)", event.getGuild().getSelfMember().getId(),
					event.getGuild().getSelfMember().getEffectiveAvatarUrl());
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

	public static void remove(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Verboten = ini.get("Namensüberwachung", "Verboten").split(",\\s+");
			String oldname = event.getOptionsByName("name").get(0).getAsString().toLowerCase().replaceAll("\\s+", "");
			String list = "";
			Boolean vorhanden = false;
			for (int i = 0; i < Verboten.length; i++) {
				if (!Verboten[i].equals(oldname)) {
					if (i == 0) {
						list = list + Verboten[i];
					} else {
						list = list + "\n" + Verboten[i];
					}
				} else {
					vorhanden = true;
				}
				;
			}
			if (!vorhanden) {
				EventHook.editOriginal("Name war nicht verboten!").queue();
				return;
			}
			ini.put("Namensüberwachung", "Verboten", list.replaceAll("\n", ", "));
			ini.store();
			Settings.load();
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Namensüberwachung Liste (verboten) \n (Alle Namen kleingeschrieben! Das ist gewollt!)", event.getGuild().getSelfMember().getId(),
					event.getGuild().getSelfMember().getEffectiveAvatarUrl());
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

	public static void list(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			String[] Verboten = Settings.Namen;
			String list = "";
			for (int i = 0; i < Verboten.length; i++) {
				list = list + Verboten[i] + "\n";
			}
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Namensüberwachung Liste (Verboten) \n (Alle Namen kleingeschrieben! Das ist gewollt!)", event.getGuild().getSelfMember().getId(),
					event.getGuild().getSelfMember().getEffectiveAvatarUrl());
			builder.setDescription(list);
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			EventHook.editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();

		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}

	public static void onoff(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			Boolean stateold = Settings.NamenActive;
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
				EventHook.editOriginal("Namensüberwachung ist bereits " + state).queue();
			} else {
				Settings.set("Namensüberwachung", "Active", statenew.toString().toLowerCase());
				EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Namensüberwachung", event.getGuild().getSelfMember().getEffectiveName(),
						event.getGuild().getSelfMember().getEffectiveAvatarUrl());
				builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
				builder.setDescription("Namensüberwachung jetzt " + state);
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
