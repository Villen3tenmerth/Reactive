package server;

import io.reactivex.netty.protocol.http.server.HttpServer;
import mongodb.MongoDBProvider;
import rx.Observable;
import server.handler.*;

import java.util.Map;

public class Server {
    private static Map<String, Handler> MAPPING;

    public static void main(String[] args) {
        MongoDBProvider provider = new MongoDBProvider("shop", 27017);
        MAPPING = Map.of("/addUser", new AddUserHandler(provider),
                "/addProduct", new AddProductHandler(provider),
                "/addToCart", new AddToCartHandler(provider),
                "/clearCart", new ClearCartHandler(provider),
                "/deleteFromCart", new DeleteFromCartHandler(provider),
                "/products", new ProductsHandler(provider),
                "/userCart", new UserCartHandler(provider),
                "/user", new UserHandler(provider),
                "/users", new UsersHandler(provider));

        HttpServer.newServer(8080).start((req, resp) -> {
            String path = req.getDecodedPath();
            Observable<String> res;
            if (MAPPING.containsKey(path)) {
                res = MAPPING.get(path).handle(req);
            } else {
                res = Observable.just("Unknown request: " + path);
            }
            return resp.writeString(res);
        }).awaitShutdown();
    }
}
