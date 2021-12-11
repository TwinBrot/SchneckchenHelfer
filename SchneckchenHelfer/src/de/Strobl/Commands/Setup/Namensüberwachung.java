package de.Strobl.Commands.Setup;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Namensüberwachung {
	private static final Logger logger = LogManager.getLogger(Namensüberwachung.class);
	public static void add(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Verboten = ini.get("Namensüberwachung", "Verboten").split(",\\s+");
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
				};
				list = list + Verboten[i] + "\n";
			}
			list = list + newname;
			if (ini.get("Namensüberwachung", "Verboten") == null || ini.get("Namensüberwachung", "Verboten").equals("")) {
				ini.put("Namensüberwachung", "Verboten", newname);
			} else {
				ini.put("Namensüberwachung", "Verboten", ini.get("Namensüberwachung", "Verboten") + ", " + newname);
			}
			ini.store();
			EmbedBuilder List = new EmbedBuilder();
			List.setAuthor("Namensüberwachung Liste (Verboten):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			List.setDescription(list);
			List.setTitle("(Alle Namen kleingeschrieben! Das ist gewollt!)");
			List.addField("Hinzugefügt: ", newname,true);
			List.setFooter("Eingestellt von: " + event.getMember().getEffectiveName());
			List.setTimestamp(ZonedDateTime.now().toInstant());
			event.getTextChannel().sendMessageEmbeds(List.build()).queue();
			EventHook.editOriginal("Erfolg.").queue();
			List.clear();
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
					if (i==0) {
						list = list + Verboten[i];
					} else {
						list = list + "\n" + Verboten[i];
					}
				} else {
					vorhanden = true;
				};
			}
			if (!vorhanden) {
				EventHook.editOriginal("Name war nicht verboten!").queue();
				return;
			}
			ini.put("Namensüberwachung", "Verboten", list.replaceAll("\n", ", "));
			ini.store();
			
			EmbedBuilder List = new EmbedBuilder();
			List.setAuthor("Namensüberwachung Liste (Verboten):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			List.setDescription(list);
			List.setTitle("(Alle Namen kleingeschrieben! Das ist gewollt!)");
			List.addField("Entfernt: ", oldname,true);
			List.setFooter("Eingestellt von: " + event.getMember().getEffectiveName());
			List.setTimestamp(ZonedDateTime.now().toInstant());
			event.getTextChannel().sendMessageEmbeds(List.build()).queue();
			EventHook.editOriginal("Erfolg.").queue();
			List.clear();
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
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String[] Verboten = ini.get("Namensüberwachung", "Verboten").split(",\\s+");
			EmbedBuilder List = new EmbedBuilder();

			List.setAuthor("Namensüberwachung Liste (Verboten):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());

			String list = "";
			for (int i = 0; i < Verboten.length; i++) {
				list = list + Verboten[i] + "\n";
			}
			List.setDescription(list);
			List.setTitle("(Alle Namen kleingeschrieben! Das ist gewollt!)");
			List.setFooter("Angefragt von: " + event.getMember().getEffectiveName());
			List.setTimestamp(ZonedDateTime.now().toInstant());
			event.getTextChannel().sendMessageEmbeds(List.build()).queue();
			EventHook.editOriginal("Erfolg.").queue();
			List.clear();
		} catch (IOException e) {
			logger.error("IO-Fehler bei Info-Befehl", e);
			EventHook.editOriginal("IO-Fehler beim Ausführen.").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}
	}

	public static void onoff(SlashCommandEvent event, InteractionHook EventHook) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));

			String stateold = ini.get("Namensüberwachung", "Active");
			String state;
			String statenew;
			if (event.getSubcommandName().equals("activate")) {
				state = "Aktiv";
				statenew = "true";
			} else {
				state = "Inaktiv";
				statenew = "false";
			}
			if (stateold.equals(statenew)) {
				EventHook.editOriginal("Namensüberwachung ist bereits " + state).queue();
			} else {
				ini.put("Namensüberwachung", "Active", statenew);
				ini.store();
				EmbedBuilder Response = new EmbedBuilder();
				Response.setAuthor("Namensüberwachung", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
				Response.setDescription("Namensüberwachung jetzt " + state);
				Response.setFooter("Eingestellt von: " + event.getMember().getEffectiveName());
				Response.setTimestamp(ZonedDateTime.now().toInstant());
				event.getTextChannel().sendMessageEmbeds(Response.build()).queue();
				EventHook.editOriginal("Erfolgreich").queue();
				;
				Response.clear();
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
