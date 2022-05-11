package repository;

import model.Currency;
import model.Product;
import model.User;
import mongodb.MongoDBProvider;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class ProductRepository {
    private final static String dbTableName = "products";
    private final MongoDBProvider provider;
    private final UserRepository userRepository;

    public ProductRepository(MongoDBProvider provider) {
        this.provider = provider;
        this.userRepository = new UserRepository(provider);
    }

    public Observable<String> getProductById(int id) {
        return provider.getDatabase()
                .getCollection(dbTableName)
                .find(eq("id", id))
                .toObservable()
                .map(Product::new)
                .map(Product::toString);
    }

    public Observable<String> getAllInCurrency(Currency currency) {
        return provider.getDatabase()
                .getCollection(dbTableName)
                .find()
                .toObservable()
                .map(Product::new)
                .map(product ->
                        new Product(product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice() * currency.getCoef())
                                .toString());
    }

    public Observable<String> getAllByUserId(int userId) {
        Observable<User> user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("User with id " + userId + " doesn't exist");
            } else {
                return user.flatMap(u -> getAllInCurrency(u.getCurrency()));
            }
        });
    }

    public Observable<String> saveProduct(Product product) {
        return getProductById(product.getId()).isEmpty().flatMap(notFound -> {
            if (notFound) {
                return provider.getDatabase()
                        .getCollection(dbTableName)
                        .insertOne(product.toDocument())
                        .map(suc -> "Product " + product + " was successfully added");
            } else {
                return Observable.just("Product with id " + product.getId() + " already exists");
            }
        });
    }
}
