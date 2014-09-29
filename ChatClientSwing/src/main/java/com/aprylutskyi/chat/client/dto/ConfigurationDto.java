package com.aprylutskyi.chat.client.dto;

import java.io.Serializable;

public class ConfigurationDto implements Serializable {

    private static final long serialVersionUID = 9056856499912636668L;

    private int port;

    private String ipAddress;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
        result = prime * result + port;
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
        ConfigurationDto other = (ConfigurationDto) obj;
        if (ipAddress == null) {
            if (other.ipAddress != null)
                return false;
        } else if (!ipAddress.equals(other.ipAddress))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Connection config [port=" + port + ", ipAddress=" + ipAddress + "]";
    }

}
