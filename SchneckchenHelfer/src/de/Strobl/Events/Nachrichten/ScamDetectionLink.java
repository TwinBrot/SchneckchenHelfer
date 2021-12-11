package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScamDetectionLink extends ListenerAdapter {
	public Wini ini;
	public Wini Link;
	public Section Links;
	private static final Logger logger = LogManager.getLogger(ScamDetectionLink.class);
	public void onGuildMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
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
					guild.getTextChannelById(ini.get("Settings","Settings.LogChannel")).sendMessage("<@227131380058947584> <@140206875596685312>").setEmbeds(Info.build()).queue();
					Info.clear();
				}
			}
		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}