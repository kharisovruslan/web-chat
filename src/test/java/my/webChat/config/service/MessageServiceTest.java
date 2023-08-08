package my.webChat.config.service;

import my.webChat.domain.*;
import my.webChat.domain.dto.StatisticsUser;
import my.webChat.service.MessageService;
import my.webChat.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    MessageService messageService;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    private User u1;
    private User u2;
    private User u3;
    private long u2_id;
    private String uuidFile;

    @BeforeEach
    void initDatabase() {
        u1 = new User("admin", "password");
        u2 = new User("user2", "password");
        u3 = new User("user3", "password");

        uuidFile = UUID.randomUUID().toString();
        userService.addUser(u1);
        userService.addUser(u2);
        userService.addUser(u3);

        Message hello_from_admin_to_u2 =
                messageService.addMessage("Hello from admin to u2", u1, Set.of(u2),
                        uuidFile, "", 0);
        Message hello_from_admin_to_u3 =
                messageService.addMessage("Hello from admin to u3", u1, Set.of(u3),
                        "", "", 0);
        messageService.addMessage("Hello from u2 to u3", u2, Set.of(u3), "", "", 0);
        Message m_l = messageService.addMessage("Hello from u3 to admin", u3, Set.of(u1), "", "", 0);
        messageService.addMessage("Hello from u2 to admin", u2, Set.of(u1), "", "", 0);
        u2_id = m_l.getId();
    }

    @AfterEach
    void releaseDatabse() {
        messageRepository.deleteAll();
        Assertions.assertEquals(0, messageRepository.findAll().size(), "Error remove message");
        userRepository.deleteAll();
        Assertions.assertEquals(0, userRepository.findAll().size(), "Error remove user");
    }

    @Test
    void findAuthorMessageOrMessageForUser() {
        Assertions.assertEquals(5, messageRepository.findAll().size(), "Error add message");
        Pageable page = PageRequest.of(0, 10);
        List<Message> adminMessages = messageService.findAuthorMessageOrMessageForUser(u1, page).getContent();
        List<Message> adminMessagesFilter = messageService.findAuthorMessageOrMessageForUser(u1, page, "u2").getContent();
        Assertions.assertEquals(2, adminMessagesFilter.size());
        Assertions.assertEquals(4, adminMessages.size());
        List<Message> u2Messages = messageService.findAuthorMessageOrMessageForUser(u2, page).getContent();
        Assertions.assertEquals(3, u2Messages.size());
        List<Message> u3Messages = messageService.findAuthorMessageOrMessageForUser(u3, page).getContent();
        Assertions.assertEquals(3, u3Messages.size());
        Assertions.assertEquals(u2_id, u3Messages.get(0).getId());
        List<StatisticsUser> statistics = messageService.getStatisticsSendMessages();
        Assertions.assertNotNull(statistics);
        Assertions.assertEquals(3, statistics.size());

        Exception exception = Assertions.assertThrows(IOException.class, () -> messageService.sendFile(uuidFile, u3));
        String errorMessageException = "Error access to file " + uuidFile;
        Assertions.assertTrue(exception.getMessage().contains(errorMessageException));
        Exception exceptionIO = Assertions.assertThrows(IOException.class, () -> messageService.sendFile(uuidFile, u1));
        Assertions.assertTrue(exceptionIO instanceof NoSuchFileException);
        Exception exceptionIOR = Assertions.assertThrows(IOException.class, () -> messageService.sendFile(uuidFile, u2));
        Assertions.assertTrue(exceptionIOR instanceof NoSuchFileException);

        Assertions.assertEquals(u2_id, Integer.parseInt(messageService.getLastID(u3)));
    }
}