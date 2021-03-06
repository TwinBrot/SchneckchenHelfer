package de.Strobl.Events.User;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinNamens├╝berwachung extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(JoinNamens├╝berwachung.class);

	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		try {
//Namens├╝berwachung aktiv
			if (Settings.NamenActive) {
				String ID = event.getMember().getId();
//Ausnahmen
				if (!ID.equals("137612175454765056") && !ID.equals("196990278643613696") && !ID.equals("137267295801049088") && !ID.equals("137300611212247040") && !ID.equals("109777843046645760")
						&& !ID.equals("137258978479439873") && !ID.equals("227131380058947584")) {
//ArrayList verbotener Namen
					String[] Name = Settings.Namen;
					ArrayList<String> Namen = new ArrayList<String>();
					for (int i = 0; i < Name.length; i++) {
						Namen.add(Name[i]);
					}
//Namens erkennung
					for (int i = 0; i < Namen.size(); i++) {
						if (event.getUser().getName().toLowerCase().contains(Namen.get(i))) {
							EmbedBuilder builder = Discord.standardEmbed(Color.RED, "Neuer User mit verbotenem Namen", ID, event.getMember().getEffectiveAvatarUrl());
							builder.addField("Username: " + event.getMember().getEffectiveName(), "Erkannter Bestandteil: " + Namen.get(i), true);
							event.getGuild().getTextChannelById(Settings.LogChannel).sendMessage("User: " + event.getMember().getAsMention() + " Notification: <@227131380058947584>")
									.setEmbeds(builder.build()).queue();
							builder.clear();
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Fehler", e);
		}
	}
}