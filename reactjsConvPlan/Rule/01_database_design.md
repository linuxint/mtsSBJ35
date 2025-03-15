# Database Design for React.js Migration

## 1. Core Tables

### 1.1 User Management

#### AUTH_USER
```sql
CREATE TABLE AUTH_USER (
    user_id         BIGINT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)      NOT NULL,
    email          VARCHAR(100)     NOT NULL,
    password       VARCHAR(200)     NOT NULL,
    full_name      VARCHAR(100)     NOT NULL,
    department_id  BIGINT,
    position_code  VARCHAR(20),
    role_type      VARCHAR(20)      NOT NULL,
    profile_image  VARCHAR(200),
    is_active      BOOLEAN         DEFAULT true,
    last_login_at  TIMESTAMP,
    created_at     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
);
```

#### AUTH_DEPARTMENT
```sql
CREATE TABLE AUTH_DEPARTMENT (
    department_id   BIGINT         NOT NULL AUTO_INCREMENT,
    parent_id      BIGINT,
    name           VARCHAR(100)    NOT NULL,
    description    VARCHAR(500),
    depth          INT            DEFAULT 0,
    sort_order     INT            DEFAULT 0,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (department_id),
    FOREIGN KEY (parent_id) REFERENCES AUTH_DEPARTMENT(department_id)
);
```

### 1.2 Common Code Management

#### COMMON_CODE_GROUP
```sql
CREATE TABLE COMMON_CODE_GROUP (
    group_id       VARCHAR(20)     NOT NULL,
    name           VARCHAR(100)    NOT NULL,
    description    VARCHAR(500),
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (group_id)
);
```

#### COMMON_CODE
```sql
CREATE TABLE COMMON_CODE (
    code_id        VARCHAR(20)     NOT NULL,
    group_id       VARCHAR(20)     NOT NULL,
    parent_code_id VARCHAR(20),
    name           VARCHAR(100)    NOT NULL,
    description    VARCHAR(500),
    sort_order     INT            DEFAULT 0,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (code_id),
    FOREIGN KEY (group_id) REFERENCES COMMON_CODE_GROUP(group_id),
    FOREIGN KEY (parent_code_id) REFERENCES COMMON_CODE(code_id)
);
```

### 1.3 Menu Management

#### MENU
```sql
CREATE TABLE MENU (
    menu_id        BIGINT         NOT NULL AUTO_INCREMENT,
    parent_id      BIGINT,
    name           VARCHAR(100)    NOT NULL,
    description    VARCHAR(500),
    url_path       VARCHAR(200),
    icon           VARCHAR(100),
    component      VARCHAR(100),
    permission     VARCHAR(100),
    sort_order     INT            DEFAULT 0,
    is_visible     BOOLEAN        DEFAULT true,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (menu_id),
    FOREIGN KEY (parent_id) REFERENCES MENU(menu_id)
);
```

## 2. Board Management

### 2.1 Board Configuration

#### BOARD_MASTER
```sql
CREATE TABLE BOARD_MASTER (
    board_id       BIGINT         NOT NULL AUTO_INCREMENT,
    name           VARCHAR(100)    NOT NULL,
    description    VARCHAR(500),
    board_type     VARCHAR(20)     NOT NULL,
    read_permission VARCHAR(20)    NOT NULL,
    write_permission VARCHAR(20)   NOT NULL,
    use_comment    BOOLEAN        DEFAULT true,
    use_attachment BOOLEAN        DEFAULT true,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (board_id)
);
```

### 2.2 Board Content

#### BOARD_POST
```sql
CREATE TABLE BOARD_POST (
    post_id        BIGINT         NOT NULL AUTO_INCREMENT,
    board_id       BIGINT         NOT NULL,
    title          VARCHAR(200)    NOT NULL,
    content        TEXT           NOT NULL,
    view_count     INT            DEFAULT 0,
    is_notice      BOOLEAN        DEFAULT false,
    is_deleted     BOOLEAN        DEFAULT false,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (post_id),
    FOREIGN KEY (board_id) REFERENCES BOARD_MASTER(board_id),
    FOREIGN KEY (created_by) REFERENCES AUTH_USER(user_id),
    FOREIGN KEY (updated_by) REFERENCES AUTH_USER(user_id)
);
```

#### BOARD_COMMENT
```sql
CREATE TABLE BOARD_COMMENT (
    comment_id     BIGINT         NOT NULL AUTO_INCREMENT,
    post_id        BIGINT         NOT NULL,
    parent_id      BIGINT,
    content        TEXT           NOT NULL,
    is_deleted     BOOLEAN        DEFAULT false,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (comment_id),
    FOREIGN KEY (post_id) REFERENCES BOARD_POST(post_id),
    FOREIGN KEY (parent_id) REFERENCES BOARD_COMMENT(comment_id),
    FOREIGN KEY (created_by) REFERENCES AUTH_USER(user_id),
    FOREIGN KEY (updated_by) REFERENCES AUTH_USER(user_id)
);
```

### 2.3 File Management

#### FILE_INFO
```sql
CREATE TABLE FILE_INFO (
    file_id        BIGINT         NOT NULL AUTO_INCREMENT,
    original_name  VARCHAR(500)    NOT NULL,
    saved_name     VARCHAR(100)    NOT NULL,
    file_path      VARCHAR(500)    NOT NULL,
    file_size      BIGINT         NOT NULL,
    mime_type      VARCHAR(100)    NOT NULL,
    target_type    VARCHAR(20)     NOT NULL,
    target_id      BIGINT         NOT NULL,
    is_deleted     BOOLEAN        DEFAULT false,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    PRIMARY KEY (file_id),
    FOREIGN KEY (created_by) REFERENCES AUTH_USER(user_id)
);
```

## 3. Project Management

### 3.1 Project Configuration

#### PROJECT
```sql
CREATE TABLE PROJECT (
    project_id     BIGINT         NOT NULL AUTO_INCREMENT,
    name           VARCHAR(200)    NOT NULL,
    description    TEXT,
    start_date     DATE           NOT NULL,
    end_date       DATE           NOT NULL,
    status         VARCHAR(20)     NOT NULL,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (project_id),
    FOREIGN KEY (created_by) REFERENCES AUTH_USER(user_id),
    FOREIGN KEY (updated_by) REFERENCES AUTH_USER(user_id)
);
```

### 3.2 Task Management

#### PROJECT_TASK
```sql
CREATE TABLE PROJECT_TASK (
    task_id        BIGINT         NOT NULL AUTO_INCREMENT,
    project_id     BIGINT         NOT NULL,
    parent_id      BIGINT,
    title          VARCHAR(200)    NOT NULL,
    description    TEXT,
    start_date     DATE,
    end_date       DATE,
    actual_end_date DATE,
    progress       INT            DEFAULT 0,
    status         VARCHAR(20)     NOT NULL,
    priority       VARCHAR(20)     NOT NULL,
    sort_order     INT            DEFAULT 0,
    is_deleted     BOOLEAN        DEFAULT false,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    PRIMARY KEY (task_id),
    FOREIGN KEY (project_id) REFERENCES PROJECT(project_id),
    FOREIGN KEY (parent_id) REFERENCES PROJECT_TASK(task_id),
    FOREIGN KEY (created_by) REFERENCES AUTH_USER(user_id),
    FOREIGN KEY (updated_by) REFERENCES AUTH_USER(user_id)
);
```

#### PROJECT_TASK_ASSIGNEE
```sql
CREATE TABLE PROJECT_TASK_ASSIGNEE (
    task_id        BIGINT         NOT NULL,
    user_id        BIGINT         NOT NULL,
    assigned_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    assigned_by    BIGINT         NOT NULL,
    PRIMARY KEY (task_id, user_id),
    FOREIGN KEY (task_id) REFERENCES PROJECT_TASK(task_id),
    FOREIGN KEY (user_id) REFERENCES AUTH_USER(user_id),
    FOREIGN KEY (assigned_by) REFERENCES AUTH_USER(user_id)
);
```

## 4. Mail System

### 4.1 Mail Configuration

#### MAIL_CONFIG
```sql
CREATE TABLE MAIL_CONFIG (
    config_id      BIGINT         NOT NULL AUTO_INCREMENT,
    user_id        BIGINT         NOT NULL,
    email_address  VARCHAR(100)    NOT NULL,
    smtp_host      VARCHAR(100)    NOT NULL,
    smtp_port      INT            NOT NULL,
    smtp_username  VARCHAR(100)    NOT NULL,
    smtp_password  VARCHAR(200)    NOT NULL,
    is_active      BOOLEAN        DEFAULT true,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (config_id),
    FOREIGN KEY (user_id) REFERENCES AUTH_USER(user_id)
);
```

### 4.2 Mail Content

#### MAIL
```sql
CREATE TABLE MAIL (
    mail_id        BIGINT         NOT NULL AUTO_INCREMENT,
    subject        VARCHAR(500)    NOT NULL,
    content        TEXT           NOT NULL,
    sender_id      BIGINT         NOT NULL,
    mail_type      VARCHAR(20)     NOT NULL,
    status         VARCHAR(20)     NOT NULL,
    sent_at        TIMESTAMP,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (mail_id),
    FOREIGN KEY (sender_id) REFERENCES AUTH_USER(user_id)
);
```

#### MAIL_RECIPIENT
```sql
CREATE TABLE MAIL_RECIPIENT (
    mail_id        BIGINT         NOT NULL,
    email_address  VARCHAR(100)    NOT NULL,
    recipient_type VARCHAR(20)     NOT NULL,
    sequence       INT            DEFAULT 0,
    PRIMARY KEY (mail_id, email_address),
    FOREIGN KEY (mail_id) REFERENCES MAIL(mail_id)
);
```

## 5. Audit & Security

### 5.1 Audit Trail

#### AUDIT_LOG
```sql
CREATE TABLE AUDIT_LOG (
    log_id         BIGINT         NOT NULL AUTO_INCREMENT,
    user_id        BIGINT,
    action_type    VARCHAR(50)     NOT NULL,
    target_type    VARCHAR(50)     NOT NULL,
    target_id      VARCHAR(100)    NOT NULL,
    old_value      TEXT,
    new_value      TEXT,
    ip_address     VARCHAR(50),
    user_agent     VARCHAR(500),
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    FOREIGN KEY (user_id) REFERENCES AUTH_USER(user_id)
);
```

### 5.2 Security

#### PERSISTENT_LOGIN
```sql
CREATE TABLE PERSISTENT_LOGIN (
    series_id      VARCHAR(64)     NOT NULL,
    user_id        BIGINT         NOT NULL,
    token          VARCHAR(64)     NOT NULL,
    last_used      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (series_id),
    FOREIGN KEY (user_id) REFERENCES AUTH_USER(user_id)
);
``` 