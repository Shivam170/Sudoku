package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.*;


public class Controller {

    //Array List for recording all moves
    ArrayList<String> moves = new ArrayList<>();

    //GridPane global declaration to contain textfields
    @FXML
    GridPane sudokuGrid;

    int countBack=0;

    //Array of modified textField
    modifiedTextField[][] textFields;

    //Creating instance of sudoku class
    Sudoku sudoku = new Sudoku();

    //Globally declaring row and col variable
    int row, col;

    //Action performed on Clicking solve button
    @FXML
    public void onClick(ActionEvent e)  {

        //Calling the solve function and showResult function if the loaded grid is valid
        if(loadFromGrid()){
            long l1 =System.nanoTime();
            countBack=0;
            solveGrid();
            long l2 =System.nanoTime();
            System.out.println("The time in nanosecond will be "+(l2-l1));
            System.out.println("The number of backtracks will be "+countBack);
            showResult();
            System.out.println();
            printGrid();
            }

    }

    //Initialize function to create the instance of modified textField
    public void initialize(){

        //Creating eventHandler to set the color of textField on focus
        EventHandler<MouseEvent> e= new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TextField tf = (TextField)event.getSource();
                resetTextField();
                //Setting the background color of text filed on MouseClick
                tf.setBackground(new Background(new BackgroundFill(Color.rgb(243, 190, 200), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        };

        //Initialing array of modified textField
        textFields = new modifiedTextField[9][9];


        /***
         * Loop for creating instance of all the textField
         * and Setting all size of Text Field and adding it to the
         * main Sudoku Grid
         */
        for(int i=0;i<9;i++){
            for (int j=0;j<9;j++){

                textFields[i][j] = new modifiedTextField();
                textFields[i][j].setOnMouseClicked(e);
                textFields[i][j].setMinSize(65,65);
                textFields[i][j].setPrefSize(65,65);
                textFields[i][j].setMaxSize(65,65);
                sudokuGrid.add(textFields[i][j],j,i);

            }
        }

    }

    //Function to solve the Grid
    void solveGrid() {
        /*Calling update grid function to set the suitable row col value and
        consequently calling solveCell function to fill the corresponding textField
        */
        updateGrid();
        solveCell(row, col);

    }

    //Function to update the Grid
    void updateGrid(){

        //Updating the ArrayList of all the textfield
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                    updateList(i,j);
            }
        }
        //Setting suitable value for row and coloumn
        minimumLength();

    }

    //Function to fill the corresponding cell
    boolean solveCell(int row, int col) {

        /**
         * Checking whether the Grid is fully solved or not
         */

        if(isFull()){
            return true;
        }

        /**
         * In this loop we are iterating over the permissible values in the corresponding list
         * and consequently calling the same function to fill the next position and if the list is empty
         * or any dead-end occur then it recurse back to previous state
         */

        for (int i = 0; i< textFields[row][col].list.size(); i++){
            sudoku.data[row][col]= textFields[row][col].list.get(i);
            moves.add(row+" "+col+" "+ textFields[row][col].list.get(i));
            updateGrid();

            if(solveCell(this.row, this.col)){
                return true;
            }else {
                int data=sudoku.data[row][col];
                sudoku.data[row][col]=0;
                countBack++;
                moves.add(row + " " + col + " " + "0");
                recover(row,col,data);
                updateGrid();
            }
        }

        //Returning false as dead end occurred

        return false;
    }

    //Function to recover back to previous state
    void recover(int row, int col, int data){

        //Loop to update the row
        for (int i=0;i<9;i++){
            if(!textFields[row][i].list.contains(data)){
                textFields[row][i].list.add(data);
                Collections.sort(textFields[row][i].list);
            }
        }

        //Loop to update the coloumn
        for (int i=0;i<9;i++){
            if(!textFields[i][col].list.contains(data))
                textFields[i][col].list.add(data);
            Collections.sort(textFields[i][col].list);
        }

        //Loop to update the subGrid
        int a=(row/3)*3;
        int b=(col/3)*3;

        for (int i=a;i<a+3;i++){
            for (int j=b;j<b+3;j++){
                if(!textFields[i][j].list.contains(data))
                    textFields[i][j].list.add(data);
                Collections.sort(textFields[i][j].list);
            }
        }

    }

    //Function to check if the grid is Full
    boolean isFull(){
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if(sudoku.data[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }

    //Function to set suitable row and column after finding the minimum sized list
    void minimumLength(){

        int min1=Integer.MAX_VALUE;

        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){

                if(sudoku.data[i][j]==0){

                    if(textFields[i][j].list.size()<min1){
                        min1= textFields[i][j].list.size();
                        //Setting the row and column variable
                        row =i;
                        col =j;

                    }
                }

            }
        }

    }

    //Function to remove unpermissible value from the corresponding textField ArrayList
    void updateList(int row, int col){

        //Loop to remove unpermissible value from the row
        for (int i=0;i<9;i++){

            if(sudoku.data[row][i]!=0 && textFields[row][col].list.contains(sudoku.data[row][i]))
                textFields[row][col].list.remove(textFields[row][col].list.indexOf(sudoku.data[row][i]));

        }

        //Loop to remove unpermissible value from the column
        for (int i=0;i<9;i++){

            if(sudoku.data[i][col]!=0 && textFields[row][col].list.contains(sudoku.data[i][col]))
                textFields[row][col].list.remove(textFields[row][col].list.indexOf(sudoku.data[i][col]));

        }

        int a=(row/3)*3;
        int b=(col/3)*3;

        //Loop to remove unpermissible value from the subGrid
        for (int i=a;i<a+3;i++){
            for (int j=b;j<b+3;j++){

                if(sudoku.data[i][j]!=0 && textFields[row][col].list.contains(sudoku.data[i][j]))
                    textFields[row][col].list.remove(textFields[row][col].list.indexOf(sudoku.data[i][j]));

            }
        }

    }

    //Function to show the Result on the Textfields
    void showResult(){

        //Creating a runnable to to set the Grid on another Thread with some time delay
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<moves.size();i++){
                    int finalI = i;
                    //Extracting values from the moves ArrayList
                    String[] l = moves.get(i).split(" ");
                    final int a=Integer.parseInt(l[0]);
                    final int b=Integer.parseInt(l[1]);
                    final String c =l[2];
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(!c.equals("0"))
                                textFields[a][b].setText(String.valueOf(c));
                            else textFields[a][b].setText("");

                        }
                    });
                    try {
                        //Setting the time delay
                        Thread.sleep(50);

                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    //Function to reset the TextField
    void resetTextField(){

        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                textFields[i][j].setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255,0), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

    }

    //Function to load the grid from the File
    public void loadFromFile(ActionEvent actionEvent) {

        reset();

        //Creating instance of file chooser
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        //Checking whether user selected any file or not
        if(file!=null) {

            int r = 0;
            String line;

            //Reading file and filling the 2-D array
            try {
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                while ((line = br.readLine()) != null && r < 9) {

                    String[] itemData = line.split(",");
                    int k;
                    for (k = 0; k < itemData.length; k++) {
                        if (!itemData[k].isEmpty() && !itemData[k].equals("0"))
                            sudoku.data[r][k] = Integer.parseInt(itemData[k]);
                        else sudoku.data[r][k] = 0;
                    }

                    for (int i = k; i < 9; i++) {
                        sudoku.data[r][i] = 0;
                    }

                    r++;

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setSudokuGrid();
            printGrid();
        }
    }

    //Function to load from the Gird
    public boolean loadFromGrid(){


        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){

                if(textFields[i][j].getText().isEmpty()){
                    sudoku.data[i][j]=0;
                }else {
                        sudoku.data[i][j]=Integer.parseInt(textFields[i][j].getText());
                }

            }
        }

        //Checking whether the grid is valid
        if(verifyGrid()){
            return true;
        }else return false;
    }

    //Funtion to print the Grid on console
    public void printGrid(){

        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                System.out.printf(String.valueOf(sudoku.data[i][j]));
            }
            System.out.println();
        }

    }

    //Function to set the 2-D array on the textfield Grid
    public void setSudokuGrid(){

        for (int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(sudoku.data[i][j]!=0)
                    textFields[i][j].setText(String.valueOf(sudoku.data[i][j]));
            }
        }

    }

    //Function to verify the Grid is valid or not
    public boolean verifyGrid() {

        int flag=0;

        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                if(!textFields[i][j].getText().equals("") && sudoku.constraint(i,j,textFields[i][j].getText(),textFields)){
                    flag=1;
                }
            }
        }

        //Generating dialog box if the grid is not valid
        if(flag==0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Input");

            alert.showAndWait();
            return false;
        }

    return true;

    }

    public void resetGrid(ActionEvent actionEvent) { reset();}

    //Function to Reset all the values
    void reset(){

        sudoku=new Sudoku();
        sudokuGrid.getChildren().clear();
        initialize();
        moves.clear();
        printGrid();
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                textFields[i][j].setText("");
            }
        }

    }

}
