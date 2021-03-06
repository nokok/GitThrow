package net.orekyuu.gitthrow.job;

import net.orekyuu.gitthrow.job.task.CleanWorkspaceTask;
import net.orekyuu.gitthrow.job.task.GitCloneTask;
import net.orekyuu.gitthrow.job.task.SaveTestLogTask;
import net.orekyuu.gitthrow.job.task.TestTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TestJob extends Job {

    @Autowired
    private TestTask testTask;
    @Autowired
    private SaveTestLogTask saveTestLogTask;
    @Autowired
    private GitCloneTask cloneTask;
    @Autowired
    private CleanWorkspaceTask cleanWorkspaceTask;

    private String hash = "master";

    @Override
    protected void onInit() {
        cloneTask.setBranch(hash);
        addTask(cloneTask);
        addTask(testTask);
        addTask(saveTestLogTask);
        addTask(cleanWorkspaceTask);
    }

    /**
     * ビルドするコミットを指定します
     * @param hash ビルドするコミットのハッシュ
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

}
