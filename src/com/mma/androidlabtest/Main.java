package com.mma.androidlabtest;
import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity 
			 implements OnInitListener {
			 
	private EditText etSentence = null;
	private Button btnSpeak = null;
	private Button btnRecord = null;
	private Button btnPlay = null;
	private Button btnAssociate = null;
	private String filename = null;
	private TextToSpeech textToSpeech = null;
	private MediaPlayer mediaPlayer = null;
	private static final int REQUEST_CODE_TTS = 0;

	public void onCreate (Bundle bundle) {
		super.onCreate (bundle);
		setContentView (R.layout.main);
		filename = "/sdcard/sound.wav";
		etSentence = (EditText)findViewById (R.id.et_sentence);
		
		btnSpeak = (Button)findViewById (R.id.btn_speak);
		btnSpeak.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				textToSpeech.speak (etSentence.getText().toString(), TextToSpeech.QUEUE_ADD, null);
			}
		});
		
		btnRecord = (Button)findViewById (R.id.btn_record);
		btnRecord.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				File file = new File (filename);
				if (file.exists())
					file.delete();
				if(textToSpeech.synthesizeToFile (etSentence.getText().toString(), null, filename) == TextToSpeech.SUCCESS) {
					Toast toast = Toast.makeText (getApplicationContext(), "File created", Toast.LENGTH_SHORT);
					toast.show();
					btnPlay.setEnabled (true);
					btnAssociate.setEnabled (true);
				} else {
					Toast toast = Toast.makeText (getApplicationContext(), "Unable to create the file", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
		btnPlay = (Button)findViewById (R.id.btn_play);
		btnPlay.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				try {
					mediaPlayer = new MediaPlayer();
					mediaPlayer.setDataSource (filename);
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch(Exception e) {
					Toast toast = Toast.makeText (getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		
		btnAssociate = (Button)findViewById (R.id.btn_associate);
		btnAssociate.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				textToSpeech.addSpeech (etSentence.getText().toString(), filename);
				Toast toast = Toast.makeText (getApplicationContext(), "Done", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
		
		Intent intent = new Intent();
		intent.setAction (TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult (intent, REQUEST_CODE_TTS);
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_TTS) {
			switch (resultCode) {
				case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
					textToSpeech = new TextToSpeech (this, this);
					break;
				case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
				case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
				case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
					Intent intentInstallation = new Intent();
					intentInstallation.setAction (TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					startActivity (intentInstallation);
					break;
				case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
				default:
			}
		}
	}

	public void onInit(int status) {
		if( status == TextToSpeech.SUCCESS) {
			btnSpeak.setEnabled (true);
			btnRecord.setEnabled (true);
		}
	}

	public void onPause() {
		super.onPause();
		if(mediaPlayer != null) {
			mediaPlayer.stop();
		}
		if( textToSpeech != null)
			textToSpeech.stop();
	}

	public void onDestroy() {
		super.onDestroy();
		if(mediaPlayer != null) {
			mediaPlayer.release();
		}
		if( textToSpeech != null) {
			textToSpeech.shutdown();
		}
	}
}
