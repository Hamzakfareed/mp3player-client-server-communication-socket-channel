/*
 * Transferring large sized file through SocketChannel
 * 1. Create a server class named FileReceiver
 * 2. Client a client class named FileSender
 * 3. Demo to send a large file
 * 
 * Thank you for your watching
 * lehuy2706@gmail.com
 *
*/
package filetransfer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Scanner;

import common.Constants;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import sun.nio.ByteBuffered;

public class FileReceiver {

	private SocketChannel socketChannel;
	private AdvancedPlayer player;
	private InputStream in;
	private Charset charset = Charset.forName("UTF-8");

	public FileReceiver() throws IOException {
		socketChannel = createServerSocketChannel();
	}

	public void playAndDownload(String fileName) throws IOException, JavaLayerException {

		readFileFromSocketChannel(socketChannel, fileName);

	}

	private void readFileFromSocketChannel(SocketChannel socketChannel, String fileName)
			throws IOException, JavaLayerException {
		// Try to create a new file
		Path path = Paths.get("subhanullah_2.mp3");
		FileChannel fileChannel = FileChannel.open(path,
				EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
		// Allocate a ByteBuffer
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		CharBuffer c = CharBuffer.wrap(fileName);
		ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
		b.compact();
		b.flip();
		b.limit();
		socketChannel.write(b);

		while (socketChannel.read(buffer) > 0) {
			buffer.flip();
			System.out.println("Hello1");
			fileChannel.write(buffer);
			buffer.clear();
		}
		fileChannel.close();

		in = new FileInputStream("H:/subhanullah_2.mp3");
		this.player = player(in);
		System.out.println("Checkpoint 222");

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("Play");
					player.play();
				} catch (JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		System.out.println("Checkpoint 222");
		thread.start();
		System.out.println("Receving file successfully!");

	}

	public AdvancedPlayer player(InputStream in) throws JavaLayerException {
		this.player = new AdvancedPlayer(in);
		return this.player;
	}

	public void stop() throws JavaLayerException, InterruptedException, IOException {
		if (player == null) {
			player = player(in);
		}

	}

	private SocketChannel createServerSocketChannel() throws IOException {
		ServerSocketChannel serverSocket = null;
		SocketChannel client = null;
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(new InetSocketAddress(Constants.PORT1));
		client = serverSocket.accept();

		System.out.println("HELO " + client.getLocalAddress());
		return client;
	}

	public void exit() throws IOException {
		socketChannel.close();
	}

	public void commands() throws IOException {

		if (!socketChannel.isConnected()) {
			socketChannel = createServerSocketChannel();
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("CMD : ");
		String line = scanner.nextLine();
		if (!line.equals("exit")) {
			CharBuffer c = CharBuffer.wrap(line);
			ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
			b.compact();
			b.flip();
			b.limit();
			socketChannel.write(b);
			b.clear();
			receiveServerMessage(socketChannel);
			line = scanner.nextLine();
		}

	}

	private void receiveServerMessage(SocketChannel socketChannel) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1024);

		socketChannel.read(bb);
		bb.flip();// sets the Position to 0 and limit to the number of bytes to be read.
		CharBuffer c = Charset.forName("ISO-8859-1").decode(bb);
		String word = c.toString();
		System.out.println("Message from server :" + word);
		commands();
	}

	public String decode(ByteBuffer buffer) {
		CharBuffer charBuffer = charset.decode(buffer);
		return charBuffer.toString();
	}

	public ByteBuffer encode(String str) {
		return charset.encode(str);
	}

}
