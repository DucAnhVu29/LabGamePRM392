package com.example.labgameprm392;

import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


enum State {
    START,
    RESET,
}

public class MainActivity extends AppCompatActivity {

    //    TextView
    TextView balanceText;

    //    EditText
    EditText firstPikachuAmountBet;
    EditText secondPikachuAmountBet;
    EditText thirdPikachuAmountBet;

    //    SeekBar
    SeekBar[] seekBars = {null, null, null};

    //    Button
    Button startRaceBtn;

    private State state = State.START;

    private int tickTime = 10;
    private static final int maxCount = 20000;
    private static final int minCount = 0;
    private double balance = (int) (50 + (Math.random() * 150 + 1));
    private ArrayList<String> rank = new ArrayList<>();
    private int initialSpeed = 10;

    private int nextRank = 1;

    double[] betAmounts = {0, 0, 0};

    TextView[] rankTVs = {null, null, null};

    SeekBarRunner[] pikachus = {null, null, null};

    GifImageView[] pikachuGifs = {null, null, null};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        pikachuGifs[0] = findViewById(R.id.pikachu_gif_0);
        pikachuGifs[1] = findViewById(R.id.pikachu_gif_1);
        pikachuGifs[2] = findViewById(R.id.pikachu_gif_2);

        rankTVs[0] = findViewById(R.id.rank_0);
        rankTVs[1] = findViewById(R.id.rank_1);
        rankTVs[2] = findViewById(R.id.rank_2);


//        TextView
        balanceText = findViewById(R.id.balanceText);


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

        for (int i = 0; i < seekBars.length; ++i) {
            seekBars[i].setOnSeekBarChangeListener(
                    onSeekBarChangeListener(i)
            );
        }


        // Pikachus
        pikachus = new SeekBarRunner[]{
                new SeekBarRunner("1", initialSpeed),
                new SeekBarRunner("2", initialSpeed),
                new SeekBarRunner("3", initialSpeed),
        };


//        Random your balance
        balanceText.setText(balance + "");


//        Start Race
        startRaceBtn.setOnClickListener(
                v -> {
                    if (state == State.START) {
                        resetRace();
                        Thread.currentThread().interrupt();

                        if (!checkInput()) {
                            Toast.makeText(this, "Cược tối đa 2 Pikachu thoyyy",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!checkBalance()) {
                            Toast.makeText(this, "Hong đủ tiền má oyy",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        Runnable[] runnables = {null, null, null};

                        for (int i = 0; i < runnables.length; ++i) {
                            runnables[i] = createRunnable(i);
                        }

                        for (Runnable runnable : runnables) {
                            Thread t = new Thread(runnable);
                            t.start();
                        }

                        firstPikachuAmountBet.setVisibility(View.INVISIBLE);
                        secondPikachuAmountBet.setVisibility(View.INVISIBLE);
                        thirdPikachuAmountBet.setVisibility(View.INVISIBLE);

                        startRaceBtn.setText("Chơi lại");
                        startRaceBtn.setEnabled(false);
                        startRaceBtn.setBackgroundColor(0xFFe0e0e0);
                        state = State.RESET;
                    } else if (state == State.RESET) {
                        resetRace();

                        firstPikachuAmountBet.setVisibility(View.VISIBLE);
                        secondPikachuAmountBet.setVisibility(View.VISIBLE);
                        thirdPikachuAmountBet.setVisibility(View.VISIBLE);
                        startRaceBtn.setText("Bắt đầu");

                        state = State.START;
                    }
                }
        );
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener(int i) {
        return new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                ViewGroup.MarginLayoutParams params =
                        (ViewGroup.MarginLayoutParams) pikachuGifs[i].getLayoutParams();
                params.setMarginStart((int) ((double) paramInt / maxCount * (seekBars[i].getWidth() - pikachuGifs[i].getWidth())));
                pikachuGifs[i].requestLayout();
            }
        };
    }

    private void resetRace() {
        rank.clear();

        nextRank = 1;
        betAmounts = new double[]{0, 0, 0};

        for (TextView rankTV: rankTVs) {
            rankTV.setText("");
        }

        for (GifImageView pikachuGif : pikachuGifs) {
            ((GifDrawable) pikachuGif.getDrawable()).start();
        }

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
                        pikachus[i].setRank(nextRank);
                        nextRank++;
                        pikachus[i].setRunning(false);
                        ((GifDrawable) pikachuGifs[i].getDrawable()).stop();

                        if (pikachus[i].getRank() == 1) {
                            balance += betAmounts[i] * 2;
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                rankTVs[i].setText(Integer.toString(pikachus[i].getRank()));

                                if (nextRank > 3) {
                                    startRaceBtn.setEnabled(true);
                                    startRaceBtn.setBackgroundColor(0xFFF6EB05);

                                    balanceText.setText(Double.toString(balance));
                                }
                            }
                        });



                        Thread.currentThread().interrupt();

                    }
                }


            }
        };

        return runnable;
    }

    //    Check Input field
    private boolean checkInput() {
        int cnt = 0;

        try {
            if (!TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {

                betAmounts[0] =
                        Double.parseDouble(firstPikachuAmountBet.getText().toString());
                cnt++;
            }

            if (!TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
                betAmounts[1] =
                        Double.parseDouble(secondPikachuAmountBet.getText().toString());
                cnt++;
            }

            if (!TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
                betAmounts[2] =
                        Double.parseDouble(thirdPikachuAmountBet.getText().toString());
                cnt++;
            }

            if (cnt >= 3) return false;

            for (double betAmount : betAmounts) {
                balance -= betAmount;
            }

            firstPikachuAmountBet.setText("");
            secondPikachuAmountBet.setText("");
            thirdPikachuAmountBet.setText("");

            balanceText.setText(Double.toString(balance));
        } catch (Exception e) {
            Toast.makeText(this, "Số tiền cược không hợp lệ",
                    Toast.LENGTH_LONG).show();
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