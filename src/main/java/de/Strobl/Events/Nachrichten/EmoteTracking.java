package de.Strobl.Events.Nachrichten;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.Instances.SQL;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteTracking extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(EmoteTracking.class);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.isFromGuild()) {
			return;
		}
		try {
			if (event.getAuthor().isBot()) {
				return;
			}
			List<CustomEmoji> eventEmotes = event.getMessage().getMentions().getCustomEmojis();
			ArrayList<String> Emotes = new ArrayList<String>();
			for (int i = 0; eventEmotes.size() > i; i++) {
				Emotes.add(eventEmotes.get(i).getId());
			}

			if (Main.ServerEmotesID == null) {
				return;
			}

			Emotes.forEach(emote -> {
				try {
					if (Main.ServerEmotesID.contains(emote)) {
						SQL.emoteup(emote);
					}
				} catch (SQLException e) {
					logger.error("Unbekannter Fehler beim Emotecounter erhöhen:", e);
				}
			});

		} catch (Exception e) {
			logger.error("Fehler Emotezählung:", e);
		}
	}
}