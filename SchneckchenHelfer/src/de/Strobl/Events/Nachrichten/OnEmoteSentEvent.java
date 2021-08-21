package de.Strobl.Events.Nachrichten;

import java.io.File;
import java.util.ArrayList;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnEmoteSentEvent extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		try {
//			event.getGuild().retrieveEmotes().queue(GuildEmotes -> {
//				List<ListedEmote> ServerEmotes = GuildEmotes;
//				List<String> ServerEmotesID = new ArrayList<String>();
//				ServerEmotes.forEach(Emote -> {
//					ServerEmotesID.add(Emote.getId());
//				});
				ArrayList<String> Emotes = new ArrayList<String>();
				for (int i = 0; event.getMessage().getEmotes().size() > i; i++) {
					Emotes.add(event.getMessage().getEmotes().get(i).getId());
				}
				Wini emoteslog = null;
				try {
					emoteslog = new Wini(new File(Main.Pfad + "Emotes.ini"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; Emotes.size() > i; i++) {
					if (Main.ServerEmotesID.contains(Emotes.get(i)) && !event.getAuthor().isBot()) {
						try {
							emoteslog.put("Emotes", Emotes.get(i),
									Integer.valueOf(emoteslog.get("Emotes", Emotes.get(i))) + 1);
						} catch (Exception e) {
							emoteslog.put("Emotes", Emotes.get(i), 1);
						}
						try {
							emoteslog.store();
						} catch (Exception e) {
							System.out.println("Konnte Emotes.ini nicht beschreiben!");
						}
					} else {
					}
				}

//			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}