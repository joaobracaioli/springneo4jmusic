package music.spring.data.neo4j.controller;


import java.util.List;
import java.util.Map;
import java.util.Set;

import music.spring.data.neo4j.domain.Artist;
import music.spring.data.neo4j.domain.Genre;
import music.spring.data.neo4j.domain.Group;

import music.spring.data.neo4j.domain.User;
import music.spring.data.neo4j.controller.Controller;
import music.spring.data.neo4j.services.ArtistService;
import music.spring.data.neo4j.services.GenreService;
import music.spring.data.neo4j.services.GroupService;
import music.spring.data.neo4j.services.Service;
import music.spring.data.neo4j.services.UserService;
import music.util.GroupGerate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.google.common.collect.Lists;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@CrossOrigin
@JsonIdentityInfo(generator=JSOGGenerator.class)
@RestController
@RequestMapping(value = "/v1/group")
public class GroupController extends Controller<Group>{
	
	private static final Logger log = LoggerFactory.getLogger(GroupController.class);
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private ArtistService artService;
		
	@Override
	public Service<Group> getService() {
		return groupService;
	}
	
	@CrossOrigin
	@RequestMapping(value="/groupCreate/{idUser}",method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Group> groupCreate(@PathVariable("idUser") String idUser, @RequestBody GroupGerate p){
		
		log.info("============= Criar Group ========================");
		Group group =  new Group();
		User userAdd = userService.findByIdSpotify(idUser);
		if (userAdd ==null) {
	           log.info("Group or User with id  / "+idUser+"not found");
	            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
	        }
		group.setOwner(userAdd.getId_spotify());
		group.setMembers(userAdd);
		group.setName(p.getName());
		group.setAtivo(true);
		group.setCaracteristicas(p.getCaracteristicas());
		groupService.createOrUpdate(group);
		return new ResponseEntity<Group>(group, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/getParticipants/{idGroup}",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Set<User>> getParticipants(@PathVariable("idGroup") Long idGroup){
		
		
		Group group = groupService.find(idGroup);
		if (group==null) {
	           log.info("Group or User with id " + idGroup + "not found");
	            return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
	        }
		
		
		return new ResponseEntity<Set<User>>( group.getMembers(), HttpStatus.OK);
	}

	@CrossOrigin
	@RequestMapping(value="/getGenreGroup/{idGroup}",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Iterable<Map<Genre, Integer>>> getGenreGroup(@PathVariable("idGroup") Long idGroup){
		
		
		Group group = groupService.find(idGroup);
		if (group==null) {
	           log.info("Group or User with id " + idGroup + "not found");
	            return new ResponseEntity<Iterable<Map<Genre, Integer>>>(HttpStatus.NOT_FOUND);
	        }
		Iterable<Map<Genre, Integer>>  genres = genreService.findByNameGroup(group.getName());
		
		return new ResponseEntity<Iterable<Map<Genre, Integer>>>( genres, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/getArtistaGroup/{idGroup}",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Iterable<Map<Artist, Integer>>> getArtsRel(@PathVariable("idGroup") Long idGroup){
		
		
		Group group = groupService.find(idGroup);
		if (group==null) {
	           log.info("Group or User with id " + idGroup + "not found");
	            return new ResponseEntity<Iterable<Map<Artist, Integer>>>(HttpStatus.NOT_FOUND);
	        }
		Iterable<Map<Artist, Integer>>  art = artService.findByRelArtista(group.getName());
		
		return new ResponseEntity<Iterable<Map<Artist, Integer>>>( art, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/getContParticipants/{idGroup}",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Integer> getContParticipants(@PathVariable("idGroup") Long idGroup){
		
		
		Group group = groupService.find(idGroup);
		if (group==null) {
	           log.info("Group or User with id " + idGroup + "not found");
	            return new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
	        }
		
		
		return new ResponseEntity<Integer>( group.getMembers().size(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/getSizeGroup",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Integer> getSizeGroup(){
		
			
		return new ResponseEntity<Integer>( Lists.newArrayList(groupService.findAll()).size(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/add/{idGroup}/{idUser}",method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Group> addGroup(@PathVariable("idGroup") Long idGroup, @PathVariable("idUser") String id_spotify) {
		
		Group group = groupService.find(idGroup);
		User userAdd = userService.findByIdSpotify(id_spotify);
		if (group==null || userAdd ==null) {
	           log.info("Group or User with id " + idGroup + " / "+userAdd+"not found");
	            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
	        }
		
		if(group.getMembers().contains(userAdd)){
			
			log.info("User já pertence ao grupo");
            return new ResponseEntity<Group>(HttpStatus.OK);
		}
		
		group.setMembers(userAdd);

		groupService.createOrUpdate(group);
		return new ResponseEntity<Group>(group, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/removeUser/{idGroup}/{idUser}",method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity<Group> removeUser(@PathVariable("idGroup") Long idGroup, @PathVariable("idUser") String id_spotify){
		
		Group group = groupService.find(idGroup);
		User userAdd = userService.findByIdSpotify(id_spotify);
		if (group==null || userAdd ==null) {
	           log.info("Group or User with id " + idGroup + " / "+userAdd+"not found");
	            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
	        }
		group.getMembers().remove(userAdd);
			
		groupService.createOrUpdate(group);
		
		if(group.getMembers().size() ==0){
			groupService.delete(group.getId());
		}
		if(userAdd.getId_spotify().equals(group.getOwner())){
			group.setAtivo(false);
			groupService.createOrUpdate(group);
		}
		
		return new ResponseEntity<Group>(group, HttpStatus.OK);
	}
/*
	@RequestMapping(value="/genero/{idGroup}",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Group> getGenero(@PathVariable("idGroup") Long idGroup) throws EchoNestException{
		
		Group groupAux = new Group();
		Group group = groupService.find(idGroup);

		
		if (group==null ) {
	           log.info("Não achei o GRUUUUPO "+idGroup+"not found");
	            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
	     }
		
		log.info("Nommmme:   "+group.getName());
		
		groupAux.setName(group.getName());
		groupAux.setCaracteristicas(group.getCaracteristicas());
		groupAux.setMembers(group.getMembers());
		
		
		List<Genre> genres = genreService.findByNameGroup(group.getName());

		groupAux.addGenres(genres);
		
		
		PlaylistParams params = new PlaylistParams();
		params.addIDSpace("spotify-WW");
		params.setType(PlaylistParams.PlaylistType.GENRE_RADIO);
		
		for(Genre g : genres){
	  
		    if(g.getName() != null || !g.getName().isEmpty()){
		    	params.addGenre(g.getName());
		    	log.info("Adicionando Generos ------ :"+g.getName());
		    	}
		}
		  params.addIDSpace("id:spotify-WW");
		  params.setMinEnergy(.6f);
		  params.setMinDanceability(.6f);
          params.includeAudioSummary();
		  params.includeTracks();
		  params.setResults(10);

		  //params.setLimit(true);
		  echoNest.setTraceSends(true);
		  
		  Playlist playlist = echoNest.createStaticPlaylist(params);
		  try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  for ( Song song : playlist.getSongs()) {
			  log.info(song.getTitle());
			  log.info(song.getArtistName());
			  com.echonest.api.v4.Track track = song.getTrack("spotify-WW");
			  group.setTracks(track);
			 // log.info(track.getForeignID() + " " + song.getTitle() + " by " + song.getArtistName());
		  }
		  
		  
		groupService.createOrUpdate(groupAux);
		return new ResponseEntity<Group>(groupAux, HttpStatus.OK);
	}
*/


}
