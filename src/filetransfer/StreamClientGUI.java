package filetransfer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.BlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javazoom.jl.decoder.JavaLayerException;

public class StreamClientGUI {

	private static final int DISP_DELAY = 80;

	private final JFrame frame;
	private final JTextField nameF;
	private final JButton startBtn;
	private final JButton cancelBtn;
	private final JTextArea textA;
	private final JLabel statusLbl;
	private static FileReceiver receiver;

	private volatile boolean terminate = false;
	private volatile boolean cancel = false;
	private Thread printThread;
	private BlockingQueue<CharBuffer> queue;
	private StringBuilder b;

	public static void main(String[] args) throws IOException {
			new StreamClientGUI().open();
		}
	

	private StreamClientGUI() throws IOException {
		receiver = new FileReceiver();
		frame = new JFrame("Streaming");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel btPanel = new JPanel();
		cp.add(btPanel, BorderLayout.NORTH);
		startBtn = new JButton(new ImageIcon("start.png"));
		cancelBtn = new JButton(new ImageIcon("stop.png"));
		nameF = new JTextField(20);
		nameF.setText("");
		btPanel.add(new JLabel("Artist: "));
		btPanel.add(nameF);
		btPanel.add(startBtn);
		btPanel.add(cancelBtn);
		textA = new JTextArea(2, 20);
		textA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		cp.add(textA, BorderLayout.CENTER);
		statusLbl = new JLabel(" ");
		cp.add(statusLbl, BorderLayout.SOUTH);

		startBtn.addActionListener(a -> {
			statusLbl.setText("Started");
			String name = nameF.getText();
			if (name == null || name.length() == 0) {
				statusLbl.setText("Please define artist name");
				return;
			}
			new Thread(() -> {
				try {
					start(nameF.getText().trim());

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								textA.setText(nameF.getText());
								receiver.playAndDownload(nameF.getText());
							} catch (IOException | JavaLayerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					});
					thread.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		});

		cancelBtn.addActionListener(a -> {
			statusLbl.setText("Canceled");
			try {
				receiver.stop();
			} catch (JavaLayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}open();
			cancel = true;
		});

	}

	private void open() {
		SwingUtilities.invokeLater(() -> {
			cancelBtn.setEnabled(false);
			startBtn.setEnabled(true);

			frame.pack();
			frame.setLocation(200, 200);
			frame.setVisible(true);
		});
	}

	private void printChars(CharBuffer chars) {
		while (chars.hasRemaining() && !cancel) {
			char ch = chars.get();
			if (ch != '\n' && ch != '\r') { // do not display line feeds
				int w = SwingUtilities.computeStringWidth(textA.getFontMetrics(textA.getFont()), b.toString());
				while (w >= textA.getWidth() - 10) {
					b.delete(0, 1);
					w = SwingUtilities.computeStringWidth(textA.getFontMetrics(textA.getFont()), b.toString());
				}
				b.append(ch);
				SwingUtilities.invokeLater(() -> {
					textA.setText(b.toString());
				});
			}
			try {
				Thread.sleep(DISP_DELAY);
			} catch (InterruptedException e) {
			}
		}
	}

	private void start(String name) throws Exception {
		terminate = false;
		cancel = false;
		cancelBtn.setEnabled(true);
		startBtn.setEnabled(false);

	}

}
