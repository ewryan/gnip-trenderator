package com.gnip;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import org.codehaus.jackson.JsonNode;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrendingStreamHandler implements StreamHandler {
    private Bucket bucket = new Bucket();

    public TrendingStreamHandler() {
        new Monitor().start();
    }

    public void handleLine(String line) {
        JsonNode rootNode = JSONUtils.parseLine(line);
        JsonNode hashtagsNode = rootNode.path("twitter_entities").path("hashtags");

        if (!hashtagsNode.isMissingNode()) {
            List<JsonNode> text = hashtagsNode.findValues("text");
            String language = rootNode.path("gnip").path("lang").path("value").getTextValue();
            for (JsonNode jsonNode : text) {
                bucket.increment(jsonNode.getTextValue(), language);
            }
        }
    }

    private class Bucket {
        private final SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
        private final ConcurrentHashMap<String, Map<String, Integer>> langMap = new ConcurrentHashMap<String, Map<String, Integer>>();

        public void increment(String hashtag, String language) {
            Map<String, Integer> tagCountMap = getMapForLanguage(language) ;
            if (tagCountMap.get(hashtag) != null) {
                int currentValue = tagCountMap.get(hashtag);
                tagCountMap.put(hashtag,  ++currentValue);
            } else {
                tagCountMap.put(hashtag, 1);
            }
        }

        public void readAndFlush() {
            System.out.println("\n\n");
            System.out.println("Twitter Trending Hashtags by Language: ");
            System.out.println(formatter.format(new Date()) + "\n");

            Set<String> keys = langMap.keySet();
            for (String key : keys) {
                System.out.println("Language: " + key);
                Map <String, Integer> tagCountMap = langMap.get(key);
                List<String> strings = mostFrequentHashTags(tagCountMap, 20);

                for (String string : strings) {
                    System.out.println("\t" + string + " => " + tagCountMap.get(string));
                }

                tagCountMap.clear();
            }
            System.out.println("\n\n");
        }

        private List<String> mostFrequentHashTags(Map<String, Integer> tagCountMap, int numberOfElements) {
            Ordering<String> stringOrdering = Ordering.natural().onResultOf(Functions.forMap(tagCountMap));
            return stringOrdering.greatestOf(tagCountMap.keySet(), numberOfElements);
        }

        @SuppressWarnings("unchecked")
        private Map<String, Integer> getMapForLanguage(String language) {
            Map<String, Integer> map = langMap.get(language);
            if (map == null)  {
                map = new ConcurrentHashMap<String, Integer>();
                langMap.put(language, map);
            }
            return map;
        }

    }

    public class Monitor {
        private TimerTask task = new TimerTask() {
            @Override
            public void run() {
                bucket.readAndFlush();
            }
        };

        public void start() {
            new Timer().scheduleAtFixedRate(task, 1000, 60000);
        }
    }

}
