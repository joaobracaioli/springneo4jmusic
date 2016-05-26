package music.spring.data.neo4j.repositories;

import music.spring.data.neo4j.domain.Artist;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends GraphRepository<Artist>{

	Artist findByName(String name);
	
}
