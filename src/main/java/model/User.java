package model;

import org.bson.Document;
import lombok.Data;

@Data
public class User implements Entity {
    private final int id;
    private final String name;
    private final Currency currency;

    public User(int id, String name, Currency currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public User(Document document) {
        this.id = document.getInteger("id");
        this.name = document.getString("name");
        this.currency = Currency.valueOf(document.getString("currency"));
    }

    @Override
    public Document toDocument() {
        return new Document("id", id)
                .append("name", name)
                .append("currency", currency.toString());
    }
}
