package com.gnip.consumers;

import com.gnip.StreamHandler;
import com.gnip.StreamIterator;

import java.io.InputStream;

public class FileStreamConsumer implements IStreamConsumer {

    public void getStream(StreamHandler handler) throws Exception {
        InputStream tweetFileStream = FileStreamConsumer.class.getResourceAsStream("/tweets_out.json");
        StreamIterator streamIterator = new StreamIterator(tweetFileStream);
        while (streamIterator.hasNext()) {
            handler.handleLine(streamIterator.next());
        }
    }

}
