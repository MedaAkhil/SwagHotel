<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.model.FoodItem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.*" %>
<%@ page import="java.sql.*" %>

<%
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/swaghotel", "root", "12345678");

        // Retrieve all food items
        String query = "SELECT * FROM items";
        stmt = con.prepareStatement(query);
        rs = stmt.executeQuery();

        // Create a list to hold food items
        List<FoodItem> foodItems = new ArrayList<>();

        // Fetch data from result set and add to the list
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            double price = rs.getDouble("price");
            String category = rs.getString("category");

            FoodItem foodItem = new FoodItem(itemId, name, description, price, category);
            foodItems.add(foodItem);
        }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Food Items</title>
</head>
<body>
    <h1>Food Items Available:</h1>
    <ul id="foodList">
        <% for (FoodItem item : foodItems) { %>
            <li>
                <strong>Name:</strong> <%= item.getName() %> <br>
                <strong>Description:</strong> <%= item.getDescription() %> <br>
                <strong>Price:</strong> $<%= item.getPrice() %> <br>
                <strong>Category:</strong> <%= item.getCategory() %> <br>
            </li>
        <% } %>
    </ul>
</body>
</html>

<%
    } catch (ClassNotFoundException | SQLException e) {
        e.printStackTrace();
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
%>
