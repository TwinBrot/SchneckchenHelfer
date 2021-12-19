package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class ScamDetection extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ScamDetection.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		CodeWort(event);
		Link(event);
	}

	private void CodeWort(MessageReceivedEvent event) {
		try {
			String message = event.getMessage().getContentRaw().toLowerCase();
			if (message.contains("http")) {
				if (message.contains("@everyone")) {
					Instance(event, "Nachricht gelöscht! Schlüsselwörter erkannt!");
					return;
				}
				if (message.contains("free") || message.contains("gift") || message.contains("trade") || message.contains("distrib") || message.contains("hack") || message.contains("money")
						|| message.contains("installer") || message.contains("giving") || message.contains("over") || message.contains("give")) {
					if (message.contains("disc") || message.contains("steam") || message.contains("nitro") || message.contains("cs:go") || message.contains("boost") || message.contains("csgo")
							|| message.contains("valorant") || message.contains("skin") || message.contains("Game") || message.contains("@everyone")) {
						Instance(event, "Nachricht gelöscht! Schlüsselwörter erkannt!");
						return;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Fehler ScamDetection", e);
		}
	}

	private void Link(MessageReceivedEvent event) {
		try {
			Wini Link = new Wini(new File(Main.Pfad + "Link.ini"));
			Section Links = Link.get("Links");
			for (int i = 1; i <= Links.size(); i++) {
				if (event.getMessage().getContentRaw().toLowerCase().contains(Links.get(i + "").toLowerCase())) {
					Instance(event, "Nachricht gelöscht! Unerlaubter Link erkannt!");
				}
			}
		} catch (Exception e) {
			logger.error("Fehler ScamDetection", e);
		}
	}

	private void Instance(MessageReceivedEvent event, String title) {
		try {
			String message = event.getMessage().getContentRaw().toLowerCase();
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			event.getMessage().delete().queue(success -> {
				try {
					String LogChannel = ini.get("Settings", "LogChannel");
					Guild guild = event.getGuild();
					EmbedBuilder builder = Discord.standardEmbed(Color.red, title, event.getMember().getId(), event.getMember().getEffectiveAvatarUrl());
					builder.addField("Nachrichten Inhalt:", message, false);
					guild.getTextChannelById(LogChannel).sendMessage("User: " + event.getMember().getAsMention() + " Notification: <@227131380058947584> <@140206875596685312>")
							.setEmbeds(builder.build()).setActionRow(Button.danger("ban " + event.getMember().getId(), "Ban User")).queue();
					builder.clear();
				} catch (Exception e) {
					logger.error("Fehler ScamDetection LogChannel", e);
				}
			}, failure -> {
				logger.error("Fehler ScamDetection Ban", failure);
			});
		} catch (Exception e) {
			logger.error("Fehler ScamDetection", e);
		}
	}
}