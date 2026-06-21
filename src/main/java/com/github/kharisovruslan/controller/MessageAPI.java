package com.github.kharisovruslan.controller;

import com.github.kharisovruslan.domain.Message;
import com.github.kharisovruslan.domain.User;
import com.github.kharisovruslan.domain.dto.UserMessage;
import com.github.kharisovruslan.service.ActiveUser;
import com.github.kharisovruslan.service.FileUtils;
import com.github.kharisovruslan.service.MessageService;
import com.github.kharisovruslan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("messagesAPI")
public class MessageAPI {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Autowired
    ActiveUser activeUser;

    @Autowired
    MessageService service;
    @Autowired
    FileUtils fileUtils;
    @Resource(name = "authenticationManager")
    private AuthenticationManager authManager;

    @PostMapping(value = "last", produces = "text/plain")
    public String getLast(@RequestParam("token") String token,
                          @RequestParam(value = "version", defaultValue = "1.0", required = false) String version) {
        User user = userService.getUserByToken(UUID.fromString(token));
        activeUser.updateUser(user);
        userService.updateClientVersion(user, version);
        return messageService.getLastTime(user);
    }

    @GetMapping("loginToken")
    public void loginToken(@RequestParam("token") String token,
                           final HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        User user = userService.getUserByToken(UUID.fromString(token));
        // only as user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
        response.sendRedirect("/form");
    }

    @GetMapping(value = "file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> messageFile(@RequestParam("token") String token,
                                              @RequestParam(name = "uuid") String uuid,
                                              @RequestParam(name = "fileName") String fileName) throws IOException, URISyntaxException {
        User user = userService.getUserByToken(UUID.fromString(token));
        HttpHeaders headers = new HttpHeaders();
        String contentDisposition = "attachment; filename*=UTF-8''" + UriUtils.encodePath(fileName, "UTF-8");
        headers.set("Content-disposition", contentDisposition);
        return ResponseEntity.ok().headers(headers).body(service.sendFile(uuid, user));
    }

    public String getFileNameByDefault(String fileName) {
        String name = "file" + fileName.substring(fileName.lastIndexOf("."));
        return name;
    }

    private String makeHTMLFromMessage(Message msg, User user, String serverAddress) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        if (msg.getText().indexOf("<") == -1) {
            sb.append("<pre>");
            sb.append(msg.getText());
            sb.append("</pre>");
        } else {
            sb.append(msg.getText());
        }
        if (!msg.getFileOrigName().isEmpty()) {
            if (fileUtils.isFileImage(msg.getFileOrigName())) {
                sb.append("<img src=\"http://");
                sb.append(serverAddress);
                sb.append("/messagesAPI/file?token=");
                sb.append(user.getToken());
                sb.append("&uuid=");
                sb.append(msg.getFileName());
                sb.append("&fileName=");
                sb.append(getFileNameByDefault(msg.getFileOrigName()));
                sb.append("\"></img>");
            }
        }
        sb.append("</html>");
        return sb.toString();
    }

    @PostMapping(value = "messagesafter", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserMessage> getMessages(@RequestParam("token") String token,
                                         @RequestParam("last") String afterTime,
                                         HttpServletRequest request) {
        User user = userService.getUserByToken(UUID.fromString(token));
        activeUser.updateUser(user);
        StringBuilder sb = new StringBuilder();
        sb.append(request.getLocalAddr());
        sb.append(":");
        sb.append(Integer.toString(request.getLocalPort()));

        return messageService.getUserMessagesAfter(user, afterTime).stream()
                .map(m -> new UserMessage(makeHTMLFromMessage(m, user, sb.toString()), m.getCreate(), m.getAuthor().getUsername()))
                .collect(Collectors.toList());
    }
}
