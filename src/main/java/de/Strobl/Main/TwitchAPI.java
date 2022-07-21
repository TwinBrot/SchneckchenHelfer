package de.Strobl.Main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

public class TwitchAPI {
	private static final Logger logger = LogManager.getLogger(TwitchAPI.class);
	private static JDA jda = Main.jda;

	public static void twitchAPI() {
		try {
			Boolean id = Settings.twitchid.equals("");
			Boolean secret = Settings.twitchsecret.equals("");
			Boolean streamer = Settings.streamer.equals("");
			if (id || secret || streamer) {
				logger.warn("Nicht alle Twitch-Details eingetragen. Ohne Vollständige Daten, kann keine automatische Benachrichtigung gesendet werden!");
				logger.warn("Sobald alle Daten eingetragen sind, muss der Bot neugestartet werden!");
				return;
			}
			TwitchClientBuilder builder = TwitchClientBuilder.builder();
			TwitchClient twitchClient = builder.withEnableHelix(true).withClientId(Settings.twitchid).withClientSecret(Settings.twitchsecret).build();
			twitchClient.getClientHelper().enableStreamEventListener(Settings.streamer);
			twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
				jda.getPresence().setActivity(Activity.streaming(event.getStream().getTitle(), "https://www.twitch.tv/" + streamer));
				try {
					if (Settings.AnnounceChannel.equals("")) {
						logger.warn("Kein Announcement Channel festgelegt!");
						return;
					}
					String name = event.getChannel().getName();
					TextChannel textchannel = jda.getChannelById(TextChannel.class, Settings.AnnounceChannel);
					textchannel.sendMessage("<@&789928074417668096> " + name + " ist jetzt wieder live! https://www.twitch.tv/" + name).queue();
				} catch (Exception e) {
					logger.fatal("Fehler bei Stream-Start Alarm", e);
				}
			});
			twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
				// Activity

				String Typ = Settings.AktivitätTyp;
				String Text = Settings.AktivitätText;

				switch (Typ) {
				case "playing":
					jda.getPresence().setActivity(Activity.playing(Text));
					break;
				case "listening":
					jda.getPresence().setActivity(Activity.listening(Text));
					break;
				case "watching":
					jda.getPresence().setActivity(Activity.watching(Text));
					break;
				}
			});
		} catch (Exception e) {
			logger.fatal("Fehler bei Twitch Anmeldung", e);
		}
	}
}