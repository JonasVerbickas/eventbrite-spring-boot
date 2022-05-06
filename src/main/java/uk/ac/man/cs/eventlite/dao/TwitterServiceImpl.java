package uk.ac.man.cs.eventlite.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TwitterServiceImpl {
	Twitter twitter;
	TwitterFactory tf;
	public TwitterServiceImpl()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("MqMpjC9c0ytH5FAK0FIbcffKR")
				.setOAuthConsumerSecret("1SvT0wETlgwM8tly2DomnemHpLgAKlOp2eZnHvSyuordKVMsmD")
				.setOAuthAccessToken("1510558622031523844-hZujotZLVxhAcPenNYCVjb9gNjFsj3")
				.setOAuthAccessTokenSecret("BfWp4a90YwgDu3HydccJ4en6SSfwpJiI2nggIwu3RzIMP");
		
		tf = new TwitterFactory(cb.build());
	}

	public List<Status> getTimeline()
	{
		Twitter twitter = tf.getInstance();
		List<Status> statuses;
		try {
			statuses = twitter.getUserTimeline(new Paging(1, 5));
		} catch (TwitterException e) {
			statuses = new ArrayList<Status>();	
		}
		return statuses;
	}

	public boolean postATweet(String tweet_text)
	{
		Twitter twitter = tf.getInstance();
		try {
			Status status = twitter.updateStatus(tweet_text);
			System.out.println("Successfully tweeted: " + status.getText());
			return true;
		} catch (TwitterException e) {
			// THIS ONLY WORKS BECAUSE TWEET WITH TEXT "ERROR" WAS POSTED BEFORE
			System.out.println("Tweeting failed");
			return false;
		}
	}
}
