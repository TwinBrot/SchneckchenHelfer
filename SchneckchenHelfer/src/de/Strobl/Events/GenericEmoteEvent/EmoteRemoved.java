package de.Strobl.Events.GenericEmoteEvent;

import java.util.List;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteRemoved extends ListenerAdapter {
	@Override
	public void onEmoteRemoved(EmoteRemovedEvent event) {
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
