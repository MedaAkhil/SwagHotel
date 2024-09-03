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

@WebServlet("/orders")
public class Orders extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/swaghotel";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "12345678";

    private static final Logger LOGGER = Logger.getLogger(Orders.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            // User not logged in, redirect to login page
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Retrieve orders for the logged-in user
            String selectQuery = "SELECT * FROM orders WHERE username = ?";
            stmt = con.prepareStatement(selectQuery);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            // Start generating HTML content
            PrintWriter out = response.getWriter();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>User Orders</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; }");
            out.println(".container { width: 80%; margin: 0 auto; }");
            out.println(".order { background-color: #f9f9f9; padding: 10px; margin-bottom: 10px; border-radius: 5px; }");
            out.println(".order p { margin: 0; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class=\"container\">");
            out.println("<center><h2>User Orders</h2></center><br>");

            // Iterate through the result set and generate HTML divs with order details
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String itemName = rs.getString("item_name");
                String type = rs.getString("type");
                double itemPrice = rs.getDouble("price");
                if (type == "food") {
                	continue;
                }
                out.println("<div class=\"order\">");
                out.println("<p><strong>Order ID:</strong> " + orderId + "</p>");
                out.println("<p><strong>Item Name:</strong> " + itemName + "</p>");
                out.println("<p><strong>Price:</strong> " + itemPrice + "</p>");
                out.println("</div><hr>");
            }

            out.println("</div>");

            out.println("</body>");
            out.println("</html>");


        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing SQL query", e);
            response.sendRedirect("error.html");
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources", e);
            }
        }
    }
}
