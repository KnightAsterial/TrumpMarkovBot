package com.knightasterial.markov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.google.gson.Gson;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MarkovFromTwitterJson {

	public static void main(String[] args) throws FileNotFoundException{		
		HashMap<String, ArrayList<String>> markov = new HashMap<String, ArrayList<String>>();
		
		markov.put("_start", new ArrayList<String>());
		markov.put("_end", new ArrayList<String>());

		
		Scanner fileReader = new Scanner(new File("trumptweets_condensed_2017.json"));
		
		String jsoncontent = "";
		while (fileReader.hasNext()){
			jsoncontent += fileReader.nextLine();
			jsoncontent += "\n";
		}
		
		String input = "";
		Gson gson = new Gson();
		
		TrumpTweet[] tweets = gson.fromJson(jsoncontent, TrumpTweet[].class);
		

		for (TrumpTweet tweet : tweets){
			input += " _start ";
			input += tweet.getText();
			input += " _end ";
		}
		
		

		
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
		
		String mostCommon = "";
		int maxOccurance = -1;
		
		
		
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		ArrayList<Integer> occurances = new ArrayList<Integer>();
		HashMap<String, Integer> top10 = new HashMap<String, Integer>();
		
		while (!temp.equals("_end")){
			counter.clear();
			maxOccurance = -1;
			occurances.clear();
			top10.clear();
			
			if (markov.get(temp) == null){
				temp = "_end";
			}
			else{
				
				//counts all the occurances of each letter
				for (String word : markov.get(temp)){
					if (counter.get(word) == null){
						counter.put(word, 0);
					}
					counter.put(word, counter.get(word)+1);
				}
				
				//adds the occurances to an arraylist
				for (String word : counter.keySet()){
					occurances.add(counter.get(word));
				}
				
				Collections.sort(occurances);
				Collections.reverse(occurances);
				
				int tophowmany = 0;
				if (occurances.size() < 10){
					tophowmany = occurances.size();
				}
				else{
					tophowmany = 10;
				}
				
				
				
				int total = 0;
				
				for (int i = 0; i < tophowmany; i++	){
					int x = occurances.get(i);
					for (String word : counter.keySet()){
						if(counter.get(word) == x && top10.get(word) == null){
							top10.put(word, x);
							total += x;
							break;
						}
						else{
							continue;
						}
						
					}
					
					
				}
				
								
				/*
				for (String word : top10.keySet()){
					System.out.print(word + ": " + top10.get(word) + " ");
				}
				System.out.println();
				*/

				
				int randomNum = rand.nextInt(total);
				int tempForProbability = 0;
				for (String word : top10.keySet()){
					//System.out.print(randomNum);
					//System.out.println(" - " + (top10.get(word)+tempForProbability));
					if ( tempForProbability <= randomNum  && randomNum <= (top10.get(word)+tempForProbability) ){
						temp = word;
						break;
					}
					
					else{
						tempForProbability += top10.get(word);
					}
					
				}
				
			
				
				//randIndex = rand.nextInt(markov.get(temp).size());
				//temp = markov.get(temp).get(randIndex);
				if (temp.equals("_end")){

				}
				else{
					result += temp + " ";
				}
				
			}
		}
		

		
		String consumerKey = "JvGwiBG7RLZfq6pNRe9LFAj3Z";
		String consumerSecret = "fo7OGJLh8wNB0ejtilkucyIqHEzSBycCttWT1FHs3drjXSLSMM";
		String accessToken = "863466774184026112-n2bowzRcjw84gn7ZaYW27HqgaiVXrLh";
		String accessSecret = "GySQ8Tc4gYIsmRKfkTT2kFi4rwHkiQTGZ55kgfFW3KSwr";
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(consumerKey) 
		.setOAuthConsumerSecret(consumerSecret)
		.setOAuthAccessToken(accessToken)
		.setOAuthAccessTokenSecret(accessSecret);
		
		try{
			TwitterFactory factory = new TwitterFactory(cb.build());
			Twitter twitter = factory.getInstance();
			
			System.out.println(twitter.getScreenName());
			ArrayList<Status> updates = new ArrayList<Status>();
			String toPost = "";
			String resultMutated = result;
			int updateCounter = 0;
			boolean shouldContinue = true;
			

			if (resultMutated.length() <= 140){
				Status status = twitter.updateStatus(resultMutated);
				System.out.println("Successfully updated the status to [" + status.getText() + "].");
				shouldContinue = false;
				
			}
			else if (resultMutated.length() > 140){
				toPost = resultMutated.substring(0, 136);
				toPost += "... ";
				resultMutated = resultMutated.substring(136, resultMutated.length());
				updates.add(twitter.updateStatus(toPost));
				System.out.println("Successfully updated the status to [" + updates.get(updateCounter).getText() + "].");
				updateCounter++;
				
				while (resultMutated.length() > 0){
					if (resultMutated.length() > 136){
						toPost = resultMutated.substring(0, 132);
						toPost = "... " + toPost + " ...";
						resultMutated = resultMutated.substring(132, resultMutated.length());
						updates.add(twitter.updateStatus(toPost));
						System.out.println("Successfully updated the status to [" + updates.get(updateCounter).getText() + "].");
					}
					else{
						toPost = resultMutated;
						resultMutated = "";
						toPost = "... " + toPost;
						updates.add(twitter.updateStatus(toPost));
						System.out.println("Successfully updated the status to [" + updates.get(updateCounter).getText() + "].");
					}
					updateCounter++;	
				}
			}
			
			System.out.println(result);

			
			//Status status = twitter.updateStatus(result);
			//System.out.println("Successfully updated the status to [" + status.getText() + "].");
			
				
		}
		catch (TwitterException e){
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		//System.out.println(result);
		
		
		

		fileReader.close();
		
	

	}
	
	public class TrumpTweet{
		String source;
		String id_str;
		String text;
		String created_at;
		int retweet_count;
		String in_reply_to_user_id_str;
		int favorite_count;
		boolean is_retweet;
		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}
		/**
		 * @param source the source to set
		 */
		public void setSource(String source) {
			this.source = source;
		}
		/**
		 * @return the id_str
		 */
		public String getId_str() {
			return id_str;
		}
		/**
		 * @param id_str the id_str to set
		 */
		public void setId_str(String id_str) {
			this.id_str = id_str;
		}
		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}
		/**
		 * @param text the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}
		/**
		 * @return the created_at
		 */
		public String getCreated_at() {
			return created_at;
		}
		/**
		 * @param created_at the created_at to set
		 */
		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}
		/**
		 * @return the retweet_count
		 */
		public int getRetweet_count() {
			return retweet_count;
		}
		/**
		 * @param retweet_count the retweet_count to set
		 */
		public void setRetweet_count(int retweet_count) {
			this.retweet_count = retweet_count;
		}
		/**
		 * @return the in_reply_to_user_id_str
		 */
		public String getIn_reply_to_user_id_str() {
			return in_reply_to_user_id_str;
		}
		/**
		 * @param in_reply_to_user_id_str the in_reply_to_user_id_str to set
		 */
		public void setIn_reply_to_user_id_str(String in_reply_to_user_id_str) {
			this.in_reply_to_user_id_str = in_reply_to_user_id_str;
		}
		/**
		 * @return the favorite_count
		 */
		public int getFavorite_count() {
			return favorite_count;
		}
		/**
		 * @param favorite_count the favorite_count to set
		 */
		public void setFavorite_count(int favorite_count) {
			this.favorite_count = favorite_count;
		}
		/**
		 * @return the is_retweet
		 */
		public boolean isIs_retweet() {
			return is_retweet;
		}
		/**
		 * @param is_retweet the is_retweet to set
		 */
		public void setIs_retweet(boolean is_retweet) {
			this.is_retweet = is_retweet;
		}
	}

}
