package com.knightasterial.markov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
* @author KnightAsterial
*/
public class MarkovFromText {

	public static void main(String[] args) throws FileNotFoundException{
		Scanner userInput = new Scanner(System.in);
		System.out.print("File to get markov source from: ");
		String source = userInput.nextLine();
		
		HashMap<String, ArrayList<String>> markov = new HashMap<String, ArrayList<String>>();
		
		markov.put("_start", new ArrayList<String>());
		markov.put("_end", new ArrayList<String>());
		
		Scanner fileReader = new Scanner(new File(source));
		
		String input = "";
		while (fileReader.hasNext()){
			input += fileReader.nextLine();
			input += "\n";
		}
		
		
		input = input.replaceAll("[.\n]" , " _end _start ");
		
		StringTokenizer strtokenizer = new StringTokenizer(input);
		
		ArrayList<String> inputwords = new ArrayList<String>();
		
		while (strtokenizer.hasMoreTokens()){
			inputwords.add(strtokenizer.nextToken());
		}
		
		for (int i = 0; i < inputwords.size(); i++){
			if (i == 0){
				ArrayList<String> following = markov.get("_start");
				following.add(inputwords.get(i+1));
				markov.put("_start", following);
			}
			else if (i == inputwords.size()-1){
				
			}
			else{
				if (markov.get(inputwords.get(i)) == null){
					ArrayList<String> following = new ArrayList<String>();
					following.add(inputwords.get(i+1));
					markov.put(inputwords.get(i), following);
				}
				else{
					ArrayList<String> following = markov.get(inputwords.get(i));
					following.add(inputwords.get(i+1));
					markov.put(inputwords.get(i), following);
				}
			}
		}
		
		String result = "";
		String temp = "_start";
		Random rand = new Random();
		int randIndex;
		
		while (!temp.equals("_end")){
			if (markov.get(temp) == null){
				temp = "_end";
			}
			else{
				randIndex = rand.nextInt(markov.get(temp).size());
				temp = markov.get(temp).get(randIndex);
				if (temp.equals("_end")){
					result += ".";
				}
				else{
					result += temp + " ";
				}
			}
		}
		
		System.out.println(result);
		
		
		
		
		fileReader.close();
		userInput.close();
	}
	

}