package com.king.re0.service;


import com.king.re0.base.aotu.ExecutorManager;
import com.king.re0.dao.CollectionRepository;
import com.king.re0.dao.MemoRepository;
import com.king.re0.dao.TokenRepository;
import com.king.re0.dao.UserRepository;
import com.king.re0.entity.CollectionEntity;
import com.king.re0.entity.MemoEntity;
import com.king.re0.entity.TokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequestMapping("/memo")
@RestController
public class MemoController {

    private final MemoRepository memoRepository;
    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ExecutorManager executorManager;
    private final CollectionRepository collectionRepository;
    @Autowired
    public MemoController(MemoRepository memoRepository, HttpServletRequest httpServletRequest, UserRepository userRepository, TokenRepository tokenRepository, ExecutorManager executorManager, CollectionRepository collectionRepository) {
        this.memoRepository = memoRepository;
        this.httpServletRequest = httpServletRequest;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.executorManager = executorManager;
        this.collectionRepository = collectionRepository;
    }

    @GetMapping("/list")
    public Flux<List<MemoEntity>> getList() {
        return Flux.just(memoRepository.findAll()).subscribeOn(executorManager.getScheduler());
    }

    @PostMapping("/add")
    public Object addMemoToken(@RequestBody MemoEntity memoEntity, @RequestHeader(value = "Authorization") String authorization) {
        Optional<TokenEntity> tokenEntity = tokenRepository.findByToken(authorization);
        tokenEntity.ifPresent(
                it -> {
                    memoEntity.setUserEntity(it.getUserEntity());
                    memoEntity.setCreateTime(System.currentTimeMillis());
                    memoRepository.save(memoEntity);
                }
        );
        return memoEntity;
    }

    @GetMapping("/findMyList")
    public Object findByToken(@RequestHeader(value = "Authorization") String authorization) {
        Optional<List<MemoEntity>> memoEntities = tokenRepository.findByToken(authorization).flatMap(it -> memoRepository.findAllByUserEntity(it.getUserEntity()));
        return memoEntities.orElseGet(ArrayList::new);
    }

    @GetMapping("/findMemoByLocation")
    public Object findMemoByLocationAndToken(@RequestHeader(value = "Authorization") String authorization,@RequestParam HashMap<String, String> requestBody) {
        Double longitude = Double.valueOf(requestBody.get("longitude"));
        Double latitude = Double.valueOf(requestBody.get("latitude"));
        Double distance = Double.valueOf(requestBody.get("distance"));

        Optional<List<MemoEntity>> memoEntities = memoRepository.findByLocation2(longitude, latitude, distance);
        Optional<TokenEntity> tokenEntity = tokenRepository.findByToken(authorization);
        Sort sort = new Sort(Sort.Direction.fromString("desc"), "id");
        Pageable pageable =  PageRequest.of(0, 1, sort);
        tokenEntity.ifPresent(it->{
            Optional<Page<CollectionEntity>> collectionEntities = collectionRepository.findAllByUser(it.getUserEntity(),pageable);
            if(memoEntities.isPresent() && collectionEntities.isPresent()){
                for (MemoEntity memoEntity : memoEntities.get()) {
                    for (CollectionEntity collectionEntity : memoEntity.getCollectionEntities()) {
                        if (it.getUserEntity().getId().equals(collectionEntity.getUser().getId())) {
                            memoEntity.setCollect(true);
                            break;
                        }
                    }
                }
            }
        });
        return memoEntities.orElseGet(ArrayList::new);
    }
//    @PostMapping("/add")
//    private InfoEntity addMemo(@RequestHeader(value="Authorization") String authorization){
////        userRepository.findById(decode(authorization).getUserId());
////        return ;
//    }

    private TokenEntity decode(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }
}
