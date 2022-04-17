package de.Strobl.CommandsMod;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.CommandsFunsies.Wordle.WordleCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ModalInteraction extends ListenerAdapter {
	private static final Logger logger = LogManager.getLogger(ModalInteraction.class);

	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		event.deferEdit().queue();
		InteractionHook hook = event.getHook();
		try {

			// Console Output
			String componentID = event.getInteraction().getModalId();
			logger.info("Modal erkannt: _______________________________________________________________________");
			logger.info("");
			logger.info(event.getUser().getName());
			logger.info(componentID);
			
			
			
			if (event.getModalId().equals("wordle")) {
				WordleCommand.wordlemodal(event, hook);
			}
			;

		} catch (Exception e) {
			event.getMessageChannel().sendMessage("Fehler beim Auswerten des Modals").queue(msg -> {
				msg.delete().queueAfter(1, TimeUnit.MINUTES);
			});
			logger.error("Fehler Modal Auswertung", e);
		}
	}
}
