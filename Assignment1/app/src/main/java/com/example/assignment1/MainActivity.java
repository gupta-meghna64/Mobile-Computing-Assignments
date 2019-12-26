package com.example.assignment1;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "LOG_DEBUG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            final int[] position_array = savedInstanceState.getIntArray("SCROLLBAR_POSITIONS");
            final ScrollView scrollView = findViewById(R.id. home_scrollView);
            if(position_array != null){
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(position_array[0], position_array[1]);
                    }
                });
            }
        }
        else{
            final ScrollView scrollView = findViewById(R.id.home_scrollView);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0,0);
                }
            });
        }

        Toast.makeText(this, this.getString(R.string.home), Toast.LENGTH_SHORT).show();

        Button lilyButton = (Button) findViewById(R.id.button_lily);
        Button roseButton = (Button) findViewById(R.id.button_rose);
        Button lotusButton = (Button) findViewById(R.id.button_lotus);

        lilyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lily_intent = new Intent(MainActivity.this, LilyActivity.class);
                startActivity(lily_intent);
            }
        });

        roseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rose_intent = new Intent(MainActivity.this, RoseActivity.class);
                startActivity(rose_intent);
            }
        });

        lotusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lotus_intent = new Intent(MainActivity.this, LotusActivity.class);
                startActivity(lotus_intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.getString(R.string.onstart));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, this.getString(R.string.onrestart));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.getString(R.string.onresume));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, this.getString(R.string.onpause));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, this.getString(R.string.onstop));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, this.getString(R.string.ondestroy));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ScrollView scrollView = findViewById(R.id. home_scrollView);
        savedInstanceState.putIntArray("SCROLLBAR_POSITIONS", new int[]{scrollView.getScrollX(), scrollView.getScrollY()});

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position_array = savedInstanceState.getIntArray("SCROLLBAR_POSITIONS");
        final ScrollView scrollView = findViewById(R.id. home_scrollView);
        if(position_array != null){
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(position_array[0], position_array[1]);
                }
            });
        }
        else{
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0,0);
                }
            });
        }

    }
}
