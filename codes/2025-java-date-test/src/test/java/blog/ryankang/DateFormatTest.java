package blog.ryankang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DateFormatTest {

    @Test
    @DisplayName("SimpleDateFormat에서 대문자 YYYY를 사용하면 2025년 12월 28일은 2026년으로 출력된다")
    void simpleDateFormatTest() {
        // Given: 2025년 12월 28일 설정 (일요일)
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.DECEMBER, 28);
        Date time = cal.getTime();

        // When: 대문자 YYYY 패턴 사용
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd", Locale.KOREA);
        String formatted = sdf.format(time);

        // Then: 2025년임에도 불구하고 Week Year 기준에 따라 2026으로 출력됨을 검증
        assertThat(formatted).isEqualTo("20261228");
        System.out.println("SimpleDateFormat (YYYY): " + formatted);
    }

    @Test
    @DisplayName("DateTimeFormatter에서도 대문자 YYYY를 사용하면 동일하게 2026년으로 출력된다")
    void dateTimeFormatterTest() {
        // Given: 2025년 12월 28일 LocalDate 생성
        LocalDate date = LocalDate.of(2025, 12, 28);

        // When: 대문자 YYYY 패턴 사용
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String formatted = date.format(formatter);

        // Then: 결과는 역시 20261228
        assertThat(formatted).isEqualTo("20261228");
        System.out.println("DateTimeFormatter (YYYY): " + formatted);
    }

    @Test
    @DisplayName("올바른 연도 표기를 위해서는 반드시 소문자 yyyy를 사용해야 한다")
    void correctYearFormatTest() {
        LocalDate date = LocalDate.of(2025, 12, 28);

        // 소문자 yyyy 사용
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formatted = date.format(formatter);

        // 정상적으로 2025 출력
        assertThat(formatted).isEqualTo("20251228");
        System.out.println("Correct Format (yyyy): " + formatted);
    }

    @Test
    @DisplayName("시작일자 구하기")
    void startDayTest() {
        Locale.availableLocales()
                .collect(Collectors.groupingBy(WeekFields::of))
                .forEach((k, v) -> System.out.println(k + ": " + v));
    }
}