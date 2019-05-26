package sample;

import Test.Test;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private HashMap<Integer,Integer> mapForNumberOfMistakes = new HashMap<>();
    private String password = "biometrics";
    @FXML
    private Button trainBtn;

    @FXML
    private Button okBtn;

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void initialize()
    {
        mapForNumberOfMistakes.put(6,2);
        mapForNumberOfMistakes.put(7,2);
        mapForNumberOfMistakes.put(8,3);
        mapForNumberOfMistakes.put(9,3);
        mapForNumberOfMistakes.put(10,3);
        mapForNumberOfMistakes.put(11,4);
        mapForNumberOfMistakes.put(12,4);

        if(counterForTraining < 10)
            okBtn.setDisable(true);

        passwordField.setOnKeyPressed(event -> {
            long timePressed = System.currentTimeMillis();
            if(!event.getCode().getName().equals("Enter") || !event.getCode().getName().equals("Tab") ||!event.getCode().getName().equals("Backspace")) {
                holdDurationForEveryKey.put(event.getCode(),timePressed);
                keyEventedTime.add(System.currentTimeMillis());
            }
            if(event.getCode().getName().equals("Enter")) {
                if(!trainBtn.isDisabled()) {
                    trainBtn.fire();
                } else if(!okBtn.isDisabled()) {
                    okBtn.fire();
                }
            }

        });

        passwordField.setOnKeyReleased(event
                -> {
            if(!event.getCode().getName().equals("Enter") || !event.getCode().getName().equals("Tab") ||!event.getCode().getName().equals("Backspace"))
            {
                long durationTime = System.currentTimeMillis() - holdDurationForEveryKey.get(event.getCode());
                holdDurationForEveryKey.replace(event.getCode(),durationTime);
                keyEventedTime.add(System.currentTimeMillis());
            }

        });

        okBtn.setOnAction(event -> {
            if(keyEventedTime.size() != 0 && passwordField.getText().trim().equals(password))
            {
                int mistakeCounter = 0;
                ArrayList<Long> checkAuth = properCountOfTimeIntervals(keyEventedTime);
                for(int i = 0; i < checkAuth.size(); i++)
                {
                    if(checkAuth.get(i) < Test.getTmin().get(i) || checkAuth.get(i) > Test.getTmax().get(i))
                        mistakeCounter++;
                }
                if(mistakeCounter <= mapForNumberOfMistakes.get(passwordField.getLength()))
                {
                    System.out.println("Congrats");
                    alertSystem("Authentication succeed","Congratulations");
                }

                else
                {
                    System.out.println("Oi oi, you cheeky wanker");
                    alertSystem("Authentication failed","Oi oi, you cheeky wanker. Wanna tussle?");
                }

            }
            keyEventedTime.clear();
            passwordField.clear();

        });
        trainBtn.setOnAction(event -> {
            System.out.println(counterForTraining);
            if(passwordField.getText().trim().equals(password))
            {
                ArrayList<Long> timeIntervals = properCountOfTimeIntervals(keyEventedTime);
                if(keyEventedTime.size() != 0)
                {
                    statisticsForTimeIntervals.add(timeIntervals);
                }
                passwordField.clear();
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
                passwordField.requestFocus();
                keyEventedTime.clear();
            } else {
                System.out.println("Wrong password, try again");
                passwordField.clear();
            }
        });
    }

    public ArrayList<Long> properCountOfTimeIntervals(ArrayList<Long> allIntervals)
    {
        ArrayList<Long> timeIntervals = new ArrayList<>();
        Collections.sort(allIntervals);
        for(int i = 0; i < 2*(passwordField.getLength() - 1) - 1; i++)
        {
            if(i % 2 == 0) {
                timeIntervals.add(allIntervals.get(i + 2) - allIntervals.get(i + 1));
            }
        }

        return timeIntervals;
    }

    public void alertSystem(String title, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
