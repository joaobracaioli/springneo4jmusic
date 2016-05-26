package music.spring.data.neo4j.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;


@NodeEntity
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Group {
	
	@JsonProperty("id")
	@GraphId
	private Long id;
	private String name;
	private List<Object> caracteristicas;
	
	@Relationship(type = "IN_THE_SOME")
	private Set<User> members;
	
	private List<com.echonest.api.v4.Track> tracks;

	private List<Genre> genres;
	
	public Group() {
		
		this.members = new HashSet<>();
		this.genres = new LinkedList<>();
		this.caracteristicas = new ArrayList<Object>();
		this.tracks = new LinkedList<>();
	}
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<com.echonest.api.v4.Track> getTracks() {
		return tracks;
	}



	public void setTracks(com.echonest.api.v4.Track track) {
		this.tracks.add(track);
	}



	public void setMembers(Set<User> members) {
		this.members = members;
	}



	public List<Genre> getGenres() {
		return genres;
	}



	public void addGenres(List<Genre> genres) {
		this.genres = genres;
	}



	public List<Object> getCaracteristicas() {
		return caracteristicas;
	}



	public void setCaracteristicas(List<Object> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}


	public Set<User> getMembers() {
		return members;
	}

	public void setMembers(User member) {
		this.members.add(member);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", caracteristicas="
				+ caracteristicas + ", members=" + members + "]";
	}



	
	
}
