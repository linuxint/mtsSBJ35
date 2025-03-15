# Database Indexes Design

## 1. User Management Indexes

### 1.1 AUTH_USER
```sql
-- 기본 인덱스
CREATE INDEX idx_auth_user_username ON AUTH_USER(username);
CREATE INDEX idx_auth_user_email ON AUTH_USER(email);
CREATE INDEX idx_auth_user_department ON AUTH_USER(department_id);
CREATE INDEX idx_auth_user_role ON AUTH_USER(role_type);
CREATE INDEX idx_auth_user_status ON AUTH_USER(is_active);

-- 복합 인덱스
CREATE INDEX idx_auth_user_dept_role ON AUTH_USER(department_id, role_type);
CREATE INDEX idx_auth_user_name_email ON AUTH_USER(username, email);
```

### 1.2 AUTH_DEPARTMENT
```sql
-- 기본 인덱스
CREATE INDEX idx_auth_department_parent ON AUTH_DEPARTMENT(parent_id);
CREATE INDEX idx_auth_department_status ON AUTH_DEPARTMENT(is_active);

-- 복합 인덱스
CREATE INDEX idx_auth_department_parent_order ON AUTH_DEPARTMENT(parent_id, sort_order);
```

## 2. Common Code Indexes

### 2.1 COMMON_CODE
```sql
-- 기본 인덱스
CREATE INDEX idx_common_code_group ON COMMON_CODE(group_id);
CREATE INDEX idx_common_code_parent ON COMMON_CODE(parent_code_id);
CREATE INDEX idx_common_code_status ON COMMON_CODE(is_active);

-- 복합 인덱스
CREATE INDEX idx_common_code_group_order ON COMMON_CODE(group_id, sort_order);
CREATE INDEX idx_common_code_group_parent ON COMMON_CODE(group_id, parent_code_id);
```

## 3. Board Management Indexes

### 3.1 BOARD_POST
```sql
-- 기본 인덱스
CREATE INDEX idx_board_post_board ON BOARD_POST(board_id);
CREATE INDEX idx_board_post_creator ON BOARD_POST(created_by);
CREATE INDEX idx_board_post_status ON BOARD_POST(is_deleted);
CREATE INDEX idx_board_post_notice ON BOARD_POST(is_notice);
CREATE INDEX idx_board_post_date ON BOARD_POST(created_at);

-- 복합 인덱스
CREATE INDEX idx_board_post_board_date ON BOARD_POST(board_id, created_at);
CREATE INDEX idx_board_post_board_notice ON BOARD_POST(board_id, is_notice, created_at);
CREATE INDEX idx_board_post_board_status ON BOARD_POST(board_id, is_deleted, created_at);
```

### 3.2 BOARD_COMMENT
```sql
-- 기본 인덱스
CREATE INDEX idx_board_comment_post ON BOARD_COMMENT(post_id);
CREATE INDEX idx_board_comment_parent ON BOARD_COMMENT(parent_id);
CREATE INDEX idx_board_comment_creator ON BOARD_COMMENT(created_by);
CREATE INDEX idx_board_comment_status ON BOARD_COMMENT(is_deleted);

-- 복합 인덱스
CREATE INDEX idx_board_comment_post_date ON BOARD_COMMENT(post_id, created_at);
CREATE INDEX idx_board_comment_post_status ON BOARD_COMMENT(post_id, is_deleted);
```

### 3.3 FILE_INFO
```sql
-- 기본 인덱스
CREATE INDEX idx_file_info_target ON FILE_INFO(target_type, target_id);
CREATE INDEX idx_file_info_status ON FILE_INFO(is_deleted);
CREATE INDEX idx_file_info_creator ON FILE_INFO(created_by);

-- 복합 인덱스
CREATE INDEX idx_file_info_target_status ON FILE_INFO(target_type, target_id, is_deleted);
```

## 4. Project Management Indexes

### 4.1 PROJECT
```sql
-- 기본 인덱스
CREATE INDEX idx_project_status ON PROJECT(status);
CREATE INDEX idx_project_dates ON PROJECT(start_date, end_date);
CREATE INDEX idx_project_creator ON PROJECT(created_by);

-- 복합 인덱스
CREATE INDEX idx_project_status_date ON PROJECT(status, start_date, end_date);
```

### 4.2 PROJECT_TASK
```sql
-- 기본 인덱스
CREATE INDEX idx_project_task_project ON PROJECT_TASK(project_id);
CREATE INDEX idx_project_task_parent ON PROJECT_TASK(parent_id);
CREATE INDEX idx_project_task_status ON PROJECT_TASK(status);
CREATE INDEX idx_project_task_dates ON PROJECT_TASK(start_date, end_date);
CREATE INDEX idx_project_task_creator ON PROJECT_TASK(created_by);

-- 복합 인덱스
CREATE INDEX idx_project_task_project_status ON PROJECT_TASK(project_id, status);
CREATE INDEX idx_project_task_project_dates ON PROJECT_TASK(project_id, start_date, end_date);
CREATE INDEX idx_project_task_parent_order ON PROJECT_TASK(parent_id, sort_order);
```

### 4.3 PROJECT_TASK_ASSIGNEE
```sql
-- 기본 인덱스
CREATE INDEX idx_task_assignee_user ON PROJECT_TASK_ASSIGNEE(user_id);
CREATE INDEX idx_task_assignee_assigner ON PROJECT_TASK_ASSIGNEE(assigned_by);

-- 복합 인덱스
CREATE INDEX idx_task_assignee_task_user ON PROJECT_TASK_ASSIGNEE(task_id, user_id);
```

## 5. Mail System Indexes

### 5.1 MAIL
```sql
-- 기본 인덱스
CREATE INDEX idx_mail_sender ON MAIL(sender_id);
CREATE INDEX idx_mail_type ON MAIL(mail_type);
CREATE INDEX idx_mail_status ON MAIL(status);
CREATE INDEX idx_mail_date ON MAIL(sent_at);

-- 복합 인덱스
CREATE INDEX idx_mail_sender_type ON MAIL(sender_id, mail_type);
CREATE INDEX idx_mail_sender_status ON MAIL(sender_id, status);
CREATE INDEX idx_mail_type_date ON MAIL(mail_type, sent_at);
```

### 5.2 MAIL_RECIPIENT
```sql
-- 기본 인덱스
CREATE INDEX idx_mail_recipient_email ON MAIL_RECIPIENT(email_address);
CREATE INDEX idx_mail_recipient_type ON MAIL_RECIPIENT(recipient_type);

-- 복합 인덱스
CREATE INDEX idx_mail_recipient_mail_type ON MAIL_RECIPIENT(mail_id, recipient_type);
```

## 6. Audit & Security Indexes

### 6.1 AUDIT_LOG
```sql
-- 기본 인덱스
CREATE INDEX idx_audit_log_user ON AUDIT_LOG(user_id);
CREATE INDEX idx_audit_log_action ON AUDIT_LOG(action_type);
CREATE INDEX idx_audit_log_target ON AUDIT_LOG(target_type, target_id);
CREATE INDEX idx_audit_log_date ON AUDIT_LOG(created_at);

-- 복합 인덱스
CREATE INDEX idx_audit_log_user_action ON AUDIT_LOG(user_id, action_type);
CREATE INDEX idx_audit_log_target_date ON AUDIT_LOG(target_type, target_id, created_at);
```

### 6.2 PERSISTENT_LOGIN
```sql
-- 기본 인덱스
CREATE INDEX idx_persistent_login_user ON PERSISTENT_LOGIN(user_id);
CREATE INDEX idx_persistent_login_token ON PERSISTENT_LOGIN(token);
CREATE INDEX idx_persistent_login_date ON PERSISTENT_LOGIN(last_used);

-- 복합 인덱스
CREATE INDEX idx_persistent_login_user_date ON PERSISTENT_LOGIN(user_id, last_used);
``` 