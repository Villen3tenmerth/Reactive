package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.ProductRepository;
import rx.Observable;

public class ProductsHandler implements Handler {
    private final ProductRepository productRepository;

    public ProductsHandler(MongoDBProvider provider) {
        productRepository = new ProductRepository(provider);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("id")) {
            return Observable.just("User id is required");
        }
        int userId;
        try {
            userId = Integer.parseInt(params.get("id").get(0));
        } catch (NumberFormatException e) {
            return Observable.just("Id must be an integer");
        }
        return productRepository.getAllByUserId(userId);
    }
}
