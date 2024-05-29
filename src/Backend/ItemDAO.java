package Backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDAO {
	private static final Logger logger = Logger.getLogger(ItemDAO.class.getName());

    /**
     * Retrieves all items with their stock from the database.
     * @return A list of Item objects with details from both Items and Stock tables.
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.ItemID, i.Name, i.Category, i.Producer, i.CountryOfOrigin, i.SellingUnitPrice, s.FacilityID, s.DateReceived, s.QuantityReceived AS stock, s.TotalBoughtPrice, s.BoughtUnitPrice FROM Items i JOIN Stock s ON i.ItemID = s.ItemID";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item(rs.getInt("ItemID"), rs.getString("Name"), rs.getString("Category"), rs.getString("Producer"), rs.getString("CountryOfOrigin"), rs.getDouble("SellingUnitPrice"), rs.getInt("stock"), rs.getString("FacilityID"), rs.getString("dateReceived"), rs.getDouble("totalBoughtPrice"), rs.getDouble("boughtUnitPrice"));
                items.add(item);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all items", e);
        }
        return items;
    }

    /**
     * Adds a new item and its initial stock to the database.
     * @param item The item to add with initial stock details.
     */
    public void addItem(Item item) {
        String sqlItem = "INSERT INTO Items (Name, Category, Producer, CountryOfOrigin, SellingUnitPrice) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "INSERT INTO Stock (ItemID, FacilityID, DateReceived, QuantityReceived, TotalBoughtPrice, BoughtUnitPrice) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtStock = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
            conn.setAutoCommit(false);  // Start transaction

            // Insert into Items
            pstmtItem = conn.prepareStatement(sqlItem, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmtItem.setString(1, item.getName());
            pstmtItem.setString(2, item.getCategory());
            pstmtItem.setString(3, item.getProducer());
            pstmtItem.setString(4, item.getCountryOfOrigin());
            pstmtItem.setDouble(5, item.getSellingUnitPrice());
            int affectedRowsItem = pstmtItem.executeUpdate();

            if (affectedRowsItem > 0) {
                try (ResultSet generatedKeys = pstmtItem.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newItemID = generatedKeys.getInt(1);

                        // Insert into Stock
                        pstmtStock = conn.prepareStatement(sqlStock);
                        pstmtStock.setInt(1, newItemID);
                        pstmtStock.setString(2, item.getFacilityID());
                        pstmtStock.setString(3, item.getDateReceived());
                        pstmtStock.setInt(4, item.getStock());
                        pstmtStock.setDouble(5, item.getTotalBoughtPrice());
                        pstmtStock.setDouble(6, item.getBoughtUnitPrice());
                        pstmtStock.executeUpdate();
                    }
                }
            }
            conn.commit();  // Commit transaction
            logger.info("Item and stock successfully added: " + item.getName());
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Roll back transaction
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            logger.log(Level.SEVERE, "Failed to add item: " + item.getName(), e);
        } finally {
            try {
                if (pstmtItem != null) pstmtItem.close();
                if (pstmtStock != null) pstmtStock.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to close resources", ex);
            }
        }
    }

    /**
     * Updates an existing item in the database, along with its stock information.
     * @param item The item to update, including new stock details and facility ID.
     */
    public void updateItem(Item item) {
        String sqlUpdateItem = "UPDATE Items SET Category = ?, Producer = ?, CountryOfOrigin = ?, SellingUnitPrice = ? WHERE ItemID = ?";
        String sqlUpdateStock = "UPDATE Stock SET DateReceived = ?, QuantityReceived = ?, TotalBoughtPrice = ?, BoughtUnitPrice = ? WHERE ItemID = ? AND FacilityID = ?";
        Connection conn = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtStock = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
            conn.setAutoCommit(false); // Start transaction

            // Update Items table
            pstmtItem = conn.prepareStatement(sqlUpdateItem);
            pstmtItem.setString(1, item.getCategory());
            pstmtItem.setString(2, item.getProducer());
            pstmtItem.setString(3, item.getCountryOfOrigin());
            pstmtItem.setDouble(4, item.getSellingUnitPrice());
            pstmtItem.setInt(5, item.getItemID());
            pstmtItem.executeUpdate();

            // Update Stock table
            pstmtStock = conn.prepareStatement(sqlUpdateStock);
            pstmtStock.setString(1, item.getDateReceived());
            pstmtStock.setInt(2, item.getStock());
            pstmtStock.setDouble(3, item.getTotalBoughtPrice());
            pstmtStock.setDouble(4, item.getBoughtUnitPrice());
            pstmtStock.setInt(5, item.getItemID());
            pstmtStock.setString(6, item.getFacilityID());
            pstmtStock.executeUpdate();

            conn.commit(); // Commit transaction
            logger.info("Item and stock successfully updated: " + item.getName());
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back transaction
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            logger.log(Level.SEVERE, "Failed to update item: " + item.getName(), e);
        } finally {
            try {
                if (pstmtItem != null) pstmtItem.close();
                if (pstmtStock != null) pstmtStock.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to close resources", ex);
            }
        }
    }
    /**
     * Deletes an item from the database based on its ItemID.
     * @param itemID The ID of the item to delete.
     */
    public void deleteItem(int itemID) {
        String sqlDeleteStock = "DELETE FROM Stock WHERE ItemID = ?";
        String sqlDeleteItem = "DELETE FROM Items WHERE ItemID = ?";
        Connection conn = null;
        PreparedStatement pstmtStock = null;
        PreparedStatement pstmtItem = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
            conn.setAutoCommit(false); // Start transaction

            // Delete from Stock table
            pstmtStock = conn.prepareStatement(sqlDeleteStock);
            pstmtStock.setInt(1, itemID);
            pstmtStock.executeUpdate();

            // Delete from Items table
            pstmtItem = conn.prepareStatement(sqlDeleteItem);
            pstmtItem.setInt(1, itemID);
            pstmtItem.executeUpdate();

            conn.commit(); // Commit transaction
            logger.info("Item and related stock successfully deleted for ItemID: " + itemID);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Roll back transaction
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            logger.log(Level.SEVERE, "Failed to delete item for ItemID: " + itemID, e);
        } finally {
            try {
                if (pstmtStock != null) pstmtStock.close();
                if (pstmtItem != null) pstmtItem.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to close resources", ex);
            }
        }
    }
}
