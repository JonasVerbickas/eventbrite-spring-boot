package uk.ac.man.cs.eventlite.entities;

public class Tweet {
	private String tweetText = "";
	
	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	@Override
	public String toString() {
		return  this.getTweetText();
	}
	
	
}