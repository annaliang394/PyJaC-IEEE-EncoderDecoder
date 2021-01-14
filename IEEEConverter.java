
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
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
		VBox pane2 = new VBox(10);
		pane2.setAlignment(Pos.TOP_CENTER);
		Scene helpscene = new Scene(pane2, 800, 600);

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
		use = new Label("                     How to Use:\n" + "Enter the number (a decimal real number or 32-bit\n"
				+ "binary value) into the text field. Then, click the\n"
				+ "corresponding button to convert your value.\n"
				+ "Note: When converting from binary to decimal, there\n"
				+ "may be a slight rounding error from the conversion\n"
				+ "since not every decimal number has a finite representation\n" + "in binary." + "\n");
		use.setFont(font);
		use.setTextAlignment(TextAlignment.LEFT);
		spcl = new Label(
				"                 Special Values:\n " + "Enter 'INFINITY', '-INFINITY', or 'NAN' to get their\n"
						+ "special IEEE-754 32-bit binary values. This converter\n"
						+ "also recognizes the difference between 0.0 and -0.0" + "\n");
		spcl.setFont(font);
		spcl.setTextAlignment(TextAlignment.LEFT);
		instrct = new Label("                      How it works:\n" + "When converting from a decimal to an IEEE-754\n"
				+ "encoded binary, the number can be represented as\n"
				+ "as: (−1)^{sign bit}*(1+fraction)*2^{exponent - bias}\n"
				+ "The bits are allocated as follows (left to right):\n"
				+ "   *1 bit for the sign (1 if the decimal is negative,\n " + "    0 if positive)\n"
				+ "   *8 bits for the exponent (with bias of 127)\n" + "   *23 bits for the fraction ('mantissa')\n"
				+ "" + "The exponent and mantissa are binary representations of the\n"
				+ "decimal value in base-2 scientific notation. For more \n"
				+ "information, visit: https://urlzs.com/DZD97" + "\n");
		instrct.setFont(font);
		instrct.setTextAlignment(TextAlignment.LEFT);
		contact = new Label("Feel free to contact us at IEEEConverter@gmail.com.");
		contact.setFont(font);
		contact.setTextAlignment(TextAlignment.LEFT);

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
