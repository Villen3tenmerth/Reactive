package model;

import lombok.Data;
import org.bson.Document;

@Data
public class CartItem implements Entity {
    private final int userId;
    private final int productId;

    public CartItem(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public CartItem(Document document) {
        this.userId = document.getInteger("userId");
        this.productId = document.getInteger("productId");
    }

    @Override
    public Document toDocument() {
        return new Document("userId", userId)
                .append("productId", productId);
    }
}
