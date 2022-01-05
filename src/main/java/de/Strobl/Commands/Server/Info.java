package de.Strobl.Commands.Server;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Instances.Strafe;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Info extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(Info.class);

	public static void slashcommandevent(SlashCommandEvent event, User user, InteractionHook EventHook) {
		try {
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Userinformationen:", user.getId(), user.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			List<Strafe> list = Strafe.getAll(user.getId());
			ArrayList<EmbedBuilder> embeds = new ArrayList<EmbedBuilder>();
			list.forEach(strafe -> {
				List<Field> listfield = Discord.SplitTexttoField(strafe.getText(), strafe.getTypString() + ": " + strafe.getId() + " Zuständiger Mod(ID): " + strafe.getModID());
				Integer size = 0;
				for (int i = 0; i < listfield.size(); i++) {
					size = size + listfield.get(i).getValue().length() + listfield.get(i).getName().length();
				}
				if (builder.length() + size > 6000) {
					EmbedBuilder temp = new EmbedBuilder();
					temp.copyFrom(builder);
					embeds.add(temp);
					builder.clearFields();
				}
				listfield.forEach(field -> {
					builder.addField(field);
				});
			});
			if (!(builder.getFields().size() == 0)) {
				embeds.add(builder);
			}
			if ((embeds.size() == 0)) {
				EventHook.editOriginal("Der User <@" + user.getId() + "> hat keine Hinweise/Warns/Kicks/Bans/Mutes").queue();
			}
			for (int i = 0; i < embeds.size(); i++) {
				if (i == 0) {
					EventHook.editOriginal("User: " + user.getAsMention()).setEmbeds(embeds.get(i).build()).queue();
				} else {
					event.getChannel().sendMessage("User: " + user.getAsMention()).setEmbeds(embeds.get(i).build()).allowedMentions(Collections.emptyList()).queue();
				}
			}
			builder.clear();
		} catch (Exception e) {
			logger.error("Fehler bei Info-Befehl", e);
			EventHook.editOriginal("Fehler beim Ausführen.").queue();
		}

	}

	public static void messagereceivedevent(MessageReceivedEvent event, User user) {
		try {
			if (!event.isFromGuild()) {
				return;
			}
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Userinformationen:", user.getId(), user.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
			List<Strafe> list = Strafe.getAll(user.getId());
			list.forEach(strafe -> {
				List<Field> listfield = Discord.SplitTexttoField(strafe.getText(), strafe.getTypString() + ": " + strafe.getId() + " Zuständiger Mod(ID): " + strafe.getModID());
				Integer size = 0;
				for (int i = 0; i < listfield.size(); i++) {
					size = size + listfield.get(i).getValue().length() + listfield.get(i).getName().length();
				}
				if (builder.length() + size > 6000) {
					event.getMessage().reply("User: " + user.getAsMention()).setEmbeds(builder.build()).queue();
					builder.clearFields();
				}
				listfield.forEach(field -> {
					builder.addField(field);
				});
			});
			if (builder.getFields().size() == 0) {
				event.getMessage().reply("Der User <@" + user.getId() + "> hat keine Hinweise/Warns/Kicks/Bans/Mutes").allowedMentions(Collections.emptyList()).queue();
			} else {
				event.getMessage().reply("User: " + user.getAsMention()).setEmbeds(builder.build()).allowedMentions(Collections.emptyList()).queue();
			}
			builder.clear();
		} catch (Exception e) {
			logger.error("Fehler beim Abrufen der Userdaten:", e);
			event.getChannel().sendMessage("Fehler beim Auslesen der Userdaten!").queue();
		}
	}
}
