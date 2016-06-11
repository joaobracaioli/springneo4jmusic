package music.spring.data.neo4j.repositories;

import music.spring.data.neo4j.domain.Artist;
import music.spring.data.neo4j.domain.Genre;

import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends GraphRepository<Artist>{

	/*
	MATCH (gr:Group)-[:IN_THE_SOME]->(u:User)-[]-()-[]->(a:Artist)<-[:OF_TYPE]-(g:Genre) where gr.name="Buraqueira"
	with u,a,count(*) as numberOfScrobbles
	with u, collect(a) as artists, collect(numberOfScrobbles) as counts
	with u, artists, reduce(x=[0,0], idx in range(0,size(counts)-1) | case when counts[idx] > x[1] then [idx,counts[idx]] else x end)[0] as index
	return u, artists[index]
	*/
	Artist findByName(String name);
	
	@Query(" MATCH (gr:Group)-[:IN_THE_SOME]->(u:User)-[]-(tr:Track)-[]->(a:Artist)<-[:OF_TYPE]-(g:Genre) "
			+ " WITH u,a, gr,count(a) as rels, collect(a) as Artist"
			+ " WHERE rels > 2 and gr.name={name} "
			+ " RETURN a,rels order by  rels desc")
	Iterable<Map<Artist, Integer>> findByRelArtista(@Param("name") String nome);
	
}
