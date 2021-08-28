package de.Strobl.Events.User;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMemberJoinEvent extends ListenerAdapter {
	Wini ini;

	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Logger logger = Main.logger;

		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
		String Alarm;
		Alarm = "false";
		String[] Name = ini.get("Namensüberwachung", "Verboten").replaceAll("]", "").replaceAll("\\s", "").split(",");

		ZoneId zone = ZoneId.of("Europe/Paris");
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(zone);

		ArrayList<String> Namen = new ArrayList<String>();
		for (int i = 0; i < Name.length; i++) {
			Namen.add(Name[i]);
		}
		Namen.set(0, "*~+");
//Namens erkennung
		for (int i = 0; i < Namen.size(); i++) {
			if (event.getUser().getName().toLowerCase().contains(Namen.get(i))) {
				Alarm = "true";
			}
		}
//Alarm im LogChannel
		if (Alarm.equals("true") && ini.get("Namensüberwachung", "Active").equals("true")) {
			if (!event.getMember().getId().equals("137612175454765056")
					&& !event.getMember().getId().equals("196990278643613696")
					&& !event.getMember().getId().equals("137267295801049088")
					&& !event.getMember().getId().equals("137300611212247040")
					&& !event.getMember().getId().equals("109777843046645760")
					&& !event.getMember().getId().equals("137258978479439873")
					&& !event.getMember().getId().equals("227131380058947584")) {
				try {
					String ID = ini.get("Settings", "Settings.LogChannel");
					EmbedBuilder join = new EmbedBuilder();
					join.setColor(0x110acc);
					join.setAuthor("Verbotener Name", event.getJDA().getGuilds().get(0).getIconUrl(),
							event.getJDA().getGuilds().get(0).getIconUrl());
					join.addField("UserID: " + event.getMember().getId(), "User: " + event.getMember().getAsMention(),
							false);
					join.setFooter("Name: " + event.getMember().getUser().getName());
					event.getGuild().getTextChannelById(ID).sendMessageEmbeds(join.build()).queue();
					event.getGuild().getTextChannelById(ID).sendMessage(
							event.getJDA().getGuilds().get(0).getMemberById("227131380058947584").getAsMention())
							.queue();
					join.clear();
					// Kein LogChannel
				} catch (Exception e) {
					logger.info("Kein Logchannel eingerichtet");
				}
			}
		}

		
		
		
//		__________________
//		
//		--JOIN DATUM LOG--
//		__________________

		try {
			Wini iniMember = new Wini(new File(Main.Userpfad + event.getMember().getId() + ".ini"));

			if (iniMember.get("Userdata", "Join").equals(null)) {

//IDs
				iniMember.put("Userdata", "Join", event.getMember().getTimeJoined().format(fmt));
				iniMember.store();

			}

		} catch (Exception e) {
			File newFile = new File(Main.Userpfad + event.getMember().getId() + ".ini");

			try {
				newFile.createNewFile();
			} catch (IOException e1) {
				logger.error("IO-Fehler", e1);
			}
			Wini iniMember = null;
			try {
				iniMember = new Wini(new File(Main.Userpfad + event.getMember().getId() + ".ini"));
			} catch (InvalidFileFormatException e1) {
				logger.error("Fehler", e1);
			} catch (IOException e1) {
				logger.error("Fehler", e1);
			}
//IDs
			iniMember.put("Userdata", "Join", event.getMember().getTimeJoined().format(fmt));

			try {
				iniMember.store();
			} catch (IOException e1) {
				logger.error("IO-Fehler", e1);
			}

		}

	}
}