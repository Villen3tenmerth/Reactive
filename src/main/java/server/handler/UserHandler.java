package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.User;
import mongodb.MongoDBProvider;
import repository.UserRepository;
import rx.Observable;

public class UserHandler implements Handler {
    private final UserRepository userRepository;

    public UserHandler(MongoDBProvider provider) {
        this.userRepository = new UserRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("id")) {
            return Observable.just("Not found parameter: id");
        }
        int id;
        try {
            id = Integer.parseInt(params.get("id").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("Id must be an integer");
        }
        var user = userRepository.getUserById(id).map(User::toString);
        return user.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("User with id " + id + " doesn't exist");
            } else {
                return user;
            }
        });
    }
}
