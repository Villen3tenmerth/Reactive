package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Currency;
import model.User;
import mongodb.MongoDBProvider;
import repository.UserRepository;
import rx.Observable;

public class AddUserHandler implements Handler {
    private final UserRepository userRepository;

    public AddUserHandler(MongoDBProvider provider) {
        userRepository = new UserRepository(provider);
    }

    private User getUserFromURI(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("id") || !params.containsKey("name") || !params.containsKey("currency")) {
            throw new RuntimeException("All user fields required");
        }
        int id;
        String name = params.get("name").get(0);
        Currency currency;
        try {
            currency = Currency.valueOf(params.get("currency").get(0));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown currency:" + params.get("currency").get(0));
        }
        try {
            id = Integer.parseInt(params.get("id").get(0));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Id must be an integer");
        }
        return new User(id, name, currency);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        User user;
        try {
            user = getUserFromURI(req);
        } catch (RuntimeException e) {
            return Observable.just("Error occurred while adding user: " + e.getMessage());
        }
        return userRepository.saveUser(user);
    }
}
