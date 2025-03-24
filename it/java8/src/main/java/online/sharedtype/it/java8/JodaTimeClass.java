package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType
final class JodaTimeClass {
    org.joda.time.DateTime jodaDateTime;
    org.joda.time.LocalDate jodaLocalDate;
    org.joda.time.MonthDay jodaMonthDay;
    org.joda.time.LocalTime jodaLocalTime;
    org.joda.time.LocalDateTime jodaLocalDateTime;
    org.joda.time.Instant jodaOffsetDateTime;
}
