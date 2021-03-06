package cc.mrbird.febs.project.service;

import cc.mrbird.febs.project.domain.ClassStatistics;
import cc.mrbird.febs.project.domain.ProjectScore;
import cc.mrbird.febs.project.domain.ProjectScoringRules;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hyl
 */
@Service
public interface ProjectScoreService extends IService<ProjectScore> {

    void addProjectScore(ProjectScore projectScore);

    void updateProjectScore(ProjectScore projectScore);

    void updateProjectScoringRules(int[] rules);

    float getGreaterThanInProject(String sid);

    float getGreaterThanInClass(String sid);

//    float getStatisticsInProject(String sid);

    ClassStatistics getStatisticsInClass(String sid);

    ProjectScore getProjectScore(String sid);

    List<ProjectScore> getAllProjectScore();

    void releaseScore();

    void unreleaseScore();
}
