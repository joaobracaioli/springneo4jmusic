package music.spring.data.neo4j;


import music.util.ArrayAdapterFactory;
import music.util.GenereAux;
import music.model.Tracks;
import music.spring.data.neo4j.domain.Artist;
import music.spring.data.neo4j.domain.Genre;
import music.spring.data.neo4j.domain.Group;
import music.spring.data.neo4j.domain.Role;
import music.spring.data.neo4j.domain.Role_AT;
import music.spring.data.neo4j.domain.Track;
import music.spring.data.neo4j.domain.User;
import music.spring.data.neo4j.repositories.UserRepository;
import music.spring.data.neo4j.services.ArtistService;
import music.spring.data.neo4j.services.GenreService;
import music.spring.data.neo4j.services.UserService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseExtractor;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.template.Neo4jOperations;
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
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author mh
 * @since 06.10.14
 */
@Configuration
@Import(MyNeo4jConfiguration.class)
@RestController("/")
public class Application extends WebMvcConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	private static final String API_KEY = "TTJBBYCF2LTQ7RLTL";
	private final AsyncRestTemplate  asyncFactory = new AsyncRestTemplate();
	private final EchoNestAPI echoNest = new EchoNestAPI(API_KEY);;
	
	private Gson gson = new GsonBuilder().registerTypeAdapterFactory(new music.util.ArrayAdapterFactory()).create();

    private JsonParser parser = new JsonParser();


    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }


    @Autowired
    UserService userService;


    @Autowired
    GenreService genreService;

    //METODO PARA TESTE GERAL
    @RequestMapping(path="/createRelacaoArtista")
    public String usuario (String token) throws URISyntaxException, IOException, InterruptedException, ExecutionException{
    	
    	
    	
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

		

    
    	return "ok";
    }
    

    @RequestMapping(path = "/v1/teste2/{token}", method = RequestMethod.POST, consumes="application/json")
    public  ResponseEntity<String>  teste(@RequestBody String caracteristicas, @PathVariable("token") String token) throws URISyntaxException, InterruptedException, ExecutionException {
    	log.debug("entrei aqui");
    	log.info("entrei pra informar ");
    	log.info(token);
    

    	/*
    	AsyncRestTemplate  asyncFactory = new AsyncRestTemplate();
		URI uri = new URI("https://api.spotify.com/v1/me/tracks");
		
		HttpMethod method = HttpMethod.GET;
		//create request entity using HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
		Class<String> responseType = String.class;

		ListenableFuture<ResponseEntity<String>> future = asyncFactory.exchange(uri, method, requestEntity, responseType);
	
		ResponseEntity<String> entity = future.get();
		log.info(entity.getBody());
	
		*/

		//prints body source code for the given URL
		
        conversor(caracteristicas, token);
    	
    	String mgs =  "{\"Usuario criado com success\":1}";
    	
    	return new ResponseEntity<String>  (mgs, HttpStatus.OK);

    }

    private void conversor(String caracteristicas, String token) throws URISyntaxException, InterruptedException, ExecutionException{


    	log.info("=================  entrei para converter ===============");

			JsonArray jArray =  parser.parse(caracteristicas).getAsJsonArray();
			ArrayList<Tracks> listTrack = new ArrayList<Tracks>() ;

			//Cria usuario
			music.model.User u = gson.fromJson(jArray.get(jArray.size()-1), music.model.User.class);

			if(userService.findByEmail(u.getEmail()).isEmpty()){
			log.info("==========  Usuario não existente  ===============");
			// JSON transforma objetos em Truck
		    for(JsonElement obj : jArray){

		    	Tracks tck = gson.fromJson(obj, Tracks.class);
		    	listTrack.add(tck);
		    }


	    	log.info("==========  Terminei de converter ===============");


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

	


		    try {

			    //	String md5 = "07a096fd8880931695723d19b1a11611";
			    	for(Tracks tk : listTrack){
			    		if(tk.getTrack()!= null){
			    		//Cria Artista

			    		log.info("================= Recuperando musica na EchoNest =====================");

			    		String id = tk.getTrack().getUri();
				    	//com.echonest.api.v4.Track trkAnal = echoNest.newTrackByID(id);
			    	
				    	

				    	Artist artNeo4j = new Artist();

				    	artNeo4j.setName(tk.getTrack().getArtists().get(0).getName());
						
				    	//Recuperando dados mais específico do artista no Spotify
				    	URI uriArt = new URI("https://api.spotify.com/v1/artists/"+tk.getTrack().getArtists().get(0).getId());
			    		
			    		HttpMethod method = HttpMethod.GET;
			    		//create request entity using HttpHeaders
			    		HttpHeaders headers = new HttpHeaders();
			    		headers.set("Authorization", "Bearer "+token);
			    		HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
			    		Class<String> responseType = String.class;

			    		ListenableFuture<ResponseEntity<String>> future = asyncFactory.exchange(uriArt, method, requestEntity, responseType);


			    		ResponseEntity<String> entity = future.get();
			    		//prints body source code for the given URL
			    		log.info("parametros do artista"+entity.getBody());
			    		
			    		
			    		music.model.Artist art = gson.fromJson(entity.getBody(), music.model.Artist.class);	
				    	
				    	
				    	//artNeo4j.setFamiliarity(art.getFamiliarity());
			    		artNeo4j.setHotttnesss(art.getPopularity());
			    		artNeo4j.setForeignID(art.getUri());

			    		//Genero EchoNest
			    		 Genre g1;
			    		if(art.getGenres().isEmpty()){
			    		//Se o genero estiver vazio pela spotify pegar pela echoNest
			    	 	//Essa atualização foi necessária apos 28 de abriu, qdo o Spotify começou a fornecer os serviços da EchoNest
			    	    Thread.sleep(5000);
			    		com.echonest.api.v4.Artist artistaEcho = echoNest.newArtistByName(tk.getTrack().getArtists().get(0).getName());
			    		
			    		
			    		
			    		 log.info(" =========  Generos EchoNest ======== ");
					      List<com.echonest.api.v4.Term> term = artistaEcho.getTerms();
					      	
					       for (com.echonest.api.v4.Term t : term){
					    	  

							if(  (g1 = genreService.findByName(t.getName()) )==null ){
					    		  g1 = new Genre();
					    		  g1.setName(t.getName());
							}

					    	   Role r = new Role();

					    	   r.setArtist(artNeo4j);
					       	   r.setAfinidade(t.getFrequency());
					       	   r.setGenere(g1);
					       	   //seta relacionamento
					       	   artNeo4j.setRoles(r);


					       }
			    		}else{
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
			    	
			    	//getTopTrack(token, userNeo4j);

				} catch (EchoNestException e) {
					log.info("==== ERRO ECHONEST ===== "+e.getMessage());

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.info("==== ERRRROOOOO ===== "+e.getMessage());
				}
			    //echoNest.showStats();
			  log.info("================= usuario salvo =====================");
		    	userService.createOrUpdate(userNeo4j);
		    	return;
			}
			 log.info("================= usuario já existente =====================");
    }

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
	    	
		    	

		    	Artist artNeo4j = new Artist();

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

    @RequestMapping("/createArtist")
    public User create(){


    	//create user
    	User u = new User();

    	u.setEmail("joao@joao");
    	u.setName("joao teste");

    	//cria artista Los Hemanos
    	Artist a = new Artist();
    	//a.setFamiliarity("0,639");
    	//a.setHotttnesss("0,600");
    	a.setName("Los Hermanos");

    	//cria artista Paralamas
    	Artist paralamas = new Artist();
    	//paralamas.setFamiliarity("0.800");
    	//paralamas.setHotttnesss("0,70");
    	paralamas.setName("Paralamas do Sucesso");







    	//cria relacionamento dos generos e afinidade
    	Role r = new Role();
    	Role r2 = new Role();
    	Role r3 = new Role();

    	//cria generos

    	Genre g1 = new Genre();
    	g1.setName("Samba");

    	Genre g2 = new Genre();
    	g2.setName("Rock");

    	//seta valor de avinidade e relacionamento
    	//r3.setAfinidade("100");
    	r3.setArtist(paralamas);
    	r3.setGenere(g2);


    	r.setArtist(a);
    	//r.setAfinidade("06");
    	r.setGenere(g1);

    	r2.setArtist(a);
    	r2.setGenere(g2);
    	//r2.setAfinidade("100");

    	//seta relacionamento com artista
    	a.setRoles(r);
    	a.setRoles(r2);
    	paralamas.setRoles(r3);

    	//Cria musica
    	Track t1 = new Track();
    	t1.setTitle("A Flor");
    	t1.setDanceability(0.76222);
    	t1.setEnergy(0.34);
    	t1.setDuration(173.333);



    	Track t2 = new Track();
    	t2.setTitle("Vencedor");
    	t2.setDanceability(0.76929292);
    	t2.setEnergy(0.3241);
    	t2.setDuration(18.89333);
    	t2.setForeign("2");

    	Track t3 = new Track();
    	t3.setTitle("Meu erro");
    	t3.setDanceability(0.7699292);
    	t3.setEnergy(0.30484);
    	t3.setDuration(178.89333);
    	t3.setForeign("3");
    	//seta relacionamento musica artista
    	Role_AT rel_los_hermanos = new Role_AT();
    	rel_los_hermanos.setArtist(a);
    	rel_los_hermanos.setTruck(t1);
    	//rel_los_hermanos.setDate_salve(new java.util.Date());

    	Role_AT rel_los_hermanos2 = new Role_AT();
    	rel_los_hermanos2.setArtist(a);
    	rel_los_hermanos2.setTruck(t2);
    	//rel_los_hermanos2.setDate_salve(new java.util.Date());

    	Role_AT rel_paralamas = new Role_AT();
    	rel_paralamas.setArtist(paralamas);
    	rel_paralamas.setTruck(t3);
    	//rel_paralamas.setDate_salve(new java.util.Date());

    	//atribui relacionamento a musica
    	t1.setRoles(rel_los_hermanos);
    	t2.setRoles(rel_los_hermanos2);
    	t3.setRoles(rel_paralamas);

    	//seta musica para usuarios
    	u.setTrucks(t2);
    	u.setTrucks(t1);
    	u.setTrucks(t3);


    	//return artistService.createOrUpdate(a);
    	return userService.createOrUpdate(u);

    }

}
