package Commands.ReactionRoles;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReactionRolesSql {

    private static Connection connection;

    public ReactionRolesSql() {

        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            connection = DriverManager.getConnection(SqlPrivate.sqlURL, SqlPrivate.sqlUsername, SqlPrivate.sqlPass);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    public static Connection getConn() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            connection = DriverManager.getConnection(SqlPrivate.sqlURL, SqlPrivate.sqlUsername, SqlPrivate.sqlPass);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        createTable();
        return connection;
    }

    private static void createTable() {
        try(PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reationroles" +
                "(" +
                " msgID BIGINT NOT NULL," +
                " emoji VARCHAR(256) NOT NULL," +
                " roleID BIGINT NOT NULL"+
                ");")) {
            ps.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void add(ReactionRole rr) {
        try(PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO reactionroles (msgID, emoji, roleID) " +
                        "VALUES (?,?,?);")) {
            setReactionRole(ps, rr.getMsgID(), rr.getEmoji(), rr.getRoleID());
            ps.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(long msgID) {
        try(PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM reactionroles WHERE msgID = ?;")) {
            ps.setLong(1, msgID);
            ps.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static ReactionRole getFromRole(long roleID) {
        ReactionRole reactionRole = new ReactionRoleImpl();
        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT msgID, emoji, roleID FROM reactionroles " +
                        "WHERE roleID = ?;")) {
            ps.setLong(1, roleID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                reactionRole.setMsgID(rs.getLong("msgID"));
                reactionRole.setEmoji(rs.getString("emoji"));
                reactionRole.setRoleID(rs.getLong("roleID"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return reactionRole;
    }

    public static ReactionRole getFromEmoji(String emoji, long msgID) {
        ReactionRole reactionRole = new ReactionRoleImpl();
        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT msgID, emoji, roleID FROM reactionroles " +
                        "WHERE (emoji = ? AND msgID = ?);")) {
            ps.setString(1, emoji);
            ps.setLong(2, msgID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                reactionRole.setMsgID(rs.getLong("msgID"));
                reactionRole.setEmoji(rs.getString("emoji"));
                reactionRole.setRoleID(rs.getLong("roleID"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return reactionRole;
    }

    public static List<ReactionRole> reactionRolesInMsg(long msgID) {
        List<ReactionRole> reactionRoles = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(
                "SELECT msgID, emoji, roleID FROM reactionroles " +
                "WHERE msgID = ?;")) {
            ps.setLong(1, msgID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                ReactionRole rr = new ReactionRoleImpl();
                rr.setMsgID(rs.getLong("msgID"));
                rr.setEmoji(rs.getString("emoji"));
                rr.setRoleID(rs.getLong("roleID"));
                reactionRoles.add(rr);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return reactionRoles;
    }

    private static void setReactionRole(PreparedStatement ps, long msgID, String emoji, long roleID) throws SQLException {
        ps.setLong(1, msgID);
        ps.setString(2, emoji);
        ps.setLong(3, roleID);
    }

}
