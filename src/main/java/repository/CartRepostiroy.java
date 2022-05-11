package repository;

import model.CartItem;
import model.User;
import mongodb.MongoDBProvider;
import rx.Observable;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class CartRepostiroy {
    private final static String dbTableName = "carts";
    private final MongoDBProvider provider;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartRepostiroy(MongoDBProvider provider) {
        this.provider = provider;
        this.userRepository = new UserRepository(provider);
        this.productRepository = new ProductRepository(provider);
    }

    public Observable<String> getCartByUser(int userId) {
        Observable<User> user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("User with id " + userId + " doesn't exist");
            } else {
                return provider.getDatabase()
                        .getCollection(dbTableName)
                        .find(eq("userId", userId))
                        .toObservable()
                        .map(CartItem::new)
                        .map(CartItem::toString);
            }
        });
    }

    public Observable<String> addToCart(int userId, int productId) {
        Observable<User> user = userRepository.getUserById(userId);
        Observable<String> product = productRepository.getProductById(productId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("User with id " + userId + " doesn't exist");
            } else {
                return product.isEmpty().flatMap(productNotFound -> {
                    if (productNotFound) {
                        return Observable.just("Product with id " + productId + " doesn't exist");
                    } else {
                        return provider.getDatabase()
                                .getCollection(dbTableName)
                                .insertOne(new CartItem(userId, productId).toDocument())
                                .map(suc -> "Product with id " + productId + " was added to cart of user with id " + userId);
                    }
                });
            }
        });
    }

    public Observable<String> deleteFromCart(int userId, int productId) {
        Observable<User> user = userRepository.getUserById(userId);
        Observable<String> product = productRepository.getProductById(productId);
        return user.isEmpty().flatMap(userNotFound -> {
            if (userNotFound) {
                return Observable.just("User with id " + userId + " doesn't exist");
            } else {
                return product.isEmpty().flatMap(productNotFound -> {
                    if (productNotFound) {
                        return Observable.just("Product with id " + productId + " doesn't exist");
                    } else {
                        return provider.getDatabase()
                                .getCollection(dbTableName)
                                .deleteOne(and(eq("userId", userId), eq("productId", productId)))
                                .map(deleteResult -> {
                                    if (deleteResult.getDeletedCount() == 0) {
                                        return "Item is not in the cart";
                                    } else {
                                        return "Itam " + productId + " was successfully deleted";
                                    }
                                });
                    }
                });
            }
        });
    }

    public Observable<String> clearCart(int userId) {
        Observable<User> user = userRepository.getUserById(userId);
        return user.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("User with id " + userId + " doesn't exist");
            } else {
                return provider.getDatabase()
                        .getCollection(dbTableName)
                        .deleteMany(eq("userId", userId))
                        .map(deleteResult -> "Deleted " + deleteResult.getDeletedCount() + " items");
            }
        });
    }
}
