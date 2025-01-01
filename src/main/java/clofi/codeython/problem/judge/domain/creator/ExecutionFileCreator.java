package clofi.codeython.problem.judge.domain.creator;

import java.util.List;

public interface ExecutionFileCreator {
    void create(List<String> inputTypes, String code, String route);
}
