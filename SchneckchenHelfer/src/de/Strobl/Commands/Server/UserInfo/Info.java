package de.Strobl.Commands.Server.UserInfo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Info {
	public static void slashcommandevent(SlashCommandEvent event, Member member, InteractionHook EventHook) {
		Logger logger = Main.logger;
		try {
			EmbedBuilder UserInfo = new EmbedBuilder();
			instance(UserInfo, member, event.getMember());
			event.getChannel().sendMessageEmbeds(UserInfo.build()).queue();
			UserInfo.clear();
			EventHook.editOriginal("Erledigt.").queue();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}

	}

	public static void messagereceivedevent(GuildMessageReceivedEvent event, Member member) {
		Logger logger = Main.logger;
		try {
			EmbedBuilder UserInfo = new EmbedBuilder();
			instance(UserInfo, member, event.getMember());
			event.getChannel().sendMessageEmbeds(UserInfo.build()).queue();
			UserInfo.clear();
		} catch (Exception e) {
			logger.error("Fehler beim Abrufen der Userdaten:", e);
			event.getChannel().sendMessage("Fehler beim Auslesen der Userdaten!").queue();
		}
	}
	
	public static EmbedBuilder instance(EmbedBuilder UserInfo, Member member, Member mod) throws Exception {
		ZoneId zone = ZoneId.of("Europe/Paris");
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd.MM.yyyy \n HH:mm").withZone(zone);

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

		UserInfo.setAuthor(member.getEffectiveName() + "     UserID: " + member.getId(), member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl());
		UserInfo.addField("Serverbeitritt:", member.getTimeJoined().format(date), true);
		UserInfo.addField("Account erstellt:", member.getUser().getTimeCreated().format(date), true);
		UserInfo.setColor(member.getColor());
		UserInfo.addField("Rollen:", roles, true);
		UserInfo.setFooter("Angefragt von: " + mod.getEffectiveName());
		UserInfo.setTimestamp(LocalDateTime.now());

//		try {
//			Wini ini1 = null;
//			ini1 = new Wini(new File(Main.Userpfad + member.getId() + ".ini"));
//			UserInfo.addField("Bans:", ini1.get("Bans").size() + "", true);
//		} catch (Exception e) {
//			UserInfo.addField("Bans:", "Keine", true);
//		}
//		try {
//			Wini ini1 = null;
//	  		ini1 = new Wini (new File(SchneckchenHelfer.Userpfad + member.getId() + ".ini"));
//			UserInfo.addField("Verwarnungen:", ini1.get("Warns").size() + "", true);
//		}catch (Exception e) {
//			UserInfo.addField("Verwarnungen:", "Es liegen keine Verwarnungen vor", true);
//		}
//		try {
//			Wini ini1 = null;
//			ini1 = new Wini(new File(Main.Userpfad + member.getId() + ".ini"));
//			UserInfo.addField("Hinweise:", ini1.get("Hinweise").size() + "", true);
//		} catch (Exception e) {
//			UserInfo.addField("Hinweise:", "Keine", true);
//		}
		return UserInfo;
	}
}
