package de.Strobl.Events.GenericEmoteEvent;

import java.util.List;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteAdded extends ListenerAdapter {
	@Override
	public void onEmoteAdded(EmoteAddedEvent event) {
		Logger logger = Main.logger;
		try {
			Main.ServerEmotesID.clear();
			event.getGuild().retrieveEmotes().queue(GuildEmotes -> {
				List<ListedEmote> ServerEmotes = GuildEmotes;
				ServerEmotes.forEach(Emote -> {
					Main.ServerEmotesID.add(Emote.getId());
				});
			});
		} catch (Exception e) {
			logger.error("IO-Fehler", e);
		}
	}

}
