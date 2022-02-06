package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
				if (m.contains("free") || m.contains("gift") || m.contains("trade") || m.contains("distrib") || m.contains("hack") || m.contains("money") || m.contains("installer")
						|| m.contains("giving") || m.contains("over") || m.contains("give") || m.contains("drop")) {
					if (m.contains("disc") || m.contains("steam") || m.contains("nitro") || m.contains("cs:go") || m.contains("boost") || m.contains("csgo") || m.contains("valorant")
							|| m.contains("skin") || m.contains("game")) {

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

			if (Discord.isMod(event.getMember()) > 0) {
				logger.info("Scam erkannt. Author ist Mod, daher nicht gelöscht.");
				return;
			}
			Member member = event.getMember();
			String m = event.getMessage().getContentRaw().toLowerCase();
			event.getMessage().delete().queue(success -> {
				try {
					Guild guild = event.getGuild();
					EmbedBuilder builder = Discord.standardEmbed(Color.red, title, member.getId(), member.getEffectiveAvatarUrl());
					builder.addField("Nachrichten Inhalt:", m, false);
					guild.getTextChannelById(Settings.LogChannel).sendMessage("User: " + member.getAsMention() + " Notification: <@227131380058947584> <@140206875596685312>")
							.setEmbeds(builder.build()).setActionRow(Button.danger("ban " + member.getId(), "Ban User")).queue();
					builder.clear();
				} catch (Exception e) {
					logger.error("Fehler ScamDetection LogChannel", e);
				}
			}, e -> {
				if (!e.getClass().getName().equals("net.dv8tion.jda.api.exceptions.ErrorResponseException")) {
					logger.error("Fehler ScamDetection Ban", e);
				}
			});
			member.getUser().openPrivateChannel().queue(pc -> {
				EmbedBuilder builder = Discord.standardEmbed(Color.red, "TimeOut", member.getId(), member.getUser().getEffectiveAvatarUrl());
				builder.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
				builder.setDescription("Dein Account wurde gerade für 10 Minuten getimeoutet!");
				builder.addField("Grund:", "Verdacht auf Scam! \n Die Moderatoren werden den Verdacht kontrollieren. ", true);
				builder.addField("Wenn sich der Verdacht bestätigt:", "Sollte sich der Verdacht bestätigen, wird dein Account temporär vom Server gebannt.", false);
				builder.addField("Wenn sich der Verdacht NICHT bestätigt:",
						"Sollte eine Nachricht fälschlicherweise als Scam erkannt werden, so musst du nichts unternehmen. Die Mods werden dich wieder freischalten.", false);
				pc.sendMessageEmbeds(builder.build()).queue(x -> {
					member.timeoutFor(10, TimeUnit.MINUTES).queue(success -> {
					}, e -> {
					});
				});
			});
		} catch (Exception e) {
			logger.error("Fehler ScamDetection", e);
		}
	}
}