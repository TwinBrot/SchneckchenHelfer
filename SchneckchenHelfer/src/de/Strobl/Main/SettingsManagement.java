package de.Strobl.Main;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

public class SettingsManagement {
	@SuppressWarnings("unused")
	public static void Update() {
		Logger logger = Main.logger;
		String Pfad = Main.Pfad;
		try {
//Read settings.ini File
			try {
				Wini ini = new Wini(new File(Pfad + "settings.ini"));
			} catch (IOException e) {
				logger.warn("settings.ini nicht gefunden.");
				logger.warn("Versuche Datei zu erstellen.");
				File newFile = new File(Pfad + "settings.ini");
				newFile.createNewFile();
			}
			Wini ini = new Wini(new File(Pfad + "settings.ini"));

//Migration of Settings

			if (ini.get("Settings", "Settings.Aktivity") != null) {
				ini.put("Settings", "Settings.AktivitätText", ini.get("Settings", "Settings.Aktivity"));
				ini.remove("Settings", "Settings.Aktivity");
			}
			if (ini.get("Settings", "Settings.Activity2") != null) {
				ini.put("Settings", "Settings.AktivitätTyp", ini.get("Settings", "Settings.Activity2"));
				ini.remove("Settings", "Settings.Activity2");
			}
			ini.store();

//Check for Not Existing Settings

			if (ini.get("Setup", "Token") == null) {
				ini.put("Setup", "Token", "INPUT_TOKEN_HERE");
			}
			if (ini.get("Settings", "Settings.AktivitätText") == null) {
				ini.put("Settings", "Settings.AktivitätText", "playing");
			}
			if (ini.get("Settings", "Settings.AktivitätTyp") == null) {
				ini.put("Settings", "Settings.AktivitätTyp", "/hilfe");
			}
			if (ini.get("Settings", "Settings.StreamLink") == null) {
				ini.put("Settings", "Settings.StreamLink", "https://www.twitch.tv/maudado");
			}
			if (ini.get("Settings", "Settings.LogChannel") == null) {
				ini.put("Settings", "Settings.LogChannel", "");
			}
			if (ini.get("Settings", "Settings.Status") == null) {
				ini.put("Settings", "Settings.Status", "ONLINE");
			}
			if (ini.get("ModRollen", "Admin") == null) {
				ini.put("ModRollen", "Admin", "");
			}
			if (ini.get("ModRollen", "Mod") == null) {
				ini.put("ModRollen", "Mod", "");
			}
			if (ini.get("ModRollen", "Channelmod") == null) {
				ini.put("ModRollen", "Channelmod", "");
			}
			if (ini.get("Dateiüberwachung", "Active") == null) {
				ini.put("Dateiüberwachung", "Active", "false");
			}
			if (ini.get("Dateiüberwachung", "Allowed") == null) {
				ini.put("Dateiüberwachung", "Allowed",
						"jpg, png, jpeg, gif, bmp, mp3, mov, mp4, m4a, webp, webm, tif, avi, psd, jpg_large, wav, txt, pdf, jfif, heic, jpglarge, 3gp");
			}
			if (ini.get("Namensüberwachung", "Active") == null) {
				ini.put("Namensüberwachung", "Active", "false");
			}
			if (ini.get("Namensüberwachung", "Verboten") == null) {
				ini.put("Namensüberwachung", "Verboten", "");
			}
			ini.store();

		} catch (Exception e) {
			logger.fatal("Fehler beim anlegen/updaten der Settings-Dateien:", e);
		}
	}
}