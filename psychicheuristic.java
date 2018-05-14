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


public class psychicheuristic{
    
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
        
        //create a set of all possible winning combos
        Set<Set<Object>> winning_number_subset = createSubsets(n, j);
        Object[] winning_number_subset_array = winning_number_subset.toArray();
        
        Object[] tickets_to_buy = buy_tickets(winning_number_subset_array, n, j, k, l);
        int[][] tickets_to_buy_sorted = sort_tickets(tickets_to_buy, k);
        saveToOutputFile(tickets_to_buy_sorted, outputFileName);
    }
    
    /*
     * This methods sorts the tickets that user should buy
     *
     * @param tickets_to_buy - an array of tickets to be sorted
     * @param k - a number of numbers on each ticket
     *
     * @return sorted_array - a sorted array of sorted tickets
     */
    public static int[][] sort_tickets(Object[] tickets_to_buy, int k)
    {
        int m = tickets_to_buy.length;
        int[][] sorted_array = new int[m][k];
        int sorted_array_i = 0;
        double multiplyer = Math.pow(10, k-1);
        int curr_ticket_int = 0;
        
        //create a 2D array of int, where each ticket is already sorted
        for(Object curr : tickets_to_buy)
        {
            Object[] curr_array = ((Set<Object>)curr).toArray();
            Arrays.sort(curr_array);
            for (int sorted_array_j = 0; sorted_array_j < k; sorted_array_j++)
            {
                sorted_array[sorted_array_i][sorted_array_j] = (int)curr_array[sorted_array_j];
            }
            sorted_array_i++;
        }
        
        //now sort the tickets
        if (tickets_to_buy.length != 1)
        {
            java.util.Arrays.sort(sorted_array, new java.util.Comparator<int[]>() {
                public int compare(int[] a, int[] b) {
                    return Double.compare(a[0], b[0]);
                }
            });
        }
        
        return sorted_array;
    }
    
    
    /*
     * This method finds a set of tickets that a user should buy using a heuristic
     * Heuristics idea: while there are spots on a ticket, add a non covered winning combo to it,
     * followed by a most popular number among not yet covered winning combos
     *
     * @param winning_number_subset_array - an array containing all possible winning combos
     * @param n - number or candidate values
     * @param j - number of correct numbers
     * @param k - number of numbers per ticket
     * @param l - number of numbers needed to win
     *
     * @return tickets_to_buy_array - an array of tickets a user should buy to have at least 1 winning tickets
     */
    public static Object[] buy_tickets(Object[] winning_number_subset_array, int n, int j, int k, int l)
    {
        Set<Set<Object>> tickets_to_buy = new HashSet<Set<Object>>();
        Set<Object> current_winning_combo = new HashSet<Object>();
        Object[] current_winning_combo_array = new Object[j];
        
        int[] current_ticket = new int[k];
        int ticket_spot_count = 0;
        int uncovered_combos = winning_number_subset_array.length;
        
        int ticket_count = 0;
        boolean can_cross_out = false;
        int count_matches = 0;
        
        
        int[] current_ticket_max_approach = new int[k];
        int[] numbers_left = fill_number_left(n, winning_number_subset_array);
        
        while(uncovered_combos > 0)
        {
            ticket_count++;
            // generate a new ticket
            // Heuristics idea: while there are spots on a ticket, add a non covered winning combo to it,
            // followed by a most popular number among not yet covered winning combos
            ticket_spot_count = 0;
            while(ticket_spot_count < k)
            {
                //check if you are done
                //case: all combos are covered, but there are empty spots on a ticket
                if(uncovered_combos <= 0)
                {
                    while(ticket_spot_count < k)
                    {
                        current_ticket[ticket_spot_count] = fill_in_with_random(current_ticket, n);
                        ticket_spot_count++;
                    }
                    break;
                }
                int i = 0;
                while (winning_number_subset_array[i] instanceof Integer)
                {
                    i++;
                }
                
                // *** add a non-covered winning combo to a ticket if there is enouph space *** //
                //get an uncovered winning combo
                if((k - ticket_spot_count) >= l)
                {
                    current_winning_combo = (Set<java.lang.Object>) winning_number_subset_array[i];
                    i = 0;
                    current_winning_combo_array = current_winning_combo.toArray();
                    //add enough numbers to a ticket to cover that combo
                    int count_matches_to_cover = 0;
                    for(int z = 0; z < current_winning_combo_array.length; z++)
                    {
                        if(ticket_spot_count < k && count_matches_to_cover < l)
                        {
                            int number = (int)current_winning_combo_array[z];
                            boolean already_found_flag = already_in_ticket(current_ticket, number); //returns -1 if the value is not yet in a ticket
                            if(already_found_flag)
                            {
                                count_matches_to_cover++;
                            }
                            //add a value to a ticket if it is not yet there
                            else
                            {
                                int number_to_add = (int)current_winning_combo_array[z];
                                current_ticket[ticket_spot_count] = number_to_add;
                                count_matches_to_cover++;
                                ticket_spot_count++;
                            }
                        }
                    }
                    uncovered_combos = cross_out(numbers_left, winning_number_subset_array, current_ticket, uncovered_combos, j, k, l);
                }
                
                // *** add a most popular number among not yet covered winning combos if there is space*** //
                if (ticket_spot_count < k)
                {
                    int[] maxes_array = array_of_max(numbers_left);
                    if (maxes_array[0] < 0) //all nub=mbers are covered
                    {
                        current_ticket[ticket_spot_count] = fill_in_with_random(current_ticket, n);
                        ticket_spot_count++;
                    }
                    else
                    {
                        boolean max_found = false;
                        for(int max_at = 0;max_at < maxes_array.length; max_at++)
                        {
                            boolean max_already_in_ticket = already_in_ticket(current_ticket, (maxes_array[max_at] + 1));
                            int debug = maxes_array[max_at] + 1;
                            if(max_found) //you aqlready have a number to add
                            {
                                break;
                            }
                            else if (max_already_in_ticket) //need another number : this one is already in a ticket
                            {
                                continue;
                            }
                            else //found a good choise, add this number
                            {
                                max_found = true;
                                int most_popular_number_index = maxes_array[max_at];
                                current_ticket[ticket_spot_count] = most_popular_number_index + 1;
                                ticket_spot_count++;
                            }
                        }
                        if(!max_found)
                        {
                            current_ticket[ticket_spot_count] = fill_in_with_random(current_ticket, n);
                            ticket_spot_count++;
                        }
                        uncovered_combos = cross_out(numbers_left, winning_number_subset_array, current_ticket, uncovered_combos, j, k, l);
                    }
                }
            }
            add_ticket_to_set(current_ticket, tickets_to_buy);
            uncovered_combos = cross_out(numbers_left, winning_number_subset_array, current_ticket, uncovered_combos, j, k, l);
            current_ticket = null;
            current_ticket = new int[k];
        }
        Object[] tickets_to_buy_array = tickets_to_buy.toArray();
        return tickets_to_buy_array;
    }
    
    
    /*
     * This method returns a random number that is not yet in a ticket
     *
     * @param current_ticket - a current ticket
     * @n - number or candidate values
     */
    public static int fill_in_with_random(int[] current_ticket, int n)
    {
        Random rand = new Random();
        int curr_rand = -1;
        
        curr_rand = rand.nextInt(n) + 1;
        while(already_in_ticket(current_ticket, curr_rand))
        {
            curr_rand = rand.nextInt(n) + 1;
        }
        return curr_rand;
    }
    
    /*
     * This method determins whether an integer in already in the array
     *
     * @param current_ticket an array to be checked
     * @param n - a number to search for
     *
     * @return in - true if the number is in array, false if not
     */
    public static boolean already_in_ticket(int[] current_ticket, int n)
    {
        boolean in = false;
        for(int i = 0; i < current_ticket.length; i++)
        {
            if(current_ticket[i] == n)
            {
                in = true;
            }
        }
        return in;
    }
    
    /*
     * This method finds numbers that show up the most in uncovered winning combos
     *
     * @param numbers_left - an array that keeps track of how many occurrence of each number are left in not yet covered winning combos
     *
     * @return - an array containing indeces in @param numbers_left of numbers that show up the most in uncovered winning combos
     * it returns -1 if numbers_left are all zeroes
     */
    public static int[] array_of_max(int[] numbers_left)
    {
        int max_number = -1;
        int count_of_maxes = 0;
        
        //find the max value in an array
        for(int i = 0; i < numbers_left.length; i++)
        {
            if(numbers_left[i] > max_number)
            {
                max_number = numbers_left[i];
            }
        }
        
        if (max_number <= 0) //return -1 if all numbers are covered
        {
            int[] error_array = {-1};
            return error_array;
        }
        
        //find how many max values are in the array
        for(int i = 0; i < numbers_left.length; i++)
        {
            if(max_number ==  numbers_left[i])
            {
                count_of_maxes++;
            }
        }
        
        //fill in the array with max indeces
        int[] return_array = new int[count_of_maxes];
        int return_array_count = 0;
        for(int i = 0; i < numbers_left.length; i++)
        {
            if(max_number ==  numbers_left[i])
            {
                return_array[return_array_count] = i;
                return_array_count++;
            }
        }
        return return_array;
    }
    
    
    /*
     * This method fills in how many of each number are initially in winning combos array
     *
     * @param n - number or candidate values
     * @param winning_number_subset_array - an array containing uncovered winning combos
     *
     * @return return_array - an array that keeps track of how many occurrence of each number are left in not yet covered winning combos
     */
    public static int[] fill_number_left(int n, Object[] winning_number_subset_array)
    {
        int[] return_array = new int[n];
        for(Object curr_combo : winning_number_subset_array)
        {
            Object[] curr_combo_array = ((Set<java.lang.Object>) curr_combo).toArray();
            for(int i = 0; i < curr_combo_array.length; i++)
            {
                int curr_number = (int) curr_combo_array[i];
                return_array[curr_number - 1]++;
            }
        }
        return return_array;
    }
    
    /*
     * This method adds a ticket to a set of tickets
     *
     * @param current_ticket - a ticket to be added
     * @param tickets_to_buy - a set of all tickets that the user should buy
     */
    public static void add_ticket_to_set(int[] current_ticket, Set<Set<Object>> tickets_to_buy)
    {
        Set<Object> temp_set = new HashSet<Object>();
        for(int g : current_ticket)
        {
            temp_set.add(g);
        }
        tickets_to_buy.add(temp_set);
        temp_set = null;
    }
    
    /*
     * This method is crossing out all possible winning combinations with a given ticket (or its portion)
     *
     * @param numbers_left - an array that keeps track of how many occurrence of each number are left in not yet covered winning combos
     * @param winning_number_subset_array - an array containing uncovered winning combos before crossing out
     * @param current_ticket - a ticket used to cross out winning combos (can be filled fully or partially)
     * @param uncovered_combos - the number of uncovered winning combos before crossing out
     * @param j - the number of promised correct numbers
     * @param k - the number of numbers on each ticket
     * @param l - the number of matched numbers needed to win
     *
     * @return uncovered_combos - the number of uncovered winning combos after crossing out
     */
    public static int cross_out(int[] numbers_left, Object[] winning_number_subset_array, int[] current_ticket, int uncovered_combos, int j, int k, int l)
    {
        boolean can_cross_out = false;
        int count_matches = 0;
        
        Set<Object> current_winning_combo = new HashSet<Object>();
        Object[] current_winning_combo_array = new Object[j];
        
        
        //see what combos you can cross out with a new ticket
        for (int i = 0; i < winning_number_subset_array.length; i++)
        {
            //check whether that combo was not yet covered
            if(!(winning_number_subset_array[i] instanceof Integer))
            {
                current_winning_combo = (Set<java.lang.Object>) winning_number_subset_array[i];
                current_winning_combo_array = current_winning_combo.toArray();
                
                //reset parameters
                can_cross_out = false;
                count_matches = 0;
                
                //check if you can cross out that combo
                for(int x = 0; x < k; x++)
                {
                    for(int y = 0; y < j; y++)
                    {
                        if((current_ticket[x]) == ((int)current_winning_combo_array[y]))
                        {
                            count_matches++;
                        }
                    }
                }
                if(count_matches >= l) //cross out the combo from winning_number_subset_array
                {
                    Set<Object> combo_to_cross_out = (Set<java.lang.Object>) winning_number_subset_array[i];
                    for(Object value_to_decrement: combo_to_cross_out)
                    {
                        int value_to_decrement_int = (int)value_to_decrement;
                        numbers_left[value_to_decrement_int-1]--;
                    }
                    winning_number_subset_array[i] = 0;
                    uncovered_combos--;
                }
            }
        }
        return uncovered_combos;
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
     * This method saves the result to the output file
     *
     * @param tickets_array - array of the tickets that a user should buy to have at least 1 winning ticket
     * @param outputFileName - string containing the name of the output file where weight_matrix should be saved
     */
    public static void saveToOutputFile(int[][] tickets_array, String outputFileName) {
        
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
        
        for (int row = 0; row < tickets_array.length; row++) {
            for (int col = 0; col < tickets_array[row].length; col++) {
                fileOutput.print(tickets_array[row][col] + " ");
            }
            fileOutput.println("");
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
