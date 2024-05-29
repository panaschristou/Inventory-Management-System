package Backend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class ItemDialog extends JDialog {
    private JTextField nameField, categoryField, producerField, countryField, priceField, stockField;
    private JTextField facilityField, dateReceivedField, totalBoughtPriceField, boughtUnitPriceField;
    private JButton saveButton, cancelButton;
    private ItemDAO itemDao;
    private DataOutputStream outputMessage;  // Stream to send data to server

    public ItemDialog(Frame owner, String title, Item item, DataOutputStream outputMessage) {
        super(owner, title, true);
        this.outputMessage = outputMessage;
        itemDao = new ItemDAO();
        setupForm(item);
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupForm(Item item) {
        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));  // Adjust grid layout to handle all fields

        nameField = new JTextField(20);
        categoryField = new JTextField(20);
        producerField = new JTextField(20);
        countryField = new JTextField(20);
        priceField = new JTextField(20);
        stockField = new JTextField(20);
        facilityField = new JTextField(20);
        dateReceivedField = new JTextField(20);
        totalBoughtPriceField = new JTextField(20);
        boughtUnitPriceField = new JTextField(20);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Producer:"));
        panel.add(producerField);
        panel.add(new JLabel("Country of Origin:"));
        panel.add(countryField);
        panel.add(new JLabel("Selling Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityField);
        panel.add(new JLabel("Date Received:"));
        panel.add(dateReceivedField);
        panel.add(new JLabel("Total Bought Price:"));
        panel.add(totalBoughtPriceField);
        panel.add(new JLabel("Bought Unit Price:"));
        panel.add(boughtUnitPriceField);

        // Ensure fields are filled if editing an existing item
        if (item != null) {
            nameField.setText(item.getName());
            categoryField.setText(item.getCategory());
            producerField.setText(item.getProducer());
            countryField.setText(item.getCountryOfOrigin());
            priceField.setText(String.valueOf(item.getSellingUnitPrice()));
            stockField.setText(String.valueOf(item.getStock()));
            facilityField.setText(item.getFacilityID());
            dateReceivedField.setText(item.getDateReceived());
            totalBoughtPriceField.setText(String.valueOf(item.getTotalBoughtPrice()));
            boughtUnitPriceField.setText(String.valueOf(item.getBoughtUnitPrice()));
        }

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveItem(item));
        cancelButton.addActionListener(e -> dispose());

        // Adding a button panel for better layout control
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveItem(Item item) {
        try {
            // Constructor for Item that matches all fields
            Item newItem = new Item(
                item == null ? 0 : item.getItemID(),
                nameField.getText(),
                categoryField.getText(),
                producerField.getText(),
                countryField.getText(),
                Double.parseDouble(priceField.getText()),
                Integer.parseInt(stockField.getText()),
                facilityField.getText(),
                dateReceivedField.getText(),
                Double.parseDouble(totalBoughtPriceField.getText()),
                Double.parseDouble(boughtUnitPriceField.getText())
            );
            // Logic to determine if this is an add or update
            String action = (item == null) ? "add" : "update";
            // Assuming methods to handle database update
            if (item == null) {
                itemDao.addItem(newItem);
            } else {
                itemDao.updateItem(newItem);
            }

            // Send confirmation to server
            outputMessage.writeUTF(action.toUpperCase() + " operation performed for Item ID: " + newItem.getItemID());
            outputMessage.flush();

            dispose();  // Close the dialog
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error sending update details to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
