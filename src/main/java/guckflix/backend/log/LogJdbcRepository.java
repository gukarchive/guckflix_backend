package guckflix.backend.log;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public int saveAll(List<LogDto> logList) {

        if(logList.isEmpty())
            return 0;

        int[] updateCounts = jdbcTemplate.batchUpdate("INSERT INTO log_count (api_name, create_at, count) VALUES (?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        LogDto logDto = logList.get(i);
                        ps.setString(1, logDto.getApiName());
                        ps.setDate(2, Date.valueOf(logDto.getCreateAt()));
                        ps.setInt(3, logDto.getCount());
                    }

                    @Override
                    public int getBatchSize() {
                        return logList.size();
                    }
                });

        return updateCounts.length;
    }
}
