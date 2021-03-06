package de.Strobl.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import de.Strobl.CommandsFunsies.MessageReceivedFunsies;
import de.Strobl.CommandsFunsies.SlashCommandFunsies;
import de.Strobl.CommandsFunsies.Wordle.Wordle;
import de.Strobl.CommandsMod.ButtonInteraction;
import de.Strobl.CommandsMod.MessageReceived;
import de.Strobl.CommandsMod.ModalInteraction;
import de.Strobl.CommandsMod.SlashCommand;
import de.Strobl.Events.EmoteEvent;
import de.Strobl.Events.Channel.ChannelCreate;
import de.Strobl.Events.Nachrichten.EmoteTracking;
import de.Strobl.Events.Nachrichten.Filechecker;
import de.Strobl.Events.Nachrichten.InviteDetection;
import de.Strobl.Events.Nachrichten.ReactionRemoveLog;
import de.Strobl.Events.Nachrichten.ScamDetection;
import de.Strobl.Events.User.BotIsOfflineAlarm;
import de.Strobl.Events.User.JoinNamens├╝berwachung;
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
	public static String version = "4.5.0beta1";
	public static List<String> ServerEmotesID;
	public static JDA jda;
	public static String Pfad = "./";

	public static void main(String[] arguments) {
		try {
			logger.info("Starte Schneckchencord-Bot mit Version " + version);
			// Update f├╝r den Bot verf├╝gbar?
			try {
				GitHub github = new GitHubBuilder().build();
				String neusteversion = github.getRepository("TwinBrot/Schneckchencord").getLatestRelease().getTagName();
				if (!version.equals(neusteversion)) {
					logger.warn("Dein Bot l├Ąuft nicht auf der neusten Stable Version. Ich empfehle auf Version '" + neusteversion + "' zu Updaten.");
				}
			} catch (Exception e) {
				logger.error("Konnte nicht auf neuste Version ├╝berpr├╝fen!", e);
			}

			// Settings.ini und SQL starten

			Settings.Update();
			Settings.load();
			SQL.initialize();

			// JDA Builder

			JDABuilder Builder = JDABuilder.createDefault(Settings.Token);
			Builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_BANS,
					GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MEMBERS,
					GatewayIntent.MESSAGE_CONTENT);
			Builder.disableIntents(GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_REACTIONS);
			Builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOJI);
			Builder.disableCache(CacheFlag.ONLINE_STATUS);
			Builder.setChunkingFilter(ChunkingFilter.NONE);
			Builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			Builder.setAutoReconnect(true);

			// Event Listener
			// Commands
			Builder.addEventListeners(new SlashCommand());
			Builder.addEventListeners(new SlashCommandFunsies());
			Builder.addEventListeners(new MessageReceived());
			Builder.addEventListeners(new MessageReceivedFunsies());
			Builder.addEventListeners(new ButtonInteraction());
			Builder.addEventListeners(new ModalInteraction());
			// Message Events
			Builder.addEventListeners(new EmoteTracking());
			Builder.addEventListeners(new Filechecker());
			Builder.addEventListeners(new InviteDetection());
			Builder.addEventListeners(new ReactionRemoveLog());
			Builder.addEventListeners(new ScamDetection());
			// User Events
			Builder.addEventListeners(new BotIsOfflineAlarm());
			Builder.addEventListeners(new JoinNamens├╝berwachung());
			Builder.addEventListeners(new OnuserUpdateNameEvent());
			// Channel Events
			Builder.addEventListeners(new ChannelCreate());
			Builder.addEventListeners(new EmoteEvent());

			// Activity

			String Typ = Settings.Aktivit├ĄtTyp;
			String Text = Settings.Aktivit├ĄtText;

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
			}

			// Status

			switch (Settings.Status) {
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

			// JDA Starten

			jda = Builder.build().awaitReady();
			SlashCommand.startupcheck(jda, version);

			// Cache Emotes

			jda.getGuilds().get(0).retrieveEmojis().queue(GuildEmotes -> {
				ServerEmotesID = new ArrayList<String>();
				GuildEmotes.forEach(Emote -> {
					ServerEmotesID.add(Emote.getId());
				});
			});

			// Loops starten

			ScheduledExecutorService Loops = Executors.newScheduledThreadPool(1);
			Loops.scheduleAtFixedRate(new Loops(), 10, 60, TimeUnit.SECONDS);

			ScheduledExecutorService WordleLoop = Executors.newScheduledThreadPool(1);
			Long midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
			WordleLoop.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					Wordle.newWord();
				}
			}, midnight, 1440, TimeUnit.MINUTES);

			// Twitch Verbindung starten

			TwitchAPI.twitchAPI();

			// Detect Commandline

			Scanner scanner = new Scanner(System.in);
			try {
				while (true) {
					String end = scanner.nextLine();
					if (end.equals("shutdown")) {
						jda.shutdown();
						System.exit(0);
					}
				}
			} catch (NoSuchElementException e) {
			}
			scanner.close();

			// Fehler Management
		} catch (IllegalStateException e) {
			logger.fatal("IllegalStateException - 'Presence Intent' und 'Server Members Intent' im Discord Developer Portal aktiviert? ", e);

		} catch (IOException e) {
			logger.fatal("IOException - Hat der Bot Berechtigungen, um Dateien zu erstellen?", e);

		} catch (SQLException e) {
			logger.fatal("SQL-Fehler", e);

		} catch (LoginException | InterruptedException e) {
			logger.fatal("Fehler beim Initialisieren des Bots. Ist der Token richtig:", e);

		}
	}
}
