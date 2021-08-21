package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.time.ZonedDateTime;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScamDetectionLink extends ListenerAdapter {
	public Wini ini;
	public Wini Link;
	public Section Links;

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Link = new Wini(new File(Main.Pfad + "Link.ini"));
			Guild guild = event.getGuild();
			Links = Link.get("Links");
			String Message = event.getMessage().getContentRaw();
			for (int i = 1; i <= Links.size(); i++) {
				if (Message.toLowerCase().contains(Links.get(i + "").toLowerCase())) {
					event.getMessage().delete().queue();
					EmbedBuilder Info = new EmbedBuilder();
					Info.setDescription("Nachricht von " + event.getAuthor().getAsMention() + " gelöscht weil ein unerlaubter Link erkannt wurde");
					Info.setTimestamp(ZonedDateTime.now().toInstant());
					Info.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
					Info.addField("UserID:", event.getAuthor().getId(), false);
					Info.addField("Nachrichten Inhalt:", Message, false);
					guild.getTextChannelById(ini.get("Settings","Settings.LogChannel")).sendMessage("<@227131380058947584>").queue();
					guild.getTextChannelById(ini.get("Settings","Settings.LogChannel")).sendMessageEmbeds(Info.build()).queue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}