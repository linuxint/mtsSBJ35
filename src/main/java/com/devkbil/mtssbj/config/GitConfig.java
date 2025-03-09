package com.devkbil.mtssbj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Git 및 GitHub 설정에 대한 구성 속성 클래스.
 * <p>
 * 아래는 application.properties에 필요한 설정 값들입니다:
 * ```properties
 * # GitHub 설정
 * github.token=your_github_token
 * github.username=your_github_username
 * github.repository=owner/repository
 * <p>
 * # Git 사용자 설정
 * git.username=your_git_username
 * git.email=your_git_email
 * ```
 */
@Configuration
@ConfigurationProperties(prefix = "")
public class GitConfig {
    @NestedConfigurationProperty
    private GitHub github = new GitHub(); // GitHub 관련 설정을 포함하는 클래스
    @NestedConfigurationProperty
    private Git git = new Git(); // Git 사용자 관련 설정을 포함하는 클래스

    public static class GitHub {
        private String token; // GitHub 토큰
        private String username; // GitHub 사용자 이름
        private String repository; // GitHub 저장소 정보 (소유자/저장소 형식)

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRepository() {
            return repository;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        // GitHub 리포지토리 URL 생성
        public String getRepositoryUrl() {
            return "https://github.com/" + repository;
        }
    }

    public static class Git {
        private String username; // Git 사용자 이름
        private String email; // Git 사용자 이메일

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public GitHub getGithub() {
        return github;
    }

    public void setGithub(GitHub github) {
        this.github = github;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }
}