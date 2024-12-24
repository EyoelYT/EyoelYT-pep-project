package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {

    public MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message addMessage(Message message) {
        return messageDAO.insertMessage(message);
    }

    public Message messageExists(int messageToBeUpdatedId) {
        return messageDAO.getMessageById(messageToBeUpdatedId);
    }

    public Message patchMessage(int message_id, String updatedMessage_text) {
        return messageDAO.updateMessage(message_id, updatedMessage_text);
    }

    public Message deleteMessage(int message_id) {
        return messageDAO.deleteMessage(message_id);
     }
    
}
