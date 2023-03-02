package backend.Skills.CanvasData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    @JsonProperty("items_url")
    public int getItems_url() {
        return this.items_url; }
    public void setItems_url(int items_url) {
        this.items_url = items_url; }
    int items_url;
}
