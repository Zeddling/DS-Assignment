package com.dao.h2;

import com.dao.ToyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToyDatabase {
    private static final Logger log = LoggerFactory.getLogger(ToyDatabase.class);

    private static final String CREATE_TABLE_SQL = " CREATE TABLE ToyInfo (\r\n" +
            "toyCode varchar(32) primary key,\r\n" + " toyName varchar(100),\r\n" +
            "description varchar(255),\r\n" + "price varchar(32),\r\n" +
            "manufacturingDate varchar(20),\r\n" + "batchNo varchar(32),\r\n" +
            "companyName varchar(100),\r\n" + "streetAddress varchar(100),\r\n" +
            "zipCode varchar(10),\r\n" + "comment varchar(255)\r\n" + " );";

    private static final String SELECT_ALL = "SELECT * FROM ToyInfo;";

    private static final String INSERT_TOY_INFORMATION = "INSERT INTO ToyInfo" +
            " (toyCode, toyName, description, price, manufacturingDate, batchNo," +
            " companyName, streetAddress, zipCode, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public void creatTable() throws SQLException {
        log.info("Creating database ToyInfo");
        try (
                 Connection connection = H2JDBCUtils.getConnection();
                 Statement statement = connection.createStatement();
             ) {
            statement.execute(CREATE_TABLE_SQL);
            log.info("Table created");
        }catch (SQLException e){
            H2JDBCUtils.printSQLException(e);
        }
    }

    public void save(List<ToyInfo> toyInfoList) throws SQLException {
        log.info("Inserting records");
        try(
                Connection connection = H2JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TOY_INFORMATION);
            ) {
            for (int i = 0; i < toyInfoList.size(); i++) {
                preparedStatement.setString(i+1, String.valueOf(toyInfoList.get(i)));
            }
            preparedStatement.executeUpdate();
            log.info("Records successfully inserted");
        } catch (SQLException e) {
            H2JDBCUtils.printSQLException(e);
        }
    }

    public List<String> showWithId(String id) {
        log.info("Fetching records");
        ResultSet rs = null;
        List<String> list = null;
        try(
                Connection connection = H2JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL);
                ) {
            //preparedStatement.setString(1, id);
            rs = preparedStatement.executeQuery();
            log.info("Records fetched");
            list = new ArrayList<>();
            int i = 1;
            while (rs.next()){
                list.add(rs.getString(i));
                i++;
            }

        }catch (SQLException e) {
            H2JDBCUtils.printSQLException(e);
        }
        return list;
    }




}
