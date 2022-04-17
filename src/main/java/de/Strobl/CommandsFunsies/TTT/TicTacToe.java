package de.Strobl.CommandsFunsies.TTT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.Strobl.CommandsFunsies.Wordle.Wordle;
import de.Strobl.Instances.SQL;
import de.Strobl.Main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class TicTacToe {
	// list.add(ActionRow.of(SelectMenu.create("KIStärke").addOption("Leicht",
	// "leicht").addOption("Mittel", "mittel").addOption("Schwer",
	// "schwer").setMaxValues(1).setMinValues(1).setPlaceholder("KI-Stärke
	// Auswählen").build()));
	public static String classname = SQL.classname;
	public static String connectionname = SQL.connectionname;
	private static final Logger logger = LogManager.getLogger(Wordle.class);
	// Field Data
	// null = Frei
	// true = Player 1
	// false = Player 2
	@Nullable
	public Boolean field11;
	@Nullable
	public Boolean field12;
	@Nullable
	public Boolean field13;
	@Nullable
	public Boolean field21;
	@Nullable
	public Boolean field22;
	@Nullable
	public Boolean field23;
	@Nullable
	public Boolean field31;
	@Nullable
	public Boolean field32;
	@Nullable
	public Boolean field33;

	// Play Data
	@Nonnull
	public String player1;
	@Nonnull
	public String messageid1;
	@Nullable
	public String player2;
	@Nullable
	public String messageid2;
	@Nonnull
	public Boolean turn; // false = Player2 am Zug true = Player1
	@Nonnull
	public Boolean ki; // Player 2 as KI?
	@Nonnull
	public int kidif = 0; // Player 2 as KI?
	@Nonnull
	public Boolean finished = false;
	@Nonnull
	public Boolean winner = null; // false = Player2 Winner true = Player1

	// Create new
	public TicTacToe(@Nonnull String userid1, @Nonnull String userid2, @Nullable int kidif, @Nullable String message1, @Nullable String message2)
			throws Exception {
		this.player1 = userid1;
		this.messageid1 = message1;
		Random random = new Random();
		this.turn = random.nextBoolean();
		this.kidif = kidif;
		if (userid2 == null) {
			this.ki = true;
			if (!turn) {
				KIMove();
			}
		} else {
			this.player2 = userid2;
			this.messageid2 = message2;
			this.ki = false;
		}
		save();
	}

	// Load from Database
	public TicTacToe(@Nonnull String userid) throws SQLException {
		Connection conn = DriverManager.getConnection(connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from tictactoe where player1 = '" + userid + "' OR player2 ='" + userid + "';");
		if (!rs.next()) {
			return;
		}
		// Playerdata
		this.player1 = rs.getString("player1");
		this.player2 = rs.getString("player2");
		if (player2.equals("null")) this.player2 = null;
		this.messageid1 = rs.getString("messageid1");
		this.messageid2 = rs.getString("messageid2");
		if (this.messageid2.equals("null")) this.messageid2 = null;
		this.kidif = rs.getInt("kidif");
		this.ki = rs.getString("ki").equals("true");
		this.turn = rs.getString("turn").equals("true");
		// Fielddata

		if (!rs.getString("field11").equals("null")) {
			this.field11 = rs.getString("field11").equals("true");
		}
		if (!rs.getString("field12").equals("null")) {
			this.field12 = rs.getString("field12").equals("true");
		}
		if (!rs.getString("field13").equals("null")) {
			this.field13 = rs.getString("field13").equals("true");
		}
		if (!rs.getString("field21").equals("null")) {
			this.field21 = rs.getString("field21").equals("true");
		}
		if (!rs.getString("field22").equals("null")) {
			this.field22 = rs.getString("field22").equals("true");
		}
		if (!rs.getString("field23").equals("null")) {
			this.field23 = rs.getString("field23").equals("true");
		}
		if (!rs.getString("field31").equals("null")) {
			this.field31 = rs.getString("field31").equals("true");
		}
		if (!rs.getString("field32").equals("null")) {
			this.field32 = rs.getString("field32").equals("true");
		}
		if (!rs.getString("field33").equals("null")) {
			this.field33 = rs.getString("field33").equals("true");
		}

		rs.close();
		stat.close();
		conn.close();
	}

	// Returns true, if move was valid
	// Y = Höhe
	// X = Breite
	// 11 = Linksoben
	public Boolean playerMove(String userID, int y, int x) throws Exception {
		Boolean p1 = null;
		if (userID.equals(player1)) {
			p1 = true;
		} else if (userID.equals(player2)) {
			p1 = false;
		}
		if ((p1 && !turn) || (!p1 && turn)) return false;

		if (x == 1) {
			if (y == 1) {
				if (field11 != null) return false;
				field11 = p1;
			} else if (y == 2) {
				if (field21 != null) return false;
				field21 = p1;
			} else if (y == 3) {
				if (field31 != null) return false;
				field31 = p1;
			}
		} else if (x == 2) {
			if (y == 1) {
				if (field12 != null) return false;
				field12 = p1;
			} else if (y == 2) {
				if (field22 != null) return false;
				field22 = p1;
			} else if (y == 3) {
				if (field32 != null) return false;
				field32 = p1;
			}
		} else if (x == 3) {
			if (y == 1) {
				if (field13 != null) return false;
				field13 = p1;
			} else if (y == 2) {
				if (field23 != null) return false;
				field23 = p1;
			} else if (y == 3) {
				if (field33 != null) return false;
				field33 = p1;
			}
		}
		this.turn = !this.turn;
		Boolean win = checkWin();
		if (win != null) {
			updateMessages();
			return true;
		}
		if (this.finished) {
			updateMessages();
			return true;
		}

		if (ki) {
			KIMove();
			return true;
		}
		updateMessages();
		return true;
	}

	private void KIMove() throws Exception {
		if (!KIMoveWins(false)) {
			if (!KIMoveWins(true)) {
				if (this.kidif == 5) { // TODO 3
					KIMoveSchwer();
				} else if (this.kidif == 6) { // TODO 2
					KIMoveMittel();
				} else {
					KIMoveLeicht();
				}
			}
		}

		this.turn = !this.turn;
		save();
		checkWin();
		updateMessages();
		return;

	}

	// First Rolebased Moves
	// true = Move taken
	// side = side to check for. false = KI, true = human
	private Boolean KIMoveWins(Boolean side) throws Exception {
		int temp = KIMoveWinsInstance(field11, field12, field13, side);
		if (temp == 1) {
			field11 = false;
			return true;
		} else if (temp == 2) {
			field12 = false;
			return true;
		} else if (temp == 3) {
			field13 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field21, field22, field23, side);
		if (temp == 1) {
			field21 = false;
			return true;
		} else if (temp == 2) {
			field22 = false;
			return true;
		} else if (temp == 3) {
			field23 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field31, field32, field33, side);
		if (temp == 1) {
			field31 = false;
			return true;
		} else if (temp == 2) {
			field32 = false;
			return true;
		} else if (temp == 3) {
			field33 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field11, field21, field31, side);
		if (temp == 1) {
			field11 = false;
			return true;
		} else if (temp == 2) {
			field21 = false;
			return true;
		} else if (temp == 3) {
			field31 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field12, field22, field32, side);
		if (temp == 1) {
			field12 = false;
			return true;
		} else if (temp == 2) {
			field22 = false;
			return true;
		} else if (temp == 3) {
			field32 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field13, field23, field33, side);
		if (temp == 1) {
			field13 = false;
			return true;
		} else if (temp == 2) {
			field23 = false;
			return true;
		} else if (temp == 3) {
			field33 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field11, field22, field33, side);
		if (temp == 1) {
			field11 = false;
			return true;
		} else if (temp == 2) {
			field22 = false;
			return true;
		} else if (temp == 3) {
			field33 = false;
			return true;
		}
		temp = KIMoveWinsInstance(field13, field22, field31, side);
		if (temp == 1) {
			field13 = false;
			return true;
		} else if (temp == 2) {
			field22 = false;
			return true;
		} else if (temp == 3) {
			field31 = false;
			return true;
		}

		return false;
	}

	// 0 = Nicht möglich
	// 1 = field1 richtig
	// 2 = field2 richtig
	// 3 = field3 richtig
	private int KIMoveWinsInstance(Boolean field1, Boolean field2, Boolean field3, Boolean side) {
		// Column 1
		if (field1 == null) {
			if (!(field2 == null) && !(field3 == null)) {
				if (field2 == side && field3 == side) {
					return 1;

				}
			}
		}
		if (field2 == null) {
			if (!(field1 == null) && !(field3 == null)) {
				if (field1 == side && field3 == side) {
					return 2;
				}
			}
		}
		if (field3 == null) {
			if (!(field1 == null) && !(field2 == null)) {
				if (field1 == side && field2 == side) {
					return 3;
				}
			}
		}
		return 0;
	}

	// KI Move Schwer
	// true = Move taken
	private Boolean KIMoveSchwer() throws Exception {
		return false;
	}

	// KI Move Mittel
	// true = Move taken
	private Boolean KIMoveMittel() throws Exception {
		return false;
	}

	// KI Move Leicht
	// true = Move taken
	private Boolean KIMoveLeicht() throws Exception { // Random Field Pick
		int fieldssize = 0;
		int[] temp2 = new int[9];
		if (this.field11 == null) {
			temp2[fieldssize] = 11;
			fieldssize++;
		}
		if (this.field12 == null) {
			temp2[fieldssize] = 12;
			fieldssize++;
		}
		if (this.field13 == null) {
			temp2[fieldssize] = 13;
			fieldssize++;
		}
		if (this.field21 == null) {
			temp2[fieldssize] = 21;
			fieldssize++;
		}
		if (this.field22 == null) {
			temp2[fieldssize] = 22;
			fieldssize++;
		}
		if (this.field23 == null) {
			temp2[fieldssize] = 23;
			fieldssize++;
		}
		if (this.field31 == null) {
			temp2[fieldssize] = 31;
			fieldssize++;
		}
		if (this.field32 == null) {
			temp2[fieldssize] = 32;
			fieldssize++;
		}
		if (this.field33 == null) {
			temp2[fieldssize] = 33;
			fieldssize++;
		}
		Random rand = new Random();
		int randint = rand.nextInt(fieldssize);
		int randomfield = temp2[randint];
		if (randomfield == 11) {
			this.field11 = false;
		} else if (randomfield == 12) {
			this.field12 = false;
		} else if (randomfield == 13) {
			this.field13 = false;
		} else if (randomfield == 21) {
			this.field21 = false;
		} else if (randomfield == 22) {
			this.field22 = false;
		} else if (randomfield == 23) {
			this.field23 = false;
		} else if (randomfield == 31) {
			this.field31 = false;
		} else if (randomfield == 32) {
			this.field32 = false;
		} else if (randomfield == 33) {
			this.field33 = false;
		}
		return true;
	}

	// Saves to Database
	public TicTacToe save() throws Exception {
		Class.forName(classname);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from tictactoe where player1 = '" + player1 + "';");
		stat.executeUpdate("INSERT INTO tictactoe (player1, messageid1, player2, messageid2, kidif, ki, turn, field11, "
				+ "field12, field13, field21, field22, field23, field31, field32, field33)" + " VALUES ('" + player1 + "', '" + messageid1 + "','"
				+ player2 + "','" + messageid2 + "','" + kidif + "','" + ki + "','" + turn + "','" + field11 + "','" + field12 + "','" + field13 + "','"
				+ field21 + "','" + field22 + "','" + field23 + "','" + field31 + "','" + field32 + "','" + field33 + "')");
		stat.close();
		conn.close();
		return this;
	}

	// Löscht den Datensatz aus der Datenbank
	public void delete() throws Exception {
		Class.forName(classname);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from tictactoe where player1 = '" + player1 + "';");
		stat.close();
		conn.close();
	}

	// null = kein Gewinner
	// True = Spieler 1 gewinnt
	// False = Spieler 2 gewinnt
	public Boolean checkWin() throws Exception {
		List<TicTacToeWins> list = listWins();
		for (int i = 0; i < list.size(); i++) {
			TicTacToeWins row = list.get(i);
			if (row.field1 == null || row.field2 == null || row.field3 == null) {
			} else if (row.field1 == true && row.field2 == true && row.field3 == true) {
				this.winner = true;
				this.finished = true;
				delete();
				return true;
			} else if (row.field1 == false && row.field2 == false && row.field3 == false) {
				this.winner = false;
				this.finished = true;
				delete();
				return false;
			}
		}

		if (field11 != null && field12 != null && field13 != null && field21 != null && field22 != null && field23 != null && field31 != null
				&& field32 != null && field33 != null) {
			this.finished = true;
			delete();
		}
		return null;
	}

	private List<TicTacToeWins> listWins() {
		List<TicTacToeWins> list = new ArrayList<>();
		list.add(new TicTacToeWins(field11, field12, field13));
		list.add(new TicTacToeWins(field21, field22, field23));
		list.add(new TicTacToeWins(field31, field32, field33));
		list.add(new TicTacToeWins(field11, field21, field31));
		list.add(new TicTacToeWins(field12, field22, field32));
		list.add(new TicTacToeWins(field13, field23, field33));
		list.add(new TicTacToeWins(field11, field22, field33));
		list.add(new TicTacToeWins(field31, field22, field13));
		Collections.shuffle(list);
		return list;
	}

	public void updateMessages() {
		JDA jda = Main.jda;
		jda.openPrivateChannelById(player1).queue(channel -> {
			updateMessage(channel, true);
			channel.retrieveMessageById(messageid1).queue(msg -> {
				msg.delete().queueAfter(2, TimeUnit.SECONDS);
			});
		});
		if (ki) return;
		jda.openPrivateChannelById(player2).queue(channel -> {
			updateMessage(channel, true);
			channel.retrieveMessageById(messageid2).queue(msg -> {
				msg.delete().queueAfter(2, TimeUnit.SECONDS);
			});
		});

	}

	private void updateMessage(PrivateChannel channel, Boolean p1) {
		String message = "";
		if (!ki) {
			message = "Gegner: ";
			if (p1) {
				message = message + "<@" + player2 + ">";
			} else {
				message = message + "<@" + player1 + ">";
			}
		} else {
			message = "Spiel gegen KI ";
			if (kidif > 2) {
				message = message + "(Schwer)";
			} else if (kidif > 1) {
				message = message + "(Mittel)";
			} else {
				message = message + "(Leicht)";
			}
		}

		if (this.finished) {
			if (winner == null) {
				message = "Unentschieden? Unentschieden! \nVersuch es doch nochmal :)";
			} else if (p1 == winner) {
				message = "Herzlichen Glückwunsch! Du hast gewonnen!";
			} else {
				message = "Du wurdest leider besiegt! Probier es doch noch einmal aus!";
			}
		}

		channel.sendMessage(message).setActionRows(createButtons(p1)).queue(msg -> {
			try {
				if (p1) {
					this.messageid1 = msg.getId();
					if (!finished) save();
				} else {
					this.messageid2 = msg.getId();
					if (!finished) save();
				}
			} catch (Exception e) {
				logger.error("Fehler Update Messages", e);
			}
		});
	}

	public List<ActionRow> createButtons(@Nonnull Boolean p1) {
		List<ActionRow> list = new ArrayList<>();
		try {
			List<Button> temp = new ArrayList<>();
			if (finished) {
				temp.add(Button.primary("TicTacToe_surr", "Aufgeben").asDisabled());
			} else {
				temp.add(Button.primary("TicTacToe_surr", "Aufgeben"));
			}
			temp.add(Button.primary("TicTacToe_hilfe", "Regeln"));
			list.add(ActionRow.of(temp));
			temp.clear();
			if (field11 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_11", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_11", "⁤"));
				}
			} else {
				if (field11) {
					if (p1) {
						temp.add(Button.success("TicTacToe_11", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_11", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_11", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_11", "O").asDisabled());
					}
				}
			}
			if (field12 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_12", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_12", "⁤"));
				}
			} else {
				if (field12) {
					if (p1) {
						temp.add(Button.success("TicTacToe_12", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_12", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_12", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_12", "O").asDisabled());
					}
				}
			}
			if (field13 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_13", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_13", "⁤"));
				}
			} else {
				if (field13) {
					if (p1) {
						temp.add(Button.success("TicTacToe_13", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_13", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_13", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_13", "O").asDisabled());
					}
				}
			}
			list.add(ActionRow.of(temp));
			temp.clear();
			if (field21 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_21", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_21", "⁤"));
				}
			} else {
				if (field21) {
					if (p1) {
						temp.add(Button.success("TicTacToe_21", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_21", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_21", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_21", "O").asDisabled());
					}
				}
			}
			if (field22 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_22", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_22", "⁤"));
				}
			} else {
				if (field22) {
					if (p1) {
						temp.add(Button.success("TicTacToe_22", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_22", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_22", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_22", "O").asDisabled());
					}
				}
			}
			if (field23 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_23", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_23", "⁤"));
				}
			} else {
				if (field23) {
					if (p1) {
						temp.add(Button.success("TicTacToe_23", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_23", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_23", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_23", "O").asDisabled());
					}
				}
			}
			list.add(ActionRow.of(temp));
			temp.clear();
			if (field31 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_31", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_31", "⁤"));
				}
			} else {
				if (field31) {
					if (p1) {
						temp.add(Button.success("TicTacToe_31", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_31", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_31", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_31", "O").asDisabled());
					}
				}
			}
			if (field32 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_32", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_32", "⁤"));
				}
			} else {
				if (field32) {
					if (p1) {
						temp.add(Button.success("TicTacToe_32", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_32", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_32", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_32", "O").asDisabled());
					}
				}
			}
			if (field33 == null) {
				if (this.finished) {
					temp.add(Button.secondary("TicTacToe_33", "⁤").asDisabled());
				} else {
					temp.add(Button.secondary("TicTacToe_33", "⁤"));
				}
			} else {
				if (field33) {
					if (p1) {
						temp.add(Button.success("TicTacToe_33", "O").asDisabled());
					} else {
						temp.add(Button.danger("TicTacToe_33", "X").asDisabled());
					}
				} else {
					if (p1) {
						temp.add(Button.danger("TicTacToe_33", "X").asDisabled());
					} else {
						temp.add(Button.success("TicTacToe_33", "O").asDisabled());
					}
				}
			}
			list.add(ActionRow.of(temp));
		} catch (Exception e) {
			logger.error("Fehler beim erstellen des Button-Layouts", e);
		}

		return list;
	}

}
