package com.gnip;

import com.gnip.consumers.FileStreamConsumer;

public class TrenderatorApp {

    public static void main(String[] args) throws Exception {
//        new HTTPStreamConsumer().getStream(new TrendingStreamHandler());
        new FileStreamConsumer().getStream(new TrendingStreamHandler());
    }

}
