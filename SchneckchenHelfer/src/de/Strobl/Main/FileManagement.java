package de.Strobl.Main;

import java.io.File;
import java.io.IOException;

import org.ini4j.Wini;

public class FileManagement {
	@SuppressWarnings("unused")
	public static void Update() {
		try {
//Read settings.ini File
			try {
				Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			} catch (IOException e) {
				System.err.println("settings.ini nicht gefunden.");
				System.err.println("Versuche Datei zu erstellen.");
				File newFile = new File(Main.Pfad + "settings.ini");
				newFile.createNewFile();
			}
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			if (ini.get("Settings", "Settings.Aktivity")!=null) {
				ini.put("Settings", "Settings.AktivitätText", ini.get("Settings", "Settings.Aktivity"));
				ini.remove("Settings", "Settings.Aktivity");
				}
			if (ini.get("Settings", "Settings.Activity2")!=null) {
				ini.put("Settings", "Settings.AktivitätTyp", ini.get("Settings", "Settings.Activity2"));
				ini.remove("Settings", "Settings.Activity2");
				}
			ini.store();
			if (ini.get("Setup", "Token")==null) {ini.put("Setup", "Token", "NzIyODU4NDA3Mzg3ODU3MDQ3.XuqAiQ.eVkd3rLUaWuI4dEkv2ulCeSJKG8");}
			if (ini.get("Settings", "Settings.AktivitätTyp")==null) {ini.put("Settings", "Settings.AktivitätTyp", "!hilfe");}
			if (ini.get("Settings", "Settings.AktivitätText")==null) {ini.put("Settings", "Settings.AktivitätText", "playing");}
			if (ini.get("Settings", "Settings.StreamLink")==null) {ini.put("Settings", "Settings.StreamLink", "https://www.twitch.tv/maudado");}
			if (ini.get("Settings", "Settings.LogChannel")==null) {ini.put("Settings", "Settings.LogChannel", "");}
			if (ini.get("Settings", "Settings.AFKVoice")==null) {ini.put("Settings", "Settings.AFKVoice", "no");}
			if (ini.get("Settings", "Settings.Status")==null) {ini.put("Settings", "Settings.Status", "ONLINE");}
			if (ini.get("ModRollen", "Channelmod")==null) {ini.put("ModRollen", "Channelmod", "[0]");}
			if (ini.get("Dateiüberwachung", "Active")==null) {ini.put("Dateiüberwachung", "Active", "false");}
			if (ini.get("Dateiüberwachung", "Allowed")==null) {ini.put("Dateiüberwachung", "Allowed", "[0, jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp]");}
			if (ini.get("Namensüberwachung", "Active")==null) {ini.put("Namensüberwachung", "Active", "false");}
			if (ini.get("Namensüberwachung", "Verboten")==null) {ini.put("Namensüberwachung", "Verboten", "[*~+]");}
			ini.store();
			
			
// Read Emotes.ini File
			try {
				Wini emotes = new Wini(new File(Main.Pfad + "Emotes.ini"));
			} catch (IOException e) {
				System.err.println("Emotes.ini nicht gefunden.");
				System.err.println("Versuche Datei zu erstellen.");
				File newFile = new File(Main.Pfad + "Emotes.ini");
				newFile.createNewFile();
			}
			Wini emotes = new Wini(new File(Main.Pfad + "Emotes.ini"));
			if (emotes.get("Embed", "Count")==null) {emotes.put("Embed", "Count", "24");}
			emotes.store();
			
			
// Read Strafen.ini File
			try {
				Wini Strafen = new Wini(new File(Main.Pfad + "Strafen.ini"));
			} catch (IOException e) {
				System.err.println("Strafen.ini nicht gefunden.");
				System.err.println("Versuche Datei zu erstellen.");
				File newFile = new File(Main.Pfad + "Strafen.ini");
				newFile.createNewFile();
				Wini Strafen = new Wini(new File(Main.Pfad + "Strafen.ini"));
				Strafen.store();
			}

// Read ID.ini File
			try {
				Wini ID = new Wini(new File(Main.Pfad + "ID.ini"));
			} catch (IOException e) {
				System.err.println("ID.ini nicht gefunden.");
				System.err.println("Versuche Datei zu erstellen.");
				File newFile = new File(Main.Pfad + "ID.ini");
				newFile.createNewFile();
			}
			Wini Auswertung = new Wini(new File(Main.Pfad + "ID.ini"));
			if (Auswertung.get("Hinweise", "Counter")==null) {Auswertung.put("Hinweise", "Counter", "0");}
			Auswertung.store();
			
			
			
			
// Read Link.ini File
			try {
				Wini Link = new Wini(new File(Main.Pfad + "Link.ini"));
			} catch (IOException e) {
				System.err.println("Link.ini nicht gefunden.");
				System.err.println("Versuche Datei zu erstellen.");
				File newFile = new File(Main.Pfad + "Link.ini");
				newFile.createNewFile();
			}
			Wini Link = new Wini(new File(Main.Pfad + "Link.ini"));
			if (Link.get("Links").size() == 0) {
				Link.putComment("Links", "Add Links like this:   COUNTER = LINK");
				Link.put("Links", "1", "discorcl.link/");
				}
			Link.store();
			
			
			
			
// Users Ordner Anlegen
			File file = new File(Main.Userpfad);
			if (!file.exists()) {
				if (file.mkdir()) {
					System.err.println("User-Dateien Ordner nicht gefunden.");
					System.err.println("Versuche Ordner zu erstellen.");
				} else {
					System.err.println("Konnte User-Ordner nicht erstellen!");
				}
			}

			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fehler beim anlegen der Dateien.");
		}
	}
}