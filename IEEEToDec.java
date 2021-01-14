import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * An implementation of an EventHandler that converts user-input from an
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
	 * Initialize the attributes belonging to the IEEEToDec class.
	 * 
	 * @param tf    A text field containing the IEEE 754 floating point number.
	 * @param label A label that will display the decimal representation of the
	 *              number.
	 */
	public IEEEToDec(TextField tf, Label label) {
		this.label = label;
		this.tf = tf;
		this.inputstr = tf.getText();

	}

	/**
	 * Sets the label to the error message if the user input is invalid.
	 */
	public void error() {
		// Set the label to display the error message
		this.label.setText("Invalid entry. Please enter a 32-bit binary value.");
	}

	/**
	 * Determines whether the IEEE 754 input is a special value. If it is, updates
	 * the label to what special value the input is and returns true. Otherwise,
	 * returns false.
	 * 
	 * @param tf    A text field containing the IEEE 754 floating point number to
	 *              convert.
	 * @param label A label that will display the decimal representation of the
	 * @return true if the input is a special value, false otherwise.
	 */
	public boolean check_spcl(String input) {
		// The decimal representation is equal to infinity
		if (input.equals("01111111100000000000000000000000")) {
			this.label.setText("Decimal Representation: INFINITY");
			return true;
			// The decimal representation is equal to -infinity
		} else if (input.equals("11111111100000000000000000000000")) {
			this.label.setText("Decimal Representation: -INFINITY");
			return true;
			// The decimal representation is equal to NAN
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
		// Assign sign as 0 if the first character is 0
		if (input == '0') {
			this.sign = 0;
			// Assign sign as 1 if the first character is 1
		} else if (input == '1') {
			this.sign = 1;
		} else {
			error();
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
			// Convert from binary to decimal
			int decimal = Integer.parseInt(input, 2);
			// Subtract the bias to determine the exponent
			this.exponent = decimal - 127;

		}

		catch (Exception e) {
			error();
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
		// Converts the mantissa to a decimal value
		for (int i = -1; i > -24; i--) {
			char chr = input.charAt(num);
			int value = Integer.parseInt(String.valueOf(chr));
			if (value > 1) {
				error();
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
			error();
			return;
		}
		// If the user input is a special value
		if (check_spcl(this.inputstr)) {
			return;
		}
		// If the user input is not a special value
		getsign(this.inputstr.charAt(0));
		getexponent(this.inputstr.substring(1, 9));
		getmantissa(this.inputstr.substring(9));
		double number = Math.pow(-1, this.sign) * (1 + this.mantissa) * Math.pow(2, this.exponent);
		this.label.setText("Value Actually Stored in Float: " + String.valueOf(number));
		return;
	}
}
