public class User
{
    public String name; 
    public int ID; 
    public Receipt claimedItems = new Receipt();

    //Constructor
    public User(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    //adds a copy of an item to the user's claimedItems
    public void claimItem(ReceiptItem item, int quantity) {
        ReceiptItem toAdd = new ReceiptItem(item.name, item.price);
        claimedItems.addItem(toAdd);
    }

    //adds an item to receipt of the user for easier management
    public void unclaimItem(ReceiptItem item, int quantity) {
        claimedItems.removeItem(item);
    }
};