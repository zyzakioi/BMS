package model;

import config.Attr;
import config.Tables;
import exceptions.BMSException;
import utils.SecurityUtils;
import view.View;

import java.sql.ResultSet;
import java.sql.SQLException;

interface User extends AutoCloseable {
    void login() throws SQLException, BMSException;

    static boolean auth(Attr ID, Attr PW, String id, char[] pw) throws SQLException {
        String[] columns = new String[]{PW.getAttrName()};
        String conditionClause = ID.getAttrName() + " = ?";
        String[] conditionVals = new String[]{id.toLowerCase()};
        try (ResultSet rs = Tables.ADMIN.query(columns, conditionClause, conditionVals)){
            if (!rs.next()) return false;
            String hash = rs.getString(1);
            return SecurityUtils.checkPasswd(pw, hash);
        }
    }
}