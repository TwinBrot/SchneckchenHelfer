package de.Strobl.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.ini4j.Wini;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import de.Strobl.Commands.DM.CatBoy;
import de.Strobl.Commands.DM.CatGirl;
import de.Strobl.Events.Nachrichten.CodewortScamDetection;
import de.Strobl.Events.Nachrichten.LinkScamDetection;
import de.Strobl.Events.User.OnGuildMemberJoinEvent;
import de.Strobl.Events.User.OnUserUpdateOnlineStatusEvent;
import de.Strobl.Events.User.OnuserUpdateNameEvent;
import de.Strobl.Events.Voice.AFKKick;
import de.Strobl.Instances.TeeOutputStream;
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
	public static String version = "v1.5.0";

	public static void main(String[] arguments) {
		try {

			PrintStream outStream = System.out;
			OutputStream os = new FileOutputStream(Pfad + "log.txt", true);
			PrintStream fileStream = new PrintStream(new TeeOutputStream(outStream, os));
			System.setOut(fileStream);

			PrintStream errStream = System.err;
			OutputStream err = new FileOutputStream(Pfad + "log.txt", true);
			PrintStream errorStream = new PrintStream(new TeeOutputStream(errStream, err));
			System.setErr(errorStream);

//Read settings.ini File
			try {
				@SuppressWarnings("unused")
				Wini ini = new Wini(new File(Pfad + "settings.ini"));
			} catch (IOException e) {
				System.err.println("settings.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "settings.ini");
				newFile.createNewFile();
				Wini ini = new Wini(new File(Pfad + "settings.ini"));
				ini.put("Setup", "Token", "NzIyODU4NDA3Mzg3ODU3MDQ3.XuqAiQ.eVkd3rLUaWuI4dEkv2ulCeSJKG8");
				ini.put("Setup", "VersionBot", version);
				ini.put("Setup", "VersionCMD", "0");
				ini.put("Settings", "Settings.Aktivity", "!hilfe");
				ini.put("Settings", "Settings.Activity2", "playing");
				ini.put("Settings", "Settings.StreamLink", "https://www.twitch.tv/maudado");
				ini.put("Settings", "Settings.LogChannel", "");
				ini.put("Settings", "Settings.AFKVoice", "no");
				ini.put("Settings", "Settings.Status", "ONLINE");
				ini.put("ModRollen", "Admin", "[0]");
				ini.put("ModRollen", "Mod", "[0]");
				ini.put("ModRollen", "Channelmod", "[0]");
				ini.put("Dateiüberwachung", "Active", "false");
				ini.put("Dateiüberwachung", "Allowed",
						"[0, jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp]");
				ini.put("Namensüberwachung", "Active", "false");
				ini.put("Namensüberwachung", "Verboten", "[*~+]");
				ini.store();
			}

			UpdateSettingsIni.Update();

//Read Emotes.ini File
			try {
				@SuppressWarnings("unused")
				Wini emotes = new Wini(new File(Pfad + "Emotes.ini"));
			} catch (IOException e) {
				System.err.println("Emotes.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Emotes.ini");
				newFile.createNewFile();
				Wini emotes = new Wini(new File(Pfad + "Emotes.ini"));
				emotes.put("Embed", "Count", "24");
				emotes.store();
			}

//Read Strafen.ini File
			try {
				@SuppressWarnings("unused")
				Wini Strafen = new Wini(new File(Pfad + "Strafen.ini"));
			} catch (IOException e) {
				System.err.println("Strafen.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Strafen.ini");
				newFile.createNewFile();
				Wini Strafen = new Wini(new File(Pfad + "Strafen.ini"));
				Strafen.store();
			}

			// Read ID.ini File
			try {
				@SuppressWarnings("unused")
				Wini Auswertung = new Wini(new File(Pfad + "ID.ini"));
			} catch (IOException e) {
				System.err.println("ID.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "ID.ini");
				newFile.createNewFile();
				Wini Auswertung = new Wini(new File(Pfad + "ID.ini"));
				Auswertung.put("Hinweise", "Counter", "0");
				Auswertung.store();
			}

			// Read Link.ini File
			try {
				@SuppressWarnings("unused")
				Wini Link = new Wini(new File(Pfad + "Link.ini"));
			} catch (IOException e) {
				System.err.println("Link.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Link.ini");
				newFile.createNewFile();
				Wini Link = new Wini(new File(Pfad + "Link.ini"));
				Link.put("Links", "1", "discorcl.link/");
				Link.store();
			}

//Users Ordner Anlegen
			File file = new File(Userpfad);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.err.println("User-Dateien Ordner nicht gefunden. \nVersuche Ordner zu erstellen.");
				} else {
					System.err.println("Keinner User-Ordner nicht erstellen!");
				}
			}

//JDA Builder
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			System.out.println(
					"----------------------------------------------\n----------------------------------------------\nJDA wird gestartet");
			JDABuilder Builder = JDABuilder.createDefault(ini.get("Setup", "Token"));
			Builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES,
					GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
					GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS);
			Builder.disableIntents(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_WEBHOOKS,
					GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_REACTIONS);
			Builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES,
					CacheFlag.ROLE_TAGS, CacheFlag.VOICE_STATE);
			Builder.disableCache(CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS);
			Builder.setChunkingFilter(ChunkingFilter.NONE);
			Builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			Builder.setAutoReconnect(true);

//Event Listener

			Builder.addEventListeners(new CatBoy());
			Builder.addEventListeners(new CatGirl());
			Builder.addEventListeners(new BefehleAuswertung());
			Builder.addEventListeners(new LinkScamDetection());
			Builder.addEventListeners(new CodewortScamDetection());
			Builder.addEventListeners(new AFKKick());
			Builder.addEventListeners(new OnUserUpdateOnlineStatusEvent());
			Builder.addEventListeners(new OnuserUpdateNameEvent());
			Builder.addEventListeners(new OnGuildMemberJoinEvent());

//Activity

			if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("playing")) {
				Builder.setActivity(Activity.playing(ini.get("Settings", "Settings.Aktivity")));
			} else if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("listening")) {
				Builder.setActivity(Activity.listening(ini.get("Settings", "Settings.Aktivity")));
			} else if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("streaming")) {
				Builder.setActivity(Activity.streaming(ini.get("Settings", "Settings.Aktivity"),
						ini.get("Settings", "Settings.StreamLink")));
			} else if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("watching")) {
				Builder.setActivity(Activity.watching(ini.get("Settings", "Settings.Aktivity")));
			}

//Status

			if (ini.get("Settings", "Settings.Status").equalsIgnoreCase("ONLINE")) {
				Builder.setStatus(OnlineStatus.ONLINE);
			} else if (ini.get("Settings", "Settings.Status").equalsIgnoreCase("IDLE")) {
				Builder.setStatus(OnlineStatus.IDLE);
			} else if (ini.get("Settings", "Settings.Status").equalsIgnoreCase("DO_NOT_DISTURB")) {
				Builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
			} else if (ini.get("Settings", "Settings.Status").equalsIgnoreCase("INVISIBLE")) {
				Builder.setStatus(OnlineStatus.INVISIBLE);
			}

//JDA Starten und fertigstellung abwarten

			jda = Builder.build().awaitReady();
			System.out.println("JDA wurde gestartet");

//Befehle anmelden
			if (!ini.get("Setup", "VersionCMD").equals(version)) {
				BefehleRegistrieren.register(jda);
				ini.put("Setup", "VersionCMD", version);
				ini.store();
			}

//Loops starten
			ScheduledExecutorService Loops = Executors.newScheduledThreadPool(1);
			Loops.scheduleAtFixedRate(new TempBan(), 10, 60, TimeUnit.SECONDS);
			System.out.println("TempBan-Loop wurde gestartet");
			Loops.scheduleAtFixedRate(new TempMute(), 10, 60, TimeUnit.SECONDS);
			System.out.println("TempMute-Loop wurde gestartet");

// Update für den Bot verfügbar?
			GitHub github = new GitHubBuilder().withOAuthToken("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").build();
			String neusteversion = github.getRepository("TwinBrot/Schneckchencord").getLatestRelease().getTagName();
			if (!version.equals(neusteversion)) {
				System.out.println("Dein Bot läuft nicht auf der neusten Stable Version. Ich empfehle zu Updaten.");
			}

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
