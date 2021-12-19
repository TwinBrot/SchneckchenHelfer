package de.Strobl.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Strafe {
	private static final Logger logger = LogManager.getLogger(Strafe.class);

	@Nullable
	private String id;
	@Nonnull
	private String userid;
	@Nonnull
	private StrafenTyp typ;
	@Nonnull
	private String text;
	@Nonnull
	private String modid;

	public Strafe(@Nonnull String newuserid, @Nonnull StrafenTyp newtyp, @Nonnull String newtext, @Nonnull String modid) {
		this.userid = newuserid;
		this.typ = newtyp;
		this.text = newtext;
		this.modid = modid;
	}

	private Strafe(@Nonnull String newid, @Nonnull String newuserid, @Nonnull StrafenTyp newtyp, @Nonnull String newtext, @Nonnull String modid) {
		this.id = newid;
		this.userid = newuserid;
		this.typ = newtyp;
		this.text = newtext;
		this.modid = modid;
	}

	@Nullable
	public static Strafe get(@Nonnull String id) throws SQLException {
		logger.info("Lese Daten aus Datenbank:");
		logger.info("ID = " + id);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from strafen where id = '" + id + "';");
		Strafe temp = null;
		while (rs.next()) {
			if (!(temp == null)) {
				rs.close();
				conn.close();
				throw new SQLException();
			}
			StrafenTyp typ = getEnum(rs.getString("typ"));
			temp = new Strafe(rs.getString("id"), rs.getString("userid"), typ, rs.getString("text"), rs.getString("mod"));

			rs.close();
			conn.close();
		}
		return temp;
	}

	@Nullable
	public static List<Strafe> getAll(@Nonnull String userid) throws SQLException {
		logger.info("Lese Daten aus Datenbank:");
		logger.info("User-ID = " + userid);
		List<Strafe> strafen = new ArrayList<Strafe>();
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from strafen where userid = '" + userid + "';");
		while (rs.next()) {
			StrafenTyp typ = getEnum(rs.getString("typ"));
			strafen.add(new Strafe(rs.getString("id"), rs.getString("userid"), typ, rs.getString("text"), rs.getString("mod")));
		}
		rs.close();
		conn.close();
		return strafen;
	}

	public static Integer getSQLSize(@Nonnull String userid, StrafenTyp typ) throws SQLException {
		logger.info("Lese Daten aus Datenbank:");
		logger.info("User-ID = " + userid);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		String query = "select * from strafen where userid = '" + userid + "'";
		if (typ != null) {
			query = query + " AND typ = '" + typ + "'";
		}
		String Query = "select * from strafen where userid = '" + userid + "'";
		if (typ != null) {
			Query = Query + " AND typ = '" + typ + "'";
		}
		ResultSet rs = stat.executeQuery(query + ";");
		Integer counter = 0;
		while (rs.next()) {
			counter = rs.getRow();
		}
		rs.close();
		conn.close();
		return counter;
	}

	public Strafe setUserId(@Nonnull String newuserid) {
		this.userid = newuserid;
		return this;
	}

	public Strafe setTyp(@Nonnull String newtyp) {
		this.userid = newtyp;
		return this;
	}

	public Strafe setText(@Nonnull String newtext) {
		this.userid = newtext;
		return this;
	}

	@Nullable
	public String getId() {
		return id;
	}

	@Nonnull
	public String getUserId() {
		return userid;
	}

	@Nonnull
	public StrafenTyp getTyp() {
		return typ;
	}

	@Nonnull
	public String getTypString() {
		return typ.toString();
	}

	@Nonnull
	public String getText() {
		return text;
	}


	@Nonnull
	public String getModID() {
		return modid;
	}
	
	@Nonnull
	public String getModPing() {
		return "<@"+modid+">";
	}

	public void delete() throws SQLException {
		remove();
		this.id = null;
		this.text = null;
		this.userid = null;
		this.typ = null;
	}

	private void remove() throws SQLException {
		logger.info("Entferne Datensatz aus der Datenbank:");
		logger.info("ID = " + this.id);
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from strafen where id = '" + id + "';");
		conn.close();
	}

	@Nonnull
	public Strafe save() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();

		if (this.id == null) {
			logger.info("Füge Datensatz der Strafen-Datenbank hinzu:");
//Set ID
			ResultSet rs = stat.executeQuery("select * from sqlite_sequence where name = 'strafen';");
			Integer id = 0;
			while (rs.next()) {
				id =Integer.parseInt(rs.getString("seq")) + 1;
			}
			rs.close();
			this.id = id.toString();
		} else {
			logger.info("Aktuallisiere Datensatz der Strafen-Datenbank:");
			remove();
		}
		logger.info("ID = " + id + "     User-ID = " + userid + "     Strafen-Typ = " + typ);
		logger.info("Strafen-Text = " + text);
		stat.executeUpdate("INSERT INTO strafen (id, userid, typ, text, mod) VALUES ('" + id + "','" + userid + "','" + typ + "','" + text + "','" + modid + "');");
		stat.close();
		conn.close();
		return this;
	}

	private static StrafenTyp getEnum(String typ) {
		switch (typ) {
		case "Ban":
			return StrafenTyp.BAN;
		case "Tempan":
			return StrafenTyp.TEMPBAN;
		case "Mute":
			return StrafenTyp.MUTE;
		case "Kick":
			return StrafenTyp.KICK;
		case "Warn":
			return StrafenTyp.WARN;
		case "Hinweis":
			return StrafenTyp.HINWEIS;
		}
		return null;
	}
}
