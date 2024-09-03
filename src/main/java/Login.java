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
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class Login extends HttpServlet {
    // JDBC URL, username, and password
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/swaghotel";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "12345678";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            // User is already logged in, redirect to index.html
            response.sendRedirect("index.html");
            return;
        }

        // Retrieve username and password from login form
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Establish database connection
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Query to retrieve user data based on username
            String query = "SELECT password FROM users WHERE username = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            // Check if the user exists and the password matches
            if (rs.next()) {
                String correctPassword = rs.getString("password");
                if (password.equals(correctPassword)) {
                    // Create a session for the user
                    HttpSession newSession = request.getSession();
                    newSession.setAttribute("username", username);
                    // Redirect the user to the home page
                    response.sendRedirect("index.html");
                    return;
                }
            }

            // If login fails, display an error message
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Invalid username or password</h2>");
            out.println("<a href=\"login.html\">Go back to login page</a>");
            out.println("</body></html>");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Send error response if there's an exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to POST
        doPost(request, response);
    }
}
