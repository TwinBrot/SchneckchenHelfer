package de.Strobl.Events.User;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnuserUpdateNameEvent extends ListenerAdapter {
	public Wini ini;
	private static final Logger logger = LogManager.getLogger(OnuserUpdateNameEvent.class);

	@Override
	public void onUserUpdateName(UserUpdateNameEvent event) {
		try {
			Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
//Namensüberwachung aktiv
			if (ini.get("Namensüberwachung", "Active").equals("true")) {
				String ID = event.getUser().getId();
//Ausnahmen
				if (!ID.equals("137612175454765056") && !ID.equals("196990278643613696") && !ID.equals("137267295801049088")
						&& !ID.equals("137300611212247040") && !ID.equals("109777843046645760") && !ID.equals("137258978479439873")
						&& !ID.equals("227131380058947584")) {
//ArrayList verbotener Namen
					String[] Name = ini.get("Namensüberwachung", "Verboten").replaceAll("]", "").replaceAll("\\s", "").split(",");
					ArrayList<String> Namen = new ArrayList<String>();
					for (int i = 0; i < Name.length; i++) {
						Namen.add(Name[i]);
					}
//Namens erkennung
					for (int i = 0; i < Namen.size(); i++) {
						if (event.getUser().getName().toLowerCase().contains(Namen.get(i))) {
							String LogChannelID = ini.get("Settings", "LogChannel");
							if (!LogChannelID.equals("")) {

								EmbedBuilder builder = Discord.standardEmbed(Color.RED, "Illegaler Name nach Namensänderung", ID, event.getUser().getEffectiveAvatarUrl());
								builder.addField("Username: " + event.getUser().getName(), "Erkannter Bestandteil: " + Namen.get(i), true);
								event.getJDA().getGuilds().get(0).getTextChannelById(LogChannelID).sendMessage("User: " + event.getUser().getAsMention() + " Notification: <@227131380058947584>").setEmbeds(builder.build()).queue();
								builder.clear();
								return;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("IO-Fehler", e);
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}