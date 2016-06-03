package music.spring.data.neo4j.repositories;

import music.spring.data.neo4j.domain.Artist;

import org.springframework.data.neo4j.repository.GraphRepository;
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
	
}
