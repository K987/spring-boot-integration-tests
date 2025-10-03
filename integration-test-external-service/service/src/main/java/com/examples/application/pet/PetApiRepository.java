package com.examples.application.pet;

import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.*;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
interface PetApiRepository {

    @GetExchange(value = "/{petId}")
    PetDto findPet(@PathVariable Long petId);

    @PostExchange(value = "/",
            contentType =  MediaType.APPLICATION_JSON_VALUE)
    PetDto create(@RequestBody PetDto pet);

    @DeleteExchange("/{petId}")
    void delete(@PathVariable Long petId);

    @GetExchange(value = "/search")
    List<PetDto> search(@RequestParam MultiValueMap<String, String> params);

    @PatchExchange(value = "/{petId}",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    PetDto update(@PathVariable Long petId, @RequestBody PetDto petDto);

    static SearchParamBuilder searchParamBuilder() {
        return new SearchParamBuilder();
    }

    class SearchParamBuilder {

        private List<String> tags = null;
        private String status = null;

        private SearchParamBuilder(){ }

        public SearchParamBuilder addTag(String tag){
            if(tags == null){
                tags = new ArrayList<>();
            }
            tags.add(tag);
            return this;
        }

        public SearchParamBuilder setStatus(String status){
            this.status = status;
            return this;
        }

        public MultiValueMap<String, String> build(){
            HashMap<String, List<String>> map = new HashMap<>();
            if(tags != null){
                map.put("tags", List.of(String.join("+", tags)));
            }
            if(status != null){
                map.put("status", List.of(status));
            }
            return CollectionUtils.unmodifiableMultiValueMap(CollectionUtils.toMultiValueMap(map));
        }
    }
}
