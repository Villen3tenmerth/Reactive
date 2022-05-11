package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import model.Product;
import mongodb.MongoDBProvider;
import repository.ProductRepository;
import rx.Observable;

public class AddProductHandler implements Handler {
    private final ProductRepository productRepository;

    public AddProductHandler(MongoDBProvider provider) {
        productRepository = new ProductRepository(provider);
    }

    private Product getProductFromURI(HttpServerRequest<ByteBuf> req) {
        var params = req.getQueryParameters();
        if (!params.containsKey("id") || !params.containsKey("name")
            || !params.containsKey("description") || !params.containsKey("price")) {
            throw new RuntimeException("All product fields are required");
        }
        int id;
        String name = params.get("name").get(0);
        String description = params.get("description").get(0);
        double price;
        try {
            id = Integer.parseInt(params.get("id").get(0));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Id must be an integer");
        }
        try {
            price = Double.parseDouble(params.get("price").get(0));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Price must be a number");
        }
        return new Product(id, name, description, price);
    }

    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        Product product;
        try {
            product = getProductFromURI(req);
        } catch (RuntimeException e) {
            return Observable.just("Error occurred while adding product: " + e.getMessage());
        }
        return productRepository.saveProduct(product);
    }
}
