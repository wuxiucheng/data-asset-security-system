package com.dataasset.security.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.LcStatus;
import com.dataasset.security.mapper.LcStatusMapper;
import org.springframework.stereotype.Service;
@Service
public class LcStatusService extends ServiceImpl<LcStatusMapper, LcStatus> {}
