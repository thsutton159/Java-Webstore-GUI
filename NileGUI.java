/* Thomas Sutton
 * An Event-driven Enterprise Simulation
 * Last Update: Wednesday, September 18, 2024
 */

//imports that were necessary on my device
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class NileGUI extends JFrame{
	
	private static final int WIDTH = 700;
	private static final int HEIGHT = 600;
	
	private JLabel		blankLabel, cart, idLabel, qtyLabel, userControls, details, subtotal, spaceHolder; // creates labels
	private JTextField 	/*blankTextField, */idTextField, qtyTextField, detailsTextField, subtotalTextField, cartTextField1, 
	cartTextField2, cartTextField3, cartTextField4, cartTextField5; // creates text fields
	private JButton		/*blankButton, */processB, confirmB, finishB, viewB, newB, exitB; // creates buttons
	
	
	//creates handlers for buttons
	private ProcessButtonHandler procBHandler; 	// handler for 'search for item #x' button
	private ConfirmButtonHandler confBHandler;	// handler for 'add item to cart' button
	private ViewButtonHandler viewBHandler;		// handler for '' button
	private FinishButtonHandler finiBHandler;	// handler for '' button
	private NewButtonHandler newBHandler; 		// handler for '' button
	private ExitButtonHandler exitBHandler;		// handler for 'exit' button
	
	String[] initialInfo, shoppingCart = new String[5], completeList = new String[5], traCSVList = new String[5];
	
	static String itemID = "", itemTitle = "", itemDescription = "", itemDescTraCSV = "", quantityStr = "", outputStr = "", maxArraySizeStr = "", 
			priceStr = "", subtotalStr = "", orderSubtotalStr = ""; // strings to hold item information
	
	static double itemPrice = 0, itemSubtotal = 0, orderSubtotal = 0, orderTotal = 0; // doubles for value totals at the end of the program
	
	static int itemQuantity = 0, itemCount = 0; // integers for item quantity and items in the cart
	
	final static double TAX_RATE = 0.060, DISCOUNT_FOR_5 = 0.10, DISCOUNT_FOR_10 = 0.15, DISCOUNT_FOR_15 = 0.20; // final doubles for various rates

	DecimalFormat df = new DecimalFormat("#.00");
	
	File transactions = new File("src/transactions.csv"); // creates transactions file
	BufferedWriter out = null; // creates a buffered writer
	
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMYYYYHHmmss");
	DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("MMMM dd, YYYY hh:mm:ss a");
	
	// --------------------------------------------------------------------------------------
	//this
	public NileGUI() { // constructor for the GUI
		
		setTitle("Nile.com - Fall 2024"); // sets the GUI title
		setSize(WIDTH, HEIGHT); // sets the GUI frame size
		
		//instantiates the blank objects
		//blankButton = new JButton(" ");
		blankLabel = new JLabel(" ", SwingConstants.RIGHT);
		//blankTextField = new JTextField();
		
		// creates labels for the program
		idLabel = new JLabel("Enter item ID for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT); // item id label 
		idLabel.setForeground(Color.yellow); // changes the text color of this label
		qtyLabel = new JLabel("Enter quantity for Item #" + (itemCount + 1), SwingConstants.RIGHT); // item quantity label
		qtyLabel.setForeground(Color.yellow); // change the text color of this label
		details = new JLabel("Details for Item #" + (itemCount + 1), SwingConstants.RIGHT); // item details label
		details.setForeground(Color.red);
		subtotal = new JLabel("Current Subtotal for " + (itemCount) + " item(s)", SwingConstants.RIGHT); // sub total label
		subtotal.setForeground(Color.blue);
		cart = new JLabel("Your Shopping Cart is Currently Empty", SwingConstants.CENTER);
		cart.setForeground(Color.red);
		cart.setFont(new Font("Serif", Font.BOLD, 20));

		spaceHolder = new JLabel("Space Holder");
		spaceHolder.setForeground(Color.darkGray);
		
		// creates label and font for user controls text in the south panel
		userControls = new JLabel("USER CONTROLS");
		userControls.setForeground(Color.white);
		userControls.setFont(new Font("Serif", Font.PLAIN, 20));
		
		// instantiates the JTextField objects
		//blankTextField = new JTextField();
		idTextField = new JTextField();
		qtyTextField = new JTextField();
		detailsTextField = new JTextField();
		subtotalTextField = new JTextField();
		cartTextField1 = new JTextField();
		cartTextField2 = new JTextField();
		cartTextField3 = new JTextField();
		cartTextField4 = new JTextField();
		cartTextField5 = new JTextField();
		
		// instantiates the processB button and handler (search for item)
		processB = new JButton("Search For Item #" + (itemCount + 1));
		procBHandler = new ProcessButtonHandler();
		processB.addActionListener(procBHandler);
		
		// instantiates the confirmB button and handler (add item to cart)
		confirmB = new JButton("Add Item #" + (itemCount + 1) + " To Cart");
		confBHandler = new ConfirmButtonHandler();
		confirmB.addActionListener(confBHandler);
		
		// instantiates the finishB button and handler (check out)
		finishB = new JButton("Check Out");
		finiBHandler = new FinishButtonHandler();
		finishB.addActionListener(finiBHandler);
		
		// instantiates the viewB button and handler (view cart)
		viewB = new JButton("View Cart");
		viewBHandler = new ViewButtonHandler();
		viewB.addActionListener(viewBHandler);
		
		// instantiates the newB button and handler (empty cart)
		newB = new JButton("Empty Cart - Start A New Order");
		newBHandler = new NewButtonHandler();
		newB.addActionListener(newBHandler);
		
		//instantiates the exitB button and handler
		exitB = new JButton("Exit (Close App)");
		exitBHandler = new ExitButtonHandler();
		exitB.addActionListener(exitBHandler);
		
		// initial settings for the buttons and text fields
		processB.setEnabled(true);
		confirmB.setEnabled(false);
		viewB.setEnabled(false);
		finishB.setEnabled(false);
		newB.setEnabled(true);
		exitB.setEnabled(true);
		detailsTextField.setEnabled(false);
		subtotalTextField.setEnabled(false);
		cartTextField1.setEnabled(false);
		cartTextField2.setEnabled(false);
		cartTextField3.setEnabled(false);
		cartTextField4.setEnabled(false);
		cartTextField5.setEnabled(false);
		
		Container pane = getContentPane(); // creates a pane to hold everything
		
		// creates grid layouts for the layout - variables (#rows, #columns, horizontal space, vertical space)
		GridLayout grid6by2 = new GridLayout(6, 2, 8, 4);
		GridLayout grid6by1 = new GridLayout(6, 1, 8, 6);
		GridLayout grid5by2 = new GridLayout(5, 2, 8, 6);
		//GridLayout grid1by1 = new GridLayout(1, 1, 2, 2);
		//GridLayout grid6by2 = new GridLayout(6, 2, 2, 2);
		
		// creates 3 panels in a north, center, and south layout
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		// set layouts for the panels
		northPanel.setLayout(grid6by2);
		centerPanel.setLayout(grid6by1);
		southPanel.setLayout(grid5by2);
		
		// adds buttons and labels to the north panel
		northPanel.add(idLabel);
		northPanel.add(idTextField);
		northPanel.add(qtyLabel);
		northPanel.add(qtyTextField);
		northPanel.add(details);
		northPanel.add(detailsTextField);
		northPanel.add(subtotal);
		northPanel.add(subtotalTextField);
		
		// adds buttons and labels to the center panel
		centerPanel.add(cart);
		centerPanel.add(cartTextField1);
		centerPanel.add(cartTextField2);
		centerPanel.add(cartTextField3);
		centerPanel.add(cartTextField4);
		centerPanel.add(cartTextField5);
		
		// adds buttons and labels to the south panel
		southPanel.add(userControls);
		southPanel.add(blankLabel);
		southPanel.add(processB);
		southPanel.add(confirmB);
		southPanel.add(viewB);
		southPanel.add(finishB);
		southPanel.add(newB);
		southPanel.add(exitB);
		
		// adds panels to panes using a border layout
		pane.add(northPanel, BorderLayout.NORTH);
		pane.add(centerPanel, BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		
		//styles the panes
		pane.setBackground(Color.darkGray);
		northPanel.setBackground(Color.darkGray);
		centerPanel.setBackground(Color.gray);
		southPanel.setBackground(Color.blue);
		
		// center's the frame for the user
		centerFrame(WIDTH, HEIGHT);
		
	} // end of myGui() constructor;
	// --------------------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------------------
	// function to center the frame on the user's screen
	public void centerFrame(int frameWidth, int frameHeight) {
		
		Toolkit myToolkit = Toolkit.getDefaultToolkit(); // creates a toolkit object
		
		Dimension screen = myToolkit.getScreenSize(); // creates dimension object based on the user's screen size
		
		// assigns the frame's position
		int xPositionOfFrame = (screen.width - frameWidth) / 2; 
		int yPositionOfFrame = (screen.height - frameHeight) / 2;
		
		// method to center the frame on the user's screen
		setBounds(xPositionOfFrame, yPositionOfFrame, frameWidth, frameHeight);
		
	} // end of centerFrame();
	// -----------------------------------------------------------------------------------------
	
	
	// -----------------------------------------------------------------------------------------
	// button handler for the process button
	private class ProcessButtonHandler implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			itemID = idTextField.getText();
			itemQuantity = Integer.parseInt(qtyTextField.getText());
			quantityStr = qtyTextField.getText();
			
			File inputFile = new File("inventory.csv");
			FileReader inputFileReader = null;
			BufferedReader inputBuffReader = null;
			Scanner aScanner = null;
			
			String inventoryLine;
			String itemId = null;
			
			boolean found = false;
			
			try {
				inputFileReader = new FileReader(inputFile);
				inputBuffReader = new BufferedReader(inputFileReader);
				
				inventoryLine = inputBuffReader.readLine(); //reads from the file
				
				while(inventoryLine != null) {
					
					aScanner = new Scanner(inventoryLine).useDelimiter("\\s*,\\s");
					itemId = aScanner.next();
					
					if(itemId.equals(itemID)) {
						found = true;
						break;
					} else {
						inventoryLine = inputBuffReader.readLine(); //read next line
					}
				} //end of while loop
				
				if(found == false) {
					
					JOptionPane.showMessageDialog(null, "Item ID " + itemID + " not in file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
					
				} else {	
					
					initialInfo = inventoryLine.split(", ");
					
					if((initialInfo[2]).equals("false")) {
				
						JOptionPane.showMessageDialog(null, "Sorry... that item is out of stock. Please try another item.", 
								"Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
						idTextField.setText("");
						qtyTextField.setText("");
					} else if (itemQuantity > (Integer.parseInt(initialInfo[3])) ){
						
						JOptionPane.showMessageDialog(null, "Insufficient stock. Only " + Integer.parseInt(initialInfo[3]) + " on hand. Please reduce the quantity", 
								"Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
						
					} else {
						
						processB.setEnabled(false); // turns the search for item button off
						confirmB.setEnabled(true); // turns the add item to button cart
						
						itemPrice = Double.parseDouble(initialInfo[4]); // converts item price from a string to a double and stores it
						itemTitle = initialInfo[1]; // places the item title in a separate associated string
						priceStr = initialInfo[4];// places item price in a separate associated string
						
						if(itemQuantity <= 4) {
							
							itemSubtotal = itemPrice * itemQuantity; // subtotal for this item
							subtotalStr = df.format(itemSubtotal);  // converts subtotal from double to string
							
							itemDescription = String.join(" ", itemID, itemTitle, priceStr, quantityStr,  "0%", subtotalStr);
							itemDescTraCSV = String.join(", ", itemID, itemTitle, priceStr, quantityStr, "0%", subtotalStr);
							detailsTextField.setEnabled(true);
							detailsTextField.setText(itemDescription);
							
						}
						
						if((itemQuantity >= 5) && (itemQuantity <= 9)) {
							
							itemSubtotal = itemPrice * itemQuantity * 0.9; // subtotal for this specific item
							subtotalStr = df.format(itemSubtotal);

							
							itemDescription = String.join(" ", itemID, itemTitle, priceStr, quantityStr,  "10%", subtotalStr);
							itemDescTraCSV = String.join(", ", itemID, itemTitle, priceStr, quantityStr, "10%", subtotalStr);
							detailsTextField.setEnabled(true);
							detailsTextField.setText(itemDescription);
							
						}
						
						if((itemQuantity >= 10) && (itemQuantity <= 14)) {
							
							itemSubtotal = itemPrice * itemQuantity * 0.85; // subtotal for this specific item
							subtotalStr = df.format(itemSubtotal);
							
							itemDescription = String.join(" ", itemID, itemTitle, priceStr, quantityStr,  "15%", subtotalStr);
							itemDescTraCSV = String.join(", ", itemID, itemTitle, priceStr, quantityStr, "15%", subtotalStr);
							detailsTextField.setEnabled(true);
							detailsTextField.setText(itemDescription);
						}
						
						if(itemQuantity >= 15) {
							
							itemSubtotal = itemPrice * itemQuantity * 0.8; // subtotal for this specific item
							subtotalStr = df.format(itemSubtotal);
							
							itemDescription = String.join(" ", itemID, itemTitle, priceStr, quantityStr,  "20%", subtotalStr);
							itemDescTraCSV = String.join(", ", itemID, itemTitle, priceStr, quantityStr, "20%", subtotalStr);
							detailsTextField.setEnabled(true);
							detailsTextField.setText(itemDescription);
							
						}
						
					}
					
				}
			}
			
			catch(FileNotFoundException fileNotFoundException) {
				JOptionPane.showMessageDialog(null, "Error: File not found", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
			}
			
			catch(IOException iOException) {
				JOptionPane.showMessageDialog(null,  "Error: Problem reading from file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);;
			}
			
		}
		
	}
	
	// -----------------------------------------------------------------------------------------
	
	
	// -----------------------------------------------------------------------------------------
	// button handler for the confirm button
	private class ConfirmButtonHandler implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			itemCount++; // updates item count;
			
			shoppingCart[itemCount - 1] = itemDescription;
			traCSVList[itemCount - 1] = itemDescTraCSV;
			
			subtotalTextField.setEnabled(true); // enables the subtotal text field for editing
			
			orderSubtotal += itemSubtotal;
			orderSubtotalStr = df.format(orderSubtotal); // converts double order subtotal to string
			
			subtotalTextField.setText(orderSubtotalStr);
			
			// update buttons
			processB.setEnabled(true);
			confirmB.setEnabled(false);
			viewB.setEnabled(true);
			finishB.setEnabled(true);
			
			// updates used labels and button labels
			processB.setText("Search For Item #" + (itemCount + 1));
			confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");
			cart.setText("Your Shopping Cart Currently Contains " + (itemCount) +" item(s)");
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1));
			
			idTextField.setText("");
			qtyTextField.setText("");
			subtotal.setText("Current Subtotal for " + (itemCount) + " item(s)"); 
			
			// settings for when the cart has less than 4 items
			switch(itemCount) {
				
				case 1 :
					cartTextField1.setEnabled(true);
					outputStr = String.join(" ", "Item " + itemCount + " - SKU:", itemID + ", Desc:", 
							itemTitle + ",",  "Price Ea. $" + priceStr + ", Qty:", quantityStr + ", Total: $" + itemSubtotal);
					cartTextField1.setText(outputStr);
					break;
				
				case 2 :
					cartTextField2.setEnabled(true);
					outputStr = String.join(" ", "Item " + itemCount + " - SKU:", itemID + ", Desc:", 
							itemTitle + ",",  "Price Ea. $" + priceStr + ", Qty:", quantityStr + ", Total: $" + itemSubtotal);
					cartTextField2.setText(outputStr);
					break;
				
				case 3 :
					cartTextField3.setEnabled(true);
					outputStr = String.join(" ", "Item " + itemCount + " - SKU:", itemID + ", Desc:", 
							itemTitle + ",",  "Price Ea. $" + priceStr + ", Qty:", quantityStr + ", Total: $" + itemSubtotal);
					cartTextField3.setText(outputStr);
					break;
				
				case 4:
					cartTextField4.setEnabled(true);
					outputStr = String.join(" ", "Item " + itemCount + " - SKU:", itemID + ", Desc:", 
							itemTitle + ",",  "Price Ea. $" + priceStr + ", Qty:", quantityStr + ", Total: $" + itemSubtotal);
					cartTextField4.setText(outputStr);
					break;
			
				default :
				
					idTextField.setEnabled(false);
					idTextField.setBackground(Color.darkGray);
					qtyTextField.setEnabled(false);
					qtyTextField.setBackground(Color.darkGray);
					processB.setEnabled(false);
					confirmB.setEnabled(false);
					cartTextField5.setEnabled(true);
		
					outputStr = String.join(" ", "Item " + itemCount + " - SKU:", itemID + ", Desc:", 
							itemTitle + ",",  "Price Ea. $" + priceStr + ", Qty:", quantityStr + ", Total: $" + itemSubtotal);
					cartTextField5.setText(outputStr);
					break;
			}
		}
			
	} //end of 	
	// -----------------------------------------------------------------------------------------
		
	
	// -----------------------------------------------------------------------------------------
	// button handler for the view button
	private class ViewButtonHandler implements ActionListener {
			
		public void actionPerformed(ActionEvent e) {
			
			JOptionPane.showMessageDialog(null, shoppingCart, "Nile Dot Com - Current Shopping Cart Status", JOptionPane.NO_OPTION);
		}

	}
	// -----------------------------------------------------------------------------------------
		
	
	// -----------------------------------------------------------------------------------------
	// button handler for the finish button
	private class FinishButtonHandler implements ActionListener {
	
		public void actionPerformed(ActionEvent e) {
			
			// updates button usability
			processB.setEnabled(false);
			confirmB.setEnabled(false);
			finishB.setEnabled(false);
			
			for(int i = 0; i < itemCount; i++) {
				completeList[i] = String.join(" ", Integer.toString( i+ 1 ) + ".", shoppingCart[i]);
			}
			
			// creates the checkout pane based on itemCount
			switch (itemCount) {
			
				case 1:
					JOptionPane.showMessageDialog(null, 
							"Number of line items: " + itemCount +"\n"
							+ "\n"
							+ "Item# / ID / Title / Price / Quantity / Disc% / Subtotal:\n"
							+"\n"
							+ completeList[0]
							+ "\n"
							+ "\n"
							+ "Order Subtotal:        " + df.format(orderSubtotal) +"\n"
							+ "\n"
							+ "Tax rate:           6%\n"
							+ "\n"
							+ "Tax amount:        " + df.format(orderSubtotal * TAX_RATE) +"\n"
							+ "\n"
							+ "ORDER TOTAL:       " + df.format((orderSubtotal * TAX_RATE) + orderSubtotal) + "\n"
							+ "\n"
							+ "Thank you for shopping at Nile Dot Com!", 
							"Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
					break;
					
				case 2:
					JOptionPane.showMessageDialog(null, 
							"Number of line items: " + itemCount +"\n" + "\n"
							+ "Item# / ID / Title / Price / Quantity / Disc% / Subtotal:\n"
							+"\n"
							+ completeList[0] + "\n"
							+ completeList[1]
							+ "\n"
							+ "\n"
							+ "Order Subtotal:        " + df.format(orderSubtotal) +"\n"
							+ "\n"
							+ "Tax rate:           6%\n"
							+ "\n"
							+ "Tax amount:        " + df.format(orderSubtotal * TAX_RATE) +"\n"
							+ "\n"
							+ "ORDER TOTAL:       " + df.format((orderSubtotal * TAX_RATE) + orderSubtotal) + "\n"
							+ "\n"
							+ "Thank you for shopping at Nile Dot Com!", 
							"Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
					break;
					
				case 3:
					JOptionPane.showMessageDialog(null, 
							"Number of line items: " + itemCount +"\n"
							+ "\n"
							+ "Item# / ID / Title / Price / Quantity / Disc% / Subtotal:\n"
							+"\n"
							+ completeList[0] + "\n"
							+ completeList[1] + "\n"
							+ completeList[2]
							+ "\n"
							+ "\n"
							+ "Order Subtotal:        " + df.format(orderSubtotal) +"\n"
							+ "\n"
							+ "Tax rate:           6%\n"
							+ "\n"
							+ "Tax amount:        " + df.format(orderSubtotal * TAX_RATE) +"\n"
							+ "\n"
							+ "ORDER TOTAL:       " + df.format((orderSubtotal * TAX_RATE) + orderSubtotal) + "\n"
							+ "\n"
							+ "Thank you for shopping at Nile Dot Com!", 
							"Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
					break;
					
				case 4:
					JOptionPane.showMessageDialog(null, 
							"Number of line items: " + itemCount +"\n"
							+ "\n"
							+ "Item# / ID / Title / Price / Quantity / Disc% / Subtotal:\n"
							+"\n"
							+ completeList[0] + "\n"
							+ completeList[1] + "\n"
							+ completeList[2] + "\n"
							+ completeList[3]
							+ "\n"
							+ "\n"
							+ "Order Subtotal:        " + df.format(orderSubtotal) +"\n"
							+ "\n"
							+ "Tax rate:           6%\n"
							+ "\n"
							+ "Tax amount:        " + df.format(orderSubtotal * TAX_RATE) +"\n"
							+ "\n"
							+ "ORDER TOTAL:       " + df.format((orderSubtotal * TAX_RATE) + orderSubtotal) + "\n"
							+ "\n"
							+ "Thank you for shopping at Nile Dot Com!", 
							"Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
					break;
					
				default:
					JOptionPane.showMessageDialog(null, 
							"Number of line items: " + itemCount +"\n"
							+ "\n"
							+ "Item# / ID / Title / Price / Quantity / Disc% / Subtotal:\n"
							+"\n"
							+ completeList[0] + "\n"
							+ completeList[1] + "\n"
							+ completeList[2] + "\n"
							+ completeList[3] + "\n"
							+ completeList[4]
							+ "\n"
							+ "\n"
							+ "Order Subtotal:        " + df.format(orderSubtotal) +"\n"
							+ "\n"
							+ "Tax rate:           6%\n"
							+ "\n"
							+ "Tax amount:        " + df.format(orderSubtotal * TAX_RATE) +"\n"
							+ "\n"
							+ "ORDER TOTAL:       " + df.format((orderSubtotal * TAX_RATE) + orderSubtotal) + "\n"
							+ "\n"
							+ "Thank you for shopping at Nile Dot Com!", 
							"Nile Dot Com - FINAL INVOICE", JOptionPane.INFORMATION_MESSAGE);
					break;
			
			} // end of switch statement
			
			LocalDateTime now = LocalDateTime.now();
			
			try (BufferedWriter out = new BufferedWriter(new FileWriter("transactions.csv", true))){
				
			    for(int i=0; i < itemCount; i++) {
					String temp = String.join(", ", dtf.format(now), traCSVList[i], dtf2.format(now));
					out.write(temp);
					out.write("\n");
			    }
				
			    out.write("\n");
			    out.close();
			}

			catch (IOException e1) {
			    System.err.println("Error: " + e1.getMessage());
			}
			
		}
		
	}	
	// -----------------------------------------------------------------------------------------
	
	
	// -----------------------------------------------------------------------------------------
	// button handler for the new button
	private class NewButtonHandler implements ActionListener {
	
		public void actionPerformed(ActionEvent e) {
			
			// resets variables
			itemCount = 0; 
			orderSubtotal = 0;
			itemSubtotal = 0;
			itemQuantity = 0;
			
			// updates text fields 
			cartTextField1.setText("");
			cartTextField2.setText("");
			cartTextField3.setText("");
			cartTextField4.setText("");
			cartTextField5.setText("");
			idTextField.setText("");
			idTextField.setEnabled(true);
			qtyTextField.setText("");
			qtyTextField.setEnabled(true);
			detailsTextField.setText("");
			subtotalTextField.setText("");
			cart.setText("Your Shopping Cart is Currently Empty");
			
			// updates labels
			idLabel.setText("Enter item ID for Item #" + (itemCount + 1) + ":");
			qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1));
			details.setText("Details for Item #" + (itemCount + 1));
			subtotal.setText("Current Subtotal for " + (itemCount) + " item(s)");
			
			// updates button labels
			processB.setText("Search For Item #" + (itemCount + 1));
			confirmB.setText("Add Item #" + (itemCount + 1) + " To Cart");
			
			// updates buttons
			processB.setEnabled(true);
			confirmB.setEnabled(false);
			viewB.setEnabled(false);
			finishB.setEnabled(false);
		}
	}
	// -----------------------------------------------------------------------------------------
		
	
	// -----------------------------------------------------------------------------------------
	// button handler for the exit button
	private class ExitButtonHandler implements ActionListener {
	
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	// -----------------------------------------------------------------------------------------
	
	
	// -----------------------------------------------------------------------------------------
	// Main Method, what creates the window where the user can interact with the store
	public static void main(String [] args) {
		JFrame myStore = new NileGUI();
		myStore.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myStore.setVisible(true);
		
	} // end of main();
	// ----------------------------------------------------------------------------------------
	
} //end of class NileGUI


