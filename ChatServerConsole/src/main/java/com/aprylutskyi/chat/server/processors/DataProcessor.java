package com.aprylutskyi.chat.server.processors;

import com.aprylutskyi.chat.server.dto.Processable;

import java.io.DataOutputStream;
import java.io.Reader;

public interface DataProcessor {

    void processData(Reader reader);

    void sendData(Processable data, DataOutputStream outbound);

}
