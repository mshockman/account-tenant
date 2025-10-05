package dev.shockman.tenant.config

import com.authzed.api.v1.PermissionsServiceGrpc
import com.authzed.grpcutil.BearerToken
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpiceDbConfiguration {
    @Bean
    fun spiceDbChannel(): ManagedChannel {
        return ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build()
    }

    @Bean
    fun bearerToken(): CallCredentials {
        return BearerToken("dev-secret")
    }

    @Bean
    fun spiceDbPermissionService(channel: ManagedChannel, token: CallCredentials): PermissionsServiceGrpc.PermissionsServiceBlockingStub {
        return PermissionsServiceGrpc.newBlockingStub(channel).withCallCredentials(token)
    }
}