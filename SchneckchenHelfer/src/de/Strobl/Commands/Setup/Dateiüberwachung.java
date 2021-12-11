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

public class Dateiüberwachung {
	private static final Logger logger = LogManager.getLogger(Dateiüberwachung.class);
	public static void add(SlashCommandEvent event, InteractionHook EventHook) {
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
				};
				list = list + Allowed[i] + "\n";
			}
			list = list + newname;
			if (ini.get("Dateiüberwachung", "Allowed") == null || ini.get("Dateiüberwachung", "Allowed").equals("")) {
				ini.put("Dateiüberwachung", "Allowed", newname);
			} else {
				ini.put("Dateiüberwachung", "Allowed", ini.get("Dateiüberwachung", "Allowed") + ", " + newname);
			}
			ini.store();
			EmbedBuilder List = new EmbedBuilder();
			List.setAuthor("Dateiüberwachung Liste (Erlaubt):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
			List.setDescription(list);
			List.setTitle("(Alle Dateien kleingeschrieben! Das ist gewollt!)");
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
			String[] Allowed = ini.get("Dateiüberwachung", "Allowed").split(",\\s+");
			String oldname = event.getOptionsByName("name").get(0).getAsString().toLowerCase().replaceAll("\\s+", "");
			String list = "";
			Boolean vorhanden = false;
			for (int i = 0; i < Allowed.length; i++) {
				if (!Allowed[i].equals(oldname)) {
					if (i==0) {
						list = list + Allowed[i];
					} else {
						list = list + "\n" + Allowed[i];
					}
				} else {
					vorhanden = true;
				};
			}
			if (!vorhanden) {
				EventHook.editOriginal("Name war nicht erlaubt!").queue();
				return;
			}
			ini.put("Dateiüberwachung", "Allowed", list.replaceAll("\n", ", "));
			ini.store();
			
			EmbedBuilder List = new EmbedBuilder();
			List.setAuthor("Dateiüberwachung Liste (Erlaubt):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
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
			String[] Allowed = ini.get("Dateiüberwachung", "Allowed").split(",\\s+");
			EmbedBuilder List = new EmbedBuilder();

			List.setAuthor("Dateiüberwachung Liste (Erlaubt):", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());

			String list = "";
			for (int i = 0; i < Allowed.length; i++) {
				list = list + Allowed[i] + "\n";
			}
			List.setDescription(list);
			List.setTitle("(Alle Endungen kleingeschrieben! Das ist gewollt!)");
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

			String stateold = ini.get("Dateiüberwachung", "Active");
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
				EventHook.editOriginal("Dateiüberwachung ist bereits " + state).queue();
			} else {
				ini.put("Dateiüberwachung", "Active", statenew);
				ini.store();
				EmbedBuilder Response = new EmbedBuilder();
				Response.setAuthor("Dateiüberwachung", event.getGuild().getIconUrl(), event.getGuild().getIconUrl());
				Response.setDescription("Dateiüberwachung jetzt " + state);
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

