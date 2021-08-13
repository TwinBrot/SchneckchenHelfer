package de.Strobl.Commands.Server.UserInfo;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.ini4j.Wini;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Info {
	public static void info (SlashCommandEvent event, Member member, InteractionHook EventHook) {
		ZoneId zone = ZoneId.of("Europe/Paris");
		
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd.MM.yyyy \n HH:mm").withZone(zone);
		
		try{

			List<Role> rolesList = member.getRoles();
			String roles;
			if (!rolesList.isEmpty()) {
				Role tempRole = (Role) rolesList.get(0);
				roles = tempRole.getAsMention();
				for (int i = 1; i < rolesList.size(); i++) {
					tempRole = (Role) rolesList.get(i);
					roles = roles + ", " + tempRole.getAsMention();
				}
			} else {
				roles = "Keine Rollen";
			}
			
			EmbedBuilder info = new EmbedBuilder();
			info.setAuthor(member.getEffectiveName() + "     UserID: " + member.getId(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
			info.addField("Serverbeitritt:", member.getTimeJoined().format(date), true);
			info.addField("Account erstellt:", member.getUser().getTimeCreated().format(date), true);
			info.setColor(member.getColor());
			info.addField("Rollen:", roles, true);
			info.setFooter("Angefragt von: " + event.getMember().getEffectiveName());
			info.setTimestamp(LocalDateTime.now());
			try {
				Wini ini1 = null;
		  		ini1 = new Wini (new File(Main.Userpfad + member.getId() + ".ini"));
				info.addField("Bans:", ini1.get("Bans").size() + "", true);
			}catch (Exception e) {
				info.addField("Bans:", "Keine", true);
			}
//			try {
//				Wini ini1 = null;
//		  		ini1 = new Wini (new File(SchneckchenHelfer.Userpfad + member.getId() + ".ini"));
//				info.addField("Verwarnungen:", ini1.get("Warns").size() + "", true);
//			}catch (Exception e) {
//				info.addField("Verwarnungen:", "Es liegen keine Verwarnungen vor", true);
//			}
			try {
				Wini ini1 = null;
		  		ini1 = new Wini (new File(Main.Userpfad + member.getId() + ".ini"));
				info.addField("Hinweise:", ini1.get("Hinweise").size() + "", true);
			}catch (Exception e) {
				info.addField("Hinweise:", "Keine", true);
			}
			event.getChannel().sendMessageEmbeds(info.build()).queue();
			EventHook.editOriginal("Erledigt.").queue();
			
		}catch (Exception e) {
			e.printStackTrace();
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
			return;
		}
		
	}
}
