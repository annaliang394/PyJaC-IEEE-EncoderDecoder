
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.text.DecimalFormat;
import javafx.geometry.Pos;

/**
 * A converter that decodes IEEE-754 floating-point numbers to a decimal
 * representation and encodes a decimal to an IEEE-754 floating-point (depending
 * on the user's choice of operation).
 */
public class IEEEConverter extends Application {

	// Main scene nodes
	private TextField inputNumText;
	private Button d2bButton;
	private Button b2dButton;
	private Button mn2helpButton;
	private Label title;
	private Label result;

	// Help Scene nodes
	private Button help2mnButton;
	private Label helptitle;
	private Label use;
	private Label spcl;
	private Label instrct;
	private Label contact;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		initUI(stage);
	}

	private void initUI(Stage stage) {
		// layout for main scene
		VBox pane = new VBox(10);
		pane.setAlignment(Pos.CENTER);

		// fonts for styling
		Font font = Font.font("Courier New", FontWeight.THIN, 16);
		Font font_smaller = Font.font("Courier New", FontWeight.THIN, 13);
		Font font_title = Font.font("Courier New", FontWeight.SEMI_BOLD, 20);

		// create and format the text field
		inputNumText = new TextField();
		inputNumText.setPromptText("Enter the number you want to convert");
		inputNumText.setAlignment(Pos.CENTER);
		inputNumText.setMaxWidth(300);
		inputNumText.setMinWidth(300);
		inputNumText.setMaxHeight(35);
		inputNumText.setMinHeight(35);
		inputNumText.setFont(font_smaller);

		// create and format the buttons for the main screen
		d2bButton = new Button("Convert to IEEE-754 Binary");
		d2bButton.setPrefSize(300, 40);
		d2bButton.setFont(font);
		b2dButton = new Button("Convert to Decimal");
		b2dButton.setPrefSize(300, 40);
		b2dButton.setFont(font);
		mn2helpButton = new Button("Help");
		mn2helpButton.setPrefSize(100, 40);
		mn2helpButton.setFont(font);

		// create and format the labels
		title = new Label("IEEE-754 Floating Point Conversion Tool");
		title.setFont(font_title);
		result = new Label("");
		result.setFont(font_smaller);

		// add elements to the main scene layout
		pane.getChildren().add(title);
		pane.getChildren().add(inputNumText);
		pane.getChildren().add(d2bButton);
		pane.getChildren().add(b2dButton);
		pane.getChildren().add(result);
		pane.getChildren().add(mn2helpButton);

		// attach EventHandlers to the buttons on main scene
		d2bButton.setOnAction(new DecToIEEE(inputNumText, result));
		b2dButton.setOnAction(new IEEEToDec(inputNumText, result));

		Scene mainScene = new Scene(pane, 500, 300);

		// -----Help Scene-----
		VBox pane2 = new VBox(5);
		pane2.setAlignment(Pos.TOP_CENTER);
		Scene helpscene = new Scene(pane2, 600, 300);

		// create and format the buttons for the help scene
		help2mnButton = new Button("Return to Converter");
		help2mnButton.setMaxWidth(400);
		help2mnButton.setMinWidth(100);
		help2mnButton.setOnAction(e -> stage.setScene(mainScene));
		mn2helpButton.setOnAction(e -> stage.setScene(helpscene));
		help2mnButton.setFont(font);

		// create and format labels for help scene
		helptitle = new Label("Using the Converter");
		helptitle.setFont(font_title);
		use = new Label("How to Use: ");
		use.setFont(font);
		spcl = new Label("Special Values: ");
		spcl.setFont(font);
		instrct = new Label("How it works: ");
		instrct.setFont(font);
		contact = new Label("Feel free to contact us at IEEEConverter@gmail.com.");
		contact.setFont(font);
		
		// add elements to the help scene layout
		pane2.getChildren().add(helptitle);
		pane2.getChildren().add(use);
		pane2.getChildren().add(spcl);
		pane2.getChildren().add(instrct);
		pane2.getChildren().add(contact);
		pane2.getChildren().add(help2mnButton);

		stage.setTitle("IEEE-754 Floating Point Conversion Tool");
		stage.setScene(mainScene);
		stage.show();
	}

}

/**
 * An EventHandler class that knows how to convert user-input from a decimal float to an 
 * IEEE-754 32-bit binary value. 
 */
class DecToIEEE implements EventHandler<ActionEvent> {

  private TextField tField;
	private float decimalVal;
	private Label resultLabel;
	private boolean isSpecial;
	private boolean isValid;
	
	/**
	 * Initializes the attributes of this event handler.
	 * 
	 * @param tf    A text field containing the decimal float number to convert.
	 * @param label A label that will display the IEEE-754 representation of the
	 *              number or an error message.
	 */
	public DecToIEEE(TextField tf, Label label) {
		this.tField = tf;
		String inputStr = tf.getText();
		this.resultLabel = label;
		setDecVal(inputStr);
	}
	
  /**
  * Checks whether the input String represents a special value or valid decimal number and 
  * attempts to set the decimal value based on the given String.
  * If the input is invalid, the decimal value is set to a default of 0.0f
  * 
  * @param inputStr a String representing the decimal number the user wishes to convert to binary.
  */
	public void setDecVal(String inputStr) {
		//check if inputStr is a special value
		if (inputStr.toUpperCase().equals("INFINITY") || inputStr.toUpperCase().equals("-INFINITY")
				|| inputStr.toUpperCase().equals("NAN")) {
			this.decimalVal = 0.0f; // placeholder value
			this.isSpecial = true;
			this.isValid = true;
		} else {
			this.isSpecial = false;
			//attempt to convert input to a float
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
	 * 
	 * @param event An ActionEvent object that indicates that the "Convert to
	 *              Decimal" button has been clicked.
	 */
	public void handle(ActionEvent event) {
		String inputStr = tField.getText(); // get the updated input string
		setDecVal(inputStr); // update the decimal value
		//handle the case where the input from the text field is invalid
		if (!this.isValid) {
			this.resultLabel.setText("Invalid input. " + inputStr + " is not a decimal number.");
			return;
		} // else: proceed; the input is valid
		//handle the special value cases
		if (this.isSpecial) {
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
		//handle the special zero cases
		} else if (decimalVal == 0.0f) {
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
	 * Returns the IEEE-754 sign-bit based on the given sign character.
	 * 
	 * @param signChar a character representing a negative value if and only if it
	 *                 is equal to '-'
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
	 * Returns the IEEE-754 exponent of the given decimal floating point.
	 * 
	 * @param input a String representing a decimal floating point
	 * @return an ArrayList containing an 8-bit binary string representing the
	 *         exponent and a float representing the decimal value of the Mantissa
	 */
	public ArrayList<Object> getExponent(float input) {
		int rawExp = 0;
		float mantissaNum = input;
		//calculate the integer that gives the nearest power of 2 to <input>
		if (mantissaNum < 1) {
			while (mantissaNum < 1) {
				mantissaNum = mantissaNum * 2;
				rawExp--;
			}
		} else { // mantissaNum >= 1
			while (mantissaNum >= 2) {
				mantissaNum = mantissaNum / 2;
				rawExp++;
			}
		}
		//the IEEE standard is to bias the exponent value by 127
		//find the corresponding binary string for the biased exponent
		String biasedExpStr = Integer.toBinaryString(rawExp + 127);
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
		//calculate 26 bits for the un-rounded Mantissa
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
	 * PRECONDITION: the given String <man> is a 26-bit binary String
	 * 
	 * @param man a 26-bit binary String (representing the un-rounded Mantissa)
	 * @return the binary String <man> rounded to a 23-bit binary String
	 */
	public String roundMantissa(String man) {
		String lastThreeDigits = man.substring(23);
		String first23Digits = man.substring(0, 23);
		String roundedMan = "";
		if (lastThreeDigits.startsWith("0")) {
			// round down
			roundedMan = first23Digits;
		} else if (lastThreeDigits.equals("110") || lastThreeDigits.equals("101") || lastThreeDigits.equals("111")) {
			// round up
			roundedMan = roundUp(first23Digits);
		} else {
			// lastThreeDigits.equals("100")
			if (man.charAt(22) == '1') {
				// round up
				roundedMan = roundUp(first23Digits);
			} else {
				// round down
				roundedMan = first23Digits;
			}
		}
		return roundedMan;
	}

	/**
	 * Rounds up the given binary string if and only if rounding up does not add any
	 * more bits. If rounding up results in adding another bit, the original string
	 * is returned.
	 * 
	 * @param binStr a String representing the binary value to be rounded up
	 * @return a String representing the rounded binary string or the original
	 *         String if rounding cannot be done without adding more bits
	 */
	public String roundUp(String binStr) {
		int currIndex = binStr.length() - 1;
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
			// do nothing; the number cannot be rounded up without adding more bits
		} else {
			retStr = retStr.substring(0, currIndex) + "1" + zeroes;
		}
		return retStr;
	}

}

/**
 * An EventHandler class that knows how to convert user-input from an 
 * IEEE-754 32-bit binary value to a decimal float.
 */
class IEEEToDec implements EventHandler<ActionEvent> {
	private String inputstr;
	private Label label;
	private int sign;
	private int exponent;
	private double mantissa;
	private TextField tf;
	
	/**
	 * Sets the sign, exponent and mantissa of the IEEE 754 floating point number.
	 * 
	 * @param tf    A text field containing the IEEE 754 floating point number.
	 * @param label A label that will display the decimal representation of the
	 *              number or an error message
	 */
	public IEEEToDec(TextField tf, Label label) {
		this.label = label;
		this.tf = tf;
		this.inputstr = tf.getText();

	}
	
	/**
	 * Sets the sign, exponent and mantissa of the IEEE 754 floating point number.
	 * 
	 * @param tf    A text field containing the IEEE 754 floating point number to convert.
	 * @param label A label that will display the decimal representation of the
	 *              number or an error message
	 */
	public boolean check_spcl(String input) {
		if (input.equals("01111111100000000000000000000000")) {
			this.label.setText("Decimal Representation: INFINITY");
			return true;
		} else if (input.equals("11111111100000000000000000000000")) {
			this.label.setText("Decimal Representation: -INFINITY");
			return true;
		} else if (input.equals("01111111111111111111111111111111")) {
			this.label.setText("Decimal Representation: NAN");
			return true;

		}
		return false;

	}
	
	/**
	 * Sets the sign to be 0 if the IEEE 754 floating point number begins with a 0
	 * or 1 if it begins with a 1. Otherwise, displays an error message.
	 * 
	 * @param input The first character of the IEEE 754 floating point number.
	 */
	public void getsign(char input) {
		if (input == '0') {
			this.sign = 0;
		} else if (input == '1') {
			this.sign = 1;
		} else {
			this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
			return;
		}
	}
	
	/**
	 * If possible, determines the exponent of the IEEE 754 floating point number.
	 * Otherwise, displays an error message.
	 * 
	 * @param input A string that is the binary representation of the exponent of
	 *              the IEEE 754 floating point number.
	 */
	public void getexponent(String input) {
		try {
			int decimal = Integer.parseInt(input, 2);
			this.exponent = decimal - 127;

		}

		catch (Exception e) {
			this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
			return;
		}

	}
	
	/**
	 * If possible, determines the mantissa of the IEEE 754 floating point number.
	 * Otherwise, displays an error message.
	 * 
	 * @param input A string that is the binary representation of the mantissa of
	 *              the IEEE 754 floating point number.
	 */
	public void getmantissa(String input) {
		double decimal = 0;
		int num = 0;
		for (int i = -1; i > -24; i--) {
			char chr = input.charAt(num);
			int value = Integer.parseInt(String.valueOf(chr));
			if (value > 1) {
				this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
				break;
			}
			decimal += (value * Math.pow(2, i));
			num++;
		}
		this.mantissa = decimal;
	}
	
	/**
	 * Responds to a user clicking the Convert to IEEE button.
	 * 
	 * @param event An ActionEvent object that indicates that the Convert to IEEE
	 *              button has been clicked.
	 */
	public void handle(ActionEvent event) {
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
		double number = Math.pow(-1, this.sign) * (1 + this.mantissa) * Math.pow(2, this.exponent);
		this.label.setText("Value Actually Stored in Float: " + String.valueOf(number));
		return;

	}

}
