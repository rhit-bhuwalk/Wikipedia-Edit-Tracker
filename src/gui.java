
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class gui {

	private static SQLConnection databaseConnection;
	private static DatabaseEditor dbe;
	private static DataImportFromFile dataImporter;
	private static JTable tableView = new JTable();
	private static JFrame viewFrame;
	private static JFrame frame;
	private static JPanel cards;
	private final static String LOGINPANEL = "Card with login";
	private final static String MAINPANEL = "Card with main page";
	private static boolean isAdmin = false;
//	private static ArrayList<Integer> rowsEdited = new ArrayList<Integer>();
	private static LinkedHashSet<Integer> rowsEdited = new LinkedHashSet<Integer>();


	private static JTextField usernameText; // I think needs to be static to access in anonymous classes
	private static JLabel userLabel;
	private static JLabel passwordLabel;
	private static JPasswordField passwordText;
	
	private static int startViewableIndex = 1;
	private static int viewableIndexOffset = 20;
	private static int finalViewableIndex = 20;
	private static int page = 1;
	private static int pages = 1;
	private static RowFilter<DefaultTableModel, Integer> pageSizeFilter;
	private static TableRowSorter<DefaultTableModel> sorter;
	private static DefaultTableModel model;
	
	public static void main(String args[]){
		//Creating the Frame
        frame = new JFrame("Database Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        
        cards = new JPanel(new CardLayout());
        frame.add(cards);
        testLoginScreen(); 

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
	
	private static void buildMainPage() {
		if (!isAdmin) {// skips viewer straight to views
			generateView(0, null, false);
			frame.setVisible(false);

		} else {
			JPanel mainPanel = new JPanel(new BorderLayout()); // Needed to add stuff to top, center, south
			cards.add(mainPanel, MAINPANEL);
			CardLayout c1 = (CardLayout) cards.getLayout();
			c1.show(cards, MAINPANEL);
			JMenuBar mb = new JMenuBar();
			JMenu m1 = new JMenu("FILE");
			JMenu m3 = new JMenu("Add to Database");
			JMenu m4 = new JMenu("Views");
			JMenu m5 = new JMenu("Tables");
			// Goal is to list new tabs for insertion purposes

			mb.add(m1);
			mb.add(m3);
			mb.add(m5);
			mb.add(m4);

			JMenuItem m11 = new JMenuItem("Open");
			JMenuItem editorMenu = new JMenuItem("Editor View");
			JMenuItem editsMenu = new JMenuItem("Edits View");
			JMenuItem organizationMenu = new JMenuItem("Organization View");
			JMenuItem categoryMenu = new JMenuItem("Category View");
			JMenuItem accountMenu = new JMenuItem("Account View");
			JMenuItem ipAddressMenu = new JMenuItem("IP Address View");

			JMenuItem accountTMenu = new JMenuItem("Account");// Admin table views
			JMenuItem categoryTMenu = new JMenuItem("Category");
			JMenuItem editorTMenu = new JMenuItem("Editor");
			JMenuItem editsTMenu = new JMenuItem("Edits");
			JMenuItem inTMenu = new JMenuItem("In");
			JMenuItem ipAddressTMenu = new JMenuItem("IP Address");
			JMenuItem organizationTMenu = new JMenuItem("Organization");
			JMenuItem pageTMenu = new JMenuItem("Page");

			m5.add(accountTMenu);
			m5.add(categoryTMenu);
			m5.add(editorTMenu);
			m5.add(editsTMenu);
			m5.add(inTMenu);
			m5.add(ipAddressTMenu);
			m5.add(organizationTMenu);
			m5.add(pageTMenu);

			accountTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(0, null, true);
				}

			});
			categoryTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(1, null, true);
				}

			});
			editorTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(2, null, true);
				}

			});
			editsTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(3, null, true);
				}

			});
			inTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(4, null, true);
				}

			});
			ipAddressTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(5, null, true);
				}

			});
			organizationTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(6, null, true);
				}

			});
			pageTMenu.addActionListener(new ActionListener() {// Admin table views
				public void actionPerformed(ActionEvent e) {
					generateView(7, null, true);
				}

			});

			// this happens when file is opened
			m11.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					int returnValue = fileChooser.showOpenDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						dataImporter.setFile(fileChooser.getSelectedFile());
						dataImporter.readFile();
					}
				}

			});
			m1.add(m11);
			m4.add(editorMenu);
			m4.add(editsMenu);
			m4.add(organizationMenu);
			m4.add(categoryMenu);
			m4.add(accountMenu);
			m4.add(ipAddressMenu);

			JMenuItem m31 = new JMenuItem("Account");
			
			//can simplify stuff by doing like this m31.addActionListener(e -> AddEditor(true));
			m31.addActionListener(new ActionListener() {// Move if supposed to go elsewhere, I have no experience with
														// this
				public void actionPerformed(ActionEvent e) {
					AddEditor(true);
				}

			});

			editorMenu.addActionListener(new ActionListener() {// Move if supposed to go elsewhere, I have no experience
																// with this
				public void actionPerformed(ActionEvent e) {
					generateView(0, null, false);
				}

			});

			editsMenu.addActionListener(new ActionListener() {// Move if supposed to go elsewhere, I have no experience
																// with this
				public void actionPerformed(ActionEvent e) {
					generateView(1, null, false);
				}

			});

			organizationMenu.addActionListener(new ActionListener() {// Move if supposed to go elsewhere, I have no
																		// experience with this
				public void actionPerformed(ActionEvent e) {
					generateView(2, null, false);
				}

			});

			categoryMenu.addActionListener(new ActionListener() {// Move if supposed to go elsewhere, I have no
																	// experience with this
				public void actionPerformed(ActionEvent e) {
					generateView(3, null, false);
				}

			});
			
			accountMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					generateView(4, null, false);
				}
				
			});
			
			ipAddressMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					generateView(5, null, false);
				}
				
			});

			m3.add(m31);

			JMenuItem m32 = new JMenuItem("IP");

			m32.addActionListener(new ActionListener() {// Add IP
				public void actionPerformed(ActionEvent e) {
					AddEditor(false);
				}
			});

			m3.add(m32);

			JMenuItem m33 = new JMenuItem("Organization");

			m33.addActionListener(new ActionListener() {// Add IP
				public void actionPerformed(ActionEvent e) {
					AddOrganization();
				}
			});

			m3.add(m33);

		    JMenuItem m34 = new JMenuItem("Page");
		    m34.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		AddPage();
		    	}
		    });
		    
			m3.add(m34);
			
			JMenuItem m35 = new JMenuItem("Category");
		    m35.addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		AddCategory();
		    	}
		    });
		    
			m3.add(m35);

			// Adding Components to the mainPanel, which is by default borderLayout.
			mainPanel.add(BorderLayout.NORTH, mb);
		}
	}

	private static void testLoginScreen() { // Notes for later, could use cardlayout and swap to logged in screen from
											// this
		JPanel loginMenu = new JPanel();
		cards.add(loginMenu, LOGINPANEL);
		String[] loginOptions = { "viewer", "admin" };// Removed other
		JComboBox loginOptionChooser = new JComboBox(loginOptions);
		final JPanel userPanel = new JPanel();
		final JPanel passwordPanel = new JPanel();
		userLabel = new JLabel("UserName: ");
		userPanel.add(userLabel);
		usernameText = new JTextField(25);
		userPanel.add(usernameText);
		passwordLabel = new JLabel("Password: ");
		passwordPanel.add(passwordLabel);
		passwordText = new JPasswordField(25);
		passwordPanel.add(passwordText);
		JButton logIn = new JButton("Log in");
		// I think this sets what is preselected when you start it
		loginOptionChooser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				int index = cb.getSelectedIndex();
				if (index == 1) { // Admin login, is basically just the connection
					passwordPanel.setVisible(true);
					userPanel.setVisible(true);
					isAdmin = true;
				} else if (index == 0) { // Viewer login
					passwordPanel.setVisible(false);
					userPanel.setVisible(false);
					usernameText.setText("WETAppViewer");
					isAdmin = false;
				} else { // Other, clear any default values
					usernameText.setText("");
					passwordText.setText("");
				}

			} //random comment to resolveGitIssue

		});
		loginOptionChooser.setSelectedIndex(0); // Order of this and the listener influences if it will auto-fill at the
												// start or not

		logIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) { // Will log in and change the card if successful
//				System.out.println("You clicked submit with " + usernameText.getText() + " and " + String.valueOf(passwordText.getPassword()));
				if (!isAdmin) {
					String password = "";
					try {
						password = AESDecrypter.decrypt("g+wCkvhaqedx02E2oFD91pmVwaH/+XRusSi4wVKUsL8=");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Login unsuccessful");
						e1.printStackTrace();
					}
				
					passwordText.setText(password);
				}
				databaseConnection = new SQLConnection(usernameText.getText(), passwordText.getText());
				if (databaseConnection.getConnection() == null) {
					JOptionPane.showMessageDialog(null, "Login unsuccessful");
				} else { // Now we can properly set up the databaseEditor
					dbe = new DatabaseEditor(databaseConnection);
					dataImporter = new DataImportFromFile(dbe);
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							if (isAdmin) {
								databaseConnection.closeConnection();
							}
						}
					});
					buildMainPage();
				}
			}

		});

		loginMenu.add(loginOptionChooser);
		loginMenu.add(userPanel);
		loginMenu.add(passwordPanel);
		userPanel.setVisible(false);
		passwordPanel.setVisible(false);
		loginMenu.add(logIn, BorderLayout.PAGE_END);

	}

	private static void generateView(final int idx, final String filter, final boolean isTable) {
		final intWrapper index = new intWrapper(idx);
		viewFrame = new JFrame("View");
		JPanel mainPanel = new JPanel();
		JLabel viewTypeLabel = new JLabel("Current View:  ");
		final JComboBox viewTypeBox = new JComboBox();

		if (isTable) {
			viewTypeBox.setModel(new DefaultComboBoxModel(new String[] { "Account", "Category", "Editor", "Edits", "In",
					"IP_Address", "Organization", "Page" }));
		} else {
			viewTypeBox
					.setModel(new DefaultComboBoxModel(new String[] { "Editor", "Edits", "Organization", "Category","Account","IP Address"}));
		}
		viewTypeBox.setSelectedIndex(idx);
		JButton viewChangeButton = new JButton("Change View");

		final JTextField textFilter = new JTextField(20);
		JLabel filterLabel = new JLabel("Filter by ");
//		JScrollPane filterScroll = new JScrollPane();
		JButton newViewFilterButton = new JButton("Filter");

		JButton applyUpdatesButton = new JButton("Apply Updates");
		JButton nextPageButton = new JButton("Next Page");
		JButton previousPageButton = new JButton("Previous");
		final JPanel textFilterPanel = new JPanel();
		final JLabel pageNumberLabel = new JLabel();

		textFilterPanel.add(viewTypeLabel);
		textFilterPanel.add(viewTypeBox);
		textFilterPanel.add(viewChangeButton);
		textFilterPanel.add(filterLabel);
		viewFrame.add(textFilterPanel, BorderLayout.PAGE_START);
		textFilterPanel.add(textFilter);
		textFilterPanel.add(newViewFilterButton);
		textFilterPanel.add(previousPageButton);
		textFilterPanel.add(nextPageButton);
		textFilterPanel.add(pageNumberLabel);

		BorderLayout layout = new BorderLayout();
		layout.setHgap(10);
		layout.setVgap(10);
		mainPanel.setLayout(layout);

//		mainPanel.add(mainLabel, BorderLayout.NORTH);
		viewFrame.add(mainPanel);
		viewFrame.setSize(1250,408);
		
		if (!isTable) { //is a view
			model = dbe.generateView(databaseConnection, index.getIndex(), filter, null);
			tableView.setModel(model);
			tableView.setRowSorter(null);
			pages = (int) Math.ceil(tableView.getRowCount()/21.0);
			pageNumber(pageNumberLabel, page, pages);
			updatePageSizeFilter();
			}
		else { //is table
			model = dbe.generateTable(databaseConnection,index.getIndex(), filter, null);
			tableView.setModel(model);
			tableView.setRowSorter(null);
			pages = (int) Math.ceil(tableView.getRowCount()/21.0);
			pageNumber(pageNumberLabel, page, pages);
			updatePageSizeFilter();
			textFilterPanel.add(applyUpdatesButton);
			
		}
		
		ArrayList<String> collumns = new ArrayList<String>();
		for (int i = 0; i < tableView.getColumnCount(); i++) {
			collumns.add(tableView.getColumnName(i));
		}
		final JComboBox filterList = new JComboBox(collumns.toArray());

		newViewFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableView.setRowSorter(null); //done out of caution
				startViewableIndex = 0;
				finalViewableIndex = viewableIndexOffset; //should give proper number displayable
				tableView.setRowSorter(null);
				String filterString = textFilter.getText();
				if (filterString.isBlank() || filterString.isEmpty()) {
					filterString = null;
				}
//				viewFrame.remove(tableView);
				System.out.println("Current index: " + index.getIndex());
				if(!isTable) {
					model = dbe.generateView(databaseConnection,index.getIndex() , filterString,filterList.getSelectedItem().toString());
					tableView.setModel(model);
					pages = (int) Math.ceil(tableView.getRowCount()/21.0);
					page = 1;
					pageNumber(pageNumberLabel, page, pages);
					updatePageSizeFilter();
				}
				else {
					model = dbe.generateTable(databaseConnection, index.getIndex(), filterString, filterList.getSelectedItem().toString());
					tableView.setModel(model);
					pages = (int) Math.ceil(tableView.getRowCount()/21.0);
					page = 1;
					pageNumber(pageNumberLabel, page, pages);
					updatePageSizeFilter();
				}
			} //so git will have a change
		});

		viewChangeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rowsEdited.clear();
				tableView.setRowSorter(null);
				page = 1;
				startViewableIndex = 0;
				finalViewableIndex = viewableIndexOffset; //should give proper number displayable
				if(isTable) { //maybe tableView.setRowSelectionInternal() needs to check size of rows
//					tableView.clearSelection();
					model = dbe.generateTable(databaseConnection, viewTypeBox.getSelectedIndex(), null, "");
					tableView.setModel(model);
					tableView.getModel().addTableModelListener(new TableModelListener() {

						public void tableChanged(TableModelEvent e) {
							rowsEdited.add(e.getFirstRow());
						}
					});
					pages = (int) Math.ceil(tableView.getRowCount()/21.0);
					pageNumber(pageNumberLabel, page, pages);
					updatePageSizeFilter();
					
					
				}
				else {
					model = dbe.generateView(databaseConnection, viewTypeBox.getSelectedIndex(), null, "");
					tableView.setModel(model);
					pages = (int) Math.ceil(tableView.getRowCount()/21.0);
					pageNumber(pageNumberLabel, page, pages);
					updatePageSizeFilter();
					
				}
				ArrayList<String> newCollumns = new ArrayList<String>();
				for (int i = 0; i < tableView.getColumnCount(); i++) {
					newCollumns.add(tableView.getColumnName(i));
				}

				filterList.setModel(new DefaultComboBoxModel(newCollumns.toArray()));
				index.setIndex(viewTypeBox.getSelectedIndex());
			}
		});
		textFilterPanel.add(filterList,4);
		mainPanel.add(tableView, BorderLayout.CENTER);
		tableView.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				rowsEdited.add(e.getFirstRow());
			}
		});

		applyUpdatesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbe.updateTable(tableView.getModel(), rowsEdited, index.getIndex());
				rowsEdited.clear();// Clears LinkedHashSet after calling update
			}
		});
		
		nextPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(page<pages) {
				startViewableIndex+=viewableIndexOffset;
				finalViewableIndex+=viewableIndexOffset;
				updatePageSizeFilter();
				page++;
				pageNumber(pageNumberLabel, page, pages);
//				tableView.setRowSelectionInterval(start, idx);
				}
			}
		});
		
		previousPageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(startViewableIndex-viewableIndexOffset >= 0) { //don't run if it will be out of bounds
					startViewableIndex-=viewableIndexOffset;
					finalViewableIndex-=viewableIndexOffset;
					page--;
					pageNumber(pageNumberLabel, page, pages);
					updatePageSizeFilter();
				}
			}
		});
		
		
		viewFrame.setVisible(true);

		viewFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!isAdmin) {
					databaseConnection.closeConnection();
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		viewFrame.setLocationRelativeTo(null);
	};
	
	private static void updatePageSizeFilter() {
		//remove old sorter first
		if(model == null) return; //don't run if there is not model; can't break stuff
		tableView.setRowSorter(null);
		pageSizeFilter = new RowFilter<DefaultTableModel, Integer>() {
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
//				System.out.println("The entry id is " + entry.getIdentifier());
				if(entry.getIdentifier()!=null) {
					if(entry.getIdentifier()==0)
						return true;
					if(entry.getIdentifier()<=finalViewableIndex&&entry.getIdentifier()>=startViewableIndex) {
						return true;					
					} else {
						return false;
					}					
				} else {
					return false;
				}
			}
		};
		tableView.setModel(model);
		sorter = new TableRowSorter<DefaultTableModel>(model);
		sorter.setRowFilter(pageSizeFilter);
		tableView.setRowSorter(sorter);
	}
	
	private static void pageNumber(JLabel pageLabel, int page, int pages)
	{
		pageLabel.setText("Page " + page + "/" + pages);
		
	}
	
	private static void AddEditor(boolean inAccountView) {
		// Creates Editor View
		JFrame frameEditor = new JFrame("Add Editor");
//        frameEditor.setAlwaysOnTop(true); //Maybe too intrusive

		JPanel standingPanel = new JPanel();
		JLabel standingLabel = new JLabel("                     Standing:                     ");
		final JTextField standingField = new JTextField(10); // final for temporary hotfix
		standingPanel.add(standingLabel);
		standingPanel.add(standingField);

		JPanel isBotPanel = new JPanel();
		final JCheckBox isBotCheckBox = new JCheckBox("Is Robot?");
		isBotPanel.add(isBotCheckBox);

		JPanel organizationPanel = new JPanel();
		JLabel organizationLabel = new JLabel("Organization: ");
		final JTextField organizationField = new JTextField(15);
		organizationPanel.add(organizationLabel);
		organizationPanel.add(organizationField);
		

		if (inAccountView) {
			frameEditor.setLayout(new GridLayout(1, 5));
			frameEditor.setSize(1250, 100);
			JPanel usernamePanel = new JPanel();
			JLabel usernameLabel = new JLabel("Username: ");
			final JTextField usernameField = new JTextField(15);

			JPanel creationDatePanel = new JPanel();
			JLabel creationDateLabel = new JLabel("Account Creation Date (yyyy/mm/dd): ");
			final JTextField creationDateField = new JTextField(8);

			usernamePanel.add(usernameLabel);
			usernamePanel.add(usernameField);

			creationDatePanel.add(creationDateLabel);
			creationDatePanel.add(creationDateField);

			JButton createAccountButton = new JButton("Add Account");
			createAccountButton.setSize(50, 20);
			JPanel buttonPanel = new JPanel();

			createAccountButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String userText = usernameField.getText();
					if (userText.isBlank()) {// Sets to null for SPROC
						userText = null;
					}
					String standingText = standingField.getText();
					if (standingText.isBlank()) {
						standingText = null;
					}
					String organizationName = organizationField.getText();
					if (organizationName.isBlank()) {
						organizationName = null;
					}
					String creationDateText = creationDateField.getText();
					if (creationDateText.isBlank()) {
						creationDateText = null;
					}
					Boolean isRobot = isBotCheckBox.isSelected();
					dbe.addAccount(userText, standingText, isRobot, organizationName, creationDateText);
				}
			});
			buttonPanel.add(createAccountButton);

			frameEditor.add(usernamePanel);
			frameEditor.add(standingPanel);
			frameEditor.add(isBotPanel);
			frameEditor.add(organizationPanel);
			frameEditor.add(creationDatePanel);
			frameEditor.add(buttonPanel);
		} // hmm
		else {
			frameEditor.setLayout(new GridLayout(1, 4));
			frameEditor.setSize(950, 100);

			JPanel ipPanel = new JPanel();
			JLabel ipLabel = new JLabel("IP Address (xxx.xxx.xxx.xxx): ");
			final JTextField ipField = new JTextField(15);
			ipPanel.add(ipLabel);
			ipPanel.add(ipField);

			JButton createIPButton = new JButton("Add IP Address");
			createIPButton.setSize(50, 20);
			JPanel buttonPanel = new JPanel();

			createIPButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String ipText = ipField.getText();
					if (ipText.isBlank()) {// Sets to null for SPROC
						ipText = null;
					}
					String standingText = standingField.getText();
					if (standingText.isBlank()) {
						standingText = null;
					}
					String organizationName = organizationField.getText();
					if (organizationName.isBlank()) {
						organizationName = null;
					}
					Boolean isRobot = isBotCheckBox.isSelected();
					dbe.addIPAddress(standingText, isRobot, organizationName, ipText);

				}
			});
			buttonPanel.add(createIPButton);

			frameEditor.add(ipPanel);
			frameEditor.add(standingPanel);
			frameEditor.add(isBotPanel);
			frameEditor.add(organizationPanel);
			frameEditor.add(buttonPanel);
		}
		frameEditor.setVisible(true);
		frameEditor.setLocationRelativeTo(null);
		frameEditor.revalidate();

	}

	private static void AddOrganization() {
		JFrame frameEditor = new JFrame("Add Organization");
		JPanel organizationNamePanel = new JPanel();
		JLabel organizationNameLabel = new JLabel("Organization Name");
		final JTextField organizationNameField = new JTextField(16); // final for temporary hotfix
		organizationNamePanel.add(organizationNameLabel);
		organizationNamePanel.add(organizationNameField);

		JPanel leanPanel = new JPanel();
		JLabel leanLabel = new JLabel("			              	Political Lean		              		");
		final JTextField leanField = new JTextField(2);
		
		JButton createAccountButton = new JButton("Add Organization");
		createAccountButton.setSize(50, 20);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(createAccountButton);
		
		createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String orgName = organizationNameField.getText();
                if (orgName.isBlank()) {// Sets to null for SPROC
                	orgName = null;
                }
                String lean = leanField.getText();
                if (lean.isBlank()) {
                	lean = null;
                }
                
                dbe.addOrganization(orgName, lean);
            }
        });
		
		leanPanel.add(leanLabel);
		leanPanel.add(leanField);
		frameEditor.setSize(600, 100);
		frameEditor.setLayout(new GridLayout(1, 6));
		frameEditor.add(organizationNamePanel, BorderLayout.WEST);
		frameEditor.add(leanPanel,BorderLayout.WEST);
		frameEditor.add(buttonPanel, BorderLayout.EAST);
		frameEditor.setVisible(true);
		frameEditor.setLocationRelativeTo(null);
		frameEditor.revalidate();
	}
	
    private static void AddPage() {
        JFrame frameEditor = new JFrame("Add Page");
        JPanel pageUrlPanel = new JPanel();
        JLabel pageUrlLabel = new JLabel("Page URL");
        final JTextField pageUrlField = new JTextField(16); // final for temporary hotfix
        pageUrlPanel.add(pageUrlLabel);
        pageUrlPanel.add(pageUrlField);

 

        JPanel pageNamePanel = new JPanel();
        JLabel pageNameLabel = new JLabel("Page Name");
        final JTextField pageNameField = new JTextField(16);
        
        JButton addPageButton = new JButton("Add Page");
        addPageButton.setSize(50, 20);
        JPanel addButton = new JPanel();
        addButton.add(addPageButton);
        
        addPageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pageURLtext = pageUrlField.getText();
                if (pageURLtext.isBlank()) {// Sets to null for SPROC
                	pageURLtext = null;
                }
                String pageNameText = pageNameField.getText();
                if (pageNameText.isBlank()) {
                	pageNameText = null;
                }
                
                dbe.addPage(pageURLtext, pageNameText);
            }
        });
        
        
        pageNamePanel.add(pageNameLabel);
        pageNamePanel.add(pageNameField);
        frameEditor.setSize(650, 100);
        frameEditor.setLayout(new GridLayout(1, 6));
        frameEditor.add(pageUrlPanel); //, BorderLayout.WEST
        frameEditor.add(pageNamePanel); //,BorderLayout.CENTER
        frameEditor.add(addButton);  //, BorderLayout.EAST
        frameEditor.setVisible(true);
        frameEditor.setLocationRelativeTo(null);
        frameEditor.revalidate();
    }
    
    private static void AddCategory() {
    	 JFrame frameEditor = new JFrame("Add Category");
         JPanel catPanel = new JPanel();
         JLabel pageUrlLabel = new JLabel("Category Name");
         final JTextField pageUrlField = new JTextField(16); // final for temporary hotfix
         catPanel.add(pageUrlLabel);
         catPanel.add(pageUrlField);

  

         JPanel pageNamePanel = new JPanel();
         JLabel pageNameLabel = new JLabel("Super Category Name");
         final JTextField pageNameField = new JTextField(16);
         
         JButton addPageButton = new JButton("Add Category");
         addPageButton.setSize(50, 20);
         JPanel addButton = new JPanel();
         addButton.add(addPageButton);
         
         addPageButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 String categoryName = pageUrlField.getText();
                 if (categoryName.isBlank()) {// Sets to null for SPROC
                	 categoryName = null;
                 }
                 String supercat = pageNameField.getText();
                 if (supercat.isBlank()) {
                	 supercat = null;
                 }
                 
                 dbe.addCategory(categoryName, supercat);
             }
         });
         
         
         pageNamePanel.add(pageNameLabel);
         pageNamePanel.add(pageNameField);
         frameEditor.setSize(650, 100);
         frameEditor.setLayout(new GridLayout(1, 6));
         frameEditor.add(catPanel); 
         frameEditor.add(pageNamePanel);
         frameEditor.add(addButton); 
         frameEditor.setVisible(true);
         frameEditor.setLocationRelativeTo(null);
         frameEditor.revalidate();
    }

}

class intWrapper {
	private int index;

	public intWrapper(int idx) {
		index = idx;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int idx) {
		index = idx;
	}
}

