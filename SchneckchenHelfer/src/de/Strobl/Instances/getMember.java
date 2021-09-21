package de.Strobl.Instances;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class getMember {
	public static Member getmember(Guild guild, String UserID) {
		Logger logger = Main.logger;
		logger.info("getMember: " + UserID);
		Member member = null;
		try {
			member = guild.getMemberById(UserID.replaceAll("<", "").replaceAll(">", "").replaceAll("@", "").replaceAll("!", ""));
			logger.info("gotMember: " + member);
		} catch (Exception e) {
			try {
				member = guild.retrieveMemberById(UserID.replaceAll("<", "").replaceAll(">", "").replaceAll("@", "").replaceAll("!", "")).complete();
				logger.info("retrievedMember: " + member);
			} catch (Exception e1) {
			}
		}
		return member;
	}
	public static Member getmember(Guild guild, User user) {
		return getmember(guild, user.getId());
	}
}