package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Clouds{
    @JsonProperty("all")
    public int getAll() { 
		 return this.all; } 
    public void setAll(int all) { 
		 this.all = all; } 
    int all;
}
