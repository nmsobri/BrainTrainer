package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {
    private TextView timer;
    private TextView question;
    private TextView counter;
    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;
    private TextView answerAnswer;
    private Button play;

    private final int roundTime = 30;
    private int currentTime;
    private int firstOperand;
    private int secondOperand;
    private boolean gameIsRunning = false;
    int correctAnswer = 0;
    int numOfQuestions = 0;

    private Timer gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.linkUI();
        this.setupEvents();
        currentTime = roundTime;
    }

    private void setupEvents() {
        this.answer1.setOnClickListener(this::checkAnswer);
        this.answer2.setOnClickListener(this::checkAnswer);
        this.answer3.setOnClickListener(this::checkAnswer);
        this.answer4.setOnClickListener(this::checkAnswer);

        this.play.setOnClickListener(v -> {
            if (!this.gameIsRunning) {
                this.gameIsRunning = true;
                this.counter.setText(correctAnswer + " / " + numOfQuestions);
                this.play.setVisibility(View.INVISIBLE);

                this.setQuestion();

                this.answerAnswer.setText(R.string.select_answer);
                this.answerAnswer.setBackgroundColor(0);
                this.answerAnswer.setVisibility(View.VISIBLE);

                gameTimer = new Timer();
                gameTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        currentTime -= 1;

                        if (currentTime <= 0) {
                            stopGame();
                        }

                        runOnUiThread(() -> timer.setText(currentTime + " sec"));
                    }
                }, 0, 1000);
            } else {
                this.gameIsRunning = false;
            }
        });
    }

    private void stopGame() {
        gameTimer.cancel();

        runOnUiThread(() -> {
            play.setText(R.string.play_again);
            play.setVisibility(View.VISIBLE);
        });

        currentTime = roundTime;
        numOfQuestions = 0;
        correctAnswer = 0;
        gameIsRunning = false;

        runOnUiThread(() -> {
            answerAnswer.setBackgroundColor(getResources().getColor(R.color.blue));
            answerAnswer.setText("Game Over");
        });

        MediaPlayer.create(this, R.raw.gong).start();
    }

    private void checkAnswer(View v) {
        if (!gameIsRunning) {
            return;
        }

        this.numOfQuestions++;
        int answer = (Integer) v.getTag();

        int color;
        String message = "";

        if (answer == firstOperand + secondOperand) {
            this.correctAnswer++;
            message = getString(R.string.correct_answer);
            color = R.color.green;
        } else {
            message = getString(R.string.wrong_answer);
            color = R.color.red;
        }

        this.answerAnswer.setText(message);
        this.answerAnswer.setBackgroundColor(getResources().getColor(color));
        this.counter.setText(correctAnswer + " / " + numOfQuestions);

        if (gameIsRunning) {
            this.setQuestion();
        }
    }

    private void linkUI() {
        this.timer = findViewById(R.id.timer);
        this.question = findViewById(R.id.question);
        this.counter = findViewById(R.id.counter);
        this.answer1 = findViewById(R.id.answer1);
        this.answer2 = findViewById(R.id.answer2);
        this.answer3 = findViewById(R.id.answer3);
        this.answer4 = findViewById(R.id.answer4);
        this.answerAnswer = findViewById(R.id.answer_answer);
        this.play = findViewById(R.id.play);
    }

    private void generateQuestion() {
        Random random = new Random();
        firstOperand = random.nextInt(100) + 1;
        secondOperand = random.nextInt(100) + 1;
    }

    private int[] generateAnswer() {
        Random random = new Random();
        int[] answers = new int[4];

        answers[0] = firstOperand + secondOperand;

        for (int i = 1; i < 4; i++) {
            answers[i] = random.nextInt(100) + 1;
        }

        return answers;
    }

    private void setQuestion() {
        this.generateQuestion();
        String question = firstOperand + " + " + secondOperand;
        this.question.setText(question);
        this.setAnswer();
    }

    private void setAnswer() {
        int[] answer = this.generateAnswer();

        ArrayList<Integer> answerCell = new ArrayList<>(asList(1, 2, 3, 4));
        Collections.shuffle(answerCell);

        for (int i = 0; i < 4; i++) {
            int id = this.getResources().getIdentifier("answer" + answerCell.get(i), "id", getPackageName());
            TextView cell = findViewById(id);
            cell.setText(answer[i] + "");
            cell.setTag(answer[i]);
        }
    }
}
