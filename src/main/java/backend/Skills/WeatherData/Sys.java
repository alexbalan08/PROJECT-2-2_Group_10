package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sys{
    @JsonProperty("type") 
    public int getType() { 
		 return this.type; } 
    public void setType(int type) { 
		 this.type = type; } 
    int type;
    @JsonProperty("id") 
    public int getId() { 
		 return this.id; } 
    public void setId(int id) { 
		 this.id = id; } 
    int id;
    @JsonProperty("country") 
    public String getCountry() { 
		 return this.country; } 
    public void setCountry(String country) { 
		 this.country = country; } 
    String country;
    @JsonProperty("sunrise") 
    public int getSunrise() { 
		 return this.sunrise; } 
    public void setSunrise(int sunrise) { 
		 this.sunrise = sunrise; } 
    int sunrise;
    @JsonProperty("sunset") 
    public int getSunset() { 
		 return this.sunset; } 
    public void setSunset(int sunset) { 
		 this.sunset = sunset; } 
    int sunset;
}
