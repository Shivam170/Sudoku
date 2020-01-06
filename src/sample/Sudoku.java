package sample;


public class Sudoku {

    //2D array to store the data
    int[][] data ;

    //Constructor to initialize the fields
    Sudoku(){
        data=new int[9][9];

        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                data[i][j]=0;
            }
        }
    }

    //Function to check if the row is valid or not
    boolean rowConstraint(int row, int col, String value, modifiedTextField[][] data){
        for(int i=0; i<9; i++ ){
            if(value.equals(data[row][i].getText())  && i!=col){return false;}
        }
        return true;
    }

    //Function to check if the column is valid or not
    boolean colConstraint(int row, int col, String value, modifiedTextField[][] data){
        for(int i=0; i<9; i++ ){
            if(value.equals(data[i][col].getText()) && i!=row){return false;}
        }
        return true;
    }

    //Function to check if the subGrid is valid or not
    boolean gridConstraint(int row, int col, String value, modifiedTextField[][] data){
        int a,b;
        a=(row/3)*3;
        b=(col/3)*3;

        for(int i=a;i<=a+2;i++){
            for (int j=b;j<=b+2;j++){
                if(value.equals(data[i][j].getText()) && i!=row && j!=col){return false;}
            }
        }
        return true;
    }

    boolean constraint(int row, int col, String value, modifiedTextField[][] data){
        if(gridConstraint(row,col,value,data) && rowConstraint(row,col,value,data) && colConstraint(row,col,value,data)){return true;}
        else return false;
    }

}



