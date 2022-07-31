package de.Strobl.Events.Nachrichten;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class InviteDetection extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(InviteDetection.class);
	private static String invite = "https://discord.gg/";

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
			if (!event.isFromGuild()) {
				return;
			}
			String content = event.getMessage().getContentRaw();
			if (content.toLowerCase().contains(invite)) {
				String[] split = content.split("\\s");
				for (int i = 0; i < split.length; i++) {
					String splitcontent = split[i];

					if (splitcontent.toLowerCase().startsWith(invite)) {
						Invite.resolve(event.getJDA(), splitcontent.replace(invite, "").replaceAll("/", "")).queue(temp -> {
							try {
								if (!temp.getGuild().getId().equals(event.getGuild().getId())) {

									if (Discord.isMod(event.getMember()) > 0) {
										logger.info("Servereinladung erkannt. Author ist Mod, daher nicht gelöscht.");
										return;
									}

									event.getMember().getUser().openPrivateChannel().queue(pc -> {
										EmbedBuilder builder = Discord.standardEmbed(Color.red, "TimeOut", event.getMember().getId(),
												event.getMember().getUser().getEffectiveAvatarUrl());
										builder.setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl());
										builder.setDescription("Dein Account wurde gerade automatisch für 10 Minuten getimeoutet!");
										builder.addField("Grund:", "Senden eines Discord-Invitelinks!", true);
										builder.addField("Die Moderatoren werden das kontrollieren.",
												"Wenn sich der Verdacht nicht bestätigt, werden die Moderatoren dich wieder freischalten.", false);
										pc.sendMessageEmbeds(builder.build()).queue(msg -> {
										}, e -> {
										});
										event.getMember().timeoutFor(10, TimeUnit.MINUTES).queueAfter(1, TimeUnit.SECONDS);
									});

									event.getMessage().delete().queue(success -> {
										try {
											String title = "Invite eines anderen Servers erkannt. Nachricht gelöscht!";
											Member member = event.getMember();
											EmbedBuilder builder = Discord.standardEmbed(Color.BLUE, title, member.getId(), member.getEffectiveAvatarUrl());
											builder.addField("Channel: " + event.getGuildChannel().getName() + "     Nachrichten Text: ", content, true);
											TextChannel channel = event.getGuild().getTextChannelById(Settings.LogChannel);
											channel.sendMessage("User: " + member.getAsMention() + " Notification: <@227131380058947584> <@140206875596685312>")
													.setEmbeds(builder.build()).setActionRow(Button.danger("ban " + member.getId(), "Ban User")).queue();
										} catch (Exception e) {
											logger.error("Fehler Invite Detection", e);
										}
									}, e -> {
										if (!e.getClass().getName().equals("net.dv8tion.jda.api.exceptions.ContextException")) {
											logger.error("Fehler Invite Detection", e);
										}
									});
								}
							} catch (Exception e) {
								logger.error("Fehler Invite Detection", e);
							}
						});
						return;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Fehler Invite Detection", e);
		}
	}
}