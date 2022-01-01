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
			String m = event.getMessage().getContentRaw().toLowerCase();
			if (m.contains("http")) {
				if (m.contains("@everyone")) {
					Instance(event, "Nachricht gelöscht! Schlüsselwörter erkannt!");
					return;
				}
				if (m.contains("free") || m.contains("gift") || m.contains("trade") || m.contains("distrib") || m.contains("hack") || m.contains("money")
						|| m.contains("installer") || m.contains("giving") || m.contains("over") || m.contains("give")) {
					if (m.contains("disc") || m.contains("steam") || m.contains("nitro") || m.contains("cs:go") || m.contains("boost") || m.contains("csgo")
							|| m.contains("valorant") || m.contains("skin") || m.contains("Game") || m.contains("@everyone")) {
						
						if (m.contains("https://discord.gift/")) {
							logger.info("Discord-Nitro Geschenk erkannt: " + m);
							return;
						}
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
			String m = event.getMessage().getContentRaw().toLowerCase();
			event.getMessage().delete().queue(success -> {
				try {
					Guild guild = event.getGuild();
					EmbedBuilder builder = Discord.standardEmbed(Color.red, title, event.getMember().getId(), event.getMember().getEffectiveAvatarUrl());
					builder.addField("Nachrichten Inhalt:", m, false);
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