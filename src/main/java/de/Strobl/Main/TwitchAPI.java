package de.Strobl.Main;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;

import net.dv8tion.jda.api.EmbedBuilder;
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
			TwitchClient twitchClient = builder.withTimeout(10000).withEnableHelix(true).withClientId(Settings.twitchid).withClientSecret(Settings.twitchsecret)
					.build();
			twitchClient.getClientHelper().enableStreamEventListener(Settings.streamer);
			twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, event -> {
				Stream stream = event.getStream();
				EventChannel channel = event.getChannel();
				jda.getPresence().setActivity(Activity.streaming(stream.getTitle(), "https://www.twitch.tv/" + channel.getName()));
				logger.info("Registered GoLiveEvent: " + channel.getName() + " with game " + stream.getGameName());
				try {
					if (Settings.AnnounceChannel.equals("")) {
						logger.warn("Kein Announcement Channel festgelegt!");
						return;
					}
					TextChannel textchannel = jda.getChannelById(TextChannel.class, Settings.AnnounceChannel);
					if (!textchannel.canTalk()) {
						logger.warn("Kein Rechte im Announcement Channel zu schreiben!");
						return;
					}

					jda.retrieveUserById(109777843046645760L).queue(owner -> {
						String name = channel.getName();
						EmbedBuilder embedbuilder = new EmbedBuilder();
						embedbuilder.setColor(0x6441a5);
						embedbuilder.setAuthor(owner.getName(), null, owner.getEffectiveAvatarUrl());
						embedbuilder.setTitle(stream.getTitle(), "https://www.twitch.tv/" + name);
						embedbuilder.addField("Game:", stream.getGameName(), false);
						logger.warn("URL: " + stream.getThumbnailUrl());
						embedbuilder.setImage(stream.getThumbnailUrl());
						embedbuilder.setTimestamp(ZonedDateTime.now().toInstant());
						embedbuilder.setFooter("Twitch", "https://pingcord.xyz/assets/twitch-footer.png");
						textchannel.sendMessage("<@&789928074417668096> ").setEmbeds(embedbuilder.build()).queue();
					});

				} catch (Exception e) {
					logger.fatal("Fehler bei Stream-Start Alarm", e);
				}
			});
			twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, event -> {
				logger.info("Registered GoOfflineEvent: " + event.getChannel().getName());

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