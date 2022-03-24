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

public class SQL {
	public static String classname = "org.sqlite.JDBC";
	public static String connectionname = "jdbc:sqlite:data.db";
	private static final Logger logger = LogManager.getLogger(SQL.class);

	public static void initialize() throws SQLException {
		try {
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE if not exists strafen (id INTEGER UNIQUE, userid, typ, text, mod, PRIMARY KEY(id AUTOINCREMENT));");
			stat.executeUpdate("CREATE INDEX if not exists Strafen_ID_UserID ON strafen (id, userid); ");
			stat.executeUpdate("CREATE TABLE if not exists emotes (emoteid UNIQUE, count);");
			stat.executeUpdate("CREATE INDEX if not exists emotes_id ON emotes (emoteid); ");
			stat.executeUpdate("CREATE TABLE if not exists temp (id	INTEGER UNIQUE, userid , typ , time, PRIMARY KEY(id));");
			stat.executeUpdate("CREATE TABLE if not exists wordle (UserID TEXT, finished TEXT, Streak INTEGER, Datum TEXT, Wort1 TEXT, Wort2 TEXT, Wort3 TEXT, Wort4 TEXT, Wort5 TEXT, Wort6 TEXT);");
			stat.close();
			conn.close();
		} catch (Exception e) {
			logger.fatal("Fehler beim Initialisieren der Datenbank:", e);
			throw new SQLException();
		}
	}

	public static void emoteup(String emoteid) throws SQLException {
		try {
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from emotes where emoteid = '" + emoteid + "';");
			String result = null;
			while (rs.next()) {
				result = rs.getString("count");
			}
			if (result == null) {
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
				throw new SQLException("", "");
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