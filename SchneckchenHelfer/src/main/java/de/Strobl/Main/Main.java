package de.Strobl.Main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import de.Strobl.Events.EmoteEvent;
import de.Strobl.Events.Nachrichten.EmoteTracking;
import de.Strobl.Events.Nachrichten.Filechecker;
import de.Strobl.Events.Nachrichten.ReactionRemoveLog;
import de.Strobl.Events.Nachrichten.ScamDetectionCodeWort;
import de.Strobl.Events.Nachrichten.ScamDetectionLink;
import de.Strobl.Events.User.BotIsOfflineAlarm;
import de.Strobl.Events.User.JoinNamensüberwachung;
import de.Strobl.Events.User.OnuserUpdateNameEvent;
import de.Strobl.Instances.SQL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	private static String version = "1.7.0";
	public static List<String> ServerEmotesID;
	public static JDA jda;
	public static String Pfad = "./";

	public static void main(String[] arguments) {
		try {
// Update für den Bot verfügbar?
			GitHub github = new GitHubBuilder().withOAuthToken("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").build();
			String neusteversion = github.getRepository("TwinBrot/Schneckchencord").getLatestRelease().getTagName();
			if (!version.equals(neusteversion)) {
				logger.warn("Dein Bot läuft nicht auf der neusten Stable Version. Ich empfehle zu Updaten.");
			}

//Settings.ini und SQL starten
			Settings.Update();
			SQL.initialize();

//JDA Builder

			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			JDABuilder Builder = JDABuilder.createDefault(ini.get("Setup", "Token"));
			Builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES,
					GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MEMBERS);
			Builder.disableIntents(GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_INVITES,
					GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_REACTIONS);
			Builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES,
					CacheFlag.ROLE_TAGS, CacheFlag.EMOTE);
			Builder.disableCache(CacheFlag.ONLINE_STATUS);
			Builder.setChunkingFilter(ChunkingFilter.NONE);
			Builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			Builder.setAutoReconnect(true);

//Event Listener

			Builder.addEventListeners(new SlashCommand());
			Builder.addEventListeners(new MessageReceived());
			Builder.addEventListeners(new ScamDetectionCodeWort());
			Builder.addEventListeners(new ScamDetectionLink());
			Builder.addEventListeners(new BotIsOfflineAlarm());
			Builder.addEventListeners(new OnuserUpdateNameEvent());
			Builder.addEventListeners(new JoinNamensüberwachung());
			Builder.addEventListeners(new ReactionRemoveLog());
			Builder.addEventListeners(new Filechecker());
			Builder.addEventListeners(new EmoteTracking());
			Builder.addEventListeners(new EmoteEvent());

//Activity

			String Typ = ini.get("Settings", "Settings.AktivitätTyp");
			String Text = ini.get("Settings", "Settings.AktivitätText");
			String URL = ini.get("Settings", "Settings.StreamLink");

			switch (Typ) {
			case "playing":
				Builder.setActivity(Activity.playing(Text));
				break;
			case "listening":
				Builder.setActivity(Activity.listening(Text));
				break;
			case "watching":
				Builder.setActivity(Activity.watching(Text));
				break;
			case "streaming":
				Builder.setActivity(Activity.streaming(Text, URL));
				break;
			}

//Status

			switch (ini.get("Settings", "Settings.Status")) {
			case "ONLINE":
				Builder.setStatus(OnlineStatus.ONLINE);
				break;
			case "IDLE":
				Builder.setStatus(OnlineStatus.IDLE);
				break;
			case "DO_NOT_DISTURB":
				Builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
				break;
			case "INVISIBLE":
				Builder.setStatus(OnlineStatus.INVISIBLE);
				break;
			}

//JDA Starten und fertigstellung abwarten

			jda = Builder.build().awaitReady();

//Befehle anmelden

			SlashCommand.startupcheck(jda);

//Loops starten

			ScheduledExecutorService Loops = Executors.newScheduledThreadPool(1);
			Loops.scheduleAtFixedRate(new LoopCheckTemp(), 10, 60, TimeUnit.SECONDS);

//Cache Emotes

			jda.getGuilds().get(0).retrieveEmotes().queue(GuildEmotes -> {
				ServerEmotesID = new ArrayList<String>();
				GuildEmotes.forEach(Emote -> {
					ServerEmotesID.add(Emote.getId());
				});
			});

//Fehler Management

		} catch (IllegalStateException e) {
			logger.fatal(
					"IllegalStateException - 'Presence Intent' und 'Server Members Intent' im Discord Developer Portal aktiviert? ",
					e);

		} catch (IOException e) {
			logger.fatal("IOException - Hat der Bot Berechtigungen, um Dateien zu erstellen?", e);

		} catch (SQLException e) {
			logger.fatal("SQL-Fehler", e);

		} catch (LoginException | InterruptedException e) {
			logger.fatal("Fehler beim Initialisieren des Bots. Ist der Token richtig:", e);

		}
	}
}
