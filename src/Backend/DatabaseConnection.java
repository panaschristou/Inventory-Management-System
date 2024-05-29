package Backend;

import java.sql.*;

public class DatabaseConnection {public static void main(String[] args)      throws SQLException, ClassNotFoundException { 
	// SQLite connection string
    String url = "jdbc:sqlite:inventory.db";

    // SQL statements for creating tables
    String sqlCreateFacilities = "CREATE TABLE IF NOT EXISTS Facilities (FacilityID TEXT PRIMARY KEY, Location TEXT NOT NULL,Country TEXT NOT NULL,Address TEXT NOT NULL);";

    String sqlCreateItems = "CREATE TABLE IF NOT EXISTS Items (ItemID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT NOT NULL,Category TEXT NOT NULL,Producer TEXT NOT NULL,CountryOfOrigin TEXT NOT NULL,SellingUnitPrice REAL NOT NULL);";

    String sqlCreateStock = "CREATE TABLE IF NOT EXISTS Stock (StockID INTEGER PRIMARY KEY AUTOINCREMENT,ItemID INTEGER NOT NULL,FacilityID TEXT NOT NULL,DateReceived DATE NOT NULL,QuantityReceived INTEGER NOT NULL,TotalBoughtPrice REAL NOT NULL,BoughtUnitPrice REAL NOT NULL,FOREIGN KEY (ItemID) REFERENCES Items(ItemID),FOREIGN KEY (FacilityID) REFERENCES Facilities(FacilityID)); ";

	// Establish a connection
    Connection connection = DriverManager.getConnection(url);
    System.out.println("Database connected");
    
    try (Statement stmt = connection.createStatement()) {
           // Create tables
           stmt.execute(sqlCreateFacilities);
           stmt.execute(sqlCreateItems);
           stmt.execute(sqlCreateStock);

           // Insert sample data
           stmt.executeUpdate("INSERT INTO Facilities (FacilityID, Location, Country, Address) VALUES ('US-NY-01', 'New York', 'USA', '1234 NY St, New York'), ('CA-TO-02', 'Toronto', 'Canada', '5678 Maple Rd, Toronto');");
           stmt.executeUpdate("INSERT INTO Items (Name, Category, Producer, CountryOfOrigin, SellingUnitPrice) VALUES ('LED Bulb', 'Electronics', 'Philips', 'Netherlands', 5.99), ('T-Shirt', 'Clothing', 'H&M', 'Bangladesh', 14.99);");
           stmt.executeUpdate("INSERT INTO Stock (ItemID, FacilityID, DateReceived, QuantityReceived, TotalBoughtPrice, BoughtUnitPrice) VALUES (1, 'US-NY-01', '2024-05-01', 100, 400.00, 4.00), (2, 'CA-TO-02', '2024-04-15', 200, 2200.00, 11.00);");

           System.out.println("Tables created and data inserted successfully.");
       } catch (Exception e) {
           System.out.println(e.getMessage());
       }
   }
}

