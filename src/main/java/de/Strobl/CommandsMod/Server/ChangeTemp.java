package de.Strobl.CommandsMod.Server;

import java.awt.Color;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.StrafeTemp;
import de.Strobl.Instances.StrafenTyp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ChangeTemp {
	private static final Logger logger = LogManager.getLogger(ChangeTemp.class);

	public static void onSlashCommand(SlashCommandInteractionEvent event, User user, InteractionHook hook, DateTime time, StrafenTyp typ) {
		try {
			boolean done = false;
			List<StrafeTemp> list = StrafeTemp.getByUserId(user.getId());
			for (int i = 0; i < list.size(); i++) {
				StrafeTemp temp = list.get(i);
				if (temp.getStrafenTyp() == typ) {
					temp.updateTime(time);
					done = true;
				}
			}

			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Dauer geändert", user.getId(), user.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			if (typ == StrafenTyp.BAN) {
				builder.addField("Neuer Zeitpunkt des Entbanns:", time.toString("dd.MM.yyyy kk:mm"), false);
			} else if (typ == StrafenTyp.MUTE) {
				builder.addField("Neuer Zeitpunkt des Unmutes:", time.toString("dd.MM.yyyy kk:mm"), false);
			}
			if (done) {
				event.getHook().editOriginalEmbeds(builder.build()).queue();
			} else {
				event.getHook().editOriginal("Temporärer " + typ.toString() + " konnte nicht gefunden werden.").queue();
			}

		} catch (Exception e) {
			event.getHook().editOriginal("Fehler beim Aktuallisieren der Dauer!").queue();
			logger.error("Fehler beim Updaten der Strafe", e);
		}
	}
}
