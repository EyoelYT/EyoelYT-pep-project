package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Message;
import Model.Account;
import Service.AccountService;
import Service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;
    ObjectMapper objectMapper;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::registerNewUserHandler);
        app.post("/login", this::loginUserHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByUserHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void loginUserHandler(Context context) throws JsonProcessingException {
        String body = context.body();
        Account loginRequestAccountInfo = objectMapper.readValue(body, Account.class);

        String username = loginRequestAccountInfo.getUsername();
        String password = loginRequestAccountInfo.getPassword();

        if (username == null || password == null) {
            context.status(401);
            return;
        }

        Account verifiedAccount = accountService.verifyAccountLogin(username, password);

        if (verifiedAccount != null) {
            String accountAsJson = objectMapper.writeValueAsString(verifiedAccount);
            context.json(accountAsJson);
            context.status(200);
        } else {
            context.status(401);
        }
    }

    private void getAllMessagesByUserHandler(Context context) throws JsonProcessingException {
        int account_id = Integer.parseInt(context.pathParam("account_id"));

        List<Message> accountMessages = accountService.getAllMessagesByAccount(account_id);
        if (accountMessages != null) {
            context.json(accountMessages);
            context.status(200);
        } else {
            context.status(404).result("Messages not found");
        }
    }

    private void registerNewUserHandler(Context context) throws JsonProcessingException {
        String body = context.body();
        Account account = objectMapper.readValue(body, Account.class);

        
        if (account.getUsername() == null || account.getUsername().isBlank() || account.getUsername().equals("")) {
            context.status(400);
            return;
        }

        if (account.getPassword() == null || account.getPassword().length() < 4 || account.getPassword().equals("")) {
            context.status(400);
            return;
        }

        String username = account.getUsername();
        Account existingAccount = accountService.getAccountByUsername(username);
        if (existingAccount != null) {
            context.status(400);
            return;
        }

        Account addedAccount = accountService.addAccount(account);

        if (addedAccount != null) {
            context.json(objectMapper.writeValueAsString(addedAccount));
            context.status(200);
        } else {
            context.status(400).result("Account creation failed");
        }
    }

    private void postMessageHandler(Context context) throws JsonProcessingException {
        final int maxMessageChars = 255;

        String body = context.body();
        Message message = objectMapper.readValue(body, Message.class);

        if (message.getMessage_text() == null || message.getMessage_text().isBlank()) {
            context.status(400);
            return;
        }

        if (message.getMessage_text().length() > maxMessageChars) {
            context.status(400);
            return;
        }

        AccountService accountService = new AccountService();
        Account existingAccount = accountService.getAccountById(message.getPosted_by());
        if (existingAccount != null) {
            context.status(400);
            return;
        }

        Message addedMessage = messageService.addMessage(message);

        if (addedMessage != null) {
            context.json(objectMapper.writeValueAsString(addedMessage));
            context.status(200);
        } else {
            context.status(400);
        }
    }

    private void updateMessageHandler(Context context) throws JsonProcessingException {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message updatedMessage = objectMapper.readValue(context.body(), Message.class);
        String updatedMessage_text = updatedMessage.getMessage_text();
 
        if (updatedMessage.getMessage_text() == null
            || updatedMessage_text.length() < 1
            || updatedMessage_text.length() > 255
            || updatedMessage_text.isBlank()) {
            context.status(400);
            return;
        }
 
        if (messageService.messageExists(messageId) == null) {
            context.status(400);
            return;
        }
 
        Message patchedMessage = messageService.patchMessage(messageId, updatedMessage_text);
 
        if (patchedMessage != null) {
            context.json(objectMapper.writeValueAsString(patchedMessage));
            context.status(200);
        } else {
            context.status(400);
        }
    }

    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        List<Message> allMessages = messageService.getAllMessages();
        context.json(objectMapper.writeValueAsString(allMessages));
        context.status(200);
    }

    private void getMessageHandler(Context context) throws JsonProcessingException {
        int message_id = Integer.parseInt(context.pathParam("message_id"));
        Message fetchMessage = messageService.messageExists(message_id);
 
        if (fetchMessage == null) {
            context.result("");
        } else {
            context.json(objectMapper.writeValueAsString(fetchMessage));
        }
        context.status(200);
    }

    private void deleteMessageHandler(Context context) throws JsonProcessingException {
        int message_id = Integer.parseInt(context.pathParam("message_id"));
 
        Message messageExists = messageService.messageExists(message_id);
 
        if (messageExists == null) {
            context.status(200).result("");
        } else {
            messageService.deleteMessage(message_id);
            context.json(objectMapper.writeValueAsString(messageExists)).status(200);
        }
    }
}
