package org.kiss.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "car_types")
@CompoundIndex(name = "make_model_unique", def = "{'make': 1, 'model': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarType {

    @Id
    private String id;

    private String make;
    private String model;
    private String previewUrl;
}
