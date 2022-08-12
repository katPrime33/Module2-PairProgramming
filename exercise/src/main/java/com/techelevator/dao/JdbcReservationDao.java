package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        Reservation reservation = new Reservation();
        String sql = "INSERT INTO reservation(site_id, name, from_date, to_date, create_date) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING reservation_id;";
        Integer newId = this.jdbcTemplate.queryForObject(sql, Integer.class, reservation.getSiteId(),
                reservation.getName(), reservation.getFromDate(), reservation.getToDate(), reservation.getCreateDate());
        reservation.setReservationId(newId);
        return reservation.getReservationId();
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        try {
            r.setReservationId(results.getInt("reservation_id"));
            r.setSiteId(results.getInt("site_id"));
            r.setName(results.getString("name"));
            r.setFromDate(results.getDate("from_date").toLocalDate());
            r.setToDate(results.getDate("to_date").toLocalDate());
            r.setCreateDate(results.getDate("create_date").toLocalDate());
        }
        catch (NullPointerException e) {
            System.out.println("Invalid input");
        }
        return r;
    }


}
