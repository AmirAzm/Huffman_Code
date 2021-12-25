import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.PriorityQueue;
import java.util.TreeMap;

/* Huffman coding , decoding */

public class Huffman {

	static PriorityQueue<Node> nodes = new PriorityQueue<>((o1, o2) -> (o1.value < o2.value) ? -1 : 1);
	static TreeMap<Character, String> codes = new TreeMap<>();
	static String text = "";
	static StringBuilder encoded;
	static StringBuilder decoded;
	static int ASCII[] = new int[100000];
	
	public static void main(String[] args) throws IOException {
		StringBuilder out = new StringBuilder();
		FileInputStream byteStream = new FileInputStream("input.txt");
		InputStreamReader characterStream =new InputStreamReader(byteStream,"ISO-8859-1");
		int c;
        while ((c = characterStream.read()) != -1) {
            out.append((char)c);
        }
        characterStream.close();
		handleNewText(out.toString());
	}

	private static boolean handleNewText(String scanner) throws IOException {
		text = scanner;
		calculateCharIntervals(nodes, true);
		buildTree(nodes);
		generateCodes(nodes.peek(), "");
		printCodes();
		System.out.println("-- Encoding/Decoding --");
		encodeText();
		decodeText();
		return false;
	}

	private static void decodeText() throws IOException {
		decoded = new StringBuilder();
		StringBuilder out = new StringBuilder();
/*		FileInputStream byteStream = new FileInputStream("output.bin");
		InputStreamReader characterStream =new InputStreamReader(byteStream);
		int c;
        while ((c = characterStream.read()) != -1) {
            out.append((char)c);
        }*/
		File file = new File("output.bin");
		FileInputStream fin = new FileInputStream(file);
		byte filecontent [] = new byte[(int)file.length()];
		fin.read(filecontent);
		BitSet set = BitSet.valueOf(filecontent);
		for(int i =0 ;i<=set.length();i++) {
			if(set.get(i)) {
				out.append("1");
			}else {
				out.append("0");
			}
		}
		Node node = nodes.peek();
		for (int i = 0; i < out.length();) {
			Node tmpNode = node;
			while (tmpNode.left != null && tmpNode.right != null && i < out.length()) {
				if (out.charAt(i) == '1')
					tmpNode = tmpNode.right;
				else
				tmpNode = tmpNode.left;
				i++;
			}
			if (tmpNode != null)
				if (tmpNode.character.length() == 1)
					decoded.append(tmpNode.character);
				else
					System.out.println("Input not Valid");

		}
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("output1.txt", "ISO-8859-1");
		} catch (FileNotFoundException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print(decoded.toString());
		writer.close();
		System.out.println("Text Decoded");
	}

	private static void encodeText() throws IOException {
		encoded = new StringBuilder();
/*		FileWriter fstream = new FileWriter("output.bin");
		BufferedWriter out = new BufferedWriter(fstream);*/
		FileOutputStream out =new FileOutputStream("output.bin");
		for (int i = 0; i < text.length(); i++) {
			encoded.append(codes.get(text.charAt(i)));
		}	
		BitSet bitset = new BitSet(encoded.length());
		int bitcounter = 0;
		for(Character c : encoded.toString().toCharArray()) {
			if(c.equals('1')) {
				bitset.set(bitcounter);
			}
			bitcounter++;
		}
		out.write(bitset.toByteArray());
		out.close();
		
/*		out.write(encoded.toString());
		out.flush();
		out.close();*/
		System.out.println("Encoded");
	}

	private static void buildTree(PriorityQueue<Node> vector) {
		while (vector.size() > 1)
			vector.add(new Node(vector.poll(), vector.poll()));
	}

	private static void printCodes() {
		System.out.println("--- Printing Codes ---");
		codes.forEach((k, v) -> System.out.println("'" + k + "' : " + v));
	}

	private static void calculateCharIntervals(PriorityQueue<Node> vector, boolean printIntervals) {
		if (printIntervals)
			System.out.println("-- intervals --");

		for (int i = 0; i < text.length(); i++)
			ASCII[text.charAt(i)]++;

		for (int i = 0; i < ASCII.length; i++)
			if (ASCII[i] > 0) {
				vector.add(new Node(ASCII[i], ((char) i) + ""));
				if (printIntervals)
					System.out.println("'" + ((char) i) + "' : " + ASCII[i]);
			}
	}

	private static void generateCodes(Node node, String s) {
		if (node != null) {
			if (node.right != null)
				generateCodes(node.right, s + "1");

			if (node.left != null)
				generateCodes(node.left, s + "0");

			if (node.left == null && node.right == null)
				codes.put(node.character.charAt(0), s);
		}
	}
}

class Node {
	Node left, right;
	double value;
	String character;

	public Node(double value, String character) {
		this.value = value;
		this.character = character;
		left = null;
		right = null;
	}

	public Node(Node left, Node right) {
		this.value = left.value + right.value;
		character = left.character + right.character;
		if (left.value < right.value) {
			this.right = right;
			this.left = left;
		} else {
			this.right = left;
			this.left = right;
		}
	}
}