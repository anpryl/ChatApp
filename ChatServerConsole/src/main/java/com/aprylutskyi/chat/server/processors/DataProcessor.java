package com.aprylutskyi.chat.server.processors;

import java.io.DataOutputStream;
import java.io.Reader;

import com.aprylutskyi.chat.server.dto.Processable;

public interface DataProcessor {

    void processData(Reader reader);
    
    void sendData(Processable data, DataOutputStream outbound);
    
}
