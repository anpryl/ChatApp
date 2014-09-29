package com.aprylutskyi.chat.client.view.frames;

import com.aprylutskyi.chat.client.dto.ErrorDto;
import com.aprylutskyi.chat.client.dto.MessageDto;
import com.aprylutskyi.chat.client.dto.UserDto;
import com.aprylutskyi.chat.client.dto.UsersListDto;
import com.aprylutskyi.chat.client.util.TimeHelper;
import com.aprylutskyi.chat.client.view.FrameManager;
import com.aprylutskyi.chat.client.view.frames.errorhandlers.ViewErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ChatView extends JFrame {

    private static final long serialVersionUID = -6544981933351675669L;

    private UserDto user;

    private ViewErrorHandler errorHandler = new ViewErrorHandler();

    private FrameManager frameManager;

    private JMenuBar menuBar;

    private JMenu menu;

    private JMenuItem info;

    private JMenuItem refreshItem;

    private JMenuItem exitItem;

    private JTextArea chatArea;

    private JTextArea messageArea;

    private JTextArea onlineUsersList;

    private JButton sendButton;

    private JTextField errorField;

    public ChatView(FrameManager frameManager) {
        this.frameManager = frameManager;
        init();
    }

    public void onConnectionLost() {
        setErrorMessage("Connection lost");
        refreshItem.setEnabled(true);
        sendButton.setEnabled(false);
    }

    public void addMessage(MessageDto message) {
        chatArea.append(getFormattedMessage(message));
    }

    public void updateOnlineUserList(UsersListDto users) {
        List<UserDto> usersList = users.getUsers();
        onlineUsersList.setText("");
        for (UserDto userDto : usersList) {
            onlineUsersList.append(userDto.getName() + "\n");
        }
    }

    public void onError(ErrorDto error) {
        setErrorMessage(errorHandler.getErrorMessage(error));
        sendButton.setEnabled(false);
        refreshItem.setEnabled(true);
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void openInfoTip() {
        JOptionPane.showMessageDialog(this, "Socket chat by Anatolii Prylutskyi, 2014", "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void enableSendButton() {
        sendButton.setEnabled(true);
        refreshItem.setEnabled(false);
        setInfoMessage("Send message");
    }

    public void clearMessages() {
        messageArea.setText("");
    }

    private void init() {
        addWindowListener(new OnCloseListener());
        setLayout(null);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        addMenuBar();
        addOnlineUsersList();
        addChat();
        addMessageField();
        addErrorField();
    }

    private void addErrorField() {
        errorField = new JTextField();
        errorField.setBorder(BorderFactory.createLineBorder(null, 0));
        errorField.setEditable(false);
        errorField.setBounds(225, 0, 350, 30);
        errorField.setHorizontalAlignment(JTextField.CENTER);
        add(errorField);
    }

    private void addMessageField() {
        JTextField chatTitle = new JTextField();
        chatTitle.setText("Enter your message (To send message: CTRL+ENTER)");
        chatTitle.setEditable(false);
        chatTitle.setBounds(220, 455, 555, 25);
        chatTitle.setHorizontalAlignment(JTextField.CENTER);
        add(chatTitle);
        messageArea = new JTextArea();
        messageArea.setEditable(true);
        messageArea.setBounds(220, 480, 430, 75);
        add(messageArea);
        sendButton = new JButton(new SendActionListener());
        sendButton.setText("Send message");
        sendButton.setBounds(650, 480, 120, 75);
        getRootPane().setDefaultButton(sendButton);
        add(sendButton);
    }

    private void addChat() {
        JTextField chatTitle = new JTextField();
        chatTitle.setText("Chat window");
        chatTitle.setEditable(false);
        chatTitle.setBounds(220, 30, 555, 25);
        chatTitle.setHorizontalAlignment(JTextField.CENTER);
        add(chatTitle);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBounds(220, 55, 555, 400);
        add(chatArea);
    }

    private void addOnlineUsersList() {
        JTextField onlineUsers = new JTextField();
        onlineUsers.setText("Online users");
        onlineUsers.setEditable(false);
        onlineUsers.setBounds(0, 30, 200, 25);
        onlineUsers.setHorizontalAlignment(JTextField.CENTER);
        add(onlineUsers);
        onlineUsersList = new JTextArea();
        onlineUsersList.setBounds(0, 55, 200, 500);
        onlineUsersList.setEditable(false);
        add(onlineUsersList);
    }

    private void addMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 100, 30);
        add(menuBar);

        menu = new JMenu("Chat");
        menuBar.add(menu);

        refreshItem = new JMenuItem(new RefreshConnectionAction());
        menu.add(refreshItem);
        refreshItem.setEnabled(false);
        refreshItem.setText("Reconnect");

        exitItem = new JMenuItem(new ExitAction());
        exitItem.setText("Exit");
        menu.add(exitItem);

        info = new JMenuItem(new InfoAction());
        info.setText("Info");
        menuBar.add(info);

    }

    private void setErrorMessage(String message) {
        errorField.setBackground(Color.ORANGE);
        errorField.setText(message);
    }

    private void setInfoMessage(String message) {
        errorField.setBackground(Color.GREEN);
        errorField.setText(message);
    }

    private String getFormattedMessage(MessageDto message) {
        String msg = message.getDate() + " " + message.getAuthor() + " : " + message.getText() + "\n";
        return msg;
    }

    private class RefreshConnectionAction extends AbstractAction {
        private static final long serialVersionUID = -2017696724533182609L;

        @Override
        public void actionPerformed(ActionEvent e) {
            setInfoMessage("Reconnection...");
            EventQueue.invokeLater(new Reconecter());
        }
    }

    private class ExitAction extends AbstractAction {

        private static final long serialVersionUID = 7393556556300579877L;

        @Override
        public void actionPerformed(ActionEvent e) {
            frameManager.clientDisconnect();
            System.exit(-1);
        }
    }

    private class OnCloseListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            frameManager.clientDisconnect();
            System.exit(-1);
        }
    }

    private class InfoAction extends AbstractAction {
        private static final long serialVersionUID = 948123916548227714L;

        @Override
        public void actionPerformed(ActionEvent e) {
            openInfoTip();
        }
    }

    private class SendActionListener extends AbstractAction {
        private static final long serialVersionUID = 1688978598538217216L;

        @Override
        public void actionPerformed(ActionEvent e) {
            String messageText = messageArea.getText().trim();
            String name = user.getName();
            messageArea.setText("");
            if (!messageText.isEmpty()) {
                MessageDto messageDto = new MessageDto();
                messageDto.setAuthor(name);
                messageDto.setDate(TimeHelper.getNow());
                messageDto.setText(messageText);
                frameManager.sendMessage(messageDto);
            }
        }
    }

    private class Reconecter implements Runnable {
        @Override
        public void run() {
            frameManager.initialConnection();
        }

    }
}
