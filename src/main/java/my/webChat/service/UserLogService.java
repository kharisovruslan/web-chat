package my.webChat.service;

import my.webChat.domain.UserLog;
import my.webChat.domain.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserLogService {
    @Autowired
    UserLogRepository repository;

    public void addLog(@NonNull String text, @NonNull String address) {
        UserLog userLog = new UserLog(text, LocalDateTime.now(), address);
        repository.save(userLog);
    }
}
