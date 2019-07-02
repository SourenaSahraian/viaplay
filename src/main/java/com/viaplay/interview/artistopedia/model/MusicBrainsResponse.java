package com.viaplay.interview.artistopedia.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MusicBrainsResponse implements Serializable {

    private List<Relations> relations = new ArrayList<>();

    @JsonAlias("release-groups")
    private List<ReleaseGroups> releaseGroups = new ArrayList<>();

}
