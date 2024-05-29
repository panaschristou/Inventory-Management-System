package Backend;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class InventoryClientGUI extends JFrame implements Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Thread thread;
    private DataOutputStream outputMessage = null;
    private DataInputStream inputMessage = null;
    private JTextArea textArea;
    private Socket socket = null;
    private JTable table;
    private InventoryTableModel tableModel;
    private TableRowSorter<InventoryTableModel> sorter;
    private ItemDAO itemDao;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JComboBox<String> columnSelector;
    private JTextField filterField;

    public InventoryClientGUI() {
        super("Inventory Client");
        this.setLayout(new BorderLayout());
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.itemDao = new ItemDAO();
        createMenu();
        setupUI();
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem connectItem = new JMenuItem("Connect");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> System.exit(0));
        connectItem.addActionListener(this::connectToServer);

        menu.add(connectItem);
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void connectToServer(ActionEvent e) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            socket = new Socket("localhost", 9898);
            outputMessage = new DataOutputStream(socket.getOutputStream());
            inputMessage = new DataInputStream(socket.getInputStream());

            String otp = JOptionPane.showInputDialog(this, "Enter OTP:");
            if (otp != null && !otp.isEmpty()) {
                outputMessage.writeUTF("OTP:" + otp);
                String response = inputMessage.readUTF();
                if ("Access granted".equals(response)) {
                    textArea.append("Connected to Server\n");
                    JOptionPane.showMessageDialog(this, "Logged in successfully!");
                    setDatabaseInteractionEnabled(true);
                    if (thread == null || !thread.isAlive()) {
                        thread = new Thread(this);
                        thread.start();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Access denied. Please try again.");
                    setDatabaseInteractionEnabled(false);
                }
            } else {
                JOptionPane.showMessageDialog(this, "OTP entry cancelled. Please connect again to retry.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Connection Failure: " + ex.getMessage());
        }
    }

    private void setupUI() {
        JPanel northPanel = new JPanel(new BorderLayout());
        setupButtonPanel(northPanel);
        setupFilterPanel(northPanel);

        this.add(northPanel, BorderLayout.NORTH);

        tableModel = new InventoryTableModel();
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);  // Attach the sorter to the table
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea(5, 30);
        textArea.setEditable(false);
        this.add(new JScrollPane(textArea), BorderLayout.SOUTH);
    }

    private void setupButtonPanel(JPanel panel) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        refreshButton.setEnabled(false);

        addButton.addActionListener(e -> openAddDialog());
        updateButton.addActionListener(e -> openUpdateDialog());
        deleteButton.addActionListener(e -> deleteSelectedItem());
        refreshButton.addActionListener(e -> refreshTable());

        panel.add(buttonPanel, BorderLayout.NORTH);
    }

    private void setupFilterPanel(JPanel panel) {
        JPanel filterPanel = new JPanel(new FlowLayout());
        filterField = new JTextField(20);
        columnSelector = new JComboBox<>(new String[]{"Item ID", "Name", "Category", "Producer", "Country"});

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            public void changedUpdate(DocumentEvent e) { updateFilter(); }
        });

        filterPanel.add(filterField);
        filterPanel.add(columnSelector);

        panel.add(filterPanel, BorderLayout.SOUTH);
    }

    private void updateFilter() {
        String text = filterField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(text, columnSelector.getSelectedIndex()));
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(this, "Bad regex pattern", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setDatabaseInteractionEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        filterField.setEnabled(enabled);
        columnSelector.setEnabled(enabled);
    }

    private void openAddDialog() {
        if (!addButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please log in to perform this action.");
            return;
        }
        JDialog dialog = new ItemDialog(this, "Add Item", null, outputMessage);
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshTable(); // Refresh the table after the dialog is closed
            }
        });
    }

    private void openUpdateDialog() {
        if (!updateButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please log in to perform this action.");
            return;
        }
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Item item = tableModel.getItemAt(selectedRow);
            JDialog dialog = new ItemDialog(this, "Update Item", item, outputMessage);
            dialog.setVisible(true);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshTable(); // Refresh the table after the dialog is closed
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedItem() {
        if (!deleteButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please log in to perform this action.");
            return;
        }
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Item item = tableModel.getItemAt(selectedRow);
                itemDao.deleteItem(item.getItemID());
                try {
                    outputMessage.writeUTF("Deleted item ID " + item.getItemID() + ": " + item.getName());
                    outputMessage.flush();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Failed to send delete request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No item selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        if (!refreshButton.isEnabled()) {
            JOptionPane.showMessageDialog(this, "Please log in to view items.");
            return;
        }
        List<Item> items = itemDao.getAllItems();
        tableModel.setItems(items);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = inputMessage.readUTF();
                SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> textArea.append("Server connection closed\n"));
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new InventoryClientGUI();
    }
}
