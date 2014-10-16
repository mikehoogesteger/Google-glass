package com.infosupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Mike Makes a kenteken valid, it is possible that the OCR reads the
 *         number 1 as a character I, this needs to be resolved. This is done by
 *         checking if it is together with another digit.
 */
public class KentekenValidator {

	public KentekenValidator() {

	}

	/**
	 * Make a valid kenteken out of a string that could be filled with special
	 * characters.
	 * 
	 * @param kenteken
	 *            is the given string, this can be filled with special
	 *            characters
	 * @return the new string based on the given kenteken.
	 */
	public String makeAValidKentekenOutOfThis(String kenteken) {
		kenteken = kenteken.toUpperCase(Locale.US);
		List<String> kentekenParts = splitKentekenAndReturnList(kenteken);
		List<String> validParts = new ArrayList<String>();
		for (String part : kentekenParts) {
			validParts.add(makeAValidPart(part));
		}

		kenteken = pasteListTogether(validParts);
		return kenteken;
	}

	/**
	 * Splits a kenteken and makes a list out of it.
	 * 
	 * @param kenteken
	 *            the kenteken that needs to be splitted on special characters
	 * @return a list of kentekenparts.
	 */
	public List<String> splitKentekenAndReturnList(String kenteken) {
		List<String> kentekenParts;
		kentekenParts = Arrays.asList(kenteken.split("[^a-zA-Z0-9]+"));
		return kentekenParts;
	}

	/**
	 * Pastes all parts into one string.
	 * 
	 * @param kentekenParts
	 *            the list of kentekenparts
	 * @return one string with all kentekenparts pasted together
	 */
	public String pasteListTogether(List<String> kentekenParts) {
		String kenteken = "";

		for (String part : kentekenParts) {
			kenteken += part;
		}

		return kenteken;
	}

	/**
	 * Checks if a part has the character I.
	 * 
	 * @param part
	 *            is a part of the kenteken
	 * @return true if there is an I, false if there isn't
	 */
	public boolean hasAnI(String part) {
		if (part.contains("I")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the part has a digit.
	 * 
	 * @param part
	 *            is a part of the kenteken
	 * @return true if it has a digit, false if it hasn't
	 */
	public boolean hasADigit(String part) {
		char[] characters = part.toCharArray();
		for (char character : characters) {
			try {
				Integer.parseInt(String.valueOf(character));
				return true;
			} catch (NumberFormatException nfe) {
				// Not a number
			}
		}
		return false;
	}

	/**
	 * Makes a valid part out of a possibly invalid part.
	 * 
	 * @param part
	 *            is a part of the kenteken
	 * @return a valid part
	 */
	public String makeAValidPart(String part) {
		if (hasAnI(part)) {
			if ((part.length() == 2 && hasADigit(part)) || part.length() == 1) {
				return makeItAllDigit(part);
			}
		} else if (part.length() == 3) {
			return makeItAllText(part);
		}
		return part;
	}

	/**
	 * Turns the I in a 1.
	 * 
	 * @param part
	 *            is a part of the kenteken
	 * @return a valid part
	 */
	public String makeItAllDigit(String part) {
		part = part.replaceAll("I", "1");
		part = part.replaceAll("O", "0");
		return part;
	}
	
	public String makeItAllText(String part) {
		part = part.replaceAll("0", "O");
		return part;
	}
}
