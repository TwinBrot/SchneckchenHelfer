package de.Strobl.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.security.auth.login.LoginException;

import org.ini4j.Wini;

import de.Strobl.Commands.DM.CatBoy;
import de.Strobl.Commands.DM.CatGirl;
import de.Strobl.Loops.LoopMain;
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
			
			System.out.println("MainThread startet");
//Read settings.ini File
			try { @SuppressWarnings("unused")
				Wini ini = new Wini (new File(Pfad + "settings.ini"));
			} catch (IOException e) {
			    System.err.println("settings.ini nicht gefunden. \nVersuche Datei zu erstellen.");
			    File newFile = new File(Pfad + "settings.ini");
			    newFile.createNewFile();
				Wini ini = new Wini (new File(Pfad + "settings.ini"));
				ini.put("Setup", "Token", "NzIyODU4NDA3Mzg3ODU3MDQ3.XuqAiQ.eVkd3rLUaWuI4dEkv2ulCeSJKG8");
				ini.put("Settings", "Settings.Aktivity", "!hilfe");
				ini.put("Settings", "Settings.Activity2", "playing");
				ini.put("Settings", "Settings.LogChannel", "");
	        	ini.put("Settings", "Settings.AFKVoice", "no");
	        	ini.put("ModRollen", "Admin", "[0]");
	        	ini.put("ModRollen", "Mod", "[0]");
	        	ini.put("ModRollen", "Channelmod", "[0]");
	        	ini.put("Dateiüberwachung", "Active", "false");
	        	ini.put("Dateiüberwachung", "Allowed", "[0, jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp]");
	        	ini.put("Namensüberwachung", "Active", "false");
	        	ini.put("Namensüberwachung", "Verboten", "[*~+]");
	        	ini.store();
			}
			
//Read Emotes.ini File
			try {@SuppressWarnings("unused")
				Wini emotes = new Wini (new File (Pfad + "Emotes.ini"));
			} catch (IOException e) {
				System.err.println("Emotes.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Emotes.ini");
				newFile.createNewFile();
				Wini emotes = new Wini (new File(Pfad + "Emotes.ini"));
	        	emotes.put("Embed", "Count", "24");
				emotes.store();
			}	
			
//Read Strafen.ini File
			try {@SuppressWarnings("unused")
				Wini Strafen = new Wini (new File (Pfad + "Strafen.ini"));
			} catch (IOException e) {
				System.err.println("Strafen.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Strafen.ini");
				newFile.createNewFile();
				Wini Strafen = new Wini (new File(Pfad + "Strafen.ini"));
				Strafen.store();
			}
			
//Read Auswertung.ini File
			try {@SuppressWarnings("unused")
				Wini Auswertung = new Wini (new File (Pfad + "Auswertung.ini"));
			} catch (IOException e) {
				System.err.println("Auswertung.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "Auswertung.ini");
				newFile.createNewFile();
				Wini Auswertung = new Wini (new File(Pfad + "Auswertung.ini"));
				Auswertung.store();
			}
			
//Read ID.ini File
			try {@SuppressWarnings("unused")
				Wini Auswertung = new Wini (new File (Pfad + "ID.ini"));
			} catch (IOException e) {
				System.err.println("ID.ini nicht gefunden. \nVersuche Datei zu erstellen.");
				File newFile = new File(Pfad + "ID.ini");
				newFile.createNewFile();
				Wini Auswertung = new Wini (new File(Pfad + "ID.ini"));
				Auswertung.put("Hinweise", "Counter", "0");
				Auswertung.store();
			}
			
//Users Ordner Anlegen
	        File file = new File(Userpfad);
	        if (!file.exists()) {
	            if (file.mkdir()) {
	                System.err.println("User-Dateien Ordner nicht gefunden. \nVersuche Ordner zu erstellen.");
	            } else {
	                System.err.println("Failed to create directory!");
	            }
	        }
	        
	        
//JDA Builder
	        
			System.out.println("");
			System.out.println("JDA wird gestartet");
			Wini ini = new Wini (new File(Pfad + "settings.ini"));
			JDABuilder Builder = JDABuilder.createDefault(ini.get("Setup", "Token"));
			Builder.enableIntents(
					GatewayIntent.DIRECT_MESSAGES,
					GatewayIntent.GUILD_VOICE_STATES, 
					GatewayIntent.GUILD_PRESENCES,
					GatewayIntent.GUILD_BANS, 
					GatewayIntent.GUILD_MESSAGE_REACTIONS,
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_MEMBERS);
			Builder.disableIntents(
					GatewayIntent.GUILD_EMOJIS, 
					GatewayIntent.GUILD_WEBHOOKS, 
					GatewayIntent.GUILD_INVITES, 
					GatewayIntent.GUILD_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_TYPING,
					GatewayIntent.DIRECT_MESSAGE_REACTIONS
					);
			Builder.enableCache(
					CacheFlag.ACTIVITY,
					CacheFlag.CLIENT_STATUS,
					CacheFlag.MEMBER_OVERRIDES,
					CacheFlag.ROLE_TAGS,
					CacheFlag.VOICE_STATE
					);
			Builder.disableCache(
					CacheFlag.EMOTE,
					CacheFlag.ONLINE_STATUS
					);
			Builder.setChunkingFilter(ChunkingFilter.NONE);
			Builder.setMemberCachePolicy(MemberCachePolicy.ALL);
			Builder.setAutoReconnect(true);
			
//Event Listener
			
			Builder.addEventListeners(new CatBoy());
			Builder.addEventListeners(new CatGirl());
			Builder.addEventListeners(new BefehleAuswertung());
			
//Activity
//TODO Activity
			
			if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("playing")) 		{Builder.setActivity(Activity.playing(ini.get("Settings", "Settings.Aktivity")));}
			if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("listening"))	{Builder.setActivity(Activity.listening(ini.get("Settings", "Settings.Aktivity")));}
			if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("streaming")) 	{Builder.setActivity(Activity.streaming(ini.get("Settings", "Settings.Aktivity"), "https://www.youtube.com/watch?v=5qap5aO4i9A"));}
			if (ini.get("Settings", "Settings.Activity2").equalsIgnoreCase("watching")) 	{Builder.setActivity(Activity.watching(ini.get("Settings", "Settings.Aktivity")));}
		
			
//Status
//TODO Status
			Builder.setStatus(OnlineStatus.ONLINE);
			
			
			
			
			
//JDA Starten und fertigstellung abwarten
			
			jda = Builder
					.build()
					.awaitReady();

			System.out.println("JDA wurde gestartet");
			
//Befehle anmelden
			
			BefehleRegistrieren.register(jda);

//	        CommandListUpdateAction commands = jda.updateCommands();
//	        commands.complete();
			
			System.out.println("MainThread wurde gestartet");
		
//Loops starten
		    	
			LoopMain LoopMain = new LoopMain();	
			LoopMain.start();

			
		} catch (IOException e1) {
			System.err.println("");
			System.err.println("IOException - Hat der Bot Berechtigungen, um Dateien zu erstellen?");
			System.err.println("");
			System.err.println("Signals that an I/O exception of some sort has occurred. Thisclass is the general class of exceptions produced by failed orinterrupted I/O operations.");
			System.err.println("");
			e1.printStackTrace();
			
		} catch (LoginException | InterruptedException e) {
			System.err.println("");
			System.err.println("Fehler beim Initialisieren des Bots. Ist der Token richtig?");
			System.err.println("");
			e.printStackTrace();
			
		}
	}
}
