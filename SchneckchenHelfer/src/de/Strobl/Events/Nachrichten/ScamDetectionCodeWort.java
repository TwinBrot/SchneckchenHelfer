package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScamDetectionCodeWort extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ScamDetectionCodeWort.class);
	public void onGuildMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		try {
			String message = event.getMessage().getContentRaw().toLowerCase();
			if (message.contains("http")) {
				if (message.contains("free") || message.contains("gift") || message.contains("trade") || message.contains("distrib")
						|| message.contains("hack") || message.contains("money") || message.contains("installer") || message.contains("giving")) {
					if (message.contains("disc") || message.contains("steam") || message.contains("nitro") || message.contains("cs:go")
							|| message.contains("boost") || message.contains("csgo") || message.contains("valorant") || message.contains("skin")
							|| message.contains("Game") || message.contains("@everyone")) {
						Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
						event.getMessage().delete().queue(success -> {
							try {
								String LogChannel = ini.get("Settings", "Settings.LogChannel");
								Guild guild = event.getGuild();
								EmbedBuilder Info = new EmbedBuilder();
								Info.setDescription("Nachricht von " + event.getAuthor().getAsMention() + " gelöscht! Schlüsselwörter erkannt!");
								Info.setTimestamp(ZonedDateTime.now().toInstant());
								Info.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
								Info.addField("UserID:", event.getAuthor().getId(), false);
								Info.addField("Nachrichten Inhalt:", message, false);
								guild.getTextChannelById(LogChannel).sendMessage("<@227131380058947584> <@140206875596685312>").setEmbeds(Info.build()).queue();
								Info.clear();
							} catch (Exception e) {
								logger.error("Fehler ScamDetection LogChannel", e);
							}
						}, failure -> {
							logger.error("Fehler ScamDetection", failure);
						});
					}
				}
			}
		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}