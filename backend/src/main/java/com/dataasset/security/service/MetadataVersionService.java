package com.dataasset.security.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.MetadataVersion;
import com.dataasset.security.mapper.MetadataVersionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MetadataVersionService extends ServiceImpl<MetadataVersionMapper, MetadataVersion> {
}
