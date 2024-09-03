import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/booking")
public class Booking extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/swaghotel";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "12345678";

    private static final Logger LOGGER = Logger.getLogger(Booking.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
        int itemId = Integer.parseInt(request.getParameter("id"));
//        System.out.println(customerName+itemId);
//        String customerName = request.getParameter("customerName"); // Assuming you have a way to get customer name
        if (session == null || session.getAttribute("username") == null) {
            // If session is not present or username attribute is not set, redirect to login page
            response.sendRedirect("login.html");
            return;
        }
        String customerName = (String) session.getAttribute("username");
        Connection con = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        String referrer = request.getHeader("referer");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Retrieve the details of the selected food item
            String selectQuery = "SELECT * FROM items WHERE item_id = ?";
            selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setInt(1, itemId);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                String itemName = rs.getString("name");
                double itemPrice = rs.getDouble("price");

                // Insert the booking information into the orders table
                String insertQuery = "INSERT INTO orders (username, item_name, type, price) VALUES (?, ?, ?, ?)";
                insertStmt = con.prepareStatement(insertQuery);
                insertStmt.setString(1, customerName);
                insertStmt.setString(2, itemName);
                insertStmt.setString(3, "food");
                insertStmt.setDouble(4, itemPrice);
                insertStmt.executeUpdate();
                PrintWriter out = response.getWriter();
                // Redirect to booking confirmation page with item details as parameters
                response.sendRedirect(referrer);
            } else {
                response.sendRedirect("index.html");
            }

        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing SQL query", e);
            response.sendRedirect("acroom.html");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (selectStmt != null)
                    selectStmt.close();
                if (insertStmt != null)
                    insertStmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources", e);
            }
        }
    }
}
