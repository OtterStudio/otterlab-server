# Database, JPA, QueryDSL Rules

- FetchType 제한: 연관관계 매핑 시에는 양방향 매핑을 최소화하고, 필요할 때만 지연 로딩(`FetchType.LAZY`)을 기본으로 설정할 것. (`EAGER` 사용 금지)
- N+1 방지: 연관된 엔티티를 함께 조회해야 할 경우, Repository 메서드에 `@EntityGraph`를 적용하거나 QueryDSL의 `fetchJoin()`을 사용할 것.
- Entity 제약사항: Entity 클래스 레벨에서 `@Setter`와 `@Data` 어노테이션 사용을 엄격히 금지함. 상태 변경이 필요할 경우 `updateVoteCount()`, `changeGameStatus()`와 같이 도메인 의도가 명확한 메서드를 구현할 것.
- 기본 키(PK) 전략: 모든 Entity의 PK는 `Long` 타입으로 선언하고 `@GeneratedValue(strategy = GenerationType.IDENTITY)`를 사용할 것.
- QueryDSL 구조: 복잡한 동적 쿼리는 `CustomRepository` 인터페이스와 `RepositoryImpl` 구현체 패턴을 사용하여 작성할 것.