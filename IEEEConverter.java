import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox; //or another one if more appropriate; arbitrary
import javafx.stage.Stage;
import java.util.ArrayList;
import java.text.DecimalFormat;
import javafx.geometry.Pos;

/**
  * A converter that decodes IEEE-754 floating-point numbers to a decimal 
  * representation and encodes a decimal to an IEEE-754 floating-point
  * (depending on the user's choice of operation). 
  */
public class IEEEConverter extends Application {
  
  private TextField inputNumText;
  private Button d2bButton;
  private Button b2dButton;
  private Label title;
  private Label result;

  public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initUI(stage);
	}
	
	private void initUI(Stage stage) {
    VBox pane = new VBox();
		pane.setAlignment(Pos.CENTER);
		
		//create and format the text field
		inputNumText = new TextField();
		inputNumText.setPromptText("Enter the number you want to convert");
		inputNumText.setAlignment(Pos.CENTER);
		inputNumText.setMaxWidth(300);
		inputNumText.setMinWidth(300);
		
		//create and format the buttons
		d2bButton = new Button("Convert to IEEE-754 Binary");
		b2dButton = new Button("Convert to Decimal");
		d2bButton.setMaxWidth(200);
		d2bButton.setMinWidth(200);
		b2dButton.setMaxWidth(200);
		b2dButton.setMinWidth(200);
		
		//create and format the labels
		title = new Label("IEEE-754 Floating Point Conversion Tool");
		result = new Label("");

		pane.getChildren().add(title);
		pane.getChildren().add(inputNumText);
		pane.getChildren().add(d2bButton);
		pane.getChildren().add(b2dButton);
		pane.getChildren().add(result);

		d2bButton.setOnAction(new DecToIEEE(inputNumText, result));
		b2dButton.setOnAction(new IEEEToDec(inputNumText, result));

		Scene mainScene = new Scene(pane, 500, 200);

		stage.setTitle("IEEE-754 Floating Point Conversion Tool");
		stage.setScene(mainScene);
		stage.show(); 
  }

}

class DecToIEEE implements EventHandler<ActionEvent> {

  private TextField tField;
	private float decimalVal;
	private Label resultLabel;
	private boolean isSpecial;
	private boolean isValid;

	public DecToIEEE(TextField tf, Label label) {
		this.tField = tf;
		String inputStr = tf.getText();
		this.resultLabel = label;
		setDecVal(inputStr);
	}
	
  /**
  * @param inputStr
  */
	public void setDecVal(String inputStr) {

    if (inputStr.toUpperCase().equals("INFINITY") || inputStr.toUpperCase().equals("-INFINITY")
				|| inputStr.toUpperCase().equals("NAN")) {
			this.decimalVal = 0.0f; // placeholder value
			this.isSpecial = true;
      this.isValid = true;
    } else {
      this.isSpecial = false;
      try {
        this.decimalVal = Float.parseFloat(inputStr);
        this.isValid = true;
		  } catch (Exception e) {
        this.decimalVal = 0.0f; // placeholder value
        this.isValid = false;
		  }
    }	
	}

  /**
    * Responds to a user clicking the "Convert to Decimal" button. 
    * @param event  An ActionEvent object that indicates that the "Convert to Decimal" button has been clicked.
    */
	public void handle(ActionEvent event) {
		String inputStr = tField.getText(); //get the updated input string
		setDecVal(inputStr); //update the decimal value
    if (!this.isValid){
	      this.resultLabel.setText("Invalid input. " + inputStr + " is not a decimal number.");
        return;
	    } //else: proceed; the input is valid
	  if (this.isSpecial){
		  switch (inputStr.toUpperCase()) {
		  case "INFINITY":
			  this.resultLabel.setText("IEEE-754 Representation: 01111111100000000000000000000000");
			  break;
		  case "-INFINITY":
			  this.resultLabel.setText("IEEE-754 Representation: 11111111100000000000000000000000");
			  break;
		  case "NAN":
			  this.resultLabel.setText("IEEE-754 Representation: 01111111111111111111111111111111");
			  break;
		  }
      return;
	  } else if (decimalVal == 0.0f){
				String sign = getSign(inputStr.charAt(0));
        this.resultLabel.setText("IEEE-754 Representation: " + sign + "0000000000000000000000000000000");
      return;
    } else {
      String sign = getSign(inputStr.charAt(0));
			ArrayList<Object> exponentResults = getExponent(this.decimalVal);
			String exponent = (String) exponentResults.get(0);
			String mantissa = getMantissa((float) exponentResults.get(1));

			this.resultLabel.setText("IEEE-754 Representation: " + sign + exponent + mantissa);
      return;
    } 
  }

  /**
  * Returns the IEEE-754 sign of the given decimal floating point.
  * @param input  a String representing a decimal floating point
  * @return a 1-bit binary string representing the sign
  */
  public String getSign(char signChar) {
    if (signChar == '-') {
      return "1";
    } else {
      return "0";
    }
  }

 /**
	 * Returns Returns the IEEE-754 exponent of the given decimal floating point.
	 * 
	 * @param input a String representing a decimal floating point
	 * @return an ArrayList containing an 8-bit binary string representing the 
   * exponent and a float representing the decimal value of the Mantissa
	 */
	public ArrayList<Object> getExponent(float input) {
		int rawExp = 0;
		float mantissaNum = input;
		if (mantissaNum < 1) {
			while (mantissaNum < 1) {
				mantissaNum = mantissaNum*2;
				rawExp--;
			}
		} else { //mantissaNum >= 1
			while (mantissaNum >= 2) {
				mantissaNum = mantissaNum/2;
				rawExp++;
			}
		}
		String biasedExpStr = Integer.toBinaryString(rawExp+127);
    while (biasedExpStr.length() < 8) {
				biasedExpStr = "0" + biasedExpStr;
		}
		ArrayList<Object> results = new ArrayList<>();
		results.add(biasedExpStr);
		results.add(mantissaNum);
		return results;
	}
  
  /**
	 * Returns the IEEE-754 Mantissa of the given decimal floating point.
	 * 
	 * @param input a decimal floating point number
	 * @return a 23-bit binary string representing the Mantissa
	 */
	public String getMantissa(float input) {
		  DecimalFormat df = new DecimalFormat("0.00");
			float val = input - 1;
			String retStrRaw = "";
			
			for (int i = 0; i < 26; i++) {
				val = Float.parseFloat(df.format(val * 2));
				if (val >= 1) {
					retStrRaw = retStrRaw + "1";
					val = val - 1;
				} else {
					retStrRaw = retStrRaw + "0"; 
				}
			}
			String retStr = roundMantissa(retStrRaw);
			return retStr;
	}

  /**
	 * Returns the binary string rounded to the nearest even.
	 * 
	 * @param man a 26-bit binary String (representing the un-rounded Mantissa)
	 * @return the binary String <man> rounded to a 23-bit binary String
	 */
	public String roundMantissa(String man) {
		  String lastThreeDigits = man.substring(23);
	    String first23Digits = man.substring(0,23);
			String roundedMan = "";
			if (lastThreeDigits.startsWith("0")) {
				//round down
				roundedMan = first23Digits;
			} else if (lastThreeDigits.equals("110") || lastThreeDigits.equals("101") || lastThreeDigits.equals("111")) {
				//round up
        roundedMan = roundUp(first23Digits);
			} else{
				//lastThreeDigits.equals("100")
				if (man.charAt(22) == '1') {
					//round up
          roundedMan = roundUp(first23Digits);
				} else {
					//round down
					roundedMan = first23Digits;
				}
			}
			return roundedMan;
	}
	
  /**
  * Rounds up the given binary string if and only if rounding up does not add 
  *any more bits. If rounding up results in adding another bit, the original 
  *string is returned. 
  * @param binStr a String representing the binary value to be rounded up
  * @return       a String representing the rounded binary string or the original String if rounding cannot be done without adding more bits
  */
	public String roundUp(String binStr) {
		int currIndex = binStr.length()-1;
		int numZeroes = 0;
		String zeroes = "";
		String retStr = binStr;
		while (binStr.charAt(currIndex) == '1' && currIndex > 1) {
			currIndex--;
			numZeroes++;
		}
		for (int i = 0; i < numZeroes; i++) {
			zeroes = zeroes + "0";
		}
		if (currIndex == 1 && binStr.charAt(1) == '1' && binStr.charAt(0) == '1') {
			//do nothing; the number cannot be rounded up without adding more bits
		} else {
			retStr = retStr.substring(0, currIndex) + "1" + zeroes;
		}
		return retStr;
	}
  
}

class IEEEToDec implements EventHandler<ActionEvent> {
  private String inputstr;
  private Label label;
  private int sign;
  private int exponent;
  private double mantissa;
  private TextField tf;

  public IEEEToDec(TextField tf, Label label) {
    /**
    * Sets the sign, exponent and mantissa of the IEEE 754 floating point number. 
    * @param tf  A textfield containing the IEEE 754 floating point number.
    * @param label A label that will display the decimal representation of the number or an error message
    */
    this.label = label;
    this.tf = tf;
    this.inputstr = tf.getText();

  }

  public boolean check_spcl(String input) {
    /**
    * Sets the sign, exponent and mantissa of the IEEE 754 floating point number. 
    * @param tf  A textfield containing the IEEE 754 floating point number.
    * @param label A label that will display the decimal representation of the number or an error message
    */
    if (input.equals("01111111100000000000000000000000")) {
      this.label.setText("Decimal Representation: INFINITY");
      return true;
    }
    else if (input.equals("11111111100000000000000000000000")) {
      this.label.setText("Decimal Representation: -INFINITY");
      return true;
    }
    else if (input.equals("01111111111111111111111111111111")) {
      this.label.setText("Decimal Representation: NAN");
      return true;

    }
    return false;


  }
  
  public void getsign(char input) {
    /**
    * Sets the sign to be 0 if the IEEE 754 floating point number begins with a 0 or 1 if it begins with a 1. Otherwise, displays an error message. 
    * @param input  The first character of the IEEE 754 floating point number.
    */
    if (input == '0') {
      this.sign = 0;
    }
    else if (input=='1') {
      this.sign = 1;
    }
    else {
      this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
      return;
    } 
  }

  public void getexponent(String input) {
    /**
    * If possible, determines the exponent of the IEEE 754 floating point number. Otherwise, displays an error message. 
    * @param input  A string that is the binary representation of the exponent of the IEEE 754 floating point number.
    */
    try {
      int decimal = Integer.parseInt(input,2); 
      this.exponent = decimal - 127;

    }

    catch (Exception e) {
      this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
      return;
    }

  }

  public void getmantissa(String input) {
    /**
    * If possible, determines the mantissa of the IEEE 754 floating point number. Otherwise, displays an error message. 
    * @param input  A string that is the binary representation of the mantissa of the IEEE 754 floating point number.
    */
    double decimal = 0;
    int num = 0;
    for (int i = -1; i >-24; i--) {
      char chr = input.charAt(num);
      int value = Integer.parseInt(String.valueOf(chr));
      if (value>1) {
        this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
        break;
      }
      decimal += (value * Math.pow(2, i));
      num ++;
    }
    this.mantissa = decimal;
  }

  public void handle(ActionEvent event) {
    /**
    * Responds to a user clicking the Convert to IEEE button. 
    * @param event  An ActionEvent object that indicates that the Convert to IEEE button has been clicked.
    */
    this.inputstr = this.tf.getText();
    if (!(this.inputstr.length() == 32)) {
      this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
      return;
    }
    if (check_spcl(this.inputstr)) {
    	return;
    }
    getsign(this.inputstr.charAt(0));
    getexponent(this.inputstr.substring(1, 9));
    getmantissa(this.inputstr.substring(9));
    double number = Math.pow(-1, this.sign)*(1+this.mantissa)*Math.pow(2, this.exponent);
    this.label.setText("Value Actually Stored in Float: " + String.valueOf(number));
    return;

  }
  
}