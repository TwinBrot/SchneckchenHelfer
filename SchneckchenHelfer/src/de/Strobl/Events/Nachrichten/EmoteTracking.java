package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmoteTracking extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Logger logger = Main.logger;
		try {
			if (event.getAuthor().isBot()) {
				return;
			}
			List<Emote> eventEmotes = event.getMessage().getEmotes();
			ArrayList<String> Emotes = new ArrayList<String>();
			for (int i = 0; eventEmotes.size() > i; i++) {
				Emotes.add(eventEmotes.get(i).getId());
			}

			Wini emoteslog = new Wini(new File(Main.Pfad + "Emotes.ini"));


			Emotes.forEach(emote -> {
				try {
					if (Main.ServerEmotesID.contains(emote)) {
						try {
							emoteslog.put("Emotes", emote, Integer.valueOf(emoteslog.get("Emotes", emote)) + 1);
						} catch (Exception e) {
							emoteslog.put("Emotes", emote, 1);
						}
						try {
							emoteslog.store();
						} catch (Exception e) {
							logger.error("Konnte Emotes.ini nicht beschreiben!", e);
						}
					} else {
					}
				} catch (NullPointerException e) {
					logger.error("NullPointerException beim EmoteTracking. "
							+ "(Mögliche Ursache: Bot neu gestartet. Emote wurde abgeschickt, bevor die Emoteliste erstellt wurde)", e);
				}
			});


		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler Emotezählung:", e);
		}
	}
}