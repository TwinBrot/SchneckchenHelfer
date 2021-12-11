package de.Strobl.Instances;

import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.EmbedBuilder;

public class Discord {
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Discord.class);

	public static EmbedBuilder StandartEmbed(String title, String serverIcon, String author, String authorIcon) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(title, null, serverIcon);
		builder.setFooter(author, authorIcon);
		builder.setTimestamp(ZonedDateTime.now().toInstant());
		return builder;
	}
}