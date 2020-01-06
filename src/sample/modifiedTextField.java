package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public class modifiedTextField extends TextField  {

    //ArrayList to accomodate the permissible values
    ArrayList<Integer> list;

    modifiedTextField(){

        //Restricting TextField to take input other than numerics
        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });


        //Restricting Textfield to take input more than 1 character
        this.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            if (newText.length() > 1 || (newText.compareTo("0")==0)) {
                return null ;
            } else {
                return change;
            }
        }));


        //Setting Stylesheet to the textField
        this.getStyleClass().add("custom");
        Font font1 = Font.font("Roboto",FontWeight.NORMAL,15);
        this.setFont(font1);
        this.setAlignment(Pos.CENTER);

        list=new ArrayList<>();
        for (int i=0;i<9;i++){
            list.add(i+1);
        }
    }

}
