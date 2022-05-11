package mongodb;

import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoDatabase;

public class MongoDBProvider {
    private final MongoDatabase db;

    public MongoDBProvider(String name, int port) {
        db = MongoClients.create("mongodb://localhost:" + port).getDatabase(name);
    }

    public MongoDatabase getDatabase() {
        return db;
    }
}
