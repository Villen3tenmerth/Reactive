package model;

import lombok.Data;
import org.bson.Document;

@Data
public class Product implements Entity {
    private final int id;
    private final String name;
    private final String description;
    private final double price;

    public Product(int id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(Document document) {
        this.id = document.getInteger("id");
        this.name = document.getString("name");
        this.description = document.getString("description");
        this.price = document.getDouble("price");
    }

    @Override
    public Document toDocument() {
        return new Document("id", id)
                .append("name", name)
                .append("description", description)
                .append("price", price);
    }
}
