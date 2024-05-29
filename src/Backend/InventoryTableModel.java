package Backend;

import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Item ID", "Name", "Category", "Producer", "Country of Origin", "Selling Price", "Stock"};
    private List<Item> items;

    public InventoryTableModel() {
        items = new ArrayList<>();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        fireTableDataChanged(); // Notify the JTable that the data has changed
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = items.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return item.getItemID();
            case 1:
                return item.getName();
            case 2:
                return item.getCategory();
            case 3:
                return item.getProducer();
            case 4:
                return item.getCountryOfOrigin();
            case 5:
                return item.getSellingUnitPrice();
            case 6:
                return item.getStock();
            default:
                return null;
        }
    }

    public Item getItemAt(int rowIndex) {
        return items.get(rowIndex);
    }
}
