package de.Strobl.Instances;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class getMember {
	private static final Logger logger = LogManager.getLogger(getMember.class);
	public static Member getmember(Guild guild, String UserID) {
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