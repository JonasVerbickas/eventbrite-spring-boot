package uk.ac.man.cs.eventlite.dao;

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

	public List<Status> getTimeline() throws TwitterException
	{
		Twitter twitter = tf.getInstance();
		List<Status> statuses = twitter.getUserTimeline(new Paging(1, 5));
		System.out.println("Showing user timeline.");
		for (Status status : statuses) {
			System.out.println(status.getUser().getName() + ":" +
					status.getText());
		}
		return statuses;
	}

	public void postATweet(String tweet_text) throws TwitterException
	{
		Twitter twitter = tf.getInstance();
		Status status = twitter.updateStatus(tweet_text);
		System.out.println("TwitterServiceImpl.postATweet()" + status.getText());
	}
}
