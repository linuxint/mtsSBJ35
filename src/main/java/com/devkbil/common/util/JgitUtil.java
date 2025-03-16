package com.devkbil.common.util;

import com.devkbil.mtssbj.config.GitConfig;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JGit을 사용하여 Git 작업을 수행하는 유틸리티 클래스.
 * Git 저장소를 초기화, 관리 및 프로그래밍 방식으로 상호 작용하는 데 필요한 메서드를 제공합니다.
 * 각 메서드는 초기화, 커밋, 푸시, 풀 등 특정 Git 작업을 캡슐화합니다.
 */
public class JgitUtil {

    private final GitConfig gitConfig;
    private final String defaultBranch = "main";
    private final CredentialsProvider cp;

    public JgitUtil(GitConfig gitConfig) {
        this.gitConfig = gitConfig;
        this.cp = new UsernamePasswordCredentialsProvider(gitConfig.getGithub().getToken(), "");
    }

    /**
     * 지정된 디렉토리에 새로운 Git 저장소를 초기화합니다.
     *
     * @param dir Git 저장소를 초기화할 디렉토리
     * @return 초기화된 Git 객체
     * @throws Exception Git 저장소 초기화 중 오류가 발생한 경우
     */
    public Git init(File dir) throws Exception {
        return Git.init().setDirectory(dir).call();
    }

    /**
     * 주어진 Git 인스턴스에 원격 저장소를 추가합니다.
     * 원격 저장소는 "origin"이라는 이름과 해당 클래스의 `url` 필드에 지정된 URI로 추가됩니다.
     * 추가적인 설정은 필요에 따라 적용될 수 있습니다.
     *
     * @param git 원격 저장소가 추가될 Git 인스턴스
     * @throws Exception 원격 저장소 추가 중 오류가 발생한 경우
     */
    public void remoteAdd(Git git) throws Exception {
        // 원격 저장소 추가:
        RemoteAddCommand remoteAddCommand = git.remoteAdd();
        remoteAddCommand.setName("origin");
        remoteAddCommand.setUri(new URIish(gitConfig.getGithub().getRepositoryUrl()));
        // 필요에 따라 설정 추가 가능
        remoteAddCommand.call();
    }

    /**
     * 로컬 변경 사항을 원격 Git 저장소에 푸시합니다.
     * 자격 증명 공급자를 사용하여 푸시 작업을 구성하고 강제 푸시로 설정합니다.
     * 추가 푸시 설정은 필요에 따라 구성할 수 있습니다.
     *
     * @param git 푸시 작업을 실행할 때 사용할 Git 인스턴스
     * @throws Exception 푸시 과정에서 오류가 발생한 경우
     */
    public void push(Git git) throws Exception {
        // 원격으로 푸시:
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(cp);
        pushCommand.setForce(true);
        // 필요에 따라 설정 추가 가능
        pushCommand.call();
    }

    /**
     * 지정된 파일 또는 파일 패턴을 Git 인덱스(스테이징 영역)에 추가합니다.
     *
     * @param git 파일을 스테이징할 저장소를 나타내는 Git 인스턴스
     * @param filePattern 스테이징 영역에 추가할 파일 이름 또는 패턴
     * @throws Exception 파일을 스테이징 영역에 추가하는 동안 오류가 발생한 경우
     */
    public void add(Git git, String filePattern) throws Exception {
        git.add().addFilepattern(filePattern).call();
    }

    /**
     * Git 저장소에서 지정된 파일 또는 파일 패턴을 제거합니다.
     *
     * @param git 파일을 제거할 저장소를 나타내는 Git 인스턴스
     * @param filePattern 저장소에서 제거할 파일 이름 또는 패턴
     * @throws Exception 파일 제거 중 오류가 발생한 경우
     */
    public void rm(Git git, String filePattern) throws Exception {
        git.rm().addFilepattern(filePattern).call();
    }

    /**
     * 지정된 커밋 메시지로 Git 저장소에 커밋을 생성합니다.
     * 커밋은 해당 클래스에 이미 구성된 사용자 정보(작성자 이름 및 이메일)로 수행됩니다.
     *
     * @param git 커밋이 수행될 저장소를 나타내는 Git 인스턴스
     * @param msg 커밋에 연결할 커밋 메시지
     * @throws Exception 커밋 과정에서 오류가 발생한 경우
     */
    public void commit(Git git, String msg) throws Exception {
        // 메시지를 포함하여 커밋 수행
        git.commit()//
            .setAuthor(gitConfig.getGit().getUsername(), gitConfig.getGit().getEmail())//
            .setMessage(msg)//
            .call();
    }

    /**
     * 제공된 Git 저장소에서 풀 작업을 수행합니다.
     * 이 작업은 원격 저장소에서 변경 사항을 가져와 로컬 저장소의 현재 체크아웃된 브랜치와 병합합니다.
     *
     * @param git 풀 작업이 수행될 저장소를 나타내는 Git 인스턴스
     * @throws Exception 풀 과정에서 오류가 발생한 경우
     */
    public void pull(Git git) throws Exception {
        PullCommand pull = git.pull();
        pull.setCredentialsProvider(cp);
        pull.call();
    }

    /**
     * 제공된 Git 인스턴스를 사용하여 Git ls-remote 작업을 실행합니다.
     * 이 메서드는 원격 저장소에서 참조 컬렉션을 가져오며, 헤드를 대상으로 하고 태그는 제외합니다.
     * 가져온 각 참조의 이름과 오브젝트 ID를 출력합니다.
     *
     * @param git 원격 참조를 나열할 로컬 저장소를 나타내는 Git 인스턴스
     * @throws Exception ls-remote 작업 도중 오류가 발생한 경우
     */
    public void lsRemote(Git git) throws Exception {
        Collection<Ref> remoteRefs = git.lsRemote()
            .setCredentialsProvider(cp)
            .setRemote("origin")
            .setTags(false)
            .setHeads(true)
            .call();
        for (Ref ref : remoteRefs) {
            System.out.println(ref.getName() + " -> " + ref.getObjectId().name());
        }
    }

    /**
     * Git 저장소에서 특정 커밋 또는 브랜치를 체크아웃합니다.
     * 이 메서드는 원격 Git 저장소를 지정된 로컬 디렉토리에 클론한 후,
     * 초기에는 어떤 파일도 체크아웃하지 않고 특정 커밋이나 브랜치를 체크아웃합니다.
     *
     * @param dir 저장소가 복제되고 체크아웃될 디렉토리
     * @throws Exception 클론 또는 체크아웃 과정에서 오류가 발생한 경우
     */
    public void checkOut(File dir) throws Exception {
        Git gitRepo = null;
        try {
            // Clone repository without performing an initial checkout
            gitRepo = Git.cloneRepository()
                .setURI(gitConfig.getGithub().getRepositoryUrl()) // Remote repository URL
                .setDirectory(dir) // Local directory
                .setNoCheckout(true) // Do not checkout files yet
                .setCredentialsProvider(cp) // Credentials provider
                .call();

            // Check if the branch exists locally
            boolean branchExistsLocally = branchExistsLocally(gitRepo, defaultBranch);

            if (branchExistsLocally) {
                // Branch exists locally, just check it out without creating it
                gitRepo.checkout().setName(defaultBranch).call();
            } else {
                // Branch does not exist locally, check if it exists remotely
                boolean branchExistsRemotely = branchExistsRemotely(gitRepo, "origin/" + defaultBranch);

                if (branchExistsRemotely) {
                    // If the branch exists remotely, create it locally and set its start point
                    gitRepo.checkout()
                        .setCreateBranch(true)
                        .setName(defaultBranch)
                        .setStartPoint("origin/" + defaultBranch)
                        .call();
                } else {
                    throw new RefNotFoundException("Branch '" + defaultBranch + "' not found locally or in the remote repository.");
                }
            }

        } catch (GitAPIException e) {
            throw new Exception("Error during checkout: " + e.getMessage(), e);
        } finally {
            if (gitRepo != null) {
                gitRepo.getRepository().close();
            }
        }
    }

    /**
     * Check if a branch exists locally.
     *
     * @param git the Git instance
     * @param branchName the name of the branch to check (e.g., "main")
     * @return true if the branch exists locally, false otherwise
     * @throws GitAPIException if an error occurs while listing branches
     */
    private boolean branchExistsLocally(Git git, String branchName) throws GitAPIException {
        List<Ref> localBranches = git.branchList().call();
        for (Ref branch : localBranches) {
            if (branch.getName().equals("refs/heads/" + branchName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Check if a branch exists remotely.
     *
     * @param git the Git instance
     * @param remoteBranchName the name of the remote branch to check (e.g., "origin/main")
     * @return true if the remote branch exists, false otherwise
     * @throws GitAPIException if an error occurs while listing remote branches
     */
    private boolean branchExistsRemotely(Git git, String remoteBranchName) throws GitAPIException {
        List<Ref> remoteBranches = new ArrayList<>(git.lsRemote().setHeads(true).call()); // List heads only (not tags)
        for (Ref branch : remoteBranches) {
            if (branch.getName().equals("refs/remotes/" + remoteBranchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 지정된 디렉토리에 있는 Git 저장소를 엽니다.
     * 저장소가 없으면 해당 디렉토리에서 새 Git 저장소를 초기화합니다.
     *
     * @param dir Git 저장소가 위치하거나 초기화될 디렉토리
     * @return 열리거나 새로 초기화된 Git 저장소를 나타내는 Git 인스턴스
     * @throws Exception Git 저장소를 열거나 초기화하는 도중 오류가 발생한 경우
     */
    public Git open(File dir) throws Exception {
        Git git;
        try {
            git = Git.open(dir);
        } catch (RepositoryNotFoundException e) {
            git = init(dir);
        }
        return git;
    }
}