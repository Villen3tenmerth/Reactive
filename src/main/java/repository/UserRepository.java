package repository;

import model.User;
import mongodb.MongoDBProvider;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository {
    private final static String dbTableName = "users";
    private final MongoDBProvider provider;

    public UserRepository(MongoDBProvider provider) {
        this.provider = provider;
    }

    public Observable<User> getUserById(int id) {
        return provider.getDatabase()
                .getCollection(dbTableName)
                .find(eq("id", id))
                .toObservable()
                .map(User::new);
    }

    public Observable<String> getAllUsers() {
        return provider.getDatabase()
                .getCollection(dbTableName)
                .find()
                .toObservable()
                .map(User::new)
                .map(User::toString);
    }

    public Observable<String> saveUser(User user) {
        return getUserById(user.getId()).isEmpty().flatMap(notFound -> {
            if (notFound) {
                return provider.getDatabase()
                        .getCollection(dbTableName)
                        .insertOne(user.toDocument())
                        .map(suc -> "User " + user + " was successfully added");
            } else {
                return Observable.just("User with id " + user.getId() + " already exists");
            }
        });
    }
}
