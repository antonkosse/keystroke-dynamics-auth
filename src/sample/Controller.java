package sample;

import Test.Test;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.util.*;



public class Controller {

    private HashMap<KeyCode, Long> holdDurationForEveryKey = new HashMap<>();
    private ArrayList<Long> upDownDuration = new ArrayList<>();
    private ArrayList<Long> keyEventedTime = new ArrayList<>();
    private ArrayList<ArrayList<Long>> statisticsForTimeIntervals = new ArrayList<>();
    private int counterForTraining = 0;
    private HashMap<Integer,Integer> mapForNumberOfMistakes = new HashMap<>();
    private String password = "biometrics";
    private boolean isUserAlreadyRegistered = false;

    @FXML
    private Button trainBtn;

    @FXML
    private Button okBtn;

    @FXML
    private Button checkBtn;

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;


    @FXML
    void initialize()
    {
        mapForNumberOfMistakes.put(5,2);
        mapForNumberOfMistakes.put(6,2);
        mapForNumberOfMistakes.put(7,2);
        mapForNumberOfMistakes.put(8,3);
        mapForNumberOfMistakes.put(9,3);
        mapForNumberOfMistakes.put(10,3);
        mapForNumberOfMistakes.put(11,4);
        mapForNumberOfMistakes.put(12,4);
        passwordField.setVisible(false);
        trainBtn.setDisable(true);

        if(counterForTraining < 10)
            okBtn.setDisable(true);

        checkBtn.setOnAction(event->{
            if(searchForUser(userField.getText().trim()).length != 0 && userField.getLength() != 0)
            {
                System.out.println(searchForUser(userField.getText().trim()).length);
                System.out.println(System.getProperty("user.dir"));
                counterForTraining = 10;
                isUserAlreadyRegistered = true;
                System.out.println(isUserAlreadyRegistered);
                okBtn.setDisable(false);
            }
            else
            {
                trainBtn.setDisable(false);
            }
            passwordField.setVisible(true);
            checkBtn.setDisable(true);
        });

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
                ArrayList<ArrayList<Double>> timeBorders =readDataFromFile(userField.getText().trim());
                ArrayList<Double> tMin = timeBorders.get(0);
                ArrayList<Double> tMax = timeBorders.get(1);
                System.out.println("Min border: " + tMin.toString());
                System.out.println("Max border: " + tMax.toString());
                for(int i = 0; i < checkAuth.size(); i++)
                {
                    if(checkAuth.get(i) < tMin.get(i) || checkAuth.get(i) > tMax.get(i))
                        mistakeCounter++;
                }
                System.out.println("Number of mistakes: " + mistakeCounter);
                if(mistakeCounter <= mapForNumberOfMistakes.get(passwordField.getLength()))
                {
                    System.out.println("Congrats");
                    alertSystem("Authentication succeed","Congratulations");
                } else {
                    System.out.println("Oi oi, you cheeky wanker");
                    alertSystem("Authentication failed","Oi oi, you cheeky wanker. Wanna tussle?");
                }

            }
            keyEventedTime.clear();
            passwordField.clear();

        });
        trainBtn.setOnAction(event -> {
            System.out.println(counterForTraining);

           if(passwordField.getText().trim().equals(password) && !isUserAlreadyRegistered)
            {
                ArrayList<Long> timeIntervals = properCountOfTimeIntervals(keyEventedTime);
                if(keyEventedTime.size() != 0)
                {
                    statisticsForTimeIntervals.add(timeIntervals);
                }
                passwordField.clear();
                if(counterForTraining == 10) {
                    for (ArrayList<Long> value:statisticsForTimeIntervals)
                    {
                        System.out.println(value.toString());
                    }
                    ArrayList<ArrayList<Long>> timeIntervalsWithFixedMistakes = Test.getStudentNumberForEveryInterval(statisticsForTimeIntervals, password.length());
                    Test.calcMathExpectationWithFixedMistakes(timeIntervalsWithFixedMistakes,password.length());
                    statisticsForTimeIntervals.clear();
                    trainBtn.setDisable(true);
                    okBtn.setDisable(false);
                    writeDataInFiles(Test.getTmin(),Test.getTmax(),userField.getText().trim());

                    } else {
                        counterForTraining++;
                    }
                    passwordField.requestFocus();
                    keyEventedTime.clear();
            } else if(!passwordField.getText().trim().equals(password)){
                System.out.println("Wrong password, try again");
                passwordField.clear();
            } else
                okBtn.setDisable(false);

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

    public void writeDataInFiles(ArrayList<Double> minTime, ArrayList<Double> maxTime, String user)
    {
        try {
            FileOutputStream fos = new FileOutputStream(user + ".out");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            ArrayList<ArrayList<Double>> minAndMaxTime = new ArrayList<>();
            minAndMaxTime.add(minTime);
            minAndMaxTime.add(maxTime);
            oos.writeObject(minAndMaxTime);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<ArrayList<Double>> readDataFromFile(String user)
    {
        try{
            FileInputStream fis = new FileInputStream(user + ".out");
            ObjectInputStream oin = new ObjectInputStream(fis);
            ArrayList<ArrayList<Double>> minMaxTime =(ArrayList<ArrayList<Double>>) oin.readObject();
            return minMaxTime;
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;

    }


    public File[] searchForUser(String user)
    {

        File f = new File(System.getProperty("user.dir"));
        return f.listFiles((dir, name) -> name.startsWith(user) && name.endsWith("out"));
    }

}
