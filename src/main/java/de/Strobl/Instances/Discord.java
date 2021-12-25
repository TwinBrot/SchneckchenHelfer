package de.Strobl.Instances;

import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Discord {
	private static final Logger logger = LogManager.getLogger(Discord.class);

	public static EmbedBuilder standardEmbed(Color color, String title, String userid, String membericon) {
		EmbedBuilder builder = new EmbedBuilder();
		if (color == null) {
			color = Color.ORANGE;
		}
		if (userid == null) {
			userid = "No ID given";
		}
		builder.setTitle(title);
		builder.setFooter(userid, membericon);
		builder.setColor(color);
		builder.setTimestamp(ZonedDateTime.now().toInstant());
		return builder;
	}
	
	public static Integer isMod(Member member) {

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
			if (member.getId().equals("227131380058947584")) {
				return 3;
			}


//Userrollen auslesen

			ArrayList<String> UserRollen = new ArrayList<String>();
			for (int i = 0; i < member.getRoles().size(); i++) {
				UserRollen.add(member.getRoles().get(i).getId().toString());
			}

//Administrator

			if (!(Settings.Admin.length==0)) {
				ArrayList<String> Admins = new ArrayList<String>();
				String[] Rollen = Settings.Admin;

				for (int i = 0; i < Rollen.length; i++) {
					Admins.add(Rollen[i]);
				}
				UserRollen.removeAll(Admins);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 3;
				}
			}

//Moderator

			if (!(Settings.Mod.length==0)) {
				ArrayList<String> Mods = new ArrayList<String>();
				String[] Rollen = Settings.Mod;

				for (int i = 0; i < Rollen.length; i++) {
					Mods.add(Rollen[i]);
				}

				UserRollen.removeAll(Mods);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 2;
				}
			}

//Channelmod

			if (!(Settings.Channelmod.length==0)) {
				ArrayList<String> Channelmods = new ArrayList<String>();
				String[] Rollen = Settings.Channelmod;

				for (int i = 0; i < Rollen.length; i++) {
					Channelmods.add(Rollen[i]);
				}

				UserRollen.removeAll(Channelmods);
				if (!(UserRollen.size() == member.getRoles().size())) {
					return 1;
				}
			}
		} catch (Exception e) {
			logger.error("Fehler bei Modkontrolle:", e);
			return -1;
		}
		return 0;
	}

	public static ArrayList<Field> SplitTexttoField(String original, String firsttitle) {
		String[] originalsplit = original.split("\\s");
		ArrayList<Field> list = new ArrayList<Field>();
		String temp = "";
		if (original.equals("")) {
			list.add(new Field(firsttitle, "`Nicht angegeben`", false));
			return list;
		}
		for (int i = 0; i < originalsplit.length; i++) {
			if (temp.length() + originalsplit[i].length() < 1000) {
				temp = temp + " " + originalsplit[i];
			} else {
				if (list.size() == 0) {
					list.add(new Field(firsttitle, temp, false));
				} else {
					list.add(new Field("", temp, false));
				}
				temp = originalsplit[i];
			}
		}
		if (!temp.equals("")) {
			if (list.size() == 0) {
				list.add(new Field(firsttitle, temp, false));
			} else {
				list.add(new Field("", temp, false));
			}
		}
		return list;
	}

	public static String trim(String text) {
		if (text.length() <= 512) {
			return text;
		} else {
			String temp = text.substring(0, 509) + "...";
			return temp;
		}
	}
}