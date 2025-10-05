package dev.shockman.tenant.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("outbox")
data class OutboxConfigurationProperties(
    /**
     * The duration that a message should be considered "leased" by the consumer.
     */
    val messageLease: Duration = Duration.ofSeconds(60),

    /**
     * The duration that a lease should be renewed before expiring.
     */
    val renewWindow: Duration = Duration.ofSeconds(30),

    /**
     * The maximum number of messages to poll in a single batch.
     */
    val messagePollLimit: Int = 10,

    /**
     * The maximum number of times to attempt processing a message before giving up.
     */
    val maxAttempts: Int = 5,

    /**
     * The initial delay before the first message is processed.
     */
    val initialDelay: Duration = Duration.ofSeconds(1),

    /**
     * The delay between each message poll.
     */
    val fixedDelay: Duration = Duration.ofSeconds(2)
)