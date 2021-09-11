package de.Strobl.Instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.Member;

public class isMod {
	public static Integer check(Member member) {
		Logger logger = Main.logger;

//-1 = Fehler		
//0 = User ist kein Mod
//1 = User ist Channelmod
//2 = User ist Mod
//3 = User ist Admin

		try {
//Owner
			if (member.isOwner()) {
				return 3;
			}
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));

//Userrollen auslesen

			ArrayList<String> UserRollen = new ArrayList<String>();
			for (int i = 0; i < member.getRoles().size(); i++) {
				UserRollen.add(member.getRoles().get(i).getId().toString());
			}

//Administrator

			if (!(ini.get("ModRollen", "Admin") == null)) {
				ArrayList<String> Admins = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Admin").replaceAll("]", "").replaceAll("\\s", "").split(",");

				for (int i = 0; i < Rollen.length; i++) {
					Admins.add(Rollen[i]);
				}
				UserRollen.removeAll(Admins);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 3;
				}
			}

//Moderator

			if (!(ini.get("ModRollen", "Mod") == null)) {
				ArrayList<String> Mods = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Mod").replaceAll("]", "").replaceAll("\\s", "").split(",");

				for (int i = 0; i < Rollen.length; i++) {
					Mods.add(Rollen[i]);
				}

				UserRollen.removeAll(Mods);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 2;
				}
			}

//Channelmod

			if (!(ini.get("ModRollen", "Channelmod") == null)) {
				ArrayList<String> Channelmods = new ArrayList<String>();
				String[] Rollen = ini.get("ModRollen", "Channelmod").replaceAll("]", "").replaceAll("\\s", "").split(",");

				for (int i = 0; i < Rollen.length; i++) {
					Channelmods.add(Rollen[i]);
				}

				UserRollen.removeAll(Channelmods);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 1;
				}
			}
		} catch (IOException e) {
			logger.error("IO-Fehler bei Modkontrolle:", e);
			return -1;
		} catch (Exception e) {
			logger.error("Fehler bei Modkontrolle:", e);
			return -1;
		}
		return 0;
	}
}
