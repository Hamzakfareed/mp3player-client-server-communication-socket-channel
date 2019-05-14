package filetransfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.Commands;
import common.Constants;

public class FileSender {

	public static void main(String[] args) throws IOException {
		FileSender client = new FileSender();
		SocketChannel socketChannel = client.CreateChannel();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (socketChannel.isConnected()) {
					try {
						client.sendFile(socketChannel);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		thread.start();

	}

	private void sendFile(SocketChannel socketChannel) throws IOException {

		// Read a file from disk. Its filesize is 54.3 MB (57,006,053 bytes)
		// receive the same size 54.3 MB (57,006,053 bytes)
		Path path = Paths.get("subhanullah.mp3");
		FileChannel inChannel = FileChannel.open(path);
		////////////////////////////////////////

		ByteBuffer bb = ByteBuffer.allocate(1024);

		socketChannel.read(bb);
		bb.flip();// sets the Position to 0 and limit to the number of bytes to be read.
		CharBuffer c = Charset.forName("ISO-8859-1").decode(bb);

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		String word = c.toString();

		ByteBuffer b1 = ByteBuffer.allocate(1024);
		CharBuffer c1 = CharBuffer.wrap(word);
		System.out.println("Message from Client " + c);
		if (word.equalsIgnoreCase("Allah")) {

			while (inChannel.read(buffer) > 0) {
				buffer.flip();
				socketChannel.write(buffer);
				buffer.clear();
			}

			socketChannel.close();
		} else {
			Commands value = null;

			if (!word.equalsIgnoreCase("Allah")) {
				value = Commands.valueOf(word.toUpperCase());
			}
			if (value == Commands.ACKN) {

				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.NOT_ACKN) {

				System.out.println("Messageto client" + c);
				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.NEXT) {
				System.out.println("Messageto client" + c);
				word = "This is a message for test the app";
				c1 = CharBuffer.wrap(word);

				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c1);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.NOT_AVAIL) {

				System.out.println("Sending date ...: to client" + c);
				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.NOT_AVAIL) {

				System.out.println("Sending date ...: to client" + c);
				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.CANCEL) {

				System.out.println("Sending date ...: to client" + c);
				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.SEND) {

				System.out.println("Sending date ...: to client" + c);
				ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
				b.compact();
				b.flip();
				b.limit();
				socketChannel.write(b);
				b.clear();
			} else if (value == Commands.DONE) {
				while (inChannel.read(b1) > 0) {

					System.out.println("Sending date ...: to client" + c);
					ByteBuffer b = Charset.forName("ISO-8859-1").encode(c);
					b.compact();
					b.flip();
					b.limit();
					socketChannel.write(b);
					b.clear();
				}
			}
		}
	}

	private SocketChannel CreateChannel() throws IOException {
		// Remember that is code only works on blocking mode
		SocketChannel socketChannel = SocketChannel.open();

		// we don't need call this function as default value of blocking mode = true
		socketChannel.configureBlocking(true);

		SocketAddress sockAddr = new InetSocketAddress(Constants.SERVER, Constants.PORT1);
		socketChannel.connect(sockAddr);
		return socketChannel;
	}

}
