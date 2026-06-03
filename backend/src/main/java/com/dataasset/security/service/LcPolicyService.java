package com.dataasset.security.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataasset.security.entity.LcPolicy;
import com.dataasset.security.mapper.LcPolicyMapper;
import org.springframework.stereotype.Service;
@Service
public class LcPolicyService extends ServiceImpl<LcPolicyMapper, LcPolicy> {}
