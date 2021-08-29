package de.Strobl.Events.User;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserInfoJoin extends ListenerAdapter {
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Logger logger = Main.logger;
		try {
			ZoneId zone = ZoneId.of("Europe/Paris");
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(zone);
			String UserID = event.getMember().getId();
			try {
				Wini iniMember = new Wini(new File(Main.Userpfad + UserID + ".ini"));
//Datei existiert, kein Join hinterlegt
				try {
					if (iniMember.get("Userdata", "Join") == null) {
						iniMember.put("Userdata", "Join", event.getMember().getTimeJoined().format(fmt));
						iniMember.store();
					}
				} catch (IOException e) {
					logger.error("IO-Fehler beim Speichern der Userdaten", e);
					return;
				}
			} catch (IOException e) {
//Datei existiert nicht
				File newFile = new File(Main.Userpfad + UserID + ".ini");
				newFile.createNewFile();
				Wini iniMember = new Wini(new File(Main.Userpfad + UserID + ".ini"));
				iniMember.put("Userdata", "Join", event.getMember().getTimeJoined().format(fmt));
				iniMember.store();

			}
		} catch (IOException e1) {
			logger.error("IO-Fehler beim erstellen einer neuer Userdatei:", e1);
			return;
		} catch (Exception e) {
			logger.error("Fehler beim Speichern der Userdaten", e);
		}
	}
}