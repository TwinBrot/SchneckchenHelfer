package de.Strobl.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.Logger;

import de.Strobl.Main.Main;

public class SQL {
	public static String classname = "org.sqlite.JDBC";
	public static String connectionname = "jdbc:sqlite:userdata.db";
	public static void initialize() throws SQLException {
		Logger logger = Main.logger;
		try {
			logger.info("SQL Datenbank wird erstellt, wenn nicht existent.");
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("CREATE TABLE if not exists strafen (ID, userid NOT NULL, typ NOT NULL, text);");
			stat.executeUpdate("CREATE INDEX if not exists Strafen_ID_UserID ON strafen (id, userid); ");
		} catch (Exception e) {
			logger.fatal("Fehler beim Initialisieren der Datenbank:", e);
			throw new SQLException();
		}
	}

	public static void strafenadd(String id, String userid, String typ, String text) throws SQLException {
		Logger logger = Main.logger;
		try {
			logger.info("Füge Datensatz der Datenbank hinzu:");
			logger.info("ID = " + id);
			logger.info("User-ID = " + userid);
			logger.info("Strafen-Typ = " + typ);
			logger.info("Strafen-Text = " + text);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("INSERT INTO strafen (id, userid, typ, text) VALUES ('"+ id +"','"+ userid +"','"+ typ +"','"+ text +"');");
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim hinzufügen des Datensatzes:", e);
			throw new SQLException();
		}
	}

	public static void strafenremove(String id) throws SQLException {
		Logger logger = Main.logger;
		try {
			logger.info("Entferne Datensatz aus der Datenbank:");
			logger.info("ID = " + id);
			Class.forName(classname);
			Connection conn = DriverManager.getConnection(connectionname);
			Statement stat = conn.createStatement();
			stat.executeUpdate("delete from strafen where id = '1';");
			conn.close();
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim hinzufügen des Datensatzes:", e);
			throw new SQLException();
		}
	}
	

//String[] temp = result.split(",", 4);
//temp[0] = ID
//temp[1] = Typ
//temp[2] = UserID
//temp[3] = Text

	public static String strafengetid(String id) throws SQLException {
		Logger logger = Main.logger;
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
				throw new SQLException();
			}
			rs.close();
			conn.close();
			return result;
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException();
		}
	}


	public static String[] strafengetuserid(String userid) throws SQLException {
		Logger logger = Main.logger;
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
			System.out.println("Länge:" + length);
			size.close();
			
	        ResultSet rs = stat.executeQuery("select * from strafen where userid = '" + userid + "';");
	        if (rs == null) {
				throw new SQLException();
	        }
	        String result[] = new String[length];
			while (rs.next()) {
				System.out.println("Row:" + rs.getRow());
		        result[rs.getRow()-1] = rs.getString("id") + "," + rs.getString("typ") + "," + rs.getString("userid") + "," + rs.getString("text");
			}
			
			
			if (result.length == 0) {
				throw new SQLException();
			}
			
			rs.close();
			conn.close();
			return result;
		} catch (Exception e) {
			logger.error("Unbekannter Fehler beim auslesen eines Datensatzes:", e);
			throw new SQLException();
		}
	}

}