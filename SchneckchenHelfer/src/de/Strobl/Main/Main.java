package de.Strobl.Main;

import java.io.File;
import java.io.IOException;
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

import de.Strobl.Commands.DM.CatBoy;
import de.Strobl.Commands.DM.CatGirl;
import de.Strobl.Events.GenericEmoteEvent.EmoteAdded;
import de.Strobl.Events.GenericEmoteEvent.EmoteRemoved;
import de.Strobl.Events.Nachrichten.OnEmoteSentEvent;
import de.Strobl.Events.Nachrichten.OnFileSentEvent;
import de.Strobl.Events.Nachrichten.OnMessageReactionRemoveEvent;
import de.Strobl.Events.Nachrichten.ScamDetectionCodeWort;
import de.Strobl.Events.Nachrichten.ScamDetectionLink;
import de.Strobl.Events.User.OnGuildMemberJoinEvent;
import de.Strobl.Events.User.OnUserUpdateOnlineStatusEvent;
import de.Strobl.Events.User.OnuserUpdateNameEvent;
import de.Strobl.Events.Voice.AFKKick;
import de.Strobl.Loops.TempBan;
import de.Strobl.Loops.TempMute;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	public static String Pfad = "./";
	public static String Userpfad = "./users/";
	public static JDA jda;
	public static String version = "v1.6.2";
	public static List<String> ServerEmotesID;
	private static final Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] arguments) {
		try {
			
			logger.fatal("Fatal");
			logger.error("ERROR");
			logger.warn("WARN");
			logger.info("INFO");
			logger.debug("DEBUG");
			logger.trace("TRACE");

			
			
			FileManagement.Update();

//JDA Builder

			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			logger.info("----------------------------------------------");
			logger.info("----------------------------------------------");
			logger.info("JDA wird gestartet");
			JDABuilder Builder = JDABuilder.createDefault(ini.get("Setup", "Token"));
			Builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES,
					GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MEMBERS);
			Builder.disableIntents(GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_INVITES,
					GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_REACTIONS);
			Builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES,
					CacheFlag.ROLE_TAGS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE);
			Builder.disableCache(CacheFlag.ONLINE_STATUS);
			Builder.setChunkingFilter(ChunkingFilter.NONE);
			Builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			Builder.setAutoReconnect(true);

//Event Listener

			Builder.addEventListeners(new CatBoy());
			Builder.addEventListeners(new CatGirl());
			Builder.addEventListeners(new BefehleAuswertung());
			Builder.addEventListeners(new ScamDetectionLink());
			Builder.addEventListeners(new ScamDetectionCodeWort());
			Builder.addEventListeners(new AFKKick());
			Builder.addEventListeners(new OnUserUpdateOnlineStatusEvent());
			Builder.addEventListeners(new OnuserUpdateNameEvent());
			Builder.addEventListeners(new OnGuildMemberJoinEvent());
			Builder.addEventListeners(new OnMessageReactionRemoveEvent());
			Builder.addEventListeners(new OnFileSentEvent());
			Builder.addEventListeners(new OnEmoteSentEvent());
			Builder.addEventListeners(new EmoteAdded());
			Builder.addEventListeners(new EmoteRemoved());

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
			logger.info("JDA wurde gestartet");

//Befehle anmelden

			BefehleRegistrieren.register(jda);

//Loops starten

			ScheduledExecutorService Loops = Executors.newScheduledThreadPool(1);
			Loops.scheduleAtFixedRate(new TempBan(), 10, 60, TimeUnit.SECONDS);
			logger.info("TempBan-Loop wurde gestartet");
			Loops.scheduleAtFixedRate(new TempMute(), 10, 60, TimeUnit.SECONDS);
			logger.info("TempMute-Loop wurde gestartet");

// Update für den Bot verfügbar?

			GitHub github = new GitHubBuilder().withOAuthToken("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").build();
			String neusteversion = github.getRepository("TwinBrot/Schneckchencord").getLatestRelease().getTagName();
			if (!version.equals(neusteversion)) {
				logger.info("Dein Bot läuft nicht auf der neusten Stable Version. Ich empfehle zu Updaten.");
			}

//Cache Emotes

			jda.getGuilds().get(0).retrieveEmotes().queue(GuildEmotes -> {
				ServerEmotesID = new ArrayList<String>();
				GuildEmotes.forEach(Emote -> {
					ServerEmotesID.add(Emote.getId());
				});
			});

//Fehler Management

		} catch (IOException e) {
			System.err.println("\nIOException - Hat der Bot Berechtigungen, um Dateien zu erstellen?");
			System.err.println(
					"\nSignals that an I/O exception of some sort has occurred. Thisclass is the general class of exceptions produced by failed orinterrupted I/O operations.\n");
			e.printStackTrace();

		} catch (LoginException | InterruptedException e) {
			System.err.println("\nFehler beim Initialisieren des Bots. Ist der Token richtig?\n");
			e.printStackTrace();

		}
	}
}
