package de.Strobl.Main;

import java.io.File;

import org.ini4j.Wini;

public class UpdateSettingsIni {

	public static void Update() {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
			String currentversion = ini.get("Setup", "VersionBot");
			while (!currentversion.equals(Main.version)) {
				currentversion = ini.get("Setup", "VersionBot");

				if (currentversion.equals("v1.4.1")) {
					ini.put("Setup", "VersionBot", "v1.4.2");
					ini.put("Setup", "VersionCMD", "0");
					ini.put("Settings", "Settings.StreamLink", "https://www.twitch.tv/maudado");
					ini.put("Settings", "Settings.Status", "ONLINE");
					ini.put("ModRollen", "Admin", ini.get("ModRollen", "Modrollen"));
					ini.put("ModRollen", "Mod", "[0]");
					ini.put("ModRollen", "Channelmod", "[0]");
					ini.remove("ModRollen", "Modrollen");
					ini.remove("Settings", "Settings.Prefix");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.4.1 to Version 1.4.2");

				} else if (currentversion.equals("v1.4.2")) {
					ini.put("Setup", "VersionBot", "v1.4.3");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.4.2 to Version 1.4.3");

				} else if (currentversion.equals("v1.4.3")) {
					ini.put("Setup", "VersionBot", "v1.4.4");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.4.3 to Version 1.4.4");

				} else if (currentversion.equals("v1.4.4")) {
					ini.put("Setup", "VersionBot", "v1.4.5");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.4.4 to Version 1.4.5");
					
				} else if (currentversion.equals("v1.4.5")) {
					ini.put("Setup", "VersionBot", "v1.5.0");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.4.5 to Version 1.5.0");
					
				} else if (currentversion.equals("v1.5.0")) {
					ini.put("Setup", "VersionBot", "v1.6.0");
					ini.store();
					System.out.println("Updated settings.ini from Version 1.5.0 to Version 1.6.0");
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Update der settings.ini fehlgeschlagen!");
		}
	}
}