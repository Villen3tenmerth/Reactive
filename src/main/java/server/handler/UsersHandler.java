package server.handler;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongodb.MongoDBProvider;
import repository.UserRepository;
import rx.Observable;

public class UsersHandler implements Handler {
    private final UserRepository userRepository;

    public UsersHandler(MongoDBProvider provider) {
        userRepository = new UserRepository(provider);
    }


    @Override
    public Observable<String> handle(HttpServerRequest<ByteBuf> req) {
        return userRepository.getAllUsers();
    }
}
