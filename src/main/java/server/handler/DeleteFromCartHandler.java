package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.CartRepostiroy;
import rx.Observable;

public class DeleteFromCartHandler implements Handler {
    private final CartRepostiroy cartRepostiroy;

    public DeleteFromCartHandler(MongoDBProvider provider) {
        cartRepostiroy = new CartRepostiroy(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("userId") || !params.containsKey("productId")) {
            return Observable.just("Both userId and productId are required");
        }
        int userId, productId;
        try {
            userId = Integer.parseInt(params.get("userId").get(0));
            productId = Integer.parseInt(params.get("productId").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("Not a valid id: " + e.getMessage());
        }
        return cartRepostiroy.deleteFromCart(userId, productId);
    }
}
