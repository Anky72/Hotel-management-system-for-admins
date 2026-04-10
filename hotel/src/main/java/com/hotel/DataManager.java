package com.hotel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String DB_URL = "jdbc:sqlite:hotel.db";

    // ── Init ───────────────────────────────────────────────────────────────
    static {
        initDB();
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDB() {
        String createRooms =
            "CREATE TABLE IF NOT EXISTS rooms (" +
            "  room_number  INTEGER PRIMARY KEY," +
            "  type         TEXT    NOT NULL," +
            "  price_per_day REAL   NOT NULL," +
            "  available    INTEGER NOT NULL DEFAULT 1" +   // 1=true, 0=false
            ");";

        String createBookings =
            "CREATE TABLE IF NOT EXISTS bookings (" +
            "  booking_id    INTEGER PRIMARY KEY," +
            "  customer_name TEXT    NOT NULL," +
            "  phone         TEXT    NOT NULL," +
            "  room_number   INTEGER NOT NULL," +
            "  check_in      TEXT    NOT NULL," +   // ISO date string
            "  check_out     TEXT    NOT NULL," +
            "  total_bill    REAL    NOT NULL," +
            "  status        TEXT    NOT NULL DEFAULT 'ACTIVE'" +
            ");";

        try (Connection con = connect();
             Statement st  = con.createStatement()) {
            st.execute(createRooms);
            st.execute(createBookings);

            // Seed default rooms only when the table is empty
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM rooms;");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("INSERT INTO rooms VALUES (101,'Single',1200,1);");
                st.execute("INSERT INTO rooms VALUES (102,'Single',1200,1);");
                st.execute("INSERT INTO rooms VALUES (201,'Double',2200,1);");
                st.execute("INSERT INTO rooms VALUES (202,'Double',2200,1);");
                st.execute("INSERT INTO rooms VALUES (301,'Suite', 4500,1);");
            }
        } catch (SQLException e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }

    // ── Rooms ──────────────────────────────────────────────────────────────
    public static void saveRooms(List<Room> rooms) {
        String upsert =
            "INSERT INTO rooms (room_number, type, price_per_day, available) VALUES (?,?,?,?) " +
            "ON CONFLICT(room_number) DO UPDATE SET " +
            "  type          = excluded.type," +
            "  price_per_day = excluded.price_per_day," +
            "  available     = excluded.available;";

        // Collect current room numbers to detect deletions
        List<Integer> incomingNumbers = new ArrayList<>();
        for (Room r : rooms) incomingNumbers.add(r.getRoomNumber());

        try (Connection con = connect()) {
            con.setAutoCommit(false);

            // Remove rooms that no longer exist in the list
            try (PreparedStatement del = con.prepareStatement(
                    "DELETE FROM rooms WHERE room_number = ?;")) {
                ResultSet existing = con.createStatement()
                        .executeQuery("SELECT room_number FROM rooms;");
                while (existing.next()) {
                    int num = existing.getInt(1);
                    if (!incomingNumbers.contains(num)) {
                        del.setInt(1, num);
                        del.addBatch();
                    }
                }
                del.executeBatch();
            }

            // Upsert every room in the list
            try (PreparedStatement ps = con.prepareStatement(upsert)) {
                for (Room r : rooms) {
                    ps.setInt   (1, r.getRoomNumber());
                    ps.setString(2, r.getType());
                    ps.setDouble(3, r.getPricePerDay());
                    ps.setInt   (4, r.isAvailable() ? 1 : 0);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_number, type, price_per_day, available FROM rooms;";
        try (Connection con = connect();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                Room r = new Room(
                    rs.getInt   ("room_number"),
                    rs.getString("type"),
                    rs.getDouble("price_per_day")
                );
                r.setAvailable(rs.getInt("available") == 1);
                rooms.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // ── Bookings ───────────────────────────────────────────────────────────
    public static void saveBookings(List<Booking> bookings) {
        String upsert =
            "INSERT INTO bookings " +
            "  (booking_id, customer_name, phone, room_number, check_in, check_out, total_bill, status) " +
            "VALUES (?,?,?,?,?,?,?,?) " +
            "ON CONFLICT(booking_id) DO UPDATE SET " +
            "  customer_name = excluded.customer_name," +
            "  phone         = excluded.phone," +
            "  room_number   = excluded.room_number," +
            "  check_in      = excluded.check_in," +
            "  check_out     = excluded.check_out," +
            "  total_bill    = excluded.total_bill," +
            "  status        = excluded.status;";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(upsert)) {
            con.setAutoCommit(false);
            for (Booking b : bookings) {
                ps.setInt   (1, b.getBookingId());
                ps.setString(2, b.getCustomerName());
                ps.setString(3, b.getPhone());
                ps.setInt   (4, b.getRoomNumber());
                ps.setString(5, b.getCheckIn().toString());
                ps.setString(6, b.getCheckOut().toString());
                ps.setDouble(7, b.getTotalBill());
                ps.setString(8, b.getStatus());
                ps.addBatch();
            }
            ps.executeBatch();
            con.commit();
        } catch (SQLException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT booking_id, customer_name, phone, room_number, " +
                     "       check_in, check_out, total_bill, status FROM bookings;";
        try (Connection con = connect();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                Booking b = new Booking(
                    rs.getInt   ("booking_id"),
                    rs.getString("customer_name"),
                    rs.getString("phone"),
                    rs.getInt   ("room_number"),
                    LocalDate.parse(rs.getString("check_in")),
                    LocalDate.parse(rs.getString("check_out")),
                    rs.getDouble("total_bill")
                );
                b.setStatus(rs.getString("status"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }
}