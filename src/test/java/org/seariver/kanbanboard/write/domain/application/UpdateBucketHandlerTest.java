package org.seariver.kanbanboard.write.domain.application;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.exception.BucketNotExistentException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class UpdateBucketHandlerTest extends TestHelper {

    @Test
    void GIVEN_ValidCommand_MUST_UpdateBucketInDatabase() {

        // given
        var externalId = UUID.randomUUID();
        var name = faker.pokemon().name();
        int originalPosition = 123;
        var command = new UpdateBucketCommand(externalId.toString(), name);
        var repository = mock(WriteBucketRepository.class);
        var bucket = new Bucket().setExternalId(externalId).setPosition(originalPosition).setName("FOOBAR");
        when(repository.findByExteranlId(externalId)).thenReturn(Optional.of(bucket));

        // when
        var handler = new UpdateBucketHandler(repository);
        handler.handle(command);

        // then
        verify(repository).findByExteranlId(externalId);
        verify(repository).update(bucket);
        assertThat(bucket.getExternalId()).isEqualTo(externalId);
        assertThat(bucket.getPosition()).isEqualTo(originalPosition);
        assertThat(bucket.getName()).isEqualTo(name);
    }

    @Test
    void GIVEN_NotExistentBucket_MUST_ThrowException() {

        // given
        var externalId = UUID.randomUUID();
        var command = new UpdateBucketCommand(externalId.toString(), "WHATEVER");
        var repository = mock(WriteBucketRepository.class);
        when(repository.findByExteranlId(externalId)).thenReturn(Optional.empty());

        // when
        var handler = new UpdateBucketHandler(repository);
        var exception = assertThrows(BucketNotExistentException.class, () -> handler.handle(command));

        // then
        verify(repository).findByExteranlId(externalId);
        assertThat(exception.getMessage()).isEqualTo(String.format("Bucket not exist"));
    }
}
