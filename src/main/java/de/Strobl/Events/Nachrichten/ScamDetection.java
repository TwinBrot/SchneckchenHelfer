package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
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
			ArrayList<String> Links = Settings.Links;
			for (int i = 0; i < Links.size(); i++) {
				if (event.getMessage().getContentRaw().toLowerCase().contains(Links.get(i).toLowerCase())) {
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
			event.getMessage().delete().queue(success -> {
				try {
					Guild guild = event.getGuild();
					EmbedBuilder builder = Discord.standardEmbed(Color.red, title, event.getMember().getId(), event.getMember().getEffectiveAvatarUrl());
					builder.addField("Nachrichten Inhalt:", message, false);
					guild.getTextChannelById(Settings.LogChannel).sendMessage("User: " + event.getMember().getAsMention() + " Notification: <@227131380058947584> <@140206875596685312> <@81796365214023680>")
							.setEmbeds(builder.build()).setActionRow(Button.danger("ban " + event.getMember().getId(), "Ban User")).queue();
					builder.clear();
				} catch (Exception e) {
					logger.error("Fehler ScamDetection LogChannel", e);
				}
			}, e -> {
				if (!e.getClass().getName().equals("net.dv8tion.jda.api.exceptions.ErrorResponseException")) {
					logger.error("Fehler ScamDetection Ban", e);
				}
			});
		} catch (Exception e) {
			logger.error("Fehler ScamDetection", e);
		}
	}
}