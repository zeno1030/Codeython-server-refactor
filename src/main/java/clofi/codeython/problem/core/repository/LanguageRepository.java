package clofi.codeython.problem.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clofi.codeython.problem.core.domain.Language;
import clofi.codeython.problem.core.domain.Problem;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByLanguageNo(Long LanguageNo);

    List<Language> findByProblem(Problem problem);

}