package de.Strobl.Commands.Buttons;

import java.awt.Color;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;

public class BanButton {
	private static final Logger logger = LogManager.getLogger(BanButton.class);

	public static void bancheck(ButtonClickEvent event, InteractionHook hook, String id) {
		try {
			event.getMessage().editMessage(event.getMessage().getContentRaw() + " Button clicked!").queue();
			TextChannel channel = event.getGuild().getTextChannelById("771465048047747112");
			channel.sendMessage("Bist du dir sicher, dass du den User <@" + id + "> bannen willst?").setActionRow(Button.danger("finalban " + id, "User bannen")).queue();
		} catch (Exception e) {
			event.getMessage().reply("Fehler bei der Buttonauswertung!").queue();
			logger.error("Fehler Button Auswertung", e);
		}
	}

	public static void banchecked(ButtonClickEvent event, InteractionHook hook, String id) {
		try {
			Member member = Discord.getmember(event.getGuild(), id);
			if (member == null) {
				baninstance(event, hook, id, event.getGuild(), null, false);
			} else {
				member.getUser().openPrivateChannel().queue(channel -> {
					EmbedBuilder builder = Discord.standardEmbed(Color.RED, "Ban vom Server:", id, member.getEffectiveAvatarUrl());
					builder.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
					builder.addField("Grund:", "Teilen von Scam Links.", true);
					channel.sendMessageEmbeds(builder.build()).queue(success -> {
						baninstance(event, hook, id, event.getGuild(), member, true);
					}, e -> {
						baninstance(event, hook, id, event.getGuild(), member, false);
					});
				}, e -> {
					baninstance(event, hook, id, event.getGuild(), member, false);
				});
			}
		} catch (Exception e) {
			event.getMessage().reply("Fehler bei Ban Button ausführung! " + event.getMember().getAsMention()).queue();
			logger.error("Fehler bei Ban Button ausführung checked", e);
		}
	}

	private static void baninstance(ButtonClickEvent event, InteractionHook hook, String id, Guild guild, Member member, Boolean info) {
		String avatar = null;
		if (!(member == null)) {
			avatar = member.getEffectiveAvatarUrl();
		}
		EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "User wurde vom Server gebannt:", id, avatar);
		guild.ban(id, 7, "Button Ban durch Scam Detection").queue(success -> {
			try {
				Strafe strafe = new Strafe(id, StrafenTyp.BAN, "Automatischer Ban durch ScamProtection", event.getMember().getId()).save();
				Integer size = Strafe.getSQLSize(id, StrafenTyp.BAN);
				if (member == null) {
					builder.addField("Ban-ID: " + strafe.getId(), id + "'s Ban Nr: " + size, false);
				} else {
					builder.addField("Ban-ID: " + strafe.getId(), member.getEffectiveName() + "'s Ban Nr: " + size, false);
				}
			} catch (SQLException e) {
				logger.error("Fehler SQL Ban Button", e);
				builder.addField("Konnte Strafe nicht in Datenbank abspeichern.", "Ban war erfolgreich", false);
			}
			if (!info) {
				builder.setDescription("User konnte nicht Informiert werden!");
			}
			builder.addField("Grund:", "Automatisch generierter Ban, ausgelöst durch die ScamProtection.\nBestätigt durch " + event.getMember().getAsMention(), true);
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			event.getChannel().sendMessage("User: <@" + id + ">").setEmbeds(builder.build()).queue();
			event.getMessage().delete().queue();

		}, e -> {
			String failure;
			if (info) {
				failure = "Der User wurde bereits über den Ban informiert!";
			} else {
				failure = "Der User wurde noch nicht über den Ban informiert!";
			}
			event.getMessage().reply("Fehler beim bannen des Users. " + failure).queue();
			event.getMessage().delete().queue();
			logger.error("Fehler bei BanButton Ausführung!", e);
		});
	}
}
