package com.aprylutskyi.chat.client.view.frames.errorhandlers;

import com.aprylutskyi.chat.client.dto.ErrorDto;

public class ViewErrorHandler {

    public String getErrorMessage(ErrorDto error) {
        String errorMessage = "";

        if (ErrorDto.OFFLINE.equals(error)) {
            errorMessage = "���������� ��������� ��������";
        } else if (ErrorDto.INVALID_NAME.equals(error)) {
            errorMessage = "�������� ���";
        } else if (ErrorDto.USER_LIMIT.equals(error)) {
            errorMessage = "�������� ����� ������";
        } else if (ErrorDto.INVALID_PARAMETERS.equals(error)) {
            errorMessage = "������ �� ��������, ��������� ����� � ����";
        } else if (ErrorDto.DISCONNECTED.equals(error)) {
            errorMessage = "���������� ���������� ���������";
        }
        return errorMessage;
    }
}
