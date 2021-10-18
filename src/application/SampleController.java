package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SampleController implements Initializable{

	@FXML
	private AnchorPane pane;
	@FXML 
	private Label songLabel;
	@FXML
	private Label musicTime;
	@FXML
	private Button playButton, pauseButton, previousButton, nextButton;
	@FXML
	private Slider volumeSlider;
	@FXML
	private Slider progressSlider;
	@FXML
	private ImageView playPauseImage;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	
	private Image playImage;
	private Image pauseImage;
	
	private ArrayList<File> songs;
	
	private int songNumber;
	
	private Timer timer;
	private TimerTask task;
	private boolean running = false;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		songs = new ArrayList<File>();
		directory = new File("music");
		files = directory.listFiles();
		if(files != null) {
			for(File file : files) {
				songs.add(file);
			}
		}
		
		
		URL playPng = getClass().getResource("/application/images/play.png");
		URL pausePng = getClass().getResource("/application/images/pause.png");
		try {
			playImage = new Image(playPng.toURI().toString());			
			pauseImage = new Image(pausePng.toURI().toString());
		}catch(Exception ex){
			System.out.println(ex);
		}
		
		songName();
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
			}
			
		});
		
		progressSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, new javafx.event.EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				changeTime(progressSlider.getValue());
			}
			
		});
	}

	public void songName() {
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		if(songs.get(songNumber).getName().length() >= 10) {
			//new SlideOutRight(songLabel).play();
			String songName = songs.get(songNumber).getName().substring(0, 20);
			songName = songName + "...";
			songLabel.setText(songName);
			//new SlideInLeft(songLabel).play();
		}else {
			songLabel.setText(songs.get(songNumber).getName());			
		}
		return;
	}
	
	public void changeTime(double time) {
		double user = progressSlider.getValue();
		double end = media.getDuration().toSeconds();
		double porcentagem = (end * (user/100));
		mediaPlayer.seek(Duration.seconds(porcentagem));
	}
	
	public void playMedia() {
		if(!running) {
			timer = null;
			beginTimer();
			playPauseImage.setImage(pauseImage);
			mediaPlayer.play();
			//System.out.println(new JavaRunCommand().getMetadata());
			
		}else {
			cancelTimer();
			playPauseImage.setImage(playImage);
			mediaPlayer.pause();
		}
		}

	@FXML
	public void nextMedia() {
		if(songNumber < songs.size()-1) {
			songNumber++;
			mediaPlayer.stop();
			if(running) {
				cancelTimer();
			}
			songName();
			playMedia();
		}
		else {
			songNumber = 0;
			mediaPlayer.stop();
			if(running) {
				cancelTimer();
			}
			songName();
			playMedia();
		}
	}

	@FXML
	public void previousMedia() {
		if(songNumber > 0) {
			songNumber--;
			mediaPlayer.stop();
			if(running) {
				cancelTimer();
			}
			songName();
			playMedia();
			
		}
		else {
			songNumber = songs.size()-1;
			mediaPlayer.stop();
			if(running) {
				cancelTimer();
			}
			songName();
			playMedia();
		}
	}

	public void beginTimer() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				Platform.runLater(() -> {
					int minutosAtuais = (int) (current/60);
					int segundosAtuais = (int) (current%60);
					int minutosTotais = (int) (end/60);
					int segundosTotais = (int) (end%60);
					String timer = new String("%02d:%02d/%02d:%02d");
					timer = String.format(timer, minutosAtuais,segundosAtuais,minutosTotais,segundosTotais);
					musicTime.setText(timer);
				});
				progressSlider.setValue((current*100)/end);
				if(current/end == 1) {
					Platform.runLater(() ->{
						nextMedia();						
					});
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public void cancelTimer() {
		running = false;
		timer.cancel();
	}
}
