package music.spring.data.neo4j.services;

import music.spring.data.neo4j.domain.Artist;
import music.spring.data.neo4j.domain.Genre;
import music.spring.data.neo4j.repositories.ArtistRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service("artistiService")
public class ArtistService extends GenericService<Artist> implements ArtistInterfaceService {

	@Autowired
	private ArtistRepository artistRepository;
	
	@Override
	public GraphRepository<Artist> getRepository() {
	
		return artistRepository;
	}
	
	public Artist  findByName(String name){
		
		return artistRepository.findByName(name);
	}
	
	public Iterable<Map<Artist, Integer>> findByRelArtista(String nome){
		return artistRepository.findByRelArtista(nome);
	}
}
