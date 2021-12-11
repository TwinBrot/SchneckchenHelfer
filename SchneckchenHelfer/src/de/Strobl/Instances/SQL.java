package de.Strobl.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import de.Strobl.Exceptions.SQLDataNotFound;
import net.dv8tion.jda.api.entities.User;

public class SQL {
	private static String classname = "org.sqlite.JDBC";
	private static String connectionname = "jdbc:sqlite:data.db";
	private static final Logger logger = LogManager.getLogger(SQL.class);

	public static void initialize() throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE if not exists strafen (ID, userid, typ, text);");
			stat.executeUpdate("CREATE INDEX if not exists Strafen_ID_UserID ON strafen (id, userid); ");
			stat.executeUpdate("CREATE TABLE if not exists emotes (emoteid, count);");
			stat.executeUpdate("CREATE INDEX if not exists emotes_id ON emotes (emoteid); ");
			stat.executeUpdate("CREATE TABLE if not exists temp (userid, typ, time);");

// Check if Strafen-Counter Exists
			ResultSet size = stat.executeQuery("select * from strafen where userid = '" + 0 + "';");
			int length = 0;
			while (size.next()) {
				length = size.getRow();
			}
			size.close();

// If no Counter exists, Create One
			if (length == 0) {
				logger.warn("Strafenzähler nicht gefunden. Lege neuen Zähler an.");
				stat.executeUpdate("INSERT INTO strafen (id, userid, typ, text) VALUES ('1','0','COUNTER','COUNTER');");
			} else if (length == 1) {
			} else {
				logger.error("Error in Database! Multiple Counter in Tabel Strafen!");
			}
		} catch (Exception e) {
			logger.fatal("Fehler beim Initialisieren der Datenbank:", e);
			throw new SQLException();
		}
	}

// Strafen

	public static void strafenadd(String id, String userid, String typ, String text) throws SQLException {
		try {
			logger.info("Füge Datensatz der Strafen-Datenbank hinzu:");
			logger.info("ID = " + id + "     User-ID = " + userid + "     Strafen-Typ = " + typ);
			logger.info("Strafen-Text = " + text);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("INSERT INTO strafen (id, userid, typ, text) VALUES ('" + id + "','" + userid + "','" + typ + "','" + text + "');");
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim hinzufügen des Datensatzes:", e);
			throw new SQLException();
		}
	}

	public static void strafenadd(Integer id, String userid, String typ, String text) throws SQLException {
		strafenadd(id.toString(), userid, typ, text);
	}

	public static void strafenadd(Integer id, Integer userid, String typ, String text) throws SQLException {
		strafenadd(id.toString(), userid.toString(), typ, text);
	}

	public static void strafenadd(String id, Integer userid, String typ, String text) throws SQLException {
		strafenadd(id, userid.toString(), typ, text);
	}

	public static String strafengetid(String id) throws SQLException, SQLDataNotFound {
		try {
			logger.info("Lese Daten aus Datenbank:");
			logger.info("ID = " + id);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from strafen where id = '" + id + "';");
			String result = "";
			while (rs.next()) {
				result = rs.getString("id") + "," + rs.getString("typ") + "," + rs.getString("userid") + "," + rs.getString("text");
			}
			if (result.equals("")) {
				throw new SQLDataNotFound("", "");
			}
			rs.close();
			conn.close();
			return result;
		} catch (SQLDataNotFound e) {
			throw new SQLDataNotFound("Datensatz nicht gefunden!", "", e);
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException(e);
		}
	}

	public static String[] strafengetuserid(String userid) throws SQLException, SQLDataNotFound {
		// String[] temp = result.split(",", 4);
		// temp[0] = ID
		// temp[1] = Typ
		// temp[2] = UserID
		// temp[3] = Text
		try {
			logger.info("Lese Daten aus Datenbank:");
			logger.info("User-ID = " + userid);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet size = stat.executeQuery("select * from strafen where userid = '" + userid + "';");
			int length = 0;
			while (size.next()) {
				length = size.getRow();
			}
			size.close();

			ResultSet rs = stat.executeQuery("select * from strafen where userid = '" + userid + "';");
			if (rs == null) {
				throw new SQLException();
			}
			String result[] = new String[length];
			while (rs.next()) {
				result[rs.getRow() - 1] = rs.getString("id") + "," + rs.getString("typ") + "," + rs.getString("userid") + "," + rs.getString("text");
			}

			if (result.length == 0) {
				throw new SQLDataNotFound("", "");
			}

			rs.close();
			conn.close();
			return result;
		} catch (SQLDataNotFound e) {
			throw new SQLDataNotFound("Datensatz nicht gefunden!", "", e);
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException();
		}
	}

	public static Integer strafengetusersize(String userid, String typ) throws SQLException {
		try {
			logger.info("Lese Daten aus Datenbank:");
			logger.info("User-ID = " + userid);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			String Query = "select * from strafen where userid = '" + userid + "'";
			if (typ != null) {
				Query = Query + " AND typ = '" + typ + "'";
			}
			ResultSet size = stat.executeQuery(Query + ";");
			int length = 0;
			while (size.next()) {
				length = size.getRow();
			}
			size.close();
			return length;
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException();
		}
	}
	public static Integer strafengetusersize(User user, String typ) throws SQLException {
		return strafengetusersize(user.getId(), typ);
	}

	public static Integer strafengetcounter() throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from strafen where userid = '0';");
			String result = "";
			while (rs.next()) {
				result = rs.getString("id");
			}
			if (result.equals("")) {
				throw new SQLException();
			}
			rs.close();
			conn.close();
			return Integer.parseInt(result);
		} catch (Exception e) {
			logger.error("Fehler beim Auslesen des Counters", e);
			throw new SQLException();
		}
	}

	public static String strafengettemp() throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from temp;");
			while (rs.next()) {
				DateTime unbantime = DateTime.parse(rs.getString("time"));
				if (!unbantime.isAfterNow()) {
					String result = rs.getString("userid") + "," + rs.getString("typ");
					stat.executeUpdate("delete from temp where userid = '" + rs.getString("userid") + "';");
					rs.close();
					conn.close();
					return result;
				}
			}
			rs.close();
			conn.close();
		} catch (Exception e) {
			logger.error("SQL Fehler Temp Check:", e);
			throw new SQLException();
		}
		return "";

	}

	public static void strafenremove(String id) throws SQLException {
		try {
			logger.info("Entferne Datensatz aus der Datenbank:");
			logger.info("ID = " + id);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("delete from strafen where id = '" + id + "';");
			conn.close();
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim hinzufügen des Datensatzes:", e);
			throw new SQLException();
		}
	}

	public static void strafencounterup() throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			Integer ID = strafengetcounter();
			ID++;
			stat.executeUpdate("DELETE FROM strafen WHERE userid = '0';");
			stat.executeUpdate("INSERT INTO strafen (id, userid, typ, text) VALUES ('" + ID + "','0','COUNTER','COUNTER');");
			conn.close();
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim hinzufügen des Datensatzes:", e);
			throw new SQLException();
		}
	}

// Emotes

	public static void emoteup(String emoteid) throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from emotes where emoteid = '" + emoteid + "';");
			String result = "";
			while (rs.next()) {
				result = rs.getString("count");
				if (rs.getRow() > 1) {
					throw new SQLException("Mehrere Datensätze mit dieser ID gefunden!", "");
				}
			}
			if (result.equals("")) {
				stat.executeUpdate("INSERT INTO emotes (emoteid, count) VALUES ('" + emoteid + "','1');");
				return;
			}
			Integer count = Integer.parseInt(result);
			count++;
			stat.executeUpdate("DELETE FROM emotes WHERE emoteid = '" + emoteid + "';");
			stat.executeUpdate("INSERT INTO emotes (emoteid, count) VALUES ('" + emoteid + "','" + count + "');");
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim Emotecounter erhöhen:", e);
			throw new SQLException();
		}
	}

	public static Map<String, Integer> emotesget() throws SQLException {
		try {
			logger.info("Lese alle Emotes aus Datenbank");
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet size = stat.executeQuery("select * from emotes ;");
			int length = 0;
			while (size.next()) {
				length = size.getRow();
			}
			size.close();

			if (length == 0) {
				return null;
			}

			ResultSet rs = stat.executeQuery("select * from emotes;");
			if (rs == null) {
				throw new SQLException();
			}
			Map<String, Integer> Emotelist = new HashMap<String, Integer>();
			while (rs.next()) {
				Integer count = Integer.parseInt(rs.getString("count"));
				Emotelist.put(rs.getString("emoteid"), count);
			}

			if (Emotelist.size() == 0) {
				throw new SQLDataNotFound("", "");
			}

			rs.close();
			conn.close();
			return Emotelist;
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException();
		}
	}

}