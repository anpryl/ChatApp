package com.aprylutskyi.chat.server.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.dto.ErrorDto;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;

public class JAXBHelper {

    private final static Logger LOGGER = Logger.getLogger(JAXBHelper.class);

    private static JAXBContext jaxbContext;

    private static Class<?>[] mappedClasses = {MessageDto.class, MessagesListDto.class,
                                               UserDto.class, UsersListDto.class,
                                               ErrorDto.class};
    
    static {
        try {
            jaxbContext = JAXBContext.newInstance(mappedClasses);
        } catch (JAXBException e) {
            LOGGER.error("Error occured while loading JAXBContext");
            System.exit(-1);
        }
    }

    private JAXBHelper(){}
    
    public static JAXBContext getJaxbContext() {
        return jaxbContext;
    }

}
