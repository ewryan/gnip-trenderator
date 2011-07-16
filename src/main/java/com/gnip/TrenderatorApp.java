package com.gnip;

import com.gnip.consumers.FileStreamConsumer;

public class TrenderatorApp {

    public static void main(String[] args) throws Exception {
        //Uncomment the following line and supply credentials in HTTPStreamConsumer if you want to use a Gnip Data Collector.
        //new HTTPStreamConsumer().getStream(new TrendingStreamHandler());

        //Use the following line for local testing
        new FileStreamConsumer().getStream(new TrendingStreamHandler());
    }

}
