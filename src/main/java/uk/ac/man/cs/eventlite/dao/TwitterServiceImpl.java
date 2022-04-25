package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import twitter4j.Query;
import twitter4j.QueryResult;
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

	public void getTimeline() throws TwitterException
	{
		Twitter twitter = tf.getInstance();
		List<Status> statuses = twitter.getHomeTimeline();
		System.out.println("Showing home timeline.");
		for (Status status : statuses) {
			System.out.println(status.getUser().getName() + ":" +
					status.getText());
		}
	}

	public void postATweet() throws TwitterException
	{
		Twitter twitter = tf.getInstance();
		Status status = twitter.updateStatus("BAH");
		System.out.println("TwitterServiceImpl.postATweet()" + status.getText());
	}
}
