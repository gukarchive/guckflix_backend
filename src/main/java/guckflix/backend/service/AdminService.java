package guckflix.backend.service;

import guckflix.backend.dto.AdminMemoDto;
import guckflix.backend.entity.AdminMemo;
import guckflix.backend.repository.AdminMemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMemoRepository adminMemoRepository;

    @Transactional
    public Long postMemo(AdminMemoDto.Post post){
        AdminMemo savedMemo = adminMemoRepository.save(
                AdminMemo.builder().text(post.getText()).build()
        );

        return savedMemo.getId();
    }

    public List<AdminMemoDto.Response> getMemos() {
        List<AdminMemo> memoEntityList = adminMemoRepository.findAll(Sort.by(Sort.Order.asc("createdAt")));
        List<AdminMemoDto.Response> response = memoEntityList.stream().map(entity -> new AdminMemoDto.Response(entity)).collect(Collectors.toList());
        return response;
    }

    @Transactional
    public void deleteMemo(Long deleteMemoId) {
        adminMemoRepository.deleteById(deleteMemoId);
    }

}
