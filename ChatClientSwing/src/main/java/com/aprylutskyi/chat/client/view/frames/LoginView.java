package com.aprylutskyi.chat.client.view.frames;

import com.aprylutskyi.chat.client.constants.ConfigConstants;
import com.aprylutskyi.chat.client.constants.UserStatus;
import com.aprylutskyi.chat.client.dto.ConfigurationDto;
import com.aprylutskyi.chat.client.dto.ErrorDto;
import com.aprylutskyi.chat.client.dto.UserDto;
import com.aprylutskyi.chat.client.view.FrameManager;
import com.aprylutskyi.chat.client.view.frames.errorhandlers.ViewErrorHandler;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginView extends JFrame {

	private static final long serialVersionUID = -6037500631291110827L;

	private FrameManager frameManager;
	
	private ViewErrorHandler errorHandler = new ViewErrorHandler();

	private JTextField loginField;

	private JSpinner portField;

	private JFormattedTextField ipAddress;

	private JTextField errorField;

	private JButton enterButton;

	private LoginAction loginAction = new LoginAction();

	private CloseAction closeAction = new CloseAction();

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private Pattern ipAddressPattern;

	public LoginView(FrameManager frameManager) {
		this.frameManager = frameManager;
	}

	public void init() {
		ipAddressPattern = Pattern.compile(IPADDRESS_PATTERN);
		setLayout(null);
		addErrorField();
		addLoginField();
		addIpAddressField();
		addPortField();
		addErrorField();
		addButtons();
		setSize(450, 200);
		setTitle("����");
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		addWindowListener(new OnCloseListener());
	}

	public void onError(ErrorDto error) {
		setErrorMessage(errorHandler.getErrorMessage(error));
		enterButton.setEnabled(true);
	}

	public void setErrorMessage(String error) {
		errorField.setBackground(Color.ORANGE);
		errorField.setText(error);
		enterButton.setEnabled(true);
	}

	public void setInfoMessage(String info) {
		errorField.setBackground(Color.GREEN);
		errorField.setText(info);
	}

	private void addButtons() {
		enterButton = new JButton(loginAction);
		enterButton.setText("����");
		enterButton.setBounds(100, 120, 75, 30);
		add(enterButton);
		getRootPane().setDefaultButton(enterButton);

		JButton closeButton = new JButton(closeAction);
		closeButton.setText("�����");
		closeButton.setBounds(275, 120, 75, 30);
		add(closeButton);

	}

	private void addErrorField() {
		errorField = new JTextField();
		errorField.setBorder(BorderFactory.createLineBorder(null, 0));
		errorField.setEditable(false);
		errorField.setBounds(100, 10, 250, 25);
		errorField.setHorizontalAlignment(JTextField.CENTER);
		add(errorField);
	}

	private void addPortField() {
		JTextField addressFieldInfo = new JTextField("����:");
		addressFieldInfo.setEditable(false);
		addressFieldInfo.setBounds(280, 80, 37, 25);
		add(addressFieldInfo);

		portField = new JSpinner(new SpinnerNumberModel(ConfigConstants.DEFAULT_PORT, ConfigConstants.MIN_PORT,
				ConfigConstants.MAX_PORT, 1));
		portField.setBounds(317, 80, 70, 25);
		add(portField);
	}

	private void addIpAddressField() {
		JTextField addressFieldInfo = new JTextField("IP �����:");
		addressFieldInfo.setEditable(false);
		addressFieldInfo.setBounds(20, 80, 115, 25);
		add(addressFieldInfo);

		try {
			MaskFormatter mf = new MaskFormatter("###.###.###.###");
			ipAddress = new JFormattedTextField(mf);
			ipAddress.setText("127.000.000.001");
		} catch (ParseException e) {
		}

		ipAddress.setBounds(135, 80, 115, 25);
		add(ipAddress);
	}

	private void addLoginField() {
		JTextField loginFieldInfo = new JTextField("��� ������������:");
		loginFieldInfo.setEditable(false);
		loginFieldInfo.setBounds(20, 50, 115, 25);
		add(loginFieldInfo);

		loginField = new JTextField();
		loginField.setBounds(135, 50, 115, 25);
		add(loginField);
	}

	private class LoginAction extends AbstractAction {

		private static final long serialVersionUID = -6806227486866849928L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fieldsValid()) {
				UserDto owner = new UserDto(loginField.getText(), UserStatus.ONLINE);
				ConfigurationDto configurationDto = new ConfigurationDto();
				configurationDto.setIpAddress(ipAddress.getText());
				configurationDto.setPort((Integer) portField.getValue());
				setInfoMessage("������������...");
				enterButton.setEnabled(false);
				EventQueue.invokeLater(new InitialConnecter(owner, configurationDto));
			}
		}

		private boolean fieldsValid() {
			String loginName = loginField.getText();
			if (loginName.trim().isEmpty()) {
				setErrorMessage("������ ���");
				return false;
			}
			String ip = ipAddress.getText();
			Matcher ipMatcher = ipAddressPattern.matcher(ip);
			if (!ipMatcher.matches()) {
				setErrorMessage("������� ���������� IP �����");
				return false;
			}
			return true;
		}

	}

	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 751399255465045671L;

		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(-1);
		}

	}

	private class OnCloseListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(-1);
		}
	}

	private class InitialConnecter implements Runnable {

		private UserDto owner;

		private ConfigurationDto config;

		public InitialConnecter(UserDto owner, ConfigurationDto config) {
			this.owner = owner;
			this.config = config;
		}

		@Override
		public void run() {
			frameManager.initialConnection(owner, config);
		}

	}

	public void onConnectionLost() {
		if (errorField.getText().isEmpty()){
			setErrorMessage("����������� �� �������� ���������");
		}
	}
}
