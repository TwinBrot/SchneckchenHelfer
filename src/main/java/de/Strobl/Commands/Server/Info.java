package de.Strobl.Commands.Server;

import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Info extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(Info.class);

	public static void slashcommandevent(SlashCommandEvent event, Member member, User user, InteractionHook EventHook) {
		try {

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Userinformationen:", user.getId(),
					user.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			instance(builder, member, user);
			EventHook.editOriginal(user.getAsMention()).setEmbeds(builder.build()).queue();
			builder.clear();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}

	}

	public static void messagereceivedevent(MessageReceivedEvent event, Member member, User user) {
		try {
			if (!event.isFromGuild()) {
				return;
			}
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Userinformationen:", user.getId(),
					user.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			instance(builder, member, user);
			event.getChannel().sendMessage(user.getAsMention()).setEmbeds(builder.build()).allowedMentions(Collections.emptyList()).queue();
			builder.clear();
		} catch (Exception e) {
			logger.error("Fehler beim Abrufen der Userdaten:", e);
			event.getChannel().sendMessage("Fehler beim Auslesen der Userdaten!").queue();
		}
	}

	public static EmbedBuilder instance(EmbedBuilder builder, Member member, User user) throws Exception {
		ZoneId zone = ZoneId.of("Europe/Paris");
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd.MM.yyyy \n HH:mm").withZone(zone);

		builder.addField("Account erstellt:", user.getTimeCreated().format(date), true);

		if (member == null) {

		} else {
			builder.setColor(member.getColor());
			builder.addField("Serverbeitritt:", member.getTimeJoined().format(date), true);

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

			builder.addField("Rollen:", roles, true);
		}
		
		String hinweise = "";
		String warns = "";
		String bans = "";
		String kicks = "";
		String mutes = "";
		List<Strafe> list = Strafe.getAll(user.getId());
		
		for (Strafe strafe:list) {
			switch (strafe.getTyp()) {
			case BAN:
				bans = stringbuilder(bans, strafe);
				break;
			case HINWEIS:
				hinweise = stringbuilder(hinweise, strafe);
				break;
			case KICK:
				kicks = stringbuilder(kicks, strafe);
				break;
			case MUTE:
				mutes = stringbuilder(mutes, strafe);
				break;
			case TEMPBAN:
				mutes = stringbuilder(bans, strafe);
				break;
			case WARN:
				warns = stringbuilder(warns, strafe);
				break;
			}
			
		}
		
		
		if (!hinweise.equals("")) {
			builder.addField("Hinweise:", hinweise, false);
		}
		
		if (!warns.equals("")) {
			builder.addField("Warns:", warns, false);
		}
		
		if (!kicks.equals("")) {
			builder.addField("Kicks:", kicks, false);
		}
		
		if (!mutes.equals("")) {
			builder.addField("Mutes:", mutes, false);
		}
		
		if (!bans.equals("")) {
			builder.addField("Bans:", bans, false);
		}
		
		return builder;
	}
	
	private static String stringbuilder (String old, Strafe add) {
		String neu = "Strafen-ID: " + add.getId() + " Grund: " + add.getText();
		if (old.equals("")) {
			old = neu;
		} else {
			old = old + "\n" + neu;
		}
		
		return old;
	}
}
