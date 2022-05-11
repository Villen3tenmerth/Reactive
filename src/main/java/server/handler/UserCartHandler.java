package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.CartRepostiroy;
import rx.Observable;

public class UserCartHandler implements Handler {
    private final CartRepostiroy cartRepostiroy;

    public UserCartHandler(MongoDBProvider provider) {
        cartRepostiroy = new CartRepostiroy(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("userId")) {
            return Observable.just("Parameter userId is required");
        }
        int userId;
        try {
            userId = Integer.parseInt(params.get("userId").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("Not a valid id: " + e.getMessage());
        }
        return cartRepostiroy.getCartByUser(userId);
    }
}
