package de.Strobl.CommandsFunsies.Wordle;

import java.awt.Color;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import de.Strobl.Instances.Discord;
import de.Strobl.Main.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;

public class WordleCommand {
	private static final Logger logger = LogManager.getLogger(WordleCommand.class);

	public static void wordlestart(SlashCommandInteractionEvent event, InteractionHook eventHook) {
		try {
			Wordle old = new Wordle(event.getUser().getIdLong());
			String title = "";
			if (old.datum == null) {
				old.newWordle();
				title = "Herzlich Willkommen zu deinem ersten Wordle! 😇";
			} else {
				if (old.datum.isBefore(DateTime.now().withTimeAtStartOfDay())) {
					old.updateWordle();
					title = "Willkommen zurück zu Wordles!";
				} else {
					if (old.finished) {
						eventHook.editOriginal("Du hast doch bereits gewonnen").queue(msg -> {
							msg.delete().queueAfter(1, TimeUnit.MINUTES);
						});

					} else if (old.Wort6 != null) {
						eventHook.editOriginal("Du kannst das heutige Wordle nicht erneut spielen!").queue(msg -> {
							msg.delete().queueAfter(1, TimeUnit.MINUTES);
						});
					} else {
						eventHook.editOriginal("Du hast das heutige Wordle bereits gestartet! Bitte verwende die Buttons um fortzufahren!").queue(msg -> {
							msg.delete().queueAfter(1, TimeUnit.MINUTES);
						});
					}
					return;
				} ;
			}
			EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, title, event.getUser().getId(), event.getUser().getEffectiveAvatarUrl());
			eventHook.editOriginal("").setEmbeds(builder.build()).setActionRow(Button.of(ButtonStyle.PRIMARY, "wordle", "Wort Prüfen!")).queue();

		} catch (Exception e) {
			logger.error("Fehler bei Wordle-Start-Befehl ausführung", e);
			eventHook.editOriginal("Fehler bei Wordle-Start-Befehl ausführung").queue();
		}
	}

	public static void wordlebuttonclick(ButtonInteractionEvent event) {
		try {
			Wordle wordle = new Wordle(event.getUser().getIdLong());
			if (wordle.datum.isBefore(DateTime.now().withTimeAtStartOfDay())) {
				event.getMessage().editMessage("Dieses Wordle ist leider bereits abgelaufen! Starte doch mit /wordle ein neues")
						.setActionRow(Collections.emptyList()).queue();
			}
			if (wordle.Wort6 != null) {
				event.getHook().sendMessage("Du hast bereits alle Versuche aufgebraucht!").queue(msg -> {
					msg.delete().queueAfter(1, TimeUnit.MINUTES);
				});
				return;
			}
		} catch (Exception e) {
		}
		TextInput wordlewort = TextInput.create("wordlewort", "Nächster Versuch:", TextInputStyle.SHORT).setPlaceholder("Wort hier eingeben")
				.setRequired(true).setMinLength(5).setMaxLength(5).build();
		Modal wordlemodal = Modal.create("wordle", "Wordle").addActionRows(ActionRow.of(wordlewort)).build();
		event.replyModal(wordlemodal).queue();
	}

	public static void wordlemodal(ModalInteractionEvent event, InteractionHook hook) {
		try {
			Wordle wordle = new Wordle(event.getUser().getIdLong());
			if (wordle.datum.isBefore(DateTime.now().withTimeAtStartOfDay())) {
				event.getInteraction().editMessage("Dieses Wordle ist leider schon abgelaufen! Starte doch mit /wordle ein neues")
						.setActionRow(Collections.emptyList()).queue();
			}
			if (wordle.finished) {
				hook.sendMessage("Du hast doch bereits gewonnen").queue(msg -> {
					msg.delete().queueAfter(1, TimeUnit.MINUTES);
				});
			}

			if (wordle.datum.isAfter(DateTime.now().withTimeAtStartOfDay().plusDays(1))) {
				hook.sendMessage("Leider hast du zu lange für das ausfüllen des Formulars benötigt und das Wordle ist abgelaufen!").queue(msg -> {
					msg.delete().queueAfter(1, TimeUnit.MINUTES);
				});
			}
			if (wordle.Wort6 != null) {
				hook.editOriginalComponents(Collections.emptyList()).queue();;
				hook.sendMessage("Du hast bereits alle Versuche aufgebraucht!").queue(msg -> {
					msg.delete().queueAfter(1, TimeUnit.MINUTES);
				});
				return;
			}

			String versuch = event.getValue("wordlewort").getAsString().toUpperCase();
			if (!Wordle.words().contains(versuch)) {
				hook.sendMessage("Dieses Wort ist nicht in der Liste der verfügbaren Wörter!").queue(msg -> {
					msg.delete().queueAfter(1, TimeUnit.MINUTES);
				});
				return;
			}

			if (wordle.Wort1 == null) {
				wordle.Wort1 = versuch;
			} else if (wordle.Wort2 == null) {
				wordle.Wort2 = versuch;
			} else if (wordle.Wort3 == null) {
				wordle.Wort3 = versuch;
			} else if (wordle.Wort4 == null) {
				wordle.Wort4 = versuch;
			} else if (wordle.Wort5 == null) {
				wordle.Wort5 = versuch;
			} else if (wordle.Wort6 == null) {
				wordle.Wort6 = versuch;
			}

			if (versuch.equals(Settings.currentword)) {
				wordle.finished = true;
				wordle.streak = wordle.streak + 1;
			}
			wordle.save();
			updateMessage(event, hook, wordle);

		} catch (Exception e) {
			logger.error("Fehler beim Modal-Auswerten!", e);
			hook.sendMessage("Fehler beim Modal-Auswerten!").queue();
		}

	}

	private static void updateMessage(ModalInteractionEvent event, InteractionHook hook, Wordle wordle) {
		Color ebcolor;
		String header = "";
		int trys = 0;
		if (wordle.Wort1 != null) {
			trys++;
		}
		if (wordle.Wort2 != null) {
			trys++;
		}
		if (wordle.Wort3 != null) {
			trys++;
		}
		if (wordle.Wort4 != null) {
			trys++;
		}
		if (wordle.Wort5 != null) {
			trys++;
		}
		if (wordle.Wort6 != null) {
			trys++;
		}
		Field field = null;
		if (wordle.finished) {
			ebcolor = Color.green;
			header = "Glückwunsch!";
			field = new Field("Schneckchencordle " + DateTime.now().toString("dd.MM.yy") + " in " + trys + "/6 Versuchen erraten",
					wordle.createPattern(true), false);
		} else if (wordle.Wort6 == null) {
			ebcolor = Color.yellow;
		} else {
			ebcolor = Color.red;
			header = "Leider wurde es dieses mal nichts. Das Wort wäre " + Settings.currentword + " gewesen!";
			field = new Field("Schneckchencordle " + DateTime.now().toString("dd.MM.yy") + " nicht erraten! " + trys + "/6 Versuche",
					wordle.createPattern(true), false);
		}
		EmbedBuilder builder = Discord.standardEmbed(ebcolor, "Wordle " + DateTime.now().toString("dd.MM.yy"), event.getUser().getId(),
				event.getUser().getEffectiveAvatarUrl());
		builder.setDescription("Derzeitige Streak: " + wordle.streak);
		if (wordle.streak > 4) {
			builder.appendDescription("  🔥🔥🔥🔥🔥");
		}

		builder.addField(header, wordle.createPattern(false), false);

		if (field != null) {
			builder.addField(field);
		}

		WebhookMessageUpdateAction<Message> action = hook.editOriginalEmbeds(builder.build());
		if (ebcolor == Color.green || ebcolor == Color.red) {
			action.setActionRows(Collections.emptyList());
		}

		action.queue();
	}

	public static void anleitung(SlashCommandInteractionEvent event, InteractionHook hook) {
		Member member = event.getMember();
		EmbedBuilder builder = Discord.standardEmbed(Color.GREEN, "Anleitung Schneckchencordle", member.getId(), member.getEffectiveAvatarUrl());
		builder.setDescription(
				"__Jeden Tag kann man 1x spielen! Um Mitternacht wird ein neues Wort erzeugt. Jeder Spieler hat am Tag das gleiche Wort! Also bitte teile keine Hinweise an andere User!__");
		builder.addField("Bedienung:",
				"In Privatnachrichten /wordle schicken startet eine neue Session Schneckchencordle \n Über den Button 'Wort prüfen!' können im Anschluss die Worte eingegeben werden.",
				false);
		builder.addField("Ziel: Ein Wort mit 5 Buchstaben erraten.",
				"Jeden Tag bekommt man 6 Versuche. Nach jedem Versuch wird angezeigt, ob die einzelnen Buchstaben korrekt sind oder an einer anderen Position im Wort vorhanden sind.",
				false);
		builder.addField("Erklärung der Zeichen:", "🇦 = Buchstabe ist an dieser Position korrekt. \n" + "🄰 = Buchstabe ist an einer anderen Stelle. \n"
				+ "A = Buchstabe ist nicht im Wort vorhanden.", false);
		hook.editOriginalEmbeds(builder.build()).queue();

	}
}
