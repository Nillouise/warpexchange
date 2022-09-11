package com.itranswarp.exchange.mysql;

import java.sql.*;

public class TestMysql {

    public static void main(String[] args) throws SQLException {
        String JDBC_URL = "jdbc:mysql://localhost:3306/test";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "root", "password")) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, grade, name, gender FROM students WHERE gender=1")) {
                    while (rs.next()) {
                        long id = rs.getLong(1); // 注意：索引从1开始
                        long grade = rs.getLong(2);
                        String name = rs.getString(3);
                        int gender = rs.getInt(4);
                        System.out.println(name);
                    }
                }
            }
        }
    }
}
