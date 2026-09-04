package ff_website.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.User;
import ff_website.entity.WithdrawalRequest;

public interface WithdrawalRequestRepository
        extends JpaRepository<WithdrawalRequest, Long> {

    List<WithdrawalRequest> findByUserOrderByRequestedAtDesc(User user);

    List<WithdrawalRequest> findByStatusOrderByRequestedAtAsc(String status);
}
