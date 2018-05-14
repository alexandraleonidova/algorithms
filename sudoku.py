# Name of File: sudoku.py
# Date: November 7, 2017
# Authors: Alexandra Leonidova

import re, itertools
from itertools import chain, product, combinations
import sys

#a memory of grids
list_of_boards = []
nodes_generated = 0
def setA(value):
    global nodes_generated   # declare a to be a global
    nodes_generated = 0  #

class Cell:
    def __init__(self, possible_values = None, fixed_flag = False, value = 0, pos = 0):
        if possible_values is None:
            possible_values = [1, 2, 3, 4, 5, 6, 7, 8, 9]
        self.possible_values = possible_values
        self.fixed_flag = fixed_flag
        self.value = value
        self.pos = pos

    def getPossibleValues(self):
        return self.possible_values

    def getFixedFlag(self):
        return self.fixed_flag

    def getValue(self):
        return self.value

    def getPosition(self):
        return self.pos

    def setPossibleValues(self, new_possible_values):
        self.possible_values = new_possible_values

    def setFixedFlag(self, new_fixed_flag):
        self.fixed_flag = new_fixed_flag

    def setValue(self, new_value):
        self.value = new_value

    def setPosition(self, new_pos):
        self.pos = new_pos

    #remove a value from a list of possible values of a cell
    def updateList(self, local_value):
        if local_value <= 0:
            exit()
        if local_value in self.possible_values:
            self.possible_values.remove(local_value)


    # 1 2 3
    # 4 5 6
    # 7 8 9
    # determines which subsquare does the cell belong to
    def getSubSquare(self):
        x = self.pos
        first = [0, 1, 2, 9, 10, 11, 18, 19, 20]
        second = [3, 4, 5, 12, 13, 14, 21, 22, 23]
        third = [6, 7, 8, 15, 16, 17, 24, 25, 26]
        fourth = [27, 28, 29, 36, 37, 38, 45, 46, 47]
        fifth = [30, 31, 32, 39, 40, 41, 48, 49, 50]
        sixth = [33, 34, 35, 42, 43, 44, 51, 52, 53]
        seventh = [54, 55, 56, 63, 64, 65, 72, 73, 74]
        eighth = [57, 58, 59, 66, 67, 68, 75, 76, 77]
        nineth = [60, 61, 62, 69, 70, 71, 78, 79, 80]
        if (x in first):
            return 1
        elif (x in second):
            return 2
        elif (x in third):
            return 3
        elif (x in fourth):
            return 4
        elif (x in fifth):
            return 5
        elif (x in sixth):
            return 6
        elif (x in seventh):
            return 7
        elif (x in eighth):
            return 8
        elif (x in nineth):
            return 9
        else: #error case
            return -1

    def getRow(self):
        return self.pos // 9

    def getColumn(self):
        return self.pos % 9


class Board:
    def __init__(self, grid_src = [[0 for x in range(9)] for y in range(9)], num_fixed = 0):
        self.num_fixed = num_fixed
        pos_count = 0
        self.grid = [[Cell() for x in range(9)] for y in range(9)]
        for row in range(9):
            for column in range(9):
                self.grid[row][column] = Cell()
                self.grid[row][column].setPosition(pos_count)
                pos_count += 1

        for row in range(9):
            for column in range(9):
                if (grid_src[row][column] != 0):
                    curr_cell = self.grid[row][column]
                    curr_value = grid_src[row][column]
                    if (curr_value > 0):
                        success_flag = self.addCell(curr_cell, curr_value)
                        if not success_flag:
                            printErrorToFile(output_file_name)
                            exit()

    def getGrid(self):
        return self.grid

    def getNumFixed(self):
        return self.num_fixed

    def setGrid(self, new_grid):
        self.grid = new_grid

    def setNumFixed(self, new_num_fixed):
        self.num_fixed = new_num_fixed

    def printGrid(self):
        for row in range(0, 9):
            for column in range(0, 9):
                x = (self.grid[row][column]).getValue()
                print(x),
            print

    # Adds a cell to a grid and updates lists of all cells in same row, column and sub-square
    # returns True if added successfully and False if unsuccessfully
    def addCell(self, cell, value):
        if value <= 0:
            exit()
        else:
            row = cell.getRow()
            column = cell.getColumn()
            sub_square = cell.getSubSquare()

            # update the data of the cell
            cell.setFixedFlag(True)
            cell.setValue(value)
            cell.setPossibleValues([value])

            self.grid[row][column] = cell
            self.num_fixed += 1

            # update all cells in same column
            for i in range(9):
                if i is not row:
                    curr = self.grid[i][column]
                    curr.updateList(cell.getValue())
                    if (len(curr.getPossibleValues())) is 0:  # err
                         return False


            # update all cells in same row
            for i in range(9):
                if i is not column:
                    curr = self.grid[row][i]
                    curr.updateList(cell.getValue())
                    if (len(curr.getPossibleValues())) is 0:  # err
                        return False

            # update all cells in same sub square
            for i in range(0, 9):
                for j in range(0, 9):
                    local_subsquare = self.grid[i][j].getSubSquare()
                    if(local_subsquare is sub_square):
                        if (not ((i is row) and (j is column))):
                            curr = self.grid[i][j]
                            curr.updateList(cell.getValue())
                            if (len(curr.getPossibleValues())) is 0:  # err
                                return False
        return True

    # Scans the grid and looks for cells with lists narrowed to 1, updates the grid if found
    # returns True if update was successful, False if not successful
    def update(self):
        done_flag = False
        while not done_flag:
            done_flag = True
            for i in range(9):
                for j in range(9):
                    curr_cell = self.grid[i][j]
                    curr_list = curr_cell.getPossibleValues()
                    curr_value = curr_cell.getValue()
                    if (len(curr_list) is 0):
                        return False
                    if (curr_cell.getFixedFlag() and (curr_list[0] is not curr_value)):
                        return False

                    if (len(curr_list) is 1):
                        if (curr_list[0] is not curr_value) and (curr_value is not 0):
                            return False
                        if (not curr_cell.getFixedFlag()):
                            if curr_value is 0:
                                done_flag = False
                                add_success_flag = self.addCell(curr_cell, curr_list[0])
                                if not add_success_flag:
                                    return False
                                    # else check if curr_list[0] and curr_value are same
        return True


# This function evaluates the board and returns the position with the smallest list of possible solutions
#
# @param board_to_eval - a board to be evaluated
#
# @return pos - position of the cell with the smallest list of possible solutions
# returns -1 if the solution is infeasible
# returns 1000 if the board is a solution
def evaluateBoard(board_to_eval):
    min_len = 100
    pos = -100

    board_to_eval_copy = copy_board(board_to_eval)

    local_grid = board_to_eval_copy.getGrid()
    if board_to_eval.getNumFixed() > 81:
        exit()

    #check if the board is solved
    if board_to_eval.getNumFixed() is 81:
        for i in range(9):
            for j in range(9):
                curr_list = local_grid[i][j].getPossibleValues()
                curr_value = local_grid[i][j].getValue()
                fixed = local_grid[i][j].getFixedFlag()
                #check for abnormalities
                if (len(curr_list) is not 1): # list of possibilities is not narrowed down to one value
                    return -1
                if (curr_value is not curr_list[0]): # the value does not match the range of allowed values for this cell
                    return -1
                if not fixed: # the cell was not fixed to the grid yet
                    return -1

        return 1000 #no abnormalities found: the board is solved

    #find the position with the smallest list of possible solutions
    for i in range(9):
        for j in range(9):
            curr_cell = local_grid[i][j]
            curr_list = curr_cell.getPossibleValues()
            curr_len = len(curr_list)
            if curr_len is 0:
                return -1  # infeasible solution
            elif (curr_len is 1) and (not curr_cell.getFixedFlag() and ( curr_cell.getValue() is 0)):
                exit()
            else: #normal case
                if ((not curr_cell.getFixedFlag()) and (curr_len < min_len)):
                    if (len(curr_cell.getPossibleValues()) is 1):
                        exit()
                    min_len = curr_len
                    pos = curr_cell.getPosition()
    return pos

# This is a recursive function to solve the board. The program tries different values for non fixed cells
# and checks whether it leads to a valid solution
#
# @param board_to_solve - a board to be solved with its current state
# @param index_to_list_of_boards - an index to access a memory of boards
def solve(board_to_solve, index_to_list_of_boards):
    global nodes_generated

    if board_to_solve is None:
        return None
    else:
        if index_to_list_of_boards > len(list_of_boards):
            exit()
        if index_to_list_of_boards is len(list_of_boards):
            list_of_boards.append(index_to_list_of_boards)
        else:
            list_of_boards[index_to_list_of_boards] = board_to_solve

    nodes_generated += 1
    #find the position of the board of the sell with the smallest list of possible values
    min_pos = evaluateBoard(board_to_solve)

    # return if special case
    if min_pos < -1: #infeasible solution
        print("Wierd: all cells are used")
        exit()
    if min_pos < 0:
        return None

    if min_pos > 100: #solution case
        return board_to_solve

    # process a regular case
    local_grid = board_to_solve.getGrid()
    row = min_pos // 9
    column = min_pos % 9
    curr_cell = local_grid[row][column]
    curr_list = curr_cell.getPossibleValues()

    # try all possible values in the list
    for possibile_val in curr_list:
        #copy the board
        bcopy = copy_board(board_to_solve)
        flag_successfull_addCell = bcopy.addCell(curr_cell, possibile_val)
        # add a ned value to the copied board and update it
        if not flag_successfull_addCell:
            bcopy = None
            continue
        flag_successfull_update = bcopy.update()
        if not flag_successfull_update:
            bcopy = None
            continue
        #check whether the board is solved after update
        if board_to_solve.getNumFixed() is 81:
            return board_to_solve
        bcopy_solution = solve(bcopy, (index_to_list_of_boards + 1))
        #keep going if the solution is not infeasible yet
        if bcopy_solution is not None:
            return bcopy_solution

    return None

# Ths function creates a copy of a board
#
# @param board_orig - the board to be copied
#
# @return board_copy - the copy of the original copy
def copy_board(board_orig):
    board_orig_grid = board_orig.getGrid()
    num_fixed_copy = board_orig.getNumFixed()

    grid_copy = [[0 for x in range(9)] for y in range(9)]
    for row in range(9):
        for column in range(9):
            cell_orig = board_orig_grid[row][column]
            fixed_flag_copy = cell_orig.getFixedFlag()
            pos_copy = cell_orig.getPosition()
            value_copy = cell_orig.getValue()
            list_copy = []
            for x in cell_orig.getPossibleValues():
                list_copy.append(x)

            grid_copy[row][column] = Cell(list_copy, fixed_flag_copy, value_copy, pos_copy)

    board_copy = Board()
    board_copy.setNumFixed(num_fixed_copy)
    board_copy.setGrid(grid_copy)

    return board_copy

# This function prints a board to the file
#
# @param output_file_name - the name of the output file
# @param board - the board to be printed
def printBoardToFile(output_file_name, board):
    global nodes_generated
    output_file_name_file = open(output_file_name, "w")

    for row in range(0, 9):
        for column in range(0, 9):
            x = (board.getGrid()[row][column]).getValue()
            output_file_name_file.write(str(x) + " "),
        output_file_name_file.write("\n")
    output_file_name_file.write("Nodes generated = " + str(nodes_generated) + "\n")

# This function prints a n error message to an output file
# for cases when the board is infeasible
#
# @param output_file_name - the name of the output file
def printErrorToFile(output_file_name):
    global nodes_generated
    output_file_name_file = open(output_file_name, "w")
    output_file_name_file.write("Infeasible\n")
    output_file_name_file.write("Nodes generated = " + str(nodes_generated) + "\n")


if __name__ == '__main__':
    nodes_generated = 0
    try:
        input_file_name = sys.argv[1]  # first command line argument is dfa text file
        output_file_name = sys.argv[2]  # second command line argument is a name of the file where the converted nfa to dfa will be stored

    # read from file and break it into variables
    except IOError:
        print("This file does not exist")
        exit()
        # open the file for reading
    f = open(input_file_name, "r")

    pos_count = 0
    fixed_count = 0

    input_array = [[0 for x in range(9)] for y in range(9)]

    # get values from input file
    for line in f:
        numbers = map(int, line.split())
        input_array[numbers[0] - 1][numbers[1] - 1] = numbers[2]

    input_cells = [[Cell() for x in range(9)] for y in range(9)]

    # initialise board
    for row in range(9):
        for column in range (9):
            if (input_array[row][column] != 0):
                input_cells[row][column].setFixedFlag(True)
                input_cells[row][column].setValue(input_array[row][column])
                input_cells[row][column].setPossibleValues([input_array[row][column]])
                fixed_count += 1
            input_cells[row][column].setPosition(pos_count)
            pos_count += 1

    board_init = Board(input_array, 0)

    flag_successful_update = board_init.update()
    if not flag_successful_update:
        printErrorToFile(output_file_name)
        exit()

    board_solution = solve(board_init, 0)
    if board_solution is None:
        printErrorToFile(output_file_name)
    else:
        printBoardToFile(output_file_name, board_solution)




