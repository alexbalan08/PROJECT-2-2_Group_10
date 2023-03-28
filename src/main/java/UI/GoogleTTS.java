//package UI;
//
//import com.google.cloud.texttospeech.v1.*;
//
//
//
//import java.io.FileOutputStream;
//
//public class GoogleTTS {
//
//    public static void main(String[] args) throws Exception {
//        // Instantiates a client
//        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
//            // Set the text input to be synthesized
//            SynthesisInput input = SynthesisInput.newBuilder()
//                    .setText("Hello, World!")
//                    .build();
//
//            // Build the voice request
//            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                    .setLanguageCode("en-US")
//                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
//                    .build();
//
//            // Select the type of audio file you want returned
//            AudioConfig audioConfig = AudioConfig.newBuilder()
//                    .setAudioEncoding(AudioEncoding.MP3)
//                    .build();
//
//            // Perform the text-to-speech request
//            //ByteString audioContents = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig)
//               //     .getAudioContent();
//
//            // Write the response to the output file.
//            try (FileOutputStream out = new FileOutputStream("output.mp3")) {
//               // out.write(audioContents.toByteArray());
//                System.out.println("Audio content written to file \"output.mp3\"");
//            }
//        }
//    }
//}
