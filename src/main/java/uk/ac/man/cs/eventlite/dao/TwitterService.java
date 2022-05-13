package uk.ac.man.cs.eventlite.dao;

import twitter4j.Status;

import java.util.List;

public interface TwitterService {
    public List<Status> getTimeline();

    public boolean postATweet(String tweet_text);
}
