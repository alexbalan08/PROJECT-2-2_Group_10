package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main{
    @JsonProperty("temp") 
    public double getTemp() { 
		 return this.temp; } 
    public void setTemp(double temp) { 
		 this.temp = temp; } 
    double temp;
    @JsonProperty("feels_like") 
    public double getFeels_like() { 
		 return this.feels_like; } 
    public void setFeels_like(double feels_like) { 
		 this.feels_like = feels_like; } 
    double feels_like;
    @JsonProperty("temp_min") 
    public double getTemp_min() { 
		 return this.temp_min; } 
    public void setTemp_min(double temp_min) { 
		 this.temp_min = temp_min; } 
    double temp_min;
    @JsonProperty("temp_max") 
    public double getTemp_max() { 
		 return this.temp_max; } 
    public void setTemp_max(double temp_max) { 
		 this.temp_max = temp_max; } 
    double temp_max;
    @JsonProperty("pressure") 
    public int getPressure() { 
		 return this.pressure; } 
    public void setPressure(int pressure) { 
		 this.pressure = pressure; } 
    int pressure;
    @JsonProperty("humidity") 
    public int getHumidity() { 
		 return this.humidity; } 
    public void setHumidity(int humidity) { 
		 this.humidity = humidity; } 
    int humidity;
}
