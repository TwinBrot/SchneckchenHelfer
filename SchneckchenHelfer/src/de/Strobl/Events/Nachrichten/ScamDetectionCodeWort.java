package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.time.ZonedDateTime;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScamDetectionCodeWort extends ListenerAdapter {
	public static Wini ini;

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		try {
			String message = event.getMessage().getContentRaw().toLowerCase();
			if (message.contains("free") && message.contains("http")) {
				if (message.contains("discord") || message.contains("steam") || message.contains("nitro")
						|| message.contains("cs:go") || message.contains("csgo") || message.contains("skin")) {
					ini = new Wini(new File(Main.Pfad + "settings.ini"));
					Guild guild = event.getGuild();
					EmbedBuilder Info = new EmbedBuilder();
					Info.setDescription("Nachricht von " + event.getAuthor().getAsMention() + " ~~gelöscht~~ `!!!NOCH NICHT IMPLEMENTIERT!!!`, weil Schlüsselwörter erkannt wurden!");
					Info.setTimestamp(ZonedDateTime.now().toInstant());
					Info.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
					Info.addField("UserID:", event.getAuthor().getId(), false);
					Info.addField("Nachrichten Inhalt:", message, false);
					guild.getTextChannelById(ini.get("Settings","Settings.LogChannel")).sendMessage("<@227131380058947584>").queue();
					guild.getTextChannelById(ini.get("Settings","Settings.LogChannel")).sendMessageEmbeds(Info.build()).queue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}