package de.Strobl.Main;

import java.io.File;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import de.Strobl.Instances.StrafeTemp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class LoopCheckTemp implements Runnable {
	private static final Logger logger = LogManager.getLogger(LoopCheckTemp.class);

	@Override
	public void run() {
		try {
			JDA jda = Main.jda;
			Guild guild = jda.getGuilds().get(0);

			StrafeTemp.getAll().forEach(strafeTemp -> {
				if (strafeTemp.getDateTime().isAfterNow()) {
					String typ = strafeTemp.getStrafenTyp().toString();
					String id = strafeTemp.getID();
					
					
					if (typ.equalsIgnoreCase("ban")) {
						guild.unban(id).queue(success -> {
							try {
								Wini ini = new Wini(new File(Main.Pfad + "settings.ini"));
								EmbedBuilder builder = new EmbedBuilder();
								builder.setAuthor("Automatischer Entbann", null, guild.getIconUrl());
								builder.setDescription("UserID: " + id);
								builder.setTimestamp(ZonedDateTime.now().toInstant());
								builder.setFooter("Log");
								guild.getTextChannelById(ini.get("Settings", "Settings.LogChannel")).sendMessageEmbeds(builder.build()).queue();
								logger.info(id + " wurde gerade entbannt!");
							} catch (Exception e1) {
								logger.error("TempBan User bereits entbannt. UserID: " + id);
							}
						}, e -> {
						});
					} else if (typ.equalsIgnoreCase("mute")) {
						guild.retrieveMemberById(id).queue(member -> {
							guild.removeRoleFromMember(id, guild.getRoleById("728414039713251338")).queue();
						});

					}

				}
			});

		} catch (Exception e) {
			logger.error("Loop Temporäre Strafen Fehler", e);
		}
	}
}
