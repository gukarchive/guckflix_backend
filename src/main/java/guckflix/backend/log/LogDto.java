package guckflix.backend.log;

import java.time.LocalDate;

public class LogDto {

    private String apiName;

    private LocalDate createAt;

    private int count;

    public LogDto(String key, LocalDate date, int value){
        this.apiName = key;
        this.createAt = date;
        this.count = value;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
