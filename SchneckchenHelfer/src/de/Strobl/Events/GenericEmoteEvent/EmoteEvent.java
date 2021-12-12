package de.Strobl.Events.GenericEmoteEvent;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteEvent extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(EmoteEvent.class);
	@Override
	public void onGenericEmote(GenericEmoteEvent event) {
		try {
			event.getGuild().retrieveEmotes().queue(GuildEmotes -> {
				List<ListedEmote> ServerEmotes = GuildEmotes;
				Main.ServerEmotesID.clear();
				ServerEmotes.forEach(Emote -> {
					Main.ServerEmotesID.add(Emote.getId());
				});
			});
		} catch (Exception e) {
			logger.error("Fehler Emoteliste aktuallisieren", e);
		}
	}
}
