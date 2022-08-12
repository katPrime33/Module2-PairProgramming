package com.techelevator.dao;

import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql = "INSERT INTO reservation(site_id, name, from_date, to_date, create_date) " +
                "VALUES (?, ?, ?, ?, CURRENT_DATE) " +
                "RETURNING reservation_id;";
        Integer newId = this.jdbcTemplate.queryForObject(sql, Integer.class, siteId,
                name, fromDate, toDate);

        return newId;
    }

    public List<Reservation> getUpcomingReservations(int parkId){
        List<Reservation> reservationList = new ArrayList<>();
        String sql = "SELECT reservation_id, reservation.site_id, reservation.name, from_date, to_date, create_date " +
                "FROM reservation " +
                "JOIN site ON reservation.site_id = site.site_id " +
                "JOIN campground ON site.campground_id = campground.campground_id " +
                "WHERE from_date BETWEEN CURRENT_DATE AND CURRENT_DATE +30 AND park_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parkId);
        while(result.next()){
            reservationList.add(mapRowToReservation(result));
        }
        return reservationList;
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
