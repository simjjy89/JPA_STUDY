JPA는 복잡한 검색 조건을 사용해서 엔티티 객체를 조회할 수 있는 다양한 쿼리 기술을 지원한다.

- JPQL
- QueryDSL
- 네이티브 SQL

### JPQL(Java Persistence Query Language) 특징?

쿼리 대상의 차이

- JPQL - 엔티티 객체를 대상으로 검색하는 객체지향 쿼리
- SQL - 데이터베이스 테이블 대상

SQL을 추상화 → 데이터베이스 SQL에 의존적이지 않음

### JPQL 쓰는 이유?

- JPA에서 제공하는 메서드 호출만으로는 섬세한 쿼리작성이 어려움
- 특정 필드만 필요한데 JPA로 모든 필드를 메모리에 올리기에는 비효율적인 경우

JPQL 사용

```kotlin
val result: List<Member> = em.createQuery(
            "select m From Member m where m.name like '%재진%'", Member::class.java).resultList
```

- 엔티티에 대해서 조회

실제 실행된 SQL

```kotlin
Hibernate: 
    /* select
        m 
    From
        Member m 
    where
        m.name like '%재진%' */ select
            member0_.id as id1_0_,
            member0_.age as age2_0_,
            member0_.name as name3_0_ 
        from
            Member member0_ 
        where
            member0_.name like '%재진%'
```

- 하이버네이트가 Entity 매핑정보를 확인하여 적절한 SQL로 변환

### JPQL로 조회한 엔티티와 영속성 컨텍스트

- JPQL로 조회한 엔티티는 영속성 컨텍스트에서 관리
- 엔티티가 아니라면 영속성 컨텍스트에서 관리되지 않음
- 항상 영속성 컨테스트보다 DB를 먼저 조회. JPQL은 SQL로 변환되어 DB로 바로 조회하기 때문

```kotlin
select m from Member m // 엔티티 조회 (관리 O) 

select o.address from Order o // (관리 X)
```

JPQL로 DB조회한 엔티티가 이미 영속성 컨텍스트에 있다면 JPQL로 DB에서 조회한 결과는 버리고 영속성 컨텍스트에 있는 엔티티 반환. (식별자를 통해 비교)

## JPQL과 Flush

- JPA는 기본적으로 영속성 컨텍스트를 이용하여 쓰기지연을함. 하지만 기본적으로 JPQL을 만드는 순간 Flush 하여 DB와 동기화 함.

- JPQL이 사용되면서 commit이 발생되므로 자주 사용하는 경우 빈번하게 commit이 일어남

  - 플러시 모드를 설정하여 COMMIT 시에만 플러시가 일어나게 할 수 있음

  ```kotlin
  em.setFlushMode(FlushModeType.COMMIT)
  ```

### JPQL 단점

- 동적 쿼리를 만들기 어려움
- 쿼리상에 오타가 있을때 런타임 시점에 오류 발생

### JPQL Update를 조심한다

JPA와 함께 사용할 경우 이런 경우를 생각해보자

1. JPA를 통해 1번 데이터 쿼리  → 영속성 컨텍스트에 올라감. DB에는 없음
2. JPQL update query로 1번 데이터 수정 → DB직접 쿼리
3. JPA를 통해 1번 데이터 다시 쿼리 → 영속성 컨텍스트에 있는 데이터 확인. **수정된 데이터가 확인되지 않음**

### Criteria 란?

- JPQL을 생성하는 빌더 클래스
- 문자가 아닌 코드로 JPQL 을 작성 할 수 있음
- JPA 표준 스펙
- 컴파일 시점에 오류를 발견할 수 있음.
- 동적 쿼리 작성하기 편함

Criteria 사용

```kotlin
//Criteria 사용 준
val cb: CriteriaBuilder = em.criteriaBuilder
val query: CriteriaQuery<Member> = cb.createQuery(Member::class.java)

//루트 클래스 (조회를 시작할 클래스)
val m: Root<Member> = query.from(Member::class.java)

//쿼리 생성
val cq: CriteriaQuery<Member> = query.select(m).where(cb.equal(m.get<String>("name"), "재진"))
val result: List<Member> = em.createQuery(cq).resultList
```

### Criteria 단점

- 너무 복잡하고 실용성이 없음.
- SQL 문법 오류는 예방 할 수 있지만 필드명에서 오타가 나면 마찬가지로 런타임시에 오류 발생
- 실무에서 잘 쓰이지 않음.

## QueryDSL

- 문자가 아닌 코드로 JPQL 을 작성 할 수 있음
- 컴파일 시점에 문법 오류를 찾을 수 있음
- 동적 쿼리 작성 용이
- 비교적 단순함
- 실무사용 권장

QueryDSL 사용

```kotlin
val m: QMember = QMember.member
val result: List<Member> = queryFactory
		.select(m)
		.from(m)
		.where(m.name.like("재진"))
		.fetch()
```

## 네이티브 SQL

- SQL을 직접 사용할 수 있는 기능
- 엔티티를 조회
- JPA가 지원하는 영속성 컨텍스트 등의 기능은 그대로 사용 가능
- JPQL이 지원하지 않는 특정 데이터베이스에 의존적인 기능 사용 가능

```kotlin
val sql = "SELECT id, name, age from MEMBER"
val result: List<Member> = em.createNativeQuery(sql,Member::class.java).resultList as List<Member>
```

## JPQL(Java Persistence Query Language)

### JPQL 문법

SELECT 문

ex) select m from Member as m where m.age > 18

- 엔티티와 속성은 대소문자 구분 ( Member, age)
- 키워드는 대소문자 구문 X (SELECT, FROM)
- 엔티티 이름 사용. 테이블 X ( Member)
- 별칭 사용 필수(m)

### TypeQuery, Query

작성한 jpql을 실행하기 위한 쿼리 객체

TypeQuery

- 반환타입이 명확한 경우에 사용

```kotlin
val query: TypedQuery<Member> = em.createQuery(
            "select m From Member m", Member::class.java
        )

val resultList: List<Member> = query.resultList
```

Query

- 반환타입이 명확하지 않은 경우

```kotlin
val query: Query = em.createQuery("select m From Member m")
val result = query.resultList
```

### 파라미터 바인딩

이름 기준

```kotlin
val query: TypedQuery<Member> = em.createQuery("select m From Member m where m.name = :name", Member::class.java)
query.setParameter("name", "재진")
val result: Member  = query.singleResult
```

위치 기준

```kotlin
em.createQuery("select m From Member m where m.name = ?1", Member::class.java)
query.setParameter(1, "재진")
val result: Member = query.singleResult
```

- 위치기반으로 적용되어있을 때, 중간에 파라미터가 추가되면 뒤에있는 것들도 다 밀려서 수정이 필요해지므로 이름기준으로 적용 하는 것이 좋다.

### 프로젝션

SELECT 절에 조회할 대상을 지정하는 것

- SELECT m FROM Member m → 엔티티 프로젝션
- SELECT [m.team](http://m.team) FROM Member m → 엔티티 프로젝션 (조인)
- SEELCT m.address FROM Member m → 임베디드 타입 프로젝션
- SELECT m.username, m.age FROM Member m → 스칼라 타입 프로젝션

엔티티 프로젝션 조회

```kotlin
val result: List<Member> = em.createQuery("select m from Member m", Member::class.java).resultList
val findMember: Member = result[0]
findMember.age = 35
/* update helloJPA.entity.Member */ 
update
            MEMBER 
        set
            age=?,
            name=?,
            TEAM_ID=? 
        where
            id=?
```

- 엔티티 프로젝션으로 조회한 엔티티 객체들은 모두 영속성 컨텍스트에서 관리됨

엔티티 프로젝션(조인)

```kotlin
val result: List<Team> 
= em.createQuery("select m.team from Member m", Team::class.java).resultList
Hibernate: 
    /* select
        m.team 
    from
        Member m */ select
            team1_.id as id1_1_,
            team1_.name as name2_1_ 
        from
            MEMBER member0_ 
        inner join
            TEAM team1_ 
                on member0_.TEAM_ID=team1_.id
Hibernate: 
    select
        members0_.TEAM_ID as TEAM_ID4_0_0_,
        members0_.id as id1_0_0_,
        members0_.id as id1_0_1_,
        members0_.age as age2_0_1_,
        members0_.name as name3_0_1_,
        members0_.TEAM_ID as TEAM_ID4_0_1_ 
    from
        MEMBER members0_ 
    where
        members0_.TEAM_ID=?
```

- 묵시적 join
- join이 일어나는 상황을 한눈에 파악하기 힘듦
- 성능 튜닝이 까다로워서 실무에서는 사용을 자제해야함

```kotlin
val result: List<Team> 
= em.createQuery("select t from Member m join m.team t", Team::class.java).resultList
```

- 명시적 join

## 페이징

페이징을 두 API로 추상화

- setFirstResult : 조회 시작 위치 (0부터 시작)
- setMaxResults : 조회할 데이터 수

```kotlin
val result: List<Member> = em.createQuery("select m from Member m ", Member::class.java)
            .setFirstResult(1)
            .setMaxResults(10)
            .resultList
Hibernate: 
    /* select
        m 
    from
        Member m  */ select
            member0_.id as id1_0_,
            member0_.age as age2_0_,
            member0_.name as name3_0_,
            member0_.TEAM_ID as TEAM_ID4_0_ 
        from
            MEMBER member0_ limit ? offset ?   //h2DB 방언 형식
```

## 조인

내부조인

- SELECT m FROM Member m [INNER] JOIN [m.team](http://m.team) t

```kotlin
val result: List<Member> 
        = em.createQuery("select m from Member m inner join m.team t", Member::class.java).resultList
Hibernate: 
    /* select
        m 
    from
        Member m 
    inner join
        m.team t */ select
            member0_.id as id1_0_,
            member0_.age as age2_0_,
            member0_.name as name3_0_,
            member0_.TEAM_ID as TEAM_ID4_0_ 
        from
            MEMBER member0_ 
        inner join
            TEAM team1_ 
                on member0_.TEAM_ID=team1_.id
```

외부조인

- SELECT m FROM Member m LEFT [OUTER] JOIN [m.team](http://m.team) t

```kotlin
val result: List<Member>
        = em.createQuery("select m from Member m left outer join m.team t", Member::class.java).resultList
Hibernate: 
    /* select
        m 
    from
        Member m 
    left outer join
        m.team t */ select
            member0_.id as id1_0_,
            member0_.age as age2_0_,
            member0_.name as name3_0_,
            member0_.TEAM_ID as TEAM_ID4_0_ 
        from
            MEMBER member0_ 
        left outer join
            TEAM team1_ 
                on member0_.TEAM_ID=team1_.id
```

세타조인

- select count(m) from Member m, Team t where m.username = [t.name](http://t.name)

```kotlin
val result: List<Member>
        = em.createQuery("select m from Member m, Team t where m.name = t.name").resultList as List<Member>
Hibernate: 
    /* select
        m 
    from
        Member m,
        Team t 
    where
        m.name = t.name */ select
            member0_.id as id1_0_,
            member0_.age as age2_0_,
            member0_.name as name3_0_,
            member0_.TEAM_ID as TEAM_ID4_0_ 
        from
            MEMBER member0_ cross 
        join
            TEAM team1_ 
        where
            member0_.name=team1_.name
```

### JPA 서브쿼리 한계

- JPA 에서는 표준 스펙으로 WHERE, HAVING 절에서만 사용 가능
- 하이버네이트에서 SELECT 절도 지원
- FROM 절에서는 JPQL 에서 사용 불가능.
  - 네이티브 SQL 사용
  - application 단에서 필터링
  - 쿼리를 분해해서 각각 날리기
  - 조인으로 해결할 수 있으면 조인 사용

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e3e864c0-31a4-44ca-af5b-679545e4ec0a/Untitled.png)