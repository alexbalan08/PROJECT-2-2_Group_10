package backend.Skills.WeatherData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather{
    @JsonProperty("id") 
    public int getId() { 
		 return this.id; } 
    public void setId(int id) { 
		 this.id = id; } 
    int id;
    @JsonProperty("main") 
    public String getMain() { 
		 return this.main; } 
    public void setMain(String main) { 
		 this.main = main; } 
    String main;
    @JsonProperty("description") 
    public String getDescription() { 
		 return this.description; } 
    public void setDescription(String description) { 
		 this.description = description; } 
    String description;
    @JsonProperty("icon") 
    public String getIcon() { 
		 return this.icon; } 
    public void setIcon(String icon) { 
		 this.icon = icon; } 
    String icon;
}
