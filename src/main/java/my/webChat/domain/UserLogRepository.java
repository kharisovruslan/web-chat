package my.webChat.domain;

import org.springframework.data.repository.CrudRepository;

public interface UserLogRepository extends CrudRepository<UserLog, Long> {
}
