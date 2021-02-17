/**
 * ReceiptItems contain the information of items that can be found on a receipt
 * @param name A String depicting the name of the item
 * @param price An Int depicting how much the item is sold for
 * @param quantity An Int depicting the number of the item were sold
 */
public class ReceiptItem {
    public String name; 
    public int price; 
    public int quantity; 

    public ReceiptItem(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
};