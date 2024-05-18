# 1. JDBC 이해

## JDBC 등장 이유

### 애플리케이션 서버와 DB - 일반적인 사용법

![일반적인 사용법](https://github.com/taechacode/SpringDB1Repository/assets/63395751/dbd03b51-027e-4311-93c3-2149904ded60)

1. 커넥션 연결 : 주로 TCP/IP를 사용해서 커넥션을 연결한다.
2. SQL 전달 : 애플리케이션 서버는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달한다.
3. 결과 응답 : DB는 전달된 SQL을 수행하고 그 결과를 응답한다. 애플리케이션 서버는 응답 결과를 활용한다.
<br/><br/>

### 애플리케이션 서버와 DB - DB 변경

![DB 변경](https://github.com/taechacode/SpringDB1Repository/assets/63395751/3c6bfdd8-8668-490f-9e69-e0037295bb03)

- 각각의 데이터베이스마다 커넥션을 연결하는 방법, SQL을 전달하는 방법, 그리고 결과를 응답받는 방법이 모두 다르다.
- 여기에는 다음과 같은 2가지 문제가 있다.
  - 데이터베이스를 다른 종류의 데이터베이스로 변경하려면 애플리케이션 서버에 개발된 데이터베이스 사용 코드도 함께 변경해야 한다.
  - 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 결과를 응답받는 방법을 새로 학습해야 한다.
<br/><br/>

### JDBC 표준 인터페이스

```
JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API다.
JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다. - 위키백과
```

![JDBC 표준 인터페이스](https://github.com/taechacode/SpringDB1Repository/assets/63395751/680e8caf-e4f3-44f9-824e-45999e1e6ad5)

- 대표적으로 다음 3가지 기능을 표준 인터페이스로 정의해서 제공한다.
  - java.sql.Connection - 연결
  - java.sql.Statement - SQL을 담은 내용
  - java.sql.ResultSet - SQL 요청 응답
- JDBC 인터페이스를 각각의 DB 벤더(회사)에서 자신의 DB에 맞도록 구현해서 라이브러리로 제공하는데, 이것을 `JDBC 드라이버`라고 한다. 예를 들어서 MySQL DB에 접근할 수 있는 것을 MySQL JDBC 드라이버라 하고, Oracle DB에 접근할 수 있는 것은 Oracle JDBC 드라이버라 한다.
<br/><br/>

## JDBC와 최신 데이터 접근 기술

### JDBC 직접 사용

![JDBC 직접 사용](https://github.com/taechacode/SpringDB1Repository/assets/63395751/2a38b88a-7876-498d-8cb9-8f751c20f3b1)
<br/><br/>

### SQL Mapper

![SQL Mapper](https://github.com/taechacode/SpringDB1Repository/assets/63395751/97d76414-f709-4c38-b75a-d7778e5f1ae2)

- SQL Mapper
  - 장점
    - JDBC를 편리하게 사용하도록 도와준다.
    - SQL 응답 결과를 객체로 편리하게 변환해준다.
    - JDBC의 반복 코드를 제거해준다.
  - 단점
    - 개발자가 SQL을 직접 작성해야 한다.
  - 대표 기술 : 스프링 JdbcTemplate, MyBatis
<br/><br/>

### ORM 기술

![ORM 기술](https://github.com/taechacode/SpringDB1Repository/assets/63395751/05ed3bc9-0e2d-4194-8540-ba13b5bf4214)

- ORM은 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술이다. 개발자는 반복적인 SLQ을 직접 작성하지 않고, ORM 기술이 개발자 대신에 SQL을 동적으로 만들어 실행해준다. 추가로 각각의 데이터베이스마다 다른 SQL을 사용하는 문제도 중간에서 해결해준다.
- 대표 기술 : JPA, 하이버네이트, 이클립스링크
<br/><br/>

## JDBC DriverManager 연결 이해

### JDBC 커넥션 인터페이스와 구현

![JDBC 커넥션 인터페이스와 구현](https://github.com/taechacode/SpringDB1Repository/assets/63395751/97701011-2085-4ca2-bd6c-62004b2c86f1)

- JDBC는 java.sql.Connection 표준 커넥션 인터페이스를 정의한다.
- H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현한 org.h2.jdbc.JdbcConnection 구현체를 제공한다.
<br/><br/>

### DriverManager 커넥션 요청 흐름

![DriverManager 커넥션 요청 흐름](https://github.com/taechacode/SpringDB1Repository/assets/63395751/0ba20475-7d4b-4605-9e17-311f7b14c226)

- JDBC가 제공하는 DriverManager는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.

1. 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()`을 호출한다.
2. DriverManager는 라이브러리에 등록된 드라이버 목록을 자동으로 인식한다. 이 드라이버들에게 순서대로 다음 정보를 넘겨서 커넥션을 획득할 수 있는지 확인한다.
  - URL: 예) `jdbc:h2:tcp://localhost/~/test`
  - 이름, 비밀번호 등 접속에 필요한 추가 정보
  - 여기서 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다. 예를 들어서 URL이 `jdbc:h2`로 시작하면 이것은 h2 데이터베이스에 접근하기 위한 규칙이다. 따라서 H2 드라이버는 본인이 처리할 수 있으므로 실제 데이터베이스에 연결해서 커넥션을 획득하고 이 커넥션을 클라이언트에 반환한다. 반면에 URL이 `jdbc:h2`로 시작했는데 MySQL 드라이버가 먼저 실행되면 이 경우 본인이 처리할 수 없다는 결과를 반환하게 되고, 다음 드라이버에게 순서가 넘어간다.
3. 이렇게 찾은 커넥션 구현체가 클라이언트에 반환된다.
<br/><br/>

## Statement와 Prepared Statement 차이점

### Statement와 Prepared Statement
- SQL문을 실행할 수 있는 객체
- 가장 큰 차이점은 캐시 사용 여부
<br/><br/>

### SQL 실행 단계
1. 쿼리 문장 분석
2. 컴파일
3. 실행
<br/><br/>

### Statement
```
String sqlstr = "SELECT name, memo FROM TABLE WHERE name =" + num
Statement stmt = conn.createStatement();
ResultSet rst = stmt.executeQuery(sqlstr);
```
- 쿼리문을 수행할 때마다 SQL 실행단계 1~3 단계를 거침
- SQL문을 수행하는 과정에서 **매번 컴파일을 하기 때문에 성능상 이슈 발생**
- 실행되는 SQL문을 확인 가능
<br/><br/>

### Prepared Statement
```
String sqlstr = "SELECT name, memo FROM TABLE WHERE num = ?"
PreparedStatement stmt = conn.preparedStatement();
stmt.setInt(1, num);
ResultSet rst = stmt.executeQuery(sqlstr);
```
- **컴파일이 미리 되어있기 때문에 Statement에 비해 좋은 성능**
- **특수문자를 자동으로 파싱**해주기 때문에 **SQL injection 같은 공격을 막을 수 있음** (사용자 입력 시 특수문자 검사를 통해 허용되지 않은 문자열이나 문자가 포함된 경우에는 에러로 처리)
- "?" 부분에만 변화를 주어 쿼리문을 수행하므로 실행되는 SQL문을 파악하기 어려움 (런타임 시 어떤 SQL문이 실행될지)
<br/><br/>

### Prepared Statement를 사용해야 하는 경우
1. 사용자 입력값으로 쿼리문을 실행하는 경우
  - 특수 기호가 들어오더라도 알아서 파싱해주므로 이로 인한 에러를 막을 수 있음
2. 쿼리 반복 수행 작업일 경우
<br/><br/>

### Statement와 Prepared Statement의 아주 큰 차이는 바로 캐시 사용여부이다
- Statement를 사용하면 매번 쿼리를 수행할 때마다 계속적으로 단계를 거치면서 수행하지만 Prepared Statement는 처음 한 번만 1~3 단계를 거친 후 캐시에 담아 재사용을 한다.
- 만약 동일한 쿼리를 반복적으로 수행한다면 Prepared Statement가 DB에 훨씬 적은 부하를 주며, 성능도 좋다.
<br/><br/>

## executeUpdate(), executeQuery(), ResultSet

### executeUpdate()
```
int executeUpdate() throws SQLException;
```
- `executeUpdate()`은 `int`를 반환하는데 영향받은 DB row 수를 반환한다. 만약 하나의 row를 INSERT하면 1을 반환한다.
<br/><br/>

### executeQuery()
```
ResultSet executeQuery() throws SQLException;
```
- 데이터를 조회할 때는 `executeQuery()`를 사용한다. `executeQuery()`는 결과를 `ResultSet`에 담아서 반환한다.
<br/><br/>

### ResultSet
- `ResultSet`은 다음과 같이 생긴 데이터 구조다. 보통 SELECT 쿼리의 결과가 순서대로 들어간다.
  - 예를 들어서 `SELECT MEMBER_ID, MONEY`라고 지정하면 `MEMBER_ID`, `MONEY`라는 이름으로 데이터가 저장된다.
  - 참고로 `SELECT *`를 사용하면 테이블의 모든 컬럼을 다 지정한다. 
- `ResultSet` 내부에 있는 커서(`cursor`)를 이동해서 다음 데이터를 조회할 수 있다.
- `rs.next()`: 이것을 호출하면 커서가 다음으로 이동한다. 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 `rs.next()`를 최초 한번은 호출해야 데이터를 조회할 수 있다.
  - `rs.next()`의 결과가 `true`면 커서의 이동 결과 데이터가 있다는 뜻이다.
  - `rs.next()`의 결과가 `false`면 더이상 커서가 가리키는 데이터가 없다는 뜻이다.
- `rs.getString("MEMBER_ID")`: 현재 커서가 가리키고 있는 위치의 `MEMBER_ID` 데이터를 `String` 타입으로 반환한다.
- `rs.getInt("MONEY")`: 현재 커서가 가리키고 있는 위치의 `MONEY` 데이터를 `int` 타입으로 반환한다.
<br/><br/>
