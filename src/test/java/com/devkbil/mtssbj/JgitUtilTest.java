package com.devkbil.mtssbj;

import com.devkbil.common.util.JgitUtil;
import com.devkbil.mtssbj.config.GitConfig;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Files;

@ExtendWith(SpringExtension.class)
@Import(JgitUtilTest.TestConfig.class)
public class JgitUtilTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public GitConfig gitConfig() {
            GitConfig config = new GitConfig();
            GitConfig.GitHub github = new GitConfig.GitHub();
            github.setToken("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");
            github.setUsername("linuxint@gmail.com");
            github.setRepository("linuxint/mtsSBJ35");

            GitConfig.Git git = new GitConfig.Git();
            git.setUsername("linuxint@gmail.com");
            git.setEmail("linuxint@gmail.com");

            config.setGithub(github);
            config.setGit(git);
            return config;
        }

        @Bean
        public JgitUtil jgitUtil(GitConfig gitConfig) {
            return new JgitUtil(gitConfig);
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(JgitUtilTest.class);

    @Autowired
    private JgitUtil jgitUtil;

    @Autowired
    private GitConfig gitConfig;

    @BeforeEach
    void setUp() {
        logger.info("[DEBUG_LOG] Starting Git test");
    }

    @Test
    public void testGitOperations() throws Exception {
        logger.info("[DEBUG_LOG] Starting Git operations test");

        // Create a temporary directory for testing
        File dir = Files.createTempDirectory("git-test").toFile();
        logger.info("[DEBUG_LOG] Created temporary directory: {}", dir.getAbsolutePath());

        Git git = null;
        try {
            // Clone and checkout repository
            logger.info("[DEBUG_LOG] Cloning repository");
            jgitUtil.checkOut(dir);

            // Open repository
            logger.info("[DEBUG_LOG] Opening Git repository");
            git = jgitUtil.open(dir);

            // List remote branches
            logger.info("[DEBUG_LOG] Listing remote branches");
            jgitUtil.lsRemote(git);

            // Create test file
            File testFile = new File(dir, "test.txt");
            Files.writeString(testFile.toPath(), "Test content");
            logger.info("[DEBUG_LOG] Created test file: {}", testFile.getAbsolutePath());

            // Add and commit changes
            logger.info("[DEBUG_LOG] Adding test file");
            jgitUtil.add(git, "test.txt");

            logger.info("[DEBUG_LOG] Committing changes");
            jgitUtil.commit(git, "Test commit: Added test.txt");

            logger.info("[DEBUG_LOG] Pushing changes");
            jgitUtil.push(git);

            logger.info("[DEBUG_LOG] Git operations completed successfully");
        } catch (Exception e) {
            logger.error("[DEBUG_LOG] Error during Git operations: {}", e.getMessage(), e);
            throw e;
        } finally {
            // Close Git repository if open
            if (git != null) {
                logger.info("[DEBUG_LOG] Closing Git repository");
                git.close();
            }

            // Cleanup: Delete the temporary directory
            logger.info("[DEBUG_LOG] Cleaning up temporary directory");
            deleteDirectory(dir);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}