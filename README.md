
# Project 2-2, Group 10

Welcome to our assistant ! 

In recent years, more and more digital assistants (Amazon Alexa, Google Home, Apple Siri, . . . ) became available. These assistants are able to perform specific tasks and support the user in her/his daily routines. Nowadays, they allow the user to control various components, help organize the daily schedule and perform many more tasks.

The central topic of this project is the development of a multi-modal digital assistant. The user can ask a question and the assistant will try to answer it. It has a general architecture that allows the easy integration of additional skills.

To use our assistant, you need to be recognized ! A top of the Facial Recognition, we also implemented a Face Recognition. The app will take some pictures of the user, to recognize it later.

You can download it and have fun with it ! Enjoy !


#### Features

- Register with your name and face
- See all the available skills 
- Deactivate / activate BERT model
- Ask a question and get an answer
- Add a skill
- Use speech recognition 







## Installation

You can download this project using the download button (located at the top right) or using the following commands in your terminal : 

```bash
  cd path/to/desired/folder
  git clone https://github.com/alexbalan08/PROJECT-2-2_Group_10.git
```
In both case, you need to follow the next steps : 
- download JavaFX (https://openjfx.io/)
- download the library OpenCV (https://opencv.org/releases/, source) and place either in the C:/ folder for Windows, or on the Desktop on Mac
- open the folder with an IDE like IntelliJ, Eclipse, ...
- install specifics dependances :
    - Jep
        - in your terminal, use the command 
            ```bash
            pip install jep
            ```
        - create a virtual environment in project directory :
            ```bash
            python3 -m venv virtualPy
            source virtualPy/bin/activate 
            pip install numpy panda torch transformer
            ```
        - go to edit configuration > advanced options > add VM options :
            ```bash
            -Djava.library.path=<path to jep folder>
            You can have the path with the command : pip show jep
            ```
    - Whisper :
        - in your terminal, use the command 
            ```bash
            pip3 install torch torchvision torchaudio --index-url 
            ```
        - in windows powershell, open as admin :
            ```bash
            Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))”)
            ```
        - close PowerShell and open it again : 
            ```bash
            choco install ffmpeg
            ```
- run the Gradle script to download all the remainings dependances (normally done automatically)
- launch the app and enjoy !


## Usage

### Register with you face 

To register, the assistant need to take some pictures of you. Turn your head slowly in all possible directions so that the app recognizes you easily.

### See all the available skills

You can ask the assistant to help you with the skills. With the question "Can you give me some examples of questions ?", it will give you all the skills known with an example of question for each. 

### Deactivate / activate BERT model

If you don't want to use BERT model and test our own CFG, you can use the prompt "Deactivate BERT". You can easily re-activate it again with the prompt "Activate BERT".

### Ask a question and get an answer

With one question in mind, you can ask the assisant and it will use BERT (if activated), our personnal CFG and a CLIP model to answer you.

### Add a skill

If you think that the assistant lacks a skill, you can easily add one ! The prompt "Can you show me the template to add a skill ?" will explain you the template for a skill and then, you can use this prompt "add skill:" to add a new one !

### Use speech recognition

When clicking on the second button after the text area, you can direclty speech to the assistant and it will recognize what you said ! Then, you can click on the send button (or enter) to have an answer !
## Specificities

**Languages:** Java, Python

**Technologies:** JavaFX, OpenCV

**External API:** Spotify, Canvas (Maastricht University), Open Weather Map, Wikipedia and Whispers


## Authors

- Balan Temocico, Alexandru [@alexbalan08](https://github.com/alexbalan08)
- Cadena Fajardo, Valentina [@valescadena](https://github.com/valescadena)
- Garland, Simon [@Simon-Garland](https://github.com/Simon-Garland)
- Kotsis, Vince [@TODO](https://github.com/)
- Mammo Zagarella, Olivier [@Mzmmo](https://github.com/Mzmmo)
- Padimanskas, Liutauras [@LiutaurasP](https://github.com/LiutaurasP)
- Purici, Adriana [@TODO](https://github.com/)
