import java.util.ArrayList;

public class Receipt {
    public ArrayList<ReceiptItem> items = new ArrayList<ReceiptItem>();

    public void addItem(ReceiptItem item) {
        items.add(item);
    }

    public int getTotal() {
        int total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += ( items.get(i).price * items.get(i).quantity );
        }
        return total;
    }

    public double getTax(int taxPercent) {
        double tax = getTotal() * taxPercent * 0.01;
        return tax;
    }
}