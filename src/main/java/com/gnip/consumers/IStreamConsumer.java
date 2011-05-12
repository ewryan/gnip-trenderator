package com.gnip.consumers;


import com.gnip.StreamHandler;

public interface IStreamConsumer {
    public void getStream(StreamHandler handler) throws Exception;
}
