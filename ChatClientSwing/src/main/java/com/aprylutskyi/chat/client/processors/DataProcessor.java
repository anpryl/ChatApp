package com.aprylutskyi.chat.client.processors;

import com.aprylutskyi.chat.client.dto.Processable;

import java.io.DataOutputStream;
import java.io.Reader;

public interface DataProcessor {

    void processData(Reader reader);
    
    void sendData(Processable data, DataOutputStream outbound);
    
}
