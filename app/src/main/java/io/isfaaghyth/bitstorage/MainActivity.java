package io.isfaaghyth.bitstorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.isfaaghyth.rak.Rak;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //just test.
        TextView txtTest = (TextView) findViewById(R.id.txt_test);
        
        //initialize
        Rak.initialize(this);
        
        //entry and show data
        Rak.entry("hai", "I am cool!");
        String result = Rak.grab("hai");
        txtTest.setText(result);
        
        //remove
        Rak.remove("hai");
    }
}
