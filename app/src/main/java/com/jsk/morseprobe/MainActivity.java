package com.jsk.morseprobe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;

import android.hardware.Camera.Parameters;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static Camera camera;               //to use flashlight for transmitting morse code
    private MediaPlayer mediaPlayer;            //to use beep sounds for transmitting morse code
    private Parameters params;                  //sets camera parameters
    //private volatile boolean musicThreadAlive=false;            // to Async the task of playing sound
    //private volatile boolean lightThreadAlive=false;            // to Async the task of flashing lights
    Thread musicThread;                                         //shouldn't block the UI thread
    Thread lightThread;                                         //shouldn't block the UI thread

    Map<Character,String> morseCode;            //this map will map each alphanumeric character to its morseCode

    public MainActivity(){
        morseCode = new HashMap<Character,String>();
        morseCode.put('A', "·—");
        morseCode.put('B', "—···");
        morseCode.put('C',"—·—·");
        morseCode.put('D',"—··");
        morseCode.put('E',"·");
        morseCode.put('F',"··—·");
        morseCode.put('G',"——·");
        morseCode.put('H',"····");
        morseCode.put('I',"··");
        morseCode.put('J',"·———");
        morseCode.put('K',"—·—");
        morseCode.put('L',"·—··");
        morseCode.put('M',"——");
        morseCode.put('N',"—·");
        morseCode.put('O',"———");
        morseCode.put('P',"·——·");
        morseCode.put('Q',"——·—");
        morseCode.put('R',"·—·");
        morseCode.put('S',"···");
        morseCode.put('T',"—");
        morseCode.put('U',"··—");
        morseCode.put('V',"···—");
        morseCode.put('W',"·——");
        morseCode.put('X',"—··—");
        morseCode.put('Y',"—·—");
        morseCode.put('Z',"——··");

        morseCode.put('1',"·————");
        morseCode.put('2',"··———");
        morseCode.put('3',"···——");
        morseCode.put('4',"····—");
        morseCode.put('5',"·····");
        morseCode.put('6',"—····");
        morseCode.put('7',"——···");
        morseCode.put('8',"———··");
        morseCode.put('9',"————·");
        morseCode.put('0',"—————");
        morseCode.put(' ',"  ");
    }

    /**
     * This method takes an alphanumeric message as input
     * and returns its morse code in the form of UTF characters.
     * @param message
     * @return
     */
    protected String generateCode(String message){
        //change all characters to upper case to make the function case insensitive
        message = message.toUpperCase();

        // initializes the code with an empty string.
        StringBuilder codedMessage= new StringBuilder("");

        // loops through the message character by character and maps
        // it to its morse code character.
        for(int i=0; i<message.length(); i++){
            // returns null for any characters other than (a-z, 0-9, and ' ')
            String val= morseCode.get(message.charAt(i));
            if(val != null) {
                codedMessage.append(val);
                codedMessage.append(" ");
            }
            else return null;           //return a null if any unacceptabe character is found.
        }

        return codedMessage.toString();
    }

    /**
     * This method checks to see if the phone has a camera or not
     * or if the camera is being used by any other app or not.
     * If the camera resource was initiated then it returns a true else false.
     * @return
     */
    private boolean getCamera() {
        if(camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
                return true;
            }
            catch(Exception e) {
                Log.e("Error: ", e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void flashMessage(final String message) {
        if(musicThread != null) {
            //musicThreadAlive=false;
            musicThread.interrupt();
            musicThread=null;
        }
        //lightThreadAlive=true;
        lightThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<message.length() && !lightThread.isInterrupted(); i++) {
                    switch(message.charAt(i)) {
                        case '·':
                            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(params);
                            camera.startPreview();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            params.setFlashMode(Parameters.FLASH_MODE_OFF);
                            camera.setParameters(params);
                            camera.startPreview();
                            break;
                        case '—':
                            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(params);
                            camera.startPreview();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            params.setFlashMode(Parameters.FLASH_MODE_OFF);
                            camera.setParameters(params);
                            camera.startPreview();
                            break;
                        case ' ':
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lightThread.interrupt();
                //lightThreadAlive=false;
            }
        });
        lightThread.start();

        // this is different from the playSound() 'if' because
        // this case runs when the thread runs till its termination
        // and is not interrupted by musicThread
        if(lightThread != null) {
            //lightThreadAlive = false;
            lightThread.interrupt();
            lightThread = null;
        }
    }

    public void playMessage(final String message) {
        mediaPlayer = MediaPlayer.create(this,R.raw.beep);
        if(lightThread != null) {
            //lightThreadAlive=false;
            lightThread.interrupt();
            lightThread=null;
        }
        //musicThreadAlive=true;
        musicThread= new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<message.length() && !musicThread.isInterrupted(); i++) {
                    switch(message.charAt(i)) {
                        case '·':
                            mediaPlayer.start();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(mediaPlayer.isPlaying()) {
                                mediaPlayer.seekTo(0);
                                mediaPlayer.pause();
                            }
                            break;
                        case '—':
                            mediaPlayer.start();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(mediaPlayer.isPlaying()) {
                                mediaPlayer.seekTo(0);
                                mediaPlayer.pause();
                            }
                            break;
                        case ' ':
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(mediaPlayer.isPlaying()) {
                                mediaPlayer.seekTo(0);
                                mediaPlayer.pause();
                            }
                            break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                musicThread.interrupt();
                //musicThreadAlive=false;
            }
        });

        // this is different from the playSound() 'if' because
        // this case runs when the thread runs till its termination
        // and is not interrupted by musicThread
        musicThread.start();
        if(musicThread != null) {
            musicThread.interrupt();
            musicThread=null;
        }
    }

    /**
     * NOTE: Copied from stackoverflow
     * this method hides keyboard when called.
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generateCodeButton = (Button) findViewById(R.id.button);
        Button flashCodeButton = (Button) findViewById(R.id.button2);
        Button playBeepButton = (Button) findViewById(R.id.button3);
        Button sendMessageButton = (Button) findViewById(R.id.button4);
        Button clearButton = (Button) findViewById(R.id.button5);
        final EditText editText = (EditText) findViewById(R.id.message);
        final TextView codedTextView = (TextView) findViewById(R.id.codedMessage);

        generateCodeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //hide keyboard
                        hideKeyboard(MainActivity.this);

                        String message = editText.getText().toString();
                        if(message.length()==0) {
                            Toast.makeText(MainActivity.this,"No value entered!",Toast.LENGTH_SHORT).show();
                            codedTextView.setText("");
                        }
                        else {
                            String codedMessage = generateCode(message);
                            if (codedMessage != null) {
                                Toast.makeText(MainActivity.this, "Morse code generated!", Toast.LENGTH_SHORT).show();
                                codedTextView.setText(codedMessage);
                            } else {
                                Toast.makeText(MainActivity.this, "ERROR: message must contain only characters (A-Z) and numbers(0-9)", Toast.LENGTH_LONG).show();
                                codedTextView.setText("");
                            }
                        }
                    }
                }
        );

        flashCodeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // hides keyboard
                        hideKeyboard(MainActivity.this);

                        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                        if(hasFlash == false) {
                            Toast.makeText(MainActivity.this, "ERROR: Your device does not support flash!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            if(!getCamera()) {
                                Toast.makeText(MainActivity.this, "ERROR: Couldn't open camera!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                String message = editText.getText().toString();
                                if(message.length()==0) {
                                    Toast.makeText(MainActivity.this,"No value entered!",Toast.LENGTH_SHORT).show();
                                    codedTextView.setText("");
                                }
                                else {
                                    String codedMessage = generateCode(message);
                                    if (codedMessage != null) {
                                        Toast.makeText(MainActivity.this, "Flashing Morse Code!", Toast.LENGTH_SHORT).show();
                                        codedTextView.setText(codedMessage);
                                        flashMessage(codedMessage);

                                    } else {
                                        Toast.makeText(MainActivity.this, "ERROR: message must contain only characters (A-Z) and numbers(0-9)", Toast.LENGTH_LONG).show();
                                        codedTextView.setText("");
                                    }
                                }
                            }
                        }
                    }
                }
        );

        playBeepButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //hides keyboard
                        hideKeyboard(MainActivity.this);

                        String message = editText.getText().toString();
                        if(message.length()==0) {
                            Toast.makeText(MainActivity.this,"No value entered!",Toast.LENGTH_SHORT).show();
                            codedTextView.setText("");
                        }
                        else {
                            String codedMessage = generateCode(message);
                            if (codedMessage != null) {
                                Toast.makeText(MainActivity.this, "Playing Morse Code!", Toast.LENGTH_SHORT).show();
                                codedTextView.setText(codedMessage);
                                playMessage(codedMessage);
                            } else {
                                Toast.makeText(MainActivity.this, "ERROR: message must contain only characters (A-Z) and numbers(0-9)", Toast.LENGTH_LONG).show();
                                codedTextView.setText("");
                            }
                        }
                    }
                }
        );

        sendMessageButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = editText.getText().toString();
                        if(message.length() == 0) {
                            Toast.makeText(MainActivity.this, "No value entered!", Toast.LENGTH_SHORT).show();
                            codedTextView.setText("");
                        }
                        else {
                            String codedMessage = generateCode(message);
                            if (codedMessage != null) {
                                codedTextView.setText(codedMessage);
                                Uri uri = Uri.parse("smsto:");
                                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                intent.putExtra("sms_body", codedMessage);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "ERROR: message must contain only characters (A-Z) and numbers(0-9)", Toast.LENGTH_LONG).show();
                                codedTextView.setText("");
                            }
                        }
                    }
                }
        );

        clearButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText("");
                        codedTextView.setText("");
                    }
                }
        );
    }

    /**
     * To release camera and media player resources
     * When app is stopped. Otherwise ither apps will
     * not be able to utilize them.
     */
    @Override
    protected void onStop() {
        super.onStop();

        if(camera!=null){
            camera.stopPreview();
            camera.release();
            camera=null;
        }

        if(mediaPlayer !=null) {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
