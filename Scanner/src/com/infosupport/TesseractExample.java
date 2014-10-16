import java.io.File;
import java.io.IOException;

import net.sourceforge.tess4j.*;

/**
 * @param args
 * @throws IOException
 */
public class TesseractExample {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File imageFile = new File("C:\\Users\\mikemo\\Pictures\\plates\\bw1.jpg");
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		// Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

		try {
			String result = instance.doOCR(imageFile);
			System.out.println(result);
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
	}
}