package de.Strobl.Instances;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class getMember {
	public static Member getmember(SlashCommandEvent event, String UserID, InteractionHook EventHook) {
		Logger logger = Main.logger;
		logger.info("getMember: " + UserID);
		Member member = null;
		try {
			member = event.getGuild().getMemberById(
					UserID.replaceAll("<", "").replaceAll(">", "").replaceAll("@", "").replaceAll("!", ""));
			logger.info("          gotMember: " + member);
		} catch (Exception e) {
			try {
				member = event.getGuild()
						.retrieveMemberById(
								UserID.replaceAll("<", "").replaceAll(">", "").replaceAll("@", "").replaceAll("!", ""))
						.complete();
				logger.info("          retrievedMember: " + member);
			} catch (Exception e1) {
				EventHook.editOriginal("User nicht erkannt").queue();
			}
		}
		return member;
	}
}