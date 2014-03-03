package laser.littlejil.smartchecklist.gui.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffUtil {

	//params: filename, before contents, after contents
	//returns unified diff format
	public static String diff(String filename, String file1, String file2){
		//user scanners to break up the source code into list of lines
		Scanner scan1 = new Scanner(file1);
		Scanner scan2 = new Scanner(file2);

		List<String> file1list = new LinkedList<String>();
		while(scan1.hasNextLine()){
			file1list.add(scan1.nextLine());
		}

		List<String> file2list = new LinkedList<String>();
		while(scan2.hasNextLine()){
			file2list.add(scan2.nextLine());
		}

		Patch patch = DiffUtils.diff(file1list, file2list);

		//the last param is the context size. increase or decrease to show more lines above and below changed lines
		List<String> unified = DiffUtils.generateUnifiedDiff(filename, filename, file1list, patch, 3);

		String ret = "";
		for(String str : unified){
			ret += str + "\r\n";
		}
		return ret;
	}
}
