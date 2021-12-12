package de.Strobl.Main;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

public class Settings {
	private static final Logger logger = LogManager.getLogger(Settings.class);

	@SuppressWarnings("unused")
	public static void Update() {
		String Pfad = Main.Pfad;
		try {
// Read settings.ini File
			try {
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
			create(ini, "Settings", "Settings.AktivitätText", "/hilfe");
			create(ini, "Settings", "Settings.AktivitätTyp", "playing");
			create(ini, "Settings", "Settings.StreamLink", "https://www.twitch.tv/maudado");
			create(ini, "Settings", "Settings.LogChannel", "");
			create(ini, "Settings", "Settings.Status", "ONLINE");
			create(ini, "ModRollen", "Admin", "");
			create(ini, "ModRollen", "Mod", "");
			create(ini, "ModRollen", "Channelmod", "");
			create(ini, "Dateiüberwachung", "Active", "false");
			create(ini, "Dateiüberwachung", "Allowed",
					"jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp");
			create(ini, "Namensüberwachung", "Active", "false");
			create(ini, "Namensüberwachung", "Verboten", "");

			ini.store();

		} catch (Exception e) {
			logger.fatal("Fehler beim anlegen/updaten der Settings-Dateien:", e);
		}
	}
	public static void create(Wini ini, String section, String option, String value) {
		if (ini.get(section, option) == null) {
			ini.put(section, option, value);
		}
	}
}