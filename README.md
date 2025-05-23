
# LV1~2 일정 관리 시스템 

## 📌 API 명세서

### 📅 일정

| URL               | Method | Query Params                        | Request Body                                                                 | Response                                                                                                                                  | Description                            |
|-------------------|--------|-------------------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| `/schedules`      | POST   | -                                   | `{ "title": "회의", "task": "기획 회의", "author": "홍길동", "password": "1234" }` | `{ "id": 1, "title": "회의", "task": "기획 회의", "author": "홍길동", "createdAt": "2025-05-22T14:00:00", "updatedAt": "2025-05-22T14:00:00" }` | 일정을 생성함.                    |
| `/schedules`      | GET    | `author` (선택), `updatedDate` (선택) | -                                                                             | `[ { "id": 1, "title": "회의", "task": "기획 회의", "author": "홍길동", "createdAt": "...", "updatedAt": "..." } ]`                       | 전체 일정 목록을 조회하거나 필터링함. |
| `/schedules/{id}` | GET    | -                                   | -                                                                             | `{ "id": 1, "title": "회의", "task": "기획 회의", "author": "홍길동", "createdAt": "...", "updatedAt": "..." }`                           | 특정 ID의 일정을 조회함.           |
| `/schedules/{id}` | PUT    | -                                   | `{ "title": "회의", "task": "변경된 회의", "author": "홍길동", "password": "1234" }` | `{ "id": 1, "title": "회의", "task": "변경된 회의", "author": "홍길동", "updatedAt": "..." }`                                             | 일정을 수정함. 비밀번호 필요         |
| `/schedules/{id}` | DELETE | `password=1234`                     | -                                                                             | `200 OK`                                                                                                                                | 특정 ID의 일정을 삭제함. 비밀번호 필요 |

## 🛠️ ERD 그리기 && SQL 테이블 생성문

![image](https://github.com/user-attachments/assets/807f9c25-e5d4-4def-9548-305b63440973)

![image](https://github.com/user-attachments/assets/ca68e66f-9fb1-4611-a8bb-bc9e1a043f86)


```sql
CREATE TABLE IF NOT EXISTS scheduled.schedule (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    task        VARCHAR(255)                 NULL,
    author      VARCHAR(255)                 NOT NULL,
    password    VARCHAR(255)                 NOT NULL,
    created_at  DATETIME                     NULL,
    updated_at  DATETIME                     NULL,
    title       VARCHAR(255) DEFAULT '제목 없음' NOT NULL
);
```
# Lv3~6 일정 관리 시스템

### Q) Lv1~2와의 차이는?

### A)

기존 Lv1~2 시스템에서는 `schedule` 테이블 하나로 모든 정보를 관리했는데, 할 일 내용(`task`), 작성자(`author`), 비밀번호(`password`), 생성 시각(`created_at`), 수정 시각(`updated_at`), 제목(`title`)까지 전부 다뤘다.

**문제점:**

* 작성자 이름이 같을 경우 누가 어떤 할 일을 작성했는지 명확히 알 수 없었다.

**개선 사항 (Lv3~6):**

`user`와 `schedule` 테이블로 분리하여 사용자와 할 일을 각각 관리하도록 변경했다.

* **`user` 테이블:** 사용자 정보 (이름, 이메일 등)를 관리
* **`schedule` 테이블:** 할 일 내용, 제목, 작성자 (user 테이블의 ID를 외래 키로 참조), 생성 시각, 수정 시각 등을 관리

**목표:**

이를 통해 각 할 일이 어떤 사용자에 의해 작성되었는지 명확하게 연결하고, 데이터의 정확성과 관리 효율성을 높일 수 있다.

## 📌 API 명세서

#### 📅 일정 관련 API

| URL               | Method | Query Params                    | Request Body                                                                                                                    | Response Example                                                                                                     | Description                       | 응답 코드                                | 에러 응답 코드 및 메시지                                                                                                                                                                                                             |
| ----------------- | ------ | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- | --------------------------------- | ------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/schedules`      | POST   | -                               | `json { "title": "고웹프공부", "task": "하기시러요...", "auth": { "name": "giyoung", "email": "gi10@naver.com", "password": "12345" } } ` | `json { "id": 1, "title": "고웹프공부", "task": "하기시러요...", "name": "giyoung", "createdAt": "...", "updatedAt": "..." } ` | 일정을 생성함. 사용자 인증 정보 포함             | 201 Created                          | - 400 BAD\_REQUEST : AUTH\_INFO\_REQUIRED, INVALID\_REQUEST\_DATA<br>- 401 UNAUTHORIZED : 권한이 없습니다.<br>- 409 CONFLICT : DUPLICATE\_SCHEDULE                                                                                |
| `/schedules`      | GET    | `user` (선택), `updatedDate` (선택) | -                                                                                                                               | `json [ { "id": 1, "title": "회의", "task": "기획 회의", "name": "홍길동", "createdAt": "...", "updatedAt": "..." } ] `       | 전체 일정 목록을 조회하거나 필터링함.             | 200 OK                               | 500 INTERNAL\_SERVER\_ERROR : 서버 오류 발생                                                                                                                                                                                     |
| `/schedules/{id}` | GET    | -                               | -                                                                                                                               | `json { "id": 1, "title": "회의", "task": "기획 회의", "name": "홍길동", "createdAt": "...", "updatedAt": "..." } `           | 특정 ID의 일정을 조회함.                   | 200 OK                               | 404 NOT\_FOUND : SCHEDULE\_NOT\_FOUND                                                                                                                                                                                      |
| `/schedules/{id}` | PUT    | -                               | `json { "auth": { "name": "dgh111", "email": "dgh01@naver.com", "password": "123456" }, "schedule": { "title": "새 작업 내용" } } `  | `json { "id": 1, "title": "새 작업 내용", "task": "(기존 작업 내용 유지)", "name": "dgh111", "updatedAt": "..." } `               | 특정 ID의 일정을 수정함. 사용자 인증 및 수정 내용 포함 | 200 OK                               | - 400 BAD\_REQUEST : AUTH\_INFO\_REQUIRED, NAME\_MISMATCH, EMAIL\_MISMATCH, INVALID\_PASSWORD, INVALID\_REQUEST\_DATA, NO\_FIELDS\_TO\_UPDATE<br>- 401 UNAUTHORIZED : 권한이 없습니다.<br>- 404 NOT\_FOUND : SCHEDULE\_NOT\_FOUND |
| `/schedules/{id}` | DELETE | -                               | `json {  "name": "dgh111", "email": "dgh01@naver.com", "password": "123456" }   `                                               | `200 OK`                                                                                                             | 특정 ID의 일정을 삭제함. 사용자의 모든 정보 필요     | 200 OK, 204 No Content (성공 시 응답에 따라) | - 400 BAD\_REQUEST : AUTH\_INFO\_REQUIRED, NAME\_MISMATCH, EMAIL\_MISMATCH, INVALID\_PASSWORD<br>- 401 UNAUTHORIZED : 권한이 없습니다.<br>- 404 NOT\_FOUND : SCHEDULE\_NOT\_FOUND                                                 |


#### 🔐 사용자 관련 API

| URL           | Method | Query Params | Request Body                                                                                                                                              | Response Example                                                                          | Description | 응답 코드                                | 에러 응답 코드 및 메시지                                                                                                                                                                                             |
| ------------- | ------ | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- | ----------- | ------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/users`      | POST   | -            | `json { "name": "giyoung", "email": "gi10@naver.com", "password": "12345" } `                                                                             | `json { "id": 1, "name": "giyoung", "email": "gi10@naver.com"  ,"createdAt":"...",...} `  | 사용자 생성      | 201 Created                          | - 400 BAD\_REQUEST : INVALID\_REQUEST\_DATA, DUPLICATE\_USER<br>- 500 INTERNAL\_SERVER\_ERROR : 서버 오류 발생                                                                                                   |
| `/users/{id}` | GET    | -            | -                                                                                                                                                         | `json { "id": 1, "name": "홍길동", "email": "hong@example.com" ,"createdAt":"...",... } `    | ID로 사용자 조회  | 200 OK                               | - 404 NOT\_FOUND : USER\_NOT\_FOUND<br>- 500 INTERNAL\_SERVER\_ERROR                                                                                                                                       |
| `/users`      | GET    | -            | -                                                                                                                                                         | `json [ { "id": 1, "name": "홍길동", "email": "hong@example.com" ,"createdAt":"...",...} ] ` | 전체 사용자 목록   | 200 OK                               | - 500 INTERNAL\_SERVER\_ERROR                                                                                                                                                                              |
| `/users/{id}` | PUT    | -            | `json { "auth": { "name": "dgh111", "email": "dgh01@naver.com", "password": "123456" }, "name": "김철수", "email": "kim@example.com", "password": "5678" } ` | `json { "id": 1, "name": "김철수", "email": "kim@example.com" ,...} `                        | 사용자 정보 수정   | 200 OK                               | - 400 BAD\_REQUEST : AUTH\_INFO\_REQUIRED, NAME\_MISMATCH, EMAIL\_MISMATCH, INVALID\_PASSWORD, INVALID\_REQUEST\_DATA, NO\_FIELDS\_TO\_UPDATE<br>- 401 UNAUTHORIZED<br>- 404 NOT\_FOUND : USER\_NOT\_FOUND |
| `/users/{id}` | DELETE | -            | `json { "name":"dgh","email":"dgh01@naver.com","password": "1234" } `                                                                                     | `"사용자 삭제 완료"`                                                                             | 사용자 삭제      | 200 OK, 204 No Content (성공 시 응답에 따라) | - 400 BAD\_REQUEST : AUTH\_INFO\_REQUIRED, NAME\_MISMATCH, EMAIL\_MISMATCH, INVALID\_PASSWORD<br>- 401 UNAUTHORIZED<br>- 404 NOT\_FOUND : USER\_NOT\_FOUND                                                 |


### 인증 (Authentication)

* 일정 생성 (`/schedules` POST) 시 요청 바디의 `auth` 객체를 통해 사용자 인증 정보를 전달함.
* 일정 수정 (`/schedules/{id}` PUT) 시 요청 바디의 `auth` 객체를 통해 사용자 인증 정보를 전달함.
* 사용자 삭제 (`/users/{id}` DELETE) 시 요청 바디에 `password`를 포함하여 인증함.

## 🛠️ ERD 그리기 && SQL 테이블 생성문
![image](https://github.com/user-attachments/assets/558a7c7d-5e67-4bcd-81a2-20f6022fcd72)

```sql
create table if not exists schedule.users
(
    id         bigint auto_increment
        primary key,
    name       varchar(255)                       not null,
    email      varchar(255)                       not null,
    password   varchar(255)                       not null,
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint unique_email
        unique (email)
);

create table if not exists schedule.schedule
(
    id         bigint auto_increment
        primary key,
    title      varchar(100)                       null,
    task       varchar(255)                       not null,
    user_id    bigint                             not null,
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_schedule_user
        foreign key (user_id) references schedule.users (id)
);
```





