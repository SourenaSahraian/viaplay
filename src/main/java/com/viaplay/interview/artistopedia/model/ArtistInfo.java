package com.viaplay.interview.artistopedia.model;

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
@Builder
public class ArtistInfo implements Serializable {

    private String mbid;
    private String  description;
    private List<Albums> albums = new ArrayList<>();

}
