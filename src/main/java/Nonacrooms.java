import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/nonacrooms")
public class Nonacrooms extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html"); // Set content type to HTML
        PrintWriter out = response.getWriter();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Establish database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/swaghotel", "root", "12345678");

            // Retrieve all non-vegetarian food items
            String query = "SELECT * FROM accommodation WHERE category = 'Non-AC Room'";
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();

            // Start generating HTML content
            out.println("<h1>Non-AC Rooms Available:</h1><br>");
            out.println("<ul class=\"foodList\" id=\"nonvegList\">");

            // Iterate through result set and generate HTML list items
            while (rs.next()) {
                String RoomNo = rs.getString("room_number");
                String description = rs.getString("category");
                double price = rs.getDouble("price");
                int itemId=rs.getInt("room_number");
                String category = rs.getString("capacity");
                String imageUrl = rs.getString("image_url");

                out.println("<li class=\"card\">");
                out.println("<a href=\"bookingroom?id=" + itemId + "\">");
                out.println("<div class=\"h1img\"><h2 class=\"itemh1\">RoomNo: " + RoomNo + "</h2>");
                out.println("<img src=\"" + imageUrl + "\"></div>"); // Add image element
                out.println("<hr><div><strong>Description:</strong> <p>" + description + "</p></div><br>");
                out.println("<strong>Price:</strong> $" + price + "<br>");
                out.println("<strong>Category:</strong> " + category + "<br>");
                out.println("</li>");
            }

            // End generating HTML content
            out.println("</ul>");
            out.println("</body>");
            out.println("</html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Send error response if there's an exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Close resources
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
