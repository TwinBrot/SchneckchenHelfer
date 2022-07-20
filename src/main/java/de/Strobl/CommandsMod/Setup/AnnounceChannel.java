package de.Strobl.CommandsMod.Setup;

import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class AnnounceChannel {
	private static final Logger logger = LogManager.getLogger(AnnounceChannel.class);

	public static void setup(SlashCommandInteractionEvent event) {
		try {
			GuildChannel Channel = event.getOption("textchannel").getAsChannel();
			if (!(Channel.getType() == ChannelType.TEXT)) {
				event.getHook().editOriginal(Channel.getAsMention() + " ist kein TextChannel, sondern ein: " + Channel.getType()).queue();
				return;
			}
			String ChannelID = Channel.getId();
			TextChannel textchannel = event.getGuild().getTextChannelById(ChannelID);
			if (!textchannel.canTalk()) {
				event.getHook().editOriginal("Ich habe nicht die notwendigen Rechte, um in " + Channel.getAsMention() + " zu schreiben!").queue();
				return;
			}

			Settings.set("Settings", "AnnounceChannel", ChannelID);

			User self = event.getJDA().getSelfUser();
			String title = "AnnounceChannel eingestellt: " + "`" + textchannel.getName() + "`";
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, title, self.getId(), self.getEffectiveAvatarUrl());
			builder.setAuthor(event.getMember().getEffectiveName(), event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
			event.getHook().editOriginal("").setEmbeds(builder.build()).queue();
			builder.clear();

		} catch (Exception e) {
			logger.error("Fehler beim Ändern des Onlinestatus:", e);
			event.getHook().editOriginal("Fehler beim Ändern des Onlinestatus.").queue();
		}
	}
}