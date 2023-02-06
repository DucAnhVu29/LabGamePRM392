package com.example.labgameprm392;

import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //    TextView
    TextView balanceText;
    TextView firstPikachuText;
    TextView secondPikachuText;
    TextView thirdPikachuText;


    //    CheckBox
    CheckBox firstPikachuCheckBox;
    CheckBox secondPikachuCheckBox;
    CheckBox thirdPikachuCheckBox;

    //    EditText
    EditText firstPikachuAmountBet;
    EditText secondPikachuAmountBet;
    EditText thirdPikachuAmountBet;

    //    SeekBar
    SeekBar[] seekBars = {null, null, null};

    //    Button
    Button startRaceBtn;


    private int tickTime = 10;
    private static final int maxCount = 10000;
    private static final int minCount = 0;
    private static final int step = 10;
    private static final String REQUIRE = "Require";
    private static final String BALANCE_TEXT = "You balance: ";
    private final double balance = (int) (50 + (Math.random() * 150 + 1));
    private ArrayList<String> rank = new ArrayList<>();


    private int initialSpeed = 10;

    SeekBarRunner[] pikachus = {null, null, null};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TextView
        balanceText = findViewById(R.id.balanceText);
        firstPikachuText = findViewById(R.id.firstPikachuText);
        secondPikachuText = findViewById(R.id.secondPikachuText);
        thirdPikachuText = findViewById(R.id.thirdPikachuText);


//        CheckBox
        firstPikachuCheckBox = findViewById(R.id.firstPikachuCheckBox);
        secondPikachuCheckBox = findViewById(R.id.secondPikachuCheckBox);
        thirdPikachuCheckBox = findViewById(R.id.thirdPikachuCheckBox);


//        EditText
        firstPikachuAmountBet = findViewById(R.id.firstPikachuAmountBet);
        secondPikachuAmountBet = findViewById(R.id.secondPikachuAmountBet);
        thirdPikachuAmountBet = findViewById(R.id.thirdPikachuAmountBet);


//        SeekBar
        seekBars[0] = findViewById(R.id.firstPikachuSeekBar);
        seekBars[1] = findViewById(R.id.secondPikachuSeekBar);
        seekBars[2] = findViewById(R.id.thirdPikachuSeekBar);


//        Button
        startRaceBtn = findViewById(R.id.startRaceBtn);


//        Set Enable SeekBar
        seekBars[0].setEnabled(false);
        seekBars[1].setEnabled(false);
        seekBars[2].setEnabled(false);


//        Set SeekBar max
        seekBars[0].setMax(maxCount);
        seekBars[1].setMax(maxCount);
        seekBars[2].setMax(maxCount);

        // Pikachus
        pikachus = new SeekBarRunner[]{
                new SeekBarRunner(firstPikachuText.getText().toString()),
                new SeekBarRunner(secondPikachuText.getText().toString()),
                new SeekBarRunner(thirdPikachuText.getText().toString()),
        };


//        Random your balance
        balanceText.setText(BALANCE_TEXT + balance + "$");


//        Set all EditText to disable
        firstPikachuAmountBet.setEnabled(false);
        firstPikachuAmountBet.setFocusable(false);
        secondPikachuAmountBet.setEnabled(false);
        secondPikachuAmountBet.setFocusable(false);
        thirdPikachuAmountBet.setEnabled(false);
        thirdPikachuAmountBet.setFocusable(false);


//        Check Box on checked

//        First
        firstPikachuCheckBox.setOnClickListener(
                v -> {
                    if (firstPikachuCheckBox.isChecked()) {
                        firstPikachuAmountBet.setFocusable(true);
                        firstPikachuAmountBet.setFocusableInTouchMode(true);
                        firstPikachuAmountBet.setEnabled(true);
                    } else {
                        firstPikachuAmountBet.setError(null);
                        firstPikachuAmountBet.setText("");
                        firstPikachuAmountBet.setEnabled(false);
                        firstPikachuAmountBet.setFocusable(false);
                    }
                }
        );

//        Second
        secondPikachuCheckBox.setOnClickListener(
                v -> {
                    if (secondPikachuCheckBox.isChecked()) {
                        secondPikachuAmountBet.setFocusable(true);
                        secondPikachuAmountBet.setFocusableInTouchMode(true);
                        secondPikachuAmountBet.setEnabled(true);
                    } else {
                        secondPikachuAmountBet.setError(null);
                        secondPikachuAmountBet.setText("");
                        secondPikachuAmountBet.setEnabled(false);
                        secondPikachuAmountBet.setFocusable(false);
                    }
                }
        );

//        Third
        thirdPikachuCheckBox.setOnClickListener(
                v -> {
                    if (thirdPikachuCheckBox.isChecked()) {
                        thirdPikachuAmountBet.setFocusable(true);
                        thirdPikachuAmountBet.setFocusableInTouchMode(true);
                        thirdPikachuAmountBet.setEnabled(true);
                    } else {
                        thirdPikachuAmountBet.setError(null);
                        thirdPikachuAmountBet.setText("");
                        thirdPikachuAmountBet.setEnabled(false);
                        thirdPikachuAmountBet.setFocusable(false);
                    }
                }
        );


//        Start Race
        startRaceBtn.setOnClickListener(
                v -> {
                    if (!checkInput() || !checkBalance()) {
                        return;
                    }

                    Thread.currentThread().interrupt();
                    resetRace();

                    Runnable[] runnables = {null, null, null};

                    for (int i = 0; i < runnables.length; ++i) {
                        runnables[i] = createRunnable(i);
                    }

                    for (Runnable runnable : runnables) {
                        Thread t = new Thread(runnable);
                        t.start();
                    }
                }
        );
    }


    private void resetRace() {
        rank.clear();

        for (SeekBarRunner pikachu : pikachus) {
            pikachu.speed = initialSpeed;
            pikachu.setProgress(0);
            pikachu.setRunning(true);
        }

        for (SeekBar seekBar : seekBars) {
            seekBar.setProgress(minCount);
        }
    }

    private Runnable createRunnable(int i) {
        Runnable runnable = new Runnable() {
            public void run() {
                while (pikachus[i].isRunning()) {
                    pikachus[i].speed = randomSpeed(pikachus[i].speed);

                    if (pikachus[i].getProgress() < maxCount) {
                        pikachus[i].setProgress(pikachus[i].getProgress() + pikachus[i].speed);

                        seekBars[i].setProgress(pikachus[i].getProgress());
                        try {
                            Thread.sleep(tickTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        rank.add(pikachus[i].getName());
                        pikachus[i].setRunning(false);

                    }
                }
            }
        };

        return runnable;
    }

    //    Check Input field
    private boolean checkInput() {
        if (firstPikachuCheckBox.isChecked() && TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            firstPikachuAmountBet.setError(REQUIRE);
            return false;
        }

        if (secondPikachuCheckBox.isChecked() && TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            secondPikachuAmountBet.setError(REQUIRE);
            return false;
        }

        if (thirdPikachuCheckBox.isChecked() && TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            thirdPikachuAmountBet.setError(REQUIRE);
            return false;
        }


        return true;
    }

    //    Check amount that bet
    private boolean checkBalance() {

        double firstPikachuBet = 0;
        double secondPikachuBet = 0;
        double thirdPikachuBet = 0;

        if (!TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            firstPikachuBet = Double.parseDouble(firstPikachuAmountBet.getText().toString());
        }

        if (!TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            secondPikachuBet = Double.parseDouble(secondPikachuAmountBet.getText().toString());
        }

        if (!TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            thirdPikachuBet = Double.parseDouble(thirdPikachuAmountBet.getText().toString());
        }

        double amountBet = firstPikachuBet + secondPikachuBet + thirdPikachuBet;

        if (balance - amountBet < 0) {
            Toast.makeText(this, "You can not bet more than what you have!"
                    , Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private int randomSpeed(int previousSpeed) {
        int randomNumber = (int) (Math.random() * 10 + -5);

        if (previousSpeed + randomNumber < 0) {
            return 0;
        }

        return previousSpeed + randomNumber;
    }

//    Bet Check


}