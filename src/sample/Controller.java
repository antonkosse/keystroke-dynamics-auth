package sample;

import Test.Test;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import java.util.*;



public class Controller {

//    @FXML

    //    private Label simpleLabel;
    private HashMap<KeyCode, Long> holdDurationForEveryKey = new HashMap<>();
    private ArrayList<Long> upDownDuration = new ArrayList<>();
    private ArrayList<Long> keyEventedTime = new ArrayList<>();
    private ArrayList<ArrayList<Long>> statisticsForTimeIntervals = new ArrayList<>();
    private int counterForTraining = 0;

    private String password = "biometrics";
    @FXML
    private Button trainBtn;

    @FXML
    private Button okBtn;

    @FXML
    private  TextArea txtArea;


    @FXML
    void initialize()
    {

        if(counterForTraining < 10)
            okBtn.setDisable(true);

        txtArea.setOnKeyPressed(event -> {
            long timePressed = System.currentTimeMillis();
            holdDurationForEveryKey.put(event.getCode(),timePressed);
            keyEventedTime.add(System.currentTimeMillis());

        });

        txtArea.setOnKeyReleased(event
                -> {
            long durationTime = System.currentTimeMillis() - holdDurationForEveryKey.get(event.getCode());
            holdDurationForEveryKey.replace(event.getCode(),durationTime);
            keyEventedTime.add(System.currentTimeMillis());
        });

        okBtn.setOnAction(event -> {
            System.out.println(keyEventedTime.toString());
            if(keyEventedTime.size() != 0 && txtArea.getText().trim().equals(password))
            {
                int mistakeCounter = 0;
                ArrayList<Long> checkAuth = properCountOfTimeIntervals(keyEventedTime);
                for(int i = 0; i < checkAuth.size(); i++)
                {
                    if(checkAuth.get(i) < Test.getTmin().get(i) || checkAuth.get(i) > Test.getTmax().get(i))
                        mistakeCounter++;
                }

                System.out.println(mistakeCounter);
            }
            keyEventedTime.clear();
            txtArea.clear();

        });
        trainBtn.setOnAction(event -> {
            System.out.println(counterForTraining);
            if(txtArea.getText().trim().equals(password))
            {
                ArrayList<Long> timeIntervals = properCountOfTimeIntervals(keyEventedTime);
                if(keyEventedTime.size() != 0)
                {
                    statisticsForTimeIntervals.add(timeIntervals);
                }
                txtArea.clear();
                if(counterForTraining == 10) {

                    for (ArrayList<Long> value:statisticsForTimeIntervals) {
                        System.out.println(value.toString());
                    }
                    ArrayList<ArrayList<Long>> timeIntervalsWithFixedMistakes = Test.getStudentNumberForEveryInterval(statisticsForTimeIntervals, 10);
                    Test.calcMathExpectationWithFixedMistakes(timeIntervalsWithFixedMistakes,password.length());
                    statisticsForTimeIntervals.clear();
                    trainBtn.setDisable(true);
                    okBtn.setDisable(false);
                } else {
                    counterForTraining++;
                }
                txtArea.requestFocus();
                keyEventedTime.clear();
            }
            else
            {
                System.out.println("Wrong password, try again");
                txtArea.clear();
            }
        });
    }

    public ArrayList<Long> properCountOfTimeIntervals(ArrayList<Long> allIntervals)
    {
        ArrayList<Long> timeIntervals = new ArrayList<>();
        Collections.sort(allIntervals);
        for(int i = 0; i < 2*(txtArea.getLength() - 1) - 1; i++)
        {
            if(i % 2 == 0) {
                timeIntervals.add(allIntervals.get(i + 2) - allIntervals.get(i + 1));
            }
        }

        return timeIntervals;
    }


}
