package com.aprylutskyi.chat.server.dto;

import com.aprylutskyi.chat.server.constants.ErrorType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDto implements Processable {

    public static final ErrorDto USER_LIMIT = new ErrorDto(ErrorType.USER_LIMIT);
    public static final ErrorDto OFFLINE = new ErrorDto(ErrorType.OFFLINE);
    public static final ErrorDto INVALID_NAME = new ErrorDto(ErrorType.INVALID_NAME);
    private static final long serialVersionUID = 8532221407891356226L;
    private String errorType;

    private ErrorDto() {
    }

    private ErrorDto(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorDto other = (ErrorDto) obj;
        if (errorType == null) {
            if (other.errorType != null)
                return false;
        } else if (!errorType.equals(other.errorType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ErrorDto [error=" + errorType + "]";
    }
}
