package backend.Skills;

import backend.SkillWrapper;
import com.google.gson.JsonParser;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.player.PauseUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// I = "18942c9e84434efbb15ad08c82cd1ee3";
// S = "6e25829074f54e10a4811b5cb39e5622";
// Getting tokens: https://alecchen.dev/spotify-refresh-token/

public class Spotify extends SkillWrapper {
    private static final String clientId = "18942c9e84434efbb15ad08c82cd1ee3";
    private static final String clientSecret = "6e25829074f54e10a4811b5cb39e5622";
    private static final String refreshToken = "AQCEzgHohbiM7wwGIcLsi3_dFJCXY3uxuF2Vt4HVyYJY6-7o0Q3ybS75NohwtVczVsfFJ3TcMLsndUMQnhc0sX7BXBc67FAPv1Ozj5HwPubcQqR_l5Q_aXn3ixLouwSsTuU";
    public Spotify(){
        AuthorizationCodeRefreshRequest codeRefresh = spotifyApi.authorizationCodeRefresh().build();
        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = codeRefresh.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private String actualMusic = "";
    private final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId).setClientSecret(clientSecret).setRefreshToken(refreshToken).build();

    // slots : [<ACTION>, optional <TITLE>]
    @Override
    public void start(List<String> slots) {
        System.out.println(slots.toString());
        String action = slots.get(0);
        if (slots.size() == 2) {
            this.actualMusic = slots.get(1);
        }

        if (Objects.equals(action, "play")) {
            outputs.add(playSong(getSongID(this.actualMusic)));
        } else if (Objects.equals(action, "resume") || Objects.equals(action, "replay")) {
            outputs.add(resumeMusic());
        } else if (Objects.equals(action, "pause") || Objects.equals(action, "stop")) {
            outputs.add(pause());
        } else {
            outputs.add(getActualMusicInformation());
        }
    }

    private String getSongID(String song) {
        try {
            SearchTracksRequest request = spotifyApi.searchTracks(song).build();

            // Execute the request and get the first page of results
            Paging<Track> trackPaging = request.execute();
            Track[] tracks = trackPaging.getItems();

            if (tracks.length > 0) {
                // Print the name and ID of the first matching track to the console
                Track track = tracks[0];
                return track.getId();
            } else {
                System.out.println("No tracks found with that name.");
            }

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private String playSong(String s) {
        try {
            // Set up the request to get a specific track by ID
            GetTrackRequest request = spotifyApi.getTrack(s).build();

            // Execute the request and get the track object
            Future<Track> future = request.executeAsync();
            Track track = future.get();

            // Print the name of the track to the console
            System.out.println("Now playing: " + track.getName());
            this.actualMusic = track.getName();

            //System.out.println(track.getUri());
            StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
                    .startResumeUsersPlayback()
                    .uris(JsonParser.parseString("[\"" + track.getUri() + "\"]").getAsJsonArray())
                    .build();
            startResumeUsersPlayback_Sync(startResumeUsersPlaybackRequest);
            return "Now playing \"" + this.actualMusic + "\", by " + getFormattedArtists(track.getArtists()) + ".";
        } catch (InterruptedException | ExecutionException e) {
            return "I can't find the song ...";
        }
    }

    private String resumeMusic() {
       try {
           startResumeUsersPlayback_Sync(spotifyApi.startResumeUsersPlayback().build());
           return "The music is resumed";
       } catch (Exception e) {
           return "I can't resume the music. It's maybe because there isn't a music at the moment.";
       }
    }

    private String pause() {
        try {
            PauseUsersPlaybackRequest pauseUsersPlaybackRequest = spotifyApi.pauseUsersPlayback().build();
            pauseUsersPlaybackRequest.execute();
            return "The music is paused.";
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            return "I can't pause the music. It's maybe because there isn't a music playing at the moment.";
        }
    }

    private String getActualMusicInformation() {
        try {
            GetUsersCurrentlyPlayingTrackRequest request = spotifyApi.getUsersCurrentlyPlayingTrack().build();
            CurrentlyPlaying currentlyPlaying = request.execute();
            String artist = currentlyPlaying.getItem().toString();
            int a = artist.indexOf("ArtistSimplified(name=");
            int b = artist.indexOf(", external");


            artist = artist.substring(a+22,b);
            return "The music is \"" + currentlyPlaying.getItem().getName() + "\", by " + artist + ".";
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void startResumeUsersPlayback_Sync(StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest) {
        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getFormattedArtists(ArtistSimplified[] artists) {
        StringBuilder result = new StringBuilder();
        for (ArtistSimplified artist : artists) {
            result.append(artist.getName()).append(", ");
        }
        return result.substring(0, result.toString().lastIndexOf(", "));
    }


}