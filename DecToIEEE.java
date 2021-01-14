import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * An implementation of an EventHandler that converts user-input from a decimal
 * float to an IEEE-754 32-bit binary value.
 */
class DecToIEEE implements EventHandler<ActionEvent> {

	private TextField tField;
	private float decimalVal;
	private Label resultLabel;
	private boolean isSpecial;
	private boolean isValid;

	/**
	 * Initialize the attributes belonging to the DecToIEEE class.
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
	 * Checks whether the input String represents a special value or valid decimal
	 * number and attempts to set the decimal value based on the given String. If
	 * the input is invalid, the decimal value is set to a default of 0.0f
	 * 
	 * @param inputStr a String representing the decimal number the user wishes to
	 *                 convert to binary.
	 */
	public void setDecVal(String inputStr) {
		// check if inputStr is a special value
		if (inputStr.toUpperCase().equals("INFINITY") || inputStr.toUpperCase().equals("-INFINITY")
				|| inputStr.toUpperCase().equals("NAN")) {
			this.decimalVal = 0.0f; // placeholder value
			this.isSpecial = true;
			this.isValid = true;
		} else {
			this.isSpecial = false;
			// attempt to convert input to a float
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
		// handle the case where the input from the text field is invalid
		if (!this.isValid) {
			this.resultLabel.setText("Invalid input. " + inputStr + " is not a decimal number.");
			return;
		} // else: proceed; the input is valid
			// handle the special value cases
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
			// handle the special zero cases
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
		// calculate the integer that gives the nearest power of 2 to <input>
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
		// the IEEE standard is to bias the exponent value by 127
		// find the corresponding binary string for the biased exponent
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
		// calculate 26 bits for the un-rounded Mantissa
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
	 * Returns the binary string rounded to the nearest even. PRECONDITION: the
	 * given String <man> is a 26-bit binary String
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
