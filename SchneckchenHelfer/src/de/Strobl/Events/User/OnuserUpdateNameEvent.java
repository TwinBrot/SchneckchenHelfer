package de.Strobl.Events.User;

import java.io.File;
import java.util.ArrayList;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnuserUpdateNameEvent extends ListenerAdapter {
	public Wini ini;

	@Override
	public void onUserUpdateName(UserUpdateNameEvent event) {
		try {
			ini = new Wini(new File(Main.Pfad + "settings.ini"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String Alarm;
		Alarm = "false";
		String[] Name = ini.get("Namensüberwachung", "Verboten").replaceAll("]", "").replaceAll("\\s", "").split(",");
		ArrayList<String> Namen = new ArrayList<String>();
		for (int i = 0; i < Name.length; i++) {
			Namen.add(Name[i]);
		}
		Namen.set(0, "*~+");
//Namens erkennung
		for (int i = 0; i < Namen.size(); i++) {
			if (event.getUser().getName().toLowerCase().contains(Namen.get(i).toLowerCase())) {
				Alarm = "true";
			}
		}
//Alarm im LogChannel
		if (Alarm.equals("true") && ini.get("Namensüberwachung", "Active").equals("true")) {
			if (!event.getUser().getId().equals("137612175454765056")
					&& !event.getUser().getId().equals("196990278643613696")
					&& !event.getUser().getId().equals("137267295801049088")
					&& !event.getUser().getId().equals("137300611212247040")
					&& !event.getUser().getId().equals("109777843046645760")
					&& !event.getUser().getId().equals("137258978479439873")
					&& !event.getUser().getId().equals("227131380058947584")) {
				try {
					String ID = ini.get("Settings", "Settings.LogChannel");
					EmbedBuilder join = new EmbedBuilder();
					join.setColor(0x110acc);
					join.setAuthor("Verbotener Name", event.getJDA().getGuilds().get(0).getIconUrl(),
							event.getJDA().getGuilds().get(0).getIconUrl());
					join.addField("UserID: " + event.getUser().getId(), "User: " + event.getUser().getAsMention(),
							false);
					join.setFooter("Neuer Name: " + event.getUser().getName());
					event.getJDA().getGuilds().get(0).getTextChannelById(ID).sendMessageEmbeds(join.build()).queue();
					event.getJDA().getGuilds().get(0).getTextChannelById(ID).sendMessage(
							event.getJDA().getGuilds().get(0).getMemberById("227131380058947584").getAsMention())
							.queue();
					join.clear();
					// Logchannel nicht eingerichtet
				} catch (Exception e) {
					System.out.println("Kein Logchannel eingerichtet");
				}
			}
		}
	}
}