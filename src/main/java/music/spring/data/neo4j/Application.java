package music.spring.data.neo4j;

import music.model.Tracks;
import music.spring.data.neo4j.domain.Artist;
import music.spring.data.neo4j.domain.Genre;
import music.spring.data.neo4j.domain.Role;
import music.spring.data.neo4j.domain.Role_AT;
import music.spring.data.neo4j.domain.Track;
import music.spring.data.neo4j.domain.User;
import music.spring.data.neo4j.repositories.UserRepository;
import music.spring.data.neo4j.services.ArtistService;
import music.spring.data.neo4j.services.GenreService;
import music.spring.data.neo4j.services.TrackService;
import music.spring.data.neo4j.services.UserService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.concurrent.ListenableFuture;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author mh
 * @since 06.10.14
 */
@CrossOrigin
@Configuration
@Import(MyNeo4jConfiguration.class)
@RestController("/")
public class Application extends WebMvcConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	//private static final String API_KEY = "TTJBBYCF2LTQ7RLTL";
	private final AsyncRestTemplate  asyncFactory = new AsyncRestTemplate();
	//private final EchoNestAPI echoNest = new EchoNestAPI(API_KEY);
	
	private Gson gson = new GsonBuilder().registerTypeAdapterFactory(new music.util.ArrayAdapterFactory()).create();

    private JsonParser parser = new JsonParser();


    public static void main(String[] args) throws EchoNestException, IOException {

        SpringApplication.run(Application.class, args);
    }


    @Autowired
    UserService userService;

    @Autowired
    ArtistService artistService; 

    @Autowired
    GenreService genreService;
    
    @Autowired
    TrackService trackService;

    
    @CrossOrigin
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public  String index() {
    	return "index.html";
    }

    @CrossOrigin
    @RequestMapping(path = "/v1/teste2/{token}", method = RequestMethod.POST, consumes="application/json")
    public  ResponseEntity<String>  teste(@RequestBody String caracteristicas, @PathVariable("token") String token) 
    			throws URISyntaxException, InterruptedException, ExecutionException , EchoNestException{
    	
    	log.info("entrei pra informar ");
    	
    	//metodo para converter criar usuario
		
        conversor(caracteristicas, token);
    	
    	String mgs =  "{\"Usuario criado com success\":1}";
    	
    	return new ResponseEntity<String>  (mgs, HttpStatus.OK);

    }

    private synchronized void conversor(String caracteristicas, String token) throws URISyntaxException, InterruptedException, ExecutionException{
 

    	log.info("=================  entrei para converter ===============");

			JsonArray jArray =  parser.parse(caracteristicas).getAsJsonArray();
			ArrayList<Tracks> listTrack = new ArrayList<Tracks>() ;
			ArrayList<Tracks>  listTop = new ArrayList<>();

			//Cria usuario
			music.model.User u = gson.fromJson(jArray.get(jArray.size()-1), music.model.User.class);

			if(userService.findByEmail(u.getEmail()).isEmpty()){
			log.info("==========  Usuario não existente  ===============");
			// JSON transforma objetos em Truck
		    for(JsonElement obj : jArray){

		    	Tracks tck = gson.fromJson(obj, Tracks.class);
		    	listTrack.add(tck);
		    }


	    	log.info("================= ECHONEST / NEO4J =====================");

	        log.info(u.toString());

			//Cria usuario para base de dados
	    	User userNeo4j = new User();
	    	userNeo4j.setName(u.getDisplayName());


			if(u.getDisplayName() == null || u.getDisplayName().isEmpty()){
				userNeo4j.setName(u.getId());
			}

			userNeo4j.setName(u.getDisplayName());
			userNeo4j.setEmail(u.getEmail());
			userNeo4j.setId_spotify(u.getId());
			userNeo4j.setUri(u.getUri());
			
			userService.createOrUpdate(userNeo4j);
	
			criaRelacionamento(listTrack, token, userNeo4j);
			
			
			
			log.info("relacionamento com as musicas salvar criada!");
			
			URI uri = new URI("https://api.spotify.com/v1/me/tracks");
			
			HttpMethod method = HttpMethod.GET;
			//create request entity using HttpHeaders
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+token);
			HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
			Class<String> responseType = String.class;

			ListenableFuture<ResponseEntity<String>> future = asyncFactory.exchange(uri, method, requestEntity, responseType);


			ResponseEntity<String> entity = future.get();
			//prints body source code for the given URL
			log.info(entity.getBody());

			JsonObject jObj =  parser.parse(entity.getBody()).getAsJsonObject();
			JsonArray tranks = jObj.getAsJsonArray("items");
			
				
			  for(JsonElement obj : tranks){

			    	Tracks tck = gson.fromJson(obj, Tracks.class);
			    	listTop.add(tck);
					
	    	}

			log.info("criando relacionamento com as top musicas ouvidas!");
			
		    criaRelacionamento(listTop, token, userNeo4j);
			  
		  
			
		    
			  	//getTopTrack(token, userNeo4j);

						    //echoNest.showStats();
			  log.info("================= usuario salvo =====================");
			  
			 
			}else{
			 log.info("================= usuario já existente =====================");
			}
    }

	private void criaRelacionamento (List<Tracks> listTrack, String token, User userNeo4j) throws URISyntaxException, ExecutionException{
		
			HttpMethod method = HttpMethod.GET;
			//create request entity using HttpHeaders
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+token);
			HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
			Class<String> responseType = String.class;

		    //	String md5 = "07a096fd8880931695723d19b1a11611";
		    	for(Tracks tk : listTrack){
		    		Track tkNeo4j;
		    		
		    		 
		    	
		    		
		    		try {
				    		if(tk.getTrack()!= null){
				    		//Cria Artista
		
				    			
				    			
				    		if((tkNeo4j = trackService.findbyTitle(tk.getTrack().getName())) ==null){
				    			tkNeo4j = new Track();
				    			
				    		
	
				    		log.info("================= Recuperando musica na EchoNest =====================");
		
				    		String id = tk.getTrack().getUri();
					    	//com.echonest.api.v4.Track trkAnal = echoNest.newTrackByID(id);
				    	
				    		Artist artNeo4j;
				    		//Criando artista, se já existir mantem oq está na base
				    		if( (artNeo4j = artistService.findByName(tk.getTrack().getArtists().get(0).getName())) ==null){
				    			
				    			artNeo4j = new Artist();
				    			artNeo4j.setName(tk.getTrack().getArtists().get(0).getName());
				    		
					    			
					    					    	
							
					    	//Recuperando dados mais específico do artista no Spotify
					    	URI uriArt = new URI("https://api.spotify.com/v1/artists/"+tk.getTrack().getArtists().get(0).getId());
				    		

		
				    		ListenableFuture<ResponseEntity<String>> future = asyncFactory.exchange(uriArt, method, requestEntity, responseType);
		
		
				    		ResponseEntity<String> entity = future.get();
				    		//prints body source code for the given URL
				    		log.info("parametros do artista"+entity.getBody());
				    		
				    		
				    		music.model.Artist art = gson.fromJson(entity.getBody(), music.model.Artist.class);	
					    	
					    	
					    	//artNeo4j.setFamiliarity(art.getFamiliarity());
				    		artNeo4j.setHotttnesss(art.getPopularity());
				    		artNeo4j.setForeignID(art.getUri());
		
				    		
				    		//Se o genero estiver vazio pela spotify pegar pela echoNest
				    	 	//Essa atualização foi necessária apos 28 de abriu, qdo o Spotify começou a fornecer os serviços da EchoNest
				    	    //Thread.sleep(5000);
				    		//com.echonest.api.v4.Artist artistaEcho = echoNest.newArtistByName(tk.getTrack().getArtists().get(0).getName());
				    		
				    		if(art.getGenres().isEmpty()){
				    		
				    		 log.info(" =========  Generos EchoNest ======== ");
						     List<String> term = art.getGenres();
						      	
							       for (String  gen : term){
							    	 //Genero EchoNest
							    	Genre g1;
									if(  (g1 = genreService.findByName(gen) )==null ){
							    		  g1 = new Genre();
							    		  g1.setName(gen);
									}
		
							    	   Role r = new Role();
		
							    	   r.setArtist(artNeo4j);
							       	   r.setAfinidade(1.0);
							       	   r.setGenere(g1);
							       	   //seta relacionamento
							       	   artNeo4j.setRoles(r);
		
		
							       }
		
				    	
				    		
				    		
				    		}
						     //truck
						       log.info("================= Criando Musica =====================");
				    	
				    		
				    		URI uri = new URI("https://api.spotify.com/v1/audio-features/"+tk.getTrack().getId());
				    		
				    	    ListenableFuture<ResponseEntity<String>> futureMusic = asyncFactory.exchange(uri, method, requestEntity, responseType);
		
		
				    		ResponseEntity<String> entityMusic = futureMusic.get();
				    		//prints body source code for the given URL
				    		log.info("parametros da musica"+entityMusic.getBody());
				    		
				    		
				    		music.model.Trackfeatures tfeatures = gson.fromJson(entityMusic.getBody(), music.model.Trackfeatures.class);
		
				    		
				    		tkNeo4j.setTitle(tk.getTrack().getName());
				    		tkNeo4j.setDanceability(tfeatures.getDanceability());
				    		tkNeo4j.setDuration(tfeatures.getDuration());
				    		tkNeo4j.setEnergy(tfeatures.getEnergy());
				    		tkNeo4j.setForeign(tk.getTrack().getUri());
				    		tkNeo4j.setLiveness(tfeatures.getLiveness());
				    		tkNeo4j.setLoudness(tfeatures.getLoudness());
				    		tkNeo4j.setPopularity(tk.getTrack().getPopularity());
				    		tkNeo4j.setSpeechiness(tfeatures.getSpeechiness());
		
				    		//relaciona Musica com artista
				    		Role_AT rel_music_art = new Role_AT();
				    		rel_music_art.setArtist(artNeo4j);
				    		rel_music_art.setTruck(tkNeo4j);
				    		rel_music_art.setDate_salve(tk.getAdded_at());
		
				    		//seta relacionamento
				    		tkNeo4j.setRoles(rel_music_art);
		
				    		//trackService.createOrUpdate(tkNeo4j);
				    		
				    		}
				    		
							userNeo4j.setTrucks(tkNeo4j);
				    		
							userService.createOrUpdate(userNeo4j);
				    		}
				    		log.info("================= Informações coletadas =====================");
		
				    	
		
				    		
		
				    		}
				    		
		    	   	} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								log.info("==== ERRRROOOOO ===== "+e.getMessage());
					}

		    		
		    		

		    	}	
		    	
		    	
		    	
	    
	    
    	
	        	
	}
/*
    private void getTopTrack(String token, User userNeo4j) throws URISyntaxException, InterruptedException, ExecutionException{
    	
        URI uriTraks = new URI("https://api.spotify.com/v1/me/tracks");
		
		HttpMethod method = HttpMethod.GET;
		//create request entity using HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
		Class<String> responseType = String.class;

		ListenableFuture<ResponseEntity<String>> future = asyncFactory.exchange(uriTraks, method, requestEntity, responseType);


		ResponseEntity<String> entity = future.get();
		//prints body source code for the given URL
		log.info(entity.getBody());
		
		
		log.info("=================  entrei para converter ===============");

		JsonArray jArray =  parser.parse(entity.getBody()).getAsJsonArray();
		ArrayList<Tracks> listTrack = new ArrayList<Tracks>();
		
		 for(JsonElement obj : jArray){

		    	Tracks tck = gson.fromJson(obj, Tracks.class);
		    	listTrack.add(tck);
		  }
		 
	    	for(Tracks tk : listTrack){
	    		if(tk.getTrack()!= null){
	    		//Cria Artista

	    		log.info("================= Recuperando musica na EchoNest =====================");

	    		String id = tk.getTrack().getUri();
		    	//com.echonest.api.v4.Track trkAnal = echoNest.newTrackByID(id);
	    	
		    	
	    		
		    	Artist artNeo4j;
		    	
		    	artNeo4j 
		    			
		    			
		    			new Artist();

		    	artNeo4j.setName(tk.getTrack().getArtists().get(0).getName());
				
		    	//Recuperando dados mais específico do artista no Spotify
		    	URI uriArt = new URI("https://api.spotify.com/v1/artists/"+tk.getTrack().getArtists().get(0).getId());
	    		
	    		HttpMethod methodTop = HttpMethod.GET;
	    		//create request entity using HttpHeaders
	    		HttpHeaders headersTop = new HttpHeaders();
	    		headers.set("Authorization", "Bearer "+token);
	    		HttpEntity<String> requestEntityTop = new HttpEntity<String>("params", headersTop);
	    		Class<String> responseTypeTop = String.class;

	    		ListenableFuture<ResponseEntity<String>> futureTop = asyncFactory.exchange(uriArt, methodTop, requestEntityTop, responseTypeTop);


	    		ResponseEntity<String> entityTop = futureTop.get();
	    		//prints body source code for the given URL
	    		log.info("parametros do artista"+entityTop.getBody());
	    		
	    		
	    		music.model.Artist art = gson.fromJson(entityTop.getBody(), music.model.Artist.class);	
		    	
		    	
		    	//artNeo4j.setFamiliarity(art.getFamiliarity());
	    		artNeo4j.setHotttnesss(art.getPopularity());
	    		artNeo4j.setForeignID(art.getUri());

	    		//Genero EchoNest
	    		 Genre g1;
	    		 log.info(" =========  Generos Spotify ======== ");
	    			 
	    			 for(String gen : art.getGenres()){
	    				 if(  (g1 = genreService.findByName(gen) )==null ){
				    		  g1 = new Genre();
				    		  g1.setName(gen);
						}

				    	   Role r = new Role();

				    	   r.setArtist(artNeo4j);
				       	   r.setAfinidade(1.0);
				       	   r.setGenere(g1);
				       	   //seta relacionamento
				       	   artNeo4j.setRoles(r);
	    				 
	    			 }
	    			
	    		
	    		
	    		

			     //truck
			       log.info("================= Criando Musica =====================");
			       
			    
	    		Track tkNeo4j = new Track();
	    		
	    		
	    		URI uri = new URI("https://api.spotify.com/v1/audio-features/"+tk.getTrack().getId());
	    		
	    	    ListenableFuture<ResponseEntity<String>> futureMusic = asyncFactory.exchange(uri, method, requestEntity, responseType);


	    		ResponseEntity<String> entityMusic = future.get();
	    		//prints body source code for the given URL
	    		log.info("parametros da musica"+entityMusic.getBody());
	    		
	    		
	    		music.model.Trackfeatures tfeatures = gson.fromJson(entityMusic.getBody(), music.model.Trackfeatures.class);

	    		
	    		tkNeo4j.setTitle(tk.getTrack().getName());
	    		tkNeo4j.setDanceability(tfeatures.getDanceability());
	    		tkNeo4j.setDuration(tfeatures.getDuration());
	    		tkNeo4j.setEnergy(tfeatures.getEnergy());
	    		tkNeo4j.setForeign(tk.getTrack().getUri());
	    		tkNeo4j.setLiveness(tfeatures.getLiveness());
	    		tkNeo4j.setLoudness(tfeatures.getLoudness());
	    		tkNeo4j.setPopularity(tk.getTrack().getPopularity());
	    		tkNeo4j.setSpeechiness(tfeatures.getSpeechiness());

	    		//relaciona Musica com artista
	    		Role_AT rel_music_art = new Role_AT();
	    		rel_music_art.setArtist(artNeo4j);
	    		rel_music_art.setTruck(tkNeo4j);
	    		rel_music_art.setDate_salve(tk.getAdded_at());

	    		//seta relacionamento
	    		tkNeo4j.setRoles(rel_music_art);

	    		userNeo4j.setTrucks(tkNeo4j);

	    		log.info("================= Informações coletadas =====================");

	    		

	    		}


	    	}

    }
*/

}
