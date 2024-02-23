package enchcracker;

import enchcracker.cracker.AbstractSingleSeedCracker;
import enchcracker.cracker.JavaSingleSeedCracker;
import enchcracker.swing.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class EnchCrackerWindow extends StyledFrameMinecraft {

	private static final ResourceBundle RES_BUNDLE = ResourceBundle.getBundle("i18n.EnchantmentCracker", new UTF8ResourceBundleControl());
	private static final NumberFormat DEC_FORMAT = DecimalFormat.getInstance();
	private boolean waitingOnAnEnchant = false;
	private float oldSecondsPercent = 0;
	private boolean oneEnchantMeansTopEnchant = true;
	private final boolean[][] itemIdCanHaveMatEnchTier = new boolean[35][8];
	private int darknessConsumed = 0;
	//private int darknessSteps;
	private long startTime = 0;
	private boolean timerIsRunning = false;

	public static URL getFile(String name) {
		File f = new File("data/"+name);
		if (f.exists() && f.isFile()) {
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				// should not happen
			}
		}
		return Thread.currentThread().getContextClassLoader().getResource("data/"+name);
	}

	private static String verText() {
		try (Scanner scanner = new Scanner(getFile("version.txt").openStream())) {
			return "v" + scanner.next();
		} catch (IOException e) {
			return "[Unknown Version]";
		}
	}

	public static String translate(String key) {
		return RES_BUNDLE.getString(key).trim();
	}

	@SuppressWarnings("FieldCanBeLocal")
	private final JPanel contentPane;
	private final JTextField bookshelvesTextField;
	private final JTextField slot1TextField;
	private final JTextField slot2TextField;
	private final JTextField slot3TextField;
	private final JLabel slot1EnchantField;
	private final JLabel slot2EnchantField;
	private final JLabel slot3EnchantField;
	private final JLabel currentStepLabel;

	private final ProgressButton progressBar;
	private final JTextField xpSeed1TextField = new FixedTextField();
	private final JTextField xpSeed2TextField = new FixedTextField();
	//private final JTextField levelTextField;
	private final JPanel enchList;
	//private JLabel darknessCounter = new JLabel("");
	private final JScrollPane scrollPane;
	private final ProgressButton btnCalculate;
	private final ProgressButton btnStartTimer;
	private final ProgressButton darknessCycle = new DarknessButton("button");
	private final ProgressButton darknessCycle2 = new DarknessButton("button");
	private final ProgressButton btnDone;
	private final JCheckBox maxDummiesCheckBox = new JCheckBox("2x",false);
	private long playerSeed, oldSeed;
	private int playerSeedStepCount = 0;
	private final int maxStacksThrown = 6;	//How many stacks of items i'm willing to throw (can reduce/increase calc time)
	private final ArrayList<Long> playerSeeds = new ArrayList<>();
	private boolean foundPlayerSeed = false;

	private JLabel outDrop, outBook, outSlot, outDummies, outDarkness;//, outItemType, outMaterialTier;
	private int newStepCount = 0;
	private int chosenSlot = -1;
	private final Enchantments myEnchantments;
	private final int[][] bookshelfLvlRanges = new int[6][16];
	final Items[] itemToEnch = {Items.HELMET};
	final Materials[] material = {Materials.BOW};

	private static AbstractSingleSeedCracker singleSeedCracker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {



		System.setProperty("sun.java2d.uiScale", "1");
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			// Write to log
			Log.fatal("An unexpected error occurred", e);

			// Display message dialog
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("An unexpected error occurred!"));
			panel.add(new JLabel(e.toString()));
			panel.add(Box.createVerticalStrut(20));
			panel.add(new JLabel("Please report this on the GitHub page at:"));
			JLabel link = new JLabel(
					"<html><a href = \"https://github.com/Earthcomputer/EnchantmentCracker/issues\">https://github.com/Earthcomputer/EnchantmentCracker/issues</a></html>");
			link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			link.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					browse("https://github.com/Earthcomputer/EnchantmentCracker/issues");
				}
			});
			panel.add(link);
			panel.add(new JLabel("Please include the log file (enchcracker.log) in your bug report."));
			JOptionPane.showMessageDialog(null, panel, "Enchantment Crasher", JOptionPane.ERROR_MESSAGE);

			// And exit the program
			System.exit(1);
		});

		// Close the file logger after program has ended
		Runtime.getRuntime().addShutdownHook(new Thread(Log::cleanupLogging));

		printSystemDetails();

		if (System.getProperty("sun.arch.data.model").equals("32")) {
			int resp = JOptionPane.showConfirmDialog(null, translate("program.warn32"), translate("program.name"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (resp != JOptionPane.OK_OPTION) System.exit(0);
		}

		// Note: Native cracker disabled as it is currently slower.
		// Initialize seed cracker
		//singleSeedCracker = new NativeSingleSeedCracker();
		//if (!singleSeedCracker.initCracker()) {
			singleSeedCracker = new JavaSingleSeedCracker();
			if (!singleSeedCracker.initCracker()) {
				return;
			}
		//}

		try {
			EnchCrackerWindow frame = new EnchCrackerWindow();
			frame.setVisible(true);
		} catch (Exception e) {
			Log.fatal("Exception creating frame", e);
		}
	}

	private static void printSystemDetails() {
		Log.info("Enchantment cracker version " + verText());
		Log.info("System details:");
		Log.info("OS = " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		Log.info("Arch (either OS/Java) = " + System.getProperty("os.arch"));
		Log.info("Java = " + System.getProperty("java.version"));
		if (System.getProperties().containsKey("sun.arch.data.model")) {
			Log.info("Java arch = " + System.getProperty("sun.arch.data.model"));
		}
	}

	public String getRemainingSeedsText(int val) {
		String[] translations = {"enchCrack.remaining", "enchCrack.remaining.thousand", "enchCrack.remaining.million", "enchCrack.remaining.billion"};

		double factor = 1.0 / 1000;
		int n = val;
		int suffix = -1;
		while (n > 0) {
			n /= 1000;
			factor *= 1000;
			suffix++;
		}

		double significand = val / factor;
		int multiplier = 1;
		while (significand < 100) {
			significand *= 10;
			multiplier *= 10;
		}
		significand = Math.round(significand);
		significand /= multiplier;
		return String.format(translate(translations[suffix]), DEC_FORMAT.format(significand));
	}

	private static CardLayout cards = new CardLayout();
	public EnchCrackerWindow() {
		super(
			cards,
			new String[]{"FindSeed", "Manip", "About"},
			new String[]{translate("tab.enchantmentCracker"), translate("tab.enchantmentCalculator"), translate("tab.about")}
		);

        myEnchantments = new Enchantments();
		Properties properties = new Properties();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("enchantments.properties"));
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String[] values = properties.getProperty("bookshelfLvlRanges").split(";");
		for(int i=0; i<values.length; i++) {
			String[] values2 = values[i].split(",");
			for(int j=0; j<values2.length; j++)
				bookshelfLvlRanges[i][j] = Integer.parseInt(values2[j]);
		}
		values = properties.getProperty("canHaveMatEnch").split(",");
		for(int i=0; i<values.length; i++) {
			String[] values2 = values[i].split("");
			for(int j=0; j<values2.length; j++)
				itemIdCanHaveMatEnchTier[i][j] = values2[j].equals("1");
		}



		setTitle(translate("program.name"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setBackground(new Color(255,255,255,0));

		contentPane.setLayout(cards);
		setContentPane(contentPane);
		setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {
			{
				setImplicitDownCycleTraversal(false);
			}

			@Override
			protected boolean accept(Component component) {
				return (component instanceof JButton || component instanceof JTextField || component instanceof JComboBox) && super.accept(component);
			}
		});

		// --- Seed Cracker section

		ImagePanel findSeedPanel = new ImagePanel("pane1");
		findSeedPanel.setLayout(null);
		contentPane.add(findSeedPanel, "FindSeed");

		int resetW = 80;
		progressBar = new ProgressButton("button");
		progressBar.setText(translate("enchCrack.check"));
		progressBar.setBounds(0, findSeedPanel.getSize().height - progressBar.getPreferredSize().height, findSeedPanel.getSize().width - resetW - 6, progressBar.getPreferredSize().height);
		findSeedPanel.add(progressBar);

		DocumentFilter numberFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() + string.length() > 2) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 2) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		DocumentFilter levelNumberFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() + string.length() > 3) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("\\d*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 3) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		DocumentFilter hexFilter = new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("[\\da-fA-F]*")) return;
				if (fb.getDocument().getLength() + string.length() > 8) return;
				super.insertString(fb, offset, string, attr);
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
				if (!string.matches("[\\da-fA-F]*")) return;
				if (fb.getDocument().getLength() - length + string.length() > 8) return;
				super.replace(fb, offset, length, string, attr);
			}
		};

		bookshelvesTextField = new FixedTextField();
		bookshelvesTextField.setFont(MCFont.standardFont);
		((PlainDocument) bookshelvesTextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(bookshelvesTextField);
		bookshelvesTextField.setBounds(225, 46, 30, 20);
		bookshelvesTextField.setToolTipText(translate("enchCrack.bookshelves.tooltip"));
		bookshelvesTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if(foundPlayerSeed)
					showBookLvlsInFirstPane();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(foundPlayerSeed)
					showBookLvlsInFirstPane();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}
		});

		slot1TextField = new FixedTextField();
		slot1TextField.setFont(MCFont.standardFont);
		slot1TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		((PlainDocument)slot1TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot1TextField);
		slot1TextField.setBounds(290, 130, 30, 20);
		slot1TextField.setToolTipText(translate("enchCrack.xpCost1.tooltip"));

		slot2TextField = new FixedTextField();
		slot2TextField.setFont(MCFont.standardFont);
		slot2TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		((PlainDocument)slot2TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot2TextField);
		slot2TextField.setBounds(290, 168, 30, 20);
		slot2TextField.setToolTipText(translate("enchCrack.xpCost2.tooltip"));

		slot3TextField = new FixedTextField();
		slot3TextField.setFont(MCFont.standardFont);
		slot3TextField.setHorizontalAlignment(SwingConstants.RIGHT);
		((PlainDocument)slot3TextField.getDocument()).setDocumentFilter(numberFilter);
		findSeedPanel.add(slot3TextField);
		slot3TextField.setBounds(290, 206, 30, 20);
		slot3TextField.setToolTipText(translate("enchCrack.xpCost3.tooltip"));

		slot1EnchantField = new JLabel("");
		slot2EnchantField = new JLabel("");
		slot3EnchantField = new JLabel("");
		slot1EnchantField.setFont(MCFont.standardFont);
		slot2EnchantField.setFont(MCFont.standardFont);
		slot3EnchantField.setFont(MCFont.standardFont);
		slot1EnchantField.setHorizontalAlignment(SwingConstants.RIGHT);
		slot2EnchantField.setHorizontalAlignment(SwingConstants.RIGHT);
		slot3EnchantField.setHorizontalAlignment(SwingConstants.RIGHT);
		slot1EnchantField.setBounds(90+50,130,150,20);
		slot2EnchantField.setBounds(90+50,168,150,20);
		slot3EnchantField.setBounds(90+50,206,150,20);
		findSeedPanel.add(slot1EnchantField);
		findSeedPanel.add(slot2EnchantField);
		findSeedPanel.add(slot3EnchantField);

		progressBar.setToolTipText(translate("enchCrack.check.tooltip"));
		progressBar.addActionListener(event -> {
			int bookshelves, slot1, slot2, slot3;
			getBookshelvesTextField().setBackground(Color.white);
			slot1TextField.setBackground(Color.white);
			slot2TextField.setBackground(Color.white);
			slot3TextField.setBackground(Color.white);
			try {
				bookshelves = Integer.parseInt(bookshelvesTextField.getText());
				slot1 = Integer.parseInt(slot1TextField.getText());
				slot2 = Integer.parseInt(slot2TextField.getText());
				slot3 = Integer.parseInt(slot3TextField.getText());
			} catch (NumberFormatException e) {
				Log.info("Add info failed, fields had invalid numbers");
				return;
			}



			if (bookshelves < 0 || bookshelves > 15) {
				Log.info("Add info failed, bookshelf count invalid");
				bookshelvesTextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			int low1 = bookshelfLvlRanges[0][bookshelves];
			int high1 = bookshelfLvlRanges[1][bookshelves];
			int low2 = bookshelfLvlRanges[2][bookshelves];
			int high2 = bookshelfLvlRanges[3][bookshelves];
			int low3 = bookshelfLvlRanges[4][bookshelves];
			int high3 = bookshelfLvlRanges[5][bookshelves];

			if (slot1 < low1 || slot1 > high1) {
				Log.info("Add info failed, slot 1 count invalid");
				slot1TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			if (slot2 < low2 || slot2 > high2) {
				Log.info("Add info failed, slot 2 count invalid");
				slot2TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			if (slot3 < low3 || slot3 > high3) {
				Log.info("Add info failed, slot 3 count invalid");
				slot3TextField.setBackground(new Color(1.0F, 0.3F, 0.0F));
				return;
			}

			Log.info("Added info, b = " + bookshelves + ", s1 = " + slot1 + ", s2 = " + slot2 + ", s3 = " + slot3);

			singleSeedCracker.abortAndThen(() -> {
				// First time is different because otherwise we have to store all 2^32 initial seeds
				boolean firstTime = singleSeedCracker.isFirstTime();
				singleSeedCracker.setFirstTime(false);

				// Start brute-forcing thread
				Thread thread;
				if (firstTime) {
					thread = new Thread(() -> {
						progressBar.setProgress(0f);
						singleSeedCracker.firstInput(bookshelves, slot1, slot2, slot3);
						int possibleSeeds = singleSeedCracker.getPossibleSeeds();
						Log.info("Reduced possible seeds to " + possibleSeeds);
						singleSeedCracker.setRunning(false);
						switch (possibleSeeds) {
							case 0:
								progressBar.setText(translate("enchCrack.impossible"));
								progressBar.setProgress(Float.NaN);
								break;
							case 1:
								//This only happens if we find the xpseed directly on the first set of tested bookshelf lvls
								progressBar.setText(String.format(translate("enchCrack.result"), singleSeedCracker.getSeed()));
								progressBar.setProgress(0f);
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
									//this assumes the player enchants his second dummy in the same darkness cycle in which the first xpseed is found
									//meaning you dont hesitate enchanting your (second) dummy after finding the first half of the seed
									darknessConsumed += currentDarknessSteps();
			//						darknessCounter.setText(String.valueOf(currentDarknessSteps()));
									resetCracker();
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								progressBar.setText(getRemainingSeedsText(possibleSeeds));
								progressBar.setProgress(-1f);
								break;
						}
					});
				} else {
					thread = new Thread(() -> {
						singleSeedCracker.addInput(bookshelves, slot1, slot2, slot3);
						int possibleSeeds = singleSeedCracker.getPossibleSeeds();
						Log.info("Reduced possible seeds to " + possibleSeeds);
						singleSeedCracker.setRunning(false);
						switch (possibleSeeds) {
							case 0:
								progressBar.setText(translate("enchCrack.impossible"));
								progressBar.setProgress(Float.NaN);
								break;
							case 1:
								progressBar.setText(String.format(translate("enchCrack.result"), singleSeedCracker.getSeed()));
								progressBar.setProgress(Float.NaN);
								if (xpSeed1TextField.getText().isEmpty()) {
									xpSeed1TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
									darknessConsumed += currentDarknessSteps();
									//darknessCounter.setText(String.valueOf(currentDarknessSteps()));
									resetCracker();
								} else if (xpSeed2TextField.getText().isEmpty()) {
									xpSeed2TextField.setText(String.format("%08X", singleSeedCracker.getSeed()));
								}
								break;
							default:
								progressBar.setText(getRemainingSeedsText(possibleSeeds));
								progressBar.setProgress(-1f);
								break;
						}
					});
				}
				thread.setDaemon(true);
				singleSeedCracker.setRunning(true);
				thread.start();

				// Start progress bar thread
				if (firstTime) {
					thread = new Thread(() -> {
						while (singleSeedCracker.isRunning()) {
							progressBar.setProgress((float)singleSeedCracker.getSeedsSearched() / 4294967296f);
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
						bookshelvesTextField.setText("");
						slot1TextField.setText("");
						slot2TextField.setText("");
						slot3TextField.setText("");
					});
				} else {
					thread = new Thread(() -> {
						while (singleSeedCracker.isRunning()) {
							// need this check, as it's possible this line might be hit before seedsSearched is set back to 0
							if (singleSeedCracker.getSeedsSearched() <= singleSeedCracker.getPossibleSeeds()) {
								progressBar.setProgress((float) singleSeedCracker.getSeedsSearched() / (float) singleSeedCracker.getPossibleSeeds());
							}
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
						bookshelvesTextField.setText("");
						slot1TextField.setText("");
						slot2TextField.setText("");
						slot3TextField.setText("");
					});
				}
				thread.setDaemon(true);
				thread.start();
			});
		});
		btnCalculate = new ProgressButton("button");
		btnStartTimer = new ProgressButton("button");

		JButton btnResetCracker = new ProgressButton("button");
		btnResetCracker.setText(translate("enchCrack.reset"));
		btnResetCracker.setBounds(findSeedPanel.getSize().width - resetW, findSeedPanel.getSize().height - progressBar.getPreferredSize().height, resetW, progressBar.getPreferredSize().height);
		findSeedPanel.add(btnResetCracker);
		btnResetCracker.setToolTipText(translate("enchCrack.reset.tooltip"));
		btnResetCracker.addActionListener(event -> {
			Log.info("Reset the cracker");
			singleSeedCracker.abortAndThen(() -> {
				resetCracker();
				progressBar.setText(translate("enchCrack.check"));
			});
		});

		JLabel xpl1 = new JLabel(translate("enchCrack.xpSeed1"));
		MCFont.setFontFor(xpl1);
		xpl1.setBounds(0, 0, 140, 20);
		findSeedPanel.add(xpl1);


		xpSeed1TextField.setFont(MCFont.standardFont);
		((PlainDocument)xpSeed1TextField.getDocument()).setDocumentFilter(hexFilter);
		xpSeed1TextField.setBounds(0, 20, 102, 20);
		findSeedPanel.add(xpSeed1TextField);
		xpSeed1TextField.setToolTipText(translate("enchCrack.xpSeed1.tooltip"));

		JLabel xpl2 = new JLabel(translate("enchCrack.xpSeed2"));
		MCFont.setFontFor(xpl2);
		xpl2.setBounds(0, 40, 140, 20);
		findSeedPanel.add(xpl2);

		xpSeed2TextField.setFont(MCFont.standardFont);
		((PlainDocument)xpSeed2TextField.getDocument()).setDocumentFilter(hexFilter);
		xpSeed2TextField.setBounds(0, 60, 102, 20);
		findSeedPanel.add(xpSeed2TextField);
		xpSeed2TextField.setToolTipText(translate("enchCrack.xpSeed2.tooltip"));

		btnCalculate.setText(translate("enchCrack.calculate"));
		btnCalculate.addActionListener(event -> {
			boolean found;
			int xpSeed1, xpSeed2;
			try {
				xpSeed1 = Integer.parseUnsignedInt(xpSeed1TextField.getText(), 16);
			} catch (NumberFormatException e) {
				Log.info("Calculate player seed failed, XP seed 1 invalid");
				return;
			}
			try {
				xpSeed2 = Integer.parseUnsignedInt(xpSeed2TextField.getText(), 16);
			} catch (NumberFormatException e) {
				Log.info("Calculate player seed failed, XP seed 2 invalid");
				return;
			}
			Log.info("Calculating player seed with " + String.format("%012X", xpSeed1) + ", "
					+ String.format("%012X", xpSeed2));
			// Brute force the low bits
			long seed1High = ((long) xpSeed1 << 16) & 0x0000_ffff_ffff_0000L;
			long seed2High = ((long) xpSeed2 << 16) & 0x0000_ffff_ffff_0000L;
			found = false;
			int stepCount = 1;
			steploop: for (; stepCount < 100; stepCount ++) {
				for (int seed1Low = 0; seed1Low < 65536; seed1Low++) {

					long newSeed = (seed1High | seed1Low);
					for (int i = 0; i < stepCount; i++)
						newSeed = newSeed * 0x5deece66dL + 0xb;

					if ((newSeed & 0x0000_ffff_ffff_0000L) == seed2High) {
						playerSeed = newSeed & 0x0000_ffff_ffff_ffffL;
						playerSeeds.add(playerSeed);
						foundPlayerSeed = true;
						found = true;
						break steploop;
					}
				}
				//writeAllOfIt(0);
			}
			if (found) {
				Log.info("Played seed calculated as " + String.format("%012X", playerSeed)+" "+stepCount);
				btnCalculate.setText(String.format("%012X", playerSeed));
				//EnchantmentStats.predictNextShownEnchants((int) (playerSeed >>> 16));
				btnCalculate.setProgress(Float.POSITIVE_INFINITY);
			} else {
				Log.info("No player seed found");
				btnCalculate.setText("Fail!");
				btnCalculate.setProgress(Float.NaN);
			}
		});
		btnCalculate.setBounds(0, 84, 152, 22);
		findSeedPanel.add(btnCalculate);

		JButton btnNextSeed = new ProgressButton("button");
		JButton btnPrevSeed = new ProgressButton("button");
		currentStepLabel = new JLabel("0",SwingConstants.CENTER);
		currentStepLabel.setFont(MCFont.standardFont.deriveFont(12f));
		btnNextSeed.setToolTipText("Go to next RNG roll (use this if you fucked up. Check the predicted bookshelf lvls and book enchants)");
		btnPrevSeed.setToolTipText("Go to previous RNG roll (use this if you fucked up. Check the predicted bookshelf lvls and book enchants)");
		currentStepLabel.setToolTipText("How many RNG rolls you moved away from the one you cracked initially (4 per throw, 2 per darkness cycle, 1 per enchant)");
		btnNextSeed.setText(">");
		btnPrevSeed.setText("<");
		btnNextSeed.addActionListener(event -> {
			if(foundPlayerSeed) {
				playerSeed = nextStep(playerSeed);
				//EnchantmentStats.predictNextShownEnchants((int) (playerSeed >>> 16));

				updateSeedsInFirstPane();
				showBookLvlsInFirstPane();
			}
		});
		btnPrevSeed.addActionListener(event -> {
			if(foundPlayerSeed && playerSeedStepCount>0) {
				playerSeed = previousStep(playerSeed);
				if(playerSeedStepCount>0) {	//still >0, so was >1
					playerSeed = previousStep(playerSeed);
					playerSeed = nextStep(playerSeed);
					//EnchantmentStats.predictNextShownEnchants((int) (playerSeed >>> 16));

					updateSeedsInFirstPane();
					showBookLvlsInFirstPane();
				} else {
					//back to where we were
					playerSeed = nextStep(playerSeed);
				}
			}
		});
		btnPrevSeed.setBounds(152-40,84-20,20,20);
		btnNextSeed.setBounds(152-20,84-20,20,20);
		currentStepLabel.setBounds(152-40,84-40,45,20);
		findSeedPanel.add(btnPrevSeed);
		findSeedPanel.add(btnNextSeed);
		findSeedPanel.add(currentStepLabel);




		// --- Enchantment Calculator section

		enchList = new JPanel();
		enchList.setOpaque(false);
		enchList.setLayout(null);
		scrollPane = new JScrollPane(enchList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(6, 80, 328, 103);

		ImagePanel manipPane = new ImagePanel("pane2");
		contentPane.add(manipPane, "Manip");
		manipPane.setLayout(null);
		manipPane.add(scrollPane);

		JComboBox<String> itemPickerDropDown = new JComboBox<>(Items.getAllItems());
		itemPickerDropDown.setBounds(5, 13, 250, 22);
		manipPane.add(itemPickerDropDown);
		itemPickerDropDown.setSelectedItem(itemToEnch[0]);
		itemPickerDropDown.addActionListener(event -> {
			itemToEnch[0] = Items.getItem((String) itemPickerDropDown.getSelectedItem());

			resetEnchantmentCalcLabels();

			recreateAvailableEnchantsWindow();
		});
		itemPickerDropDown.setFont(MCFont.standardFont);
		itemPickerDropDown.setToolTipText("Item Type");

		JComboBox<String> materialPickerDropDown = new JComboBox<>(Materials.getAllMaterials());
		materialPickerDropDown.setBounds(5, 44, 250, 22);
		manipPane.add(materialPickerDropDown);
		materialPickerDropDown.setSelectedItem(itemToEnch[0]);
		materialPickerDropDown.addActionListener(event -> {
			material[0] = Materials.getMaterial((String) materialPickerDropDown.getSelectedItem());

			resetEnchantmentCalcLabels();

			recreateAvailableEnchantsWindow();
		});
		materialPickerDropDown.setFont(MCFont.standardFont);
		materialPickerDropDown.setToolTipText("Material");

		JCheckBox topEnchantCheckBox = new JCheckBox("1st",true);
		topEnchantCheckBox.setFont(MCFont.standardFont);
		topEnchantCheckBox.setBounds(265,12,70,20);
		topEnchantCheckBox.setToolTipText("One enchant means top enchant");
		topEnchantCheckBox.setBackground(new Color(0,0,0,1));
		manipPane.add(topEnchantCheckBox);
		topEnchantCheckBox.addActionListener(event -> {
			oneEnchantMeansTopEnchant = topEnchantCheckBox.isSelected();

			resetEnchantmentCalcLabels();
		});


		btnStartTimer.setText("Start Timer");
		btnStartTimer.addActionListener(event -> {
			startTime = System.currentTimeMillis();
			timerIsRunning = true;
			darknessConsumed = 0;
			btnStartTimer.setText("Timer running");
			btnStartTimer.setProgress(Float.NaN);
	//		darknessCounter.setText(String.valueOf(currentDarknessSteps()));

			Thread thread = new Thread(() -> {
				while (timerIsRunning) {
					float newSecondsPercent = ((float) (System.currentTimeMillis()-startTime) % 30000)/30000;
					if(newSecondsPercent < oldSecondsPercent){	//Only when we roll over from 30s to 0s
						System.out.println("Darkness steps at" + currentDarknessSteps()+ ", consumed "+darknessConsumed);
						if(waitingOnAnEnchant) {
							int dummiesNeeded = Integer.parseInt(outDummies.getText());
							if (dummiesNeeded > 0) {
								//READ
								String itemDrops = outDrop.getText().replace(" ", "");
								int itemThrowsNeeded = 0;
								if (itemDrops.matches("64x\\d+\\+\\d+")) {
									String[] split = itemDrops.split("\\+");
									itemThrowsNeeded = Integer.parseInt(split[0].split("x")[1]) * 64 + Integer.parseInt(split[1]);
								} else
									itemThrowsNeeded = Integer.parseInt(itemDrops);
								int darknessNeeded = Integer.parseInt(outDarkness.getText());

								//CHANGE
								darknessNeeded--;
								if (darknessNeeded < 0) {
									itemThrowsNeeded--;
									darknessNeeded = 1;
								}

								//WRITE
								if (itemThrowsNeeded >= 0) {
									outDarkness.setText("" + darknessNeeded);
									if (itemThrowsNeeded > 63)
										outDrop.setText(String.format(translate("enchCalc.stackFormat"), itemThrowsNeeded / 64, itemThrowsNeeded % 64));
									else outDrop.setText("" + itemThrowsNeeded);
								} else {
									JOptionPane.showMessageDialog(this, "Missed it. Try again!", translate("program.name"), JOptionPane.INFORMATION_MESSAGE);
									resetEnchantmentCalcLabels();
								}
							}
						}
						//play darkling sound maybe idk
					}
					oldSecondsPercent = newSecondsPercent;
					darknessCycle.setProgress(newSecondsPercent);
					darknessCycle.repaint();
					darknessCycle2.setProgress(newSecondsPercent);
					darknessCycle2.repaint();
		//			darknessCounter.setText(String.valueOf(currentDarknessSteps()));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});
			thread.setDaemon(true);
			thread.start();
		});
		btnStartTimer.setBounds(0, 244, 152, 22);
		findSeedPanel.add(btnStartTimer);

		ProgressButton findEnchantment = new ProgressButton("button");
		findEnchantment.setText(translate("enchCalc.calculate"));
		findEnchantment.setToolTipText(translate("enchCalc.calculate.tooltip"));
		btnDone = new ProgressButton("button");
		findEnchantment.addActionListener(event -> {
			findEnchantment.setProgress(-1);
			if (!foundPlayerSeed) {
				JOptionPane.showMessageDialog(this, translate("enchCalc.playerSeedNotFound"), translate("program.name"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			long seed = playerSeed;
			if (itemToEnch[0] == null) return;
			int maxDummies = 1;
			try {
				maxDummies = maxDummiesCheckBox.isSelected()?2:1;
			}
			catch (NumberFormatException e) {
				Log.info("Max shelves invalid");
				return;
			}

			Log.info("Calculating items to throw");
			Log.info("Item: " + itemToEnch[0]);
			ArrayList<Enchantments.EnchantmentInstance> wantedEnch = new ArrayList<>();
			ArrayList<Enchantments.EnchantmentInstance> unwantedEnch = new ArrayList<>();
			for(int i = 1; i < Enchantments.ALL_ENCHANTMENTS.length; i++)
				if(!myEnchantments.isGoodEnchant[i])
					unwantedEnch.add(new Enchantments.EnchantmentInstance(i,1));

			int combinedMinLvl = 0;
			int combinedMaxLvl = Math.round((30+1+2*(material[0].enchantability/4))*1.15f);

			for (Component c : enchList.getComponents()) {
				if (c instanceof MultiBtnPanel) {
					MultiBtnPanel btns = (MultiBtnPanel)c;
					if (btns.id != null) {
						int v = btns.getSelection();
						if (v != 0) {
							if (v == -1) unwantedEnch.add(new Enchantments.EnchantmentInstance(Enchantments.getId(btns.id), 1));
							else {
								Enchantments.EnchantmentInstance newWantedEnch = new Enchantments.EnchantmentInstance(Enchantments.getId(btns.id), v);
								wantedEnch.add(newWantedEnch);
								int maxEnchLvl = myEnchantments.getMaxEnchantability(newWantedEnch.enchantmentId,v);
								int minEnchLvl = myEnchantments.getMinEnchantability(newWantedEnch.enchantmentId,v);

								combinedMaxLvl = Math.min(combinedMaxLvl,maxEnchLvl);
								combinedMinLvl = Math.max(combinedMinLvl,minEnchLvl);
							}
						}
					}
				}
			}

			if(combinedMinLvl<=combinedMaxLvl)
				Log.info("Acceptable range of modified levels is "+ combinedMinLvl +"-"+ combinedMaxLvl);
			else
				Log.info("Impossible combination of enchants");

			if(wantedEnch.size()==1) {
				double combinedProbability = 0.;
				for (int i = combinedMinLvl; i <= combinedMaxLvl; i++) {
					double probForLvl = EnchantmentStats.getProbabilityForLvl(i, material[0].enchantability, 30);
					//double probForEnchCount = EnchantmentStats.getProbabilityForEnchantCount(wantedEnch.size(),i);
					double probForThisEnch = EnchantmentStats.getProbabilityForEnchantAtModLvl(wantedEnch.get(0).enchantmentId, itemToEnch[0], i);
					//combinedProbability += probForLvl*probForEnchCount;
					combinedProbability += probForLvl * probForThisEnch;
				}
				Log.info("Chance to get such an enchant at max bookshelves is 1 in " + Math.round(1 / combinedProbability));
			}

			Log.info("Wanted list:");
			for (Enchantments.EnchantmentInstance inst : wantedEnch) {
				Log.info("  " + inst);
			}
			/*Log.info("Not wanted list:");
			for (Enchantments.EnchantmentInstance inst : unwantedEnch) {
				Log.info("  " + inst);
			}*/

			if (material[0].enchantability == 0) {
				return;
			}

			// -2: not found; -1: no dummy enchantment needed; >= 0: number of times needed
			// to throw out item before dummy enchantment
			int itemThrowsNeeded = -2;
			int dummiesNeeded = 0;
			int bookshelvesNeeded = 0;
			int darknessNeeded = 0;
			int slot = 0;
			newStepCount = -1;
			int[][] enchantLevels = new int[16][3];

			ArrayList<Items> itemTypesToCheck = new ArrayList<>();
			if(itemToEnch[0].name.startsWith("Any")){
				itemTypesToCheck.addAll(Arrays.asList(Items.values()));
				itemTypesToCheck.remove(Items.ANY_ARMOR);
				itemTypesToCheck.remove(Items.ANY_TOOL);
				itemTypesToCheck.remove(Items.ANY_WEAPON);
			} else itemTypesToCheck.add(itemToEnch[0]);

			String currentItem = "", currentMaterialTier = "", currentEnchants = "", currentTopEnchant = "";

			int darknessSteps = currentDarknessSteps();

			itemThrowLoop:
			for(itemThrowsNeeded = 0; itemThrowsNeeded <= 64*maxStacksThrown; itemThrowsNeeded++) {

				int minDarkness, maxDarkness;
				if(itemThrowsNeeded == 0 || !timerIsRunning){
					minDarkness = 0;
					maxDarkness = 0;
				} else if(itemThrowsNeeded <= 32) {
					minDarkness = 1;
					maxDarkness = 2;
				}
				else {
					minDarkness = 2;
					maxDarkness = 3;
				}

				for (dummiesNeeded = itemThrowsNeeded == 0 ? 0 : 1; dummiesNeeded <= maxDummies; dummiesNeeded++)
				for (darknessNeeded = minDarkness; darknessNeeded <= maxDarkness; darknessNeeded++) {
					// Simulate all the RNG rolls
					long rollSeed = seed;
					int nStepsForward = 0;    //+1 for actual enchant isnt counted
					if (dummiesNeeded > 0) {
						nStepsForward = 4 * itemThrowsNeeded + 2 * (darknessSteps + darknessNeeded) + dummiesNeeded;    //nice binary ruler you got there
						for (int iStepsForward = 0; iStepsForward < nStepsForward; iStepsForward++) {
							rollSeed = nextStep(rollSeed);
						}
					}
					int xpSeed = (int) (rollSeed >>> 16);

					// Calculate all slot levels
					// Important they're done in a row like this because RNG is not reset in between
					for (int bookshelves = 0; bookshelves <= 15; bookshelves++) {
						Random rand = new Random();
						rand.setSeed(xpSeed);
						for (slot = 0; slot < 3; slot++) {
							int level = myEnchantments.calcEnchantmentTableLevel(rand, slot, bookshelves, 1);
							if (level < slot + 1) {
								level = 0;
							}
							enchantLevels[bookshelves][slot] = level;
						}
					}


					itemLoop:
					for (Items item : itemTypesToCheck) {
						currentItem = item.name;
						ArrayList<Integer> allowedEnchantabilities = new ArrayList<>();
						if (material[0].equals(Materials.ANY)) {
							for (int i = 0; i < 8; i++)
								if (itemIdCanHaveMatEnchTier[item.id][i])
									allowedEnchantabilities.add(i * 4 + 1);
						} else if (itemIdCanHaveMatEnchTier[item.id][material[0].enchantabilityTier])    //Item might have been added by "Any ..." item type, but cant provide the selected ench tier
							allowedEnchantabilities.add(material[0].enchantability);
						else continue itemLoop;

						for (int materialEnchantability : allowedEnchantabilities) {
							currentMaterialTier = String.valueOf(materialEnchantability / 4);
							Random rand = new Random();

							for (bookshelvesNeeded = 0; bookshelvesNeeded <= 15; bookshelvesNeeded++)
								slotLoop:for (slot = 0; slot < 3; slot++) {
									//Dont even need to check anything if we cant ever get to the desired modified lvl with the current bookshelf lvl
									int minPossibleModLvl = (int) Math.round(0.85 * (enchantLevels[bookshelvesNeeded][slot] + 1));
									int maxPossibleModLvl = (int) Math.round(1.15 * (enchantLevels[bookshelvesNeeded][slot] + 1 + 2 * materialEnchantability));
									if (maxPossibleModLvl < combinedMinLvl || combinedMaxLvl < minPossibleModLvl)
										continue slotLoop;

									// Get enchantments (changes RNG seed)
									List<Enchantments.EnchantmentInstance> enchantments = myEnchantments
											.getEnchantmentsInTable(rand, xpSeed, item.id, materialEnchantability, slot, enchantLevels[bookshelvesNeeded][slot]);
									currentEnchants = enchantments.toString();

									if (!enchantments.isEmpty()) {

										//Check if its the top enchant if we are only looking for one enchant
										if (wantedEnch.size() == 1 && oneEnchantMeansTopEnchant) {
											Enchantments.EnchantmentInstance inst = wantedEnch.get(0);
											Enchantments.EnchantmentInstance inst2 = enchantments.get(0);
											if (!inst.enchantment.equals(inst2.enchantment)) continue;
											if (inst.level > inst2.level) continue;

										} else {

											// Does this list contain all the enchantments we want?
											for (Enchantments.EnchantmentInstance inst : wantedEnch) {
												boolean found = false;
												for (Enchantments.EnchantmentInstance inst2 : enchantments) {
													if (!inst.enchantment.equals(inst2.enchantment)) continue;
													if (inst.level > inst2.level) continue slotLoop;
													found = true;
													break;
												}
												if (!found) continue slotLoop;
											}

											// Does this list contain none of the enchantments we don't want?
											for (Enchantments.EnchantmentInstance inst : unwantedEnch) {
												for (Enchantments.EnchantmentInstance inst2 : enchantments) {
													if (!inst.enchantment.equals(inst2.enchantment)) continue;
													continue slotLoop;
												}
											}
										}

										int chosenItem = rand.nextInt(enchantments.size());
										Enchantments.EnchantmentInstance inst = enchantments.get(chosenItem);
										currentTopEnchant = translate("ench." + inst.enchantment) + " " + inst.level;

										//Save the target step count, not the amount of steps it takes to get there right now (since that could change over time)
										newStepCount = playerSeedStepCount + nStepsForward;
										break itemThrowLoop;
									}
								}
						}
					}
				}
			}

			if (newStepCount == -1) {
				Log.info("Impossible combination");
				resetEnchantmentCalcLabels();
				outDrop.setText(translate("enchCalc.impossible"));
			} else {
				if (itemThrowsNeeded > 63) outDrop.setText(String.format(translate("enchCalc.stackFormat"), itemThrowsNeeded / 64, itemThrowsNeeded % 64));
				else outDrop.setText(""+itemThrowsNeeded);
				outBook.setText(""+bookshelvesNeeded);
				outSlot.setText(""+(slot + 1));
				outDarkness.setText(""+darknessNeeded);
				outDummies.setText(""+dummiesNeeded);
				if(itemToEnch[0].name.startsWith("Any") || material[0].name.startsWith("Any")){
					JOptionPane.showMessageDialog(this,
							"Found Enchant on \nItem: " +currentItem
									+"\nMaterial Tier: "+currentMaterialTier
									+"\nShows: "+currentTopEnchant,
							translate("program.name"), JOptionPane.INFORMATION_MESSAGE);
				}
				//outItemType.setText(currentItem);
				//outMaterialTier.setText(currentMaterialTier);
				Log.info("Throw " + itemThrowsNeeded + " items and enchant "+dummiesNeeded+" dummies, wait for "+darknessNeeded+" darkness cycles (30secs), b = " + bookshelvesNeeded + ", s = " + (slot + 1) + ", item = "+currentItem+", material tier = "+currentMaterialTier);
				Log.info(currentEnchants);
				btnDone.setProgress(-1);
				waitingOnAnEnchant = true;
			}
			chosenSlot = slot;
		});
		findEnchantment.setBounds(6, 190, 264, 24);
		manipPane.add(findEnchantment);

		darknessCycle.setBounds(177, 272, 152, darknessCycle.getPreferredSize().height);
		darknessCycle.setProgress(Float.NaN);
		darknessCycle.setToolTipText("Darkness Cycle. Steps two rng rolls further every 30 seconds for players in survival. Needs stable 20 TPS");
		darknessCycle2.setBounds(177, 244, 152, darknessCycle2.getPreferredSize().height);
		darknessCycle2.setProgress(Float.NaN);
		darknessCycle2.setToolTipText("Darkness Cycle. Steps two rng rolls further every 30 seconds for players in survival. Needs stable 20 TPS");
		manipPane.add(darknessCycle);
		findSeedPanel.add(darknessCycle2);
	//	darknessCounter.setBounds(156+175,274,24,24);
	//	darknessCounter.setFont(MCFont.standardFont.deriveFont(12f));
		//manipPane.add(darknessCounter);

		btnDone.setText(translate("enchCalc.done"));
		btnDone.setProgress(Float.NaN);
		btnDone.setToolTipText(translate("enchCalc.done.tooltip"));
		btnDone.addActionListener(event -> {
			Log.info("Enchanted and applied changes");
			if (newStepCount == -1 || chosenSlot == -1) {
				// nothing happened, since it was impossible anyway
				return;
			}

			//Do the required amount of steps
			int nStepsForward = newStepCount-playerSeedStepCount;
			if(nStepsForward==0)
				nStepsForward += 2*currentDarknessSteps();	//no dummy needs to also step the darkness forward

			for (int iStepsForward = 0; iStepsForward < nStepsForward; iStepsForward++) {
				playerSeed = nextStep(playerSeed);
			}

			// actual enchantment
			playerSeed = nextStep(playerSeed);

			updateSeedsInFirstPane();
			showBookLvlsInFirstPane();

			darknessConsumed += currentDarknessSteps();

			resetEnchantmentCalcLabels();

		});
		btnDone.setBounds(276, 190, 58, 24);
		manipPane.add(btnDone);

		maxDummiesCheckBox.setFont(MCFont.standardFont);
		maxDummiesCheckBox.setBounds(265, 46, 75, 20);
		maxDummiesCheckBox.setBackground(new Color(0,0,0,1));
		manipPane.add(maxDummiesCheckBox);
		maxDummiesCheckBox.setToolTipText("Allow two dummies, which gives more enchants but its harder to keep track than one dummy and costs more");
		maxDummiesCheckBox.addActionListener(event ->{
			waitingOnAnEnchant = false;
			resetEnchantmentCalcLabels();
		});


		outDrop = new JLabel("-");
		outDrop.setFont(MCFont.standardFont);
		outDrop.setToolTipText(translate("enchCalc.throwCount.tooltip"));
		outDrop.setBounds(40, 232, 110, 20);
		manipPane.add(outDrop);

		outSlot = new JLabel("-");
		outSlot.setFont(MCFont.standardFont);
		outSlot.setToolTipText(translate("enchCalc.slot.tooltip"));
		outSlot.setBounds(204, 232, 20, 20);
		manipPane.add(outSlot);

		outBook = new JLabel("-");
		outBook.setFont(MCFont.standardFont);
		outBook.setToolTipText(translate("enchCalc.bookshelves.tooltip"));
		outBook.setBounds(292, 232, 25, 20);
		manipPane.add(outBook);

		outDarkness = new JLabel("-");
		outDarkness.setFont(MCFont.standardFont);
		outDarkness.setToolTipText("Wait this many darkness cycles (each 30secs)");
		outDarkness.setBounds(40,232+40,25,20);
		manipPane.add(outDarkness);

		outDummies = new JLabel("-");
		outDummies.setFont(MCFont.standardFont);
		outDummies.setToolTipText("Enchant this many dummies (last dummy has to be in the correct darkness cycle)");
		outDummies.setBounds(122,232+40,25,20);
		manipPane.add(outDummies);

		/*outItemType = new JLabel("-");
		outItemType.setFont(MCFont.standardFont);
		outItemType.setToolTipText("Use this item type");
		outItemType.setBounds(0,0,0,0);//TODO: bruh what do i know
		//manipPane.add(outItemType);

		outMaterialTier = new JLabel("-");
		outMaterialTier.setFont(MCFont.standardFont);
		outMaterialTier.setToolTipText("Use a material with this enchantability tier");
		outMaterialTier.setBounds(0,0,0,0);//TODO: bruh what do i know
		//manipPane.add(outMaterialTier);
*/

		// About section
		JPanel aboutPane = new JPanel();
		aboutPane.setOpaque(false);
		contentPane.add(aboutPane, "About");
		aboutPane.setLayout(new BoxLayout(aboutPane, BoxLayout.Y_AXIS));

		String[] aboutLines = String.format(translate("program.about"), verText()).split("\n");
		for (String line : aboutLines) {
			if (line.startsWith("LINK")) {
				String[] parts = line.split(" ");
				if (parts.length >= 3) {
					String url = parts[1];
					String text = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
					JLabel label = new JLabel(String.format("<html><a href=\\\"%s\\\">%s</a></html>", url, text));
					label.setToolTipText(url);
					label.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							browse(url);
						}
					});
					label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					aboutPane.add(label);
					continue;
				}
			} else if (line.isEmpty()) {
				aboutPane.add(new JLabel("<html><br/></html>"));
				continue;
			}
			aboutPane.add(new JLabel("<html>" + line + "</html>"));
		}

		Insets i = getInsets();
		Insets i2 = rootPane.getBorder().getBorderInsets(this);
		setSize(i.left + i.right + findSeedPanel.getSize().width + i2.left + i2.right, i.top + i.bottom + findSeedPanel.getSize().height + i2.top + i2.bottom);
		setLocationRelativeTo(null);
	}

	private void resetEnchantmentCalcLabels() {
		waitingOnAnEnchant = false;
		outDrop.setText("-");
		outDarkness.setText("-");
		/*outMaterialTier.setText("");
		outItemType.setText("");*/
		outBook.setText("-");
		outSlot.setText("-");
		outDummies.setText("-");
		btnDone.setProgress(Float.NaN);
	}

	private JTextField getBookshelvesTextField() {
		return bookshelvesTextField;
	}

	private void updateSeedsInFirstPane() {
		currentStepLabel.setText(String.valueOf(playerSeedStepCount));
		btnCalculate.setText(String.format("%012X", playerSeed));
		int xpSeed1 = (int) (oldSeed >>> 16);
		int xpSeed2 = (int) (playerSeed >>> 16);
		xpSeed1TextField.setText(String.format("%08X", xpSeed1));
		xpSeed2TextField.setText(String.format("%08X", xpSeed2));
	}

	private void showBookLvlsInFirstPane() {
		if(bookshelvesTextField.getText().isEmpty()){
			slot1TextField.setText("");
			slot2TextField.setText("");
			slot3TextField.setText("");
			return;
		}

		int bookshelvesNeeded = Integer.parseInt(bookshelvesTextField.getText());
		if(bookshelvesNeeded>15 || bookshelvesNeeded<0) {
			slot1TextField.setText("");
			slot2TextField.setText("");
			slot3TextField.setText("");
			return;
		}
		int[] enchantLevels = new int[3];

		Random rand = new Random();
		int xpSeed = (int) (playerSeed >>> 16);
		rand.setSeed(xpSeed);

		for (int slot = 0; slot < 3; slot++) {
			int level = myEnchantments.calcEnchantmentTableLevel(rand, slot, bookshelvesNeeded, 1);
			if (level < slot + 1) {
				level = 0;
			}
			enchantLevels[slot] = level;
		}


		slot1TextField.setText(String.valueOf(enchantLevels[0]));
		slot2TextField.setText(String.valueOf(enchantLevels[1]));
		slot3TextField.setText(String.valueOf(enchantLevels[2]));

		List<Enchantments.EnchantmentInstance> enchantments1 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.BOOK.id,1, 0, enchantLevels[0]);
		int chosenItem1 = rand.nextInt(enchantments1.size());
		Enchantments.EnchantmentInstance inst = enchantments1.get(chosenItem1);
		slot1EnchantField.setText(translate("ench." + inst.enchantment) +" "+ inst.level);

		List<Enchantments.EnchantmentInstance> enchantments2 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.BOOK.id,1, 1, enchantLevels[1]);
		int chosenItem2 = rand.nextInt(enchantments2.size());
		inst = enchantments2.get(chosenItem2);
		slot2EnchantField.setText(translate("ench." + inst.enchantment) +" "+ inst.level);

		List<Enchantments.EnchantmentInstance> enchantments3 = myEnchantments
				.getEnchantmentsInTable(rand, xpSeed, Items.BOOK.id,1, 2, enchantLevels[2]);
		int chosenItem3 = rand.nextInt(enchantments3.size());
		inst = enchantments3.get(chosenItem3);
		slot3EnchantField.setText(translate("ench." + inst.enchantment) +" "+ inst.level);

	}

	private int currentDarknessSteps() {
		int darknessSteps = timerIsRunning ? (int) ((System.currentTimeMillis() - startTime) / 1000 / 30) : 0;
		return darknessSteps - darknessConsumed;
	}

	private long nextStep(long seed) {
		int index = playerSeeds.indexOf(seed);

		//End of list
		if(index == playerSeeds.size()-1){
			oldSeed = playerSeed;
			long newSeed = (seed * 0x5deece66dL + 0xb) & 0x0000_ffff_ffff_ffffL;
			playerSeeds.add(newSeed);
			playerSeedStepCount = index+1;
			return newSeed;
		}
		//Previously known
		else if(index>-1){
			oldSeed = playerSeed;
			playerSeedStepCount = index+1;
			return playerSeeds.get(index+1);
		}
		//Shouldnt happen
		else {
			System.out.println("This shouldnt happen");
			return -1;
		}
	}

	private long previousStep(long seed) {
		int index = playerSeeds.indexOf(seed);

		//Start of list
		if(index == 0){
			System.out.println("Can't go any farther to the past");
			return seed;
		}
		//Previously known
		else if(index>-1){
			playerSeedStepCount = index-1;
			return playerSeeds.get(index-1);
		}
		//Shouldnt happen
		else {
			System.out.println("This shouldn't happen");
			return -1;
		}
	}

	private void resetCracker() {
		singleSeedCracker.resetCracker();

		foundPlayerSeed = false;
		currentStepLabel.setText("0");
		playerSeeds.clear();
		playerSeed = 0;
		playerSeedStepCount = 0;

		bookshelvesTextField.setText("");
		slot1TextField.setText("");
		slot2TextField.setText("");
		slot3TextField.setText("");
		slot1EnchantField.setText("");
		slot2EnchantField.setText("");
		slot3EnchantField.setText("");

		//progressBar.setText(translate("enchCrack.check"));
		progressBar.setProgress(-1f);
		btnCalculate.setText(translate("enchCrack.calculate"));
		btnCalculate.setProgress(-1f);
	}

	private void recreateAvailableEnchantsWindow() {
		int level = 30;
		int enchantability = material[0].enchantability;
		level = level + 1 + (enchantability / 4) + (enchantability / 4);
		level = Math.max(1,Math.round(level * 1.15f));

		ArrayList<Enchantments.EnchantmentInstance> fullList = new ArrayList<>();
		while (level > 0) {
			List<Enchantments.EnchantmentInstance> list = myEnchantments.getHighestAllowedEnchantments(level, itemToEnch[0].id, false);
			for (Enchantments.EnchantmentInstance inst : list) {
				boolean contains = false;
				for (Enchantments.EnchantmentInstance inst2 : fullList) {
					if (inst.enchantment.equals(inst2.enchantment)) {
						contains = true;
						break;
					}
				}
				if (!contains) fullList.add(inst);
			}
			level--;
		}
		//fullList.sort(Comparator.comparing(ench -> ench.enchantment));

		enchList.removeAll();
		int counter = 0;
		for (int a = 0; a < fullList.size(); a++) {
			if(myEnchantments.isGoodEnchant[fullList.get(a).enchantmentId]) {
				Enchantments.EnchantmentInstance inst = fullList.get(a);
				JLabel enchLabel = new JLabel(translate("ench." + inst.enchantment));
				MCFont.setFontFor(enchLabel);
				enchLabel.setBounds(2, counter * 26, 154, 24);
				enchList.add(enchLabel);

				int max = myEnchantments.getMaxLevelInTable(inst.enchantmentId, itemToEnch[0].id, material[0].enchantability);
				int maxLvl = myEnchantments.getMaxLevel(inst.enchantmentId);
				MultiBtnPanel enchButton = (maxLvl == 1) ? new MultiBtnPanel("levelbtnshort", 3, 1) : new MultiBtnPanel("levelbtn", 7, max);
				enchButton.setBounds(156, counter * 26, enchButton.getSize().width, enchButton.getSize().height);
				enchList.add(enchButton);
				enchButton.id = inst.enchantment;

				counter++;
			}
		}
		enchList.setPreferredSize(new Dimension(156 + 154, counter*26-4));
		enchList.invalidate();
		scrollPane.validate();
		repaint();
	}

	private static void browse(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			Log.warn("Error browsing to " + url, e);
		}
	}

	/*private void writeAllOfIt(int maxSteps) {
		BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("testNischi.txt"));
        } catch (IOException e) {
			System.out.println("1");
            throw new RuntimeException(e);
        }
		int lastStep = 0;
		Enchantments.EnchantmentInstance lastEnchant = new Enchantments.EnchantmentInstance(0,1);
        long seed = playerSeed;

		stepLoop:
		for (int step = 0; step < maxSteps; step++) {
			int xpSeed = (int) (seed >>> 16);
			int[] enchantLevels = new int[3];
			bookloop:for (int bookshelves = 10; bookshelves <= 15; bookshelves++)
			slotLoop:for (int slot = 2; slot < 3; slot++)
			itemLoop:for (String itemName : Items.getAllItems())
			materialLoop:for (int materialTier = 0; materialTier < 8; materialTier++) {
				Items item = Items.getItem(itemName);
				if (itemIdCanHaveMatEnchTier[item.id][materialTier]) {
					int materialEnch = materialTier * 4 + 1;

					Random rand = new Random();
					rand.setSeed(xpSeed);
					for (int slotagain = 0; slotagain < 3; slotagain++) {
						int level = myEnchantments.calcEnchantmentTableLevel(rand, slotagain, bookshelves, materialEnch);
						if (level < slotagain + 1) {
							level = 0;
						}
						enchantLevels[slotagain] = level;
					}

					// Get enchantments (changes RNG seed)
					List<Enchantments.EnchantmentInstance> enchantments = myEnchantments
							.getEnchantmentsInTable(rand, xpSeed, item.id, materialEnch, slot, enchantLevels[slot]);

					if(!enchantments.isEmpty()) {
						Enchantments.EnchantmentInstance firstEnchant = enchantments.get(0);
						int interestingLvl = myEnchantments.interestingEnchLvl[firstEnchant.enchantmentId];
						if(interestingLvl>0 && firstEnchant.level>=interestingLvl) {
							if(step!=lastStep && !lastEnchant.equals(firstEnchant)) {
								String s = step + "\t"
										+ bookshelves + "\t"
										+ slot + "\t"
										+ itemName + "\t"
										+ materialTier + "\t"
										+ myEnchantments.getMinEnchantability(firstEnchant.enchantmentId,firstEnchant.level) + "\t"
										+ firstEnchant + "\r\n";
								try {
									writer.write(s);
								} catch (IOException e) {
									System.out.println("2");
									throw new RuntimeException(e);
								}
								lastStep = step;
								lastEnchant = firstEnchant;
							}
						}
					}

                }
			}
			seed = nextStep(seed);
		}
        try {
            writer.close();
        } catch (IOException e) {
			System.out.println("3");
            throw new RuntimeException(e);
        }
    }*/
}
