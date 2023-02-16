package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coord{
    @JsonProperty("lon") 
    public double getLon() { 
		 return this.lon; } 
    public void setLon(double lon) { 
		 this.lon = lon; } 
    double lon;
    @JsonProperty("lat") 
    public double getLat() { 
		 return this.lat; } 
    public void setLat(double lat) { 
		 this.lat = lat; } 
    double lat;
}
