package com.jsk.morseprobe;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    protected String generateCode(String message){
        Map<Character,String> morseCode = new HashMap<Character,String>();
        morseCode.put('A', ".-");
        morseCode.put('B', "-...");
        morseCode.put('C',"-.-.");
        morseCode.put('D',"-..");
        morseCode.put('E',".");
        morseCode.put('F',"..-.");
        morseCode.put('G',"--.");
        morseCode.put('H',"....");
        morseCode.put('I',"..");
        morseCode.put('J',".---");
        morseCode.put('K',"-.-");
        morseCode.put('L',".-..");
        morseCode.put('M',"--");
        morseCode.put('N',"-.");
        morseCode.put('O',"---");
        morseCode.put('P',".--.");
        morseCode.put('Q',"--.-");
        morseCode.put('R',"._.");
        morseCode.put('S',"...");
        morseCode.put('T',"-");
        morseCode.put('U',"..-");
        morseCode.put('V',"...-");
        morseCode.put('W',".--");
        morseCode.put('X',"-..-");
        morseCode.put('Y',"-.--");
        morseCode.put('Z',"--..");

        morseCode.put('1',".----");
        morseCode.put('2',"..---");
        morseCode.put('3',"...--");
        morseCode.put('4',"....-");
        morseCode.put('5',".....");
        morseCode.put('6',"-....");
        morseCode.put('7',"--...");
        morseCode.put('8',"---..");
        morseCode.put('9',"----.");
        morseCode.put('0',"-----");
        morseCode.put(' ',"   ");

        message = message.toUpperCase();

        StringBuilder codedMessage= new StringBuilder("");

        for(int i=0; i<message.length(); i++){
            // could throw an exception here for any other characters
            // needs to be amended
            String val= morseCode.get(message.charAt(i));
            if(val != null) {
                codedMessage.append(val);
                codedMessage.append(" ");
            }
            else return null;
        }

        return codedMessage.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generateCodeButton = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.message);
        final TextView codedTextView = (TextView) findViewById(R.id.codedMessage);

        generateCodeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = editText.getText().toString();
                        String codedMessage = generateCode(message);
                        if(codedMessage != null) {
                            Toast.makeText(MainActivity.this, "Morse code generated!", Toast.LENGTH_SHORT).show();
                            codedTextView.setText(codedMessage);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "ERROR: message must contain only characters (A-Z) and numbers(0-9)", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}
