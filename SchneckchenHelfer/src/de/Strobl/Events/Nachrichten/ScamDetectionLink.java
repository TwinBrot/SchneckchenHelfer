package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import de.Strobl.Instances.PingPauseReset;
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
		Logger logger = Main.logger;
		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
			Link = new Wini(new File(Main.Pfad + "Link.ini"));
			Guild guild = event.getGuild();
			Links = Link.get("Links");
			String Message = event.getMessage().getContentRaw();
			for (int i = 1; i <= Links.size(); i++) {
				if (Message.toLowerCase().contains(Links.get(i + "").toLowerCase())) {
					event.getMessage().delete().queue(success -> {
						try {
							String LogChannel = ini.get("Settings", "Settings.LogChannel");
							EmbedBuilder Info = new EmbedBuilder();
							Info.setDescription("Nachricht von " + event.getAuthor().getAsMention() + " gelöscht weil ein unerlaubter Link erkannt wurde");
							Info.setTimestamp(ZonedDateTime.now().toInstant());
							Info.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getAvatarUrl());
							Info.addField("UserID:", event.getAuthor().getId(), false);
							Info.addField("Nachrichten Inhalt:", Message, false);
							guild.getTextChannelById(LogChannel).sendMessageEmbeds(Info.build()).queue();
							if (!Main.PingPause) {
								Main.PingPause = true;
								guild.getTextChannelById(LogChannel).sendMessage("<@227131380058947584> <@140206875596685312>").queue();
								PingPauseReset Reset = new PingPauseReset();
								Reset.start();
							}
						} catch (Exception e) {
							logger.error("Fehler ScamDetection LogChannel", e);
						}
					}, failure -> {
						logger.error("Fehler Link Scam Protection", failure);
					});
				}
			}
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}