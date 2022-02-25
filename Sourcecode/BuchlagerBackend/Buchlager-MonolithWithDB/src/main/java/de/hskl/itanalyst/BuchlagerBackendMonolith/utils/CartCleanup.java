package de.hskl.itanalyst.BuchlagerBackendMonolith.utils;

import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
@Transactional
public class CartCleanup {
    @Autowired
    private CartRepository cartRepository;

    @Scheduled(cron = "0 0 */12 ? * *")
    public void cleanupOldCarts() {
        cartRepository.deleteByValidUntilBefore(LocalDateTime.now());
    }
}
