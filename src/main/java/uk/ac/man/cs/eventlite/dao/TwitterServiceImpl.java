package uk.ac.man.cs.eventlite.dao;

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
	public TwitterServiceImpl()
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey("1joTftLh14E9IAVp5D2BMjNdE")
				.setOAuthConsumerSecret("guMY5aDOZZJQwfdczvhlEG3m3ZzF0SgtYIuIrpQThDgEg1H7mg")
				.setOAuthAccessToken("1510558622031523844-MEtB52l0AHJHQ0FnjsKdkJ4OzNRjqn")
				.setOAuthAccessTokenSecret("04kSgikkwIUgdm89NUgXluVFdnMhwvp2EXgbxiYENIvN1");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public String getTweets() throws TwitterException
	{
		Query query = new Query("source:twitter4j baeldung");
		QueryResult result = twitter.search(query);

		String output = result.getTweets().stream()
				.map(item -> item.getText())
				.collect(Collectors.toList()).toString();
		System.out.println(output);
		return output;
	}

	public void postATweet() throws TwitterException
	{
		Status status = twitter.updateStatus("creating baeldung API");
		System.out.println("TwitterServiceImpl.postATweet()" + status.getText());
	}
}
