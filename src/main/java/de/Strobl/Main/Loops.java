package de.Strobl.Main;

import java.awt.Color;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.Instances.Discord;
import de.Strobl.Instances.StrafeTemp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;

public class Loops implements Runnable {
	private static final Logger logger = LogManager.getLogger(Loops.class);
	private static JDA jda = Main.jda;
	private static Guild guild = jda.getGuilds().get(0);

	@Override
	public void run() {
		try {
			StrafeTemp.getAll().forEach(strafeTemp -> {
				if (!strafeTemp.getDateTime().isAfterNow()) {

					String typ = strafeTemp.getStrafenTyp().toString();
					String id = strafeTemp.getID();
					String userid = strafeTemp.getUserID();
					// Unban
					if (typ.equalsIgnoreCase("Ban")) {
						logger.info(strafeTemp.getID() + " " + strafeTemp.getUserID() + " " + strafeTemp.getDateTime().toString() + " "
								+ strafeTemp.getStrafenTyp().toString());
						guild.unban(UserSnowflake.fromId(userid)).queue(success -> {
							try {
								EmbedBuilder builder = Discord.standardEmbed(Color.BLUE, "Tempban User entbannt", userid, null);
								guild.getTextChannelById(Settings.LogChannel).sendMessage("User: <@" + userid + ">").setEmbeds(builder.build()).queue();
								logger.info(id + " wurde gerade entbannt!");
								strafeTemp.delete();
							} catch (SQLException e) {
								logger.error("SQLFehler bei Automatischem Entbann. User wurde entbannt! UserID: " + userid, e);
							} catch (Exception e) {
								logger.error("Fehler bei Automatischem Entbann. User wurde entbannt!", e);
							}
						}, e -> {
							if (e.getClass().getName().equals("net.dv8tion.jda.api.exceptions.ErrorResponseException")) {
								try {
									logger.info("TempBan User bereits entbannt. UserID: " + userid);
									strafeTemp.delete();
								} catch (SQLException e1) {
									logger.error("SQLFehler bei Automatischem Entbann.", e);
								}
								return;
							} else {
								logger.error("Fehler Automatischer Entbann.", e);
							}
						});
					}

				}
			});

		} catch (Exception e) {
			logger.error("Loop Temporäre Strafen Fehler", e);
		}
	}
}
