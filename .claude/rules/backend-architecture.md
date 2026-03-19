# Spring Boot Architecture & Layer Rules

- 디렉토리 구조: 클래스의 역할에 맞게 `controller/`, `service/`, `repository/`, `dto/`, `domain/` 패키지에 정확히 위치시킬 것.
- 트랜잭션 관리: Service 클래스 상단에는 기본적으로 `@Transactional(readOnly = true)`를 적용하고, 데이터 변경(C/U/D)이 일어나는 메서드에만 `@Transactional`을 오버라이딩하여 적용할 것.
- 의존성 주입: `@Autowired` 사용을 금지하고, 반드시 `final` 키워드와 `@RequiredArgsConstructor`를 이용한 생성자 주입을 사용할 것.
- DTO 처리: 클라이언트의 요청/응답(Request/Response DTO) 객체와 DB 영속성 객체(Entity)는 철저히 분리할 것. DTO와 Entity 간의 변환 로직은 Service 계층이나 별도의 Mapper 클래스에서 처리할 것.
- 파라미터 검증: Controller의 Request DTO에는 `@NotBlank`, `@NotNull` 등의 validation 어노테이션을 부착하고, 파라미터 앞에 `@Valid`를 선언하여 검증할 것.