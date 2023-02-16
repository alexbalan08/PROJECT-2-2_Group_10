package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Wind{
    @JsonProperty("speed") 
    public double getSpeed() { 
		 return this.speed; } 
    public void setSpeed(double speed) { 
		 this.speed = speed; } 
    double speed;
    @JsonProperty("deg") 
    public int getDeg() { 
		 return this.deg; } 
    public void setDeg(int deg) { 
		 this.deg = deg; } 
    int deg;
}
