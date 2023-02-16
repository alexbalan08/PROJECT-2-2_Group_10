package backend.Skills;

import backend.SkillWrapper;

import backend.Skills.WeatherData.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather extends SkillWrapper {
    public final String APIkey = "450b386258d6eca1fe2eda0820dfa7a6";
    public String city = "Maastricht";
    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+APIkey+"&units=metric";


    @Override
    public void start(String matchedTemplate) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // System.out.println(response.toString());

            ObjectMapper om = new ObjectMapper();
            WeatherData WD = om.readValue(response.toString(), WeatherData.class);
            System.out.println("At the moment, in "+ city + ", it's "+ WD.getMain().getTemp()+ " °C. Feels like: "+WD.getMain().getFeels_like()+" °C.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Weather w = new Weather();
        w.start("TODO");
    }



    }





