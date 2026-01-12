package SamoDev.Where2Play.dao;

import SamoDev.Where2Play.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleDao extends JpaRepository<Rule, Integer> {
}
