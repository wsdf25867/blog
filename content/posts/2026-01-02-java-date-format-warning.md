---
title: "2025년 12월 31일이 2026년으로 출력되는 이유 (yyyy vs YYYY)"
date: 2026-01-02T22:00:00+09:00
draft: false
tags: ["Java", "Testing", "DateFormat"]
categories: ["Development"]
code_path: "codes/2025-java-date-test"
type: docs
---

자바에서 날짜를 포맷팅할 때 `yyyy` 와 `YYYY` 에 다른점에 대해 알고 계신가요? 

연말에 날짜가 미래로 찍히는 황당한 버그를 마주하게 됩니다.

단,그깟 문자열 차이였어요, 대문자 소문자 차이

이번 포스팅에서는 테스트 코드를 통해 왜 이런 현상이 발생하는지, 그리고 올바른 사용법은 무엇인지 알아보겠습니다.

### 문제의 현상: 2025년이 2026년으로?

2025년 12월 28일은 일요일입니다. 이 날짜를 `YYYYMMdd` 패턴으로 포맷팅하면 어떤 결과가 나올까요? 놀랍게도 결과는 **20261228**이 됩니다.

실제 테스트 코드로 확인해 보겠습니다.


```java
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
}
```

처음에는 자바에 레거시 라이브러리에 문제가 인줄 알았습니다. 

그래서 Java 8 이후에 등장한 `DateTimeFormatter`로 테스트 해봤습니다. 결과는 동일합니다.

```java
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
}
```

### 왜 이런 결과가 나오나요?

이유는 `yyyy`와 `YYYY`가 가리키는 의미가 다르기 때문입니다.

- **yyyy (Year)**: 우리가 흔히 생각하는 그해의 연도(Calendar year)를 의미합니다.
- **YYYY (Week based year)**: 해당 주가 속한 연도(Week-based-year)를 의미합니다.

[ISO-8601 기준](https://en.wikipedia.org/wiki/ISO_week_date)에 따르면 한 주의 시작은 월요일이고, 

그 해의 첫 번째 주는 '그 해의 4일 이상이 포함된 첫 번째 주'입니다.

핵심은 **특정 주가 다음 해로 넘어가는 시점에 걸쳐 있으면 `YYYY`는 다음 해를 반환할 수 있다**는 점입니다.

2025년 12월 28일 일요일이 포함된 주간이 2026년의 첫 번째 주(Week 1)로 간주되기 때문에 `YYYY`를 사용하면 2026년으로 표시되는 것입니다.

### 해결 방법: 항상 소문자 yyyy 사용하기

대부분의 비즈니스 로직에서는 달력상의 연도가 필요합니다. 따라서 날짜 포맷팅 시에는 **반드시 소문자 `yyyy`를 사용**해야 합니다.

```java
@Test
@DisplayName("올바른 연도 표기를 위해서는 반드시 소문자 yyyy를 사용해야 한다")
void correctYearFormatTest() {
    LocalDate date = LocalDate.of(2025, 12, 28);

    // 소문자 yyyy 사용
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formatted = date.format(formatter);

    // 정상적으로 2025 출력
    assertThat(formatted).isEqualTo("20251228");
}
```

### 요약

- `yyyy`는 **연도(Year)**, `YYYY`는 **주 기준 연도(Week Year)**입니다.
- 연말/연초 날짜 계산 오류를 방지하려면 무조건 `yyyy`를 사용하세요.
- 대문자 `YYYY`는 특별히 주 단위 통계 등을 계산할 때만 제한적으로 사용합니다.
