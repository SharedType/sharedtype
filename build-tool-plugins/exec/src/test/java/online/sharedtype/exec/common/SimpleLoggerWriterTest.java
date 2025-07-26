package online.sharedtype.exec.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
final class SimpleLoggerWriterTest {
    private @Mock Logger log;
    private SimpleLoggerWriter logger;
    private @Captor ArgumentCaptor<String> messageCaptor;

    @BeforeEach
    void setup() {
        logger = new SimpleLoggerWriter(log);
    }

    @Test
    void removeEndNewLine() throws Exception {
        logger.append("some ");
        logger.append("content\r\n\n");

        logger.flush();
        verify(log).info(messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo("some content");
    }
}
