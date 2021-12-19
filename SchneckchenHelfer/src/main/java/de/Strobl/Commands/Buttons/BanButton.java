package de.Strobl.Commands.Buttons;

import java.awt.Color;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class BanButton extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(BanButton.class);

	public void onButtonClick(ButtonClickEvent event) {
		try {
// Console Output
			logger.info("Button erkannt: _______________________________________________________________________");
			logger.info("Author: " + event.getMember());
			logger.info(event.getComponentId());

// Only from Guilds
			if (!event.isFromGuild()) {
				return;
			}

// Modrollen Abfragen
// -1 = Fehler
// 0 = User
// 1 = Channelmod
// 2 = Mod
// 3 = Admin
			Integer Modrolle = Discord.isMod(event.getMember());
			if (Modrolle < 2) {
				event.reply("Nicht die nötigen Rechte!").queue();
				return;
			}

			event.deferEdit().queue();

			String componentID = event.getComponentId();
			String id;
			if (componentID.startsWith("ban ")) {
				id = componentID.replaceFirst("ban ", "");
				event.getMessage().editMessage(event.getMessage().getContentRaw() + " Button clicked!").queue();
				event.getGuild().getTextChannelById("486955077899386909").sendMessage("Bist du dir sicher, dass du diesen User <@" + id + "> bannen willst ?")
						.setActionRow(Button.danger("finalban " + id, "User bannen")).queue();

			} else if (componentID.startsWith("finalban ")) {
				id = componentID.replaceFirst("finalban ", "");
				event.getGuild().getMemberById(id).getUser().openPrivateChannel().queue(privatchannel -> {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setTimestamp(ZonedDateTime.now());
					builder.setDescription("***Ban auf dem Schneckchencord:*** \n Du wurdest vom Schneckchencord gebannt, weil du Scam Links geteilt hast.");
					privatchannel.sendMessageEmbeds(builder.build()).queue(success -> {
						event.getGuild().ban(id, 1, "Button Ban durch Scam Detection").queue(success2 -> {

							EmbedBuilder builder2 = Discord.standardEmbed(Color.GREEN, "User wurde gebannt", id, null);
							builder2.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
							builder2.addField("Grund:","Automatisch generierter Ban, ausgelöst durch die ScamProtection.\nBestätigt durch <@" + event.getMember().getId() + ">", true);
							event.getMessage().editMessage("<@" + id + ">").setEmbeds(builder2.build()).override(true).queue();
						}, e -> {
							event.getMessage().editMessage("Konnte User <@" + id + "> nicht bannen.").queue();
							logger.error("Fehler Button Auswertung Ban", e);
						});
					}, e -> {
						event.getGuild().ban(id, 1, "Button Ban durch Scam Detection").queue(success -> {

							EmbedBuilder builder2 = Discord.standardEmbed(Color.GREEN, "User wurde gebannt", id, null);
							builder2.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
							builder2.addField("Grund:","Automatisch generierter Ban, ausgelöst durch die ScamProtection.\nBestätigt durch <@" + event.getMember().getId() + ">", true);
							event.getMessage().editMessage("<@" + id + ">").setEmbeds(builder2.build()).override(true).queue();
						}, e1 -> {
							event.getMessage().editMessage("Konnte User <@" + id + "> nicht bannen.").queue();
							logger.error("Fehler Button Auswertung Ban", e);
						});
					});
				}, e-> {
					event.getGuild().ban(id, 1, "Button Ban durch Scam Detection").queue(success -> {

						EmbedBuilder builder2 = Discord.standardEmbed(Color.GREEN, "User wurde gebannt", id, null);
						builder2.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
						builder2.addField("Grund:","Automatisch generierter Ban, ausgelöst durch die ScamProtection.\nBestätigt durch <@" + event.getMember().getId() + ">", true);
						event.getMessage().editMessage("<@" + id + ">").setEmbeds(builder2.build()).override(true).queue();
					}, e1 -> {
						event.getMessage().editMessage("Konnte User <@" + id + "> nicht bannen.").queue();
						logger.error("Fehler Button Auswertung Ban", e);
					});
				});
			}

		} catch (Exception e) {
			logger.error("Fehler Button Auswertung", e);
		}
	}
}
