package de.Strobl.Instances;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class StrafeTemp {
	private static final Logger logger = LogManager.getLogger(StrafeTemp.class);

	String id;
	String userid;
	StrafenTyp typ;
	DateTime unbantime;

	public StrafeTemp(Strafe strafe, DateTime unbantime) {
		this.id = strafe.getId();
		this.typ = strafe.getTyp();
		this.userid = strafe.getUserId();
		this.unbantime = unbantime;
	}

	private StrafeTemp(String id, String userid, StrafenTyp typ, DateTime unbantime) {
		this.id = id;
		this.typ = typ;
		this.unbantime = unbantime;
	}

	public static List<StrafeTemp> getAll() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from temp;");
		List<StrafeTemp> temp = new ArrayList<StrafeTemp>();
		while (rs.next()) {
			temp.add(new StrafeTemp(rs.getString("id"), rs.getString("userid"), getEnum(rs.getString("typ")), DateTime.parse(rs.getString("time"))));
		}
		rs.close();
		conn.close();
		return temp;
	}

	public static List<StrafeTemp> getByUserId(String userid) throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from temp where userid = '" + userid + "';");
		List<StrafeTemp> temp = new ArrayList<StrafeTemp>();
		while (rs.next()) {
			temp.add(new StrafeTemp(rs.getString("id"), rs.getString("userid"), getEnum(rs.getString("typ")), DateTime.parse(rs.getString("time"))));
		}
		rs.close();
		conn.close();
		return temp;
	}

	public DateTime getDateTime() {
		return this.unbantime;
	}

	public StrafenTyp getStrafenTyp() {
		return this.typ;
	}

	public String getUserID() {
		return this.userid;
	}

	public String getID() {
		return this.id;
	}

	public void delete() throws SQLException {
		logger.info("Entferne Temp-Strafe aus der Datenbank:");
		logger.info("ID = " + id);
		remove();
		this.id = null;
		this.typ = null;
		this.unbantime = null;
		this.userid = null;
	}

	private void remove() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from temp where id = '" + id + "';");
		conn.close();
	}

	public void save() throws SQLException {
		Connection conn = DriverManager.getConnection(SQL.connectionname);
		Statement stat = conn.createStatement();
		stat.executeUpdate("INSERT INTO temp (id, userid, typ, time) VALUES ('" + id + "', '" + userid + "','" + typ.toString() + "','" + unbantime + "')");
		conn.close();
		stat.close();
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
