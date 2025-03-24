package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

@SharedType(
    typescriptTargetDatetimeTypeLiteral = "Date"
)
final class JavaTimeClass {
    java.util.Date utilDate;
    java.sql.Date sqlDate;

    LocalDate localDate;
    LocalTime localTime;
    LocalDateTime localDateTime;
    ZonedDateTime zonedDateTime;
    OffsetDateTime offsetDateTime;
    OffsetTime offsetTime;
    java.time.Instant instant;
}
