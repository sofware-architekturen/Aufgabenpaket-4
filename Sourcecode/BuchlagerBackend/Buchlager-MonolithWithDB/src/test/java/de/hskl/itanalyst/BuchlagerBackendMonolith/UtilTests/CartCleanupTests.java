package de.hskl.itanalyst.BuchlagerBackendMonolith.UtilTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.utils.CartCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class CartCleanupTests {
    @SpyBean
    private CartCleanup cartCleanup;

    @MockBean
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() {
        Mockito.doNothing().when(cartRepository).deleteByValidUntilBefore(Mockito.any());
    }

    @Test
    public void whenWaitForThreeHours_thenCartIsDeleted() {
        await().atMost(12, TimeUnit.HOURS).untilAsserted(() -> verify(cartCleanup, atMostOnce()).cleanupOldCarts());

        Mockito.verify(cartRepository, VerificationModeFactory.atMostOnce()).deleteByValidUntilBefore(Mockito.any());
    }
}
