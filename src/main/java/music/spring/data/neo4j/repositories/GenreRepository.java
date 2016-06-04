package music.spring.data.neo4j.repositories;

import java.util.List;
import java.util.Map;
import java.util.Set;

import music.spring.data.neo4j.domain.Genre;
import music.util.GenereAux;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends GraphRepository<Genre>{

	Genre findByName(String name);
	
	//trabalhar futuramente nessa query 
	/* MATCH (gr:Group)-[:IN_THE_SOME]->(u:User)-[]-()-[]->(a:Artist)<-[:OF_TYPE]-(g:Genre) where gr.name={name}
	 * MATCH (gr:Group) where gr.name ="Buraqueira"
		MATCH (gr:Group)-[:IN_THE_SOME]->(u:User)-[]-()-[]->(a:Artist)<-[:OF_TYPE]-(g:Genre) where gr.name="Buraqueira"
		WITH u, g,count(a) as rels, collect(a) as artists
		WHERE rels > 7
		RETURN g,rels order by  rels desc*/
	@Query(" MATCH (gr:Group) where gr.name ={name}"
			+ " MATCH (gr:Group)-[:IN_THE_SOME]->(u:User)-[]-()-[]->(a:Artist)<-[:OF_TYPE]-(g:Genre)"
			+ " WITH gr, g,count(a) as rels, collect(a) as artists"
			+ " WHERE gr.name={name} and rels >2"
			+ " RETURN g, rels order by rels desc")
	Iterable<Map<Genre, Integer>> findBy(@Param("name") String nome);
}
