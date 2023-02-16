package backend.Skills;

import backend.SkillWrapper;
import com.google.gson.JsonParser;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// Getting tokens: https://alecchen.dev/spotify-refresh-token/
public class Spotify extends SkillWrapper {

    public Spotify(){
//        pause();
        playSong(getSongID("Dancing Queen"));
    }

    private final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            // .setRefreshToken("refreshtokenhere")
            .setAccessToken("BQAAxsBrcxnGJWXUFwKjXV6mja2l-c6ot-l2AmBk3OKnPt21DdNJusY9tV7ntV4yW_JDtjqPnI2QWhGEmZT2urotjVVTqQkqijME-dd_-UhB7mWQ8Lc9wwgnVcMJAKFF2MooAu_YoTDfQZZ8OpehSUoOp41m0t0KwqEwjrM1uqfQH8K66igUJZuzDpcbvCjTSovGi94ZzoiYs_WWcWcp7sh-rStp_Vm7UoBl14pevX3vPBSsHH3nQc35yHHMrRls4EDlo0oP8mNcwgmhE7AjMiQbKLqJyPiBnVYjNFh9FaizuPtDiU69KiyHhQmKB9CL3oOhPYJsS1Tfx3GB-gnlNA")
            .build();
    public void startResumeUsersPlayback_Sync(StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest) {
        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return;
    }
    public String getSongID(String song){
        SearchTracksRequest request = spotifyApi.searchTracks(song)
                .build();

        try {
            // Execute the request and get the first page of results
            Paging<Track> trackPaging = request.execute();
            Track[] tracks = trackPaging.getItems();

            if (tracks.length > 0) {
                // Print the name and ID of the first matching track to the console
                Track track = tracks[0];
//                System.out.println("Track name: " + track.getName());
//                System.out.println("Track ID: " + track.getId());

                return track.getId();
            } else {
                System.out.println("No tracks found with that name.");
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
    public void pause() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback()
                .build();
        try {
            pauseUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return;
    }
    private void playSong(String s) {
        // Set up the request to get a specific track by ID
        GetTrackRequest request = spotifyApi.getTrack(s).build();
        try {
            // Execute the request and get the track object
            Future<Track> future = request.executeAsync();
            Track track = future.get();

            // Print the name of the track to the console
            System.out.println("Now playing: " + track.getName());
            //System.out.println(track.getUri());
            StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                    .startResumeUsersPlayback()
                    .uris(JsonParser.parseString("[\""+ track.getUri()+"\"]").getAsJsonArray())
                    .build();
            startResumeUsersPlayback_Sync(startResumeUsersPlaybackRequest);

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        }
        return;
    }

    public static void main(String[] args) {
        new Spotify();
    }


}