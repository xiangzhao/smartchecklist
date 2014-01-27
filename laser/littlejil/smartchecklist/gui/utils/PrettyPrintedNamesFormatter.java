package laser.littlejil.smartchecklist.gui.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * This class is responsible for producing a human-readable version of class names used for Little-JIL exceptions
 * and parameters and for keeping the correspondence between that human-readable
 * version and the original fully-qualified class name.
 *
 */
public class PrettyPrintedNamesFormatter
{
	private List<String> fullyQualifiedNames_;
	private List<String> humanReadableNames_;
	
	public PrettyPrintedNamesFormatter(Set<String> fullyQualifiedExceptionNames)
	{
		fullyQualifiedNames_ = new ArrayList<String>();
		fullyQualifiedNames_.addAll(fullyQualifiedExceptionNames);
		humanReadableNames_ = new ArrayList<String>();
		
		// Create a human-readable name from each fully qualified name and add the human-readable
		// names to a list in the order of the corresponding fully qualified names
		for (int i = 0; i < fullyQualifiedNames_.size(); i++)
			humanReadableNames_.add(createHumanReadableName(fullyQualifiedNames_.get(i)));
		
		// Check if there are duplicate human-readable names. If so, use the fully qualified name
		// for the duplicates
		
		// The positions in the list of human readable names where there are duplicates
		Set<Integer> duplicatePositions = new HashSet<Integer>();
					
		for (int i = 0; i < humanReadableNames_.size(); i++)
		{		
			for (int j = i; j < humanReadableNames_.size(); j++)
			{
				if (j == i)
					continue;
				if (humanReadableNames_.get(i).equals(humanReadableNames_.get(j)))
				{
					duplicatePositions.add(i);
					duplicatePositions.add(j);
				}
			}//end inner for loop		
		}//end outer for loop
		
		// For the positions that have duplicates, replace the human readable name in
		// humanReadableNames_ with the original fully qualified name
		for (Integer duplicatePosition : duplicatePositions)
			humanReadableNames_.set(duplicatePosition, fullyQualifiedNames_.get(duplicatePosition));
	}
	
	
	public Set<String> getHumanReadableExceptionNames()
	{
		return new HashSet<String>(humanReadableNames_);
	}
	
	
	public String getFullyQualifiedName(String humanReadableName)
	{
		int index = humanReadableNames_.indexOf(humanReadableName);
		return fullyQualifiedNames_.get(index);
	}
	
	
	/**
	 * Takes a string in Camel Back case with no white space (like a Java class name) and
	 * outputs a "prettier version" of that String. A space is inserted before each capital
	 * case letter, unless a capital letter is preceded by another capital letter.
	 * 
	 * @param fullyQualifiedName	A string in Camel Back case with no white space (like a Java class name)
	 * @return	A prettier version of the given String.
	 */
	public static String createHumanReadableName(String fullyQualifiedName)
	{
		String[] tokens = fullyQualifiedName.split("\\.");
		
		// The last part of the fully qualified name is the one we will use for the human-readable
		// version of the exception name.
		String lastToken = tokens[tokens.length-1];
		// Assuming the exception class name is in Camel case, insert space before each
		// word starting with an upper case letter.
		StringBuffer spaceSeparatedString = new StringBuffer();
		for (int i = 0; i < lastToken.length(); i++)
		{
			if (i == 0)
			{
				spaceSeparatedString.append(lastToken.charAt(i));
				continue;
			}
			
			// If the character at position i is upper case and the one at position i-1 is lower
			// case, insert a blank space before the character at position i
			if (Character.isUpperCase(lastToken.charAt(i)) &&
					//i > 0 &&
					Character.isLowerCase(lastToken.charAt(i-1)))
			{
				spaceSeparatedString.append(" " + lastToken.charAt(i));
				continue;
			}
			
			// If the character at position i is upper case, the one after it is lower case,
			// and the characters at position i-1 and i-2 are upper case, insert a blank space
			// before the character at position i. This allows splitting properly strings like
			// "PracticeRNIsBusy"
			if (Character.isUpperCase(lastToken.charAt(i)) &&
					i < (lastToken.length() - 1) &&
					i > 1 &&
					Character.isLowerCase(lastToken.charAt(i+1)) &&
					Character.isUpperCase(lastToken.charAt(i-1)) &&
					Character.isUpperCase(lastToken.charAt(i-2)))
			{
				spaceSeparatedString.append(" " + lastToken.charAt(i));
				continue;
			}
			else
				spaceSeparatedString.append(lastToken.charAt(i));
		}
		
		// Go through the space separated String and convert to lower case all tokens, except
		// ones whose letters are all upper case.
		String[] tokens2 = spaceSeparatedString.toString().split("\\s+");
		StringBuffer toReturn = new StringBuffer();
		for (int i = 0; i < tokens2.length; i++)
		{
			if ( !tokens2[i].toUpperCase().equals(tokens2[i]))
				toReturn.append(tokens2[i].toLowerCase());
			else
				toReturn.append(tokens2[i]);
			
			if (i != tokens2.length-1)
				toReturn.append(" ");
		}
			
		return toReturn.toString();
	}
}
