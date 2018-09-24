package xmacedo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import xmacedo.model.Reuniao;

public interface ReuniaoRepository extends MongoRepository<Reuniao, String> {
}
