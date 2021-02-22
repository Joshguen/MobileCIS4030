import java.util.ArrayList;
/**
 * ReceiptItems contain the information of items that can be found on a receipt
 * @param name A String depicting the name of the item
 * @param price A Double depicting how much the item is sold for
 */
public class ReceiptItem {
    public String name; 
    public double price; 
    public ArrayList<User> claims = new ArrayList<User>();

    //Constructor
    public ReceiptItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void addClaim(User user) {
        claims.add(user);
    }

    public void removeClaim(User user) {
        claims.remove(user);
    }
};