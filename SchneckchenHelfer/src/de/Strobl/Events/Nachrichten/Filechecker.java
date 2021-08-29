package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Filechecker extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Logger logger = Main.logger;
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Boolean inactive = !ini.get("Dateiüberwachung", "Active").equals("true");
			Boolean Bot = event.getAuthor().isBot();
			Boolean NoFile = event.getMessage().getAttachments().isEmpty();

			if (inactive) {
				return;
			} else if (Bot) {
				return;
			} else if (NoFile) {
				return;
			}

			String[] Endungen = ini.get("Dateiüberwachung", "Allowed").replaceAll("\\s", "").split(",");
			ArrayList<String> DateiEndungen = new ArrayList<String>();
			for (int i = 0; i < Endungen.length; i++) {
				DateiEndungen.add(i, Endungen[i]);
			}

			List<Attachment> Dateien = event.getMessage().getAttachments();
			for (int i = 0; i < Dateien.size(); i++) {
				if (!DateiEndungen.contains(Dateien.get(i).getFileExtension().toLowerCase())) {
					event.getMessage().delete().queue(success -> {
						String ID = ini.get("Settings", "Settings.LogChannel");
						EmbedBuilder Dateigelöscht = new EmbedBuilder();
						Dateigelöscht.setColor(0xd41406);
						Dateigelöscht.setAuthor("Nachricht mit Datei gelöscht", event.getGuild().getIconUrl(),
								event.getGuild().getIconUrl());
						Dateigelöscht.addField("User:",
								event.getAuthor().getAsMention() + "             " + event.getAuthor().getName(),
								false);
						Dateigelöscht.addField("Channel:", event.getChannel().getAsMention(), true);
						Dateigelöscht.addField("DateiName:", event.getMessage().getAttachments().get(0).getFileName(),
								true);
						event.getGuild().getTextChannelById(ID).sendMessageEmbeds(Dateigelöscht.build()).queue();
						event.getGuild().getTextChannelById(ID).sendMessage("<@227131380058947584>").queue();
					}, failure -> {
						logger.error("Fehler bei der Dateiüberwachung", failure);
					});
					return;
				}
			}
		} catch (IOException e) {
			logger.error("IO-Fehler Dateiüberwachung", e);
		} catch (Exception e) {
			logger.error("Fehler Dateiüberwachung:", e);
		}
	}
}