/*
 * Alexandra Leonidova
 * Algorithms (COMP 480)
 * Project 1
 * 9 Oct 2017
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.io.*;
import java.lang.Math;
import java.lang.Byte;


public class psychicoptimal{
    private static final int Object = 0;
    private static final int HashSet = 0;
    
    public static void main(String[] args) {
        
        String inputFileName, outputFileName;
        
        inputFileName = args[0];
        outputFileName = args[1];
        
        BufferedReader input_file_stream = null;
        
        int n, j, k, l;
        String line;
        String[] read_arguments = new String[3];
        
        // open the input file
        try {
            input_file_stream = new BufferedReader(new FileReader(inputFileName));
        } catch (IOException e) {
            System.out.println("Error opening file");
        }
        
        //read from input file
        try {
            
            line = input_file_stream.readLine();
            read_arguments = line.split(" ");
            
        } catch (IOException e) {
            System.out.println("Error reading from file.");
        }
        
        n = Integer.parseInt(read_arguments[0]); //number or candidate values
        j = Integer.parseInt(read_arguments[1]); //number of correct numbers
        k = Integer.parseInt(read_arguments[2]); //number of numbers per ticket
        l = Integer.parseInt(read_arguments[3]); //number of numbers needed to win
        
        if (j < l)
        {
            System.out.println("Psycic did not give you enouph correct numbers. You have to buy all tickets");
            System.exit(0);
        }
        
        // close the input file
        try {
            input_file_stream.close();
        } catch (IOException e) {
            System.out.println("Error closing Buffered Reader.");
            
        }
        
        //create a set of all possible tickets
        Set<Set<Object>> all_number_subset = createSubsets(n, k);
        //create a set of all possible winning combos
        Set<Set<Object>> winning_number_subset = createSubsets(n, j);
        
        Object[] all_number_subset_array = all_number_subset.toArray();
        Object[] winning_number_subset_array = winning_number_subset.toArray();
        
        //create a set of all possible ticket combos (indices of tickets, not tickets themselves)
        Set<Set<Object>> ticket_indices_powerset = createPowerset(all_number_subset_array.length);
        Object[] ticket_indices_powerset_array = winning_number_subset.toArray();
        
        Object[] tickets_to_buy = ticket_combo(l, ticket_indices_powerset, all_number_subset_array, winning_number_subset_array);
        int[] tickets_to_buy_sorted = sort_tickets(tickets_to_buy, all_number_subset_array, k);
        saveToOutputFile(tickets_to_buy_sorted, outputFileName, k);
        
        
    }
    
    /*
     * This methods sorts the array of tickets that user should buy
     *
     * @param tickets_to_buy_indices - indices of tickets that the user should buy
     * @param all_number_subset_array - an array of all possible tickets
     * @param k - number of numbers on each ticket
     *
     * @return tickets_to_buy_sorted - an sorted array of sorted tickets
     */
    public static int[] sort_tickets(Object[] tickets_to_buy_indices, Object[] all_number_subset_array, int k)
    {
        Set<Object> curr_t = new HashSet<Object>(); //carries one ticket
        int[] tickets_to_buy_sorted = new int[tickets_to_buy_indices.length];
        int curr_ticket_int = 0;
        double multiplyer = Math.pow(10, k-1);
        int array_int_i = 0;
        
        for(Object index : tickets_to_buy_indices) {
            curr_t = (java.util.HashSet<java.lang.Object>) all_number_subset_array[(int) index - 1];
            Object[] curr_t_array = curr_t.toArray();
            for(int j = 0 ; j < k; j ++)
            {
                curr_ticket_int += ((Integer)curr_t_array[j]).intValue() * multiplyer;
                multiplyer /= 10;
            }
            tickets_to_buy_sorted[array_int_i] = curr_ticket_int;
            curr_ticket_int= 0;
            multiplyer = Math.pow(10, k-1);
            array_int_i++;
            
        }
        Arrays.sort(tickets_to_buy_sorted);
        return tickets_to_buy_sorted;
    }
    
    /*
     * This method chooses the combination tickets that a user should by using a brute force approach
     *
     * @param l - number of numbers needed to win
     * @param ticket_indices_powerset - all possible ticket combinations (contains ticket indices)
     * @param all_number_subset_array - an array of all possible tickets
     * @param winning_number_subset_array - an array of all possible winning combinations
     *
     * @return final_ticket_indeces - indices of tickets that the user should buy
     */
    public static Object[] ticket_combo(int l, Set<Set<Object>> ticket_indices_powerset, Object[] all_number_subset_array, Object[] winning_number_subset_array) {
        
        Set<Object> curr_t = new HashSet<Object>(); //carries one ticket
        Set<Object> set_to_buy = new HashSet<Object>(); //carries one winning combo
        Set<Object> curr_win_combo = new HashSet<Object>(); //carries one ticket combo
        
        boolean has_winning = false;
        int match_count = 0;
        int ticket_amount_count = all_number_subset_array.length; //smallest amount of tickets needed so far
        
        // a combo to possibly buy
        for(Set<Object> curr_indices_subset : ticket_indices_powerset) {
            int curr_num_tickets_to_buy = curr_indices_subset.toArray().length;
            if(ticket_amount_count <= curr_num_tickets_to_buy && has_winning)
            {
                continue;
            }
            //copy a winning array
            Object[] winning_number_subset_array_copy = Arrays.copyOf(winning_number_subset_array, winning_number_subset_array.length);
            //setup a counter of uncovered winning combos
            int uncovered_winning_possibilities = winning_number_subset_array_copy.length;
            Object[] curr_indices_subset_array = curr_indices_subset.toArray();
            
            //take each ticket in a combo
            for(Object curr_index : curr_indices_subset_array) {
                int curr_index_int = ((Integer) curr_index).intValue() - 1;
                curr_t = (java.util.HashSet<java.lang.Object>) all_number_subset_array[curr_index_int];
                
                //see which winning combos you can cross out
                for(int i = 0; i < winning_number_subset_array_copy.length; i++) {
                    match_count = 0;
                    curr_win_combo = (java.util.HashSet<java.lang.Object>) winning_number_subset_array[i];
                    if (winning_number_subset_array_copy[i] instanceof Integer)
                    {
                        continue;
                    }
                    
                    for(Object combo_num : curr_win_combo) {
                        for(Object ticket_num : curr_t) {
                            if(((Integer)combo_num).intValue() == ((Integer)ticket_num).intValue())
                            {
                                match_count++;
                            }
                        }
                    }
                    if(match_count >= l)
                    {
                        uncovered_winning_possibilities--;
                        winning_number_subset_array_copy[i] = 0;
                    }
                }
            }
            //after ticket combo loop
            if((uncovered_winning_possibilities == 0) && (!has_winning || (curr_num_tickets_to_buy < ticket_amount_count)))
            {
                has_winning = true;
                ticket_amount_count = curr_num_tickets_to_buy;
                set_to_buy = curr_indices_subset;
                
            }
        }
        Object[] final_ticket_indeces = set_to_buy.toArray();
        return final_ticket_indeces;
    }
    
    
    /*
     * This method generates all subsets of k numbers from the candidate set.
     * @param n -  the number of candidate values supplied by the psychic
     * @param k - the size of each subset in a set
     * @return - subsets of k numbers from the candidate set (all possible tickets)
     */
    public static Set<Set<Object>> createSubsets(int n, int k) {
        Set<Set<Object>> power_set = new HashSet<Set<Object>>(); //power set
        Set<Set<Object>> set_of_subsets = new HashSet<Set<Object>>(); //only "tickets" set
        List<Object> set_list = new ArrayList<Object>(); //set
        
        double power_set_size;
        int mask;
        
        //determine the size of a power set
        power_set_size = Math.pow(2, n);
        
        //create a list containing all numbers from 1 to n
        for (int i = 0; i < n; i++) {
            set_list.add(i+1);
        }
        
        //create a powerset based on the set
        for(int i = 0; i < power_set_size; i++){
            
            Set<Object> subset = new HashSet<Object>();
            mask =  1;
            
            for(int j = 0; j < n; j++){
                
                if((mask & i) != 0){
                    subset.add(set_list.get(j));
                }
                mask = mask << 1;
            }
            power_set.add(subset);
        }
        
        //pick all the subsets from a power set that are size k
        for(Set<Object> curr_subset : power_set) {
            if(curr_subset.size() == k) {
                set_of_subsets.add(curr_subset);
            }
        }
        return set_of_subsets;
    }
    
    
    /*
     * This method generates a powerset over n
     * @param n -  the base of a powerset
     * @return - powerset with a base n
     */
    public static Set<Set<Object>> createPowerset(int n) {
        Set<Set<Object>> power_set = new HashSet<Set<Object>>(); //power set
        List<Object> set_list = new ArrayList<Object>(); //set
        
        double power_set_size;
        int mask;
        
        //determine the size of a power set
        power_set_size = Math.pow(2, n);
        
        //create a list containing all numbers from 1 to n
        for (int i = 0; i < n; i++) {
            set_list.add(i+1);
        }
        
        //create a powerset based on the set
        for(int i = 0; i < power_set_size; i++){
            
            Set<Object> subset = new HashSet<Object>();
            mask =  1;
            
            for(int j = 0; j < n; j++){
                
                if((mask & i) != 0){
                    subset.add(set_list.get(j));
                }
                mask = mask << 1;
            }
            power_set.add(subset);
        }
        return power_set;
    }
    
    
    
    /*
     * This method saves the result to the output file
     *
     * @param tickets_array - array of the tickets that a user should buy to have at least 1 winning ticket
     * @param outputFileName - string containing the name of the output file where weight_matrix should be saved
     */
    public static void saveToOutputFile(int[]tickets_array, String outputFileName, int k) {
        
        PrintWriter fileOutput = null;
        
        // PrintWriter forces handling of the FileNotFoundException, and FileWriter forces
        // handling of IOException, which includes FileNotFoundException, so
        // only IOException needs to be handled.
        try {
            fileOutput = new PrintWriter(new FileWriter(outputFileName));
        } catch (IOException e) {
            
            System.out.println("PrintWriter IOException occured when trying to save weight matrix data.");
            
        }
        
        // save number of tickets
        fileOutput.println(tickets_array.length);
        
        //save the tickets
        
        double multiplyer = Math.pow(10, k-1);
        double curr = 0;
        int number = 0;
        for (int ticket : tickets_array)
        {
            curr = ticket;
            while(curr / multiplyer > 0)
            {
                number = (int)(curr / multiplyer);
                fileOutput.print(number + " ");
                curr = ticket % multiplyer;
                multiplyer /= 10;
                
            }
            fileOutput.println("");
            multiplyer = Math.pow(10, k-1);
        }
        
        // flush PrintWriter
        fileOutput.flush();
        
        // close file
        fileOutput.close();
        
    }
    
    /*
     * This method reads the empty line from a file
     *
     * @param input_file_stream - the name of the BufferedReader being used
     */
    public static void readEmptyLine(BufferedReader input_file_stream) {
        try {
            input_file_stream.readLine();
        } catch (IOException e) {
            System.out.println("Error reading empty line from stream " + input_file_stream.toString());
        }
    }
    
}
