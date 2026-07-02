package com.fms.management.converter;

import com.fms.management.dto.response.FlagDTO;
import com.fms.management.entity.FlagDO;

public final class FlagConverter {

    private FlagConverter() {}

    public static FlagDTO toDTO(FlagDO f) {
        FlagDTO flagDTO = new FlagDTO();
        flagDTO.setId(f.getId());
        flagDTO.setFlagKey(f.getFlagKey());
        flagDTO.setFlagType(f.getFlagType());
        flagDTO.setState(f.getState());
        flagDTO.setDefinition(f.getDefinition());
        flagDTO.setVersion(f.getVersion());
        flagDTO.setDescription(f.getDescription());
        flagDTO.setCreatedAt(f.getCreatedAt());
        flagDTO.setUpdatedAt(f.getUpdatedAt());
        flagDTO.setCreatedBy(f.getCreatedBy());
        flagDTO.setUpdatedBy(f.getUpdatedBy());
        return flagDTO;
    }
}