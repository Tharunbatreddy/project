package com.example.proj.dto;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor // Lombok will generate the no-argument constructor
@AllArgsConstructor
public class TeamSearchResponse {
    private List<TeamDTO> results;
    private long totalCount;
    private int pageCount;
    private int currentPage;
    public void setResults(List<TeamDTO> results) {
        this.results = results;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}

