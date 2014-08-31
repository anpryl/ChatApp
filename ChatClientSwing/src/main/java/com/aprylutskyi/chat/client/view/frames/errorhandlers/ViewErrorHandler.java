package com.aprylutskyi.chat.client.view.frames.errorhandlers;

import com.aprylutskyi.chat.client.dto.ErrorDto;

public class ViewErrorHandler {

	public String getErrorMessage(ErrorDto error) {
		String errorMessage = "";

		if (ErrorDto.OFFLINE.equals(error)) {
			errorMessage = "Соединение разорвано сервером";
		} else if (ErrorDto.INVALID_NAME.equals(error)) {
			errorMessage = "Измените имя";
		} else if (ErrorDto.USER_LIMIT.equals(error)) {
			errorMessage = "Превышен лимит юзеров";
		} else if (ErrorDto.INVALID_PARAMETERS.equals(error)) {
			errorMessage = "Сервер не отвечает, проверьте адрес и порт";
		} else if (ErrorDto.DISCONNECTED.equals(error)) {
			errorMessage = "Соединение неожиданно разорвано";
		}
		return errorMessage;
	}
}
