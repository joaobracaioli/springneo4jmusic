package music.model;

import com.google.gson.annotations.SerializedName;

public class Trackfeatures {
	
	 @SerializedName("danceability")
	private Double danceability;
	 @SerializedName("energy")
	private Double energy;
	 @SerializedName("liveness")
	private Double liveness;
	 @SerializedName("speechiness")
	private Double speechiness;
	 @SerializedName("duration")
	private Double duration;
	 @SerializedName("foreign")
	private String foreign;
	 @SerializedName("loudness")
	private Double loudness;
	
	
	public final Double getDanceability() {
		return danceability;
	}
	public final void setDanceability(Double danceability) {
		this.danceability = danceability;
	}
	public final Double getEnergy() {
		return energy;
	}
	public final void setEnergy(Double energy) {
		this.energy = energy;
	}
	public final Double getLiveness() {
		return liveness;
	}
	public final void setLiveness(Double liveness) {
		this.liveness = liveness;
	}
	public final Double getSpeechiness() {
		return speechiness;
	}
	public final void setSpeechiness(Double speechiness) {
		this.speechiness = speechiness;
	}
	public final Double getDuration() {
		return duration;
	}
	public final void setDuration(Double duration) {
		this.duration = duration;
	}
	public final String getForeign() {
		return foreign;
	}
	public final void setForeign(String foreign) {
		this.foreign = foreign;
	}
	public final Double getLoudness() {
		return loudness;
	}
	public final void setLoudness(Double loudness) {
		this.loudness = loudness;
	}
	
	

}
