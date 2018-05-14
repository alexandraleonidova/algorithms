/*
 * Elijah Grady and Alexandria Leonidova
 * COMP480 Algorithms - Dr. Glick - USD 2017
 * best case this algorithm runs in O(NM) time
 * worst case this algorithm runs in appx O(MN^3) time
 * average case somewhere around O(MN^2)
 */

// Imports
import static java.lang.System.exit;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.text.Collator;
import java.text.Format;
import java.text.Normalizer;
import java.text.StringCharacterIterator;

/*
 * This is a Sub Class for Dynamic Functionality
 * It acts as a Generator for the Built-In SubTable Class
 */
class SubTable {
    HashMap<Character, ArrayList<String>> table = new HashMap<Character, ArrayList<String>>();
}

// This is the main class of the program
// Class Declaration
public class parse {

    // Class Variable Declarations
    private HashMap<String, Character> multiplication_table = new HashMap<String, Character>();
    private SubTable[][] multiplication_sub_table;
    private int xxx;
    private char target_value;
    private String alphabet_string;
    private String input_string;

    // Class Constructor for parse.java
    // Initializes a HashMap, String, String, int, and char
    // Houses the method dynamic_solution which is used by main to solve the problem
    private parse(HashMap<String, Character> multiplication_table, String alphabet_string, String input_string, int xxx, char target_value) {

        this.multiplication_table = multiplication_table; //table that denotes legal transformations
        this.alphabet_string = alphabet_string; //string to denote possible characters in the alphabet
        this.input_string = input_string; //string to be parsed
        this.xxx = xxx; //xxx is the length of the alphabet ( n from the project description)
        this.target_value = target_value; //target transformation as defined by the test cases

        //construct the subtable and init with values from i to j
        multiplication_sub_table = new SubTable[xxx][xxx];
        for (int i=0;i<xxx;i++) {
            for (int j=0;j<xxx;j++) {
                multiplication_sub_table[i][j]=new SubTable();
            }
        }
    }

    /*
     * This function dynamically solves the problem of computing parenthesis possibilities
     * It does so by filling out a table and reusing the values that were previosly computated
     */
    private void dynamic_solution() {

        int temp_counter_1 = 1; // a count to go from 1 to the # of characters in the alphabet

        //take care of the base cases: when the string length is 1, the sub-solution is just character by itself
        for (int i=0;i<xxx;i++) {
            ArrayList<String> this_array_list=new ArrayList<>();
            //add characters to the list per iteration
            this_array_list.add(Character.toString(input_string.charAt(i)));
            multiplication_sub_table[i][i].table.put(input_string.charAt(i), this_array_list);
        }

        //for each character in the alphabet
        while (temp_counter_1 < xxx) {
            // i and j are pointers to start and end of substring used for generating sub-solution
            for (int i=0, j=temp_counter_1; i<xxx-temp_counter_1; i++,j++) {
                // reference the correct index in the loop
                int temp_counter_2=j-1;
                // for all iterations
                while (temp_counter_2>=i) {
                    // read from the hashmap
                    // the 4 nested for loops here are used to do the bulk of the work of the algorithm
                    for (Map.Entry<Character, ArrayList<String>> sub1 : multiplication_sub_table[i][temp_counter_2].table.entrySet()) {
                        for (String temp_string_1 : sub1.getValue()) {
                            for (Map.Entry<Character, ArrayList<String>> sub2 : multiplication_sub_table[temp_counter_2+1][j].table.entrySet()) {
                                for (String temp_string_2 : sub2.getValue()) {

                                    //sub strings to the left and right of the dividing point
                                    String temp_string_2_1=temp_string_2;
                                    String temp_string_1_1=temp_string_1;

                                    // add parenthesis around sub solutions if they are longer than one character
                                    if (temp_string_1.length()>1) {
                                        temp_string_1_1="("+temp_string_1+")";
                                    }
                                    if (temp_string_2_1.length()>1) {
                                        temp_string_2_1="("+temp_string_2+")";
                                    }

                                    // the sumation is calculated
                                    String temp_value_1=temp_string_1_1+temp_string_2_1;
                                    char temp_value_2=multiplication_table.get(Character.toString(sub1.getKey())+Character.toString(sub2.getKey()));
                                    // compare to value in table and see if a reducation or change can be made
                                    if (multiplication_sub_table[i][j].table.containsKey(temp_value_2)) {
                                        multiplication_sub_table[i][j].table.get(temp_value_2).add(temp_value_1);
                                    }
                                    else {
                                        // if a reducation can't be made then prepare for the next iteration
                                        ArrayList<String> this_array_list=new ArrayList<String>();
                                        this_array_list.add(temp_value_1);
                                        multiplication_sub_table[i][j].table.put(temp_value_2, this_array_list);
                                    }
                                }
                            }
                        }
                    }
                    // end by decrementing counters and looping until the entire string has been searched
                    temp_counter_2--;
                }
            }
            temp_counter_1++;
        }
    }


    /*
     * This is the subroutine or Helper Method for dynamic_solution
     * Enables the Main Method to call dynamic_solution and output_solution
     * Overrides the Built-In Comparator Class and Allows for Lexicographical Order
     *
     * @param output_string - a name of the output file
     */
    private void output_solution(String output_string) {
        // init the writer
        BufferedWriter outStream=null;
        // use try catch block to handle errors
        try {
            outStream=new BufferedWriter(new FileWriter(output_string));
            // the sub table should contain all the values that need to be ordered
            if (multiplication_sub_table[0][xxx-1].table.containsKey(target_value)) {
                multiplication_sub_table[0][xxx-1].table.get(target_value).sort(new Comparator<String>() {
                    // this is the case when there is at least one way to parenthesise the given string to get a target value
                    @Override
                    //override the comparator class to compare parenthesized strings
                    public int compare(String temp_string_counter_1, String temp_string_counter_2) {
                        if (temp_string_counter_1.charAt(0)=='(' && temp_string_counter_2.charAt(0)!='(') {
                            return 1;
                        }
                        else if (temp_string_counter_1.charAt(0)!='(' && temp_string_counter_2.charAt(0)=='(') {
                            return -1;
                        }
                        else if (temp_string_counter_1.length()>1 && temp_string_counter_2.length()>1) {
                            return compare(temp_string_counter_1.substring(1),temp_string_counter_2.substring(1));
                        }
                        else if (temp_string_counter_1.length()>1) {
                            return 1;
                        }
                        else {
                            return -1;
                        }
                    }
                });
                //write solutions to the file
                for (String value : multiplication_sub_table[0][xxx-1].table.get(target_value)) {
                    outStream.write(value + "\n");
                }
            }
            else {
                // This is the case when no parenthesising combo results in a target character
                outStream.write("not possible!\n");
            }
            outStream.close();
        }
        // dont forget to handle the exception
        catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    /*
     * This is the Main Method
     * It makes the call to dynamic_solution
     * It makes the call to output_solution
     */
    public static void main(String[] args) {

        if (args.length != 2) { // case when user did not provide input and output files
            System.out.println("enter input and output file names");
            return;
        }

        // otherwise read input from the user
        Scanner input = null;
        try { //open input file
            input = new Scanner(new File(args[0]));
        }
        catch (IOException e) {
            System.out.println("file not found " + args[0]);
            exit(1);
        }

        // get the data from the opened input file
        String alphabet_string = input.nextLine();
        int aLength = alphabet_string.length();

        // initialize the data from user into a hash map
        HashMap<String, Character> multiplication_table = new HashMap<String, Character>();
        //fill in the multiplication table
        for (int i = 0; i < aLength; i++) {
            String nextLine = input.nextLine();
            for (int j = 0; j < aLength; j++) {
                multiplication_table.put(Character.toString(alphabet_string.charAt(i)) + Character.toString(alphabet_string.charAt(j)), nextLine.charAt(j));
            }
        }

        // read in the input string from the test cases
        String input_string = input.nextLine();
        // read in the target from the test cases
        String targetLine = input.nextLine();
        // read in the target value from the targetLine
        char target_value = targetLine.charAt(0);

        //parse the data read from input file
        parse this_iteration = new parse(multiplication_table, alphabet_string, input_string, input_string.length(), target_value);

        // dynamically create all possible combinations of parenthesis to meet the targer value
        this_iteration.dynamic_solution();

        //write the result to the output file
        this_iteration.output_solution(args[1]);
    }
}
