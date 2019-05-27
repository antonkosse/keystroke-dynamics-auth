package Test;

import java.util.ArrayList;

public class Test {
//    studentСoefficientFromTable
    private  static  double[]  STUDENT_COEFFICIENT_FROM_TABLE = new double[]{6.313752,2.919986,2.353363,2.131847,2.015048,
            1.943180,1.894579,1.859548,1.833113,1.812461,1.795885,1.782288,1.770933,1.761310,1.753050};
    private static ArrayList<Double> mathExpectaion = new ArrayList<>();
    private static ArrayList<Double> arrayForDispersion = new ArrayList<>();
    private static ArrayList<Double> calculatedStudentСoefficient = new ArrayList<>();
    private static ArrayList<Double> Tmin = new ArrayList<>();
    private static ArrayList<Double> Tmax = new ArrayList<>();

    public static ArrayList<Double> getTmin() {
        return Tmin;
    }

    public static ArrayList<Double> getTmax() {
        return Tmax;
    }

    public static ArrayList<ArrayList<Long>> getStudentNumberForEveryInterval(ArrayList<ArrayList<Long>>  timeINtervals, int passwordLen)
    {
//        int n = 8;
        int n = passwordLen - 2;
        int counterOfReplaces = 0;
        for(int i = 0; i <passwordLen - 1; i++)
        {
            ArrayList<Long> elementsAtIndexes = extractElementsAtIndex(timeINtervals,i);
            long backUpElemnt;
            for(int j = 0; j < elementsAtIndexes.size(); j++)
            {
                ArrayList<Long> tmpValue = new ArrayList<>(elementsAtIndexes);
                backUpElemnt = tmpValue.get(j);
                tmpValue.remove(j);
                long sum = 0;
                for (Long aTmpValue : tmpValue) {
                    sum += aTmpValue;
                }
                double mathExpectation = (double) sum / (double) n;
                mathExpectaion.add(mathExpectation);
                double dispersion = Math.sqrt(calcDispersion(mathExpectation,tmpValue,passwordLen,false));
                arrayForDispersion.add(dispersion);
                double studentKoef = Math.abs((backUpElemnt - mathExpectation)/dispersion);
                calculatedStudentСoefficient.add(studentKoef);
                if(studentKoef > STUDENT_COEFFICIENT_FROM_TABLE[7+1])
                {
                    timeINtervals.get(j).set(i,-1L);
                    counterOfReplaces++;
                }
            }
        }
        System.out.println(" Replaces " + counterOfReplaces);
        for (ArrayList<Long> value:timeINtervals) {
            System.out.println(value.toString());
        }
        System.out.println("Math expectation: " + mathExpectaion.toString());
        System.out.println("Dispersion: " + arrayForDispersion.toString());
        System.out.println("Student koeff: " + calculatedStudentСoefficient.toString());
        return timeINtervals;
    }

    public static double calcDispersion(double mathExpecation, ArrayList<Long> intervals,int passwordLen, boolean isCalculatingWithMistakes)
    {
        if(!isCalculatingWithMistakes)
        {
            int n = passwordLen - 3;
            double sum = 0;
            for (Long value: intervals) {
                sum +=Math.pow ((value.doubleValue() - mathExpecation),2);
            }
            return sum / n;
        }
        else
        {
            double sum = 0;
            int sizeOfArray = intervals.size();
            for (Long value: intervals) {
                if(value == -1)
                    sizeOfArray--;
                else
                    sum +=Math.pow ((value.doubleValue() - mathExpecation),2);
            }

            return sum/sizeOfArray;
        }

    }

    public static ArrayList<Long> extractElementsAtIndex(ArrayList<ArrayList<Long>> intervals, int index)
    {
        ArrayList<Long> arrayOfElementsAtIndex = new ArrayList<>();
        for (ArrayList<Long> value:
                intervals) {
            arrayOfElementsAtIndex.add(value.get(index));
        }

        return arrayOfElementsAtIndex;
    }
    //Do again math expectation but for every column not for every element in column
    //Same with dispersion
    //If in column there is -1 we ignore this element and number of elements column.length - 1
    //Than we count tmin tmax where we calculate Student koeff and where L - length of column(without -1) - 1 (to get value from the table)
    public static void calcMathExpectationWithFixedMistakes(ArrayList<ArrayList<Long>> timeIntervals, int password)
    {
        ArrayList<Double> tempExpectation = new ArrayList<>();
        ArrayList<Double> tempDispersion = new ArrayList<>();
        for(int i = 0; i < password - 1; i++)
        {
            ArrayList<Long> elementsAtIndex = extractElementsAtIndex(timeIntervals,i);
            int sizeOfArray = elementsAtIndex.size();
            long sum = 0;
            for (Long value:elementsAtIndex) {
                if(value == -1)
                    sizeOfArray--;
                else
                    sum+=value;
            }
            double mathExpectation = (double) sum/(double)(sizeOfArray);
            tempExpectation.add(mathExpectation);
            double dispersion = Math.sqrt(calcDispersion(mathExpectation,elementsAtIndex,password,true));
            tempDispersion.add(dispersion);
            System.out.println("Coefficient: " +STUDENT_COEFFICIENT_FROM_TABLE[sizeOfArray - 1] + "for length: " + sizeOfArray);
            double minTime = mathExpectation - STUDENT_COEFFICIENT_FROM_TABLE[sizeOfArray - 1]*dispersion;
            Tmin.add(minTime);
            double maxTime = mathExpectation + STUDENT_COEFFICIENT_FROM_TABLE[sizeOfArray - 1]*dispersion;
            Tmax.add(maxTime);
        }
        System.out.println("Down time border: " + Tmin.toString());
        System.out.println("Up time border: " + Tmax.toString());
        System.out.println("Temporary math expectation: " + tempExpectation.toString());
        System.out.println("Temporary math dispersion: " + tempDispersion.toString());
    }
}
