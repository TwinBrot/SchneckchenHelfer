package de.Strobl.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

import de.Strobl.CommandsFunsies.Wordle.Wordle;

public class Settings {
	private static final Logger logger = LogManager.getLogger(Settings.class);
	private static Boolean update = false;
	private static String Pfad = Main.Pfad;

	public static String Token;
	public static String Version;
	public static String AktivitätText;
	public static String AktivitätTyp;
	public static String Status;
	public static String LogChannel;
	public static String AnnounceChannel;
	public static String[] Admin;
	public static String[] Mod;
	public static String[] Channelmod;
	public static Boolean DateiActive;
	public static String[] Datei;
	public static Boolean NamenActive;
	public static String[] Namen;
	public static ArrayList<String> Links;
	public static String currentword;
	public static String twitchid;
	public static String twitchsecret;
	public static String streamer;

	public static void load() throws InvalidFileFormatException, IOException, NullPointerException {
		Wini ini = new Wini(new File(Pfad + "settings.ini"));
		Token = ini.get("Setup", "Token");
		Version = ini.get("Setup", "Version");
		AktivitätText = ini.get("Settings", "AktivitätText");
		AktivitätTyp = ini.get("Settings", "AktivitätTyp");
		Status = ini.get("Settings", "Status");
		LogChannel = ini.get("Settings", "LogChannel");
		Admin = ini.get("ModRollen", "Admin").replaceAll(" ", "").split(",");
		Mod = ini.get("ModRollen", "Mod").replaceAll(" ", "").split(",");
		Channelmod = ini.get("ModRollen", "Channelmod").replaceAll(" ", "").split(",");
		Datei = ini.get("Dateiüberwachung", "Allowed").replaceAll(" ", "").split(",");
		Namen = ini.get("Namensüberwachung", "Verboten").replaceAll(" ", "").split(",");
		AnnounceChannel = ini.get("Twitch", "AnnounceChannel");
		twitchid = ini.get("Twitch", "twitchid");
		twitchsecret = ini.get("Twitch", "twitchsecret");
		streamer = ini.get("Twitch", "streamer");

		String DateiTemp = ini.get("Dateiüberwachung", "Active");
		if (DateiTemp.equals("true")) {
			DateiActive = true;
		} else {
			DateiActive = false;
		}
		String NamenTemp = ini.get("Namensüberwachung", "Active");
		if (NamenTemp.equals("true")) {
			NamenActive = true;
		} else {
			NamenActive = false;
		}
		currentword = ini.get("Settings", "Wordle");
		if (currentword.equals("")) {
			Wordle.newWord();
		}

		Wini linkini = new Wini(new File(Main.Pfad + "Link.ini"));
		Section links = linkini.get("Links");
		ArrayList<String> temp = new ArrayList<String>();
		links.forEach((unused, link) -> {
			temp.add(link);
		});
		Links = temp;
	}

	public static void set(String section, String option, String value) throws IOException {
		Wini ini = new Wini(new File(Pfad + "settings.ini"));
		ini.put(section, option, value);
		ini.store();
		load();
	}

	public static void Update() {
		try {
// Read settings.ini File
			try {
				@SuppressWarnings("unused")
				Wini ini = new Wini(new File(Pfad + "settings.ini"));
			} catch (IOException e) {
				logger.warn("settings.ini nicht gefunden.");
				logger.warn("Versuche Datei zu erstellen.");
				File newFile = new File(Pfad + "settings.ini");
				newFile.createNewFile();
			}
			Wini ini = new Wini(new File(Pfad + "settings.ini"));

// Check for Not Existing Settings

			create(ini, "Setup", "Token", "INPUT_TOKEN_HERE");
			create(ini, "Setup", "Version", "");
			create(ini, "Settings", "AktivitätText", "/hilfe");
			create(ini, "Settings", "AktivitätTyp", "playing");
			create(ini, "Settings", "StreamLink", "https://www.twitch.tv/maudado");
			create(ini, "Settings", "Status", "ONLINE");
			create(ini, "Settings", "LogChannel", "");
			create(ini, "Settings", "AnnounceChannel", "");
			create(ini, "Settings", "Wordle", "");
			create(ini, "ModRollen", "Admin", "");
			create(ini, "ModRollen", "Mod", "");
			create(ini, "ModRollen", "Channelmod", "");
			create(ini, "Dateiüberwachung", "Active", "false");
			create(ini, "Dateiüberwachung", "Allowed",
					"jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp");
			create(ini, "Namensüberwachung", "Active", "false");
			create(ini, "Namensüberwachung", "Verboten", "");
			create(ini, "Twitch", "AnnounceChannel", "");
			create(ini, "Twitch", "twitchid", "");
			create(ini, "Twitch", "twitchsecret", "");
			create(ini, "Twitch", "streamer", "");

			if (update) {
				logger.warn("Settings.ini wird angepasst");
				ini.store();
				logger.warn("Settings.ini wurde angepasst");
			}
		} catch (Exception e) {
			logger.fatal("Fehler beim anlegen/updaten der Settings-Dateien:", e);
		}
	}

	private static void create(Wini ini, String section, String option, String value) {
		if (ini.get(section, option) == null) {
			update = true;
			ini.put(section, option, value);
		}
	}
}