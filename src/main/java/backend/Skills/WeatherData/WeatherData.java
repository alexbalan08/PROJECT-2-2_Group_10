package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    @JsonProperty("coord") 
    public Coord getCoord() { 
		 return this.coord; } 
    public void setCoord(Coord coord) { 
		 this.coord = coord; } 
    Coord coord;
    @JsonProperty("weather") 
    public ArrayList<Weather> getWeather() {
		 return this.weather; } 
    public void setWeather(ArrayList<Weather> weather) { 
		 this.weather = weather; } 
    ArrayList<Weather> weather;
    @JsonProperty("base") 
    public String getBase() { 
		 return this.base; } 
    public void setBase(String base) { 
		 this.base = base; } 
    String base;
    @JsonProperty("main") 
    public Main getMain() { 
		 return this.main; } 
    public void setMain(Main main) { 
		 this.main = main; } 
    Main main;
    @JsonProperty("visibility") 
    public int getVisibility() { 
		 return this.visibility; } 
    public void setVisibility(int visibility) { 
		 this.visibility = visibility; } 
    int visibility;
    @JsonProperty("wind") 
    public Wind getWind() { 
		 return this.wind; } 
    public void setWind(Wind wind) { 
		 this.wind = wind; } 
    Wind wind;
    @JsonProperty("clouds") 
    public Clouds getClouds() { 
		 return this.clouds; } 
    public void setClouds(Clouds clouds) { 
		 this.clouds = clouds; } 
    Clouds clouds;
    @JsonProperty("dt") 
    public int getDt() { 
		 return this.dt; } 
    public void setDt(int dt) { 
		 this.dt = dt; } 
    int dt;
    @JsonProperty("sys") 
    public Sys getSys() { 
		 return this.sys; } 
    public void setSys(Sys sys) { 
		 this.sys = sys; } 
    Sys sys;
    @JsonProperty("timezone") 
    public int getTimezone() { 
		 return this.timezone; } 
    public void setTimezone(int timezone) { 
		 this.timezone = timezone; } 
    int timezone;
    @JsonProperty("id") 
    public int getId() { 
		 return this.id; } 
    public void setId(int id) { 
		 this.id = id; } 
    int id;
    @JsonProperty("name") 
    public String getName() { 
		 return this.name; } 
    public void setName(String name) { 
		 this.name = name; } 
    String name;
    @JsonProperty("cod") 
    public int getCod() { 
		 return this.cod; } 
    public void setCod(int cod) { 
		 this.cod = cod; } 
    int cod;
}
