package model;

public class Product {
    private int productId;
    private String productName;
    private String productColor;
    private String category;
    private int productStock;
    private double productWeight;
    private String description;

    public Product(String productName, String productColor, String category, int productStock, double productWeight, String description) {
        this.productName = productName;
        this.productColor = productColor;
        this.category = category;
        this.productStock = productStock;
        this.productWeight = productWeight;
        this.description = description;
    }

    public boolean reduceStock(int quantity) {
        if (productStock >= quantity) {
            productStock -= quantity;
            return true;
        }
        System.out.println("Yetersiz stok: " + productName);
        return false;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public double getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(double productWeight) {
        this.productWeight = productWeight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}